package com.mba2dna.apps.EmploiNet.config;

/**
 * Created by BIDA on 11/5/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.model.Category;
import com.mba2dna.apps.EmploiNet.model.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "UserSession";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_fullname = "fullname";
    private static final String KEY_userpic = "pic";


    // Main Table Name
    private static final String TABLE_OFFRE = "offres";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INGRIDIENT = "Ingridient";
    private static final String TABLE_METHOD = "Steps";
    private static final String TABLE_CATEGORY = "category";

    // Relational table Offre to Category ( N to N )
    private static final String TABLE_PLACE_CATEGORY = "reciepes_category";

    // table only for android client
    private static final String TABLE_FAVORITES = "favorites_table";

    // Table Columns names TABLE_OFFRE
    private static final String KEY_OFFRE_ID = "article_id";
    private static final String KEY_NAME = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_CATEGORY = "type_activite";
    private static final String KEY_DETAIL = "description";
    private static final String KEY_USERNAME = "contact_info";
    private static final String KEY_USERPIC = "email_candidature";
    private static final String KEY_PUBLISHED = "pub_date";
    private static final String KEY_TAGS = "fonction";
    private static final String KEY_VIEWS = "views";
    private static final String KEY_WILLAYA = "willaya";
    private static final String KEY_SALAIRE = "salaire";
    private static final String KEY_POSTES = "postes";
    private static final String KEY_REFERENCE = "reference";
    private static final String KEY_CONTRACT = "contrat";
    private static final String KEY_RECRUTEUR_ID = "recruteur_id";
    private static final String KEY_LAST_UPDATE = "entry_date";


    // Table Columns names TABLE_IMAGES
    private static final String KEY_IMG_PLACE_ID = "reciepe_id";
    private static final String KEY_IMG_NAME = "title";

    // Table Columns names TABLE_CATEGORY
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_CAT_NAME = "title";
    private static final String KEY_CAT_ICON = "icon";
    private static final String KEY_CAT_NUM = "num";

    // Table Columns names TABLE_CATEGORY
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "fullname";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PIC = "pic";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableOffre(db);
        createTableCategory(db);
        createTableFavorites(db);
        createTableUser(db);
        // createIngridientReciepe(db);
        // createMethodReciepe(db);

        Log.d(TAG, "Database tables created");
    }

    private void createTableOffre(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_OFFRE + " ("
                + KEY_OFFRE_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_IMAGE + " TEXT, "
                + KEY_CATEGORY + " TEXT, "
                + KEY_DETAIL + " TEXT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_USERPIC + " TEXT, "
                + KEY_PUBLISHED + " TEXT, "
                + KEY_TAGS + " TEXT, "
                + KEY_VIEWS + " INTEGER, "
                + KEY_RECRUTEUR_ID + " TEXT, "
                + KEY_REFERENCE + " TEXT, "
                + KEY_WILLAYA + " TEXT, "
                + KEY_POSTES + " TEXT, "
                + KEY_SALAIRE + " TEXT, "
                + KEY_CONTRACT + " TEXT, "
                + KEY_LAST_UPDATE + " TEXT "
                + ")";
        db.execSQL(CREATE_TABLE);
    }


    private void createTableCategory(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " ("
                + KEY_CAT_ID + " INTEGER PRIMARY KEY, "
                + KEY_CAT_NAME + " TEXT, "
                + KEY_CAT_ICON + " TEXT, "
                + KEY_CAT_NUM + " TEXT "

                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableUser(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_USER_ID + " INTEGER PRIMARY KEY, "
                + KEY_USER_NAME + " TEXT, "
                + KEY_USER_EMAIL + " TEXT, "
                + KEY_USER_PIC + " TEXT "

                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableFavorites(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_OFFRE_ID + " INTEGER PRIMARY KEY "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFRE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing UserSession details in database
     */
    public void addArticle(Offre rc) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_OFFRE_ID + ") FROM " + TABLE_OFFRE + " WHERE " + KEY_OFFRE_ID + "=" + rc.id, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {

                ContentValues values = new ContentValues();
                values.put(KEY_OFFRE_ID, rc.id);
                values.put(KEY_NAME, rc.title);
                values.put(KEY_IMAGE, rc.photo);
                values.put(KEY_CATEGORY, rc.type_activite);
                values.put(KEY_TAGS, rc.fonction);
                values.put(KEY_VIEWS, rc.views);
                values.put(KEY_RECRUTEUR_ID, rc.recruteur_id);
                values.put(KEY_USERNAME, rc.contact_info);
                values.put(KEY_USERPIC, rc.email_candidature);
                values.put(KEY_PUBLISHED, rc.pub_date);
                values.put(KEY_POSTES, rc.postes);
                values.put(KEY_WILLAYA, rc.willaya);
                values.put(KEY_SALAIRE, rc.salaire);
                values.put(KEY_REFERENCE, rc.reference);
                values.put(KEY_CONTRACT, rc.contrat);
                // addIngridients(rc);
                // addMethods(rc);

                // Inserting Row
                long id = db.insert(TABLE_OFFRE, null, values);
                Log.v("Offre Inserted", rc.id + " : " + rc.title);
            }
        } catch (Exception e) {
            Log.e("Offre Eroor", " : " + e.getMessage());
        }
        db.close();
    }

    /**
     * Storing Category in database
     */
    public void addCategory(Category cat) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CATEGORY); // refresh table content
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, cat.cat_id);
        values.put(KEY_CAT_NAME, cat.category);
        values.put(KEY_CAT_ICON, cat.photo);
        values.put(KEY_CAT_NUM, cat.num);


        // Inserting Row
        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New TABLE_CATEGORY inserted into sqlite: " + id);
    }

    /**
     * Storing Ingridient in database
     */


    /**
     * Storing Methods in database
     */


    /**
     * Getting UserSession config from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return UserSession
        Log.d(TAG, "Fetching UserSession from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting All offres
     */
    public List<Offre> getAllArticles() {
        List<Offre> offresList = new ArrayList<Offre>();
        String selectQuery = "SELECT  * FROM " + TABLE_OFFRE;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Offre rc = new Offre();
                rc.id = c.getInt((c.getColumnIndex(KEY_OFFRE_ID)));
                rc.title = c.getString((c.getColumnIndex(KEY_NAME)));
                rc.photo = c.getString((c.getColumnIndex(KEY_IMAGE)));
                rc.type_activite = c.getString((c.getColumnIndex(KEY_CATEGORY)));
                rc.fonction = c.getString((c.getColumnIndex(KEY_TAGS)));
                rc.contact_info = c.getString((c.getColumnIndex(KEY_USERNAME)));
                rc.email_candidature = c.getString((c.getColumnIndex(KEY_USERPIC)));
                rc.pub_date = c.getString((c.getColumnIndex(KEY_PUBLISHED)));
                rc.description = c.getString((c.getColumnIndex(KEY_DETAIL)));
                rc.views = c.getInt((c.getColumnIndex(KEY_VIEWS)));
                rc.recruteur_id = c.getInt((c.getColumnIndex(KEY_RECRUTEUR_ID)));
                rc.postes = c.getString((c.getColumnIndex(KEY_POSTES)));
                rc.willaya = c.getString((c.getColumnIndex(KEY_WILLAYA)));
                rc.contrat = c.getString((c.getColumnIndex(KEY_CONTRACT)));
                rc.salaire = c.getString((c.getColumnIndex(KEY_SALAIRE)));
                rc.reference = c.getString((c.getColumnIndex(KEY_REFERENCE)));


                offresList.add(rc);
            } while (c.moveToNext());
        }

        return offresList;
    }

    public List<Category> getAllCategory() {
        List<Category> CategoryList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category cat = new Category();
                // cat.cat_id = c.getInt((c.getColumnIndex(KEY_CAT_ID)));
                cat.category = c.getString((c.getColumnIndex(KEY_CAT_NAME)));
                cat.photo = c.getString((c.getColumnIndex(KEY_CAT_ICON)));
                cat.num = c.getInt((c.getColumnIndex(KEY_CAT_NUM)));


                CategoryList.add(cat);
            } while (c.moveToNext());
        }

        return CategoryList;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all UserSession info from sqlite");
    }

    public void addListArticles(List<Offre> articles) {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.execSQL("DELETE FROM " + TABLE_OFFRE); // refresh table content
        try {
            for (Offre rc : articles) {
                try {
                    Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_OFFRE_ID + ") FROM " + TABLE_OFFRE + " WHERE " + KEY_OFFRE_ID + "=" + rc.id, null);
                    cursor.moveToFirst();
                    if (cursor.getInt(0) == 0) {

                        ContentValues values = new ContentValues();
                        values.put(KEY_OFFRE_ID, rc.id);
                        values.put(KEY_NAME, rc.title);
                        values.put(KEY_IMAGE, rc.photo);
                        values.put(KEY_CATEGORY, rc.type_activite);
                        values.put(KEY_TAGS, rc.fonction);
                        values.put(KEY_VIEWS, rc.views);
                        values.put(KEY_DETAIL, rc.description);
                        values.put(KEY_RECRUTEUR_ID, rc.recruteur_id);
                        values.put(KEY_USERNAME, rc.contact_info);
                        values.put(KEY_USERPIC, rc.email_candidature);
                        values.put(KEY_PUBLISHED, rc.pub_date);
                        values.put(KEY_POSTES, rc.postes);
                        values.put(KEY_WILLAYA, rc.willaya);
                        values.put(KEY_SALAIRE, rc.salaire);
                        values.put(KEY_REFERENCE, rc.reference);
                        values.put(KEY_CONTRACT, rc.contrat);
                        // addIngridients(rc);
                        // addMethods(rc);

                        // Inserting Row
                        long id = db.insert(TABLE_OFFRE, null, values);
                        Log.v("Offre Inserted", rc.id + " : " + rc.title);
                    }
                } catch (Exception e) {
                    Log.e("Offre Eroor", " : " + e.getMessage());
                }

            }
        } catch (Exception e) {
        }
        db.close(); // Closing database connection
    }

    public void addListCategory(List<Category> cats) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CATEGORY); // refresh table content
        for (Category rc : cats) {


            ContentValues values = new ContentValues();
            // values.put(KEY_CAT_ID, rc.cat_id);
            values.put(KEY_CAT_NAME, rc.category);
            //values.put(KEY_CAT_ICON, rc.photo);
            values.put(KEY_CAT_NUM, rc.num);


            // Inserting Row
            long id = db.insert(TABLE_CATEGORY, null, values);

        }
        db.close(); // Closing database connection
    }

    public int getReciepesSize() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_OFFRE_ID + ") FROM " + TABLE_OFFRE, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getFavoritesSize() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_OFFRE_ID + ") FROM " + TABLE_FAVORITES, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getCategorySize() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_OFFRE_ID + ") FROM " + TABLE_CATEGORY, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    // Adding new Connector
    public void addFavorites(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OFFRE_ID, id);
        // Inserting Row
        db.insert(TABLE_FAVORITES, null, values);
    }

    // all Favorites
    public List<Offre> getAllFavorites() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Offre> locList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT p.* FROM " + TABLE_OFFRE + " p, " + TABLE_FAVORITES + " f" + " WHERE p." + KEY_OFFRE_ID + " = f." + KEY_OFFRE_ID, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Offre rc = new Offre();
                rc.id = c.getInt((c.getColumnIndex(KEY_OFFRE_ID)));
                rc.title = c.getString((c.getColumnIndex(KEY_NAME)));
                rc.photo = c.getString((c.getColumnIndex(KEY_IMAGE)));
                rc.type_activite = c.getString((c.getColumnIndex(KEY_CATEGORY)));
                rc.fonction = c.getString((c.getColumnIndex(KEY_TAGS)));
                rc.recruteur_id = c.getInt((c.getColumnIndex(KEY_RECRUTEUR_ID)));
                rc.description = c.getString((c.getColumnIndex(KEY_DETAIL)));
                rc.postes = c.getString((c.getColumnIndex(KEY_POSTES)));
                rc.willaya = c.getString((c.getColumnIndex(KEY_WILLAYA)));
                rc.contrat = c.getString((c.getColumnIndex(KEY_CONTRACT)));
                rc.salaire = c.getString((c.getColumnIndex(KEY_SALAIRE)));
                rc.reference = c.getString((c.getColumnIndex(KEY_REFERENCE)));
                rc.contact_info = c.getString((c.getColumnIndex(KEY_USERNAME)));
                rc.email_candidature = c.getString((c.getColumnIndex(KEY_USERPIC)));
                rc.pub_date = c.getString((c.getColumnIndex(KEY_PUBLISHED)));


                locList.add(rc);
            } while (c.moveToNext());
        }

        return locList;

    }

    // all Favorites
    public List<Offre> getTopViews() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Offre> locList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_OFFRE + " ORDER BY " + KEY_VIEWS + "  DESC LIMIT 20", null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Offre rc = new Offre();
                rc.id = c.getInt((c.getColumnIndex(KEY_OFFRE_ID)));
                rc.title = c.getString((c.getColumnIndex(KEY_NAME)));
                rc.photo = c.getString((c.getColumnIndex(KEY_IMAGE)));
                rc.type_activite = c.getString((c.getColumnIndex(KEY_CATEGORY)));
                rc.fonction = c.getString((c.getColumnIndex(KEY_TAGS)));
                rc.views = c.getInt((c.getColumnIndex(KEY_VIEWS)));
                rc.recruteur_id = c.getInt((c.getColumnIndex(KEY_RECRUTEUR_ID)));
                rc.description = c.getString((c.getColumnIndex(KEY_DETAIL)));
                rc.postes = c.getString((c.getColumnIndex(KEY_POSTES)));
                rc.willaya = c.getString((c.getColumnIndex(KEY_WILLAYA)));
                rc.contrat = c.getString((c.getColumnIndex(KEY_CONTRACT)));
                rc.salaire = c.getString((c.getColumnIndex(KEY_SALAIRE)));
                rc.reference = c.getString((c.getColumnIndex(KEY_REFERENCE)));
                rc.contact_info = c.getString((c.getColumnIndex(KEY_USERNAME)));
                rc.email_candidature = c.getString((c.getColumnIndex(KEY_USERPIC)));
                rc.pub_date = c.getString((c.getColumnIndex(KEY_PUBLISHED)));


                locList.add(rc);
            } while (c.moveToNext());
        }

        return locList;

    }


    public void deleteFavorites(int id) {
        if (isFavoritesExist(id)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_FAVORITES, KEY_OFFRE_ID + " = ?", new String[]{id + ""});
        }
    }

    public boolean DBinUse() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.isOpen();
    }

    public boolean isFavoritesExist(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_OFFRE_ID + " = " + id, null);
        int count = cursor.getCount();
        if (count > 0) {
            return true;
        } else {
            return false;
        }

    }


    public List<Offre> getCategoryArticles(String name) {


        List<Offre> offresList = new ArrayList<Offre>();
        String selectQuery = "SELECT  * FROM " + TABLE_OFFRE + " WHERE " + KEY_CATEGORY + " = '" + name + "'";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Offre rc = new Offre();
                rc.id = c.getInt((c.getColumnIndex(KEY_OFFRE_ID)));
                rc.title = c.getString((c.getColumnIndex(KEY_NAME)));
                rc.photo = c.getString((c.getColumnIndex(KEY_IMAGE)));
                rc.type_activite = c.getString((c.getColumnIndex(KEY_CATEGORY)));
                rc.fonction = c.getString((c.getColumnIndex(KEY_TAGS)));
                rc.recruteur_id = c.getInt((c.getColumnIndex(KEY_RECRUTEUR_ID)));
                rc.description = c.getString((c.getColumnIndex(KEY_DETAIL)));
                rc.postes = c.getString((c.getColumnIndex(KEY_POSTES)));
                rc.willaya = c.getString((c.getColumnIndex(KEY_WILLAYA)));
                rc.contrat = c.getString((c.getColumnIndex(KEY_CONTRACT)));
                rc.salaire = c.getString((c.getColumnIndex(KEY_SALAIRE)));
                rc.reference = c.getString((c.getColumnIndex(KEY_REFERENCE)));
                rc.contact_info = c.getString((c.getColumnIndex(KEY_USERNAME)));
                rc.email_candidature = c.getString((c.getColumnIndex(KEY_USERPIC)));
                rc.pub_date = c.getString((c.getColumnIndex(KEY_PUBLISHED)));


                offresList.add(rc);
            } while (c.moveToNext());
        }

        return offresList;
    }

    public List<Offre> getSuggestionArticles(String name) {


        List<Offre> offresList = new ArrayList<Offre>();
        String selectQuery = "SELECT  * FROM " + TABLE_OFFRE + "  ORDER BY RANDOM() LIMIT 4";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Offre rc = new Offre();
                rc.id = c.getInt((c.getColumnIndex(KEY_OFFRE_ID)));
                rc.title = c.getString((c.getColumnIndex(KEY_NAME)));
                rc.photo = c.getString((c.getColumnIndex(KEY_IMAGE)));
                rc.type_activite = c.getString((c.getColumnIndex(KEY_CATEGORY)));
                rc.fonction = c.getString((c.getColumnIndex(KEY_TAGS)));
                rc.description = c.getString((c.getColumnIndex(KEY_DETAIL)));
                rc.recruteur_id = c.getInt((c.getColumnIndex(KEY_RECRUTEUR_ID)));
                rc.postes = c.getString((c.getColumnIndex(KEY_POSTES)));
                rc.willaya = c.getString((c.getColumnIndex(KEY_WILLAYA)));
                rc.contrat = c.getString((c.getColumnIndex(KEY_CONTRACT)));
                rc.salaire = c.getString((c.getColumnIndex(KEY_SALAIRE)));
                rc.reference = c.getString((c.getColumnIndex(KEY_REFERENCE)));
                rc.contact_info = c.getString((c.getColumnIndex(KEY_USERNAME)));
                rc.email_candidature = c.getString((c.getColumnIndex(KEY_USERPIC)));
                rc.pub_date = c.getString((c.getColumnIndex(KEY_PUBLISHED)));


                offresList.add(rc);
            } while (c.moveToNext());
        }

        return offresList;
    }

    public boolean isOpen() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public void addUser(UserSession userSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER); // refresh table content
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userSession.id);
        values.put(KEY_USER_NAME, userSession.fullName);
        values.put(KEY_USER_EMAIL, userSession.Email);
        values.put(KEY_USER_PIC, userSession.Pic);
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.e(TAG, "New TABLE_USER inserted into sqlite: " + id);
    }

    public boolean isUserExist() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        int count = cursor.getCount();
        if (count > 0) {
            return true;
        } else {
            return false;
        }

    }

    public UserSession getUser() {


        UserSession offresList = new UserSession();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + "  LIMIT 1";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                UserSession rc = new UserSession();
                rc.id = c.getInt((c.getColumnIndex(KEY_USER_ID)));
                rc.fullName = c.getString((c.getColumnIndex(KEY_USER_NAME)));
                rc.Pic = c.getString((c.getColumnIndex(KEY_USER_PIC)));
                rc.Email = c.getString((c.getColumnIndex(KEY_USER_EMAIL)));


                offresList = rc;
                Log.e("offresList:", offresList.fullName);
            } while (c.moveToNext());
        }

        return offresList;
    }

    public int getUserID() {


        int offresList = 0;
        String selectQuery = "SELECT  " + KEY_USER_ID + " FROM " + TABLE_USER + "  LIMIT 1";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                offresList = c.getInt((c.getColumnIndex(KEY_USER_ID)));

            } while (c.moveToNext());
        }

        return offresList;
    }

    public void DeleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER); // refresh table content
        db.close(); // Closing database connection
        Log.e(TAG, "New TABLE_USER DELETED into sqlite: ");
    }

    public String getUserEmail() {
        String email = "";
        String selectQuery = "SELECT  " + KEY_USER_EMAIL + " FROM " + TABLE_USER + "  LIMIT 1";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                email = c.getString((c.getColumnIndex(KEY_USER_EMAIL)));

            } while (c.moveToNext());
        }

        return email;
    }
}
