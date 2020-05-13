package com.furja.qc.databases;

import com.furja.qc.QcApplication;
import com.furja.qc.beans.DimenGroupItem;

import java.util.List;

/**
 * 尺寸测量记录实体
 */
public class DimenGaugeLog {
    private int FID;
    private String materialISN;
    private String operatorID;
    private String workplaceID;
    private String moldCavity;
    private String workingClass;
    private String produceDate;
    private String timePeriod;
    private String moldNo;
    private String type;
    private List<DimenGroupItem> dimenGroupItems;

    public DimenGaugeLog() {
        this.operatorID=QcApplication.getUserID();
    }
    public int getFID() {
        return FID;
    }

    public void setFID(int FID) {
        this.FID = FID;
    }

    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getWorkplaceID() {
        return workplaceID;
    }

    public void setWorkplaceID(String workplaceID) {
        this.workplaceID = workplaceID;
    }

    public String getMoldCavity() {
        return moldCavity;
    }

    public void setMoldCavity(String moldCavity) {
        this.moldCavity = moldCavity;
    }

    public String getWorkingClass() {
        return workingClass;
    }

    public void setWorkingClass(String workingClass) {
        this.workingClass = workingClass;
    }

    public List<DimenGroupItem> getDimenGroupItems() {
        return dimenGroupItems;
    }

    public void setDimenGroupItems(List<DimenGroupItem> dimenGroupItems) {
        this.dimenGroupItems = dimenGroupItems;
    }

    public String getMoldNo() {
        return moldNo;
    }

    public void setMoldNo(String moldNo) {
        this.moldNo = moldNo;
    }

    public String getProduceDate() {
        return produceDate;
    }

    public String getType() {
        return type;
    }

    public DimenGaugeLog setType(String type) {
        this.type = type;
        return this;
    }

    public void setProduceDate(String produceDate) {
        this.produceDate = produceDate;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setWorkingClass(boolean isJiaClass) {
        if(isJiaClass)
            setWorkingClass("甲班");
        else
            setWorkingClass("乙班");
    }
}
