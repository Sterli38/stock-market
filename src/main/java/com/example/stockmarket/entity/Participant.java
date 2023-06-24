package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Participant {
    private Long id;
    private String name;
    private Date creationDate;
    private String password;
}
