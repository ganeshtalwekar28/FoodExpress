package com.ofds.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ofds.dto.CustomerDTO;
import com.ofds.entity.CustomerEntity;
import com.ofds.exception.NoDataFoundException;
import com.ofds.exception.RecordAlreadyFoundException;
import com.ofds.repository.CustomerRepository;

/**
 * Service class for handling all business logic related to customer data, 
 * including retrieval, registration, and data mapping.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository custRepo;

    @Autowired
    private ModelMapper modelMapper;
    
    /**
     * Retrieves all customer records from the database and maps them to a list of DTOs.
     */
    public List<CustomerDTO> getCustomerData() throws NoDataFoundException {
        List<CustomerEntity> entityList = custRepo.findAll();

        if (entityList.isEmpty()) 
        {
            throw new NoDataFoundException("No Records found in the database.");
        }

        List<CustomerDTO> dtoList = entityList.stream()
            .map(entity -> modelMapper.map(entity, CustomerDTO.class))
            .collect(Collectors.toList());

        return dtoList;
    }
    
    /**
     * Registers a new customer after verifying that a record with the given email does not already exist.
     */
    public CustomerDTO insertCustomerData(CustomerDTO customerDTO) throws RecordAlreadyFoundException {
        Optional<CustomerEntity> existing = custRepo.findByEmail(customerDTO.getEmail());

        if (existing.isPresent())
        {
            throw new RecordAlreadyFoundException("Given record exists in the database");
        }

        CustomerEntity entity = modelMapper.map(customerDTO, CustomerEntity.class);
        CustomerEntity saved = custRepo.save(entity);
        CustomerDTO responseDTO = modelMapper.map(saved, CustomerDTO.class);

        return responseDTO;
    }

    /**
     * Retrieves a specific customer record by their ID and maps it to a DTO.
     */
    public CustomerDTO getCustomerById(Long id) throws NoDataFoundException {
        CustomerEntity entity = custRepo.findById(id)
            .orElseThrow(() -> new NoDataFoundException("Customer not found with id: " + id));
        CustomerDTO dto = modelMapper.map(entity, CustomerDTO.class);
        
        return dto;
    }
}