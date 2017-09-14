package com.mba2dna.apps.EmploiNet.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.adapter.MySpinnerAdapter;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class KidsGrewthFragment extends Fragment {

    private View view;
    private CardView CardCalc, CardResult;
    private TextView header, headerResult, result1, result2, result3, title2, instrucion1, instrucion2, instrucion3;
    private Button CalcBtn, AgainBtn;
    private Spinner months, years;
    private EditText length, wieght, circule;
    private RadioButton male, female;

    public KidsGrewthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kids_grewth, container, false);


        prepareAds();

        months = (Spinner) view.findViewById(R.id.months);
        years = (Spinner) view.findViewById(R.id.years);
        length = (EditText) view.findViewById(R.id.lenghthtxt);
        wieght = (EditText) view.findViewById(R.id.wighttxt);
        circule = (EditText) view.findViewById(R.id.editcircle);
        male = (RadioButton) view.findViewById(R.id.male);
        female = (RadioButton) view.findViewById(R.id.female);
        CommonUtils.setRobotoThinFont(getActivity(), circule);
        CommonUtils.setRobotoThinFont(getActivity(), wieght);
        CommonUtils.setRobotoThinFont(getActivity(), length);
        CommonUtils.setRobotoThinFont(getActivity(), male);
        CommonUtils.setRobotoThinFont(getActivity(), female);

        final List<String> monthss = new ArrayList<String>();
        monthss.add("شهر");
        for (
                Integer i = 1;
                i < 13; i++)

        {
            monthss.add(i.toString());
        }

        MySpinnerAdapter dataAdapter1 = new MySpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, monthss);
        this.months.setAdapter(dataAdapter1);
        final List<String> yearss = new ArrayList<String>();
        yearss.add("سنة");
        for (
                Integer i = 0;
                i < 5; i++)

        {
            yearss.add(i.toString());
        }
        MySpinnerAdapter dataAdapter2 = new MySpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, yearss);
        this.years.setAdapter(dataAdapter2);


        CardCalc = (CardView) view.findViewById(R.id.CardCalc);
        CardResult = (CardView) view.findViewById(R.id.CardResult);
        CalcBtn = (Button) view.findViewById(R.id.CalcBtn);
        CommonUtils.setRobotoThinFont(

                getActivity(), CalcBtn);
        CalcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (length.getText().equals("") || wieght.getText().equals("") || circule.getText().equals("") || months.getSelectedItemPosition() == 0 || years.getSelectedItemPosition() == 0) {
                    Snackbar snackbar = Snackbar.make(view, "لم تجيبي على كل الأسئلة", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.textError));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getActivity(), tv);
                    snackbar.show();
                } else {

                    try {
                       /* String dt = "-" + months.getSelectedItem().toString() + "-" + years.getSelectedItem().toString();  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        c.setTime(sdf.parse(dt));*/
                        String gender = "m", sex = "ولدك", y = "", m = "";
                        if (male.isChecked()) {
                            gender = "m";
                        } else {
                            gender = "f";
                            sex = "ابنتك";
                        }
                        if ((years.getSelectedItemPosition() - 1) == 1) y = "سنة و ";
                        else if ((years.getSelectedItemPosition() - 1) == 2) y = "سنتين و ";
                        else if ((years.getSelectedItemPosition() - 1) > 2)
                            y = (years.getSelectedItemPosition() - 1) + " سنوات و ";

                        if ((months.getSelectedItemPosition()) == 1) m = "شهر ";
                        else if ((months.getSelectedItemPosition()) == 2) m = "شهران ";
                        else if ((months.getSelectedItemPosition()) > 2)
                            m = (months.getSelectedItemPosition()) + " شهور ";
                        Integer t = (Integer.parseInt(years.getSelectedItem().toString()) * 12) + months.getSelectedItemPosition();
                        title2.setText("عمر " + sex + " " + y + m);
                        Integer height = l(gender + "h", t, length.getText().toString());
                        instrucion1.setText(Html.fromHtml(r(height, "طول الطفل")));
                        result1.setText(Html.fromHtml(til(height)));
                        Integer weight = l(gender + "w", t, wieght.getText().toString());
                        instrucion2.setText(Html.fromHtml(r(weight, "وزن الطفل")));
                        result2.setText(Html.fromHtml(til(weight)));
                        Integer CR = l(gender + "cp", t, circule.getText().toString());
                        instrucion3.setText(Html.fromHtml(r(CR, "محيط رأس الطفل")));
                        result3.setText(Html.fromHtml(til(CR)));

                        /*JSONObject oneObject = mainObject.getJSONObject("1");
                        String id = oneObject.getString("id");*/
                        CardResult.setVisibility(view.VISIBLE);
                        CardCalc.setVisibility(view.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        AgainBtn = (Button) view.findViewById(R.id.BtnAgain);
        CommonUtils.setRobotoThinFont(

                getActivity(), AgainBtn);
        AgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardResult.setVisibility(view.GONE);
                CardCalc.setVisibility(view.VISIBLE);
            }
        });
        header = (TextView) view.findViewById(R.id.header);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.instrucion);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.label1);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.label2);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.label3);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.label4);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.label5);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.textcm);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.textkg);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        header = (TextView) view.findViewById(R.id.textcmm);
        CommonUtils.setRobotoThinFont(getActivity(), header);
        headerResult = (TextView) view.findViewById(R.id.headerResult);
        CommonUtils.setRobotoThinFont(getActivity(), headerResult);
        title2 = (TextView) view.findViewById(R.id.title2);
        CommonUtils.setRobotoThinFont(getActivity(), title2);
        instrucion1 = (TextView) view.findViewById(R.id.instrucion1);
        CommonUtils.setRobotoThinFont(getActivity(), instrucion1);
        instrucion2 = (TextView) view.findViewById(R.id.instrucion2);
        CommonUtils.setRobotoThinFont(getActivity(), instrucion2);
        instrucion3 = (TextView) view.findViewById(R.id.instrucion3);
        CommonUtils.setRobotoThinFont(getActivity(), instrucion3);
        result1 = (TextView) view.findViewById(R.id.Result1);
        CommonUtils.setRobotoThinFont(getActivity(), result1);
        result2 = (TextView) view.findViewById(R.id.Result2);
        CommonUtils.setRobotoThinFont(getActivity(), result2);
        result3 = (TextView) view.findViewById(R.id.Result3);
        CommonUtils.setRobotoThinFont(getActivity(), result3);
        return view;
    }

    public Integer l(String jsonGrop, Integer t, String hei) throws IOException, JSONException {
        InputStream is = getResources().openRawResource(R.raw.values);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        Integer perc = 0;
        String jsonString = writer.toString();
        JSONObject mainObject = new JSONObject(jsonString);
        JSONObject uniObject = mainObject.getJSONObject(jsonGrop);
        JSONArray uniName = uniObject.getJSONArray(t + "");
        for (int i = 0; i < uniName.length(); i++) {
            JSONObject row = uniName.getJSONObject(i);
Log.i("L ---->",row.getDouble("val") + "  "+Double.parseDouble(hei) + " " +t +"    "+ jsonGrop);
            if (row.getDouble("val") <= Double.parseDouble(hei)) {
                perc = row.getInt("perc");
                Log.e("L ---->",row.getDouble("val") + "  "+Double.parseDouble(hei));
            }
        }
        if(perc==99) perc=100;
        return perc;
    }

    public String r(Integer e, String a) {
        return "  <Stong>" + a + ":</Strong> <font color=\'#bc276b\'>" + e + "%</span>";
    }

    public String til(Integer e) {
        String c = null;
        if ((25 <= e) && (e <= 75)) {
            c = "قيمة متوسطة توازي مستوى نمو معظم الأطفال في سن";
        } else if (e < 25) {

            c = "قيمة منخفضة أقل من مستوى نمو الأطفال في سنه";
        } else if (e > 75) {

            c = "قيمة عالية تتقدم على مستوى نمو معظم الأطفال في سنه";
        }

        return c;
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void prepareAds() {
        if (AppConfig.ENABLE_ADSENSE && Tools.cekConnection(getActivity())) {


            AdView mAdView = (AdView) view.findViewById(R.id.ad_view);
           // AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
              AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e("AD PERIOD", "Ad failed: " + i);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("AD PERIOD", "Ad Loaded: ");
                }
            });
        } else {
            ((RelativeLayout) view.findViewById(R.id.banner_layout)).setVisibility(View.GONE);
        }
    }
}
