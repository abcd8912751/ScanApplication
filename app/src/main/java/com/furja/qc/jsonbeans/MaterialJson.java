package com.furja.qc.jsonbeans;

import java.util.List;

/**
 * 从http://www.nbfurja.com/FJCommonInterface/GetBarCodeInfo/
 * ?BarCode=70030110076140009T获取Json
 */

public class MaterialJson {


    /**
     * ErrCode : 100
     * ErrMsg : 条码信息获取成功!
     * ErrData : {"FBCDate":"2019-04-28 00:00:00","FItemID":"54119","FNumber":"2.520.201.5202120101","FShortNumber":"5202120101","FName":"NV350新电机罩小扣(271C)","FModel":"ABS747","FQty":590,"FTranType":"注塑","FSCDNO":"190410-239","FBatteryBarCodeNO1":null,"FBatteryBarCodeNO2":null,"FMaterialDescription":null,"MSL":null,"FUrl":[{"url":"https://www.nbfurja.com:7070/Image/5202120101/图片8.png"},{"url":"https://www.nbfurja.com:7070/Image/5202120101/图片9.png"}],"FGrossWeight":"0.9000000000"}
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
         * FBCDate : 2019-04-28 00:00:00
         * FItemID : 54119
         * FNumber : 2.520.201.5202120101
         * FShortNumber : 5202120101
         * FName : NV350新电机罩小扣(271C)
         * FModel : ABS747
         * FQty : 590
         * FTranType : 注塑
         * FSCDNO : 190410-239
         * FBatteryBarCodeNO1 : null
         * FBatteryBarCodeNO2 : null
         * FMaterialDescription : null
         * MSL : null
         * FUrl : [{"url":"https://www.nbfurja.com:7070/Image/5202120101/图片8.png"},{"url":"https://www.nbfurja.com:7070/Image/5202120101/图片9.png"}]
         * FGrossWeight : 0.9000000000
         */

        private String FBCDate;
        private String FItemID;
        private String FNumber;
        private String FShortNumber;
        private String FName;
        private String FModel;
        private int FQty;
        private String FTranType;
        private String FSCDNO;
        private Object FBatteryBarCodeNO1;
        private Object FBatteryBarCodeNO2;
        private Object FMaterialDescription;
        private Object MSL;
        private String FGrossWeight;
        private List<FUrlBean> FUrl;

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

        public String getFSCDNO() {
            return FSCDNO;
        }

        public void setFSCDNO(String FSCDNO) {
            this.FSCDNO = FSCDNO;
        }

        public Object getFBatteryBarCodeNO1() {
            return FBatteryBarCodeNO1;
        }

        public void setFBatteryBarCodeNO1(Object FBatteryBarCodeNO1) {
            this.FBatteryBarCodeNO1 = FBatteryBarCodeNO1;
        }

        public Object getFBatteryBarCodeNO2() {
            return FBatteryBarCodeNO2;
        }

        public void setFBatteryBarCodeNO2(Object FBatteryBarCodeNO2) {
            this.FBatteryBarCodeNO2 = FBatteryBarCodeNO2;
        }

        public Object getFMaterialDescription() {
            return FMaterialDescription;
        }

        public void setFMaterialDescription(Object FMaterialDescription) {
            this.FMaterialDescription = FMaterialDescription;
        }

        public Object getMSL() {
            return MSL;
        }

        public void setMSL(Object MSL) {
            this.MSL = MSL;
        }

        public String getFGrossWeight() {
            return FGrossWeight;
        }

        public void setFGrossWeight(String FGrossWeight) {
            this.FGrossWeight = FGrossWeight;
        }

        public List<FUrlBean> getFUrl() {
            return FUrl;
        }

        public void setFUrl(List<FUrlBean> FUrl) {
            this.FUrl = FUrl;
        }

        public static class FUrlBean {
            /**
             * url : https://www.nbfurja.com:7070/Image/5202120101/图片8.png
             */

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
