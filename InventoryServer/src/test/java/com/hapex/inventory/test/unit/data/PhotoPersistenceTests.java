package com.hapex.inventory.test.unit.data;

import com.hapex.inventory.data.entity.Item;
import com.hapex.inventory.data.entity.Photo;
import com.hapex.inventory.data.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PhotoPersistenceTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;

    @Test
    public void persistItemTest() {
        Item item = new Item("item");
        item.setPhoto(new Photo("img.jpg"));

        item = repository.save(item);
        entityManager.flush();

        Item found = repository.findById(item.getId()).orElse(null);
        assertThat(found.getPhoto().getFilename()).isEqualTo(item.getPhoto().getFilename());
        assertThat(found.getPhoto().getId()).isEqualTo(found.getId());  //testing @MapsId
    }

    @Test
    public void attachPhotoTest() {
        Item item = entityManager.persistFlushFind(new Item("item"));
        assertThat(item.getPhoto()).isNull();

        item.addPhoto(new Photo("some.png"));
        item = repository.save(item);
        entityManager.flush();

        assertThat(item.getPhoto().getFilename()).isEqualTo("some.png");
        assertThat(item.getPhoto().getId()).isEqualTo(item.getId());  //testing @MapsId
    }

    @Test
    public void detachPhotoTest() {
        Item item = new Item("item");
        item.addPhoto(new Photo("ph.jpg"));
        item = entityManager.persistFlushFind(item);

        item.removePhoto();

        Item found = repository.save(item);
        entityManager.flush();

        assertThat(found.getId()).isEqualTo(item.getId());
        assertThat(found.getPhoto()).isNull();
    }

    @Test
    public void removeItemTest() {
        Item item = new Item("item");
        item.addPhoto(new Photo("ph.jpg"));
        item = entityManager.persistFlushFind(item);

        repository.delete(item);
        entityManager.flush();

        //FIXME: No idea for assertions here. Just watch SQL
    }
}
