package com.furja.qc.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import com.furja.qc.jsonbeans.BadBeanJSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 将从服务器提取到的数据处理成List
 */

public class BadBeanUtils {
    private List<BadBeanJSON.ErrDataBean> ErrData;
    private List<List<Float>> yValues;
    private List<Float> yChildValues;   //
    private List<String> workplaceIDs;  //机台号 集
    private List<List<String>> xLabels; //子柱对应的异常类型
    private List<String> xChildLabels;
    private List<String> typeLabels;    //异常类型标牌
    private List<Integer> colours;
    private List<List<String>> columnDetail; //点击柱子显示的明细
    private List<String> childOfColumnDetail;
    public BadBeanUtils(List<BadBeanJSON.ErrDataBean> ErrDatas)
    {
        this.ErrData=ErrDatas;
        this.yValues= new ArrayList<>();
        this.xLabels= new ArrayList<>();
        this.columnDetail = new ArrayList<>();
        this.xChildLabels=new ArrayList<String>();
        this.yChildValues=new ArrayList<Float>();
        this.childOfColumnDetail=new ArrayList<String>();
        this.workplaceIDs=new ArrayList<String>();
        this.typeLabels=new ArrayList<String>();
        this.colours=new ArrayList<Integer>();
        colours.add(Color.GREEN);
        colours.add(Color.BLUE);
        colours.add(Color.RED);
        colours.add(Color.CYAN);
        colours.add(Color.DKGRAY);
        colours.add(Color.LTGRAY);
        colours.add(Color.MAGENTA);
        colours.add(Color.rgb(104, 241, 175));
        colours.add(Color.rgb(164, 228, 251));
        colours.add(Color.rgb(242, 247, 158));
        colours.add(Color.rgb(255, 102, 0));
    }

    /**
     * 根据JSON值创造XY轴信息
     */
    @SuppressLint("NewApi")
    public void buildXYValue()
    {
        for(BadBeanJSON.ErrDataBean dataBean:ErrData)
        {
            if(!workplaceIDs.contains(dataBean.getMachine()))
            {
                workplaceIDs.add(dataBean.getMachine());
                if(xChildLabels.size()>0)
                    xLabels.add(xChildLabels);
                if(yChildValues.size()>0)
                    yValues.add(yChildValues);
                if(childOfColumnDetail.size()>0)
                    columnDetail.add(childOfColumnDetail);
                xChildLabels = new ArrayList<String>();
                yChildValues = new ArrayList<Float>();
                childOfColumnDetail= new ArrayList<String>();
            }
            if(!typeLabels.contains(dataBean.getBadType()))
                typeLabels.add(dataBean.getBadType());
            if(!xChildLabels.contains(dataBean.getBadType()))
            {
                xChildLabels.add(dataBean.getBadType());
                yChildValues.add((float) dataBean.getCount());
                childOfColumnDetail.add(dataBean.toString());
            }
            else
            {
                int index=xChildLabels.indexOf(dataBean.getBadType());
                yChildValues.set(index,yChildValues.get(index)+dataBean.getCount());
                String sourceString=childOfColumnDetail.get(index);
                sourceString=sourceString+";"+System.lineSeparator()+dataBean.toString();
                childOfColumnDetail.set(index,sourceString);
            }
        }
        if(yChildValues.size()>0)
            yValues.add(yChildValues);
        if(xChildLabels.size()>0)
            xLabels.add(xChildLabels);
        if(childOfColumnDetail.size()>0)
            columnDetail.add(childOfColumnDetail);
        addColor(typeLabels.size());
//        showLog(columnDetail.size()+"<columnDetail:"+columnDetail.toString());
//        showLog(xLabels.size()+"<xLabels:"+xLabels.toString());
//        showLog(workplaceIDs.size()+"<workplaceIDs:"+workplaceIDs.toString());
//        showLog(yValues.size()+"<yValues:"+yValues.toString());
//        showLog(typeLabels.size()+"<typeLabels:"+typeLabels.toString());
    }

    public int getColor(int columnIndex,int subColumnIndex)
    {
        int index=0;
        String badtype=xLabels.get(columnIndex).get(subColumnIndex);
        index=typeLabels.indexOf(badtype);
        if(index<1)
            index=0;
        return colours.get(index);
    }

    public String getBadType(int columnIndex,int subColumnIndex)
    {
        if(columnIndex>=xLabels.size())
            return " ";
        List<String> xBadTypes=xLabels.get(columnIndex);
        if(subColumnIndex>=xBadTypes.size())
            return " ";
        String badtype=xBadTypes.get(subColumnIndex);
        return badtype;
    }


    public String getXLabel(int index)
    {
        if(index>=workplaceIDs.size())
            return " ";
        return workplaceIDs.get(index);
    }



    public int getColor(int index)
    {
        if(index<1||index>=colours.size())
            index=0;
        return colours.get(index);
    }
    private void showLog(String str)
    {
        Log.e("ChartApp",str);
    }

    /**
     * 根据 length 补加颜色
     * @param length
     */
    private void addColor(int length) {
        length=length-colours.size();
        if(length<1)
            return;
        Random random = new Random();
        for(int i=0;i<length;i++)
        {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            colours.add(Color.rgb(r,g,b));
        }
    }


    public List<List<Float>> getyValues() {
        return yValues;
    }

    public void setyValues(List<List<Float>> yValues) {
        this.yValues = yValues;
    }

    public List<String> getWorkplaceIDs() {
        return workplaceIDs;
    }

    public void setWorkplaceIDs(List<String> workplaceIDs) {
        this.workplaceIDs = workplaceIDs;
    }

    public List<List<String>> getxLabels() {
        return xLabels;
    }

    public void setxLabels(List<List<String>> xLabels) {
        this.xLabels = xLabels;
    }

    public List<String> getTypeLabels() {
        return typeLabels;
    }

    /**
     * 获取对话框信息
     * @return
     */
    public String getDialogMsg(int index,int subIndex)
    {
        String msg="无有效信息";
        try {
            msg=columnDetail.get(index).get(subIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}
