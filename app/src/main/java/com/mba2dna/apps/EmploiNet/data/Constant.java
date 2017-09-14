package com.mba2dna.apps.EmploiNet.data;

import android.Manifest;

public class Constant {

	// for search logs Tag
	public static final String LOG_TAG = "ARTICLE_LOG";

	// Google analytics event type_activite
	public enum Event {
		FAVORITES,
		THEME,
		NOTIFICATION,
		REFRESH
	}

	// for permission android M (6.0)
	public static String[] PERMISSIONS_LOCATION = {
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	public static String[] ALL_REQUIRED_PERMISSION = {
			Manifest.permission.GET_ACCOUNTS,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE
	};



	/** ------------------- EDIT THIS WITH YOURS ------------------------- */

	private static String HOST_URL = "http://emploinet.net/";
	private static String WEB_PATH = "cp/mob.php";

	/** ------------------------------------------------------------------ */
	// photo file url
	public static String getURLimgPlace(String file_name){
		String URL = HOST_URL + WEB_PATH + IMG_PATH_PLACE + "/" + file_name;
		return URL;
	}
	// photo file url
	public static String getURLimgUser(String file_name){
		String URL = HOST_URL + WEB_PATH + IMG_PATH_USERS + "/3a_" + file_name;
		return URL;
	}
	// photo Steps main file url
	public static String getURLimgPlaceStepsMain(String file_name){
		String URL = HOST_URL + WEB_PATH + IMG_MAIN_STEPS_PATH_PLACE + "/" + file_name;
		return URL;
	}
	// photo Steps  file url
	public static String getURLimgPlaceTips(String file_name){
		String URL = HOST_URL + WEB_PATH + IMG_MAIN_TIPS_PATH_PLACE + "/" + file_name;
		return URL;
	}

	// gcm registration url
	public static String getURLgcmserver(){
		String URL = HOST_URL + WEB_PATH + API_PATH_GCM;
		return URL;
	}

	// All in one API data url
	public static String getURLApiClientData(){
		String URL = HOST_URL + WEB_PATH ;//+ API_CLIENT_DATA
		return URL;
	}

	public static String getURLApiSinglePlace(int place_id){
		String URL = HOST_URL + WEB_PATH + API_SINGLE_PLACE + "?reciepe_id="+place_id;
		return URL;
	}

	/** ------------------- DON'T EDIT THIS ------------------------------ */

	private static String API_SINGLE_PLACE = "/app/services/getPlace";
	private static String API_CLIENT_DATA  = "/app/services/" + ( AppConfig.LAZY_LOAD ? "getApiClientDataDraft" : "getApiClientData");

	private static String IMG_PATH_PLACE   = "/pics/";
	private static String IMG_PATH_USERS   = "/users/";
	private static String IMG_MAIN_STEPS_PATH_PLACE   = "/pics/steps";
	private static String IMG_MAIN_TIPS_PATH_PLACE   = "/pics/tips";
	private static String API_PATH_GCM 	   = "/app/services/insertGcm";

	/** ------------------------------------------------------------------ */

}
