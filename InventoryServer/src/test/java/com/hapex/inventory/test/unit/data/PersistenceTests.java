package com.hapex.inventory.test.unit.data;

import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.data.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersistenceTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void persistenceSimpleTest() throws Exception{
        //given
        Item item = new Item();
        item.setName("item1");
        entityManager.persist(item);
        entityManager.flush();

        //when
        Optional<Item> found = itemRepository.findById(item.getId());

        //assert
        assertThat(found.isPresent()).isTrue();
        assertThat(found.orElseThrow(()->new Exception("Not Found!")).getName()).isEqualTo(item.getName());
    }
}
