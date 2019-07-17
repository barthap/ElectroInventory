package com.hapex.inventory.test.integration;

import com.hapex.inventory.InventoryApplication;
import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.hapex.inventory.test.utils.TestUtils.asJsonString;
import static com.hapex.inventory.test.utils.TestUtils.randId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = InventoryApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties"
)
@WithMockUser
public class ItemIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository repository;


    @Test
    public void itemListTest_shouldReturn200() throws Exception {
        createTestItem("bc547");

        mvc.perform(get("/v1/items")
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value("bc547"));
    }

    @Test
    public void getItemTest_shouldReturn200() throws Exception {
        long id = createTestItem("irlz44n");

        mvc.perform(get("/v1/items/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("irlz44n"));
    }

    @Test
    public void getUnexistingItemTest_shouldReturn404() throws Exception {
        mvc.perform(get("/v1/items/" + randId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createItemTest_shouldReturn201() throws Exception {
        ItemDTO dto = new ItemDTO();
        dto.setName("bc337");

        mvc.perform(post("/v1/items").contentType(MediaType.APPLICATION_JSON).content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("bc337"));
    }

    @Test
    public void createInvalidItemTest_shouldReturn400() throws Exception {
        ItemDTO dto = new ItemDTO();
        dto.setName("");

        mvc.perform(post("/v1/items").contentType(MediaType.APPLICATION_JSON).content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemTest_shouldReturn200() throws Exception {
        long id = createTestItem("uln2803");

        Item updated = new Item();
        updated.setName("uln2803A");

        mvc.perform(put("/v1/items/"+id).contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("uln2803A"));


        assertThat(repository.findById(id).orElseThrow(()->new Exception("Not Found!")).getName()).isEqualTo("uln2803A");
    }

    @Test
    public void updateUnexistingItemTest_shouldReturn404() throws Exception {
        Item updated = new Item();
        updated.setName("uln2803A");

        mvc.perform(put("/v1/items/" + randId()).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updated)))
                .andExpect(status().isNotFound());
    }


    @Test
    public void deleteItemTest_shouldReturn204() throws Exception {
        long id = createTestItem("atmega8");

        mvc.perform(delete("/v1/items/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUnexistingItemTest_shouldReturn404() throws Exception {
        mvc.perform(delete("/v1/items/" + randId()))
                .andExpect(status().isNotFound());
    }


    //helpers
    private long createTestItem(String name) {
        repository.deleteAll();

        Item item = new Item();
        item.setName(name);

        return repository.save(item).getId();
    }
}
