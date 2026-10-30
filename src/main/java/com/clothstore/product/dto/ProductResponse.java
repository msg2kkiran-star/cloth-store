package com.clothstore.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
