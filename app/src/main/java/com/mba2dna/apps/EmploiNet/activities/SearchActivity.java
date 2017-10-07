package com.mba2dna.apps.EmploiNet.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.adapter.AdapterOffres;
import com.mba2dna.apps.EmploiNet.loader.ApiSearchLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity implements AdapterOffres.OnLoadMoreListener{
    private FloatingActionButton fab;
    private SearchView searchView;
    private MenuItem searchItem;
    private ImageLoader imgloader = ImageLoader.getInstance();

    public ActionBar actionBar;
    public Toolbar toolbar;
    public static TextView mTitle;
    private View view;
    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;
    private List<Offre> itemList = new ArrayList<>();
    private static AdapterOffres mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_search);
        view =findViewById(R.id.root_view);
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleSearchView(searchItem.isVisible());
            }
        });
        initToolbar();
        actionBar.setTitle("");
        mTitle.setText("Rechercher des offres");
        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, Tools.getGridSpanCount(this));
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemViewType(position) == 0 ? Tools.getGridSpanCount(SearchActivity.this) : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new AdapterOffres(getBaseContext(), itemList, this);
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
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,p.title);
                //firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                OffreDetailActivity.navigate(SearchActivity.this, view.findViewById(R.id.image), p);
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
    }
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        CommonUtils.setRobotoThinFont(this, mTitle);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setActionBarColor(this, actionBar);
    }
    @Override
    protected void onStart() {
        super.onStart();
       //
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
    private void actionRefresh(String s) {
        boolean conn = Tools.cekConnection(getApplicationContext());
        if (conn) {
            if (!onProcess) {
                onProcess = true;
                Log.d("onLoad", "actionRefresh1:"+s);
                showProgress(onProcess);
                Log.d("onLoad", "actionRefresh2:"+s);
                loadData(s);
            } else {
              /*  Snackbar snackbar = Snackbar.make(view, "التحديث مازال قائما", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(this, tv);
                snackbar.show();*/
            }
        } else {
           // getDB();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void loadData(final String s) {
        itemList.clear();
        Log.d("onLoad", "Search:"+s);
        ApiSearchLoader task = new ApiSearchLoader(s,new Callback<ApiClient>() {
            @Override
            public void onSuccess(ApiClient result) {
                itemList.clear();
                itemList = result.offres;
                mAdapter.addAll(result.offres);
                mAdapter.setMoreLoading(false);
                //mAdapter.notifyDataSetChanged();
                onProcess = false;
                showProgress(onProcess);
                checkItems();
            }
            @Override
            public void onError(String result) {
                itemList.clear();
                mAdapter.addAll(itemList);
                mAdapter.setMoreLoading(false);
                onProcess = false;
                showProgress(onProcess);
                Snackbar snackbar = Snackbar.make(view,  "Aucune offre " + s, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getApplication(), tv);
                snackbar.show();
                checkItems();
            }
        });
        task.execute(s);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(false);
        TextView searchText = (TextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        CommonUtils.setRobotoThinFont(this, searchText);
        searchView.setQueryHint(getString(R.string.search_toolbar_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                        actionRefresh(s);
                } catch (Exception e) {
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                try {

                    if(s.length()>=4){

                     actionRefresh(s);
                    }
                } catch (Exception e) {
                }
                return true;
            }
        });
        searchView.onActionViewCollapsed();
        searchItem.setVisible(false);
        toogleSearchView(false);
        checkItems();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
            startActivity(i);
        } else if (id == R.id.action_rate) {
            Tools.rateAction(SearchActivity.this);
        } else if (id == R.id.action_about) {
            Tools.aboutAction(SearchActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
    private void toogleSearchView(boolean open) {
        if (open) {
            searchItem.setVisible(false);
            searchView.onActionViewCollapsed();
            fab.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);
            hideKeyboard();
        } else {
            searchItem.setVisible(true);
            searchView.onActionViewExpanded();
            fab.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
        }
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
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onResume() {
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            Tools.systemBarLolipop(this);
        }
        super.onResume();
    }

    @Override
    public void onLoadMore() {

    }
}
