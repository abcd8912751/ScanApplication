package com.furja.qc.beans;

/**
 * 主要设置请求次数的,默认超出三次即为over将Toast网络异常
 */

public class RequestLog {
    private int errorCount;
    private int limit;
    public RequestLog()
    {
        initValue(3);
    }

    public RequestLog(int limitCount)
    {
        initValue(limitCount);
    }

    public boolean isOverTimes()
    {
        if(errorCount <limit)
            return false;
        return true;
    }



    /**
     * 请求失败将请求次数加1
     */
    public void error()
    {
        this.errorCount = errorCount +1;
    }

    /**
     * 初始化数值
     */
    public void initValue(int value)
    {
        this.limit=value;
        this.errorCount =0;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isLastTimes() {
        return (limit-errorCount)==1;
    }
}
