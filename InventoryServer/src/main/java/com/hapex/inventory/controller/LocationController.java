package com.hapex.inventory.controller;

import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.dto.LocationDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Location;
import com.hapex.inventory.service.CategoryService;
import com.hapex.inventory.service.LocationService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Location> listAll(@RequestParam(required = false) String ids,
                                  @QuerydslPredicate(root = Location.class) Predicate predicate,
                                  Pageable pageable) {
        if(ids != null && !ids.isEmpty()) {
            List<Long> identifiers = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            return new PageImpl<>(service.findBySpecifiedIds(identifiers));
        } else
            return service.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    public Location getDetails(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody LocationDTO dto) {
        Location location = service.addLocation(dto);
        URI uri = MvcUriComponentsBuilder.fromMethodName(LocationController.class, "getDetails",
                location.getId()).build().toUri();
        return ResponseEntity.created(uri).body(location);
    }

    @PutMapping("/{id}")
    public Location update(@PathVariable Long id, @RequestBody LocationDTO dto) {
        return service.updateLocation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable Long id) {
        service.deleteById(id);
    }
}
