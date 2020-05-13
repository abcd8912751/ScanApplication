package com.furja.qc.databases;

import com.furja.qc.QcApplication;
import com.furja.qc.beans.BadLogEntry;

import java.util.List;


public class BadAssemblyLog {
    private String qcPersonnel;   //质检人员
    private String materialISN;
    private String operator;
    private String lineNumber;
    private String productModel;
    private String note;
    private List<BadLogEntry> badLogEntries;


    public BadAssemblyLog() {
        this.setMaterialISN("");
        this.setNote("");
        this.qcPersonnel= QcApplication.getUserID();
    }


    public String getQcPersonnel() {
        return qcPersonnel;
    }

    public void setQcPersonnel(String qcPersonnel) {
        this.qcPersonnel = qcPersonnel;
    }

    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<BadLogEntry> getBadLogEntries() {
        return badLogEntries;
    }

    public void setBadLogEntries(List<BadLogEntry> badLogEntries) {
        this.badLogEntries = badLogEntries;
    }

}
