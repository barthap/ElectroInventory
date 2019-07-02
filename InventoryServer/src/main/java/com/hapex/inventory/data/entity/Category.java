package com.hapex.inventory.data.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hapex.inventory.utils.InvalidValueException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Category parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<Category> subcategories = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<Item> items = new HashSet<>();

    public void addSubcategory(Category subcategory) {
        subcategory.setParent(this);
        this.subcategories.add(subcategory);
    }

    public void removeSubcategory(Category subcategory) {
        this.subcategories.remove(subcategory);
        subcategory.setParent(null);
    }

    public boolean hasChildren() {
        return !getSubcategories().isEmpty();
    }

    public boolean hasItems() {
        return !getItems().isEmpty();
    }

    public void setName(String name) {
        if(StringUtils.isBlank(name))
            throw new InvalidValueException("Name cannot be empty");

        this.name = name;
    }

    public Category() {}
    public Category(String name) { setName(name); }

    @PreRemove
    public void preRemove() {
        items.forEach(item -> item.setCategory(null));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
