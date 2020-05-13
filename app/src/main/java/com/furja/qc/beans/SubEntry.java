package com.furja.qc.beans;

public class SubEntry {
    private String productModel;
    private float count;
    private String typeDetails;
    private String remark;
    public SubEntry(String productModel, int count) {
        this.productModel = productModel;
        this.count = count;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(String typeDetails) {
        this.typeDetails = typeDetails;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    @Override
    public String toString() {
        String line_separater=System.getProperty("line.separator");
        return "产品型号: " + productModel + line_separater+
                "异常类型: " + typeDetails + line_separater+
                "异常明细: "+remark+line_separater+
                "相应数目: " + Float.valueOf(count).intValue() ;
    }
}
