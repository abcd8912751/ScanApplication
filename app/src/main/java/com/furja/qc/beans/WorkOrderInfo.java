package com.furja.qc.beans;

/**
 * 提取WorkOrder的键值信息
 * 存储物料内码/操作员ID及工位号
 * 作为异常数据库键值传递
 */

public class WorkOrderInfo {
    private String materialISN;
    private String operatorId;
    private String workPlaceId;

    public WorkOrderInfo(String materialISN, String operatorId, String workPlaceId) {
        this.materialISN = materialISN;
        this.operatorId = operatorId;
        this.workPlaceId = workPlaceId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getWorkPlaceId() {
        return workPlaceId;
    }

    public void setWorkPlaceId(String workPlaceId) {
        this.workPlaceId = workPlaceId;
    }

    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }
}
