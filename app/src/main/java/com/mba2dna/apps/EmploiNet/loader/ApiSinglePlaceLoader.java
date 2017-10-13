package com.mba2dna.apps.EmploiNet.loader;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

//import org.apache.http.NameValuePair;

import java.util.ArrayList;

import com.mba2dna.apps.EmploiNet.config.Constant;
//import com.mba2dna.apps.EmploiNet.json.JSONStream;
import com.mba2dna.apps.EmploiNet.json.JSONStream;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;

import org.apache.http.NameValuePair;

public class ApiSinglePlaceLoader extends AsyncTask<String, String, Offre> {
    Callback<Offre> callback;

    JSONStream jsonStream = new JSONStream();

    private Gson gson = new Gson();
    boolean success = false;
    private int place_id;

    public ApiSinglePlaceLoader(int place_id, Callback<Offre> callback) {
        this.callback = callback;
        this.place_id = place_id;
    }

    @Override
    protected Offre doInBackground(String... params) {

        String URL_SINGLE_PLACES = Constant.getURLApiSinglePlace(place_id);
        try {
            Log.e("CITY", URL_SINGLE_PLACES);
            Thread.sleep(300);
            JsonReader reader = jsonStream.getJsonResult(URL_SINGLE_PLACES, jsonStream.METHOD_GET, new ArrayList<NameValuePair>());

            Offre offres = gson.fromJson(reader, Offre.class);
            reader.close();

            success = true;
            return offres;
        } catch (Exception e) {
            Log.e("Error",e.getMessage());
            e.printStackTrace();
            success = false;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Offre result) {
        // Send callback when finish
        if (success) {
            callback.onSuccess(result);
        }else{
            callback.onError("failed");
        }
        super.onPostExecute(result);
    }

}
