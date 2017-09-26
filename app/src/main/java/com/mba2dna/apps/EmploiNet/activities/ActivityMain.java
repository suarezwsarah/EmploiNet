package com.mba2dna.apps.EmploiNet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.fragment.CategoryFragment;
import com.mba2dna.apps.EmploiNet.fragment.FavorieFragment;
import com.mba2dna.apps.EmploiNet.fragment.RecruteurFragment;
import com.mba2dna.apps.EmploiNet.fragment.InfosEmploiFragment;
import com.mba2dna.apps.EmploiNet.fragment.CVsFragment;
import com.mba2dna.apps.EmploiNet.fragment.AllArticlesFragment;
import com.mba2dna.apps.EmploiNet.model.UserSession;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.CustomTypefaceSpan;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.mba2dna.apps.EmploiNet.widget.ViewDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityMain extends AppCompatActivity {
    //for ads
    // private InterstitialAd mInterstitialAd;
    public static String NAME_CATEGORY = "com.mba2dna.apps.EmploiNet.nameCategory";
    private ImageLoader imgloader = ImageLoader.getInstance();
    InterstitialAd mInterstitialAd;

    public ActionBar actionBar;
    public Toolbar toolbar;
    private int cat[];
    private FloatingActionButton fab;
    private SearchView searchView;
    private MenuItem searchItem;
    private NavigationView navigationView;
    private SQLiteHandler db;
    private SharedPref sharedPref;
    private RelativeLayout nav_header_lyt;
    public static TextView mTitle;
    private BottomSheetBehavior mBottomSheetBehavior;
private TextView headNom,headEmail;
    static ActivityMain activityMain;
    private View root;
    private Button LoginBon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityMain = this;


        if (!imgloader.isInited()) Tools.initImageLoader(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new SQLiteHandler(this);
        sharedPref = new SharedPref(this);


        prepareAds();
        initToolbar();
        initDrawerMenu();
        prepareImageLoader();

        cat = getResources().getIntArray(R.array.id_category);
        // Log.v("LENGH",cat.length+"");
        // first drawer view
        actionBar.setTitle("");
        mTitle.setText(getString(R.string.title_nav_all));
        onItemSelected(R.id.nav_all);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //toogleSearchView(searchItem.isVisible());
                Intent intent = new Intent(ActivityMain.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        Tools.cekConnection(getApplicationContext(), ((View) findViewById(R.id.frame_content)));
        if (Tools.cekConnection(this) == false) {
            final BottomSheetDialog BottomDialog = new BottomSheetDialog(this);
            View parentview = getLayoutInflater().inflate(R.layout.dialog_no_connexion, null);
            BottomDialog.setContentView(parentview);
            BottomSheetBehavior BottomBehavior = BottomSheetBehavior.from((View) parentview.getParent());
            BottomBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
            BottomDialog.show();
            TextView t = (TextView) parentview.findViewById(R.id.infonoconnexion);
            CommonUtils.setRobotoThinFont(this, t);
            Button b = (Button) parentview.findViewById(R.id.Okbtn);
            CommonUtils.setRobotoThinFont(this, b);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomDialog.dismiss();
                }
            });


        }
        // for system bar in lollipop
        Tools.systemBarLolipop(this);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public static void setTitleToolbar(String name) {


        TextView t = ((TextView) getInstance().findViewById(R.id.toolbar_title));
        t.setText(name);
        CommonUtils.setRobotoThinFont(getInstance(), t);


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

    private void initDrawerMenu() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                updateUser();
                updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
                toogleSearchView(true);
                showInterstitial();

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                actionBar.setTitle("");
                mTitle.setText(item.getTitle().toString());
                return onItemSelected(item.getItemId());
            }
        });

        // navigation header
        View nav_header = navigationView.getHeaderView(0);
        nav_header_lyt = (RelativeLayout) nav_header.findViewById(R.id.nav_header_lyt);
        nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
        (nav_header.findViewById(R.id.menu_nav_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
                startActivity(i);
            }
        });

        (nav_header.findViewById(R.id.menu_nav_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent i = new Intent(getApplicationContext(), ActivityMaps.class);
                startActivity(i);*/
            }
        });
        headNom=(TextView) nav_header.findViewById(R.id.headNom);
        headEmail=(TextView) nav_header.findViewById(R.id.headEmail);
        LoginBon=(Button) nav_header.findViewById(R.id.LoginBon);
        LoginBon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=LoginBon.getText().toString();
                if(db.isUserExist()){
                    db.DeleteUser();
                    updateUser();

                }else{
                    Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "nexalight.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUser();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    //  AllArticlesFragment.filterAdapter(s);
                } catch (Exception e) {
                }
                return true;
            }
        });
        searchView.onActionViewCollapsed();
        searchItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
            startActivity(i);
        } else if (id == R.id.action_rate) {
            Tools.rateAction(ActivityMain.this);
        } else if (id == R.id.action_about) {
            Tools.aboutAction(ActivityMain.this);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onItemSelected(int id) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (id) {
            //sub menu
            case R.id.nav_all:
                fragment = new AllArticlesFragment();
                bundle.putString(AllArticlesFragment.TAG_TYPE, "ALL");
                bundle.putInt(AllArticlesFragment.TAG_ID, -1);
                bundle.putString(AllArticlesFragment.TAG_NAME, "");
                break;
            // favorites
            case R.id.nav_favorites:

                fragment = new FavorieFragment();
                bundle.putInt(FavorieFragment.TAG_CATEGORY, -2);
                break;
            case R.id.nav_cvs:
                fragment = new CVsFragment();
                break;
            case R.id.nav_category:
                fragment = new CategoryFragment();
                bundle.putInt(CategoryFragment.TAG_CATEGORY, cat[1]);
                break;
            case R.id.nav_period:
                fragment = new InfosEmploiFragment();
                break;
            case R.id.nav_fetrnit:
                fragment = new RecruteurFragment();
                break;

            case R.id.nav_apps:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mba2dna.apps.AtyebTabkha" )));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mba2dna.apps.AtyebTabkha" )));
                }
                break;
            default:
                break;
        }


        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragment);
            fragmentTransaction.commit();
            //initToolbar();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(this, "Voulez-vous quitter l'application?");
      /*  AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_permission));
        builder.setMessage(getString(R.string.dialog_content_permission));
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();*/
       /* if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }*/
    }

    private void prepareImageLoader() {
        Tools.initImageLoader(this);
    }


    @Override
    protected void onResume() {
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        updateUser();
        updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            // for system bar in lollipop
            Tools.systemBarLolipop(this);
        }
        if (nav_header_lyt != null) {
            nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
        }
        super.onResume();
    }


    private void updateFavoritesCounter(NavigationView nav, @IdRes int itemId, int count) {
        TextView view = (TextView) nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(String.valueOf(count));
    }

    public void updateUser() {
        if(db.isUserExist()){
            UserSession u =db.getUser();
            headNom.setText(u.fullName);
            headEmail.setText(u.Email);
            LoginBon.setText("Deconnectez-vous");
        }else{
            LoginBon.setText("Connectez-vous");
            headNom.setText("EmploiNet");
            headEmail.setText("1er site de l’Emploi en Algerie");
        }

    }
    private void prepareAds() {
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        //prepare ads
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest2);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e("ADS","Ad failed: " + i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e("ADS","Ad Loaded: " );
            }
        });
    }

    /**
     * show ads
     */
    public void showInterstitial() {
        // Show the ad if it's ready
        if (AppConfig.ENABLE_ADSENSE && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            Log.e("ADS","mInterstitialAd.show()");
            mInterstitialAd.show();
        }
    }


    public static ActivityMain getInstance() {
        return activityMain;
    }

    public static void animateFab(final boolean hide) {
        FloatingActionButton f_ab = (FloatingActionButton) activityMain.findViewById(R.id.fab);
        int moveY = hide ? (2 * f_ab.getHeight()) : 0;
        f_ab.animate().translationY(moveY).setStartDelay(100).setDuration(400).start();
    }
}
