package com.furja.qc.databases;

import com.alibaba.fastjson.JSON;
import com.furja.qc.QcApplication;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.jsonbeans.UploadDataJson;
import com.furja.qc.utils.Constants;
import com.furja.qc.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.furja.qc.utils.Constants.FURJA_UPLOAD_URL;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;
import static com.furja.qc.utils.Utils.textOf;

/**
 * GreenDao的品质异常类型基类 数据自动上传功能用途较少
 *为防止数据多次上传,于2018-8-11日改为直接删除本地数据不予自动上传
 */

public class BadMaterialLog {
    private Long id;


    private int sourceType;  // 1表示btn录入的数据,2表示key录入的数据,区分不同场景
    private String qcPersonnel;   //质检人员
    private String materialISN;
    private String operatorId;
    private String workPlaceId;
    private Date collectionDate;
    //每一个异常的代码,ButtonView使用ID,KeyView使用字母数字组成的代码
    private List<String> badTypeCode;
    //各异常类型(badTypeCode)的计数集
    private List<String> badCodeCount;

    private long badCount;  //集合,究竟有多少次品


    private boolean isUploaded;



    public BadMaterialLog(WorkOrderInfo workOrderInfo, int sourceType, List<Long> markCounts, long BadCount) {
        this.id=null;
        setWorkOrderInfo(workOrderInfo);
        setSourceType(sourceType);
        this.qcPersonnel= QcApplication.getUserID();
        setLongBadCounts(markCounts);
        this.badCount = BadCount;
        this.collectionDate= Calendar.getInstance().getTime();
        setIsUploaded(false);
    }


    public BadMaterialLog(WorkOrderInfo workOrderInfo, int sourceType, List<String> markCounts) {
        this.id=null;
        setWorkOrderInfo(workOrderInfo);
        setSourceType(sourceType);
        this.qcPersonnel= QcApplication.getUserID();
        setBadCodeCount(markCounts);
        this.badCount = markCounts.size();
        this.collectionDate= Calendar.getInstance().getTime();
        setIsUploaded(false);
    }


    @Generated(hash = 34962087)
    public BadMaterialLog() {
    }



    public Map<String, String> getUploadParams() {
        Map<String,String> uploadParams=new HashMap<String,String>();
        uploadParams.put("QCheckPersonnel", qcPersonnel);
        uploadParams.put("MaterialISN", materialISN);
        uploadParams.put("OperatorID", operatorId);
        uploadParams.put("WorkPlaceId", workPlaceId);
        uploadParams.put("SourceType", textOf(getSourceType()));
        uploadParams.put("BadTypeCode", getStringOfBadCode());
        uploadParams.put("BadTypeCount", getStringOfCodeCount());
        return uploadParams;
    }

    /**
     * 返回上传参数的字符串明细,以供校验
     * @return
     */
    @Keep
    public String toUploadString()
    {
        return getUploadParams().toString();
    }

    /**
     * 将本实例中的defections列表转换成字符串用于提交上传
     */
    @Keep
    public String getStringOfCodeCount()
    {
        if(badCodeCount ==null)
            return "";
        StringConverter stringConverter=new StringConverter();
        return stringConverter.convertToDatabaseValue(badCodeCount);
    }
    @Keep
    public String getStringOfBadCode() {
        if(badCodeCount ==null)
            return "";
        StringConverter stringConverter=new StringConverter();
        return stringConverter.convertToDatabaseValue(badTypeCode);
    }


    /**
     *
     * @param badCounts
     */
    @Keep
    public void setLongBadCounts(List<Long> badCounts) {
        List<String> defections=new ArrayList<String>();
        for(Long count:badCounts)
            defections.add(count.toString());
        this.badCodeCount =defections;
    }
    @Keep
    public List<Long> toLongList() {
        List<Long> longList=new ArrayList<Long>();
        for(String count: badCodeCount)
            longList.add(Long.valueOf(count));
        return longList;
    }




    @Keep
    public void setWorkOrderInfo(WorkOrderInfo workOrderInfo) {
        this.materialISN = workOrderInfo.getMaterialISN();
        this.operatorId= workOrderInfo.getOperatorId();
        this.workPlaceId= workOrderInfo.getWorkPlaceId();
    }
    @Keep
    public boolean isUploaded()
    {
        return getIsUploaded();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getQcPersonnel() {
        return this.qcPersonnel;
    }

    public void setQcPersonnel(String qcPersonnel) {
        this.qcPersonnel = qcPersonnel;
    }

    public String getMaterialISN() {
        return this.materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }

    public String getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getWorkPlaceId() {
        return this.workPlaceId;
    }

    public void setWorkPlaceId(String workPlaceId) {
        this.workPlaceId = workPlaceId;
    }

    public Date getCollectionDate() {
        return this.collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public List<String> getBadTypeCode() {
        return this.badTypeCode;
    }

    public void setBadTypeCode(List<String> badTypeCode) {
        this.badTypeCode = badTypeCode;
    }

    public List<String> getBadCodeCount() {
        return this.badCodeCount;
    }

    public void setBadCodeCount(List<String> badCodeCount) {
        this.badCodeCount = badCodeCount;
    }

    public long getBadCount() {
        return this.badCount;
    }

    public void setBadCount(long badCount) {
        this.badCount = badCount;
    }

    public boolean getIsUploaded() {
        return this.isUploaded;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }



}
