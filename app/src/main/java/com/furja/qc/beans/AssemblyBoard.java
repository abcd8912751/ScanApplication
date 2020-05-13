package com.furja.qc.beans;

/**
 * 装配看板实例
 */

public class AssemblyBoard {
    private String ProductModel;
    private String BadTypeDetail;
    private String BadTypeInfo;
    private int CodeCount;
    private String Remark;
    private String FDate;

    public String getProductModel() {
        return ProductModel;
    }

    public void setProductModel(String productModel) {
        ProductModel = productModel;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getBadTypeDetail() {
        return BadTypeDetail;
    }

    public void setBadTypeDetail(String badTypeDetail) {
        BadTypeDetail = badTypeDetail;
    }

    public int getCodeCount() {
        return CodeCount;
    }

    public void setCodeCount(int codeCount) {
        CodeCount = codeCount;
    }

    public String getBadTypeInfo() {
        return BadTypeInfo;
    }

    public void setBadTypeInfo(String badTypeInfo) {
        BadTypeInfo = badTypeInfo;
    }

    public String getFDate() {
        return FDate;
    }

    public void setFDate(String FDate) {
        this.FDate = FDate;
    }
}
