package com.mba2dna.apps.EmploiNet.loader;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import com.mba2dna.apps.EmploiNet.config.Constant;
import com.mba2dna.apps.EmploiNet.config.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.json.JSONStream;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.InfoEmploi;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.model.Category;
import com.mba2dna.apps.EmploiNet.model.Candidats;
import com.mba2dna.apps.EmploiNet.model.Recruteur;
import com.mba2dna.apps.EmploiNet.model.UserSession;
import com.mba2dna.apps.EmploiNet.utils.Callback;

public class ApiClientLoader extends AsyncTask<String, String, ApiClient> {
    Callback<ApiClient> callback;

    JSONStream jsonStream = new JSONStream();
    String URL = Constant.getURLApiClientData();
    private SQLiteHandler db;

    private Gson gson = new Gson();
    boolean success = false;

    public ApiClientLoader(Callback<ApiClient> callback) {
        this.callback = callback;
    }

    @Override
    protected ApiClient doInBackground(String... params) {
        try {
            String s = params[0];
            Log.e("ARTCILE:", URL + s);
            // Thread.sleep(100);

            JsonReader reader = jsonStream.getJsonResult(URL + s, jsonStream.METHOD_GET, new ArrayList<NameValuePair>());

            ApiClient apiClient = new ApiClient();
            List<Offre> listArticles = new ArrayList<>();
            List<Candidats> listCandidats = new ArrayList<>();
            List<Category> listReciepesCategory = new ArrayList<>();
            List<Recruteur> listRecruteur = new ArrayList<>();
            List<InfoEmploi> listInfoEmplois = new ArrayList<>();
            UserSession userSession = new UserSession();
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("Offres")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Offre offres = gson.fromJson(reader, Offre.class);
                        listArticles.add(offres);
                    }
                    reader.endArray();
                } else if (name.equals("category")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Category OffresCategory = gson.fromJson(reader, Category.class);
                        listReciepesCategory.add(OffresCategory);
                    }
                    reader.endArray();
                } else if (name.equals("toprecruter")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Recruteur recruteurs = gson.fromJson(reader, Recruteur.class);
                        listRecruteur.add(recruteurs);
                    }
                    reader.endArray();
                } else if (name.equals("candidats")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Candidats candidats = gson.fromJson(reader, Candidats.class);
                        listCandidats.add(candidats);
                    }
                    reader.endArray();
                } else if (name.equals("infos")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        InfoEmploi tips = gson.fromJson(reader, InfoEmploi.class);
                        listInfoEmplois.add(tips);
                    }
                    reader.endArray();
                } else if (name.equals("user")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                       // UserSession user = gson.fromJson(reader, UserSession.class);
                       // userSession = user;
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();

            // set attribute object ApiClient
            apiClient.offres = listArticles;
            apiClient.candidatsList = listCandidats;
            apiClient.UserSessions = userSession;
            apiClient.reciepes_category = listReciepesCategory;
            apiClient.recruteur = listRecruteur;
            apiClient.infoEmplois = listInfoEmplois;

            success = true;
            return apiClient;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            success = false;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ApiClient result) {
        // Send callback when finish
        if (success) {
            callback.onSuccess(result);
            //   Log.v("RES",result.toString());
        } else {
            callback.onError("failed");
        }
        super.onPostExecute(result);
    }


}
