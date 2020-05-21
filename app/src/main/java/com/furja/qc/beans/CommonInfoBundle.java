package com.furja.qc.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.furja.qc.beans.MaterialInfo;

public class CommonInfoBundle implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.materialInfo, flags);
        dest.writeString(this.moldNo);
        dest.writeString(this.workingClass);
        dest.writeString(this.workplace);
        dest.writeString(this.moldCavity);
    }

    protected CommonInfoBundle(Parcel in) {
        this.materialInfo = in.readParcelable(MaterialInfo.class.getClassLoader());
        this.moldNo = in.readString();
        this.workingClass = in.readString();
        this.workplace = in.readString();
        this.moldCavity = in.readString();
    }

    public static final Parcelable.Creator<CommonInfoBundle> CREATOR = new Parcelable.Creator<CommonInfoBundle>() {
        @Override
        public CommonInfoBundle createFromParcel(Parcel source) {
            return new CommonInfoBundle(source);
        }

        @Override
        public CommonInfoBundle[] newArray(int size) {
            return new CommonInfoBundle[size];
        }
    };
}
