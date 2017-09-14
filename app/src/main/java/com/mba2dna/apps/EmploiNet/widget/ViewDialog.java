package com.mba2dna.apps.EmploiNet.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;

/**
 * Created by user on 09/11/2016.
 */

public class ViewDialog {

    public void showDialog(final Activity activity, String msg){
        final Activity activit=activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogexit);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        CommonUtils.setRobotoThinFont(activit, text);
        text.setText(msg);
        TextView text1 = (TextView) dialog.findViewById(R.id.textTil);
        CommonUtils.setRobotoThinFont(activit, text1);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        CommonUtils.setRobotoThinFont(activit, dialogButton);
        Button CancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        CommonUtils.setRobotoThinFont(activit, CancelButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ((Activity)(activit)).finish();
            }
        });
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
