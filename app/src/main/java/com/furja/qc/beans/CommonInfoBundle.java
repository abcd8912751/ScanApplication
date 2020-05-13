package com.furja.qc.beans;

import com.furja.qc.beans.MaterialInfo;

public class CommonInfoBundle {
    private MaterialInfo materialInfo;
    private String moldNo;
    private String workingClass;
    private String workplace;
    private String moldCavity;

    public CommonInfoBundle() {
    }

    public MaterialInfo getMaterialInfo() {
        return materialInfo;
    }

    public void setMaterialInfo(MaterialInfo materialInfo) {
        this.materialInfo = materialInfo;
    }

    public String getMoldNo() {
        return moldNo;
    }

    public void setMoldNo(String moldNo) {
        this.moldNo = moldNo;
    }

    public String getWorkingClass() {
        return workingClass;
    }

    public void setWorkingClass(String workingClass) {
        this.workingClass = workingClass;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getMoldCavity() {
        return moldCavity;
    }

    public void setMoldCavity(String moldCavity) {
        this.moldCavity = moldCavity;
    }
}
