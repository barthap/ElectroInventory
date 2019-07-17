package com.hapex.inventory.test.unit.other;

import com.hapex.inventory.data.dto.ItemDTO;
import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ObjectMapperTests {
    private ModelMapper mapper;

    @Before
    public void setUp() {
        //need to set up new mapper before each test - they have different config
        mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    @Test
    public void itemToDTOtest() {
        Category cat = new Category();
        cat.setId(2L);
        cat.setName("cat1");

        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setDescription("desc");
        item.setQuantity(2);
        item.setWebsite("web");
        item.setCategory(cat);

        ItemDTO dto = mapper.map(item, ItemDTO.class);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getQuantity(), item.getQuantity());
        assertEquals(dto.getWebsite(), item.getWebsite());
        assertEquals(dto.getCategoryId(), cat.getId());
    }

    @Test
    public void itemFromDTOtest() {
        ItemDTO dto = new ItemDTO("FFF", null, 5, null, 1L, 2L, null);

        TypeMap<ItemDTO, Item> itemMapper = mapper.createTypeMap(ItemDTO.class, Item.class).addMappings(mapper -> {
            mapper.skip(Item::setId);
            mapper.skip(Item::setCategory);
            mapper.skip((Item item, Long categoryId) -> item.getCategory().setId(categoryId));
        });

        Item item = itemMapper.map(dto);

        //assertEquals(item.getId(), dto.getId());
        //assertEquals(item.getCategory().getId(), dto.getCategoryId());
        assertNull(item.getId());
        assertNull(item.getCategory());

        assertEquals(item.getName(), dto.getName());
        assertNull(item.getDescription());
        assertEquals(5, item.getQuantity());
        assertNull(item.getWebsite());
    }

    @Test
    public void mergeEntityTest() {
        Item item = new Item("FFF");
        item.setWebsite("www");
        item.setId(1L);
        item.setQuantity(5);

        assertNotNull(item.getWebsite());
        assertNull(item.getDescription());
        assertEquals(item.getQuantity(), 5);

        ItemDTO dto = new ItemDTO(null, "desc", 2, null, null, null, null);
        assertNull(dto.getId());
        assertNull(dto.getWebsite());

        TypeMap<ItemDTO, Item> itemMapper = mapper.createTypeMap(ItemDTO.class, Item.class).addMappings(mapper -> {
            mapper.skip(Item::setId);
            mapper.skip(Item::setCategory);
            mapper.skip((Item i, Long categoryId) -> i.getCategory().setId(categoryId));
        });

        itemMapper.map(dto, item);
        assertNotNull(item.getId());
        assertNull(item.getCategory());
        assertEquals(item.getWebsite(), "www");
        assertEquals(item.getQuantity(), dto.getQuantity());
        assertEquals(item.getDescription(), dto.getDescription());

    }
}
