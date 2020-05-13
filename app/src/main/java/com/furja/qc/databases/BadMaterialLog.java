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

/**
 * GreenDao的品质异常类型基类 数据自动上传功能用途较少
 *为防止数据多次上传,于2018-8-11日改为直接删除本地数据不予自动上传
 */
@Entity(active = true)
public class BadMaterialLog {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int sourceType;  // 1表示btn录入的数据,2表示key录入的数据,区分不同场景
    @NotNull
    private String qcPersonnel;   //质检人员
    @NotNull
    private String materialISN;
    private String operatorId;
    private String workPlaceId;
    private Date collectionDate;
    //每一个异常的代码,ButtonView使用ID,KeyView使用字母数字组成的代码
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> badTypeCode;
    //各异常类型(badTypeCode)的计数集
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> badCodeCount;

    private long badCount;  //集合,究竟有多少次品

    @NotNull
    private boolean isUploaded;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 308540047)
    private transient BadMaterialLogDao myDao;

    @Keep
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

    @Keep
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

    @Generated(hash = 1854446091)
    public BadMaterialLog(Long id, int sourceType, @NotNull String qcPersonnel, @NotNull String materialISN,
            String operatorId, String workPlaceId, Date collectionDate, List<String> badTypeCode,
            List<String> badCodeCount, long badCount, boolean isUploaded) {
        this.id = id;
        this.sourceType = sourceType;
        this.qcPersonnel = qcPersonnel;
        this.materialISN = materialISN;
        this.operatorId = operatorId;
        this.workPlaceId = workPlaceId;
        this.collectionDate = collectionDate;
        this.badTypeCode = badTypeCode;
        this.badCodeCount = badCodeCount;
        this.badCount = badCount;
        this.isUploaded = isUploaded;
    }

    @Generated(hash = 34962087)
    public BadMaterialLog() {
    }

    /**
     * 针对本数据库上传数据
     */
    @Keep
    public synchronized void uploadToRemote()
    {
        if(isUploaded)
            return;
        showLog("只有我一个人在战斗");
        String uploadUrl=Constants.getBaseUrl()+ FURJA_UPLOAD_URL;
        try {
            Map<String, String> uploadParams = getUploadParams();
            showLog(uploadParams.toString());
            OkHttpUtils
                    .post()
                    .url(uploadUrl)
                    .params(uploadParams)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                            showToast("网络异常");
							Utils.delete(BadMaterialLog.this);
                        }

                        @Override
                        public void onResponse(String responce, int i) {
                            showLog("上传所得信息:"+responce);
                            checkUploadSuccessAndSave(responce);
                        }

                        /**
                         * 根据返回的Json检验是否上传成功
                         *  如果上传成功则更新存入本地数据库
                         *  在网络异常时未上传成功需考虑定时上传
                         */
                        private void checkUploadSuccessAndSave(String responce) {
                            UploadDataJson uploadDataJson
                                    = JSON.parseObject(responce,UploadDataJson.class);
                            if(uploadDataJson!=null&&uploadDataJson.getErrCode()==100)
                            {
                                setIsUploaded(true);
                                showLog("上传数据成功啦");
                                showToast("上传成功");
                                
                            }
                            else
                            {
                                showLog(responce+">"+toUploadString());
//                                Utils.saveToLocal(BadMaterialLog.this);
                            }
							Utils.delete(BadMaterialLog.this);
                        }
                    });
        } catch (Exception e) {
            showLog("核查url: "+uploadUrl);
        }
    }

    @Keep
    public synchronized void uploadToRemoteBackGround()
    {
        if(isUploaded)
            return;
        showLog("只有我一个人在战斗");
        String uploadUrl= Constants.getBaseUrl()+ FURJA_UPLOAD_URL;
        try {
            Map<String, String> uploadParams = getUploadParams();
            OkHttpUtils
                    .post()
                    .url(uploadUrl)
                    .params(uploadParams)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String responce, int i) {
                            checkUploadSuccessAndSave(responce);
                        }

                        /**
                         * 根据返回的Json检验是否上传成功
                         *  如果上传成功则更新存入本地数据库
                         *  在网络异常时未上传成功需考虑定时上传
                         */
                        private void checkUploadSuccessAndSave(String responce) {
                            UploadDataJson uploadDataJson
                                    = JSON.parseObject(responce,UploadDataJson.class);
                            if(uploadDataJson!=null&&uploadDataJson.getErrCode()==100)
                            {
                                setIsUploaded(true);
                                showLog(responce);
                                Utils.delete(BadMaterialLog.this);
                            }
                            else
                            {
                                showLog(responce+">"+toUploadString());
                                Utils.saveToLocal(BadMaterialLog.this);
                            }
                        }
                    });
        } catch (Exception e) {
            showLog("核查url: "+uploadUrl);
        }
    }

    @Keep
    public Map<String, String> getUploadParams() {
        Map<String,String> uploadParams=new HashMap<String,String>();
        uploadParams.put("QCheckPersonnel",qcPersonnel);
        uploadParams.put("MaterialISN", materialISN);
        uploadParams.put("OperatorID",operatorId);
        uploadParams.put("WorkPlaceId",workPlaceId);
        uploadParams.put("SourceType", getSourceType()+"");
        uploadParams.put("BadTypeCode",getStringOfBadCode());
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

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 400131254)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBadMaterialLogDao() : null;
    }



}
