package com.mba2dna.apps.EmploiNet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.adapter.AdapterSuggestion;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.fragment.AllArticlesFragment;
import com.mba2dna.apps.EmploiNet.loader.ApiSinglePlaceLoader;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.mba2dna.apps.EmploiNet.widget.TagLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class OffreDetailActivity extends AppCompatActivity {
    private static final String EXTRA_OBJ = "com.mba2dna.apps.EmploiNet.EXTRA_OBJ";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Offre p) {
        Intent intent = new Intent(activity, OffreDetailActivity.class);
        intent.putExtra(EXTRA_OBJ, p);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_OBJ);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Offre offres = null;
    private ImageLoader imgloader = ImageLoader.getInstance();
    private FloatingActionButton fab;
    private View parent_view = null;
    // private GoogleMap googleMap;
    private SQLiteHandler db;

    private boolean onProcess = false;
    private ApiSinglePlaceLoader task_loader = null;
    private View lyt_progress;
    private View lyt_no_internet;
    private LinearLayout mIngredients;
    private LinearLayout mHowToPrepare;
    //  private View mIngredientsTab;

    // private View mRecipeTab;
    private TextView articletitle;
    private TextView username, paragraph;
    private TextView published;
    private Button BtnMore,Postuler;
    public RoundedImageView userpic;
    private WebView paragraph2;


    Fragment fragment = null;
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
        setContentView(R.layout.activity_offre_detail);
        parent_view = findViewById(android.R.id.content);
        userpic = (RoundedImageView) findViewById(R.id.profilePic);
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        db = new SQLiteHandler(this);
        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJ);
        Intent mIntent = getIntent();
        id = mIntent.getIntExtra("id", 0);

        offres = (Offre) getIntent().getSerializableExtra(EXTRA_OBJ);
        if (Tools.cekConnection(this)) {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("addviewid", offres.id + "")
                                .build();
                        Request request = new Request.Builder()
                                .url(URL + "?addviewid=" + offres.id)
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
        // lyt_no_internet = findViewById(R.id.lyt_no_internet);
        // lyt_progress = findViewById(R.id.lyt_progress);
        //  lyt_distance = findViewById(R.id.lyt_distance);
        if (offres.photo != null)
            imgloader.displayImage(offres.photo, (ImageView) findViewById(R.id.image));
        else {
            String uri = "@drawable/detail_bg";  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());

            ImageView imageView = (ImageView) findViewById(R.id.image);
            Drawable res = getResources().getDrawable(imageResource);
            imageView.setImageDrawable(res);
        }


        prepareAds();
        fabToggle();
        setupToolbar(offres.title);
        displayData(offres);
        // initMap();
        TagLayout tagLayout = (TagLayout) findViewById(R.id.tagLayout);
        LayoutInflater layoutInflater = getLayoutInflater();
        String tags[] = offres.fonction.split("#");
        List<String> tagss = new ArrayList<String>();
        for (String tag : tags
                ) {
            if (!tag.equals("")) {
                View tagView = layoutInflater.inflate(R.layout.item_tags, null, false);

                TextView tagTextView = (TextView) tagView.findViewById(R.id.tagTextView);
                tagTextView.setText("#" + tag.replace("\n", ""));
                CommonUtils.setRobotoThinFont(this, tagTextView);
                tagLayout.addView(tagView);
                tagss.add("#" + tag.replace("\n", ""));
            }
        }
       /* TagContainerLayout mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tagcontainerLayout);
        Typeface font = Typeface.createFromAsset(getAssets(), "nexalight.ttf");
        mTagContainerLayout.setTagTypeface("nexalight.ttf");
        mTagContainerLayout.setGravity(Gravity.RIGHT);
        mTagContainerLayout.setTags(tagss);*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.isFavoritesExist(offres.id)) {
                    db.deleteFavorites(offres.id);
                    Snackbar snackbar = Snackbar.make(view, offres.title + " " + getString(R.string.remove_favorite), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                    snackbar.show();
                    //Snackbar.make(parent_view, offres.title +" "+ getString(R.string.remove_favorite), Snackbar.LENGTH_SHORT).show();
                    // analytics tracking
                    //    ThisApplication.getInstance().trackEvent(Constant.Event.FAVORITES.name(), "REMOVE", offres.title);
                } else {
                    db.addFavorites(offres.id);
                    db.addArticle(offres);
                    Snackbar snackbar = Snackbar.make(view, offres.title + " " + getString(R.string.add_favorite), Snackbar.LENGTH_LONG);
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
        imgloader.displayImage(Constant.getURLimgUser(offres.email_candidature), userpic, Tools.getGridOption());
        paragraph = (TextView) findViewById(
                R.id.paragraph);
        paragraph.setText(Html.fromHtml("" + offres.description));
        paragraph2 = (WebView) findViewById(R.id.paragraph2);
        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/nexalight.ttf\")}body {font-family: MyFont;font-size: 16px;text-align: left;line-height: 120%;} a{color:#d78401} ul {list-style-type: none;} img{width:90%}</style></head><body>";
        String pas = "</body></html>";
        //  Log.e("HTML :", offres.description);
        //  Integer start= offres.description.indexOf(" <figure>");
        //  Integer end= offres.description.indexOf("</figure>");
        String html = offres.description;//.substring(end+9)
        //    Log.e("HTML :",html);
        paragraph2.loadDataWithBaseURL(null, pish + html + pas, "text/html", "UTF-8", null);
        CommonUtils.setRobotoThinFont(getBaseContext(), paragraph);
        username = (TextView) findViewById(
                R.id.username);
        username.setText(offres.contact_info);
        CommonUtils.setRobotoBoldFont(getBaseContext(), username);

        published = (TextView) findViewById(
                R.id.timestamp);
        published.setText(offres.pub_date);
        CommonUtils.setRobotoBoldFont(getBaseContext(), published);

        //Caption
        TextView caption = (TextView) findViewById(R.id.captionTxt);
        CommonUtils.setRobotoBoldFont(getBaseContext(), caption);
        caption = (TextView) findViewById(R.id.captionTxt1);
        CommonUtils.setRobotoBoldFont(getBaseContext(), caption);

String da ="";
        if( offres.salaire.equals("0.00"))
            da ="Non disponible"; else da =offres.salaire+" DA";
        paragraph2 = (WebView) findViewById(R.id.paragraph1);
        pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/nexalight.ttf\")}body {font-family: MyFont;font-size: 16px ;text-align: left;line-height: 120%;} a{color:#d78401} ul {list-style-type: none;} img{width:90%}</style></head><body><table width=\"100%\">";
        pish += "  <tr><td><b>Reference :</b></td><td>" + offres.reference + "</td>";
        pish += "  <tr><td><b>Entreprise :</b></td><td>" + offres.contact_info + "</td>";
        pish += "  <tr><td><b>Secteur :</b></td><td>" + offres.type_activite + "</td>";
        pish += "  <tr><td><b>Fonction :</b></td><td>" + offres.fonction + "</td>";
        pish += "  <tr><td><b>Localisation :</b></td><td>" + offres.willaya + "</td>";
        pish += "  <tr><td><b>Postes ouverts :</b></td><td>" + offres.postes + "</td>";
        pish += "  <tr><td><b>Salaire :</b></td><td>" + da + "  </td>";



        pas = "</table></body></html>";
        paragraph2.loadDataWithBaseURL(null, pish + pas, "text/html", "UTF-8", null);

        Tools.systemBarLolipop(this);



    }

    private void displayData(Offre p) {
        articletitle = ((TextView) findViewById(R.id.reciepetitle));
        CommonUtils.setRobotoThinFont(this, articletitle);
        articletitle = ((TextView) findViewById(R.id.reciepetags));
        CommonUtils.setRobotoThinFont(this, articletitle);


       /* t1.setText(p.deficulty);
        TextView t2 = ((TextView) findViewById(R.id.fra_single_recipe_prep_time));
        CommonUtils.setRobotoThinFont(this, t2);
        t2.setText(p.prepair);
        TextView t3 = ((TextView) findViewById(R.id.fra_single_recipe_cook_time));
        CommonUtils.setRobotoThinFont(this, t3);
        t3.setText(p.cocking.contains("/") ? getString(R.string.do_not_cook) : p.cocking);
        TextView t4 = ((TextView) findViewById(R.id.fra_single_recipe_portions));
        CommonUtils.setRobotoThinFont(this, t4);
        t4.setText(p.portion);*/
        //Log.e("Cayegory :",p.type_activite);
        setSuggestionReciepes(db.getSuggestionArticles(p.type_activite));
        final String cate = p.type_activite;
        BtnMore = ((Button) findViewById(R.id.bt_more));
        CommonUtils.setRobotoThinFont(this, BtnMore);
        BtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle = new Bundle();
                fragment = new AllArticlesFragment();
                bundle.putInt(AllArticlesFragment.TAG_CATEGORY, -1);
                bundle.putString(AllArticlesFragment.NAME_CATEGORY, cate);
                fragment.setArguments(bundle);


                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, fragment);
                fragmentTransaction.commit();
            }
        });
        Postuler = ((Button) findViewById(R.id.PostulerBtn));
        CommonUtils.setRobotoBoldFont(this, Postuler);
        Postuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffreDetailActivity.this, ActivityLogin.class);
                startActivity(intent);
            }
        });
      /*  if(distance == -1){
            lyt_distance.setVisibility(View.GONE);
        }else{
            lyt_distance.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.distance)).setText(Tools.getFormatedDistance(distance));
        }*/

        //  setSuggestionReciepes(db.getListImageByPlaceId(p.place_id));
    }

    // this method name same with android:onClick="clickLayout" at layout xml
    public void clickLayout(View view) {
       /* switch (view.getId()){
            case R.id.lyt_address:
                if(!place.isDraft()) {
                    Uri uri = Uri.parse("http://maps.google.com/maps?q=loc:" + place.lat + "," + place.lng);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.lyt_phone:
                if(!place.isDraft() && !place.phone.equals("-")){
                    Tools.dialNumber(this, place.phone);
                }else{
                    Snackbar.make(parent_view, R.string.fail_dial_number, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.lyt_website:
                if(!place.isDraft() && !place.website.equals("-")){
                    Tools.directUrl(this, place.website);
                }else{
                    Snackbar.make(parent_view, R.string.fail_open_website, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }*/
    }

    private void setSuggestionReciepes(List<Offre> articles) {
        // add optional image into list

        List<Offre> suggestions = new ArrayList<>();
        suggestions = articles;

        RecyclerView SuggestionRecycler = (RecyclerView) findViewById(R.id.galleryRecycler);
        SuggestionRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterSuggestion adapter = new AdapterSuggestion(suggestions);
        SuggestionRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterSuggestion.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Offre p) {
                View view =v.findViewById(R.id.image);
                if(view==null){
                    String uri = "@drawable/detail_bg";  // where myresource (without the extension) is the file

                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());

                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    Drawable res = getResources().getDrawable(imageResource);
                    imageView.setImageDrawable(res);
                    OffreDetailActivity.navigate(OffreDetailActivity.this, imageView, p);
                }else
                OffreDetailActivity.navigate(OffreDetailActivity.this, view, p);
            }
        });
    }

    private void fabToggle() {
        try {
            fab.setImageResource(R.drawable.ic_nav_favorites_outline);
            if (db.isFavoritesExist(offres.id)) {
                fab.setImageResource(R.drawable.ic_nav_favorites);
            } else {
                fab.setImageResource(R.drawable.ic_nav_favorites_outline);
            }
        } catch (Exception e) {

        }

    }

    private void prepareAds() {
        if (AppConfig.ENABLE_ADSENSE && Tools.cekConnection(this)) {


            AdView mAdView = (AdView) findViewById(R.id.ad_view);
            // AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e("ADS", "Ad failed: " + i);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("ADS", "Ad Loaded: ");
                }
            });
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
            if (!offres.isDraft()) {

                Snackbar snackbar = Snackbar.make(parent_view, "Partagé l'offre", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                snackbar.show();
                Tools.methodShare(OffreDetailActivity.this, offres);
            }
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

    private void requestDetailsPlace(int place_id) {
        if (!onProcess) {
            onProcess = true;
            showProgressbar(true);
            task_loader = new ApiSinglePlaceLoader(place_id, new Callback<Offre>() {
                @Override
                public void onSuccess(Offre result) {
                    showProgressbar(false);
                    onProcess = false;
                    //  Offre p = db.updatePlace(result);
                    // if (p != null) offres = p;
                    displayData(offres);
                }

                @Override
                public void onError(String result) {
                    showProgressbar(false);
                    onProcess = false;
                    Snackbar.make(parent_view, "Failed load place data", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadReciepeData();
                        }
                    }).show();
                }
            });
            task_loader.execute("");
        } else {
            Snackbar.make(parent_view, "Task still running", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showProgressbar(boolean show) {
        lyt_progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}