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
@Table(name = "Locations")
public class Location {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Location parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<Location> children = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "location")
    private Set<Item> items = new HashSet<>();

    public void addChildLocation(Location subcategory) {
        subcategory.setParent(this);
        this.children.add(subcategory);
    }

    public void removeChildLocation(Location subcategory) {
        this.children.remove(subcategory);
        subcategory.setParent(null);
    }

    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    public boolean hasItems() {
        return !getItems().isEmpty();
    }

    public void setName(String name) {
        if(StringUtils.isBlank(name))
            throw new InvalidValueException("Name cannot be empty");

        this.name = name;
    }

    @JsonGetter("fullName")
    public String getFullName() {
        if(this.parent == null)
            return name;
        else
            return this.parent.getFullName() + " - " + this.name;
    }

    public Location() {}
    public Location(String name) { setName(name); }

    @PreRemove
    public void preRemove() {
        items.forEach(item -> item.setLocation(null));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
