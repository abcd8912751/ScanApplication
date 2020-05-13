package com.furja.qc.beans;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.SubcolumnValue;

/**
 * 装配看板数据构造
 */
public class BoardEntryUtils {
    private List<List<SubEntry>> columns;
    private List<String> aximBottoms;
    private List<Integer> colors;
    private List<String> productModels;
    private List<String> badTypedetails;
    private int maxSubColumn;
    List<Column> columnList;
    public BoardEntryUtils()
    {
        columns=new ArrayList<>();
        aximBottoms=new ArrayList<>();
        this.colors =new ArrayList<Integer>();
        badTypedetails =new ArrayList<>();
        columnList=new ArrayList<>();
        productModels=new ArrayList<>();
        maxSubColumn=0;
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.CYAN);
        colors.add(Color.DKGRAY);
        colors.add(Color.LTGRAY);
        colors.add(Color.MAGENTA);
    }
    public void buildColumns(List<AssemblyBoard> result)
    {
        List<SubEntry> subEntries=new ArrayList<>();
        for(AssemblyBoard board:result)
        {
            if(!aximBottoms.contains(board.getBadTypeInfo()))
            {
                aximBottoms.add(board.getBadTypeInfo());
                badTypedetails.add(board.getBadTypeDetail());
                if(!subEntries.isEmpty())
                {
                    columns.add(subEntries);
                    subEntries=new ArrayList<>();
                }
            }
            if(!productModels.contains(board.getProductModel()))
                productModels.add(board.getProductModel());
            SubEntry subEntry
                    =new SubEntry(board.getProductModel(),board.getCodeCount());
            subEntry.setTypeDetails(board.getBadTypeDetail());
            subEntry.setRemark(board.getRemark());
            subEntries.add(subEntry);
        }
        if(!subEntries.isEmpty())
        {
            columns.add(subEntries);
        }
        addColor(productModels.size());
        for(int columnIndex=0; columnIndex<aximBottoms.size();columnIndex++)
        {
            List<SubcolumnValue> subcolumnValues=new ArrayList<>();
            List<SubEntry> subEntryList=columns.get(columnIndex);
            for(SubEntry subEntry:subEntryList)
                subcolumnValues
                        .add(new SubcolumnValue(subEntry.getCount(),getColorOfModel(subEntry.getProductModel())));
            maxSubColumn
                    =Math.max(subcolumnValues.size(),maxSubColumn);
            Column column = new Column(subcolumnValues);
            column.setHasLabels(true);
            columnList.add(column);
        }
    }

    public List<AxisValue> getAxisBottoms()
    {
        List<AxisValue> axisValues=new ArrayList<AxisValue>();
        for(int j=0;j<aximBottoms.size();j++)
        {
            axisValues.add(new AxisValue(j).setLabel(aximBottoms.get(j)));
        }
        return axisValues;
    }
    public int getColorOfModel(String model) {
        int index=productModels.indexOf(model);
        if(index<0||index>=colors.size())
            index=0;
        return colors.get(index);
    }

    public int getMaxSubSize() {
        return maxSubColumn;
    }

    public void setMaxSubColumn(int maxSubColumn) {
        this.maxSubColumn = maxSubColumn;
    }

    /**
     * 根据 length 补加颜色
     * @param length
     */
    private void addColor(int length) {
        length=length- colors.size();
        if(length<1)
            return;
        Random random = new Random();
        for(int i=0;i<length;i++)
        {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            colors.add(Color.rgb(r,g,b));
        }
    }

    public List<List<SubEntry>> getColumns() {
        return columns;
    }

    public void setColumns(List<List<SubEntry>> columns) {
        this.columns = columns;
    }

    public List<String> getBadTypedetails() {
        return badTypedetails;
    }

    public void setBadTypedetails(List<String> badTypedetails) {
        this.badTypedetails = badTypedetails;
    }

    public void setAximBottoms(List<String> aximBottoms) {
        this.aximBottoms = aximBottoms;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<String> getProductModels() {
        return productModels;
    }

    public void setProductModels(List<String> productModels) {
        this.productModels = productModels;
    }

    public String getDialogMsg(int columnIndex, int subcolumnIndex) {
        String result="显示具体异常";
        try {
            SubEntry subEntry=columns.get(columnIndex).get(subcolumnIndex);
            result=subEntry.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
