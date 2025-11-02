package com.ofds.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ofds.dto.AddressDTO;
import com.ofds.dto.CustomerDTO;
import com.ofds.entity.CustomerEntity;
import com.ofds.exception.NoDataFoundException;
import com.ofds.exception.RecordAlreadyFoundException;
import com.ofds.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository custRepo;

    @Autowired
    private ModelMapper modelMapper;
    
    // get all customers
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
    
    //signing up new user
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

    public CustomerDTO getCustomerById(Integer id) throws NoDataFoundException {
        CustomerEntity entity = custRepo.findById(id)
            .orElseThrow(() -> new NoDataFoundException("Customer not found with id: " + id));
        CustomerDTO dto = modelMapper.map(entity, CustomerDTO.class);
        
//        List<AddressDTO> addressDTOs = new ArrayList<>();
//        if (entity.getAddresses() != null) {
//            addressDTOs = entity.getAddresses().stream()
//                .map(addressEntity -> modelMapper.map(addressEntity, AddressDTO.class))
//                .collect(Collectors.toList());
//        }
//        dto.setAddresses(addressDTOs);
        
        return dto;
    }

    


}

