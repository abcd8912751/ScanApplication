package com.furja.qc.databases;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *GreenDao的数据工单基类
 * 包含物料信息、工号、日期等
 */
@Entity(active = true)
public class WorkOrder {
    @Id(autoincrement = true)
    private Long id;
    private String materialISN; //物料内码
    @NotNull
    private String materielID;    //物料代码
    @NotNull
    private String materielName;    //物料名称
    @NotNull
    private String norms;   //规格

    @NotNull
    private Date saveDate;  //日期
    private String operatorId;    //操作员
    private String workplaceID;   //工位号

    private long defectCount;   //不良品数量

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 771970160)
    private transient WorkOrderDao myDao;

    

    @Generated(hash = 2081754328)
    public WorkOrder() {
    }

    @Generated(hash = 1188845399)
    public WorkOrder(Long id, String materialISN, @NotNull String materielID,
            @NotNull String materielName, @NotNull String norms, @NotNull Date saveDate,
            String operatorId, String workplaceID, long defectCount) {
        this.id = id;
        this.materialISN = materialISN;
        this.materielID = materielID;
        this.materielName = materielName;
        this.norms = norms;
        this.saveDate = saveDate;
        this.operatorId = operatorId;
        this.workplaceID = workplaceID;
        this.defectCount = defectCount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMaterielID() {
        return this.materielID;
    }

    public void setMaterielID(String materielID) {
        this.materielID = materielID;
    }

    public String getMaterielName() {
        return this.materielName;
    }

    public void setMaterielName(String materielName) {
        this.materielName = materielName;
    }

    public String getNorms() {
        return this.norms;
    }

    public void setNorms(String norms) {
        this.norms = norms;
    }

    public Date getSaveDate() {
        return this.saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }



    public String getWorkplaceID() {
        return this.workplaceID;
    }

    public void setWorkplaceID(String workplaceID) {
        this.workplaceID = workplaceID;
    }
    @Keep
    public List<String> toStringList()
    {
        List<String> result=new ArrayList<String>();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.add(""+getMaterielID());
        result.add(getMaterielName());
        result.add(getNorms());
        result.add(formater.format(getSaveDate()));
        result.add(getOperatorId());
        result.add(getWorkplaceID());
        result.add(""+getDefectCount());
        return result;
    }

    public long getDefectCount() {
        return this.defectCount;
    }

    public void setDefectCount(long defectCount) {
        this.defectCount = defectCount;
    }

    public String getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getMaterialISN() {
        return this.materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1463153995)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWorkOrderDao() : null;
    }


}
