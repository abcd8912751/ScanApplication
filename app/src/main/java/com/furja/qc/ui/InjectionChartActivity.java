package com.furja.qc.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.furja.qc.R;
import com.furja.qc.jsonbeans.BadBeanJSON;
import com.furja.qc.utils.BadBeanUtils;
import com.furja.qc.utils.Constants;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.LegendView;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apmem.tools.layouts.FlowLayout;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.Call;

import static android.view.KeyEvent.META_CAPS_LOCK_ON;
import static com.furja.qc.utils.Constants.FJ_BADTYPETOTAL_WORKPLACE;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 查看报表的 Activity
 */
public class InjectionChartActivity extends BaseActivity {
    ColumnChartView columnChartView;
    SwitchButton switchScroll;
    BadBeanUtils utils;
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;    //用于执行定时获取数据
    Runnable scrollRunnable;
    FlowLayout linearLayout;
    float curr_Right=-1;
    public  boolean SCROLL_RIGHT=true;
    int requestCount=0;
    private float zoomLevel=1;
    private AlertDialog alertDialog;
    public int deviceWidth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        columnChartView=(ColumnChartView)findViewById(R.id.columnChart);
        linearLayout=(FlowLayout) findViewById(R.id.legendLayout);
        switchScroll=(SwitchButton)findViewById(R.id.switch_autoScroll);

        scheduledThreadPoolExecutor
                =new ScheduledThreadPoolExecutor(3);
        scrollRunnable=new Runnable() {
            @Override
            public void run() {
                if(zoomLevel<=1||!switchScroll.isChecked())
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

    /**
     * 点击子柱弹出对话框
     * @param msg
     */
    private void showDetailDialog(String msg)
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(InjectionChartActivity.this);
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




    /**
     * 自动滚动和定时获取数据刷新视图
     */
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
                requestJson();
            }
        },0,60, TimeUnit.SECONDS);
    }

    /**
     *显示Json
     */
    private void testJson(String response)
    {
        columnChartView.resetViewports();
        try
        {
            BadBeanJSON json= JSON.parseObject(response,BadBeanJSON.class);
            utils=new BadBeanUtils(json.getErrData());
            utils.buildXYValue();
            List<String> workPlaceIDs=utils.getWorkplaceIDs();
            int columnCount=workPlaceIDs.size();
            List<Column> columns=new ArrayList<Column>();
            int maxSubcolumnSize=0; //最大的子柱数目
            for(int columnIndex=0;columnIndex<columnCount;columnIndex++)
            {
                List<SubcolumnValue> subcolumnValues=new ArrayList<SubcolumnValue>();
                List<Float> childValues=utils.getyValues().get(columnIndex);
                for(int i=0;i<childValues.size();i++)
                {
                    subcolumnValues.add(new SubcolumnValue(childValues.get(i),utils.getColor(columnIndex,i)));
                }
                if(childValues.size()>maxSubcolumnSize)
                    maxSubcolumnSize=childValues.size();
                Column column=new Column(subcolumnValues);
                column.setHasLabels(true);
                columns.add(column);
            }
            ColumnChartData chartData=new ColumnChartData(columns);
            chartData.setValueLabelBackgroundEnabled(false);// 设置数据背景是否跟随节点颜色
            Axis axisLeft = new Axis();
            Axis axisBootom = new Axis();
            axisBootom.setTextColor(Color.BLACK);
            axisLeft.setTextColor(Color.BLACK);
            axisBootom.setHasLines(true);
            axisLeft.setHasLines(true);
            List<AxisValue> axisValues=new ArrayList<AxisValue>();
            for(int j=0;j<workPlaceIDs.size();j++)
            {
                axisValues.add(new AxisValue(j).setLabel(workPlaceIDs.get(j)));
            }
            axisBootom.setValues(axisValues);
            chartData.setAxisYLeft(axisLeft);
            chartData.setAxisXBottom(axisBootom);
            chartData.setValueLabelsTextColor(Color.BLACK);
            columnChartView.setColumnChartData(chartData);
            columnChartView.setZoomType(ZoomType.HORIZONTAL);
            columnChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            this.zoomLevel= (float)maxSubcolumnSize*2/5;
            showLog("zoomLevel:"+zoomLevel);
            if(zoomLevel>1)
                zoom(zoomLevel);
            addLegendText(utils);
        }
        catch (Exception e){
            showLog(Utils.exceptionToString(e));
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog()
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(InjectionChartActivity.this);
        builder.setMessage("暂无有效数据");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                alertDialog=null;
                showLog("置空了我");
            }
        });
        if(alertDialog==null)
            alertDialog=builder.create();
        if(!alertDialog.isShowing())
            alertDialog.show();
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


    /**
     * 添加图例
     * @param utils
     */
    public void addLegendText(BadBeanUtils utils)
    {
        linearLayout.removeAllViews();
        List<String> typeLabels=utils.getTypeLabels();
        for(int i=0;i<typeLabels.size();i++)
        {
            LegendView view=new LegendView(this);
            view.setBackgroundColor(utils.getColor(i));
            view.setText(typeLabels.get(i));
            linearLayout.addView(view,i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(scheduledThreadPoolExecutor!=null)
            scheduledThreadPoolExecutor.shutdown();
    }


    private void showLog(String str) {
        Log.e("ChartApp",str);
    }






    public void requestJson()
    {
        utils=null;
        final String uploadUrl
                =Constants.getBaseUrl()+ FJ_BADTYPETOTAL_WORKPLACE;
        final SimpleDateFormat formater
                = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Observable.fromCallable(new Callable<Object>() {
                @Override
                public URLConnection call() throws Exception {
                    URL url =  new URL(uploadUrl);
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect();
                    return uc;
                }})
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) {
                            URLConnection uc=(URLConnection)o;
                            Date date=new Date(uc.getDate());
                            String dateString=formater.format(date);
                            showLog("dateStringFromNetWork:"+dateString);
                            Map<String,String> uploadParams=new HashMap<String,String>();
                            uploadParams.put("fstartdate",dateString);
                            uploadParams.put("fenddate", dateString);
                            OkHttpUtils
                                    .post()
                                    .url(uploadUrl)
                                    .params(uploadParams)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int i) {
                                            e.printStackTrace();
                                            requestCount++;
                                            if(requestCount<3)
                                                requestJson();
                                        }
                                        @Override
                                        public void onResponse(final String responce, int i) {
                                            requestCount=0;
                                            Log.e("ChartApp","获取Size:"+responce.length());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    testJson(responce);
                                                }
                                            });
                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            requestCount++;
            if(requestCount<3)
                requestJson();
        }
    }






    private void toLogin() {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLog("返回了");
        finish();
    }

}