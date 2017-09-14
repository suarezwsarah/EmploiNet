package com.mba2dna.apps.EmploiNet.model;

/*import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;*/

import java.io.Serializable;

public class Reciepe implements Serializable {//, ClusterItem
    public int id;
    public String title;
    public String photo;
    public String category;
    public String portion;
    public String deficulty;
    public String prepair;
    public String cocking;
    public String ingridiant;
    public String method;
    public String tags;
    public int views;

    public int user_id;
    public String entry_date;









    public boolean isDraft(){
        return (title == null && category == null && deficulty == null && portion == null);
    }

}
