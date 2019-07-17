package com.hapex.inventory.service;

import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.dto.LocationDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.entity.Location;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.data.repository.LocationRepository;
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
public class LocationService {

    private LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public Page<Location> findAll(Predicate predicate, Pageable pageable) {
        return repository.findAll(predicate, pageable);
    }

    public List<Location> findBySpecifiedIds(Iterable<Long> ids) {
        return StreamSupport
                .stream(repository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }

    public Location findById(Long id) {
        return repository.findById(id).orElseThrow(()->throwNotFound(id));
    }

    public Location addLocation(LocationDTO dto) {
        Location location;
        try {
            location = locationMapper.map(dto);
        } catch (MappingException e) {
            throw new InvalidValueException(e.getCause().getMessage());
        }
        if(dto.getParentId() != null) {
            Location parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> throwNotFound(dto.getParentId()));

            location.setParent(parent);
        }

        return repository.save(location);
    }

    public Location updateLocation(long id, LocationDTO dto) {
        Location location = repository.findById(id)
                .orElseThrow(() -> throwNotFound(id));
        locationMapper.map(dto, location);

        if(dto.getParentId() != null) {
            //detach this location from its current parent
            if(location.getParent() != null)
                location.getParent().removeChildLocation(location);

           if(dto.getParentId() > 0) {
               Location newParent = repository.findById(dto.getParentId())
                       .orElseThrow(() -> throwNotFound(dto.getParentId()));

               newParent.addChildLocation(location);
           }
        }

        return repository.save(location);
    }

    public void deleteById(long id) {
        Location location = repository.findById(id).orElseThrow(() -> throwNotFound(id));
        if(location.hasChildren()) {
            throw new ConflictException("Cannot delete location (id="+id+")! Remove child locations first!");
        }

        repository.delete(location);
    }

    private ModelMapper mapper = new ModelMapper();
    private TypeMap<LocationDTO, Location> locationMapper = mapper.createTypeMap(LocationDTO.class, Location.class)
            .addMappings((mapper) -> {
                mapper.skip(Location::setId);
                mapper.skip(Location::setParent);
                mapper.skip((Location l, Long id) -> l.getParent().setId(id));
            });

    private RuntimeException throwNotFound(long id) {
        return new ResourceNotFoundException("Location with id=" + id + " not found!");
    }
}
