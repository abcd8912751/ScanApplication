package com.furja.qc.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public enum TimePeriodDaily {
    PEROID_1(1),PEROID_2(2),PEROID_3(3),
    PEROID_4(4),PEROID_5(5),PEROID_6(6),
    PEROID_7(7),PEROID_8(8),PEROID_9(9),
    PEROID_10(10),PEROID_11(11),PEROID_12(12),
    PEROID_13(13),PEROID_14(14),PEROID_15(15),
    PEROID_16(16),PEROID_17(17),PEROID_18(18);
    private TimePeriod timePeroid;
    private TimePeriodDaily(int peroidSign)
    {
        this.timePeroid=new TimePeriod(peroidSign);
    }

    /**
     * 获取当前所属时段,参数为真则是获取 2小时一划分的时段,为假便获取4小时一划分的时段
     * @param splitTwoHours
     * @return
     */
    public static TimePeriodDaily getNowPeroid(boolean splitTwoHours) {
        Calendar calendar=Calendar.getInstance();
        for(TimePeriodDaily timePeriodDaily :TimePeriodDaily.values())
        {
            TimePeriod timePeriod= timePeriodDaily.timePeroid;
            if(timePeriod.contains(calendar)) {
                if(splitTwoHours) {
                    if (timePeriod.getPeroidSign() > 12)
                        continue;
                }
                else
                    if(timePeriod.getPeroidSign()<13)
                        continue;
                return timePeriodDaily;
            }
        }
        return PEROID_1;
    }

    public static String getTimePeroid(boolean splitTwoHours) {
        return getNowPeroid(splitTwoHours).timePeroid.getPeroid();
    }
    public static List<String> getPeroidLst(boolean splitTwoHours)
    {
        List<String> arrays=new ArrayList<>();
        for(TimePeriodDaily timePeriodDaily :TimePeriodDaily.values())
        {
            TimePeriod timePeriod= timePeriodDaily.timePeroid;
            if(splitTwoHours) {
                if (timePeriod.getPeroidSign() > 12)
                    continue;
            }
            else
                if(timePeriod.getPeroidSign()<13)
                    continue;
            arrays.add(timePeriodDaily.timePeroid.getPeroid());
        }
        return arrays;
    }
    public static TimePeriodDaily formatPeroid(String timePeroid)
    {
        for(TimePeriodDaily timePeriodDaily :TimePeriodDaily.values())
        {
            TimePeriod period= timePeriodDaily.timePeroid;
            if(period.getPeroid().equals(timePeroid))
                return timePeriodDaily;
        }
        return PEROID_1;
    }

    @Override
    public String toString() {
        return timePeroid.getPeroid();
    }

    public int getIndex()
    {
        return Math.max(timePeroid.getPeroidSign()-1,0);
    }

    public TimePeriod getTimePeroid() {
        return timePeroid;
    }
}
