package com.hapex.inventory.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
@Entity
@Table(name = "Photos")
public class Photo {

    @Id
    private long id;

    private String filename;

    @OneToOne
    @MapsId
    private Item item;

    public Photo() {}
    public Photo(String filename) {
        this.filename = filename;
    }

    public boolean isValid() {
        if(filename == null || filename.isEmpty())
            return false;

        File f = new File(filename);
        try {
            f.getCanonicalPath();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
}
