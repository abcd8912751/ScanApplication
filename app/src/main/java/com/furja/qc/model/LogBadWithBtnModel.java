package com.furja.qc.model;

import android.content.Context;

import com.furja.qc.R;
import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadMaterialLog;

import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.LogBadWithBnContract;
import com.furja.qc.databases.BadMaterialLogDao;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.utils.Utils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public class LogBadWithBtnModel implements LogBadWithBnContract.Model {
    private String[] btn_titles;
    private List<Long> markCounts;
    private List<String> badcodes;
    private DaoSession daoSession;
    private BadMaterialLog badMaterialLog;
    private WorkOrderInfo workOrderInfo;
    public LogBadWithBtnModel(String[] titles)
    {
        this.daoSession= QcApplication.getDaoSession();

        this.btn_titles=titles;
        initArrays();
    }


    /**
     * 将所有MarkerButton清零
     */
    public void clearCount()
    {
        for(int i=0;i<markCounts.size();i++)
            markCounts.set(i, (long) 0);
        if(badMaterialLog !=null)
        {
            badMaterialLog.setBadCount(0);
            badMaterialLog.setLongBadCounts(markCounts);
        }
    }

    /**
     * 初始化按钮视图,从本地数据库提取数据加载
     * 如果本地数据库没有则从arrays.xml里加载
     */
    private void initArrays() {
        markCounts=new ArrayList<Long>();
        badcodes= new ArrayList<String>();
        for(int i=0;i<getItemCount();i++)
        {
            markCounts.add((long) 0);
            badcodes.add(i+"");
        }
    }

    @Override
    public int getItemCount() {
        return btn_titles.length;
    }

    @Override
    public String getOptionTitle(int position) {

        return btn_titles[position];
    }

    @Override
    public long getMarkerCount(int position) {
        return markCounts.get(position);
    }

    @Override
    public void addMarkerCount(int position) {
        markCounts.set(position,markCounts.get(position)+1);
        badMaterialLog.setBadCount(getTotalBad());
        badMaterialLog.setLongBadCounts(markCounts);
    }

    @Override
    public long getTotalBad() {
        long count=0;
        for(long i:markCounts)
            count=count+i;
        return count;
    }

    /**
     * 将物料、工号、员工号等信息传入更新视图数据
     * @param workOrderInfo
     */
    @Override
    public void updateData(WorkOrderInfo workOrderInfo) {
        setWorkOrderInfo(workOrderInfo);
        clearCount();
        if(badMaterialLog ==null)
            badMaterialLog
                    =new BadMaterialLog(workOrderInfo,TYPE_BADLOG_WITHBTN,markCounts, getTotalBad());
        else
        {
            badMaterialLog.setWorkOrderInfo(workOrderInfo);
            badMaterialLog.setLongBadCounts(markCounts);
            badMaterialLog.setBadCount(getTotalBad());
        }
        if(badcodes==null)
        {
            badcodes=new ArrayList<String>();
            for(int i=0;i<markCounts.size();i++)
                badcodes.add(i+"");
        }
        badMaterialLog.setBadTypeCode(badcodes);
        setMarkCounts(badMaterialLog.toLongList());
    }


    public void setMarkCounts(List<Long> markCounts) {
        this.markCounts = markCounts;
    }

    /**
     * 以MaterialInfo来查找DefectLog
     * @return
     */
    public List<BadMaterialLog> queryBadLogByInfo()
    {
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<BadMaterialLog> badMaterialLogs =queryBuilder.where
                (BadMaterialLogDao.Properties.MaterialISN.eq(workOrderInfo.getMaterialISN()))
                .where(BadMaterialLogDao.Properties.OperatorId.eq(workOrderInfo.getOperatorId()))
                .where(BadMaterialLogDao.Properties.WorkPlaceId.eq(workOrderInfo.getWorkPlaceId()))
                .list();
        if(badMaterialLogs ==null)
            return Collections.EMPTY_LIST;
        else
            return badMaterialLogs;
    }


    /**
     * 同步数据至本地及服务器
     */
    public void syncData()
    {
        if(badMaterialLog!=null)
        {
            if(badMaterialLog.getBadCount()>0)
            {
                syncToLocal();
                Utils.toUpload();   //在此基础上重新上传数据
            }
        }
    }

    /**
     * 同步至本地
     */
    public void syncToLocal()
    {
        if(badMaterialLog ==null)
            showLog(getClass()+"->当前执有的BadMaterialLog数据库为空,不予保存");
        else
        {
            badMaterialLog.setLongBadCounts(markCounts);
            badMaterialLog.setBadCount(getTotalBad());
            if(badcodes==null)
            {
                badcodes=new ArrayList<String>();
                for(int i=0;i<markCounts.size();i++)
                    badcodes.add(i+"");
            }
            badMaterialLog.setBadTypeCode(badcodes);
            Utils.saveToLocal(badMaterialLog);
        }
    }

    /**
     * 获取品质异常数据库基类
     * @return
     */
    public BadMaterialLog getBadMaterialLog() {
        return badMaterialLog;
    }
    /**
     * 设置品质异常数据库 基类
     */
    public void setBadMaterialLog(BadMaterialLog badMaterialLog) {
        this.badMaterialLog = badMaterialLog;
    }

    public WorkOrderInfo getWorkOrderInfo() {
        return workOrderInfo;
    }

    public void setWorkOrderInfo(WorkOrderInfo workOrderInfo) {
        this.workOrderInfo = workOrderInfo;
    }


    public String getMarkCountString() {
        StringBuffer stringBuffer=new StringBuffer();
        for(Long entity:markCounts)
        {
            stringBuffer.append(entity);
            stringBuffer.append(",");
        }
        if(stringBuffer.length()>0)
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        return stringBuffer.toString();
    }

    /**
     * 设置指定位置的异常个数
     * @param position
     * @param count
     */
    public void setMarkerCount(int position, long count) {
        markCounts.set(position,count);
        badMaterialLog.setBadCount(getTotalBad());
        badMaterialLog.setLongBadCounts(markCounts);

    }
}