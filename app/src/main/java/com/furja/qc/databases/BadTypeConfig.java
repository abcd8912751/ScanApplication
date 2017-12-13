package com.furja.qc.databases;

import com.furja.qc.QcApplication;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * GreenDao异常基础类型配置实例
 */
@Entity(active = true)
public class BadTypeConfig {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private long sourcType;  // 1表示btn录入的数据,2表示key录入的数据,区分不同场景
    @NotNull
    private String badTypeCode; //具体上传使用的ID或异常代码如F0010
    private String typeDesp; //每个ID对应的异常类型,如ID为1时对应的是料花

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1290593246)
    private transient BadTypeConfigDao myDao;



    @Generated(hash = 1023180599)
    public BadTypeConfig(Long id, long sourcType, @NotNull String badTypeCode,
            String typeDesp) {
        this.id = id;
        this.sourcType = sourcType;
        this.badTypeCode = badTypeCode;
        this.typeDesp = typeDesp;
    }
    @Generated(hash = 1419734551)
    public BadTypeConfig() {
    }





    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getSourcType() {
        return this.sourcType;
    }
    public void setSourcType(long sourcType) {
        this.sourcType = sourcType;
    }
    public String getBadTypeCode() {
        return this.badTypeCode;
    }
    public void setBadTypeCode(String badTypeCode) {
        this.badTypeCode = badTypeCode;
    }
    public String getTypeDesp() {
        return this.typeDesp;
    }
    public void setTypeDesp(String typeDesp) {
        this.typeDesp = typeDesp;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
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
    @Generated(hash = 120303353)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBadTypeConfigDao() : null;
    }

}
