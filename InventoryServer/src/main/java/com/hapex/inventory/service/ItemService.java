package com.hapex.inventory.service;

import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.data.repository.ItemRepository;
import com.hapex.inventory.utils.InvalidValueException;
import com.hapex.inventory.utils.ResourceNotFoundException;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
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
@Slf4j
public class ItemService {
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;

    private ModelMapper mapper = new ModelMapper();
    private TypeMap<ItemDTO, Item> itemMapper = mapper.createTypeMap(ItemDTO.class, Item.class)
            .addMappings((mapper) -> {
                mapper.skip(Item::setId);
                mapper.skip(Item::setCategory);
                mapper.skip((Item it, Long id) -> it.getCategory().setId(id));
            });

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;

        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    public Item findById(long id) {
        return itemRepository.findById(id).orElseThrow(() -> throwNotFound(id));
    }
    public Page<Item> findAll(Predicate predicate, Pageable pageable) {
        return itemRepository.findAll(predicate, pageable);
    }

    public List<Item> findBySpecifiedIds(Iterable<Long> ids) {
        return StreamSupport
                .stream(itemRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }

    public Item addItem(ItemDTO dto) {
        Item item;
        try {
            log.debug("Starting mapping item");
            item = itemMapper.map(dto);
        } catch (MappingException e) {
            throw new InvalidValueException(e.getCause().getMessage());
        }
        if(dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with id=" + dto.getCategoryId() + " not found!"));

            item.setCategory(category);
        }

        log.debug("Item mapped. Saving...");
        return itemRepository.save(item);
    }

    public Item updateItem(long id, ItemDTO dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> throwNotFound(id));
        itemMapper.map(dto, item);

        if(dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with id=" + dto.getCategoryId() + " not found!"));

            item.setCategory(category);
        }

        return itemRepository.save(item);
    }

    public void deleteById(long id) {
        if(itemRepository.existsById(id))
            itemRepository.deleteById(id);
        else
            throw throwNotFound(id);
    }

    private RuntimeException throwNotFound(long id) {
        return new ResourceNotFoundException("Item with id=" + id + " not found!");
    }
}