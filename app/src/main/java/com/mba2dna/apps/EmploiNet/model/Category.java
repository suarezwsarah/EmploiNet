package com.mba2dna.apps.EmploiNet.model;

import java.io.Serializable;

public class Category implements Serializable {
    public int cat_id;
    public String category;
    public String photo;
    public int num;

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getName() {
        return category;
    }

    public void setName(String name) {
        this.category = name;
    }

    public String getImage() {
        return photo;
    }

    public void setImage(String image) {
        photo = image;
    }

    public int getCount_recip() {
        return num;
    }

    public void setCount_recip(int count_recip) {
        num = count_recip;
    }
}
