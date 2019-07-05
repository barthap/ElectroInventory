package com.hapex.inventory.test.unit.controller;

import com.hapex.inventory.controller.CategoryController;
import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.service.CategoryService;
import com.hapex.inventory.service.ItemService;
import com.hapex.inventory.utils.ConflictException;
import com.hapex.inventory.utils.InvalidValueException;
import com.hapex.inventory.utils.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.hapex.inventory.test.utils.TestUtils.randId;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CategoryController.class)
@WithMockUser
public class CategoryControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryService service;

    //TODO: criteria/predicate tests of CategoryController::findAll()

    @Test
    public void deleteCategoryTest() throws Exception {
        Mockito.doNothing().when(service).deleteById(anyLong());

        mvc.perform(delete("/v1/categories/" + randId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCategoryHavingChildrenTest() throws Exception {
        doThrow(new ConflictException("")).when(service).deleteById(anyLong());

        mvc.perform(delete("/v1/categories/" + randId()))
                .andExpect(status().isConflict());
    }
}
