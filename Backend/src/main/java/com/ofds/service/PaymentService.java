package com.ofds.service;

import com.ofds.dto.RazorpayOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling external payment gateway interactions, 
 * specifically creating new orders via the Razorpay API.
 */
@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient; 
    
    /**
     * Creates a new order on the Razorpay payment gateway using the provided amount and currency.
     */
    public RazorpayOrderResponse createRazorpayOrder(Double amount, String currency) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.intValue()); 
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1); // Auto capture the payment

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        
        response.setOrderId((String) razorpayOrder.get("id"));
        response.setCurrency((String) razorpayOrder.get("currency"));
        response.setAmountInPaise(((Integer) razorpayOrder.get("amount")).longValue());
        
        return response;
    }
}