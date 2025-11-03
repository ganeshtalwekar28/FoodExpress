package com.ofds.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofds.dto.CustomerDTO;
import com.ofds.exception.NoDataFoundException;
import com.ofds.exception.RecordAlreadyFoundException;
import com.ofds.service.CustomerService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping ("/api/auth")
public class CustomerController 
{

	@Autowired
	CustomerService custServiceObj;

    @GetMapping("/getCustomerData")
    public ResponseEntity<List<CustomerDTO>> getCustomerData() throws NoDataFoundException 
    {
        List<CustomerDTO> customers = custServiceObj.getCustomerData();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
    
//    for testing in postman:
    @GetMapping ("/hello")
	public String hello() {
		System.out.println("Inside hello() of MyController...");
		return "Hello, you are an authenticated user..";
	}

    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) throws NoDataFoundException {
        CustomerDTO customer = custServiceObj.getCustomerById(id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PostMapping("/insertCustomerData")
    public ResponseEntity<CustomerDTO> insertCustomerData(@Valid @RequestBody CustomerDTO customerObj) throws RecordAlreadyFoundException 
    {
        CustomerDTO customer = custServiceObj.insertCustomerData(customerObj);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }
    
}

