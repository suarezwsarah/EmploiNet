package com.mba2dna.apps.EmploiNet.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.library.lgsnackbar.LGSnackbarManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mba2dna.apps.EmploiNet.library.lgsnackbar.style.LGSnackBarTheme.SnackbarStyle.WARNING;


public class ActivityRegister extends Activity {
    private TextView signin1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signin1 = (TextView) findViewById(R.id.signin1);
        signin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LGSnackbarManager.show(WARNING, "Désolée, l'inscription est désactivé depuis l'application, pour creer un compte vous devez consulter le site web");

               // LGSnackbarManager.show(INFO, "L'inscription sera bientôt réparée");
            }
        });


    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
