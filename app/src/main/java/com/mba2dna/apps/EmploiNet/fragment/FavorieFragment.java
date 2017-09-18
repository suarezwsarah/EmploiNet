package com.mba2dna.apps.EmploiNet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.activities.OffreDetailActivity;
import com.mba2dna.apps.EmploiNet.adapter.AdapterFavoriteList;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FavorieFragment extends Fragment {
    public static String TAG_CATEGORY = "com.mba2dna.apps.EmploiNet.tagCategory";
    private static int category_id;

    private View view;
    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;
    private static AdapterFavoriteList adapter;

    private SQLiteHandler db;

    private List<Offre> items = new ArrayList<>();
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favories, null);

        // activate fragment menu
        setHasOptionsMenu(true);

        db = new SQLiteHandler(getActivity());
        sharedPref = new SharedPref(getActivity());
        category_id = getArguments().getInt(TAG_CATEGORY);

        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView v, int state) {
                super.onScrollStateChanged(v, state);
                if (state == v.SCROLL_STATE_DRAGGING || state == v.SCROLL_STATE_SETTLING) {
                    ActivityMain.animateFab(true);
                } else {
                    ActivityMain.animateFab(false);
                }
            }
        });
        items = db.getAllFavorites();

        adapter = new AdapterFavoriteList(getActivity(), items);
        adapter.setOnItemClickListener(new AdapterFavoriteList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Offre p) {
                OffreDetailActivity.navigate((ActivityMain) getActivity(), v.findViewById(R.id.image), p);
            }
        });
        recyclerView.setAdapter(adapter);
        checkItems();

        return view;
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

    public static void filterAdapter(String keyword) {
        adapter.getFilter().filter(keyword);
        checkItems();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            actionRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionRefresh() {

            if (!onProcess) {
                onRefresh();
            } else {
                // Snackbar.make(view, "Task still running", Snackbar.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(view, "التحديث مازال قائما", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
            }

    }

    private boolean onProcess = false;

    private void onRefresh() {
        Snackbar snackbar = Snackbar.make(view, "تحديث البيانات...", Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        CommonUtils.setRobotoThinFont(getActivity(), tv);
        snackbar.show();
        onProcess = true;
        showProgress(onProcess);
        items = db.getAllFavorites();

        adapter = new AdapterFavoriteList(getActivity(), items);
        adapter.setOnItemClickListener(new AdapterFavoriteList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Offre p) {
                OffreDetailActivity.navigate((ActivityMain) getActivity(), v.findViewById(R.id.image), p);
            }
        });
        recyclerView.setAdapter(adapter);
        checkItems();
        sharedPref.setRefreshReciepes(false);
        Snackbar snackbar1 = Snackbar.make(view, "Mise à jour réussie", Snackbar.LENGTH_SHORT);
        View sbView1 = snackbar1.getView();
        sbView1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        TextView tv1 = (TextView) (snackbar1.getView()).findViewById(android.support.design.R.id.snackbar_text);
        CommonUtils.setRobotoThinFont(getActivity(), tv1);
        snackbar1.show();
        onProcess = false;
        showProgress(onProcess);

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

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sharedPref.isRefreshReciepes() || db.getReciepesSize() == 0) {
            actionRefresh();
        }
    }
}
