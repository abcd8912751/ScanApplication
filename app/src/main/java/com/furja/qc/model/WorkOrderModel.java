package com.furja.qc.model;

import android.content.Context;
import android.text.TextUtils;

import com.furja.qc.R;
import com.furja.qc.QcApplication;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.databases.WorkOrder;
import com.furja.qc.databases.WorkOrderDao;
import com.furja.qc.contract.WorkOrderContract;
import com.furja.qc.utils.SharpBus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.furja.qc.utils.Constants.FURIA_BARCODEINFO_URL;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.MATERIAL_INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.UPDATE_WORKORDER_BY_MATERIAL;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 从数据库提取工单信息
 */

public class WorkOrderModel implements WorkOrderContract.Model {
    //工单信息中条目名称
    private List<String> titles;
    //工单信息中各条目对应的值
    private List<String> workinfoes;
    private DaoSession daoSession;
    //包含 数据库键值的信息
    private WorkOrderInfo workOrderInfo;
    private WorkOrder workOrder;

    private String materialBarCode; //预存未加载二维码
    public WorkOrderModel()
    {
        initList();
        Context context= QcApplication.getContext();
        String[] items = context.getResources().getStringArray(R.array.workinfo_titles);
        for(String item:items)
            titles.add(item);

        setDefault();
        showLog("初始化:"+getClass());
        this.daoSession= QcApplication.getDaoSession();

    }

    private void initList() {
        titles=new ArrayList<String>();
        workinfoes=new ArrayList<String>();
    }
    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    @Override
    public int getItemCount() {
        if(titles==null)
            return 0;
        return titles.size();
    }

    /**
     * 获取数据库里最新一条保存的工单信息
     * @return
     */
    public WorkOrder getLastWorkOrder()
    {
        WorkOrderDao workOrderDao=daoSession.getWorkOrderDao();
        QueryBuilder queryBuilder=workOrderDao.queryBuilder();
        LazyList<WorkOrder> workOrderList
                = queryBuilder.orderDesc(WorkOrderDao.Properties.Id).listLazy();

        if(workOrderList==null ||workOrderList.size()<1)
            return null;
        else
            return workOrderList.get(0);
    }

    @Override
    public String getTitle(int position) {
        return titles.get(position);
    }

    /**
     * 设置当前工单信息,ListView以此List刷新视图
     * @param workinfoes
     */
    public void setWorkinfoes(List<String> workinfoes) {
        this.workinfoes = workinfoes;

    }
    @Override
    public String getContent(int position) {
        return workinfoes.get(position);
    }



    private void updateInfoes()
    {
        this.setWorkinfoes(workOrder.toStringList());
    }

    /**
     * 检测是否存在指定的ID
     * @return
     */
    public void checkExistOrUpdateByBarCode(String barCode)
    {
        syncDataBase();
        updateWorkOrderByBarCode(barCode);
    }

    /**
     * 返回二维测试条码,有缺省值
     * @return
     */
    public String getMaterialBarCode() {
        if(TextUtils.isEmpty(this.materialBarCode))
            return "";
        else
            return materialBarCode;
    }


    public void setMaterialBarCode(String materialBarCode) {
        this.materialBarCode = materialBarCode;
    }

    /**
     * 设置当前工单信息并设置当前日期
     */
    public synchronized void setWorkInfoAndDate() {
        setWorkOrderInfo();
        //获取当前时间并存储
        Date date=new Date(System.currentTimeMillis());
        workOrder.setSaveDate(date);
        updateInfoes();
    }

    /**
     * 以 id 来查询工单信息
     * @param id
     * @return
     */
    private List<WorkOrder> queryByMaterialId(long id)
    {
        WorkOrderDao dao=daoSession.getWorkOrderDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<WorkOrder> workOrders
                =queryBuilder.where(WorkOrderDao.Properties.MaterielID.eq(id))
                .list();
        if(workOrders==null)
            return Collections.EMPTY_LIST;
        else
            return workOrders;
    }

    /**
     * 同步本地工单数据
     */
    public void syncDataBase()
    {
//        if(workOrder==null)
//            showLog("同步时当前WorkOrder对象为空");
//        else
//        {
//            WorkOrderDao dao=daoSession.getWorkOrderDao();
//            dao.saveInTx(workOrder);
//        }
    }

    /**
     * 测试时传入设置数据
     */
    public void setDefault()
    {
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        workOrder=new WorkOrder
                (null,"","","","",date,"","",0);
        setWorkInfoAndDate();
    }

    public WorkOrderInfo getWorkOrderInfo()
    {
        return this.workOrderInfo;
    }

    /**
     * 更新工单信息视图的不良品数量
     * @param value
     */
    public void updateDefectCount(long value) {
        workOrder.setDefectCount(value);
        setWorkinfoes(workOrder.toStringList());
    }

    public void setWorkOrderInfo() {
        if(this.workOrderInfo ==null)
            workOrderInfo =new WorkOrderInfo(workOrder.getMaterialISN(),
                    workOrder.getOperatorId(),workOrder.getWorkplaceID());
        else
        {
            workOrderInfo.setMaterialISN(workOrder.getMaterialISN());
            workOrderInfo.setOperatorId(workOrder.getOperatorId());
            workOrderInfo.setWorkPlaceId(workOrder.getWorkplaceID());
        }
    }

    /**
     * 与数据库同步获知该员工是否存在
     * @param id
     * @return
     * 默认存在
     */
    public boolean checkOperatorExistById(String id) {

        return true;
    }

    /**
     * 与数据库同步获知该机台是否存在
     * @param workPlaceId
     * @return
     * 默认存在
     */
    public boolean checkWorkPlaceExistById(String workPlaceId) {

        return true;
    }
    /**
     * 从服务器数据库提取物料信息刷新当前List
     * @param barCode
     */
    public void updateWorkOrderByBarCode(final String barCode)
    {
        showLog(getClass()+">正在读取物料信息");
        OkHttpUtils
                .get()
                .url(FURIA_BARCODEINFO_URL)
                .addParams("Barcode", barCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        SharpBus.getInstance().post(MATERIAL_INTERNET_ABNORMAL,"true");
                    }

                    @Override
                    public void onResponse(String response, int i) {
                        showLog(getClass()+response);
                        MaterialInfo info = new MaterialInfo();
                        try {
                            info.formatJson(response);
                            if(!TextUtils.isEmpty(info.getMaterialName()))
                            {
                                workOrder.setMaterielID(info.getMaterialId());
                                workOrder.setMaterielName(info.getMaterialName());
                                workOrder.setNorms(info.getNorm());
                                workOrder.setMaterialISN(info.getMaterialISN());
                                setWorkOrderInfo();
                                SharpBus.getInstance()
                                        .post(UPDATE_WORKORDER_BY_MATERIAL,"finish");
                            }
                            setWorkInfoAndDate();   //刷新视图
                        }
                        catch (Exception e) {
                            showToast("没有找到物料,需重输");
                            SharpBus.getInstance()
                                    .post(UPDATE_WORKORDER_BY_MATERIAL,"error");
                        }
                    }
                });
    }
    /**
     * 从本地数据库提取物料信息刷新当前List
     * @param materialid
     */
    public void updateWorkOrderById(long materialid) {
        List<WorkOrder> workOrders= queryByMaterialId(materialid);
        if(!workOrders.isEmpty())
        {
            this.workOrder=workOrders.get(0);
        }
        else
        {
            //更新物料代码
            workOrder.setMaterielID(String.valueOf(materialid));
        }
        setWorkInfoAndDate();
    }




    public void updateWorkOrderByWorkPlaceId(String placeId) {
        workOrder.setWorkplaceID(placeId);
        setWorkInfoAndDate();
    }

    public void updateWorkOrderByOperatorId(String operatorId) {
        workOrder.setOperatorId(operatorId);
        setWorkInfoAndDate();
    }

    /**
     * 做一些微妙设置,使光标跳下去
     */
    public void setMaterialNull() {
        workOrder.setMaterielID("");
        workOrder.setMaterielName("");
        workOrder.setNorms("");
        workOrder.setDefectCount(0);
        setMaterialBarCode("");
        setWorkInfoAndDate();
    }
}
