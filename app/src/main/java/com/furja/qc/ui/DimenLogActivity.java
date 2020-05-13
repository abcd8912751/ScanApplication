package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.furja.qc.beans.CommonInfoBundle;
import com.furja.qc.R;
import com.furja.qc.beans.DimenGroupItem;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.TimePeriodDaily;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.databases.DimenGaugeLog;
import com.furja.qc.presenter.InjectionLogPresenter;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.AutoCapTransitionMethod;
import com.furja.qc.view.ClearableEditTextWithIcon;
import com.furja.qc.view.DatePickerWheel;
import com.furja.qc.view.DimenGroupAdapter;
import com.furja.qc.view.WheelView;
import com.furja.qc.view.WrapLinearLayoutManager;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kyleduo.switchbutton.SwitchButton;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.TAG_GOT_DIMENLOG;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.TextInputListener.INPUT_ERROR;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class DimenLogActivity extends BaseActivity implements InjectionLogContract.View {
    @BindView(R.id.edit_barCode)
    AppCompatEditText editBarCode;
    @BindView(R.id.text_materialName)
    TextView textMaterialName;
    @BindView(R.id.text_materialModel)
    TextView textMaterialModel;
    @BindView(R.id.text_date)
    TextView textDate;
    @BindView(R.id.edit_moldCavity)
    MaterialSpinner editMoldCavity;
    @BindView(R.id.edit_workplace)
    ClearableEditTextWithIcon editWorkplace;
    @BindView(R.id.edit_moldNo)
    MaterialSpinner editMoldNo;
    @BindView(R.id.materialInfo)
    ConstraintLayout materialInfoView;
    @BindView(R.id.recycler_dimenGroup)
    RecyclerView recyclerGroup;
    @BindView(R.id.spinner_class)
    MaterialSpinner spinnerClass;
    @BindView(R.id.nested_scroll)
    NestedScrollView nestedScroll;
    @BindView(R.id.label_dimenAdd)
    AppCompatTextView label_dimenAdd;
    @BindView(R.id.swith_packageOrBody)
    SwitchButton swith_packageOrBody;
    BottomSheetDialog bottomSheetDialog;
    MaterialInfo materialInfo;
    InjectionLogPresenter presenter;
    String selectDate;
    TimePeriodDaily timePeriodDaily;
    DimenGroupAdapter groupAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimenlog);
        ButterKnife.bind(this);
        AutoCapTransitionMethod autoCapMethod = new AutoCapTransitionMethod();
        editWorkplace.setTransformationMethod(autoCapMethod);
        presenter = new InjectionLogPresenter(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        analyseIntent();
    }

    /**
     * 解析Intent
     */
    private void analyseIntent() {
        Intent intent=getIntent();
        if (intent !=null) {
            String extraString = intent.getDataString();
            if (!TextUtils.isEmpty(extraString)) {
                try {
                    CommonInfoBundle bundle
                            = JSON.parseObject(extraString,CommonInfoBundle.class);
                    showMaterialInfo(bundle.getMaterialInfo());
                    List<String> stringItems = editMoldNo.getItems();
                    editMoldNo.setSelectedIndex(stringItems.indexOf(bundle.getMoldNo()));
                    stringItems = editMoldCavity.getItems();
                    editMoldCavity.setSelectedIndex(stringItems.indexOf(bundle.getMoldCavity()));
                    stringItems = spinnerClass.getItems();
                    spinnerClass.setSelectedIndex(stringItems.indexOf(bundle.getWorkingClass()));
                    editWorkplace.setText(bundle.getWorkplace());
                 } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initView() {
        valueSelectDate();
        timePeriodDaily = TimePeriodDaily.getNowPeroid(false);
        listenSharpBus(); freshDatePeriod();
        bottomSheetDialog
                = new BottomSheetDialog(this);
        groupAdapter=new DimenGroupAdapter(R.layout.layout_dimengroup_item);
        WrapLinearLayoutManager wrapLayoutManager
                = new WrapLinearLayoutManager(this);
        wrapLayoutManager.setSmoothScrollbarEnabled(true);
        wrapLayoutManager.setScrollAndTopEnabled(true);
        recyclerGroup.setLayoutManager(wrapLayoutManager);
        recyclerGroup.setHasFixedSize(true);
        groupAdapter.bindToRecyclerView(recyclerGroup);
        groupAdapter.setNestedScrollView(nestedScroll);
        groupAdapter.startRenderAutoFalse();
        groupAdapter.addData(new DimenGroupItem());
        groupAdapter.addData(new DimenGroupItem());
        editMoldNo.setOnItemSelectedListener((view, position, id, item) -> {
            syncDimenLog();
        });
        spinnerClass.setItems(presenter.getClassLst());
        editMoldNo.setItems(presenter.getMoldNoLst());
        editMoldCavity.setItems(presenter.getMoldCavityLst());
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.background, typedValue, true);
        ColorDrawable colorDrawable=new ColorDrawable(typedValue.data);
        editMoldNo.setBackground(colorDrawable);
        editMoldCavity.setBackground(colorDrawable);
        spinnerClass.setBackground(colorDrawable);
        swith_packageOrBody.setOnCheckedChangeListener((buttonView, isChecked) -> {
            syncDimenLog();
        });
    }


    /**
     * 显示日期和时段
     */
    private void freshDatePeriod() {
        textDate.setText(selectDate + " " + timePeriodDaily.toString());
    }


    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(editMoldNo.getWindowToken(), 0);
    }

    /**
     * 监听物料代码的输入框,及输入法
     */
    public void listenSharpBus() {
        TextInputListener listener
                = new TextInputListener();
        listener.bindEditText(editBarCode);
        SharpBus.getInstance().register(TAG_SCAN_BARCODE, String.class)
                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(scanString -> {
                    if (scanString.equals(INPUT_ERROR)
                            || scanString.equals(INTERNET_ABNORMAL)
                            || scanString.equals(NODATA_AVAILABLE)) {
                        editBarCode.setEnabled(true);
                        editBarCode.setText("");
                        showToast(scanString);
                    } else if(editBarCode.isEnabled()){
                        showToast("正在获取物料信息");
                        editBarCode.setEnabled(false);
                        presenter.acquireMaterialInfo(scanString);
                    }
                });
        SharpBus.getInstance().register(TAG_GOT_DIMENLOG,DimenGaugeLog.class)
                .as(AutoDispose.<DimenGaugeLog>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(dimenLog->{
                    valueByDimenLog(dimenLog);
                });
    }

    @Override
    public void resetView() {
        materialInfoView
                .setVisibility(View.GONE);
        enableBarCodeInput();
        presenter.resetFieldData();
        materialInfo = null;
        valueByEmptyLog();
        editBarCode.requestFocus();
        swith_packageOrBody.setChecked(true);
    }

    private void valueByEmptyLog() {
        valueByDimenLog(null);
    }

    /**
     * 将 dimenGaugeLog 携带的值赋予界面
     * @param dimenGaugeLog
     */
    private void valueByDimenLog(DimenGaugeLog dimenGaugeLog) {
        if (dimenGaugeLog == null) {
            editMoldCavity.setSelectedIndex(0);
            editWorkplace.setText("");
            editMoldNo.setSelectedIndex(0);
            spinnerClass.setSelectedIndex(0);
            valueSelectDate();
            timePeriodDaily = TimePeriodDaily.getNowPeroid(false);
            freshDatePeriod();
            groupAdapter.clear();
            groupAdapter.startRenderAutoFalse();
            groupAdapter.addData(new DimenGroupItem());
            groupAdapter.addData(new DimenGroupItem());
            recyclerGroup.requestLayout();
        }
        else{
            if (dimenGaugeLog.getFID()>0) {
                editWorkplace.setText(dimenGaugeLog.getWorkplaceID());
                editMoldCavity.setText(dimenGaugeLog.getMoldCavity());
                String workClass=dimenGaugeLog.getWorkingClass();
                spinnerClass.setText(workClass);
            }
            List<DimenGroupItem> groupItemList
                    = dimenGaugeLog.getDimenGroupItems();
            if (groupItemList!=null&&!groupItemList.isEmpty())
                groupAdapter.setDataOfJSON(groupItemList);
        }
    }

    /**
     * 给日期赋值,当前时间在7:30之前的话将日期设定为昨天
     */
    private void valueSelectDate() {
        SimpleDateFormat formater
                = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay<8){
            if(hourOfDay <7||calendar.get(Calendar.MINUTE)<30)
                calendar.add(Calendar.DAY_OF_MONTH,-1);
        }
        selectDate = formater.format(calendar.getTime());
    }

    private void enableBarCodeInput() {
        editBarCode.setText("");
        editBarCode.setEnabled(true);
        editBarCode.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (editBarCode.getVisibility() == View.VISIBLE)
            if (keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                    || keyCode == KEYCODE_F7) {
                editBarCode.requestFocus();
            }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void showMaterialInfo(MaterialInfo materialInfo) {
        if (materialInfo != null) {
            editBarCode.setEnabled(true);
            editBarCode.setText("");
            if(materialInfo.getMaterialISN()==null) {
                showToast("物料信息异常,请重新扫描");
                return;
            }
            editBarCode.setVisibility(View.GONE);
            materialInfoView.setVisibility(View.VISIBLE);
            textMaterialName.setText(materialInfo.getMaterialName());
            textMaterialModel.setText(materialInfo.getNorm());
            editWorkplace.requestFocus();
            if (TextUtils.isEmpty(editWorkplace.getText())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive())
                    imm.showSoftInput(editWorkplace, 0);
            }
        }
        this.materialInfo = materialInfo;
        syncDimenLog();
    }


    @OnClick({R.id.label_date, R.id.label_dimenAdd,R.id.text_date,R.id.spinner_class,R.id.edit_moldNo,R.id.edit_moldCavity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.label_date:
            case R.id.text_date:
                showPeriodDialog();
                break;
            case R.id.label_dimenAdd:
                groupAdapter.addData(new DimenGroupItem());
                recyclerGroup.scrollToPosition(groupAdapter.size()-1);
                View footView=findViewById(R.id.footView_newDimen);
                Observable.just(footView.getTop())
                        .delay(200,TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(top->{
                            nestedScroll.smoothScrollTo(0,top);
                        });
                break;
            case R.id.spinner_class:
                if(spinnerClass.getText().length()>4)
                    spinnerClass.setItems(presenter.getClassLst());
                break;
            case R.id.edit_moldNo:
                if(editMoldNo.getText().length()>4)
                    editMoldNo.setItems(presenter.getMoldNoLst());
                break;
            case R.id.edit_moldCavity:
                if(editMoldCavity.getText().length()>4)
                    editMoldCavity.setItems(presenter.getMoldCavityLst());
                break;
            default:
                hideSoftInput();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,InjectionLogActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 显示 时段选择的对话框
     */
    private void showPeriodDialog() {
        if (bottomSheetDialog.isShowing())
            return;
        View view = getLayoutInflater()
                .inflate(R.layout.layout_period_dialog, null);
        DatePickerWheel datePicker = view.findViewById(R.id.datePicker);
        WheelView<String> wheelView = view.findViewById(R.id.peroid_wheelView);
        wheelView.setData(TimePeriodDaily.getPeroidLst(false));
        wheelView.setSelectedItem(timePeriodDaily.getTimePeroid().getPeroid());
        datePicker.setCurved(true);
        try {
            datePicker.setSelectedDate(selectDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bottomSheetDialog.setContentView(view);
        Window window = bottomSheetDialog.getWindow();
        BottomSheetBehavior bottomSheetBehavior
                = BottomSheetBehavior.from(window.findViewById(R.id.design_bottom_sheet));
        bottomSheetBehavior.setHideable(false);
        window.findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值
        p.dimAmount = 0.2f;
        window.setAttributes(p);
        bottomSheetDialog.show();
        view.findViewById(R.id.text_cancel).setOnClickListener(cancelText -> {
            bottomSheetDialog.cancel();
        });
        view.findViewById(R.id.text_confirm).setOnClickListener(cancelText -> {
            bottomSheetDialog.cancel();
            if (!selectDate.equals(datePicker.getStandardDate())
                    ||!timePeriodDaily.toString().equals(wheelView.getSelectedItemData())){
                selectDate = datePicker.getStandardDate();
                timePeriodDaily = TimePeriodDaily.formatPeroid(wheelView.getSelectedItemData());
                syncDimenLog();
            }
            freshDatePeriod();
        });
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.menu_tourinspection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_complete:
                if (!hasEmptyInputAndValue()) {
                    presenter.submitWithConfirm();
                }
                break;
            case android.R.id.home:
                if (materialInfo!=null
                        &&!TextUtils.isEmpty(editWorkplace.getText())) {
                    presenter.confirmLeave();
                }
                else {
                    onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 检查输入是否为空并赋值
     *
     * @return
     */
    private boolean hasEmptyInputAndValue() {
        if (materialInfo == null) {
            showToast("物料信息为必录项,请扫描相应二维码");
            return true;
        }
        CharSequence workplace=editWorkplace.getText();
        if (TextUtils.isEmpty(workplace)) {
            showToast("机台号为必填项,请记载");
            editWorkplace.requestFocus();
            return true;
        } else {
            String input = workplace.toString().toUpperCase();
            String pattern="^[A-F]\\d{1,2}$";
            if(!Pattern.matches(pattern,input)) {
                showToast("输入的机台号与格式不符");
                editWorkplace.setText("");
                editWorkplace.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editWorkplace.getY());
                return true;
            }
        }
        if (TextUtils.isEmpty(editMoldNo.getText())) {
            showToast("模号为必填项,请记载");
            editWorkplace.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(editMoldCavity.getText())) {
            showToast("模穴为必填项,请记载");
            editMoldCavity.requestFocus();
            return true;
        }
        DimenGaugeLog dimenGaugeLog=presenter.getDimenGaugeLog();
        dimenGaugeLog.setMaterialISN(materialInfo.getMaterialISN());
        dimenGaugeLog.setMoldCavity(textOf(editMoldCavity.getText()));
        dimenGaugeLog.setWorkplaceID(textOf(editWorkplace.getText()));
        dimenGaugeLog.setWorkingClass(textOf(spinnerClass.getText()));
        dimenGaugeLog.setProduceDate(selectDate);
        CharSequence type = swith_packageOrBody.getTextOn();
        if(!swith_packageOrBody.isChecked()) {
            type = swith_packageOrBody.getTextOff();
        }
        dimenGaugeLog.setType(textOf(type));
        String moldNo = textOf(editMoldNo.getText());
        if(moldNo.length()>2) {
            int position = editMoldNo.getSelectedIndex();
            List<String> items = editMoldNo.getItems();
            moldNo = items.get(position);
        }
        dimenGaugeLog.setMoldNo(moldNo);
        dimenGaugeLog.setTimePeriod(timePeriodDaily.toString());
        List<DimenGroupItem> jsonGroups = groupAdapter.toJsonGroupsWithNoEmpty();
        if (jsonGroups != null) {
            dimenGaugeLog.setDimenGroupItems(jsonGroups);
        }
        else {
            return true;
        }
        presenter.setDimenGaugeLog(dimenGaugeLog);
        return false;
    }

    private void syncDimenLog() {
        showLog("获取DimenLog");
        List<String> items = editMoldNo.getItems();
        int position = editMoldNo.getSelectedIndex();
        CharSequence moldNoText=items.get(position);
        if(materialInfo!=null&&!TextUtils.isEmpty(moldNoText)) {
            String modoNoStr= textOf(moldNoText);
            CharSequence type = swith_packageOrBody.getTextOn();
            if(!swith_packageOrBody.isChecked()) {
                type = swith_packageOrBody.getTextOff();
            }
            presenter.acquireDimenLog(materialInfo.getMaterialISN(),
                    selectDate, timePeriodDaily.toString(), modoNoStr, Utils.textOf(type));
        }
    }

    /**
     * 将<T>str 格式化为字符串的大写,若参数是0则返回""
     * @param str
     * @param <T>
     * @return
     */
    private <T> String textOf(T str){
        if(str==null)
            return "";
        else{
            String string=str+"";
            try {
                if(Double.valueOf(string)==0)
                    return "";
            } catch (NumberFormatException e) {
            }
            return string.toUpperCase();
        }
    }
}
