package com.furja.qc.jsonbeans;

import java.util.List;

/**
 * 异常类型的JSON解析实体
 */

public class BadBeanJSON {

    /**
     * ErrCode : 100
     * ErrMsg : 异常统计信息获取成功!
     * ErrData : [{"CheckDate":"2018-01-02","FNumber":"5303350102","FName":"NV350电机罩-改模后(花岗岩灰50%珠光)","FModel":"ABS121","BadType":"气纹","Machine":"D03","Count":36},{"CheckDate":"2018-01-02","FNumber":"5303350102","FName":"NV350电机罩-改模后(花岗岩灰50%珠光)","FModel":"ABS121","BadType":"缺料","Machine":"D03","Count":36},{"CheckDate":"2018-01-02","FNumber":"5303350102","FName":"NV350电机罩-改模后(花岗岩灰50%珠光)","FModel":"ABS121","BadType":"料花","Machine":"D03","Count":329},{"CheckDate":"2018-01-02","FNumber":"5303350102","FName":"NV350电机罩-改模后(花岗岩灰50%珠光)","FModel":"ABS121","BadType":"混色","Machine":"D04","Count":2},{"CheckDate":"2018-01-02","FNumber":"5202370101","FName":"NV350电机罩(鲨鱼白)","FModel":"ABS 750/121鲨鱼白","BadType":"杂料","Machine":"D04","Count":3},{"CheckDate":"2018-01-02","FNumber":"5202370101","FName":"NV350电机罩(鲨鱼白)","FModel":"ABS 750/121鲨鱼白","BadType":"混色","Machine":"D04","Count":5},{"CheckDate":"2018-01-02","FNumber":"5202370101","FName":"NV350电机罩(鲨鱼白)","FModel":"ABS 750/121鲨鱼白","BadType":"油污","Machine":"D04","Count":21},{"CheckDate":"2018-01-02","FNumber":"5202370101","FName":"NV350电机罩(鲨鱼白)","FModel":"ABS 750/121鲨鱼白","BadType":"烧焦","Machine":"D04","Count":50},{"CheckDate":"2018-01-02","FNumber":"5202370101","FName":"NV350电机罩(鲨鱼白)","FModel":"ABS 750/121鲨鱼白","BadType":"碰伤","Machine":"D04","Count":105},{"CheckDate":"2018-01-02","FNumber":"5303550201","FName":"NV470地刷底座(花岗岩灰50%珠光)-加胶","FModel":"ABS750/121","BadType":"混色","Machine":"D05","Count":21},{"CheckDate":"2018-01-02","FNumber":"5268980101","FName":"NV470地刷上盖(花岗岩灰带50%的珠光)","FModel":"ABS750","BadType":"料花","Machine":"D06","Count":26},{"CheckDate":"2018-01-02","FNumber":"5268980101","FName":"NV470地刷上盖(花岗岩灰带50%的珠光)","FModel":"ABS750","BadType":"混色","Machine":"D06","Count":70},{"CheckDate":"2018-01-02","FNumber":"5268980101","FName":"NV470地刷上盖(花岗岩灰带50%的珠光)","FModel":"ABS750","BadType":"缺料","Machine":"D06","Count":205},{"CheckDate":"2018-01-02","FNumber":"5305390204","FName":"HV380地刷底座(灰白色)改模","FModel":"ABS747","BadType":"水口修整","Machine":"D07","Count":28},{"CheckDate":"2018-01-02","FNumber":"5305390204","FName":"HV380地刷底座(灰白色)改模","FModel":"ABS747","BadType":"白芯","Machine":"D07","Count":238},{"CheckDate":"2018-01-02","FNumber":"5300290101","FName":"NV470地刷装饰板2(本色)","FModel":"ABS750","BadType":"水口修整","Machine":"D09","Count":32},{"CheckDate":"2018-01-02","FNumber":"5300290101","FName":"NV470地刷装饰板2(本色)","FModel":"ABS750","BadType":"碰伤","Machine":"D09","Count":46},{"CheckDate":"2018-01-02","FNumber":"5233350101","FName":"NV470地刷透明窗(透明)","FModel":"透明ABS TR558A","BadType":"碰伤","Machine":"D18","Count":4},{"CheckDate":"2018-01-02","FNumber":"5233350101","FName":"NV470地刷透明窗(透明)","FModel":"透明ABS TR558A","BadType":"缺料","Machine":"D18","Count":21},{"CheckDate":"2018-01-02","FNumber":"5240560101","FName":"HV300小电动地刷上盖(DC35灰)","FModel":"ABS747","BadType":"冷料","Machine":"D22","Count":22},{"CheckDate":"2018-01-02","FNumber":"5303140101","FName":"UV540左右手柄(花岗岩灰50%珠光+花岗岩灰50%珠光)","FModel":"ABS PA-747(+)","BadType":"水口修整","Machine":"D23","Count":15},{"CheckDate":"2018-01-02","FNumber":"5303140101","FName":"UV540左右手柄(花岗岩灰50%珠光+花岗岩灰50%珠光)","FModel":"ABS PA-747(+)","BadType":"冷料","Machine":"D23","Count":90},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"料花","Machine":"D36","Count":44},{"CheckDate":"2018-01-02","FNumber":"5303350102","FName":"NV350电机罩-改模后(花岗岩灰50%珠光)","FModel":"ABS121","BadType":"混色","Machine":"DO4","Count":12},{"CheckDate":"2018-01-02","FNumber":"5303550201","FName":"NV470地刷底座(花岗岩灰50%珠光)-加胶","FModel":"ABS750/121","BadType":"混色","Machine":"DO5","Count":12},{"CheckDate":"2018-01-02","FNumber":"5268980101","FName":"NV470地刷上盖(花岗岩灰带50%的珠光)","FModel":"ABS750","BadType":"混色","Machine":"DO6","Count":18},{"CheckDate":"2018-01-02","FNumber":"5305140404","FName":"HV380地刷上盖-改模后20170821(木炭灰)","FModel":"ABS750","BadType":"料花","Machine":"E19","Count":33},{"CheckDate":"2018-01-02","FNumber":"5305140404","FName":"HV380地刷上盖-改模后20170821(木炭灰)","FModel":"ABS750","BadType":"料花","Machine":"E21","Count":110},{"CheckDate":"2018-01-02","FNumber":"5208410102","FName":"NV355地刷上盖(鲨鱼白）-优化","FModel":"ABS 750/121鲨鱼白","BadType":"油污","Machine":"E22","Count":33},{"CheckDate":"2018-01-02","FNumber":"5314110301","FName":"NV480Y手柄连接管（本色）","FModel":"ABS747","BadType":"缺料","Machine":"E24","Count":21},{"CheckDate":"2018-01-02","FNumber":"5304210101","FName":"NV350左右手柄（可调）2#3#4#5#6#(宝马蓝+宝马蓝)","FModel":"ABS747","BadType":"水口修整","Machine":"E25","Count":33},{"CheckDate":"2018-01-02","FNumber":"5203490201","FName":"NV350地刷底座(9C)-优化","FModel":"ABS750/121","BadType":"其他","Machine":"E28","Count":1},{"CheckDate":"2018-01-02","FNumber":"5203490201","FName":"NV350地刷底座(9C)-优化","FModel":"ABS750/121","BadType":"水口修整","Machine":"E28","Count":6},{"CheckDate":"2018-01-02","FNumber":"5203490301","FName":"NV350地刷底座(10397C)-优化","FModel":"ABS750/121","BadType":"缺料","Machine":"E28","Count":8},{"CheckDate":"2018-01-02","FNumber":"5203490701","FName":"NV350地刷底座(DC35灰)-优化","FModel":"ABS750/121","BadType":"缺料","Machine":"E29","Count":136},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"碰伤","Machine":"E30","Count":8},{"CheckDate":"2018-01-02","FNumber":"5221050101","FName":"NV100尘杯(透明)","FModel":"透明ABS TR588A","BadType":"杂料","Machine":"E34","Count":13},{"CheckDate":"2018-01-02","FNumber":"5221050101","FName":"NV100尘杯(透明)","FModel":"透明ABS TR588A","BadType":"混色","Machine":"E34","Count":28},{"CheckDate":"2018-01-02","FNumber":"5221050101","FName":"NV100尘杯(透明)","FModel":"透明ABS TR588A","BadType":"其他","Machine":"E34","Count":50},{"CheckDate":"2018-01-02","FNumber":"5221050101","FName":"NV100尘杯(透明)","FModel":"透明ABS TR588A","BadType":"料花","Machine":"E34","Count":88},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"碰伤","Machine":"E36","Count":15},{"CheckDate":"2018-01-02","FNumber":"5221050101","FName":"NV100尘杯(透明)","FModel":"透明ABS TR588A","BadType":"料花","Machine":"E36","Count":40},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"流痕","Machine":"E36","Count":45},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"热光影","Machine":"E36","Count":72},{"CheckDate":"2018-01-02","FNumber":"5236980105","FName":"HV300尘杯A(透明)-改模","FModel":"透明ABS\r\nTR558A","BadType":"料花","Machine":"E36","Count":405}]
     */

    private int ErrCode;
    private List<ErrDataBean> ErrData;

    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int ErrCode) {
        this.ErrCode = ErrCode;
    }

    public List<ErrDataBean> getErrData() {
        return ErrData;
    }

    public void setErrData(List<ErrDataBean> ErrData) {
        this.ErrData = ErrData;
    }

    public static class ErrDataBean {
        /**
         * CheckDate : 2018-01-02
         * FNumber : 5303350102
         * FName : NV350电机罩-改模后(花岗岩灰50%珠光)
         * FModel : ABS121
         * BadType : 气纹
         * Machine : D03
         * Count : 36
         */

        private String CheckDate;
        private String FNumber;
        private String FName;
        private String FModel;
        private String BadType;
        private String Machine;
        private int Count;

        public String getCheckDate() {
            return CheckDate;
        }

        public void setCheckDate(String CheckDate) {
            this.CheckDate = CheckDate;
        }

        public String getFNumber() {
            return FNumber;
        }

        public void setFNumber(String FNumber) {
            this.FNumber = FNumber;
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

        public String getBadType() {
            return BadType;
        }

        public void setBadType(String BadType) {
            this.BadType = BadType;
        }

        public String getMachine() {
            return Machine;
        }

        public void setMachine(String Machine) {
            this.Machine = Machine;
        }

        public int getCount() {
            return Count;
        }

        public void setCount(int Count) {
            this.Count = Count;
        }

        @Override
        public String toString() {
            return "物料 " + FName +" 的规格是 "+FModel+ ",其异常类型为 "  +BadType +
                    " 的数目是:" + Count;
        }
    }
}
