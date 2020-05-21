package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.qc.beans.CommonInfoBundle;
import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.Preferences;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.presenter.InjectionLogPresenter;
import com.furja.qc.utils.InjectionAutoUpdateUtils;
import com.furja.qc.utils.HttpCallback;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.StatusBarUtils;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.AutoCapTransitionMethod;
import com.furja.qc.view.CleanableEditText;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.qc.utils.Constants.ACTION_UPDATE_APK;
import static com.furja.qc.utils.Constants.EXTRA_COMMONINFO;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.RESET_CONFIG;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.TextInputListener.INPUT_ERROR;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;
import static com.furja.qc.utils.Utils.textOf;

public class InjectionLogActivity extends BaseActivity implements InjectionLogContract.View {
    InjectionLogPresenter presenter;
    @BindView(R.id.edit_barCode)
    CleanableEditText editBarCode;
    @BindView(R.id.materialInfo)
    ConstraintLayout materialInfoView;
    @BindView(R.id.text_date)
    TextView textDate;
    @BindView(R.id.edit_operator)
    CleanableEditText editOperator;
    @BindView(R.id.edit_workplace)
    CleanableEditText editWorkplace;
    @BindView(R.id.recycler_marker)
    RecyclerView recyclerMarker;
    @BindView(R.id.btn_undo_btnFrag)
    ImageButton btnUndoBtnFrag;
    @BindView(R.id.btn_redo_btnFrag)
    ImageButton btnRedoBtnFrag;
    @BindView(R.id.btn_edit_btnFrag)
    ImageButton btnEditBtnFrag;
    @BindView(R.id.btn_submit_btnFrag)
    ImageButton btnSubmitBtnFrag;
    @BindView(R.id.text_materialName)
    TextView textMaterialName;
    @BindView(R.id.text_materialModel)
    TextView textMaterialModel;
    @BindView(R.id.text_badCount)
    TextView textBadCount;
    BoomMenuButton boomMenuButton;
    private MaterialInfo materialInfo;
    private boolean needUpdate=false;
    Disposable disposable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injectionlog);
        ButterKnife.bind(this);
        new StatusBarUtils().setStatusBarColor(this, R.color.colorPrimary);
        recyclerMarker.setLayoutManager(new GridLayoutManager(this, 4));
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        textDate.setText(formater.format(new Date(System.currentTimeMillis())));
        presenter = new InjectionLogPresenter(this);
        AutoCapTransitionMethod autoCapMethod
                = new AutoCapTransitionMethod();
        editWorkplace.setTransformationMethod(autoCapMethod);
        editOperator.setTransformationMethod(autoCapMethod);
        recyclerMarker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
        initView();
    }



    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(editWorkplace.getWindowToken(), 0);
    }

    private void initView() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        TextInputListener listener
                = new TextInputListener();
        listener.bindEditText(editBarCode);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View customView = mInflater.inflate(R.layout.layout_bar_boommenu, null);
        boomMenuButton = customView.findViewById(R.id.boom);
        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) customView.getParent()).setContentInsetsAbsolute(0,0);
        boomMenuButton.setButtonEnum(ButtonEnum.TextOutsideCircle);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.DOT_8_3);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.SC_8_3);
        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            TextOutsideCircleButton.Builder builder
                    = new TextOutsideCircleButton.Builder()
                    .normalImageRes(getImageRes(i))
                    .normalTextRes(getTextRes(i))
                    .normalColor(android.R.color.transparent)
                    .pieceColorRes(R.color.holo_circle)
                    .textSize(16)
                    .listener(index->{
                        confirmAction(index);
                    });
            boomMenuButton.addBuilder(builder);
        }
        boomMenuButton.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
            }

            @Override
            public void onBackgroundClick() {
            }

            @Override
            public void onBoomWillHide() {
            }

            @Override
            public void onBoomDidHide() {
            }

            @Override
            public void onBoomWillShow() {
                hideSoftInput();
            }

            @Override
            public void onBoomDidShow() {
            }
        });
        Intent intent=getIntent();
        String action=null;
        if(intent!=null)
            action=intent.getAction();
        if(action!=null&&action.equals(ACTION_UPDATE_APK)) {
            new InjectionAutoUpdateUtils(this, true)
                    .checkUpdate();
        }
        materialInfoView
                .setVisibility(View.GONE);
        enableBarCodeInput();
        presenter.resetFieldData();
        textBadCount.setText("");
    }

    private int getTextRes(int i) {
        return getResorceID(i,false);
    }

    private int getImageRes(int i) {
        return getResorceID(i,true);
    }

    private int getResorceID(int i, boolean isImage) {
        switch (i) {
            case 1:
                if (isImage)
                    return R.mipmap.ic_switch_src;
                else
                    return R.string.action_switchScene;
            case 2:
                if (isImage)
                    return R.mipmap.ic_tour;
                else
                    return R.string.label_tourInspection;
            case 3:
                if (isImage)
                    return R.mipmap.ic_dimenlog;
                else
                    return R.string.label_dimenLog;
            case 4:
                if (isImage)
                    return R.mipmap.ic_update;
                else
                    return R.string.action_resetConfig;
            case 5:
                if (isImage)
                    return R.mipmap.ic_chart;
                else
                    return R.string.action_viewReport;
            case 6:
                if (isImage)
                    return R.mipmap.icon_history;
                else
                    return R.string.action_viewHistory;
            case 7:
                if (isImage)
                    return R.mipmap.icon_caution;
                else
                    return R.string.action_viewCaution;
                default:
                    if (isImage)
                        return R.mipmap.ic_logout_src;
                    else
                        return R.string.action_logOut;
        }
    }

    @OnTextChanged(value = R.id.edit_workplace, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editWorkPlace(Editable s) {
        if (s == null || s.length()==0)
            return;
        String input = s.toString().toUpperCase();
        String pattern="^[A-F]\\d{0,2}$";
        if(Pattern.matches(pattern,input)) {
            presenter.setWorkPlace(input);
        }
        else {
            showToast("输入的机台号与格式不符");
            editWorkplace.setText("");
        }
    }

    @Override
    public void resetView() {
        MaterialDialog materialDialog=new MaterialDialog.Builder(this)
                .title("趁热打铁")
                .content("立即进行注塑巡检?")
                .positiveText(R.string.label_confirm)
                .negativeText(R.string.label_cancel)
                .onPositive((dialog, which)->{
                    Intent intent
                            = new Intent(InjectionLogActivity.this,TourInspectionActivity.class);
                    CommonInfoBundle bundle=new CommonInfoBundle();
                    bundle.setMaterialInfo(materialInfo);
                    bundle.setWorkplace(textOf(editWorkplace.getText()));
                    intent.putExtra(EXTRA_COMMONINFO, bundle);
                    startActivity(intent);
                    finish();
                })
                .onNegative((dialog, which)->{
                    clearSubView();
                })
                .cancelable(false)
                .build();
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
        textBadCount.setText("");
        Editable editable = editOperator.getText();
        if(editable == null)
            presenter.setOperator("");
        else
            presenter.setOperator(textOf(editable));
        editable = editWorkplace.getText();
        if(editable==null)
            presenter.setWorkPlace("");
        else
            presenter.setWorkPlace(textOf(editable).toUpperCase());
    }

    @Override
    public void showMaterialInfo(MaterialInfo materialInfo) {
        this.materialInfo=materialInfo;
        if (materialInfo != null) {
            editBarCode.setEnabled(true);
            editBarCode.setText("");
            editBarCode.setVisibility(View.GONE);
            materialInfoView.setVisibility(View.VISIBLE);
            textMaterialName.setText(materialInfo.getMaterialName());
            textMaterialModel.setText(materialInfo.getNorm());
            editWorkplace.requestFocus();
            if(TextUtils.isEmpty(editWorkplace.getText())) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null&&imm.isActive())
                    imm.showSoftInput(editWorkplace,0);
            }
        }
    }

    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        recyclerMarker.setAdapter(adapter);
    }

    @Override
    public void setButtonClickListener(View.OnClickListener buttonClickListener) {
        btnUndoBtnFrag.setOnClickListener(buttonClickListener);
        btnRedoBtnFrag.setOnClickListener(buttonClickListener);
        btnEditBtnFrag.setOnClickListener(buttonClickListener);
        btnSubmitBtnFrag.setOnClickListener(buttonClickListener);
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
     * 监听物料代码的输入框,及输入法
     */
    public void listenSharpBus() {
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
        SharpBus.getInstance().register(UPDATE_BAD_COUNT)
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(obj->{
                if(presenter.getTotalBadCount()==0)
                    textBadCount.setText("");
                else
                    textBadCount.setText(presenter.getTotalBadCount()+"");
                hideSoftInput();
            });
    }

    public void showAbortDialog(int itemId) {
        new MaterialDialog.Builder(this)
                .title("要离开这里")
                .iconRes(R.mipmap.ic_planet)
                .content("有数据尚未上传,就这样走吗?")
                .positiveText("上传数据")
                .negativeText("舍弃它们")
                .onPositive((dialog, which) -> {
                    presenter.setHttpCallback(new HttpCallback() {
                        @Override
                        public void onSuccess(Object message) {
                            performAction(itemId);
                        }
                        @Override
                        public void onFail(String errorMsg) {
                        }
                    });
                    dialog = Utils.showWaitingDialog(getContext());
                    presenter.uploadLog(dialog);
                }).onNegative((dialog, which) -> {
                    dialog.cancel();
                    performAction(itemId);
            }).show()
                .getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);;
    }

    /**
     * @param id
     */
    private void confirmAction(int id) {
        if (presenter.getTotalBadCount()>0) {
            switch (id) {
                case 2:
                case 5:
                case 6:
                case 7:
                    performAction(id);
                    break;
                    default:
                        showAbortDialog(id);
            }
        }
        else
            performAction(id);
    }

    private void performAction(int id) {
        switch (id) {
            case R.id.action_logOut:
            case 0:
                Preferences.saveAutoLogin(false);
                Preferences.clearUser();
                QcApplication.setUserAndSave(null);
                toLogin();
                break;
            case 1:
            case R.id.action_switchScene:
                Preferences.saveSourceType("" + TYPE_BADLOG_EMPTY);
                toLogin();
                break;
            case 2:
                Intent intent
                     = new Intent(InjectionLogActivity.this, TourInspectionActivity.class);
                startActivity(intent);
                break;
            case 3:
                Intent intent3=new Intent(this, DimenLogActivity.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
            case R.id.action_resetConfig:
                Intent intent1 = new Intent(this, SplashActivity.class);
                intent1.setAction(RESET_CONFIG);
                startActivity(intent1);
                finish();
                break;
            case 5:
            case R.id.action_viewReport:
                Intent intent2 = new Intent(this, InjectionChartActivity.class);
                startActivity(intent2);
                break;
            case 6:
                Intent intent4 = new Intent(this, InspectionHistoryActivity.class);
                startActivity(intent4);
                break;
            case 7:
                Intent intent5 = new Intent(this, SopActivity.class);
                startActivity(intent5);
                break;
            case R.id.home:
                super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(disposable!=null)
            disposable.dispose();
        if(TextUtils.isEmpty(Preferences.getOperatorID())) {
            showToast("转班了,祝你工作顺利");
            toLogin();
        }
        else if(QcApplication.getUser()==null)
            toLogin();
         else
            listenSharpBus();
        if (needUpdate) {
            new InjectionAutoUpdateUtils(this, true)
                    .checkUpdate();
            needUpdate=false;
        }
    }

    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (editBarCode.getVisibility() == View.VISIBLE)
            if (keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                        || keyCode == KEYCODE_F7) {
				if(editBarCode.isEnabled())
					editBarCode.requestFocus();
            }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0); //来去无动画
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (presenter.getTotalBadCount()>0)
            showAbortDialog(R.id.home);
        else {
            moveTaskToBack(true);
            showLog("返回了");
            needUpdate = true;
            disposable = Observable.just(0)
                    .delay(10, TimeUnit.MINUTES)
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(event->{
                        System.exit(0);
                    },error->{
                        //在上面执行30S自杀程序
                    });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showLog("onNewIntent");
    }

    private void enableBarCodeInput() {
        editBarCode.setText("");
        editBarCode.setEnabled(true);
        editBarCode.setVisibility(View.VISIBLE);
    }

}
