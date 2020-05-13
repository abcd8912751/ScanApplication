package com.furja.qc.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.R;
import com.furja.qc.beans.AssemblyBoard;
import com.furja.qc.beans.BoardEntryUtils;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetrofitHelper;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.view.LegendView;

import org.apmem.tools.layouts.FlowLayout;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import lecho.lib.hellocharts.view.ColumnChartView;

import static android.view.KeyEvent.META_CAPS_LOCK_ON;
import static com.furja.qc.utils.Constants.VERTX_INNER_URL;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class AssemblyChartActivity extends BaseActivity {
    ColumnChartView columnChartView;
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;    //用于执行定时获取数据
    Runnable scrollRunnable;
    FlowLayout linearLayout;
    private float zoomLevel=1,curr_Right=-1;
    public int deviceWidth;
    public  boolean SCROLL_RIGHT=true;
    BoardEntryUtils utils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblychart);
        columnChartView=findViewById(R.id.columnChart);
        linearLayout=findViewById(R.id.legendLayout);
        scheduledThreadPoolExecutor
                =new ScheduledThreadPoolExecutor(3);
        scrollRunnable=new Runnable() {
            @Override
            public void run() {
                if(zoomLevel<=1)
                    return;
                Viewport viewport=columnChartView.getCurrentViewport();
                if(curr_Right== viewport.right||Math.abs(curr_Right-viewport.right)<1)
                {
                    SCROLL_RIGHT=!SCROLL_RIGHT;
                }
                else
                    curr_Right=viewport.right;
                showLog("CurrRight:"+curr_Right);
                if(!SCROLL_RIGHT)
                    scrollToLeft();
                else
                    scrollToRight();
            }
        };
        deviceWidth=getScreenWidth();
        autoScrollAndFreshRegular();
        columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                if(utils!=null)
                {
                    String msg=utils.getDialogMsg(columnIndex,subcolumnIndex);
                    showDetailDialog(msg);
                }
                else
                    showToast("正在更新数据,请稍候重试");
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void autoScrollAndFreshRegular() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(scrollRunnable);
            }
        },1500,4000, TimeUnit.MILLISECONDS);

        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                requestJSON();
            }
        },0,60, TimeUnit.SECONDS);
    }


    /**
     * 点击子柱弹出对话框
     * @param msg
     */
    private void showDetailDialog(String msg)
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }



    private void scrollToLeft()
    {
        MotionEvent event;
        float X=deviceWidth*195/10000;
        showLog("X>"+X);
        float grads=(deviceWidth*95/100)/5;
        long downTime=SystemClock.uptimeMillis();
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, deviceWidth*988/1000, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
    }


    private void scrollToRight()
    {
        MotionEvent event;
        float X=deviceWidth*98/100;
        float grads=(deviceWidth*95/100)/5;
        showLog("X>"+X);
        long downTime=SystemClock.uptimeMillis();
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, deviceWidth*195/10000, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
    }


    public  int getScreenWidth()
    {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }
    /**
     * 延时200 ms
     */
    private void delay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }


    private void requestJSON() {
        RetrofitHelper helper
                =RetrofitBuilder.getHelperByUrl(VERTX_INNER_URL,RetrofitHelper.class);
        final SimpleDateFormat formater
                = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Observable.fromCallable(new Callable<URLConnection>() {
                @Override
                public URLConnection call() throws Exception {
                    URL url =  new URL(VERTX_INNER_URL);
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect();
                    return uc;
                }})
                    .subscribeOn(Schedulers.io())
                    .subscribe(uc->{
                        Date date = new Date(uc.getDate());
                        String dateString = formater.format(date);
                        helper.getAssemblyBoard(dateString,dateString)
                                .retryWhen(new RetryWhenUtils())
                                .subscribe(listBaseHttpResponse -> {
                                    if(listBaseHttpResponse.getCode()>0)
                                    {
                                        List<AssemblyBoard> result
                                                =listBaseHttpResponse.getResult();
                                        showChart(result);
                                    }
                                    else
                                    {
                                        showToast("无符合条件的数据");
                                    }
                                },throwable -> {
                                    throwable.printStackTrace();
                                });
                    },throwable -> {
                        Observable.fromCallable(new Callable<MaterialDialog>() {
                            @Override
                            public MaterialDialog call() throws Exception {
                                return new MaterialDialog.Builder(AssemblyChartActivity.this)
                                        .title(INTERNET_ABNORMAL)
                                        .show();
                            }}).subscribeOn(AndroidSchedulers.mainThread())
                                .delay(40,TimeUnit.SECONDS)
                                .subscribe(materialDialog -> {
                                    materialDialog.cancel();});
                    });
        } catch (Exception e) {
            showLog("315行报喜");
            };
    }

    private void showChart(List<AssemblyBoard> result) {
        utils=new BoardEntryUtils();
        utils.buildColumns(result);
        ColumnChartData chartData=new ColumnChartData(utils.getColumnList());
        chartData.setValueLabelBackgroundEnabled(false);// 设置数据背景是否跟随节点颜色
        Axis axisLeft = new Axis();
        Axis axisBootom = new Axis();
        axisBootom.setTextColor(Color.BLACK);
        axisLeft.setTextColor(Color.BLACK);
        axisBootom.setHasLines(true);
        axisLeft.setHasLines(true);
        axisBootom.setValues(utils.getAxisBottoms());
        chartData.setAxisYLeft(axisLeft);
        chartData.setAxisXBottom(axisBootom);
        chartData.setValueLabelsTextColor(Color.BLACK);
        columnChartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showLog("放大级别:"+columnChartView.getZoomLevel());
                return false;
            }
        });
        ColumnChartRenderer columnChartRenderer=(ColumnChartRenderer)columnChartView.getChartRenderer();
        columnChartRenderer.setBaseColumnWidth(100);
        columnChartView.setChartRenderer(columnChartRenderer);
        runOnUiThread(()->{
            columnChartView.setColumnChartData(chartData);
            columnChartView.setZoomType(ZoomType.HORIZONTAL);
            columnChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        });
        this.zoomLevel= (float)utils.getMaxSubSize()*2/4;
        if(zoomLevel>1)
            zoom(zoomLevel);
        addLegendText(utils);
    }

    private void addLegendText(BoardEntryUtils utils) {
        runOnUiThread(()->{
            linearLayout.removeAllViews();
            List<String> typeLabels=utils.getProductModels();
            for(int i=0;i<typeLabels.size();i++)
            {
                LegendView view=new LegendView(this);
                view.setBackgroundColor(utils.getColorOfModel(typeLabels.get(i)));
                view.setText(typeLabels.get(i));
                linearLayout.addView(view,i);
            }
        });

    }

    public void zoom(final float level)
    {
        if(scheduledThreadPoolExecutor.isTerminated())
        {
            showLog("我已经被关闭了");
        }
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                columnChartView.setZoomLevel(0,0,level);
            }
        },1,TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(scheduledThreadPoolExecutor!=null)
        {
            showLog("拜拜了");
            scheduledThreadPoolExecutor.shutdown();
        }

    }
}
