package com.hapex.inventory.test.unit.controller;

import com.hapex.inventory.controller.ItemController;
import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.service.ItemService;
import com.hapex.inventory.utils.InvalidValueException;
import com.hapex.inventory.utils.ResourceNotFoundException;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.hapex.inventory.test.utils.TestUtils.asJsonString;
import static com.hapex.inventory.test.utils.TestUtils.randId;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ItemController.class)
@WithMockUser
public class ItemControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void getAllItemsTest() throws Exception {

        Page<Item> page = new PageImpl<>(Collections.singletonList(
                new Item("1n4004")
        ));

        when(itemService.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(header().longValue("X-Total-Count", 1))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("1n4004"));
    }

    //specified criteria tests are in category controller

    @Test
    public void getDetailsTest() throws Exception {
        given(itemService.findById(anyLong()))
                .willReturn(new Item("1n4007"));

        mvc.perform(get("/items/" + randId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("1n4007"));
    }

    @Test
    public void notFoundTest() throws Exception {
        given(itemService.findById(anyLong())).willThrow(new ResourceNotFoundException(""));
        given(itemService.updateItem(anyLong(), any())).willThrow(new ResourceNotFoundException(""));
        Mockito.doThrow(new ResourceNotFoundException("")).when(itemService).deleteById(anyLong());

        mvc.perform(get("/items/" + randId())).andExpect(status().isNotFound());
        mvc.perform(put("items/" + randId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new ItemDTO())))
                .andExpect(status().isNotFound());
        mvc.perform(delete("api/items/" + randId())).andExpect(status().isNotFound());
    }

    @Test
    public void addItemTest() throws Exception {
        Item item = new Item("bc327");
        ItemDTO dto = ItemDTO.builder().name(item.getName()).build();
        given(itemService.addItem(eq(dto))).willReturn(item);

        mvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    public void updateItemTest() throws Exception {
        Item item = new Item("bc327");
        ItemDTO dto = ItemDTO.builder().name(item.getName()).build();
        given(itemService.updateItem(anyLong(), eq(dto))).willReturn(item);

        mvc.perform(put("/items/" + randId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    public void updateInvalidItemTest() throws Exception {
        given(itemService.updateItem(anyLong(), any())).willThrow(new InvalidValueException(""));

        mvc.perform(put("/items/" + randId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new ItemDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteItemTest() throws Exception {
        Mockito.doNothing().when(itemService).deleteById(anyLong());

        mvc.perform(delete("/items/" + randId()))
                .andExpect(status().isNoContent());
    }
}
