package com.mba2dna.apps.EmploiNet.loader;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

//import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import com.mba2dna.apps.EmploiNet.data.Constant;
//import com.mba2dna.apps.EmploiNet.json.JSONStream;
import com.mba2dna.apps.EmploiNet.json.JSONStream;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.InfoEmploi;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.model.Category;
import com.mba2dna.apps.EmploiNet.model.Images;
import com.mba2dna.apps.EmploiNet.model.Candidats;
import com.mba2dna.apps.EmploiNet.utils.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by UserSession on 01/12/2016.
 */

public class ApiSearchLoader extends AsyncTask<String, String, ApiClient> {
        Callback<ApiClient> callback;

        JSONStream jsonStream = new JSONStream();
    String URL = Constant.getURLApiClientData();

private Gson gson = new Gson();
        boolean success = false;
private String search;

public ApiSearchLoader(String search, Callback<ApiClient> callback) {
        this.callback = callback;
        this.search = search;
        }

@Override
protected ApiClient doInBackground(String... params) {

        try {
            String s = params[0];
            Log.e("CITY", URL+"?offres=true&s="+s);
        Thread.sleep(300);
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("search", search));
        JsonReader reader = jsonStream.getJsonResult(URL+"?offres=true&s="+s , jsonStream.METHOD_GET, urlParameters);

            ApiClient apiClient = new ApiClient();
            List<Offre> listArticles = new ArrayList<>();
            List<Candidats> listCandidats = new ArrayList<>();
            List<Category> listReciepesCategory = new ArrayList<>();
            List<Images> listImages = new ArrayList<>();
            List<InfoEmploi> listInfoEmplois = new ArrayList<>();

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
                }  else if (name.equals("candidats")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Candidats candidats = gson.fromJson(reader, Candidats.class);
                        listCandidats.add(candidats);
                    }
                    reader.endArray();
                } else if (name.equals("infoEmplois")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        InfoEmploi tips = gson.fromJson(reader, InfoEmploi.class);
                        listInfoEmplois.add(tips);
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();

            // set attribute object ApiClient
            apiClient.offres = listArticles;
            apiClient.candidatsList = listCandidats;
            apiClient.infoEmplois = listInfoEmplois;

        success = true;
        return apiClient;
        } catch (Exception e) {
        Log.e("Error",e.getMessage());
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
               Log.v("RES",result.toString());
        } else {
            callback.onError("failed");
        }
        super.onPostExecute(result);
    }

        }
