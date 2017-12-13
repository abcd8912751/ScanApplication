package com.furja.qc.utils;

import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.BadMaterialLogDao;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通过本地数据库查询内容
 */

public class LocalBadTypeQuery implements BadTypeQuery {


    @Override
    public List<String> query(String input) {
        return null;
    }

    private  List<BadTypeConfig> queryLocal(String some)
    {
        DaoSession daoSession= QcApplication.getDaoSession();
        BadTypeConfigDao dao=daoSession.getBadTypeConfigDao();
        List<BadTypeConfig> badLogs=new ArrayList<BadTypeConfig>();
        QueryBuilder queryBuilder=dao.queryBuilder();
//        badLogs =queryBuilder.where
//                (BadTypeConfigDao.Properties.BadTypeCode..(false))
//                .list();
        if(badLogs==null)
            return Collections.EMPTY_LIST;
        else
            return badLogs;
    }

}
