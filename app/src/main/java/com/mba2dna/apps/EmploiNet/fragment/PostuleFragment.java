package com.mba2dna.apps.EmploiNet.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.OffreDetailActivity;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.model.Offre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import greco.lorenzo.com.lgsnackbar.LGSnackbarManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static greco.lorenzo.com.lgsnackbar.style.LGSnackBarTheme.SnackbarStyle.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostuleFragment extends DialogFragment {

    private Offre mOffre;
    private Button cancel, postulerBtn;
    private TextView title;
    private SQLiteHandler db;
    private List<String> motivations = new ArrayList<String>(), cv_word = new ArrayList<String>(), cv_id = new ArrayList<String>(), cv_titles = new ArrayList<String>(), motivation_ids = new ArrayList<String>();
    private Spinner cvsSpn, motivationSpn;
    private View lyt_progress;
    private int userID, RecruteurID;
    private String email;

    public static PostuleFragment newInstance(Offre mOffre) {
        PostuleFragment fragment = new PostuleFragment();
        fragment.mOffre = mOffre;
        fragment.RecruteurID = mOffre.recruteur_id;

        return fragment;
    }

    public PostuleFragment() {
        // Required empty public constructor
    }

    private void showProgress(boolean show) {

        if (show) {
            lyt_progress.setVisibility(View.VISIBLE);
        } else {
            lyt_progress.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_postule, container, false);
        db = new SQLiteHandler(getContext());
        userID = db.getUserID();
        email=db.getUserEmail();
        title = (TextView) v.findViewById(R.id.titleShop);
        title.setText(mOffre.title + " pour le recruteur " + mOffre.contact_info);
        cvsSpn = (Spinner) v.findViewById(R.id.spinner1);
        motivationSpn = (Spinner) v.findViewById(R.id.spinner2);
        lyt_progress = v.findViewById(R.id.lyt_progress);
        cancel = (Button) v.findViewById(R.id.btncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        postulerBtn = (Button) v.findViewById(R.id.btnpostuler);
        postulerBtn.setEnabled(false);
        postulerBtn.setClickable(false);
        postulerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.isUserExist()) {
                    showProgress(true);
                    int userId = userID;
                    OkHttpClient client = new OkHttpClient();
                    String URL = Constant.getURLApiClientData();
                    Log.e("RESPENSE", userId + "");
                    Log.e("userid", userId + "");
                    Log.e("recruteurid", RecruteurID + "");
                    Log.e("offreid", mOffre.id + "");
                    if(cv_word.size()>0)
                    Log.e("cvword", cv_word.get(cvsSpn.getSelectedItemPosition()) + "");
                    if(motivation_ids.size()>0)
                    Log.e("lettreid", motivation_ids.get(motivationSpn.getSelectedItemPosition()) + "");


                    HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
                    urlBuilder.addQueryParameter("postuleuser", "true");
                    urlBuilder.addQueryParameter("userid", userId + "");
                    urlBuilder.addQueryParameter("useremail", email + "");
                    urlBuilder.addQueryParameter("recruteurid", RecruteurID + "");
                    urlBuilder.addQueryParameter("offreid", mOffre.id + "");
                    if(cv_word.size()>0)
                    urlBuilder.addQueryParameter("cvword", cv_word.get(cvsSpn.getSelectedItemPosition()) + "");
                    if(motivation_ids.size()>0)
                    urlBuilder.addQueryParameter("lettreid", motivation_ids.get(motivationSpn.getSelectedItemPosition()) + "");
                    String url = urlBuilder.build().toString();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String S = response.body().string();

                        Log.e("RESPENSE", S);

                        JSONObject resulat = new JSONObject(S);
                        if(resulat.getString("potuler").equals("OK")){
                            LGSnackbarManager.show(SUCCESS, "Vous avez bien postuler a cette offre!");
                            OffreDetailActivity callingActivity = (OffreDetailActivity) getActivity();
                            callingActivity.onUserSelectValue("OK");
                            dismiss();
                        }
                        showProgress(false);


                    } catch (JSONException e) {
                        Log.e("ERROR EEE", e.getMessage());
                        e.printStackTrace();
                        showProgress(false);

                    } catch (IOException e) {
                        Log.e("ERROREEE", e.getMessage());
                        e.printStackTrace();
                        showProgress(false);
                    }
                }
            }
        });
        if (db.isUserExist()) {
            int userId = userID;
            OkHttpClient client = new OkHttpClient();
            String URL = Constant.getURLApiClientData();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
            urlBuilder.addQueryParameter("checkuser", "true");
            urlBuilder.addQueryParameter("userid", userId + "");
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String S = response.body().string();
                Log.e("RESPENSE", userId + "");
                Log.e("RESPENSE", S);
                showProgress(false);
                postulerBtn.setEnabled(true);
                postulerBtn.setClickable(true);
                JSONObject jObject = new JSONObject(S);

                JSONArray cvs = jObject.getJSONArray("cvs"); // get data object
                JSONObject cv = cvs.getJSONObject(0);
                if (!cv.getString("cv_word").equals(""))
                    cv_word.add(cv.getString("cv_word"));
                if (!cv.getString("cv_word2").equals(""))
                    cv_word.add(cv.getString("cv_word2"));
                if (!cv.getString("cv_word3").equals(""))
                    cv_word.add(cv.getString("cv_word3"));
                if (!cv.getString("cv_title").equals(""))
                    cv_titles.add(cv.getString("cv_title"));
                if (!cv.getString("cv_title2").equals(""))
                    cv_titles.add(cv.getString("cv_title2"));
                if (!cv.getString("cv_title3").equals(""))
                    cv_titles.add(cv.getString("cv_title3"));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cv_titles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cvsSpn.setAdapter(adapter);

                JSONArray lettres = jObject.getJSONArray("lettres"); // get data object
                for (int n = 0; n < lettres.length(); n++) {
                    JSONObject lettre = lettres.getJSONObject(n);
                    if (!lettre.getString("title").equals("")) {
                        motivations.add(lettre.getString("title"));
                        motivation_ids.add(lettre.getString("id"));
                    }
                }
                if (lettres.length() > 0) {
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, motivations);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    motivationSpn.setAdapter(adapter2);
                }


            } catch (JSONException e) {
                Log.e("ERROR EEE", e.getMessage());
                e.printStackTrace();


            } catch (IOException e) {
                Log.e("ERROREEE", e.getMessage());
                e.printStackTrace();
            }
        }
        setCancelable(false);
        return v;
    }

}
