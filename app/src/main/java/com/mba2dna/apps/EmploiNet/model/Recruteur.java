package com.mba2dna.apps.EmploiNet.model;

import java.io.Serializable;

public class Recruteur implements Serializable {
    public int rec_id;
    public String recruteur;
    public String photo;
    public int num;

    public int getRec_id() {
        return rec_id;
    }

    public void setRec_id(int cat_id) {
        this.rec_id = cat_id;
    }

    public String getName() {
        return recruteur;
    }

    public void setName(String name) {
        this.recruteur = name;
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

