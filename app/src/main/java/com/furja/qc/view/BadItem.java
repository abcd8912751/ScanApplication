package com.furja.qc.view;

/**
 * KeyViewHolder使用的实例.
 */

public class BadItem {
    private String badCode; //异常代码
    private long codeCount=0; //针对异常代码的计数

    public BadItem(String badCode, long codeCount) {
        this.badCode = badCode;
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
}
