package com.furja.qc.beans;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.ArrayList;
import java.util.List;
import static com.furja.qc.utils.Constants.TYPE_RULER_GAUGE;
import static com.furja.qc.utils.Utils.showToast;

public class DimenGroupItem {

    private boolean isOk;
    private double upperLimit;  //上限
    private double lowerLimit;  //下限
    private double standardDimen;   //规格起始
    private double offset;
    private double upperOffset;     //上公差
    private double lowerOffset;     //下公差
    private int gaugeType;
    private List<DimenChildItem> childItems;
    private String dimenJson;
    @JSONField(serialize = false)
    private static String interString="±";  //加减分割符
    public DimenGroupItem() {
        this.isOk=true;
        this.childItems=new ArrayList<>();
        childItems.add(new DimenChildItem().setItemType(TYPE_RULER_GAUGE));
        childItems.add(new DimenChildItem().setItemType(TYPE_RULER_GAUGE));
        childItems.add(new DimenChildItem().setItemType(TYPE_RULER_GAUGE));
    }


    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public List<DimenChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<DimenChildItem> childItems) {
        this.childItems = childItems;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public double getStandardDimen() {
        return standardDimen;
    }

    public void setStandardDimen(double standardDimen) {
        this.standardDimen = standardDimen;
    }

    public double getUpperOffset() {
        return upperOffset;
    }

    public void setUpperOffset(double upperOffset) {
        this.upperOffset = upperOffset;
    }

    public double getLowerOffset() {
        return lowerOffset;
    }

    public void setLowerOffset(double lowerOffset) {
        this.lowerOffset = lowerOffset;
    }


    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public int getGaugeType() {
        return gaugeType;
    }

    public void setGaugeType(int gaugeType) {
        this.gaugeType = gaugeType;
    }

    public String getDimenJson() {
        return dimenJson;
    }

    public void setDimenJson(String dimenJson) {
        this.dimenJson = dimenJson;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @JSONField(serialize = false)
    public boolean hasInvalidValue() {
        int maxValue = 10000000;
        if(standardDimen>maxValue || upperOffset>maxValue
            ||lowerOffset>maxValue ||upperLimit>maxValue||lowerLimit>maxValue) {
            showToast("输入的规格、公差或上下限过大");
            return true;
        }
        return false;
    }
}
