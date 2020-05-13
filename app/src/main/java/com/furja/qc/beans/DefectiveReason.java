package com.furja.qc.beans;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public enum DefectiveReason {
    MATERIAL("原材料"),MOLDING_CONDITION("成型条件"),ENVIRONMENT("环境"),
    MOLD("模具"),HUMAN_LOSSES("人为漏失"),EQUIPMENT("机台设备"),OPERATION_METHOD("操作方法");
  private final String reason;
    private DefectiveReason(String reason) {
        this.reason = reason;
    }



    public static List<String> strings(){
        List<String> strings=new ArrayList<>();
        for(DefectiveReason reason:values()){
            strings.add(reason.getReason());
        }
        return strings;
    }

    @NonNull
    public static String getReasonOfDefetive(Integer[] whichArray) {
        String reason = "";
        for (int index : whichArray) {
            reason = reason + getReason(index) + "/";
        }
        if(reason.length()>0)
            reason = reason.substring(0, reason.length() - 1);
        return reason;
    }

    public static Integer[] getIntegerArray(String reasonOfDefective) {
        String[] reasons=reasonOfDefective.split("/");
        Integer[] array=new Integer[reasons.length];
        int index=0;
        for (String reason:reasons) {
            array[index++]=getIndex(reason);
        }
        return array;
    }

    public static int getIndex(String reason){
        List<String> stringLs=strings();
        return stringLs.indexOf(reason);
    }

    public String getReason()
    {
        return reason;
    }

    public static String getReason(int index)
    {
        List<String> stringLs=strings();
        if (index<0||index>= stringLs.size())
            return ENVIRONMENT.getReason();
        else
            return stringLs.get(index);
    }
}
