package com.hapex.inventory.controller;

import com.hapex.inventory.controller.helper.ApiFilterable;
import com.hapex.inventory.controller.helper.ApiPageable;
import com.hapex.inventory.data.dto.CategoryDTO;
import com.hapex.inventory.data.dto.LocationDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Location;
import com.hapex.inventory.service.CategoryService;
import com.hapex.inventory.service.LocationService;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/v1/locations")
@Api(description = "Location management")
public class LocationController {

    private LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    @ApiOperation(value = "View list of locations", response = List.class)
    @ApiPageable
    @ApiImplicitParam(name = "[filters]*", paramType = "query", dataType = "string", allowMultiple = true,
            value = "It can be any name corresponding to entity properties. For example, " +
                    "parent.name=Sem will return all locations, which parent has name containing " +
                    "string 'sem' anywhere, ignoring case. See QueryDSL Predicate for more info")
    public Page<Location> listAll(
            @ApiParam(value = "Return only items with matching IDs (comma separated), for example ids=1,3,4")
            @RequestParam(required = false) String ids,
            @ApiIgnore @QuerydslPredicate(root = Location.class) Predicate predicate,
            @ApiIgnore Pageable pageable) {
        if(ids != null && !ids.isEmpty()) {
            List<Long> identifiers = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            return new PageImpl<>(service.findBySpecifiedIds(identifiers));
        } else
            return service.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get details about location with specified ID")
    public Location getDetails(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ApiOperation(value = "Add new location to database")
    public ResponseEntity<Location> create(@RequestBody LocationDTO dto) {
        Location location = service.addLocation(dto);
        URI uri = MvcUriComponentsBuilder.fromMethodName(LocationController.class, "getDetails",
                location.getId()).build().toUri();
        return ResponseEntity.created(uri).body(location);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update location with specified ID")
    public Location update(@PathVariable Long id, @RequestBody LocationDTO dto) {
        return service.updateLocation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete location with specified ID")
    public void deleteLocation(@PathVariable Long id) {
        service.deleteById(id);
    }
}
