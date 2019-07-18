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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

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

        mvc.perform(get("/v1/items"))
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

        mvc.perform(get("/v1/items/" + randId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("1n4007"));
    }

    @Test
    public void notFoundTest() throws Exception {
        given(itemService.findById(anyLong())).willThrow(new ResourceNotFoundException(""));
        given(itemService.updateItem(anyLong(), any())).willThrow(new ResourceNotFoundException(""));
        Mockito.doThrow(new ResourceNotFoundException("")).when(itemService).deleteById(anyLong());

        mvc.perform(get("/v1/items/" + randId())).andExpect(status().isNotFound());
        mvc.perform(put("/v1/items/" + randId())
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

        mvc.perform(post("/v1/items")
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

        mvc.perform(put("/v1/items/" + randId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    public void updateInvalidItemTest() throws Exception {
        given(itemService.updateItem(anyLong(), any())).willThrow(new InvalidValueException(""));

        mvc.perform(put("/v1/items/" + randId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new ItemDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteItemTest() throws Exception {
        Mockito.doNothing().when(itemService).deleteById(anyLong());

        mvc.perform(delete("/v1/items/" + randId()))
                .andExpect(status().isNoContent());
    }

    // Photo tests

    @Test
    public void getPhotoTest() throws Exception {
        //TODO: not implemented
        given(itemService.getPhoto(anyLong())).willReturn(Optional.of(new InputStreamResource(mockFile().getInputStream())));

        mvc.perform(get("/v1/items/" + randId()+ "/photo"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    public void getUnexistingPhotoTest() throws Exception {
        given(itemService.getPhoto(anyLong())).willReturn(Optional.empty());

        mvc.perform(get("/v1/items/" + randId()+ "/photo"))
                .andExpect(status().isNoContent())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    public void postPhotoTest() throws Exception {
        given(itemService.updatePhoto(anyLong(), any())).willReturn(true);

        mvc.perform(MockMvcRequestBuilders.multipart("/v1/items/" + randId() + "/photo")
                .file(mockFile()))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    public void replacePhotoTest() throws Exception {
        given(itemService.updatePhoto(anyLong(), any())).willReturn(false);

        mvc.perform(MockMvcRequestBuilders.multipart("/v1/items/" + randId() + "/photo")
                .file(mockFile()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    public void deletePhotoTest() throws Exception {
        Mockito.doNothing().when(itemService).deletePhoto(anyLong());

        mvc.perform(delete("/v1/items/" + randId() + "/photo"))
                .andExpect(status().isNoContent());
    }

    private MockMultipartFile mockFile() {
        return new MockMultipartFile("file", "filename.jpg", MediaType.IMAGE_JPEG_VALUE, "aaa".getBytes());
    }
}
