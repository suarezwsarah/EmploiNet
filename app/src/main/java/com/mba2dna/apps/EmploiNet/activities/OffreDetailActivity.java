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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;
import com.iamhabib.easyads.EasyAds;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.adapter.AdapterSuggestion;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.fragment.OffresFragment;
import com.mba2dna.apps.EmploiNet.fragment.PostuleFragment;
import com.mba2dna.apps.EmploiNet.loader.ApiSinglePlaceLoader;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    private Button BtnMore, Postuler;
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

        if (offres.photo != null) {
            ImageView image = (ImageView) findViewById(R.id.image);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgloader.displayImage(offres.photo, image);
        }else {
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

                } else {
                    db.addFavorites(offres.id);
                    db.addArticle(offres);
                    Snackbar snackbar = Snackbar.make(view, offres.title + " " + getString(R.string.add_favorite), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getBaseContext(), tv);
                    snackbar.show();

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

        String html = offres.description;//.substring(end+9)
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

        String da = "";
        if (offres.salaire.equals("0.00"))
            da = "Non disponible";
        else da = offres.salaire + " DA";
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

        setSuggestionReciepes(db.getSuggestionArticles(p.type_activite));
        final String cate = p.type_activite;
        BtnMore = ((Button) findViewById(R.id.bt_more));
        CommonUtils.setRobotoThinFont(this, BtnMore);
        BtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle = new Bundle();
                fragment = new OffresFragment();
                bundle.putInt(OffresFragment.TAG_ID, -1);
                bundle.putString(OffresFragment.TAG_NAME, cate);
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
                if (db.isUserExist()) {
                    Log.e("LOGGED", "LOGGED");
                    FragmentManager fm = getSupportFragmentManager();

                    PostuleFragment screen = PostuleFragment.newInstance(offres);

                    screen.show(fm, "Postule Offre");
                } else {
                    Intent intent = new Intent(OffreDetailActivity.this, ActivityLogin.class);
                    startActivity(intent);
                }

            }
        });
        if (db.isUserExist()) {
            int userId = db.getUserID();
            OkHttpClient client = new OkHttpClient();
            String URL = Constant.getURLApiClientData();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
            urlBuilder.addQueryParameter("checkoffre", "true");
            urlBuilder.addQueryParameter("userid", userId + "");
            urlBuilder.addQueryParameter("offreid", p.id + "");
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String S = response.body().string();
                Log.e("RESPENSE", userId + "");
                Log.e("RESPENSE", p.id + "");
                Log.e("RESPENSE", S);
                if (S.contains("exist")) {
                    Postuler.setEnabled(false);
                    Postuler.setClickable(false);
                    Postuler.setText("Déja Postulé");
                    Postuler.setBackgroundResource(R.drawable.rect_white_normal);
                } else {
                    Postuler.setEnabled(true);
                    Postuler.setClickable(true);
                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Postuler.setEnabled(true);
            Postuler.setClickable(true);
        }
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
                View view = v.findViewById(R.id.image);
                if (view == null) {
                    String uri = "@drawable/detail_bg";  // where myresource (without the extension) is the file

                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());

                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    Drawable res = getResources().getDrawable(imageResource);
                    imageView.setImageDrawable(res);
                    OffreDetailActivity.navigate(OffreDetailActivity.this, imageView, p);
                } else
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
            EasyAds.forNative(this)
                    .with((NativeExpressAdView)findViewById(R.id.adView))
                    .show();
           /* NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
          //  AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
          /*  AdView mAdView = (AdView) findViewById(R.id.ad_view);
            // AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
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
            });*/
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


    @Override
    protected void onResume() {
        if (!imgloader.isInited()) Tools.initImageLoader(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (task_loader != null) task_loader.cancel(true);
        super.onDestroy();
    }


    private void showProgressbar(boolean show) {
        lyt_progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onUserSelectValue(String ok) {
        Postuler.setEnabled(false);
        Postuler.setClickable(false);
        Postuler.setText("Déja Postulé");
        Postuler.setBackgroundResource(R.drawable.rect_white_normal);
    }
}
