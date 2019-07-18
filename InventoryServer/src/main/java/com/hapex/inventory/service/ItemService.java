package com.hapex.inventory.service;

import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.entity.Photo;
import com.hapex.inventory.data.repository.ItemRepository;
import com.hapex.inventory.service.storage.StorageService;
import com.hapex.inventory.utils.InvalidValueException;
import com.hapex.inventory.utils.ResourceNotFoundException;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@Slf4j
public class ItemService {
    private ItemRepository itemRepository;
    private CategoryService categoryService;
    private LocationService locationService;
    private StorageService storageService;

    private ModelMapper mapper = new ModelMapper();
    private TypeMap<ItemDTO, Item> itemMapper = mapper.createTypeMap(ItemDTO.class, Item.class)
            .addMappings((mapper) -> {  //we skip these mappings, because we set them manually
                mapper.skip(Item::setId);

                mapper.skip(Item::setCategory);
                mapper.skip((Item it, Long id) -> it.getCategory().setId(id));

                mapper.skip(Item::setLocation);
                mapper.skip((Item it, Long id) -> it.getLocation().setId(id));
            });

    public ItemService(ItemRepository itemRepository,
                       CategoryService categoryService,
                       LocationService locationService,
                       StorageService storageService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.storageService = storageService;

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
        Item item = findById(id);
        if(item.hasPhoto())
            deletePhoto(id);

        itemRepository.delete(item);
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

    private RuntimeException photoNotFound(long id) {
        return new ResourceNotFoundException("Item with id=" + id + " has no photo!");
    }

    public Optional<Resource> getPhoto(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> throwNotFound(itemId));

        if(item.getPhoto() == null || !item.getPhoto().isValid())
            return Optional.empty();

        return Optional.of(storageService.loadAsResource(item.getPhoto().getFilename()));
    }


    /**
     * Creates or updates item photo
     * @param itemId item id
     * @param file file data
     * @return is photo created?
     */
    public boolean updatePhoto(long itemId, MultipartFile file) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> throwNotFound(itemId));

        boolean isCreated = true;

        if(item.hasPhoto()) {
            if(!storageService.delete(item.getPhoto().getFilename()))
                throw new RuntimeException("Could not remove file: " + item.getPhoto().getFilename());

            isCreated = false;
        }

        final String filename = "photo_" + itemId + Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf('.'));

        if(!storageService.store(file, filename))
            throw new RuntimeException("Could not save photo for item id=" + itemId);

        if(isCreated)
            item.addPhoto(new Photo(filename));
        else
            item.getPhoto().setFilename(filename);

        return isCreated;
    }

    public void deletePhoto(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> throwNotFound(itemId));

        if(!item.hasPhoto())
            throw photoNotFound(itemId);

        if(storageService.delete(item.getPhoto().getFilename()))
            item.removePhoto();
        else
            throw new RuntimeException("Could not remove file: " + item.getPhoto().getFilename());

    }
}