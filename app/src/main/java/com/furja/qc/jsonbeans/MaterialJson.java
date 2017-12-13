package com.furja.qc.jsonbeans;

/**
 * 从http://www.nbfurja.com/FJCommonInterface/GetBarCodeInfo/
 * ?BarCode=70030110076140009T获取Json
 */

public class MaterialJson {

    /**
     * ErrCode : 100
     * ErrMsg : 条码信息获取成功!
     * ErrData : {"FBCDate":"2017-05-23T00:00:00","FItemID":"110076","FNumber":"2.520.504.5305601401","FShortNumber":"5305601401","FName":"HV380地刷中接头(花岗岩灰)","FModel":"ABS747H","FQty":140,"FTranType":"注塑"}
     */

    private int ErrCode;
    private String ErrMsg;
    private ErrDataBean ErrData;

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

    public ErrDataBean getErrData() {
        return ErrData;
    }

    public void setErrData(ErrDataBean ErrData) {
        this.ErrData = ErrData;
    }

    public static class ErrDataBean {
        /**
         * FBCDate : 2017-05-23T00:00:00
         * FItemID : 110076
         * FNumber : 2.520.504.5305601401
         * FShortNumber : 5305601401
         * FName : HV380地刷中接头(花岗岩灰)
         * FModel : ABS747H
         * FQty : 140
         * FTranType : 注塑
         */

        private String FBCDate;
        private String FItemID;
        private String FNumber;
        private String FShortNumber;
        private String FName;
        private String FModel;
        private int FQty;
        private String FTranType;

        public String getFBCDate() {
            return FBCDate;
        }

        public void setFBCDate(String FBCDate) {
            this.FBCDate = FBCDate;
        }

        public String getFItemID() {
            return FItemID;
        }

        public void setFItemID(String FItemID) {
            this.FItemID = FItemID;
        }

        public String getFNumber() {
            return FNumber;
        }

        public void setFNumber(String FNumber) {
            this.FNumber = FNumber;
        }

        public String getFShortNumber() {
            return FShortNumber;
        }

        public void setFShortNumber(String FShortNumber) {
            this.FShortNumber = FShortNumber;
        }

        public String getFName() {
            return FName;
        }

        public void setFName(String FName) {
            this.FName = FName;
        }

        public String getFModel() {
            return FModel;
        }

        public void setFModel(String FModel) {
            this.FModel = FModel;
        }

        public int getFQty() {
            return FQty;
        }

        public void setFQty(int FQty) {
            this.FQty = FQty;
        }

        public String getFTranType() {
            return FTranType;
        }

        public void setFTranType(String FTranType) {
            this.FTranType = FTranType;
        }
    }
}
