package com.example.stockmarket.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
public class Participant {
    private Long id;
    private String name;
    private Date creationDate;
    @ToString.Exclude
    private String password;
}
