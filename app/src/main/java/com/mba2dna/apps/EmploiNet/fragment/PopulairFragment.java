package com.mba2dna.apps.EmploiNet.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.activities.ArticleDetailActivity;
import com.mba2dna.apps.EmploiNet.adapter.AdapterPopulair;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopulairFragment extends Fragment {

    private static AdapterPopulair adapter;
    private SQLiteHandler db;
    private View lyt_progress;
    private static View lyt_not_found;
    static RecyclerView recyclerView;
    private View view;
    private List<Offre> items = new ArrayList<>();

    public PopulairFragment() {
        // Required empty public constructor
    }

    public static PopulairFragment newInstance() {
        Bundle args = new Bundle();
        PopulairFragment fragment = new PopulairFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_populair, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new SQLiteHandler(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.rvCards);
        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getData();



    }

    private void getData() {

        if (Tools.cekConnection(getActivity())) {
            Snackbar snackbar = Snackbar.make(view, "تحديث البيانات...", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
            CommonUtils.setRobotoThinFont(getActivity(), tv);
            snackbar.show();
            showProgress(true);
            ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
                @Override
                public void onSuccess(ApiClient result) {
                    items = result.offres;
                    Log.v("RECIEPES LENTH", "" + items.size());
                    Snackbar snackbar = Snackbar.make(view, "Mise à jour réussie", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getActivity(), tv);
                    snackbar.show();
                    showProgress(false);
                    adapter = new AdapterPopulair(getContext(), items);
                    adapter.setItemClickListener(new AdapterPopulair.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int itemPosition, final View view, final Offre reciepe) {
                            ArticleDetailActivity.navigate((ActivityMain) getActivity(), view.findViewById(R.id.ivSportPreview), reciepe);
                        }

                    });

                    recyclerView.setAdapter(adapter);
                    checkItems();
                }

                @Override
                public void onError(String result) {
                    showProgress(false);
                    Snackbar snackbar = Snackbar.make(view, "Une erreur s’est produite lors de l’envoi de commandes au serveur", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getActivity(), tv);
                    snackbar.show();
                }
            });
            task.execute("/?b=true");

        } else {
            items = db.getTopViews();
            adapter = new AdapterPopulair(getContext(), items);

            // adapter.addAll(items);
            // adapter.notifyDataSetChanged();
            adapter.setItemClickListener(new AdapterPopulair.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, final View view, final Offre offres) {
                    ArticleDetailActivity.navigate((ActivityMain) getActivity(), view.findViewById(R.id.ivSportPreview), offres);
                }

            });

            recyclerView.setAdapter(adapter);
            checkItems();
        }
    }

    private static void checkItems() {

        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_not_found.setVisibility(View.GONE);
        }

    }

    private void showProgress(boolean show) {
        if (show) {
            lyt_progress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            lyt_progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}
