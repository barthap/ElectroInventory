package com.hapex.inventory.controller.helper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
        @ApiImplicitParam(name = "[filters]*", paramType = "query", dataType = "any",
                value = "It can be any name corresponding to entity properties. For example" +
                        "category.name=Sem will return all items, which has category with name containing " +
                        "string 'sem' anywhere, ignoring case. Setting location.id=5 will return all" +
                        "records, which has location with ID = 5. See QueryDSL Predicate for more info")
})
public @interface ApiFilterable {
}
