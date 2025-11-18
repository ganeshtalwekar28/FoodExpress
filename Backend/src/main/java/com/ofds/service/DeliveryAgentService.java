package com.ofds.service;

import com.ofds.dto.DeliveryAgentDTO;
import com.ofds.entity.DeliveryAgentEntity;
import com.ofds.entity.OrderEntity;
import com.ofds.repository.DeliveryAgentRepository;
import com.ofds.repository.OrderRepository;
import com.ofds.exception.AgentListNotFoundException; 
import com.ofds.exception.AgentNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing all business logic related to Delivery Agents,
 * including retrieval, status management, performance metrics, and DTO mapping.
 */
@Service
public class DeliveryAgentService {

    private final DeliveryAgentRepository agentRepository;
    private final OrderRepository ordersRepository;
    
    /**
     * Constructs the DeliveryAgentService with required repositories.
     */
    public DeliveryAgentService(DeliveryAgentRepository agentRepository, OrderRepository ordersRepository) {
        this.agentRepository = agentRepository;
        this.ordersRepository = ordersRepository;
    }

    /**
     * Retrieves a list of DTOs for all agents currently marked as "AVAILABLE".
     */
    @Transactional(readOnly = true)
    public List<DeliveryAgentDTO> findAvailableDeliveryAgents() throws AgentListNotFoundException {
        List<DeliveryAgentEntity> availableAgents = agentRepository.findByStatus("AVAILABLE");

        if(!availableAgents.isEmpty()) {
            return availableAgents.stream()
                    .map(this::mapAgentToDTO)
                    .collect(Collectors.toList());
        } else {
            throw new AgentListNotFoundException("Agent Data Not found in the Database...");
        }
    }

    /**
     * Retrieves a list of DTOs for ALL agents, handling database fetching duplicates 
     * and confirming the active order status from the Order Repository.
     */
    @Transactional(readOnly = true)
    public List<DeliveryAgentDTO> findAllDeliveryAgents() throws AgentListNotFoundException {
        // Fetch all agents with their associated orders eagerly.
        List<DeliveryAgentEntity> rows = agentRepository.findAllWithOrdersEagerly();
        if(rows.isEmpty()) {
            throw new AgentListNotFoundException("Delivery Agent cannot be found by its ID...");
        } else {
            // Deduplicate entities using a map to handle LEFT JOIN FETCH results
            Map<Long, DeliveryAgentEntity> byId = new LinkedHashMap<>();

            for (DeliveryAgentEntity a : rows) {
                Long id = a.getId();
                if (id == null) continue;

                DeliveryAgentEntity existing = byId.get(id);
                if (existing == null) {
                    byId.put(id, a);
                } else if ("AVAILABLE".equalsIgnoreCase(a.getStatus()) && !"AVAILABLE".equalsIgnoreCase(existing.getStatus())) {
                    byId.put(id, a);
                }
            }

            // Map the unique entities to DTOs
            List<DeliveryAgentDTO> dtos = byId.values().stream()
                    .map(this::mapAgentToDTO)
                    .collect(Collectors.toList());

            // Explicitly confirm and set the active order ID using the OrdersRepository
            for (DeliveryAgentDTO dto : dtos) {
                if (dto.getId() == null) continue;
                ordersRepository.findActiveOrderByAgentId(dto.getId())
                        .ifPresent(activeOrder -> dto.setCurrentOrderID(activeOrder.getId()));
            }

            return dtos;
        }
    }

    /**
     * Fetches detailed information for a specific agent by ID.
     */
    @Transactional(readOnly = true)
    public DeliveryAgentDTO getAgentDetails(Long agentId) throws AgentNotFoundException {
        // Find the agent by ID and throw custom exception if not found
        DeliveryAgentEntity agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new AgentListNotFoundException("Delivery Agent not found with ID: " + agentId));

        DeliveryAgentDTO dto = mapAgentToDTO(agent);

        // Explicitly check for an active order using the OrdersRepository
        ordersRepository.findActiveOrderByAgentId(agent.getId())
                .ifPresent(activeOrder -> dto.setCurrentOrderID(activeOrder.getId()));

        return dto;
    }

    /**
     * Maps the JPA DeliveryAgentEntity to the DeliveryAgentDTO.
     */
    private DeliveryAgentDTO mapAgentToDTO(DeliveryAgentEntity agent) {
        DeliveryAgentDTO dto = new DeliveryAgentDTO();

        // 1. Map Agent Identification and Contact
        dto.setId(agent.getId());
        dto.setAgentID(agent.getAgentCode() != null ? agent.getAgentCode() : String.valueOf(agent.getId()));
        dto.setName(agent.getName());
        dto.setPhone(agent.getPhone());
        dto.setEmail(agent.getEmail());
        dto.setStatus(agent.getStatus());

        // 2. Map Performance/Financials
        dto.setTotalEarning(agent.getTotalEarnings() != null ? agent.getTotalEarnings() : 0.0);
        dto.setTodayEarning(agent.getTodaysEarning() != null ? agent.getTodaysEarning() : 0.0);
        dto.setTotalDeliveries(agent.getTotalDeliveries() != null ? agent.getTotalDeliveries() : 0);
        dto.setRating(agent.getRating() != null ? agent.getRating() : 0.0);

        // 3. Attempt to derive Current Order ID from the loaded collection
        OrderEntity activeOrder = agent.getOrdersDelivered().stream()
                .filter(o -> "OUT FOR DELIVERY".equals(o.getOrderStatus()))
                .findFirst()
                .orElse(null);

        if (activeOrder != null) {
            dto.setCurrentOrderID(activeOrder.getId());
        } else {
            dto.setCurrentOrderID(null);
        }

        // 4. Map Orders List
        dto.setOrders(List.of());

        return dto;
    }
}