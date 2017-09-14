package com.mba2dna.apps.EmploiNet.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.adapter.MySpinnerAdapter;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FertilizationFragment extends Fragment implements DatePickerDialog.OnDateSetListener  {
    private View view;
    private CardView CardCalc, CardResult;
    private TextView header, headerResult,result1,result2,result3;
    private Button CalcBtn, AgainBtn;
    private Spinner days, months, years, periodDuration;
    private ImageView calimg;

    public FertilizationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fertilization, container, false);
        calimg = (ImageView) view.findViewById(R.id.calimg);
        calimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        FertilizationFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                now.add(Calendar.YEAR, 1);
                dpd.setMaxDate(now);
                now.add(Calendar.YEAR, -2);
                dpd.setMinDate(now);
                dpd.vibrate(true);
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.setAccentColor(Color.parseColor("#ffb0b1"));
                dpd.setTitle("أَدْخلي تاريخ أول يوم من آخر دورة شهرية لك");
                dpd.show(getFragmentManager(), "sd");
            }
        });
        prepareAds();
        days = (Spinner) view.findViewById(R.id.days);
        months = (Spinner) view.findViewById(R.id.months);
        years = (Spinner) view.findViewById(R.id.years);
        periodDuration = (Spinner) view.findViewById(R.id.periodDuration);

        final List<String> dayss = new ArrayList<String>();
        dayss.add("يوم");
        for (
                Integer i = 1;
                i < 32; i++)

        {
            dayss.add(i.toString());
        }

        MySpinnerAdapter dataAdapter = new MySpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, dayss);
        this.days.setAdapter(dataAdapter);
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
        yearss.add(Calendar.getInstance().

                get(Calendar.YEAR) - 1 + "");
        yearss.add(Calendar.getInstance().

                get(Calendar.YEAR) + "");
        yearss.add(Calendar.getInstance().

                get(Calendar.YEAR) + 1 + "");
        MySpinnerAdapter dataAdapter2 = new MySpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, yearss);
        this.years.setAdapter(dataAdapter2);

        final List<String> periodDurations = new ArrayList<String>();
        for (
                Integer i = 20;
                i < 46; i++)

        {
            periodDurations.add(i.toString());
        }

        MySpinnerAdapter dataAdapter3 = new MySpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, periodDurations);
        this.periodDuration.setAdapter(dataAdapter3);
        CardCalc = (CardView) view.findViewById(R.id.CardCalc);
        CardResult = (CardView) view.findViewById(R.id.CardResult);
        CalcBtn = (Button) view.findViewById(R.id.CalcBtn);
        CommonUtils.setRobotoThinFont(getActivity(), CalcBtn);
        CalcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days.getSelectedItemPosition() == 0 || months.getSelectedItemPosition() == 0 || years.getSelectedItemPosition() == 0) {
                    Snackbar snackbar = Snackbar.make(view, "أدخلي تاريخا صحيحا من فضلك", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.textError));
                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                    CommonUtils.setRobotoThinFont(getActivity(), tv);
                    snackbar.show();
                }else{

                    try {
                        String dt = days.getSelectedItem().toString()+"-"+months.getSelectedItem().toString()+"-"+years.getSelectedItem().toString();  // Start date
                        String dt2 = days.getSelectedItem().toString()+"-"+months.getSelectedItem().toString()+"-"+years.getSelectedItem().toString();  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        c.setTime(sdf.parse(dt));
                        c.add(Calendar.DATE, Integer.parseInt(periodDuration.getSelectedItem().toString()));  // number of days to add
                        dt = sdf.format(c.getTime());
                        result2.setText(dt.toString());
                        c.add(Calendar.DATE, -4);  // number of days to add
                        dt = sdf.format(c.getTime());
                        result3.setText(dt.toString());
                        c.add(Calendar.DATE, -13);  // number of days to add
                        dt = sdf.format(c.getTime());
                        c.add(Calendar.DATE, 5);  // number of days to add
                         dt2 = sdf.format(c.getTime());
                        result1.setText(Html.fromHtml(dt.toString() + "<font color=\"#2c3e50\"> حتى </font>" + dt2.toString()));

                        CardResult.setVisibility(view.VISIBLE);
                        CardCalc.setVisibility(view.GONE);
                    } catch (ParseException e) {
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
        CommonUtils.setRobotoThinFont(

                getActivity(), header);
        header = (TextView) view.findViewById(R.id.instrucion);
        CommonUtils.setRobotoThinFont(

                getActivity(), header);
        header = (TextView) view.findViewById(R.id.detailperiod);
        CommonUtils.setRobotoThinFont(

                getActivity(), header);
        headerResult = (TextView) view.findViewById(R.id.headerResult);
        CommonUtils.setRobotoThinFont(

                getActivity(), headerResult);
        headerResult = (TextView) view.findViewById(R.id.instrucion1);
        CommonUtils.setRobotoThinFont(getActivity(), headerResult);
        headerResult = (TextView) view.findViewById(R.id.instrucion2);
        CommonUtils.setRobotoThinFont(getActivity(), headerResult);
        headerResult = (TextView) view.findViewById(R.id.instrucion3);
        CommonUtils.setRobotoThinFont(getActivity(), headerResult);
        result1 = (TextView) view.findViewById(R.id.Result1);
        CommonUtils.setRobotoThinFont(getActivity(), result1);
        result2 = (TextView) view.findViewById(R.id.Result2);
        CommonUtils.setRobotoThinFont(getActivity(), result2);
        result3 = (TextView) view.findViewById(R.id.Result3);
        CommonUtils.setRobotoThinFont(getActivity(), result3);
        return view;
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Log.i("DATE :", date);
        days.setSelection(getIndex(days, ("" + dayOfMonth)));
        months.setSelection(getIndex(months, ("" + (monthOfYear + 1))));
        years.setSelection(getIndex(years, ("" + year)));
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
            //AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
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
