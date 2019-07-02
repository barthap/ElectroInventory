package com.hapex.inventory.controller;

import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.service.CategoryService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Category> listAll(@RequestParam(required = false) String ids,
                                  @QuerydslPredicate(root = Category.class) Predicate predicate,
                                  Pageable pageable) {
        if(ids != null && !ids.isEmpty()) {
            List<Long> identifiers = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            return new PageImpl<>(service.findBySpecifiedIds(identifiers));
        } else
            return service.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    public Category getDetails(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CategoryDTO dto) {
        Category category = service.addCategory(dto);
        URI uri = MvcUriComponentsBuilder.fromMethodName(CategoryController.class, "getDetails",
                category.getId()).build().toUri();
        return ResponseEntity.created(uri).body(category);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        service.deleteById(id);
    }
}
