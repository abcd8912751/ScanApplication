package com.furja.qc.utils;

import android.text.TextUtils;

import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.BadMaterialLogDao;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.databases.ProduceNo;
import com.furja.qc.databases.ProduceNoDao;
import com.furja.qc.databases.ProductModel;
import com.furja.qc.databases.ProductModelDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.furja.qc.utils.Utils.showLog;

/**
 * 通过本地数据库查询内容
 */

public class LocalBadTypeQuery implements BadTypeQuery {
    private DaoSession daoSession;
    public LocalBadTypeQuery(){
        daoSession= QcApplication.getDaoSession();
    }

    @Override
    public List<String> query(String input) {
        List<BadTypeConfig> badTypeConfigs=queryLocal(input);
        List<String> results=new ArrayList<String>();
        for(BadTypeConfig config:badTypeConfigs)
            results.add(config.toString());
        return results;
    }

    public List<String> queryProductModel(String input) {
        if(TextUtils.isEmpty(input))
            return new ArrayList<String>();
        input=input.toUpperCase();
        ProductModelDao dao=daoSession.getProductModelDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<ProductModel> models=
                queryBuilder
                        .where(ProductModelDao.Properties.FName.like("%"+input+"%")).list();
        List<String> results=new ArrayList<String>();
        if(models==null)
            return results;
        else
        {
            for(ProductModel model:models)
                results.add(model.getFName());
        }
        return results;
    }
    public List<String> queryProduceNo(CharSequence queryString) {
        String query
                = "%" + queryString + "%";
        List<String> results
                = getFilterList(query);
        return results;

    }

    public List<String> getFilterList(String query) {
        ProduceNoDao dao = daoSession.getProduceNoDao();
        QueryBuilder queryBuilder
                = dao.queryBuilder();
        List<ProduceNo> productNumbers = queryBuilder.where(ProduceNoDao.Properties.ProductNo.like(query))
                .list();
        if (productNumbers == null)
            return Collections.emptyList();
        List<String> results = new ArrayList<String>();
        for (ProduceNo productNumber : productNumbers)
            results.add(productNumber.getProductNo());
        return results;
    }
    public  List<BadTypeConfig> queryLocal(String some)
    {
        if(TextUtils.isEmpty(some))
            return new ArrayList<BadTypeConfig>();
        some=some.toUpperCase();
        BadTypeConfigDao dao=daoSession.getBadTypeConfigDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<BadTypeConfig> badLogs=
                queryBuilder.where(BadTypeConfigDao.Properties.BadTypeCode.like("%"+some+"%"))
                        .where(BadTypeConfigDao.Properties.SourcType.eq(2)).list();
        if(badLogs==null)
            return Collections.EMPTY_LIST;
        else
            return badLogs;
    }



}
