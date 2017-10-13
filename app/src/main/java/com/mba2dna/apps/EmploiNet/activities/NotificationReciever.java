package com.mba2dna.apps.EmploiNet.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.config.Constant;
import com.mba2dna.apps.EmploiNet.model.Offre;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationReciever extends AppCompatActivity {
    String URL = Constant.getURLApiClientData();
    private int id;
    private Offre offres = null;
    private static final String EXTRA_OBJ = "com.mba2dna.apps.EmploiNet.EXTRA_OBJ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reciever);
        offres = new Offre();
        Intent mIntent = getIntent();
        id = mIntent.getIntExtra("id", 0);
        Log.e("IDD", id + "");
        if (id != 0) {
            Log.e("ID", id + "");
            final OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            final Request request = new Request.Builder()
                    .url(URL + "/?i=" + id)
                    .build();
            Log.e("LINK SINGLE", URL + "/?i=" + id);

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();


                        JSONObject json = new JSONObject(responseData);

                        JSONObject ri = json.getJSONObject("article");

                        Log.e("Resultat", ri.names().toString() + "");
                        offres.type_activite = ri.getString("type_activite");
                        offres.id = ri.getInt("id");

                        offres.fonction = ri.getString("fonction");
                        offres.photo = ri.getString("photo");
                        offres.description = ri.getString("description");
                        offres.email_candidature = ri.getString("email_candidature");
                        offres.contact_info = ri.getString("contact_info");
                        offres.pub_date = ri.getString("pub_date");
                        offres.title = ri.getString("title");
                        Intent intent = new Intent(NotificationReciever.this, OffreDetailActivity.class);
                        intent.putExtra(EXTRA_OBJ, offres);
                       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callMainActivty();
                    }

                }
            });

            thread.start();


        } else {
            callMainActivty();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        callMainActivty();
    }

    private void callMainActivty() {
        Intent intent = new Intent(this, ActivityMain.class);
        //  intent.putExtra(EXTRA_OBJ, offres);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
