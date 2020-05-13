package com.furja.qc;

import com.furja.qc.beans.DimenChildItem;
import com.furja.qc.beans.TimePeriod;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import static com.furja.qc.utils.Utils.showToast;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void test() throws Exception {

    }



    public String getTwoChars(int value) {
        if(value<10)
            return "0"+value;
        else
            return value+"";
    }

    int binarySearch(long[] array, long value) {
        int lo = 0;
        int hi = array.length - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final long midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }
    public <T> void showLog(T object)
    {
        System.out.println(object+"");
    }
}