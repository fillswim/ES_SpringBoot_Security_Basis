package com.example.es_springboot_security_basis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data // Add getters and setters
@AllArgsConstructor
public class Developer {
    private Long id;
    private String firstname;
    private String lastname;
}
