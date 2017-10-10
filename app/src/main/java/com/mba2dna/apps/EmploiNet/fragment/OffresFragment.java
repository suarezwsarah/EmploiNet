package com.mba2dna.apps.EmploiNet.fragment;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteAccessPermException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.activities.OffreDetailActivity;
import com.mba2dna.apps.EmploiNet.adapter.AdapterOffres;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.library.beautifulrefreshlibrary.BeautifulRefreshLayout;

import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.model.UserSession;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;


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
 * Created by BIDA on 11/11/2016.
 */

public class OffresFragment extends Fragment implements AdapterOffres.OnLoadMoreListener {
    public static String TAG_TYPE = "com.mba2dna.apps.EmploiNet.type";
    public static String TAG_ID = "com.mba2dna.apps.EmploiNet.id";
    public static String TAG_NAME = "com.mba2dna.apps.EmploiNet.name";
    private static int category_id;
    private static String category_name, type;

    private View view;
    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;

    private SQLiteHandler db;

    private List<Offre> itemList = new ArrayList<>();

    private SharedPref sharedPref;
    private static AdapterOffres mAdapter;
    private Integer page = 1;
    private BeautifulRefreshLayout mPullToRefreshView;
    AlertDialog dialog;
    private FirebaseAnalytics firebaseAnalytics;
    private Offre feature;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reciepes, null);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        // activate fragment menu
        setHasOptionsMenu(true);
        db = new SQLiteHandler(getActivity());
        sharedPref = new SharedPref(getActivity());
        category_id = getArguments().getInt(TAG_ID);
        category_name = getArguments().getString(TAG_NAME);
        type = getArguments().getString(TAG_TYPE);
        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);

        itemList = new ArrayList<>();


        try {
            mPullToRefreshView = (BeautifulRefreshLayout) view.findViewById(R.id.pull_to_refresh);
            mPullToRefreshView.setBuautifulRefreshListener(new BeautifulRefreshLayout.BuautifulRefreshListener() {
                @Override
                public void onRefresh(final BeautifulRefreshLayout refreshLayout) {
                    actionRefresh();
               /* refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                }, 5000);*/
                }
            });
        } catch (Exception e) {

        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemViewType(position) == 0 ? 1 : 1;

            }
        });
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new AdapterOffres(getContext(), itemList, this);
        mAdapter.setLinearLayoutManager(layoutManager);
        mAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mAdapter.setOnItemClickListener(new AdapterOffres.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Offre p) {
                Bundle bundle = new Bundle();
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, p.id);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, p.title);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                OffreDetailActivity.navigate((ActivityMain) getActivity(), view.findViewById(R.id.image), p);
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
      //  feature = LoadFeature();
        actionRefresh();


        //  displayDataFromDatabase();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, " الدخول الى كل الوصفات");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private static void checkItems() {

        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_not_found.setVisibility(View.GONE);
        }

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
        boolean conn = Tools.cekConnection(getActivity().getApplicationContext(), view);
        if (conn) {
            if (!onProcess) {
                onProcess = true;
                showProgress(onProcess);
                loadData();
            } else {
                Snackbar snackbar = Snackbar.make(view, "التحديث مازال قائما", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
            }
        } else {
            getDB();
        }
    }

    private void getDB() {
        onProcess = true;
        showProgress(onProcess);
        if (type.equals("CATEGORY")) {
            if (!category_name.equals("")) mAdapter.addAll(db.getCategoryArticles(category_name));
            else mAdapter.addAll(db.getAllArticles());
        } else {
            mAdapter.addAll(db.getAllArticles());
        }
        mAdapter.setMoreLoading(false);
        sharedPref.setRefreshReciepes(false);
        onProcess = false;
        showProgress(onProcess);
        mPullToRefreshView.finishRefreshing();
    }

    private boolean onProcess = false;

    private void showProgress(boolean show) {
      /*  if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }*/
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
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onLoadMore() {
        Log.d("MainActivity_", "onLoadMore");
        boolean conn = Tools.cekConnection(getActivity().getApplicationContext(), view);
        if (conn) {
            mAdapter.setProgressMore(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    itemList.clear();
                    page++;
                    ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
                        @Override
                        public void onSuccess(ApiClient result) {
                            itemList = result.offres;
                            final List<Offre> finalListArticles = result.offres;

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO your background code
                                    try {
                                        db.addListArticles(finalListArticles);
                                    } catch (SQLiteAccessPermException e) {

                                    }

                                }


                            });

                            mAdapter.addItemMore(itemList);
                            mAdapter.setMoreLoading(false);
                            sharedPref.setRefreshReciepes(false);
                            onProcess = false;
                            showProgress(onProcess);
                            checkItems();
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
                            checkItems();
                        }
                    });
                    String var = "";
                    if (type.equals("CATEGORY")) {
                        if (!category_name.equals("")) var = "&c=" + category_id;
                    } else if (type.equals("RECRUTEUR")) {
                        if (!category_name.equals("")) var = "&r=" + category_id;
                    }
                    task.execute("?offres=true&p=" + page + "&n=" + Tools.getGridSpanCount(getActivity()) + var);
                    mAdapter.setProgressMore(false);


                }
            }, 2000);
        }
    }

    private Offre LoadFeature() {
        String URL = Constant.getURLApiClientData();
        OkHttpClient client = new OkHttpClient();
        Offre o = new Offre();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
        urlBuilder.addQueryParameter("alaune", "true");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.e("RESPENSE", url);
        try {
            Response response = client.newCall(request).execute();
            String S = response.body().string();
            Log.e("RESPENSE", S);
            JSONObject jObject = new JSONObject(S);

            JSONArray data = jObject.getJSONArray("alaune"); // get data object
            final JSONObject object = data.getJSONObject(0);
            try {

                o.setId(object.getInt("rec_id"));
                o.setTitle(object.getString("recruteur"));
                o.setType_activite(object.getString("type_activite"));
                o.setPostes(object.getString("num"));
                o.setPhoto(object.getString("photo"));
                // itemList.add(0,o);

            } catch (JSONException e) {

                e.printStackTrace();
            }


        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();

        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();

        }
        return o;
    }

    private void loadData() {
        itemList.clear();

        Log.d("MainActivity_", "onLoad");
        ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
            @Override
            public void onSuccess(ApiClient result) {
                itemList = result.offres;
                Log.d("MainActivity_", "onLoad:" + result.offres.size());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code
                        List<Offre> finalListArticles = itemList;
                        db.addListArticles(finalListArticles);
                    }


                });
               // itemList.add(0, feature);
                mAdapter.addAll(itemList);
                mAdapter.setMoreLoading(false);
                mAdapter.notifyDataSetChanged();
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

                mPullToRefreshView.finishRefreshing();
                checkItems();


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
                mPullToRefreshView.finishRefreshing();
                checkItems();
            }
        });
        String var = "";
        if (type.equals("CATEGORY")) {
            if (!category_name.equals("")) var = "&c=" + category_id;
        } else if (type.equals("RECRUTEUR")) {
            if (!category_name.equals("")) var = "&r=" + category_id;
        }
        task.execute("?offres=true&p=1&n=" + Tools.getGridSpanCount(getActivity()) + var);

    }


}
