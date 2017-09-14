package com.mba2dna.apps.EmploiNet.model;

/*import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;*/

import java.io.Serializable;

public class Offre implements Serializable {//, id,reference,recruteur_id,type_activite,fonction,postes,salaire,pays,region,contrat,description,contact_info,email_candidature,pub_date,views
    public int id;
    public String title;
    public String photo;
    public String type_activite;
    public String fonction;
    public String description;
    public  String contact_info;
    public  String email_candidature;

    public String reference;
    public String postes;
    public String salaire;
    public  String pays;
    public  String willaya;
    public  String contrat;
    public int views;
    public int recruteur_id;
    public  String pub_date;








    public boolean isDraft(){
        return (title == null && type_activite == null );
    }

}
