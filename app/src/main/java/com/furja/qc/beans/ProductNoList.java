package com.furja.qc.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 生产单号信息JSON
 */

public class ProductNoList {
    private List<IsCheckBean> IsCheck;
    private List<BCSCDHBean> BCSCDH;

    public List<IsCheckBean> getIsCheck() {
        return IsCheck;
    }

    public void setIsCheck(List<IsCheckBean> IsCheck) {
        this.IsCheck = IsCheck;
    }

    public List<BCSCDHBean> getBCSCDH() {
        return BCSCDH;
    }

    public void setBCSCDH(List<BCSCDHBean> BCSCDH) {
        this.BCSCDH = BCSCDH;
    }

    public static class IsCheckBean {
        /**
         * IsCheck : true
         */

        private String IsCheck;

        public String getIsCheck() {
            return IsCheck;
        }

        public void setIsCheck(String IsCheck) {
            this.IsCheck = IsCheck;
        }
    }

    public static class BCSCDHBean {
        /**
         * 生产单号 : 170102-001
         */

        private String productNo;
        @JSONField(name = "生产单号")
        public String getProductNo() {
            return productNo;
        }
        @JSONField(name = "生产单号")
        public void setProductNo(String productNo) {
            this.productNo = productNo;
        }
    }
}
