package com.mba2dna.apps.EmploiNet.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.model.Offre;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostuleFragment extends DialogFragment {

    private Offre mOffre;
    private Button cancel;
    private TextView title;
    private SQLiteHandler db;
    public static PostuleFragment newInstance(Offre mOffre) {
        PostuleFragment fragment = new PostuleFragment();
        fragment.mOffre = mOffre;
        return fragment;
    }

    public PostuleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_postule, container, false);
        db = new SQLiteHandler(getContext());
        title = (TextView) v.findViewById(R.id.titleShop);
        title.setText(mOffre.title+" pour le recruteur "+mOffre.contact_info);
        cancel = (Button) v.findViewById(R.id.btncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if(db.isUserExist()){
            int userId=db.getUserID();
            OkHttpClient client = new OkHttpClient();
            String URL = Constant.getURLApiClientData();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
            urlBuilder.addQueryParameter("checkuser", "true");
            urlBuilder.addQueryParameter("userid", userId+"");
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String S =response.body().string();
                Log.e("RESPENSE", userId+"");
                Log.e("RESPENSE", S);

            } catch (IOException e) {
                Log.e("ERROR",e.getMessage());
                e.printStackTrace();
            }
        }
        setCancelable(false);
        return v;
    }

}
