package com.ofds.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RazorpayConfig {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        String keyId = env.getProperty("razorpay.key.id");
        String keySecret = env.getProperty("razorpay.key.secret");

        if (keyId != null) {
            keyId = keyId.trim();
            logger.info("Razorpay Key ID loaded: {}", keyId);
        }
        if (keySecret != null) {
            keySecret = keySecret.trim();
        }
        
        // The third parameter 'true' forces the client to add authentication headers.
        return new RazorpayClient(keyId, keySecret, true);
    }
}