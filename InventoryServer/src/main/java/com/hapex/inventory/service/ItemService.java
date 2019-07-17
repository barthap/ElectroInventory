package com.hapex.inventory.service;

import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.entity.Location;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.data.repository.ItemRepository;
import com.hapex.inventory.data.repository.LocationRepository;
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
    private CategoryService categoryService;
    private LocationService locationService;

    private ModelMapper mapper = new ModelMapper();
    private TypeMap<ItemDTO, Item> itemMapper = mapper.createTypeMap(ItemDTO.class, Item.class)
            .addMappings((mapper) -> {  //we skip these mappings, because we set them manually
                mapper.skip(Item::setId);

                mapper.skip(Item::setCategory);
                mapper.skip((Item it, Long id) -> it.getCategory().setId(id));

                mapper.skip(Item::setLocation);
                mapper.skip((Item it, Long id) -> it.getLocation().setId(id));
            });

    public ItemService(ItemRepository itemRepository, CategoryService categoryService, LocationService locationService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.locationService = locationService;

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
            //we need to throw our own exception, because it is handled in custom Controller Advice
            throw new InvalidValueException(e.getCause().getMessage());
        }

        updateRelationships(item, dto);

        log.debug("Item mapped. Saving...");
        return itemRepository.save(item);
    }

    public Item updateItem(long id, ItemDTO dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> throwNotFound(id));
        itemMapper.map(dto, item);

        updateRelationships(item, dto);

        return itemRepository.save(item);
    }

    public void deleteById(long id) {
        if(itemRepository.existsById(id))
            itemRepository.deleteById(id);
        else
            throw throwNotFound(id);
    }

    private void updateRelationships(Item item, ItemDTO dto) {
        if(dto.getCategoryId() != null)
            item.setCategory(dto.getCategoryId() > 0 ? categoryService.findById(dto.getCategoryId()) : null);

        if(dto.getLocationId() != null)
            item.setLocation(dto.getLocationId() > 0 ? locationService.findById(dto.getLocationId()) : null);
    }

    private RuntimeException throwNotFound(long id) {
        return new ResourceNotFoundException("Item with id=" + id + " not found!");
    }
}