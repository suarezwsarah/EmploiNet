package com.mba2dna.apps.EmploiNet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiClient implements Serializable {
    public List<Offre> offres = new ArrayList<>();
    public List<Candidats> candidatsList = new ArrayList<>();
    public List<Category> reciepes_category = new ArrayList<>();
    public List<Images> images = new ArrayList<>();
    public List<InfoEmploi> infoEmplois = new ArrayList<>();
    public UserSession UserSessions = new UserSession();

    public ApiClient() {
    }

    public ApiClient(List<Offre> offres, UserSession userSession, List<Images> images, List<Candidats> candidatsList, List<InfoEmploi> infoEmplois, List <Category> category) {
        this.offres = offres;
        this.reciepes_category = category;
        this.candidatsList = candidatsList;
        this.UserSessions = userSession;
        this.images = images;
        this.infoEmplois = infoEmplois;
    }
}
