package com.mba2dna.apps.EmploiNet.model;

import java.io.Serializable;

/**
 * Created by BIDA on 9/12/2017.
 */

public class UserSession implements Serializable {
    public int id;
    public String fullName;
    public String Email;
    public String Pic;

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPic(String pic) {
        Pic = pic;
    }
}
