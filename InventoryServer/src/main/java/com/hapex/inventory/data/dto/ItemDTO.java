package com.hapex.inventory.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String name;
    private String description;
    private int quantity;
    private String website;

    private Long id;
    private Long categoryId;

}
