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
import android.support.v7.widget.CardView;
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
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.library.beautifulrefreshlibrary.BeautifulRefreshLayout;

import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by BIDA on 11/11/2016.
 */

public class OffresFragment extends Fragment implements AdapterOffres.OnLoadMoreListener {

    // A Native Express ad is placed in every nth position in the RecyclerView.
    public static final int ITEMS_Count = 0;
    // A Native Express ad is placed in every nth position in the RecyclerView.
    public static final int ITEMS_PER_AD = 8;
    // The Native Express ad height.
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 150;
    // The Native Express ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";


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

    private List<Object> itemList = new ArrayList<>();

    private SharedPref sharedPref;
    private static AdapterOffres mAdapter;
    private Integer page = 1;
    private BeautifulRefreshLayout mPullToRefreshView;
    AlertDialog dialog;
    private FirebaseAnalytics firebaseAnalytics;


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

        // itemList = new ArrayList<>();

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
        recyclerView.setHasFixedSize(true);


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
        actionRefresh();
        //  displayDataFromDatabase();
        mAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

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

    private void addNativeExpressAds() {
        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = ITEMS_Count; i <= itemList.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(getContext());
            itemList.add(i, adView);
        }
    }

    /**
     * Sets up and loads the Native Express ads.
     */
    private void setUpAndLoadNativeExpressAds() {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = getActivity().getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                if(itemList.size()>0)
                for (int i = 0; i <= itemList.size(); i += ITEMS_PER_AD) {
                    final NativeExpressAdView adView =
                            (NativeExpressAdView) itemList.get(i);
                    final CardView cardView = (CardView) view.findViewById(R.id.ad_card_view);
                    final int adWidth = cardView.getWidth() - cardView.getPaddingLeft()
                            - cardView.getPaddingRight();
                    AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                    adView.setAdSize(adSize);
                    adView.setAdUnitId(AD_UNIT_ID);
                }

                // Load the first Native Express ad in the items list.
                loadNativeExpressAd(0);
            }
        });
    }

    /**
     * Loads the Native Express ads in the items list.
     */
    private void loadNativeExpressAd(final int index) {

        if (index >= itemList.size()) {
            return;
        }

        Object item = itemList.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        // Load the Native Express ad.
        adView.loadAd(new AdRequest.Builder().build());
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
            if (!category_name.equals("")) {

                for (Offre o : db.getCategoryArticles(category_name)) {
                    itemList.add(o);
                }
                mAdapter.addAll(itemList);
            } else {

                for (Offre o : db.getAllArticles()) {
                    itemList.add(o);
                }
                mAdapter.addAll(itemList);
            }
        } else {

            for (Offre o : db.getAllArticles()) {
                itemList.add(o);
            }
            mAdapter.addAll(itemList);
        }
        mAdapter.setMoreLoading(false);
        sharedPref.setRefreshReciepes(false);
        onProcess = false;
        showProgress(onProcess);
        mPullToRefreshView.finishRefreshing();
    }

    private boolean onProcess = false;

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
                            // itemList = result.offres;
                            final List<Offre> finalListArticles = result.offres;

                            for (Offre o : result.offres) {
                                itemList.add(o);
                            }

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
                            addNativeExpressAds();
                            setUpAndLoadNativeExpressAds();
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
                        if (!category_name.equals("")) var = "&s=" + category_id;
                    } else if (type.equals("RECRUTEUR")) {
                        if (!category_name.equals("")) var = "&r=" + category_id;
                    }
                    task.execute("?offres=true&p=" + page + "&n=" + Tools.getGridSpanCount(getActivity()) + var);
                    mAdapter.setProgressMore(false);


                }
            }, 2000);
        }
    }

    private void loadData() {
        itemList.clear();
        Log.d("MainActivity_", "onLoad");
        ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
            @Override
            public void onSuccess(final ApiClient result) {
                // itemList = result.offres;
                final List<Offre> finalListArticles = result.offres;
                for (Offre o : result.offres) {
                    itemList.add(o);
                }

                Log.d("MainActivity_", "onLoad:" + result.offres.size());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code

                        db.addListArticles(finalListArticles);
                    }


                });
                addNativeExpressAds();
                setUpAndLoadNativeExpressAds();
                mAdapter.addAll(itemList);
                mAdapter.setMoreLoading(false);
                //mAdapter.notifyDataSetChanged();
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
            if (!category_name.equals("")) var = "&s=" + category_id;
        } else if (type.equals("RECRUTEUR")) {
            if (!category_name.equals("")) var = "&r=" + category_id;
        }
        task.execute("?offres=true&p=1&n=" + Tools.getGridSpanCount(getActivity()) + var);

    }


}
