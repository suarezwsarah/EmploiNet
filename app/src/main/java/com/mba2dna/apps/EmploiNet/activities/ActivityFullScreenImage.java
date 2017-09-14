package com.mba2dna.apps.EmploiNet.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import com.mba2dna.apps.EmploiNet.adapter.AdapterFullScreenImage;
import com.mba2dna.apps.EmploiNet.utils.Tools;

public class ActivityFullScreenImage extends AppCompatActivity {

    public static final String EXTRA_POS   = "app.thecity.EXTRA_POS";
    public static final String EXTRA_IMGS  = "app.thecity.EXTRA_IMGS";

    private ImageLoader imgloader = ImageLoader.getInstance();
    private AdapterFullScreenImage adapter;
    private ViewPager viewPager;
    private TextView text_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        if(!imgloader.isInited()) Tools.initImageLoader(this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        text_page = (TextView) findViewById(R.id.text_page);

        ArrayList<String> items = new ArrayList<>();
        Intent i = getIntent();
        final int position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);
        adapter = new AdapterFullScreenImage(ActivityFullScreenImage.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);

        text_page.setText((position+1)+" of "+total);

        // displaying selected image first
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                text_page.setText((pos+1)+" of "+total);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ((ImageButton) findViewById(R.id.btnClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    @Override
    protected void onResume() {
        if(!imgloader.isInited()) Tools.initImageLoader(this);
        super.onResume();
    }

}
