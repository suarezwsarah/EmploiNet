package com.mba2dna.apps.EmploiNet.fragment;


import android.database.sqlite.SQLiteAccessPermException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.activities.OffreDetailActivity;
import com.mba2dna.apps.EmploiNet.adapter.OffreLoadMoreAdapter;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.OnLoadMoreListener;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OffresLoadMoreFragment extends Fragment {
    public static final int ITEMS_PER_AD = 20;
    private List<Offre> items;
    private OffreLoadMoreAdapter adapter;
    public static String TAG_TYPE = "com.mba2dna.apps.EmploiNet.type";
    public static String TAG_ID = "com.mba2dna.apps.EmploiNet.id";
    public static String TAG_NAME = "com.mba2dna.apps.EmploiNet.name";
    private static int category_id;
    private static String category_name,type;
    private SharedPref sharedPref;
    private SQLiteHandler db;
    private View view;
    private Integer page = 1;
    private int count_total = 0;

    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;
    public OffresLoadMoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offres_load_more, container, false);
        db = new SQLiteHandler(getActivity());
        sharedPref = new SharedPref(getActivity());
        category_id = getArguments().getInt(TAG_ID);
        category_name = getArguments().getString(TAG_NAME);
        type = getArguments().getString(TAG_TYPE);
        items = new ArrayList<>();
        // activate fragment menu
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
       // text_progress = (TextView) view.findViewById(R.id.text_progress);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        //set data and list adapter
        adapter = new OffreLoadMoreAdapter(getActivity(), recyclerView, new ArrayList<Offre>());
        recyclerView.setAdapter(adapter);

        // on item list clicked
        adapter.setOnItemClickListener(new OffreLoadMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Offre obj) {
              //  OffreDetailActivity.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_content), obj);
            }
        });

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
        startLoadMoreAdapter();
        return view;

    }

    @Override
    public void onDestroyView() {
      /*  if (snackbar_retry != null) snackbar_retry.dismiss();
        if (callback != null && callback.isExecuted()) {
            callback.cancel();
        }*/
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
       /* if (sharedPref.isRefreshPlaces() || db.getPlacesSize() == 0) {
            actionRefresh(sharedPref.getLastPlacePage());
        } else {
            startLoadMoreAdapter();
        }*/
    }
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (item.getItemId() == R.id.action_refresh) {
            sharedPref.setLastPlacePage(1);
            sharedPref.setRefreshPlaces(true);
            text_progress.setText("");
            if (snackbar_retry != null) snackbar_retry.dismiss();
            actionRefresh(sharedPref.getLastPlacePage());
        }
        return super.onOptionsItemSelected(item);*/
        if (item.getItemId() == R.id.action_refresh) {
           // actionRefresh();
        }
        return super.onOptionsItemSelected(item);
    }
    private void startLoadMoreAdapter() {
        adapter.resetListData();
      //  List<Offre> items = db.getAllArticles(category_id, Constant.LIMIT_LOADMORE, 0);
        adapter.insertData( items);
        showNoItemView();
     /*   final int item_count = (int) db.getPlacesSize(category_id);
        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new OffreLoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int current_page) {
                if (item_count > adapter.getItemCount() && current_page != 0) {
                    displayDataByPage(current_page);
                } else {
                    adapter.setLoaded();
                }
            }
        });*/
    }
    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              /*  List<Offre> items = db.getPlacesByPage(category_id, Constant.LIMIT_LOADMORE, (next_page * Constant.LIMIT_LOADMORE));
                adapter.insertData( items);
                showNoItemView();*/
            }
        }, 500);
    }

    // checking some condition before perform refresh data
    private void actionRefresh(int page_no) {
        boolean conn = Tools.cekConnection(getActivity());
        if (conn) {
            if (!onProcess) {
                onRefresh(page_no);
            } else {
               // Snackbar.make(view, R.string.task_running, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            onFailureRetry(page_no, getString(R.string.no_internet));
        }
    }

    private boolean onProcess = false;

    private void onRefresh(final int page_no) {
        items.clear();
        Log.d("MainActivity_", "onLoad");
        ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
            @Override
            public void onSuccess(final ApiClient result) {
                // itemList = result.offres;
                final List<Offre> finalListArticles = result.offres;
                for (Offre o : result.offres) {
                    items.add(o);
                }

                Log.d("MainActivity_", "onLoad:" + result.offres.size());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code

                        db.addListArticles(finalListArticles);
                    }


                });
                // addNativeExpressAds();
                // setUpAndLoadNativeExpressAds();
                adapter.insertData(items);
                adapter.setLoaded();
                adapter.notifyDataSetChanged();
                sharedPref.setRefreshReciepes(false);
                try {
                    Snackbar snackbar = Snackbar.make(view, "Mise à jour réussie", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getActivity(), tv);
                    snackbar.show();
                } catch (Exception e) {
                }
                onProcess = false;
                showProgress(onProcess);

              //  mPullToRefreshView.finishRefreshing();
                showNoItemView();


            }

            @Override
            public void onError(String result) {
                onProcess = false;
                showProgress(onProcess);
                Snackbar snackbar = Snackbar.make(view, "Une erreur s’est produite lors de l’envoi de commandes au serveur", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
              //  mPullToRefreshView.finishRefreshing();
                showNoItemView();
            }
        });
        String var = "";
        if (type.equals("CATEGORY")) {
            if (!category_name.equals("")) var = "&s=" + category_id;
        } else if (type.equals("RECRUTEUR")) {
            if (!category_name.equals("")) var = "&r=" + category_id;
        }
        task.execute("?offres=true&p=1&n=" + Tools.getGridSpanCount(getActivity()) + var);


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

    private void showNoItemView() {
        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    private void onFailureRetry(final int page_no, String msg) {
        onProcess = false;
        showProgress(onProcess);
        showNoItemView();
        startLoadMoreAdapter();
      /*  snackbar_retry = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar_retry.setAction(R.string.RETRY, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionRefresh(page_no);
            }
        });
        snackbar_retry.show();*/
    }

    private void delayNextRequest(final int page_no) {
        if (count_total == 0) {
            onFailureRetry(page_no, getString(R.string.refresh_failed));
            return;
        }
        if ((page_no * Constant.LIMIT_PLACE_REQUEST) > count_total) { // when all data loaded
            onProcess = false;
            showProgress(onProcess);
            startLoadMoreAdapter();
            sharedPref.setRefreshReciepes(false);

          //  Snackbar.make(view, R.string.load_success, Snackbar.LENGTH_LONG).show();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh(page_no + 1);
            }
        }, 500);
    }

}
