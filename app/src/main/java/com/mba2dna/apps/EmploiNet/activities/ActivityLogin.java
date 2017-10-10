package com.mba2dna.apps.EmploiNet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jaychang.sa.AuthCallback;
import com.jaychang.sa.SimpleAuth;
import com.jaychang.sa.SocialUser;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.model.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import greco.lorenzo.com.lgsnackbar.LGSnackbarManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static greco.lorenzo.com.lgsnackbar.style.LGSnackBarTheme.SnackbarStyle.ERROR;
import static greco.lorenzo.com.lgsnackbar.style.LGSnackBarTheme.SnackbarStyle.SUCCESS;
import static greco.lorenzo.com.lgsnackbar.style.LGSnackBarTheme.SnackbarStyle.WARNING;

public class ActivityLogin extends Activity {
    private TextView signin1, forgotpass, signup;
    private EditText emailText, passwordText;
    private CheckBox checkbocremember;
    private LinearLayout fbl;
    private SQLiteHandler db;
    private View lyt_progress;
    private static final String TAG = ActivityLogin.class.getSimpleName();
    String URL = Constant.getURLApiClientData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new SQLiteHandler(this);
        fbl=(LinearLayout) findViewById(R.id.fbl) ;
        fbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFacebook();
            }
        });
        lyt_progress = findViewById(R.id.lyt_progress);
        forgotpass = (TextView) findViewById(R.id.forgotpass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(ActivityLogin.this, ActivityPasswordLost.class);
              //  startActivity(intent);
                LGSnackbarManager.show(WARNING, "Désolée, cette fonction sera prochainement ajoutée");
            }
        });
        signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
        signin1 = (TextView) findViewById(R.id.signin1);
        signin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailText = (EditText) findViewById(R.id.emailText);
                passwordText = (EditText) findViewById(R.id.passwordText);
                checkbocremember = (CheckBox) findViewById(R.id.checkbocremember);
                signin1.setEnabled(false);
                signin1.setClickable(false);
                if (emailText.getText().toString().equals("") || passwordText.getText().toString().equals("")) {
                    LGSnackbarManager.show(ERROR, "Vous avez laissez des champs vides!");
                } else {
                    if (isValidEmail(emailText.getText().toString())) {
                        showProgress(true);
                        OkHttpClient client = new OkHttpClient();

                        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
                        urlBuilder.addQueryParameter("user", "true");
                        urlBuilder.addQueryParameter("u", emailText.getText().toString());
                        urlBuilder.addQueryParameter("n", passwordText.getText().toString());
                        String url = urlBuilder.build().toString();

                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String S = response.body().string();
                            Log.e("RESPENSE", S);
                            JSONObject jObject = new JSONObject(S);

                            JSONArray data = jObject.getJSONArray("user"); // get data object
                            final JSONObject object = data.getJSONObject(0);


                            if (checkbocremember.isChecked()) {

                                try {
                                    UserSession userSession = new UserSession();
                                    userSession.setId(object.getInt("id"));
                                    userSession.setEmail(object.getString("email"));
                                    userSession.setFullName(object.getString("nom") + " " + object.getString("prenom"));
                                    userSession.setPic(object.getString("photo"));
                                    db.addUser(userSession);
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }

                            }

                            showProgress(false);
                            emailText.setEnabled(false);
                            passwordText.setEnabled(false);
                            LGSnackbarManager.show(SUCCESS, "Vous avez bien connecté a votre compte");
                            finish();

                        } catch (IOException e) {
                            Log.e("ERROR", e.getMessage());
                            e.printStackTrace();

                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
                            e.printStackTrace();

                        }


                    } else {
                        showProgress(false);
                        signin1.setEnabled(true);
                        signin1.setClickable(true);
                        LGSnackbarManager.show(WARNING, "Votre Email semble etre erroné");
                    }

                }
            }
        });
    }

    void connectFacebook() {
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");

        SimpleAuth.getInstance().connectFacebook(scopes, new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                Log.e(TAG, "userId:" + socialUser.userId);
                Log.e(TAG, "email:" + socialUser.email);
                Log.e(TAG, "accessToken:" + socialUser.accessToken);
                Log.e(TAG, "profilePictureUrl:" + socialUser.profilePictureUrl);
                Log.e(TAG, "username:" + socialUser.username);
                Log.e(TAG, "fullName:" + socialUser.fullName);
                Log.e(TAG, "pageLink:" + socialUser.pageLink);
                OkHttpClient client2 = new OkHttpClient();
                HttpUrl.Builder urlBuilder2 = HttpUrl.parse(URL).newBuilder();
                urlBuilder2.addQueryParameter("userfacebook", "true");
                urlBuilder2.addQueryParameter("e", socialUser.email);
                String url2 = urlBuilder2.build().toString();

                Request request2 = new Request.Builder()
                        .url(url2)
                        .build();
                Log.e("RESPENSE2", url2);
                try {
                    Response response2 = client2.newCall(request2).execute();
                    String S2 = response2.body().string();
                    if(S2!=null){


                        if(!S2.contains("notexist")){
                            Log.e("RESPENSE2", S2);
                            JSONObject jObject = new JSONObject(S2);

                            JSONArray data = jObject.getJSONArray("userfacebook"); // get data object
                            final JSONObject object = data.getJSONObject(0);

                                try {
                                    UserSession userSession = new UserSession();
                                    userSession.setId(object.getInt("id"));
                                    userSession.setEmail(socialUser.email);
                                    userSession.setFullName(socialUser.fullName);
                                    userSession.setPic(socialUser.profilePictureUrl);
                                    db.addUser(userSession);
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }


                        }
                    }


                    showProgress(false);
                    LGSnackbarManager.show(SUCCESS, "Vous avez bien connecté a votre compte");
                    finish();

                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage());
                    e.printStackTrace();

                } catch (JSONException e) {
                    Log.e("ERROR", e.getMessage());
                    e.printStackTrace();

                }

                showProgress(false);
                LGSnackbarManager.show(SUCCESS, "Vous avez bien connecté a votre compte");
                finish();
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, error.getMessage());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Canceled");
            }
        });
    }

    private void showProgress(boolean show) {

        if (show) {
            lyt_progress.setVisibility(View.VISIBLE);
        } else {
            lyt_progress.setVisibility(View.GONE);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
