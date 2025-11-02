package com.ofds.dto;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Integer id;

    private String name;

    private String email;
    
    private String password;

    private String phone;
    
    private boolean termsAccepted;


    
    public CustomerDTO(String name, String email, String password, String phone, boolean termsAccepted) {
        
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.termsAccepted = termsAccepted;
    }

}

