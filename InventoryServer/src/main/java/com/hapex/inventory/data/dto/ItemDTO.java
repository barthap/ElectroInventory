package com.hapex.inventory.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO object for creating and updating Item entity. Parameters that you do NOT want " +
        "to update should be omitted.")
public class ItemDTO {
    @ApiModelProperty(notes = "Component name", allowEmptyValue = true)
    private String name;

    @ApiModelProperty(notes = "Short description, for example basic parameters, voltage levels etc.", allowEmptyValue = true)
    private String description;

    @ApiModelProperty(notes = "Quantity of elements in stock", allowEmptyValue = true)
    private int quantity;

    @ApiModelProperty(notes = "Website - datasheet URL or usage tutorial link", allowEmptyValue = true)
    private String website;

    @ApiModelProperty(notes = "ID should be specified in URI, this property is ignored", allowEmptyValue = true)
    private Long id;

    @ApiModelProperty(notes = "Set this to null or omit if you do NOT want to update it. " +
            "Set 0 if you want set category to NULL", allowEmptyValue = true)
    private Long categoryId;

    @ApiModelProperty(notes = "Set this to null or omit if you do NOT want to update it. " +
            "Set 0 if you want set location to NULL", allowEmptyValue = true)
    private Long locationId;

}
