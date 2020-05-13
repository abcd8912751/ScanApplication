package com.furja.qc.beans;

/**
 * KeyViewHolder使用的实例.
 */
public class BadLogEntry {
    private String badCode; //异常代码
    private long codeCount=0; //针对异常代码的计数
    private String remark;    //对应的备注

    public BadLogEntry() {
    }

    public BadLogEntry(String badCode, long codeCount) {
        this.badCode = badCode;
        this.codeCount = codeCount;
    }
    public BadLogEntry(int badCodeIndex, long codeCount) {
        this.badCode = badCodeIndex+"";
        this.codeCount = codeCount;
    }
    public void addCount()
    {
        setCodeCount(this.codeCount+1);
    }

    public String getBadCode() {
        return badCode;
    }

    public void setBadCode(String badCode) {
        this.badCode = badCode;
    }

    public long getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(long codeCount) {
        this.codeCount = codeCount;
    }

    public String getRemark() {
        return remark;
    }

    public BadLogEntry setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
