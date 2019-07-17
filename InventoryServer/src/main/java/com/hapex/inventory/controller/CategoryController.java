package com.hapex.inventory.controller;

import com.hapex.inventory.controller.helper.ApiFilterable;
import com.hapex.inventory.controller.helper.ApiPageable;
import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.service.CategoryService;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/categories")
@Api(description = "Category management")
public class CategoryController {

    private CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @ApiOperation(value = "View list of categories", response = List.class)
    @ApiPageable
    @ApiImplicitParam(name = "[filters]*", paramType = "query", dataType = "string", allowMultiple = true,
            value = "It can be any name corresponding to entity properties. For example, " +
                    "parent.name=Sem will return all categories, which parent has name containing " +
                    "string 'sem' anywhere, ignoring case. See QueryDSL Predicate for more info")
    public Page<Category> listAll(
            @ApiParam(value = "Return only items with matching IDs (comma separated), for example ids=1,3,4")
            @RequestParam(required = false) String ids,
            @ApiIgnore @QuerydslPredicate(root = Category.class) Predicate predicate,
            @ApiIgnore Pageable pageable) {
        if(ids != null && !ids.isEmpty()) {
            List<Long> identifiers = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            return new PageImpl<>(service.findBySpecifiedIds(identifiers));
        } else
            return service.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get details about category with specified ID")
    public Category getDetails(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ApiOperation(value = "Add new category to database")
    public ResponseEntity<Category> create(@RequestBody CategoryDTO dto) {
        Category category = service.addCategory(dto);
        URI uri = MvcUriComponentsBuilder.fromMethodName(CategoryController.class, "getDetails",
                category.getId()).build().toUri();
        return ResponseEntity.created(uri).body(category);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update category with specified ID")
    public Category update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete category with specified ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        service.deleteById(id);
    }
}
