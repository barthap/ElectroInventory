package com.hapex.inventory.service;

import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.utils.ConflictException;
import com.hapex.inventory.utils.InvalidValueException;
import com.hapex.inventory.utils.ResourceNotFoundException;
import com.querydsl.core.types.Predicate;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CategoryService {

    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Page<Category> findAll(Predicate predicate, Pageable pageable) {
        return repository.findAll(predicate, pageable);
    }

    public List<Category> findBySpecifiedIds(Iterable<Long> ids) {
        return StreamSupport
                .stream(repository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }

    public Category findById(Long id) {
        return repository.findById(id).orElseThrow(()->throwNotFound(id));
    }

    public Category addCategory(CategoryDTO dto) {
        Category category;
        try {
            category = categoryMapper.map(dto);
        } catch (MappingException e) {
            throw new InvalidValueException(e.getCause().getMessage());
        }
        if(dto.getParentId() != null) {
            Category parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> throwNotFound(dto.getParentId()));

            category.setParent(parent);
        }

        return repository.save(category);
    }

    public Category updateCategory(long id, CategoryDTO dto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> throwNotFound(id));
        categoryMapper.map(dto, category);

        if(dto.getParentId() != null) {
            //detach this category from its current parent
            if(category.getParent() != null)
                category.getParent().removeSubcategory(category);

            Category newParent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> throwNotFound(dto.getParentId()));

            newParent.addSubcategory(category);
        }

        return repository.save(category);
    }

    public void deleteById(long id) {
        Category category = repository.findById(id).orElseThrow(() -> throwNotFound(id));
        if(category.hasChildren()) {
            throw new ConflictException("Cannot delete category (id="+id+")! Remove subcategories first!");
        }

        if(category.hasItems()) {
            for (Item it: category.getItems()) {
                it.setCategory(null);
            }
        }
        repository.delete(category);
    }

    private ModelMapper mapper = new ModelMapper();
    private TypeMap<CategoryDTO, Category> categoryMapper = mapper.createTypeMap(CategoryDTO.class, Category.class)
            .addMappings((mapper) -> {
                mapper.skip(Category::setId);
                mapper.skip(Category::setParent);
                mapper.skip((Category cat, Long id) -> cat.getParent().setId(id));
            });

    private RuntimeException throwNotFound(long id) {
        return new ResourceNotFoundException("Category with id=" + id + " not found!");
    }
}
