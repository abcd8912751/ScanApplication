package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.qc.QcApplication;
import com.furja.qc.beans.CommonInfoBundle;
import com.furja.qc.R;
import com.furja.qc.beans.DefectiveReason;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.TimePeriodDaily;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.databases.TourInspectionLog;
import com.furja.qc.presenter.InjectionLogPresenter;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.AutoCapTransitionMethod;
import com.furja.qc.view.CleanableEditText;
import com.furja.qc.view.DatePickerWheel;
import com.furja.qc.view.WheelView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kyleduo.switchbutton.SwitchButton;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.qc.beans.DefectiveReason.getReasonOfDefetive;
import static com.furja.qc.utils.Constants.EXTRA_COMMONINFO;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.TAG_GOT_TOURLOG;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.TextChanger.flat;
import static com.furja.qc.utils.TextInputListener.INPUT_ERROR;
import static com.furja.qc.utils.Utils.doubleOf;
import static com.furja.qc.utils.Utils.getSampleSizeByProduction;
import static com.furja.qc.utils.Utils.intOf;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;
import static com.furja.qc.utils.Utils.textOf;

public class TourInspectionActivity extends BaseActivity implements InjectionLogContract.View {
    @BindView(R.id.edit_barCode)
    CleanableEditText editBarCode;
    @BindView(R.id.text_materialName)
    TextView textMaterialName;
    @BindView(R.id.text_materialModel)
    TextView textMaterialModel;
    @BindView(R.id.text_date)
    TextView textDate;
    @BindView(R.id.edit_moldCavity)
    MaterialSpinner editMoldCavity;
    @BindView(R.id.edit_workplace)
    CleanableEditText editWorkplace;
    @BindView(R.id.edit_moldNo)
    MaterialSpinner editMoldNo;
    @BindView(R.id.materialInfo)
    ConstraintLayout materialInfoView;
    @BindView(R.id.recycler_marker)
    RecyclerView recyclerMarker;
    @BindView(R.id.spinner_class)
    MaterialSpinner spinnerClass;
    @BindView(R.id.radioBtn_ok)
    RadioButton radioBtnOk;
    @BindView(R.id.radioBtn_ng)
    RadioButton radioBtnNg;
    @BindView(R.id.preassembly_ok)
    RadioButton preassemblyOk;
    @BindView(R.id.preassembly_ng)
    RadioButton preassemblyNg;
    @BindView(R.id.shockTest_ok)
    RadioButton shockTestOk;
    @BindView(R.id.shockTest_ng)
    RadioButton shockTestNg;
    @BindView(R.id.edit_standardCycle)
    CleanableEditText editStandardCycle;
    @BindView(R.id.edit_actualCycle)
    CleanableEditText editActualCycle;
    @BindView(R.id.switch_appearanceQC)
    SwitchButton switchAppearanceQC;
    @BindView(R.id.switch_preassembly)
    SwitchButton switchPreassembly;
    @BindView(R.id.switch_shockTest)
    SwitchButton switchShockTest;
    @BindView(R.id.edit_nozzleRatio)
    CleanableEditText editNozzleRatio;
    @BindView(R.id.edit_totalProduction)
    CleanableEditText editTotalProduction;
    @BindView(R.id.edit_batchSubmission)
    CleanableEditText editBatchSubmission;
    @BindView(R.id.edit_sampleSize)
    CleanableEditText editSampleSize;
    @BindView(R.id.edit_numberOfDefective)
    EditText editNumberOfDefective;
    @BindView(R.id.group_preassembly)
    Group group_preassembly;
    @BindView(R.id.group_shockTest)
    Group group_shockTest;
    @BindView(R.id.text_percentageOfDefective)
    AppCompatTextView textPercentageOfDefective;
    @BindView(R.id.text_reasonOfDefective)
    AppCompatTextView textReasonOfDefective;
    @BindView(R.id.label_badCount)
    AppCompatTextView label_badCount;
    @BindView(R.id.nested_scroll)
    NestedScrollView nestedScroll;
    @BindView(R.id.edit_solution)
    AppCompatTextView editSolution;
    @BindView(R.id.edit_traceResult)
    AppCompatTextView edit_traceResult;
    @BindView(R.id.check_newStuff)
    CheckBox checkNewStuff;
    @BindView(R.id.edit_closeStatus)
    AppCompatTextView editCloseStatus;
    @BindView(R.id.edit_disposal)
    AppCompatTextView editDisposal;
    InjectionLogPresenter presenter;
    MaterialInfo materialInfo;
    String selectDate;TimePeriodDaily timePeriodDaily;
    BottomSheetDialog bottomSheetDialog;
    Disposable defectDisposable;
    long lastMillseconds;boolean needSysc=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourinspection);
        ButterKnife.bind(this);
        presenter = new InjectionLogPresenter(this);
        presenter.setTourInspection(true);
        AutoCapTransitionMethod autoCapMethod = new AutoCapTransitionMethod();
        editWorkplace.setTransformationMethod(autoCapMethod);
        recyclerMarker.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerMarker.setHasFixedSize(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        analyseIntent();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        valueSelectDate();
        timePeriodDaily = TimePeriodDaily.getNowPeroid(true);
        listenSharpBus();
        freshDatePeriod();
        bottomSheetDialog = new BottomSheetDialog(this);
        switchAppearanceQC.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nestedScroll.requestFocus();
            if (isChecked) {
                recyclerMarker.setVisibility(View.VISIBLE);
                nestedScroll.smoothScrollTo(0, (int) switchPreassembly.getY());
                nestedScroll.smoothScrollTo(0, (int) recyclerMarker.getY());
            }
            else
                recyclerMarker.setVisibility(View.GONE);
        });
        switchPreassembly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nestedScroll.requestFocus();
            if (isChecked) {
                Rect scrollBounds = new Rect();
                nestedScroll.getHitRect(scrollBounds);
                group_preassembly.setVisibility(View.VISIBLE);
                //group下方的分割线是否显示,如果没显示则滑动视图
                if (!findViewById(R.id.separater_line14).getLocalVisibleRect(scrollBounds))
                    nestedScroll.smoothScrollBy(0,preassemblyOk.getHeight()*2);
            }
            else
                group_preassembly.setVisibility(View.GONE);
        });
        switchShockTest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nestedScroll.requestFocus();
            if (isChecked) {
                group_shockTest.setVisibility(View.VISIBLE);
                Rect scrollBounds = new Rect();
                nestedScroll.getHitRect(scrollBounds);
                //group下方的分割线是否显示,如果没显示则移动视图
                if (!findViewById(R.id.separater_line16).getLocalVisibleRect(scrollBounds))
                    nestedScroll.smoothScrollBy(0,shockTestOk.getHeight()*2);
            }
            else
                group_shockTest.setVisibility(View.GONE);
        });
        recyclerMarker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
        editMoldNo.setOnItemSelectedListener((view, position, id, item) -> {
            syncTourLog();
        });
        editMoldCavity.setOnItemSelectedListener((view, position, id, item) -> {
            CharSequence charSequence = editActualCycle.getText();
            if (!TextUtils.isEmpty(charSequence)) {
                int actualCycle =Integer.valueOf(charSequence.toString());
                if (actualCycle!=0) {
                    int index= editMoldCavity.getSelectedIndex();
                    double moldCavity = Math.pow(2,index);
                    int production = (int) (3600d/actualCycle*moldCavity*2);
                    editTotalProduction.setText(""+production);
                }
            }
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
    }

    /**
     * 显示日期和时段
     */
    private void freshDatePeriod() {
        textDate.setText(selectDate + " " + timePeriodDaily.toString());
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
                    }});
        SharpBus.getInstance().register(UPDATE_BAD_COUNT,Long.class)
                .as(AutoDispose.<Long>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(badCount->{
                   nestedScroll.requestFocus();
                   if(badCount==0) {
                       label_badCount.setText("");
                       editNumberOfDefective.setText("");
                   }
                   else {
                       editNumberOfDefective.setText(""+badCount);
                       label_badCount.setText(""+badCount);
                   }
                    hideSoftInput();
                });
        SharpBus.getInstance().register(TAG_GOT_TOURLOG,TourInspectionLog.class)
                .as(AutoDispose.<TourInspectionLog>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(tourLog->{
                    valueByInspectionLog(tourLog);
                });
        editBatchSubmission.addTextChangedListener(flat(text->{
                double batchSubmission = doubleOf(text);
                int adviceSampleSize = getSampleSizeByProduction(batchSubmission);
                editSampleSize.setText(adviceSampleSize+"");
            }));
        editActualCycle.addTextChangedListener(flat(text->{
            if (!text.isEmpty()) {
                try {
                    int actualCycle = Integer.valueOf(text);
                    if (actualCycle != 0) {
                        int index = editMoldCavity.getSelectedIndex();
                        double moldCavity = Math.pow(2, index);
                        int production = (int) (3600 / actualCycle * moldCavity * 2);
                        editTotalProduction.setText("" + production);
                    }
                } catch (Exception e) {
                    showToast("实际周期过大");
                    editActualCycle.setText("");
                }
            }
        }));
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(editActualCycle.getWindowToken(), 0);
    }

    /**
     * 提交成功后会调用此方法,这时判断是否跳转至DimenLogActivity
     */
    @Override
    public void resetView() {
        MaterialDialog materialDialog=new MaterialDialog.Builder(this)
                .title("趁热打铁")
                .content("开始执行尺寸测量记录?")
                .positiveText(R.string.label_confirm)
                .negativeText(R.string.label_cancel)
                .onPositive((dialog, which)->{
                    Intent intent
                            = new Intent(TourInspectionActivity.this, DimenLogActivity.class);
                    CommonInfoBundle bundle=new CommonInfoBundle();
                    bundle.setMaterialInfo(materialInfo);
                    bundle.setMoldCavity(textOf(editMoldCavity.getText()));
                    List<String> items=editMoldNo.getItems();
                    int position = editMoldNo.getSelectedIndex();
                    bundle.setMoldNo(items.get(position));
                    bundle.setWorkingClass(spinnerClass.getText().toString());
                    bundle.setWorkplace(textOf(editWorkplace.getText()));
                    intent.putExtra(EXTRA_COMMONINFO, bundle);
                    startActivity(intent);
                    finish();
                })
                .onNegative((dialog, which)->{
                    clearSubView();
                })
                .cancelable(false).build();
        try {
            materialDialog.show();
            materialDialog.getWindow()
                    .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSubView() {
        materialInfoView
                .setVisibility(View.GONE);
        enableBarCodeInput();
        presenter.resetFieldData();
        materialInfo = null;
        valueByEmptyLog();
    }

    private void valueByEmptyLog() {
        valueByInspectionLog(null);
        editBarCode.requestFocus();
    }

    /**
     * 将TourInspectionLog的值 赋予视图
     * @param tourLog
     */
    private void valueByInspectionLog(TourInspectionLog tourLog) {
        TourInspectionLog tourInspectionLog;
        if (tourLog != null) {
            tourInspectionLog = tourLog;
            tourInspectionLog.setOperatorID(QcApplication.getUserID());
        }
        else
            tourInspectionLog = new TourInspectionLog();
        if(TextUtils.isEmpty(tourInspectionLog.getCloseStatus()))
            tourInspectionLog.setCloseStatus("未停机");
        String closeStatus = tourInspectionLog.getCloseStatus();
        editCloseStatus.setText(closeStatus);
        if (!closeStatus.equals("未停机")) {
            editCloseStatus.setTextColor(Color.RED);
        }
        else {
            editCloseStatus.setTextColor(Color.rgb(118, 118, 118));
        }
        editWorkplace.setText(textOf(tourInspectionLog.getWorkplaceID()));
        editNumberOfDefective.setText(textOf(tourInspectionLog.getNumberOfDefective()));
        editSampleSize.setText(textOf(tourInspectionLog.getSampleSize()));
        String nozeleRatio=tourInspectionLog.getNozzleRatio();
        if (nozeleRatio!=null && nozeleRatio.equals("新料"))
            checkNewStuff.setChecked(true);
        else {
            checkNewStuff.setChecked(false);
            editNozzleRatio.setText(textOf(tourInspectionLog.getNozzleRatio()));
        }
        editStandardCycle.setText(textOf(tourInspectionLog.getStandardCycle()));
        editActualCycle.setText(textOf(tourInspectionLog.getActualCycle()));
        switchShockTest.setChecked(tourInspectionLog.isExistShockTest());
        switchPreassembly.setChecked(tourInspectionLog.isExistPreAssembly());
        editBatchSubmission.setText(textOf(tourInspectionLog.getBatchSubmission()));
        String str = tourInspectionLog.getSolution();
        if ( TextUtils.isEmpty(str))
            str = "请选择";
        editSolution.setText(str);
        str=tourInspectionLog.getTraceResult();
        if (TextUtils.isEmpty(str))
            str = "请选择";
        edit_traceResult.setText(str);
        if (tourLog != null) {
            editMoldCavity.setText(tourInspectionLog.getMoldCavity());
            editMoldNo.setText(tourInspectionLog.getMoldNo());
            String workClass=tourInspectionLog.getWorkingClass();
            spinnerClass.setText(workClass);
            if (tourLog.getFID()>0) {
                radioBtnOk.setChecked(tourInspectionLog.isOk());
                preassemblyOk.setChecked(tourInspectionLog.isPreAssemblyIsOk());
                shockTestOk.setChecked(tourInspectionLog.isShockTestIsOk());
                double sampleSize = tourInspectionLog.getSampleSize();
                double numberOfDefetive = tourInspectionLog.getNumberOfDefective();
                double percentage = numberOfDefetive * 100 / sampleSize;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                if(percentage == (int)percentage)
                    decimalFormat.applyPattern("#");
                textPercentageOfDefective.setText(decimalFormat.format(percentage)+ "%");
                selectDate = tourInspectionLog.getProduceDate();
                timePeriodDaily = TimePeriodDaily.formatPeroid(tourInspectionLog.getTimePeriod());
                textReasonOfDefective.setText(tourInspectionLog.getReasonOfDefective());
                presenter.setDefetiveReasons(DefectiveReason.getIntegerArray(tourInspectionLog.getReasonOfDefective()));
                presenter.setBadLogEntries(tourInspectionLog.getBadLogEntries());
                editTotalProduction.setText(textOf(tourInspectionLog.getTotalProduction()));
                if(!TextUtils.isEmpty(tourInspectionLog.getBatchDisposal()))
                    editDisposal.setText(tourInspectionLog.getBatchDisposal());
            }
            else {
                List<String> cavities = editMoldCavity.getItems();
                int actualCycle = intOf(tourInspectionLog.getActualCycle());
                if (actualCycle>0&&cavities!=null){
                    int position = cavities.indexOf(tourInspectionLog.getMoldCavity());
                    if(position<0)
                        position= editMoldCavity.getSelectedIndex();
                    double moldCavity = Math.pow(2,position);
                    int production = (int) (3600d/actualCycle*moldCavity*2);
                    editTotalProduction.setText(""+production);
                }
            }
        }
        else {
            radioBtnOk.setChecked(true);
            preassemblyOk.setChecked(true);
            shockTestOk.setChecked(true);
            textPercentageOfDefective.setText("");
            editMoldCavity.setSelectedIndex(0);
            editMoldNo.setSelectedIndex(0);
            valueSelectDate();
            timePeriodDaily = TimePeriodDaily.getNowPeroid(true);
            textReasonOfDefective.setText("请选择");
            label_badCount.setText("");
            spinnerClass.setSelectedIndex(0);
            editTotalProduction.setText(textOf(tourInspectionLog.getTotalProduction()));
            editDisposal.setText("接受");
            editBatchSubmission.setText("");
        }
        freshDatePeriod();
        presenter.setTourInspectionLog(tourInspectionLog);
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
    public void showMaterialInfo(MaterialInfo materialInfo) {
        this.materialInfo = materialInfo;
        if (materialInfo != null) {
            editBarCode.setEnabled(true);
            editBarCode.setText("");
            editBarCode.setVisibility(View.GONE);
            materialInfoView.setVisibility(View.VISIBLE);
            textMaterialName.setText(materialInfo.getMaterialName());
            textMaterialModel.setText(materialInfo.getNorm());
            editMoldNo.requestFocus();
            if (TextUtils.isEmpty(editMoldNo.getText())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive())
                    imm.showSoftInput(editMoldNo, 0);
            }
            syncTourLog();
        }
    }

    private void syncTourLog() {
        showLog("syncTourLog");
        List<String> items=editMoldNo.getItems();
        int position = editMoldNo.getSelectedIndex();
        CharSequence moldNoText=items.get(position);
        if (materialInfo!=null&&!TextUtils.isEmpty(moldNoText))
            presenter.acquireTourLog(materialInfo.getMaterialISN(),
                    selectDate,timePeriodDaily.toString(),moldNoText.toString());
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
        wheelView.setData(TimePeriodDaily.getPeroidLst(true));
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
                timePeriodDaily
                        = TimePeriodDaily.formatPeroid(wheelView.getSelectedItemData());
                syncTourLog();
            }
            freshDatePeriod();
        });
    }

    /**
     * 解析Intent,显示已录入的基础信息
     */
    private void analyseIntent() {
        Intent intent=getIntent();
        if (intent !=null) {
            CommonInfoBundle bundle = intent.getParcelableExtra(EXTRA_COMMONINFO);
            if (bundle != null) {
                try {
                    showMaterialInfo(bundle.getMaterialInfo());
                    editWorkplace.setText(bundle.getWorkplace());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        recyclerMarker.setAdapter(adapter);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LifecycleOwner getLifeCycle() {
        return this;
    }

    /**
     * 检查输入是否为空并赋值,返回true说明有空值不予录入
     * @return
     */
    private boolean hasEmptyInputAndValue() {
        String closeStatus = editCloseStatus.getText().toString();
        boolean deviceIsClose = !closeStatus.equals("未停机");
        if (materialInfo == null&&!deviceIsClose) {
            showToast("物料信息为必录项,请扫描相应二维码");
            return true;
        }
        CharSequence workplace=editWorkplace.getText();
        if (TextUtils.isEmpty(workplace)) {
            showToast("机台号为必填项,请记载");
            editWorkplace.requestFocus();
            nestedScroll.smoothScrollTo(0, (int) editWorkplace.getY());
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
        TourInspectionLog tourInspectionLog=presenter.getTourInspectionLog();
        if (!deviceIsClose) {
            if (TextUtils.isEmpty(editActualCycle.getText())) {
                showToast("实际周期为必填项,请记载");
                editActualCycle.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editActualCycle.getY());
                return true;
            }
            String standardCycle = textOf(editStandardCycle.getText());
            if (TextUtils.isEmpty(editNozzleRatio.getText())
                    &&!checkNewStuff.isChecked()) {
                showToast("水口比例为必填项,请记载");
                editNozzleRatio.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editNozzleRatio.getY());
                return true;
            }
            if (TextUtils.isEmpty(editTotalProduction.getText())) {
                showToast("生产总数为必填项,请记载");
                editTotalProduction.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editTotalProduction.getY());
                return true;
            }
            if (TextUtils.isEmpty(editBatchSubmission.getText())) {
                showToast("送检数量为必填项,请记载");
                editBatchSubmission.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editBatchSubmission.getY());
                return true;
            }
            int batchSubmission=intOf(editBatchSubmission.getText());
            int totalProduction=intOf(editTotalProduction.getText());
            if (TextUtils.isEmpty(editSampleSize.getText())) {
                showToast("抽样数为必填项,请记载");
                editSampleSize.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) radioBtnOk.getY());
                return true;
            }
            int sampleSize=intOf(editSampleSize.getText()),numberOfDefective = 0;
            if (!TextUtils.isEmpty(editNumberOfDefective.getText()))
                numberOfDefective
                        = intOf(editNumberOfDefective.getText());
            if(batchSubmission<numberOfDefective) {
                showToast("录入的送检数量 < 抽样数,请核查");
                editSampleSize.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) editSampleSize.getY());
                return true;
            }
            if(sampleSize<numberOfDefective) {
                showToast("录入的不良数 > 抽样数,请核查");
                editSampleSize.setSelection(editSampleSize.length());
                editSampleSize.requestFocus();
                nestedScroll.smoothScrollTo(0, (int) radioBtnOk.getY());
                return true;
            }
            String reason="";
            if(!textOf(textReasonOfDefective.getText()).contains("请选择"))
                reason=textOf(textReasonOfDefective.getText());
            if (numberOfDefective > 0) {
                if (reason.length() == 0) {
                    showToast("不良数 >0,请选择造成不良原因");
                    selectDefectiveReason();
                    return true;
                }
            } else {
                if (reason.length() > 0) {
                    showToast("选择了造成不良原因,请记载不良数");
                    editNumberOfDefective.requestFocus();
                    nestedScroll.smoothScrollTo(0, (int) radioBtnOk.getY());
                    return true;
                }
            }
            tourInspectionLog.setBatchSubmission(batchSubmission);
            tourInspectionLog.setTotalProduction(totalProduction);
            tourInspectionLog.setSampleSize(sampleSize);
            tourInspectionLog.setNumberOfDefective(numberOfDefective);
            tourInspectionLog.setMaterialISN(materialInfo.getMaterialISN());
            tourInspectionLog.setMoldCavity(editMoldCavity.getText().toString());
            tourInspectionLog.setStandardCycle(standardCycle);
            tourInspectionLog.setActualCycle(editActualCycle.getText().toString());
            if(!checkNewStuff.isChecked())
                tourInspectionLog.setNozzleRatio(editNozzleRatio.getText().toString());
            else
                tourInspectionLog.setNozzleRatio("新料");
            CharSequence sequence = editSolution.getText();
            if(!TextUtils.isEmpty(sequence)&&!sequence.toString().equals("请选择"))
                tourInspectionLog.setSolution(sequence.toString());
            else
                tourInspectionLog.setSolution("");
            sequence=edit_traceResult.getText();
            if(!TextUtils.isEmpty(sequence)&&!sequence.toString().equals("请选择"))
                tourInspectionLog.setTraceResult(sequence.toString());
            else
                tourInspectionLog.setTraceResult("");
            sequence=textReasonOfDefective.getText();
            if(!TextUtils.isEmpty(sequence)&&!sequence.toString().equals("请选择"))
                tourInspectionLog.setReasonOfDefective(sequence.toString());
            else
                tourInspectionLog.setReasonOfDefective("");
            tourInspectionLog.setOk(radioBtnOk.isChecked());
            tourInspectionLog.setExistPreAssembly(switchPreassembly.isChecked());
            tourInspectionLog.setPreAssemblyIsOk(preassemblyOk.isChecked());
            tourInspectionLog.setExistShockTest(switchShockTest.isChecked());
            tourInspectionLog.setShockTestIsOk(shockTestOk.isChecked());
        }
        tourInspectionLog.setCloseStatus(closeStatus);
        List<String> items=editMoldNo.getItems();
        int position = editMoldNo.getSelectedIndex();
        tourInspectionLog.setMoldNo(items.get(position));
        tourInspectionLog.setWorkplaceID(workplace.toString().toUpperCase());
        tourInspectionLog.setWorkingClass(spinnerClass.getText().toString());
        tourInspectionLog.setProduceDate(selectDate);
        tourInspectionLog.setTimePeriod(timePeriodDaily.toString());
        if(TextUtils.isEmpty(editDisposal.getText()))
            tourInspectionLog.setBatchDisposal("接受");
        else
            tourInspectionLog.setBatchDisposal(textOf(editDisposal.getText()));
        presenter.setTourInspectionAndBadLog(tourInspectionLog);
        return false;
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

    @OnTextChanged(value = {R.id.edit_nozzleRatio,R.id.edit_sampleSize, R.id.edit_numberOfDefective}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void freshPercentage(Editable s) {
        CharSequence charSequence = editNozzleRatio.getText();
        if (!TextUtils.isEmpty(charSequence)) {
            double nozzleRatio=doubleOf(charSequence+"");
            if (nozzleRatio>100) {
                showToast("水口比例大于 100%,已更正");
                editNozzleRatio.setText("100");
                editNozzleRatio.setSelection(3);
            }
        }
        charSequence = editSampleSize.getText();
        double sampleSize = 0,  batchSubmission = 0;
        if (!TextUtils.isEmpty(charSequence))
            sampleSize = doubleOf(charSequence);
        charSequence = editBatchSubmission.getText();
        if (!TextUtils.isEmpty(charSequence))
            batchSubmission = doubleOf(charSequence);
        int adviceSapleSize = Utils.getSampleSizeByProduction(batchSubmission);
        if (sampleSize>batchSubmission&&sampleSize!=adviceSapleSize)
            editSampleSize.setText( adviceSapleSize+"");
        double percentage = doubleOf(editNumberOfDefective.getText()) * 100 / sampleSize;
        DecimalFormat decimalFormat= new DecimalFormat("#0.00");
        if(percentage == (int)percentage)
            decimalFormat.applyPattern("#");
        textPercentageOfDefective.setText(decimalFormat.format(percentage)+ "%");
        if (defectDisposable!=null)
            defectDisposable.dispose();
        defectDisposable = Observable.just(sampleSize)
                .delay(1200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(smeSize->{
                    if(smeSize>0) {

                        double numberOfDefective
                                = doubleOf(editNumberOfDefective.getText());
                        if (numberOfDefective > smeSize)
                            showToast("输入的抽样数 < 不良数,请更正");
                    }
                });
    }

    @OnClick({R.id.text_reasonOfDefective, R.id.label_date, R.id.text_date,R.id.edit_solution,R.id.spinner_class,R.id.edit_moldNo,
            R.id.label_traceResult,R.id.label_reasonOfDefective,R.id.label_solution,R.id.label_closeStatus,R.id.label_disposal,R.id.edit_moldCavity
            ,R.id.promot_apperanceQC,R.id.promot_preassembly,R.id.promot_shockTest,R.id.edit_traceResult,R.id.edit_closeStatus,R.id.edit_disposal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.label_date:
            case R.id.text_date:
                showPeriodDialog();
                break;
            case R.id.text_reasonOfDefective:
            case R.id.label_reasonOfDefective:
                selectDefectiveReason();
                break;
            case R.id.promot_apperanceQC:
                switchAppearanceQC.toggle();
                break;
            case R.id.promot_preassembly:
                switchPreassembly.toggle();
                break;
            case R.id.promot_shockTest:
                switchShockTest.toggle();
                break;
            case R.id.edit_solution:
            case R.id.label_solution:
                showSolutionDialog();
                break;
            case R.id.edit_traceResult:
            case R.id.label_traceResult:
                showTraceResultDialog();
                break;
            case R.id.edit_disposal:
            case R.id.label_disposal:
                showDisposalDialog();
                break;
            case R.id.edit_closeStatus:
            case R.id.label_closeStatus:
                showCloseStatusDialog();
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
            default:hideSoftInput();
        }
    }

    private void showCloseStatusDialog() {
        List<String> closeResults=Arrays.asList("未停机","机台异常","模具异常","转色",
                "待人力","待料","烘料","转模","完单","其他");
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("停机原因")
                .items(closeResults)
                .itemsCallback((dialog,view,position, text) -> {
                    editCloseStatus.setText(text);
                    if (position>0)
                        editCloseStatus.setTextColor(Color.RED);
                    else
                        editCloseStatus.setTextColor(Color.rgb(118,118,118));
                    showLog("选择了");
                })
                .onPositive((dialog, which) -> {
                    dialog.cancel();
                })
                .show();
        materialDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    private void showTraceResultDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("结果追踪")
                .items(Arrays.asList("已完成改善","未完成改善"))
                .itemsCallback((dialog,view,position, text) -> {
                    edit_traceResult.setText(text);
                    showLog("选择了"+text); })
                .onPositive((dialog, which) -> {
                    dialog.cancel(); })
                .show();
        materialDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    private void showSolutionDialog() {
        List<String> solutions=Arrays.asList("调机改善","修模","克服加工","调色","培训");
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("解决方法").items(solutions)
                .itemsCallback((dialog,view,position, text) -> {
                    editSolution.setText(text);
                    showLog("选择了"+text); })
                .onPositive((dialog, which) -> {
                    dialog.cancel();
                })
                .show();
        materialDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    /**
     * 批号处置对话框
     */
    private void showDisposalDialog() {
        List<String> items=Arrays.asList("接受","返修","特采","报废处理");
        String disposalText = textOf(editDisposal.getText());
        int selectIndex=Math.max(0,items.indexOf(disposalText));
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("批号处置").items(items)
                .itemsCallbackSingleChoice(selectIndex, (dialog,itemView,which,text)->{
                    editDisposal.setText(text);
                    showLog("批号处置 ~>"+text);
                    return false;
                })
                .onPositive((dialog, which) -> {
                    dialog.cancel();
                })
                .show();
        materialDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }


    @OnCheckedChanged({R.id.radioBtn_ok,R.id.check_newStuff,R.id.radioBtn_ng
            , R.id.preassembly_ok, R.id.preassembly_ng, R.id.shockTest_ok, R.id.shockTest_ng})
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        long time=System.currentTimeMillis();
        if (System.currentTimeMillis()-lastMillseconds<300)
            return;     //防止多次调用而致堆栈超出异常
        lastMillseconds=time;
        switch(button.getId()) {
            case R.id.radioBtn_ok:
                radioBtnNg.setChecked(!isChecked);
                break;
            case R.id.radioBtn_ng:
                radioBtnOk.setChecked(!isChecked);
                break;
            case R.id.preassembly_ok:
                preassemblyNg.setChecked(!isChecked);
                break;
            case R.id.preassembly_ng:
                preassemblyOk.setChecked(!isChecked);
                break;
            case R.id.shockTest_ok:
                shockTestNg.setChecked(!isChecked);
                break;
            case R.id.shockTest_ng:
                shockTestOk.setChecked(!isChecked);
                break;
            case R.id.check_newStuff:
                if(isChecked)
                    editNozzleRatio.setVisibility(View.INVISIBLE);
                else
                    editNozzleRatio.setVisibility(View.VISIBLE);
                break;
        }

    }
    /**
     * 弹出对话框以选择 造成不良原因
     */
    private void selectDefectiveReason() {
        new MaterialDialog.Builder(this).title("造成不良原因")
                .items(DefectiveReason.strings())
                .itemsCallbackMultiChoice(presenter.getDefetiveReasons(), (dialog, whichArray, text) -> {
                    presenter.setDefetiveReasons(whichArray);
                    String reason = getReasonOfDefetive(whichArray);
                    if (reason.length() > 0)
                        textReasonOfDefective.setText(reason);
                    else
                        textReasonOfDefective.setText("请选择");
                    return true;
                })
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    dialog.cancel();
                })
                .show().getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
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
                if (materialInfo!=null &&!TextUtils.isEmpty(editWorkplace.getText())){
                    presenter.confirmLeave();}
                else {
                    onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
