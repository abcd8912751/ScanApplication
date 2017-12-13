package com.furja.qc.jsonbeans;

import java.util.List;

/**
 * 用于解析BadType基础信息的json
 */

public class BadTypeConfigJson {

    /**
     * ErrCode : 100
     * ErrMsg : 异常信息获取成功!
     * ErrData : [{"SourceType":1,"BadTypeID":0,"BadTypeInfo":"materialFloer","BadTypeDetail":"料花"},{"SourceType":1,"BadTypeID":1,"BadTypeInfo":"airMark","BadTypeDetail":"气纹"},{"SourceType":1,"BadTypeID":2,"BadTypeInfo":"shrinkage","BadTypeDetail":"缩水"},{"SourceType":1,"BadTypeID":3,"BadTypeInfo":"lackMaterial","BadTypeDetail":"缺料"},{"SourceType":1,"BadTypeID":4,"BadTypeInfo":"mixedColor","BadTypeDetail":"混色"},{"SourceType":1,"BadTypeID":5,"BadTypeInfo":"flowMark","BadTypeDetail":"夹水纹"},{"SourceType":1,"BadTypeID":6,"BadTypeInfo":"cutMaterial","BadTypeDetail":"划伤"},{"SourceType":1,"BadTypeID":7,"BadTypeInfo":"bumpMaterial","BadTypeDetail":"碰伤"},{"SourceType":1,"BadTypeID":8,"BadTypeInfo":"foreignMaterial","BadTypeDetail":"异物"},{"SourceType":1,"BadTypeID":9,"BadTypeInfo":"blackSpot","BadTypeDetail":"黑点"},{"SourceType":1,"BadTypeID":10,"BadTypeInfo":"whiteSpot","BadTypeDetail":"白点"},{"SourceType":1,"BadTypeID":11,"BadTypeInfo":"greasyDirt","BadTypeDetail":"油污"},{"SourceType":1,"BadTypeID":12,"BadTypeInfo":"rubberThread","BadTypeDetail":"胶丝"},{"SourceType":1,"BadTypeID":13,"BadTypeInfo":"distortion","BadTypeDetail":"变形"},{"SourceType":1,"BadTypeID":14,"BadTypeInfo":"tunePoor","BadTypeDetail":"调机不良"},{"SourceType":1,"BadTypeID":15,"BadTypeInfo":"other","BadTypeDetail":"其他"}]
     */

    private int ErrCode;
    private String ErrMsg;
    private List<ErrDataBean> ErrData;

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

    public List<ErrDataBean> getErrData() {
        return ErrData;
    }

    public void setErrData(List<ErrDataBean> ErrData) {
        this.ErrData = ErrData;
    }

    public static class ErrDataBean {
        /**
         * SourceType : 1
         * BadTypeID : 0
         * BadTypeInfo : materialFloer
         * BadTypeDetail : 料花
         */

        private int SourceType;
        private int BadTypeID;
        private String BadTypeInfo;
        private String BadTypeDetail;

        public int getSourceType() {
            return SourceType;
        }

        public void setSourceType(int SourceType) {
            this.SourceType = SourceType;
        }

        public int getBadTypeID() {
            return BadTypeID;
        }

        public void setBadTypeID(int BadTypeID) {
            this.BadTypeID = BadTypeID;
        }

        public String getBadTypeInfo() {
            return BadTypeInfo;
        }

        public void setBadTypeInfo(String BadTypeInfo) {
            this.BadTypeInfo = BadTypeInfo;
        }

        public String getBadTypeDetail() {
            return BadTypeDetail;
        }

        public void setBadTypeDetail(String BadTypeDetail) {
            this.BadTypeDetail = BadTypeDetail;
        }
    }
}
