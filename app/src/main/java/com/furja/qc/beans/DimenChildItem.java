package com.furja.qc.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import static com.furja.qc.utils.Constants.TYPE_RULER_GAUGE;

public class DimenChildItem implements MultiItemEntity {
    private int position;
    private double dimen;
    private double startDimen;  //通的dimen
    private double endDimen;    //止的dimen
    private double hardness;    //硬度
    private double flatness;    //平面度
    @JSONField(serialize = false)
    private int itemType;
    public DimenChildItem() {
        this.itemType= TYPE_RULER_GAUGE;
    }

    public DimenChildItem(int itemType) {
        this.itemType=itemType;
    }

    public DimenChildItem setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public double getHardness() {
        return hardness;
    }

    public void setHardness(double hardness) {
        this.hardness = hardness;
    }

    public double getFlatness() {
        return flatness;
    }

    public void setFlatness(double flatness) {
        this.flatness = flatness;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getDimen() {
        return dimen;
    }

    public void setDimen(double dimen) {
        this.dimen = dimen;
    }

    public double getStartDimen() {
        return startDimen;
    }

    public void setStartDimen(double startDimen) {
        this.startDimen = startDimen;
    }

    public double getEndDimen() {
        return endDimen;
    }

    public void setEndDimen(double endDimen) {
        this.endDimen = endDimen;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
