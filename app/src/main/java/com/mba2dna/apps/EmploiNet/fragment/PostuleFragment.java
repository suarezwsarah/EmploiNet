package com.mba2dna.apps.EmploiNet.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.model.Offre;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostuleFragment extends DialogFragment {

    private Offre mOffre;
    private Button cancel;
    private TextView title;

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
        title = (TextView) v.findViewById(R.id.titleShop);
        title.setText(mOffre.title+" pour le recruteur "+mOffre.contact_info);
        cancel = (Button) v.findViewById(R.id.btncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setCancelable(false);
        return v;
    }

}
