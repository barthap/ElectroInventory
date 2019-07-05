package com.hapex.inventory.data.entity;

import com.hapex.inventory.utils.InvalidValueException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Items")
public class Item {
    @Id
    @GeneratedValue
    private Long id;

    @Setter(AccessLevel.NONE)
    private String name;

    @Setter(AccessLevel.NONE)
    private int quantity;

    private String description;
    private String website;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    public Item() {}
    public Item(String name) {
        setName(name);
    }

    public void setName(String name) {
        if(StringUtils.isBlank(name))
            throw new InvalidValueException("Name cannot be empty");

        this.name = name;
    }

    public void setQuantity(int quantity) {
        if(quantity < 0)
            quantity = 0;

        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
}
