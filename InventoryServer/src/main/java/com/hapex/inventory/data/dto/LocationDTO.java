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
@ApiModel(description = "DTO object for creating and updating item locations")
public class LocationDTO {
    @ApiModelProperty(value = "Location name")
    private String name;

    @ApiModelProperty(notes = "ID should be specified in URI, this property is ignored", allowEmptyValue = true)
    private Long id;

    @ApiModelProperty(notes = "Set this to null or omit if you do NOT want to update it. " +
            "Set to 0 if you want set location as root", allowEmptyValue = true)
    private Long parentId;
}