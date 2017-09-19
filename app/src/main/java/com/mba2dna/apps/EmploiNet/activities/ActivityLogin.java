package com.mba2dna.apps.EmploiNet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.model.UserSession;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import greco.lorenzo.com.lgsnackbar.LGSnackbarManager;
import okhttp3.Call;
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
    private SQLiteHandler db;
    private View lyt_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new SQLiteHandler(this);
        lyt_progress = findViewById(R.id.lyt_progress);
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
                            String S =response.body().string();
                            Log.e("RESPENSE", S);
                            JSONObject jObject = new JSONObject(S);

                            JSONArray data = jObject.getJSONArray("user"); // get data object
                            final JSONObject object = data.getJSONObject(0);



                                    if (checkbocremember.isChecked()) {

                                        try {
                                            UserSession userSession = new UserSession();
                                            userSession.setId(object.getInt("id"));
                                            userSession.setEmail(object.getString("email"));
                                            userSession.setFullName(object.getString("nom")+" "+object.getString("prenom"));
                                            userSession.setPic(object.getString("photo"));
                                            db.addUser(userSession);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                            showProgress(false);
                            emailText.setEnabled(false);
                            passwordText.setEnabled(false);
                            LGSnackbarManager.show(SUCCESS, "Everything is looking good! Awesome!");
                            finish();

                        } catch (IOException e) {
                            Log.e("ERROR",e.getMessage());
                            e.printStackTrace();

                        } catch (JSONException e) {
                            Log.e("ERROR",e.getMessage());
                            e.printStackTrace();

                        }

                     /*   ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
                            @Override
                            public void onSuccess(ApiClient result) {
                                final UserSession userSession = result.UserSessions;
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (checkbocremember.isChecked()) {
                                            db.addUser(userSession);
                                        }
                                    }


                                });
                                showProgress(false);
                                emailText.setEnabled(false);
                                passwordText.setEnabled(false);
                                LGSnackbarManager.show(SUCCESS, "Everything is looking good! Awesome!");
                            }

                            //moumouh206@gmail.com
                            @Override
                            public void onError(String result) {
                                showProgress(false);
                                signin1.setEnabled(true);
                                signin1.setClickable(true);
                                LGSnackbarManager.show(ERROR, "Vous identifiants sont inccorects");
                            }
                        });

                        task.execute("?user=true&u=" + emailText.getText().toString() + "&n=" + passwordText.getText().toString());*/

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
