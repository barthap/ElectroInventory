package com.hapex.inventory.test.unit.data;

import com.hapex.inventory.data.entity.Category;
import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.repository.CategoryRepository;
import com.hapex.inventory.data.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CategoryPersistenceTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void setParentTest() {
        Category parent = new Category("parent1");
        Category child = new Category("child");
        entityManager.persist(parent);
        assertThat(parent.getId()).isNotNull();

        parent.addSubcategory(child);
        categoryRepository.save(child);
        entityManager.flush();

        assertThat(child.getId()).isNotNull();
        assertThat(child.getParent().getName()).isEqualTo(parent.getName());
    }

    @Test
    public void switchParentTest() {
        Category parent1 = new Category("parent1");
        Category parent2 = new Category("parent2");
        Category child = new Category("child");
        entityManager.persist(parent1);
        entityManager.persist(parent2);
        parent1.addSubcategory(child);
        categoryRepository.save(child);
        entityManager.flush();
        assertThat(child.getParent().getName()).isEqualTo(parent1.getName());

        parent1.removeSubcategory(child);
        parent2.addSubcategory(child);

        categoryRepository.save(child);
        entityManager.flush();

        assertThat(child.getParent().getName()).isEqualTo(parent2.getName());
    }

    @Test(expected = PersistenceException.class)    //sometimes its ConstraintViolationException
    public void removeParentCategoryTest() {
        Category parent = new Category("parent");
        Category child = new Category("child");
        categoryRepository.save(parent);
        parent.addSubcategory(child);
        categoryRepository.save(child);
        entityManager.flush();

        assertThat(parent.hasChildren()).isTrue();
        assertThat(child.getParent()).isNotNull();

        categoryRepository.delete(parent);
        entityManager.flush();  //should throw

        //it would be for testing cascade
        //child =  categoryRepository.findById(child.getId()).get();
        //assertThat(child.getParent()).isNull();
    }

    @Test
    public void removeCategoryHavingItemsTest() {
        Category cat = new Category("cat");
        categoryRepository.save(cat);

        Item item = new Item("item");
        item.setCategory(cat);
        itemRepository.save(item);
        cat = entityManager.persistFlushFind(cat);

        assertThat(item.getId()).isNotNull();
        assertThat(cat.getId()).isNotNull();
        assertThat(item.getCategory().getName()).isEqualTo(cat.getName());

        assertThat(cat.hasItems()).isTrue();

        categoryRepository.delete(cat);
        assertThat(item.getCategory()).isNull();
        entityManager.flush();

        Item found = itemRepository.findById(item.getId()).orElse(null);
        assertThat(found.getCategory()).isNull();
    }
}
