package com.mba2dna.apps.EmploiNet.model;

/*import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;*/

import java.io.Serializable;

public class Offre implements Serializable {
    public int id;
    public String title;
    public String photo;
    public String type_activite;
    public String fonction;
    public String description;
    public String contact_info;
    public String email_candidature;

    public String reference;
    public String postes;
    public String salaire;
    public String pays;
    public String willaya;
    public String contrat;
    public int views;
    public int recruteur_id;
    public String pub_date;


    public boolean isDraft() {
        return (title == null && type_activite == null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType_activite() {
        return type_activite;
    }

    public void setType_activite(String type_activite) {
        this.type_activite = type_activite;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

    public String getEmail_candidature() {
        return email_candidature;
    }

    public void setEmail_candidature(String email_candidature) {
        this.email_candidature = email_candidature;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPostes() {
        return postes;
    }

    public void setPostes(String postes) {
        this.postes = postes;
    }

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getWillaya() {
        return willaya;
    }

    public void setWillaya(String willaya) {
        this.willaya = willaya;
    }

    public String getContrat() {
        return contrat;
    }

    public void setContrat(String contrat) {
        this.contrat = contrat;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getRecruteur_id() {
        return recruteur_id;
    }

    public void setRecruteur_id(int recruteur_id) {
        this.recruteur_id = recruteur_id;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }
}
