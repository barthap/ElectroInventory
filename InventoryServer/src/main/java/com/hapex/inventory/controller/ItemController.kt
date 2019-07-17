package com.hapex.inventory.controller

import com.hapex.inventory.controller.helper.ApiFilterable
import com.hapex.inventory.controller.helper.ApiPageable
import com.hapex.inventory.data.dto.ItemDTO
import com.hapex.inventory.data.entity.Category
import com.hapex.inventory.data.entity.Item
import com.hapex.inventory.data.entity.QItem
import com.hapex.inventory.service.ItemService
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import io.swagger.annotations.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import springfox.documentation.annotations.ApiIgnore
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/v1/items")
@Api(value = "Item managament", description = "CRUD operations for electronic components")
class ItemController(private val itemService: ItemService) {

    @GetMapping
    @ApiOperation(value = "View list of items", response = List::class)
    @ApiPageable
    @ApiImplicitParam(name = "[filters]*", paramType = "query", dataType = "string", allowMultiple = true,
            value = "It can be any name corresponding to entity properties. For example, " +
            "category.name=Sem will return all items, which has category with name containing " +
            "string 'sem' anywhere, ignoring case. Setting location.id=5 will return all " +
            "records, which has location with ID = 5. See QueryDSL Predicate for more info")
    fun getAll(
            @ApiParam(required = false, value = "Return only items with matching IDs (comma separated), for example ids=1,3,4")
            @RequestParam(required = false) ids: String?,

            @ApiParam(required = false, value = "Search query string - finds matching values in name and description")
            @RequestParam(required = false) q: String?,

            @ApiIgnore @QuerydslPredicate(root = Item::class) predicate: Predicate?,
            @ApiIgnore pageable: Pageable): Page<Item> {
        if (!ids.isNullOrBlank())  {
            val identifiers = ids!!.split(",").toList().map { it.toLong() }
            return PageImpl<Item>(itemService.findBySpecifiedIds(identifiers))
        } else if (!q.isNullOrBlank()) {
            val item = QItem.item
            val expr: BooleanExpression = item.name.contains(q).or(item.description.contains(q));
            return itemService.findAll(expr.and(predicate), pageable);
        } else
            return itemService.findAll(predicate, pageable);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get details about item with specified ID")
    fun getDetails(@PathVariable id: Long) = itemService.findById(id)

    @PostMapping
    @ApiOperation(value = "Add new item to database")
    fun addItem(
            @ApiParam(required = true, value = "Item DTO object")
            @RequestBody dto: ItemDTO): ResponseEntity<Item> {
        val item = itemService.addItem(dto)
        return ResponseEntity.created(
                MvcUriComponentsBuilder.fromMethodName(
                        ItemController::class.java,
                        "getDetails", item.id)
                        .build().toUri()
        ).body(item)
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update item with specified ID")
    fun updateItem(@PathVariable id: Long,
                   @ApiParam(required = true, value = "Item DTO object")
                   @RequestBody dto: ItemDTO) = itemService.updateItem(id, dto)

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete item with specified ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(@PathVariable id: Long) = itemService.deleteById(id)
}
