package com.mba2dna.apps.EmploiNet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;*/
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.activities.ActivitySplash;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.SharedPref;

public class Tools {

    public static float getAPIVerison() {
        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", " API" + e.getMessage());
        }

        return f.floatValue();
    }

    private static boolean isLolipopOrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

   public static void systemBarLolipop(Activity act) {
        if (isLolipopOrHigher()) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
          //  window.setStatusBarColor(Tools.colorDarker(new SharedPref(act).getThemeColorInt()));
        }
    }

    public static boolean cekConnection(Context context, View view) {
        ConnectionDetector conn = new ConnectionDetector(context);
        if (conn.isConnectingToInternet()) {
            return true;
        } else {
           // noConnectionSnackBar(view);
            return false;
        }
    }

    public static boolean cekConnection(Context context) {
        ConnectionDetector conn = new ConnectionDetector(context);
        if (conn.isConnectingToInternet()) {
            return true;
        } else {
            return false;
        }
    }

    public static void noConnectionSnackBar(View view) {
        Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG).show();
    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(AppConfig.IMAGE_CACHE)
                .cacheOnDisk(AppConfig.IMAGE_CACHE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .threadPoolSize(3)
                .memoryCache(new WeakMemoryCache())
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getGridOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(AppConfig.IMAGE_CACHE)
                .cacheOnDisk(AppConfig.IMAGE_CACHE)
                .showImageOnLoading(R.drawable.noimage) // resource or drawable
                .showImageOnFail(R.drawable.noimage) // resource or drawable
                .build();

        return options;
    }


  /*  public static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length > 0) {
            return accounts[0].title;
        } else {
            return null;
        }
    }*/

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + "";
    }

    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_place_width);
        return Math.round(screenWidth / cellWidth);
    }

  /*  public static GoogleMap configStaticMap(Activity act, GoogleMap googleMap, Offre place) {
        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        // enable traffic layer
        googleMap.isTrafficEnabled();
        googleMap.setTrafficEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View marker_view = inflater.inflate(R.layout.maps_marker, null);
        ((ImageView) marker_view.findViewById(R.id.marker_bg)).setColorFilter(act.getResources().getColor(R.color.marker_secondary));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getPosition()).zoom(12).build();
        MarkerOptions markerOptions = new MarkerOptions().position(place.getPosition());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(act, marker_view)));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return googleMap;
    }

    public static GoogleMap configActivityMaps(GoogleMap googleMap) {
        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(true);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        return googleMap;
    }*/

    /*
     * getting screen width
     */
    public static int getScreenWidth(Context ctx) {
        int columnWidth;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }


    public static void rateAction(Activity activity) {
       /* Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }*/
        final RatingDialog ratingDialog = new RatingDialog.Builder(activity)
                // .icon(R.drawable.app_logo)
               // .session(7)
                .threshold(3)
                .title("كيف كانت تجربتك لهذا التطبيق ؟")
                .titleTextColor(R.color.black)
                .positiveButtonText("ليس الان")
                .negativeButtonText("ابدا")
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("ارسال تعليق")
                .formHint("اخبرنا اين يمكن ان نحسن التطبيق")
                .formSubmitText("ارسال")
                .formCancelText("الغاء")
                .ratingBarColor(R.color.colorPrimary)
               // .positiveButtonBackgroundColor(R.drawable.favorite)
               //  .negativeButtonBackgroundColor(R.drawable.common_google_signin_btn_icon_light)
               /* .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        ratingDialog.dismiss();
                    }
                })*/
               /* .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        ratingDialog.dismiss();
                    }
                })*/
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
    }

    public static void aboutAction(Activity activity) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dialog_about_title));
        builder.setMessage(activity.getString(R.string.about_text));
        builder.setPositiveButton("OK", null);
        builder.show();*/
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.dialog_about_title)).setMessage(R.string.about_text).setPositiveButton("OK", null).show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
       // TextView textView2 = (TextView) dialog.findViewById(android.R.id.title);
       // Typeface face=Typeface.createFromAsset(activity.getAssets(),"fonts/nexalight.ttf");
        CommonUtils.setRobotoThinFont(activity, textView);
       // CommonUtils.setRobotoBoldFont(activity, textView2);
       // textView.setTypeface(face);
    }

    public static void dialNumber(Context ctx, String phone) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + phone));
        ctx.startActivity(i);
    }

    public static void directUrl(Context ctx, String website) {
        String url = website;
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        ctx.startActivity(i);
    }

    public static void methodShare(Activity act, Offre p) {

        // string to share
        String shareBody = "Postuler a l\'offre : \'" + p.title + "\'"
                + "sur l\'application android \'" + act.getString(R.string.app_name) + "\'"
                + "telecharger sur le lien https://play.google.com/store/apps/details?id=" + act.getString(R.string.app_name) + "\'";

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, act.getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        act.startActivity(Intent.createChooser(sharingIntent, "Partager avec :"));
    }

    public static Bitmap createBitmapFromView(Activity act, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static void setActionBarColor(Context ctx, ActionBar actionbar) {
        ColorDrawable colordrw = new ColorDrawable(new SharedPref(ctx).getThemeColorInt());
        actionbar.setBackgroundDrawable(colordrw);
    }

    public static int colorDarker(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static int colorBrighter(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] /= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static void restartApplication(Activity activity) {
        activity.finish();
        ActivityMain.getInstance().finish();
        Intent i = new Intent(activity, ActivitySplash.class);
        activity.startActivity(i);
    }

   /* private static float calculateDistance(LatLng from, LatLng to) {
        Location start = new Location("");
        start.setLatitude(from.latitude);
        start.setLongitude(from.longitude);

        Location end = new Location("");
        end.setLatitude(to.latitude);
        end.setLongitude(to.longitude);

        float distInMeters = start.distanceTo(end);
        float resultDist = 0;
        if(AppConfig.DISTANCE_METRIC_CODE.equals("KILOMETER")){
            resultDist = distInMeters / 1000;
        } else {
            resultDist = (float) (distInMeters * 0.000621371192);
        }
        return resultDist;
    }

    public static List<Offre> getSortedDitanceList(List<Offre> offres, LatLng curLoc) {
        List<Offre> result = new ArrayList<>();
        if (offres.size() > 0) {
            for (int i = 0; i < offres.size(); i++) {
                Offre p = offres.get(i);
                p.distance = calculateDistance(curLoc, p.getPosition());
                result.add(p);
            }
            Collections.sort(result, new Comparator<Offre>() {
                @Override
                public int compare(final Offre p1, final Offre p2) {
                    return Float.compare(p1.distance, p2.distance);
                }
            });
        } else {
            return offres;
        }
        return result;
    }

    public static LatLng getCurLocation(Activity act) {
        if (PermissionUtil.isGroupPermissionGranted(act, Constant.PERMISSIONS_LOCATION)) {
            LocationManager manager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location loc = getLastKnownLocation(act);
                if (loc != null) {
                    return new LatLng(loc.getLatitude(), loc.getLongitude());
                }
            }
        }
        return null;
    }

    public static Location getLastKnownLocation(Activity act) {
        LocationManager mLocationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }*/



}
