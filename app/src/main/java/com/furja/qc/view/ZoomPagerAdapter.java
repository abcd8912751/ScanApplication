package com.furja.qc.view;

import android.net.Uri;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.furja.qc.R;
import com.furja.qc.contract.ZoomContract;

import java.util.ArrayList;


/**
 * Created by zhangmeng on 2017/11/13.
 */

public class ZoomPagerAdapter  extends PagerAdapter implements ZoomContract.Presenter{
    private ArrayList<String> imageUries;
    private  int mItemCount;
    private boolean mAllowSwipingWhileZoomed = true;

    public ZoomPagerAdapter(ArrayList<String> imageUries) {
        this.imageUries=imageUries;
        mItemCount = imageUries.size();
    }

    public void setAllowSwipingWhileZoomed(boolean allowSwipingWhileZoomed) {
        mAllowSwipingWhileZoomed = allowSwipingWhileZoomed;
    }

    public boolean allowsSwipingWhileZoomed() {
        return mAllowSwipingWhileZoomed;
    }

    public void toggleAllowSwipingWhileZoomed() {
        mAllowSwipingWhileZoomed = !mAllowSwipingWhileZoomed;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomableDraweeView zoomableDraweeView;TextView imageIndex;
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.zoomable_image,container,false);
        zoomableDraweeView =
                    (ZoomableDraweeView) view.findViewById(R.id.zoomableView);
        zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(mAllowSwipingWhileZoomed);
        zoomableDraweeView.setIsLongpressEnabled(false);
        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(getImageUri(position))
                .setCallerContext(this)
                .build();
        zoomableDraweeView.setController(controller);
        container.addView(view);
        view.requestLayout();
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ZoomableDraweeView zoomableDraweeView = (ZoomableDraweeView) view.findViewById(R.id.zoomableView);
        zoomableDraweeView.setController(null);
        container.removeView(view);
    }


    @Override
    public int getCount() {
        return mItemCount;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
         return POSITION_NONE;
    }

    public Uri getImageUri(int position) {
        String url=imageUries.get(position);
        return Uri.parse(url);
    }

}

