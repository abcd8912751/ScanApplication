package com.furja.qc.view;

import android.content.Context;

import androidx.core.app.NotificationCompatSideChannelService;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_START;

/**
 * 捕捉布局异常的 LayoutManager
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {
    boolean scrollAndTopEnabled=false;//滚动到一个布局并置顶
    public WrapLinearLayoutManager(Context context) {
        super(context,LinearLayoutManager.VERTICAL,false);
    }

    public static WrapLinearLayoutManager wrapLayoutManager(Context context)
    {
        return new WrapLinearLayoutManager(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if(scrollAndTopEnabled) {
            TopSnappedSmoothScroller smoothScroller
                    = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
        else
            super.smoothScrollToPosition(recyclerView, state, position);
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically();
    }

    public boolean isScrollAndTopEnabled() {
        return scrollAndTopEnabled;
    }

    public void setScrollAndTopEnabled(boolean scrollAndTopEnabled) {
        this.scrollAndTopEnabled = scrollAndTopEnabled;
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference)
        {
            return boxStart-viewStart;
        }

    }
}
