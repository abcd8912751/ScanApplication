package com.furja.qc.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.furja.qc.R;
import com.furja.qc.utils.Constants;
import com.furja.qc.view.ZoomPagerAdapter;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ZoomImageActivity extends AppCompatActivity {
    @BindView(R.id.zoomViewPager)
    ViewPager viewPager;
    @BindView(R.id.text_imageIndex)
    TextView imageIndex;
    @BindView(R.id.img_back)
    ImageView img_back;
    private ZoomPagerAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomimage);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        if(intent==null)
            finish();
        int position=Integer.valueOf(intent.getAction());
        ArrayList<String> urls=intent.getStringArrayListExtra(Constants.ZOOM_EXTRA_NAME);
        mAdapter=new ZoomPagerAdapter(urls);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(position);
        img_back.setOnClickListener(view->{
            onBackPressed();
        });
        setImageIndex(viewPager.getCurrentItem()+1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                setImageIndex(i+1);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    public void setImageIndex(int position) {
        int count=mAdapter.getCount();
        imageIndex.setText(position+"/"+count);
    }
}
