package com.furja.qc.databases;

import android.text.TextUtils;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * GreenDa存储List数组所需转换器
 */

public class StringConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if(TextUtils.isEmpty(databaseValue))
            return null;
        else
        {
            List<String> lst=new ArrayList<String>();
            String[] strings=databaseValue.split(",");
            for(String string:strings)
                lst.add(string);
            return lst;
        }
    }



    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        StringBuffer stringBuffer=new StringBuffer();
        if(entityProperty==null)
            return null;
        else
        {
            for(String entity:entityProperty)
            {
                stringBuffer.append(entity);
                stringBuffer.append(",");
            }
            if(stringBuffer.length()>0)
                stringBuffer.deleteCharAt(stringBuffer.length()-1);
            return stringBuffer.toString();
        }
    }
}
/**
    private long materialFloer;
    private long airMark;
    private long shrinkAge;
    private long lackMaterial;
    private long mixedColor;
    private long flowMark;
    private long cutMaterial;
    private long bumpMaterial;
    private long foreignMaterial;
    private long blackSpot;
    private long whiteSpot;
    private long greasyDirt;
    private long rubberThread;
    private long distortion;
    private long tunePoor;
    private long other;
    **/