package com.hapex.inventory.controller

import com.hapex.inventory.data.dto.ItemDTO
import com.hapex.inventory.data.entity.Category
import com.hapex.inventory.data.entity.Item
import com.hapex.inventory.data.entity.QItem
import com.hapex.inventory.service.ItemService
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/items")
class ItemController(private val itemService: ItemService) {

    @GetMapping
    fun getAll(
            @RequestParam(required = false) ids: String?,
            @RequestParam(required = false) q: String?,
            @QuerydslPredicate(root = Item::class) predicate: Predicate?,
            pageable: Pageable): Page<Item>
    {
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
    fun getDetails(@PathVariable id: Long) = itemService.findById(id)

    @PostMapping
    fun addItem(@RequestBody dto: ItemDTO): ResponseEntity<Item> {
        val item = itemService.addItem(dto)
        return ResponseEntity.created(
                MvcUriComponentsBuilder.fromMethodName(
                        ItemController::class.java,
                        "getDetails", item.id)
                        .build().toUri()
        ).body(item)
    }

    @PutMapping("/{id}")
    fun updateItem(@PathVariable id: Long, @RequestBody dto: ItemDTO) = itemService.updateItem(id, dto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(@PathVariable id: Long) = itemService.deleteById(id)
}
