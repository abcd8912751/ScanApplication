package com.furja.qc.beans;

import java.util.Calendar;
import java.util.Date;

import static com.furja.qc.utils.Utils.showLog;

public class TimePeriod {
    int peroidSign;
    String peroid;

    public TimePeriod(int peroidSign) {
        this.peroidSign = peroidSign;
        switch (peroidSign)
        {
            case 1: this.peroid="01:30-03:30";break;
            case 2: this.peroid="03:30-05:30";break;
            case 3: this.peroid="05:30-07:30";break;
            case 4: this.peroid="07:30-09:30";break;
            case 5: this.peroid="09:30-11:30";break;
            case 6: this.peroid="11:30-13:30";break;
            case 7: this.peroid="13:30-15:30";break;
            case 8: this.peroid="15:30-17:30";break;
            case 9: this.peroid="17:30-19:30";break;
            case 10:this.peroid="19:30-21:30";break;
            case 11:this.peroid="21:30-23:30";break;
            case 12:this.peroid="23:30-01:30";break;
            case 13:this.peroid="03:30-07:30";break;
            case 14:this.peroid="07:30-11:30";break;
            case 15:this.peroid="11:30-15:30";break;
            case 16:this.peroid="15:30-19:30";break;
            case 17:this.peroid="19:30-23:30";break;
            case 18:this.peroid="23:30-03:30";break;
        }
    }

    public Calendar getEndDate()
    {
        String[] time=peroid.split("-");
        String endHour=time[1].substring(0,2);
        String endMinute=time[1].substring(3);
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(endHour));
        calendar.set(Calendar.MINUTE,Integer.valueOf(endMinute));
        return calendar;
    }

    public boolean contains(Calendar nowCalendar)
    {
        Calendar calendar=getEndDate();
        if(calendar.before(nowCalendar))
            return false;
        if(peroidSign<13)   //1545204754438
            calendar.add(Calendar.HOUR_OF_DAY,-2);
        else
            calendar.add(Calendar.HOUR_OF_DAY,-4);
        if(calendar.before(nowCalendar))
            return true;
        return false;
    }

    public TimePeriod(int peroidSign, String peroid) {
        this.peroidSign = peroidSign;
        this.peroid = peroid;
    }

    public int getPeroidSign() {
        return peroidSign;
    }

    public void setPeroidSign(int peroidSign) {
        this.peroidSign = peroidSign;
    }

    public String getPeroid() {
        return peroid;
    }

    public void setPeroid(String peroid) {
        this.peroid = peroid;
    }
}
