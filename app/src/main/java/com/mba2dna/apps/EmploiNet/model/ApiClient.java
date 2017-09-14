package com.mba2dna.apps.EmploiNet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiClient implements Serializable {
    public List<Offre> offres = new ArrayList<>();
    public List<Candidats> candidatsList = new ArrayList<>();
    public List<Category> reciepes_category = new ArrayList<>();
    public List<Images> images = new ArrayList<>();
    public List<Tip> tips = new ArrayList<>();
    public UserSession UserSessions = new UserSession();

    public ApiClient() {
    }

    public ApiClient(List<Offre> offres, UserSession userSession, List<Images> images, List<Candidats> candidatsList, List<Tip> tips) {
        this.offres = offres;
        this.candidatsList = candidatsList;
        this.UserSessions = userSession;
        this.images = images;
        this.tips = tips;
    }
}