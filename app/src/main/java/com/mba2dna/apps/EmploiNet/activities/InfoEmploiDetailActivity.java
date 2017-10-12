package com.mba2dna.apps.EmploiNet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.iamhabib.easyads.EasyAds;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.loader.ApiSinglePlaceLoader;
import com.mba2dna.apps.EmploiNet.model.InfoEmploi;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InfoEmploiDetailActivity extends AppCompatActivity {
    private static final String EXTRA_OBJ = "com.mba2dna.apps.EmploiNet.EXTRA_OBJ";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, InfoEmploi p) {
        Intent intent = new Intent(activity, InfoEmploiDetailActivity.class);
        intent.putExtra(EXTRA_OBJ, p);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_OBJ);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private InfoEmploi info = null;
    private ImageLoader imgloader = ImageLoader.getInstance();
    private FloatingActionButton fab;
    private View parent_view;
    private SQLiteHandler db;

    private ApiSinglePlaceLoader task_loader = null;

    private WebView paragraph2;


    Bundle bundle = null;
    private int id;
    String URL = Constant.getURLApiClientData();


    private void callMainActivty() {
        Intent intent = new Intent(this, ActivityMain.class);
        //  intent.putExtra(EXTRA_OBJ, offres);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_emploi_detail);
        parent_view = findViewById(android.R.id.content);
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        db = new SQLiteHandler(this);
        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJ);
        Intent mIntent = getIntent();
        id = mIntent.getIntExtra("id", 0);

        info = (InfoEmploi) getIntent().getSerializableExtra(EXTRA_OBJ);
        if (Tools.cekConnection(this)) {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("addviewid", info.id + "")
                                .build();
                        Request request = new Request.Builder()
                                .url(URL + "?addviewid=" + info.id)
                                .post(formBody)
                                .build();

                        try {
                            client.newCall(request).execute();

                            // Do something with the response.
                        } catch (IOException e) {
                            // e.printStackTrace();
                            callMainActivty();
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        callMainActivty();
                    }

                }
            });
            thread.start();

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if (info.image != null)
            imgloader.displayImage(info.image, (ImageView) findViewById(R.id.image));
        else {
            String uri = "@drawable/detail_bg";  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());

            ImageView imageView = (ImageView) findViewById(R.id.image);
            Drawable res = getResources().getDrawable(imageResource);
            imageView.setImageDrawable(res);
        }


        prepareAds();
        fabToggle();
        setupToolbar(info.title);

        // initMap();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.isFavoritesExist(info.id)) {
                   // db.deleteFavorites(info.id);
                    Snackbar snackbar = Snackbar.make(view, info.title + " " + getString(R.string.remove_favorite), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                    snackbar.show();
                } else {
                //    db.addFavorites(info.id);
                   // db.addArticle(info);
                    Snackbar snackbar = Snackbar.make(view, info.title + " " + getString(R.string.add_favorite), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                    snackbar.show();
                    // analytics tracking
//                    ThisApplication.getInstance().trackEvent(Constant.Event.FAVORITES.name(), "ADD", offres.title);
                }
                fabToggle();
            }
        });

        paragraph2 = (WebView) findViewById(R.id.paragraph1);
        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/nexalight.ttf\")}body {font-family: MyFont;font-size: 16px;text-align: left;line-height: 120%;} a{color:#d78401} ul {list-style-type: none;} img{width:90%}</style></head><body>";
        String pas = "</body></html>";
        //  Log.e("HTML :", offres.description);
        //  Integer start= offres.description.indexOf(" <figure>");
        //  Integer end= offres.description.indexOf("</figure>");
        String html = info.text;//.substring(end+9)
        //    Log.e("HTML :",html);
        paragraph2.loadDataWithBaseURL(null, pish + html + pas, "text/html", "UTF-8", null);
        Tools.systemBarLolipop(this);
    }





    private void fabToggle() {
        try {
            fab.setImageResource(R.drawable.ic_nav_favorites_outline);
            if (db.isFavoritesExist(info.id)) {
                fab.setImageResource(R.drawable.ic_action_share);
            } else {
                fab.setImageResource(R.drawable.ic_action_share);
            }
        } catch (Exception e) {

        }

    }

    private void prepareAds() {
        if (AppConfig.ENABLE_ADSENSE && Tools.cekConnection(this)) {

            EasyAds.forBanner(this)
                    .with((AdView)findViewById(R.id.ad_view))
                    .show();
        } else {
            ((RelativeLayout) findViewById(R.id.banner_layout)).setVisibility(View.GONE);
        }
    }

    private void setupToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        TextView t = ((TextView) findViewById(R.id.toolbar_title));
        t.setText(name);
        CommonUtils.setRobotoThinFont(this, t);

        final CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setContentScrimColor(new SharedPref(this).getThemeColorInt());
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsing_toolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsing_toolbar)) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.action_share) {

        } /*else if (id == R.id.action_shop) {
            if (!offres.isDraft()) {

                Snackbar snackbar = Snackbar.make(parent_view, "قائمة المشتريات", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                snackbar.show();


                FragmentManager fm = getSupportFragmentManager();
                ShopFragment screen = ShopFragment.newInstance(offres);
                screen.show(fm, "ShoppingList");

                // Tools.methodShare(OffreDetailActivity.this, offres);
            }
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
      /*  if (googleMap == null) {
            MapFragment mapFragment1 =(MapFragment) getFragmentManager().findFragmentById(R.id.mapPlaces);
            mapFragment1.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    googleMap = gMap;
                    if (googleMap == null) {
                        Snackbar.make(parent_view, "Sorry! unable to create maps", Snackbar.LENGTH_SHORT).show();
                    }else {
                        // config map
                        googleMap = Tools.configStaticMap(ActivityPlaceDetail.this, googleMap, place);
                    }
                }
            });
        }

        ((Button) findViewById(R.id.bt_navigate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"OPEN", Toast.LENGTH_LONG).show();
                Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + place.lat + "," + place.lng));
                startActivity(navigation);
            }
        });
        ((Button) findViewById(R.id.bt_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });
        ((LinearLayout) findViewById(R.id.map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ActivityMain.class);
        //  intent.putExtra(EXTRA_OBJ, offres);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openPlaceInMap() {
       /* Intent intent = new Intent(ActivityPlaceDetail.this, ActivityMaps.class);
        intent.putExtra(ActivityMaps.EXTRA_OBJ, place);
        startActivity(intent);*/
    }


    @Override
    protected void onResume() {
        if (!imgloader.isInited()) Tools.initImageLoader(getApplicationContext());
        loadReciepeData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (task_loader != null) task_loader.cancel(true);
        super.onDestroy();
    }

    // places description load with lazy scheme
    private void loadReciepeData() {
//        offres = db.getPlace(offres.id);
//        lyt_no_internet.setVisibility(View.GONE);
       /* if(offres.isDraft()){
            if(Tools.cekConnection(this)){
                requestDetailsPlace(offres.id);
            }else{
                lyt_no_internet.setVisibility(View.VISIBLE);
            }
        }else{
            displayData(offres);
        }*/
    }




}
