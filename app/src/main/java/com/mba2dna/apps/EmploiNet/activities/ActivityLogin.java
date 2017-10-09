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

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.Credentials;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.model.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cafe.adriel.androidoauth.callback.OnLoginCallback;
import cafe.adriel.androidoauth.model.SocialUser;
import cafe.adriel.androidoauth.oauth.FacebookOAuth;
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
                Intent intent = new Intent(ActivityLogin.this, ActivityPasswordLost.class);
                startActivity(intent);
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
                        String URL = Constant.getURLApiClientData();
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


        FacebookOAuth.login(this)
                .setClientId(Credentials.FACEBOOK_APP_ID)
                .setClientSecret(Credentials.FACEBOOK_APP_SECRET)
                .setAdditionalScopes("user_birthday")
                .setRedirectUri(Credentials.FACEBOOK_REDIRECT_URI)
                .setCallback(new OnLoginCallback() {
                    @Override
                    public void onSuccess(String token, SocialUser socialUser) {
                        Log.d("Facebook Token", token);
                        Log.d("Facebook User", socialUser+"");
                        Log.d(TAG, "userId:" + socialUser.getId());
                        Log.d(TAG, "email:" + socialUser.getEmail());
                        Log.d(TAG, "profilePictureUrl:" + socialUser.getPictureUrl());
                        Log.d(TAG, "username:" + socialUser.getCoverUrl());
                        Log.d(TAG, "fullName:" + socialUser.getName());
                        Log.d(TAG, "pageLink:" + socialUser.getBirthday());
                        UserSession userSession = new UserSession();
                        userSession.setId(Integer.getInteger(socialUser.getId()));
                        userSession.setEmail(socialUser.getEmail());
                        userSession.setFullName(socialUser.getName());
                        userSession.setPic(socialUser.getPictureUrl());
                        db.addUser(userSession);
                        showProgress(false);
                        LGSnackbarManager.show(SUCCESS, "Vous avez bien connecté a votre compte");
                        finish();
                    }
                    @Override
                    public void onError(Exception error) {
                        error.printStackTrace();
                    }
                })
                .init();


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
