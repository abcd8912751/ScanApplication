package com.furja.qc.jsonbeans;

/**
 * 上传数据返回的JSON解析
 */

public class UploadDataJson {

    /**
     * ErrCode : 100
     * ErrMsg : 数据提交成功!
     * ErrData : true
     */

    private int ErrCode;
    private String ErrMsg;


    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int ErrCode) {
        this.ErrCode = ErrCode;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

}
