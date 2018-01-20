package com.furja.qc.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.furja.qc.R;
import com.furja.qc.beans.Preferences;
import com.furja.qc.jsonbeans.BadBeanJSON;
import com.furja.qc.utils.BadBeanUtils;
import com.furja.qc.utils.Constants;
import com.furja.qc.view.LegendView;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apmem.tools.layouts.FlowLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
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
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 查看报表的 Activity
 */
public class ChartActivity extends AppCompatActivity {
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

        String response="{\"ErrCode\":100,\"ErrMsg\":\"异常统计信息获取成功!\",\"ErrData\":[{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303350102\",\"FName\":\"NV350电机罩-改模后(花岗岩灰50%珠光)\",\"FModel\":\"ABS121\",\"BadType\":\"气纹\",\"Machine\":\"D03\",\"Count\":36},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303350102\",\"FName\":\"NV350电机罩-改模后(花岗岩灰50%珠光)\",\"FModel\":\"ABS121\",\"BadType\":\"缺料\",\"Machine\":\"D03\",\"Count\":36},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303350102\",\"FName\":\"NV350电机罩-改模后(花岗岩灰50%珠光)\",\"FModel\":\"ABS121\",\"BadType\":\"料花\",\"Machine\":\"D03\",\"Count\":329},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303350102\",\"FName\":\"NV350电机罩-改模后(花岗岩灰50%珠光)\",\"FModel\":\"ABS121\",\"BadType\":\"混色\",\"Machine\":\"D04\",\"Count\":2},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5202370101\",\"FName\":\"NV350电机罩(鲨鱼白)\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"杂料\",\"Machine\":\"D04\",\"Count\":3},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5202370101\",\"FName\":\"NV350电机罩(鲨鱼白)\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"混色\",\"Machine\":\"D04\",\"Count\":5},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5202370101\",\"FName\":\"NV350电机罩(鲨鱼白)\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"油污\",\"Machine\":\"D04\",\"Count\":21},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5202370101\",\"FName\":\"NV350电机罩(鲨鱼白)\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"烧焦\",\"Machine\":\"D04\",\"Count\":50},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5202370101\",\"FName\":\"NV350电机罩(鲨鱼白)\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"碰伤\",\"Machine\":\"D04\",\"Count\":105},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303550201\",\"FName\":\"NV470地刷底座(花岗岩灰50%珠光)-加胶\",\"FModel\":\"ABS750/121\",\"BadType\":\"混色\",\"Machine\":\"D05\",\"Count\":21},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5268980101\",\"FName\":\"NV470地刷上盖(花岗岩灰带50%的珠光)\",\"FModel\":\"ABS750\",\"BadType\":\"料花\",\"Machine\":\"D06\",\"Count\":26},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5268980101\",\"FName\":\"NV470地刷上盖(花岗岩灰带50%的珠光)\",\"FModel\":\"ABS750\",\"BadType\":\"混色\",\"Machine\":\"D06\",\"Count\":70},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5268980101\",\"FName\":\"NV470地刷上盖(花岗岩灰带50%的珠光)\",\"FModel\":\"ABS750\",\"BadType\":\"缺料\",\"Machine\":\"D06\",\"Count\":205},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5305390204\",\"FName\":\"HV380地刷底座(灰白色)改模\",\"FModel\":\"ABS747\",\"BadType\":\"水口修整\",\"Machine\":\"D07\",\"Count\":28},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5305390204\",\"FName\":\"HV380地刷底座(灰白色)改模\",\"FModel\":\"ABS747\",\"BadType\":\"白芯\",\"Machine\":\"D07\",\"Count\":238},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5300290101\",\"FName\":\"NV470地刷装饰板2(本色)\",\"FModel\":\"ABS750\",\"BadType\":\"水口修整\",\"Machine\":\"D09\",\"Count\":32},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5300290101\",\"FName\":\"NV470地刷装饰板2(本色)\",\"FModel\":\"ABS750\",\"BadType\":\"碰伤\",\"Machine\":\"D09\",\"Count\":46},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5233350101\",\"FName\":\"NV470地刷透明窗(透明)\",\"FModel\":\"透明ABS TR558A\",\"BadType\":\"碰伤\",\"Machine\":\"D18\",\"Count\":4},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5233350101\",\"FName\":\"NV470地刷透明窗(透明)\",\"FModel\":\"透明ABS TR558A\",\"BadType\":\"缺料\",\"Machine\":\"D18\",\"Count\":21},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5240560101\",\"FName\":\"HV300小电动地刷上盖(DC35灰)\",\"FModel\":\"ABS747\",\"BadType\":\"冷料\",\"Machine\":\"D22\",\"Count\":22},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303140101\",\"FName\":\"UV540左右手柄(花岗岩灰50%珠光+花岗岩灰50%珠光)\",\"FModel\":\"ABS PA-747(+)\",\"BadType\":\"水口修整\",\"Machine\":\"D23\",\"Count\":15},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303140101\",\"FName\":\"UV540左右手柄(花岗岩灰50%珠光+花岗岩灰50%珠光)\",\"FModel\":\"ABS PA-747(+)\",\"BadType\":\"冷料\",\"Machine\":\"D23\",\"Count\":90},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"料花\",\"Machine\":\"D36\",\"Count\":44},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303350102\",\"FName\":\"NV350电机罩-改模后(花岗岩灰50%珠光)\",\"FModel\":\"ABS121\",\"BadType\":\"混色\",\"Machine\":\"DO4\",\"Count\":12},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5303550201\",\"FName\":\"NV470地刷底座(花岗岩灰50%珠光)-加胶\",\"FModel\":\"ABS750/121\",\"BadType\":\"混色\",\"Machine\":\"DO5\",\"Count\":12},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5268980101\",\"FName\":\"NV470地刷上盖(花岗岩灰带50%的珠光)\",\"FModel\":\"ABS750\",\"BadType\":\"混色\",\"Machine\":\"DO6\",\"Count\":18},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5305140404\",\"FName\":\"HV380地刷上盖-改模后20170821(木炭灰)\",\"FModel\":\"ABS750\",\"BadType\":\"料花\",\"Machine\":\"E19\",\"Count\":33},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5305140404\",\"FName\":\"HV380地刷上盖-改模后20170821(木炭灰)\",\"FModel\":\"ABS750\",\"BadType\":\"料花\",\"Machine\":\"E21\",\"Count\":110},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5208410102\",\"FName\":\"NV355地刷上盖(鲨鱼白）-优化\",\"FModel\":\"ABS 750/121鲨鱼白\",\"BadType\":\"油污\",\"Machine\":\"E22\",\"Count\":33},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5314110301\",\"FName\":\"NV480Y手柄连接管（本色）\",\"FModel\":\"ABS747\",\"BadType\":\"缺料\",\"Machine\":\"E24\",\"Count\":21},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5304210101\",\"FName\":\"NV350左右手柄（可调）2#3#4#5#6#(宝马蓝+宝马蓝)\",\"FModel\":\"ABS747\",\"BadType\":\"水口修整\",\"Machine\":\"E25\",\"Count\":33},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5203490201\",\"FName\":\"NV350地刷底座(9C)-优化\",\"FModel\":\"ABS750/121\",\"BadType\":\"其他\",\"Machine\":\"E28\",\"Count\":1},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5203490201\",\"FName\":\"NV350地刷底座(9C)-优化\",\"FModel\":\"ABS750/121\",\"BadType\":\"水口修整\",\"Machine\":\"E28\",\"Count\":6},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5203490301\",\"FName\":\"NV350地刷底座(10397C)-优化\",\"FModel\":\"ABS750/121\",\"BadType\":\"缺料\",\"Machine\":\"E28\",\"Count\":8},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5203490701\",\"FName\":\"NV350地刷底座(DC35灰)-优化\",\"FModel\":\"ABS750/121\",\"BadType\":\"缺料\",\"Machine\":\"E29\",\"Count\":136},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"碰伤\",\"Machine\":\"E30\",\"Count\":8},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5221050101\",\"FName\":\"NV100尘杯(透明)\",\"FModel\":\"透明ABS TR588A\",\"BadType\":\"杂料\",\"Machine\":\"E34\",\"Count\":13},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5221050101\",\"FName\":\"NV100尘杯(透明)\",\"FModel\":\"透明ABS TR588A\",\"BadType\":\"混色\",\"Machine\":\"E34\",\"Count\":28},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5221050101\",\"FName\":\"NV100尘杯(透明)\",\"FModel\":\"透明ABS TR588A\",\"BadType\":\"其他\",\"Machine\":\"E34\",\"Count\":50},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5221050101\",\"FName\":\"NV100尘杯(透明)\",\"FModel\":\"透明ABS TR588A\",\"BadType\":\"料花\",\"Machine\":\"E34\",\"Count\":88},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"碰伤\",\"Machine\":\"E36\",\"Count\":15},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5221050101\",\"FName\":\"NV100尘杯(透明)\",\"FModel\":\"透明ABS TR588A\",\"BadType\":\"料花\",\"Machine\":\"E36\",\"Count\":40},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"流痕\",\"Machine\":\"E36\",\"Count\":45},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"热光影\",\"Machine\":\"E36\",\"Count\":72},{\"CheckDate\":\"2018-01-02\",\"FNumber\":\"5236980105\",\"FName\":\"HV300尘杯A(透明)-改模\",\"FModel\":\"透明ABS\\r\\nTR558A\",\"BadType\":\"料花\",\"Machine\":\"E36\",\"Count\":405}]}";
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
        showLog("deviceWidth:"+deviceWidth);
//        testJson(response);
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
        switchScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Preferences.saveManualScroll(!b);
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
                =new AlertDialog.Builder(ChartActivity.this);
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
            int maxSubcolumnSize=0;
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
            if(alertDialog!=null)
            {
                showLog("关闭了");
                alertDialog.cancel();
            }
            else
                showLog("我竟然是空");
        }
        catch (Exception e)
        {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.close();
            showLog(writer.toString());
//            showDialog();
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog()
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(ChartActivity.this);
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



    public  int getScreenWidth()
    {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }


    public void requestJson()
    {
        utils=null;
        final String uploadUrl= FJ_BADTYPETOTAL_WORKPLACE;
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