package com.furja.qc.model;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.furja.qc.beans.BadLogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class InjectionLogModel implements InjectionLogContract.Model {
    private String[] btn_titles;
    private List<Long> markCounts;
    private List<String> badcodes;
    private BadMaterialLog badMaterialLog;

    public InjectionLogModel() {

    }




    /**
     * 判断物料代码是否为空
     * @return
     */
    public boolean ISNisNull() {
        if(TextUtils.isEmpty(badMaterialLog.getMaterialISN()))
            return true;
        return false;
    }

    /**
     * 判断物料tiaoma、机台号中是否有空的情况
     */
    public boolean infoHasNull() {
        if(badMaterialLog==null)
            return true;
        if(ISNisNull()) {
            showToast("设置物料代码后方可记录");
            return true;
        }
        if(TextUtils.isEmpty(badMaterialLog.getWorkPlaceId())) {
            showToast("设置机台号后方可记录");
            return true;
        }
        return false;
    }


    /**
     * 将所有MarkerButton清零
     */
    public void clearCount() {
        for(int i=0;i<markCounts.size();i++)
            markCounts.set(i, (long) 0);
    }

    /**
     * 初始化按钮视图,从本地数据库提取数据加载
     * 如果本地数据库没有则从arrays.xml里加载
     */
    private void initArrays() {
        markCounts=new ArrayList<Long>();
        badcodes= new ArrayList<String>();
        int length = getItemCount();
        for(int i = 0; i< length; i++) {
            markCounts.add( 0L);
            badcodes.add(i+"");
        }
        updateData(new WorkOrderInfo());
    }

    @Override
    public int getItemCount() {
        return btn_titles==null?0:btn_titles.length;
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
     * 将物料、工号等信息传入更新视图数据
     * @param workOrderInfo
     */
    @Override
    public void updateData(WorkOrderInfo workOrderInfo) {
        if(badMaterialLog ==null)
            badMaterialLog
                    =new BadMaterialLog(workOrderInfo,TYPE_BADLOG_WITHBTN,markCounts, getTotalBad());
        else {
            badMaterialLog.setWorkOrderInfo(workOrderInfo);
            badMaterialLog.setLongBadCounts(markCounts);
            badMaterialLog.setBadCount(getTotalBad());
        }
        if(badcodes==null) {
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

    /**
     * 将当前状态整理成字符串以供备忘录保存
     * @return
     */
    public String getMarkCountString() {
        StringBuffer stringBuffer=new StringBuffer();
        for(Long entity:markCounts) {
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

    public void resetFieldData() {
        if(btn_titles==null){
            readBtnTitlesFromLocal();
        }
        else {
            initArrays();
        }
    }

    public List<BadLogEntry> getBadLogEntries() {
        List<BadLogEntry> badLogEntries
                =new ArrayList<>();
        for(int i = 0; i< getItemCount(); i++) {
            long count=markCounts.get(i);
            if(count>0)
                badLogEntries.add(new BadLogEntry(i,count));
        }
        return badLogEntries;
    }
    public void setMaterialISN(String materialISN) {
        badMaterialLog.setMaterialISN(materialISN);
    }

    public void setWorkPlace(String input) {
        badMaterialLog.setWorkPlaceId(input);
    }

    public void setOperator(String input) {
        badMaterialLog.setOperatorId(input);
    }

    public void setBadLogEntries(List<BadLogEntry> badLogEntries) {
        initArrays();
        if(badLogEntries!=null){
            for(BadLogEntry entry:badLogEntries){
                int index=badcodes.indexOf(entry.getBadCode());
                if (index != -1)
                    markCounts.set(index,entry.getCodeCount());
            }
        }
        SharpBus.getInstance().post(UPDATE_BAD_COUNT, getTotalBad());
    }

    /**
     * 从本地数据库或Resource读取按钮标题
     */
    private void readBtnTitlesFromLocal() {
        Observable.fromCallable(new Callable<List<BadTypeConfig>>() {
            @Override
            public List<BadTypeConfig> call() throws Exception {
                DaoSession daoSession= Utils.getDaoSession();
                BadTypeConfigDao typeConfigDao
                        =daoSession.getBadTypeConfigDao();
                List<BadTypeConfig> allResults=typeConfigDao.queryBuilder()
                        .where(BadTypeConfigDao.Properties.SourcType.eq(1))
                        .list();
                daoSession.clear();
                if(allResults!=null)
                    return allResults;
                else
                    return Collections.EMPTY_LIST;
            }}).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<BadTypeConfig>>() {
            @Override
            public void accept(List<BadTypeConfig> badTypeConfigs) throws Exception {
                int list_size = badTypeConfigs.size();
                btn_titles = readTitles(badTypeConfigs, list_size);
                initArrays();
            }

            /**
             * 获取MarkerButton的label文本
             * @param badTypeConfigs
             * @param list_size
             * @return
             */
            @NonNull
            private String[] readTitles(List<BadTypeConfig> badTypeConfigs, int list_size) {
                String[] btn_titles;
                if (list_size > 0) {
                    btn_titles = new String[list_size];
                    for (int i = 0; i < list_size; i++) {
                        BadTypeConfig typeConfig = badTypeConfigs.get(i);
                        btn_titles[i] = typeConfig.getTypeDesp();
                    }
                } else {
                    //如果本地数据库没有相应数据使用默认配置信息
                    showLog("没有找到相应异常配置信息");
                    Context context= QcApplication.getContext();
                    btn_titles = context.getResources().getStringArray(R.array.operate_options);
                }
                return btn_titles;
            }
        },error->{
            Context context= QcApplication.getContext();
            btn_titles = context.getResources().getStringArray(R.array.operate_options);
            initArrays();
        });
    }
}
