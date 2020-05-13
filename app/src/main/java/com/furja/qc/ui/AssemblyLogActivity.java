package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.Preferences;
import com.furja.qc.contract.AssemblyLogContract;
import com.furja.qc.presenter.AssemblyLogPresenter;
import com.furja.qc.utils.AssemblyAutoUpdateUtils;
import com.furja.qc.utils.Constants;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.StatusBarUtils;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.view.AutoCapTransitionMethod;
import com.furja.qc.beans.BadLogEntry;
import com.furja.qc.view.GridSpacingItemDecoration;
import com.furja.qc.view.KeyRecyclerAdapter;
import com.furja.qc.view.MyAutoAdapter;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.qc.utils.Constants.ACTION_UPDATE_APK;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.RESET_CONFIG;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.TextInputListener.INPUT_ERROR;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class AssemblyLogActivity extends BaseActivity implements AssemblyLogContract.View {
    @BindView(R.id.edit_keyboardShow)
    AutoCompleteTextView edit_keyboardShow; //显示异常码
    @BindView(R.id.edit_barCode)
    AppCompatEditText edit_barCode;
    @BindView(R.id.btn_upload_key)
    ImageButton btn_upload;
    @BindView(R.id.btn_edit_keyFrag)
    ImageButton btn_edit;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.materialInfo)
    View materialInfoLayout;
    @BindView(R.id.button_linear)
    View button_linear;
    @BindView(R.id.text_materialName)
    TextView materialName;
    @BindView(R.id.text_materialModel)
    TextView materialModel;
    @BindView(R.id.text_badCount)
    TextView textBadCount;
    @BindView(R.id.edit_operatorID)
    EditText edit_operator;
    @BindView(R.id.edit_note)
    EditText edit_note;
    @BindView(R.id.edit_productModel)
    AutoCompleteTextView edit_productModel;
    @BindView(R.id.edit_lineNumber)
    EditText edit_lineNumber;
    @BindView(R.id.cardView)
    View cardView;
    ImageButton btn_hiedOrShow;
    private KeyRecyclerAdapter recyclerAdapter;
    private SharpBus sharpBus;
    AssemblyLogPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblylog);
        ButterKnife.bind(this);
        MyAutoAdapter myAutoAdapter=new MyAutoAdapter(this,1);
        edit_keyboardShow.setAdapter(myAutoAdapter);
        MyAutoAdapter myAutoAdapter1=new MyAutoAdapter(this,2);
        edit_productModel.setAdapter(myAutoAdapter1);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        GridSpacingItemDecoration itemDecoration
                =new GridSpacingItemDecoration(4,10,true);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerAdapter=new KeyRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        sharpBus=SharpBus.getInstance();
        edit_keyboardShow.setOnItemClickListener((adapterView, view,position,id)->{
                String badCode= (String) adapterView.getItemAtPosition(position);
                showLog(badCode);
                String[] spit=badCode.split(Constants.INTER_SPLIT);
                badCode=spit[0];
                addBadItem(badCode);
            });
        edit_productModel.setOnItemClickListener((adapterView, view,position,id)->{
            String input= (String) adapterView.getItemAtPosition(position);
            presenter.setProductModel(input);
        });
        presenter=new AssemblyLogPresenter(this);
        listenBarCodeInput();
        new StatusBarUtils().setStatusBarColor(this,R.color.colorPrimary);
        sharpBus.register(UPDATE_BAD_COUNT)
                .subscribe(object->
                {
                    textBadCount.setText(""+recyclerAdapter.getTotalCount());
                });
        AutoCapTransitionMethod autoCapMethod
                =new AutoCapTransitionMethod();
        edit_productModel.setTransformationMethod(autoCapMethod);
        edit_lineNumber.setTransformationMethod(autoCapMethod);
        edit_operator.setTransformationMethod(autoCapMethod);

        Intent intent=getIntent();
        String action="";
        if (intent!=null&&intent.getAction()!=null)
            action=intent.getAction();
        if (action.equals(ACTION_UPDATE_APK))
            new AssemblyAutoUpdateUtils(this,true)
                    .checkUpdate();
    }

    /**
     * 往异常列表里添加item
     * @param badCode
     */
    private void addBadItem(String badCode) {
        if(TextUtils.isEmpty(badCode))
        {
            showToast("无有效输入,不予记录");
            return;
        }
        cleanKeyEdit();
        if (presenter.inputHasNull())
            return;
        if(recyclerAdapter!=null)
        {
            recyclerView
                    .scrollToPosition(recyclerAdapter.addItem(badCode));
        }
        textBadCount.setText(""+recyclerAdapter.getTotalCount());
    }

    @OnTextChanged(value = R.id.edit_operatorID, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editOperatorID(Editable s) {
        if(TextUtils.isEmpty(s))
            return;
        String input=s.toString().toUpperCase();
        presenter.setOperatorID(input);
    }

    @OnTextChanged(value = R.id.edit_lineNumber, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editLineNumber(Editable s) {
        if(TextUtils.isEmpty(s))
            return;
        String input=s.toString().toUpperCase();
        presenter.setLineNumber(input);
    }

    @OnTextChanged(value = R.id.edit_note, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editNote(Editable s) {
        if(TextUtils.isEmpty(s))
            return;
        String input=s.toString();
        presenter.setNote(input);
    }

    @OnTextChanged(value = R.id.edit_productModel, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editProductModel(Editable s) {
        if(TextUtils.isEmpty(s))
            return;
        String input=s.toString().toUpperCase();
//        presenter.setProductModel(input);
    }

    @OnClick({R.id.btn_edit_keyFrag,R.id.btn_upload_key,R.id.text_materialModel,
                R.id.text_materialName,R.id.label_materialModel,R.id.label_materialName})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //编辑按钮
            case R.id.btn_edit_keyFrag:
                try{
                    if(recyclerAdapter.getTotalCount()>0)
                    {
                        if(!recyclerAdapter.isEditing())
                        {
                            btn_edit.setImageResource(R.mipmap.ic_editing_src);
                        }
                        else
                        {
                            btn_edit.setImageResource(R.mipmap.ic_edit_src);
                        }
                        recyclerAdapter.setEditing(!recyclerAdapter.isEditing());
                    }
                    else
                        showToast("暂无可编辑的对象");
                }catch(Exception e){e.printStackTrace();}
                break;
            //上传按钮
            case R.id.btn_upload_key:
                if (presenter.getBadAssemblyLog()!=null
                        &&recyclerAdapter.getTotalCount()<1)
                    showToast("未记录,不予上传");
                else
                {
                    presenter.toUpload();
                }
                break;
            case R.id.text_materialModel:
            case R.id.text_materialName:
            case R.id.label_materialModel:
            case R.id.label_materialName:
                new MaterialDialog.Builder(this).title("重新录入物料信息")
                        .content("确定清空当前物料信息重新扫描吗?")
                        .negativeText("取消").positiveText("确定")
                        .onPositive((dialog, which) -> {
                            hideMaterialInfo();
                            edit_barCode.setText("");
                            edit_barCode.setEnabled(true);
                            edit_barCode.setVisibility(View.VISIBLE);
                            presenter.setMaterialInfo(null);
                        }).show().getWindow()
                        .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                break;
        }
    }

    /**
     * 将edit_keyboardShow
     * 的AutoCompleteTextView清空
     */
    private void cleanKeyEdit() {
        edit_keyboardShow.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(edit_barCode.getVisibility()==View.VISIBLE)
            if(keyCode==KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    ||keyCode==KeyEvent.KEYCODE_BRIGHTNESS_UP
                        ||keyCode==KEYCODE_F7)
            {
                edit_barCode.requestFocus();
            }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public List<BadLogEntry> getDatas() {
        return recyclerAdapter.getmData();
    }

    /**
     * 监听物料代码的输入框,及输入法
     */
    public void listenBarCodeInput()
    {
        TextInputListener listener
                =new TextInputListener();
        listener.bindEditText(edit_barCode);
        SharpBus.getInstance().register(TAG_SCAN_BARCODE,String.class)
                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(scanString->{
                    if(scanString.equals(INPUT_ERROR)
                            ||scanString.equals(INTERNET_ABNORMAL)
                            ||scanString.equals(NODATA_AVAILABLE))
                    {
                        edit_barCode.setEnabled(true);
                        edit_barCode.setText("");
                        showToast(scanString);
                    }
                    else
                    {
                        showToast("正在获取物料信息");
                        edit_barCode.setEnabled(false);
                        presenter.acquireMaterialInfo(scanString);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.menu_badassembly, menu);
        btn_hiedOrShow
                =(ImageButton) menu.findItem(R.id.action_hideOrShow).getActionView();
        btn_hiedOrShow.setBackgroundResource(android.R.color.transparent);
        btn_hiedOrShow.setImageResource(R.mipmap.ic_floating_hide);
        btn_hiedOrShow.setOnClickListener(v->{
                if(cardView.getVisibility()==View.VISIBLE)
                {
                    if(presenter.inputHasNull())
                        return;
                    cardView.setVisibility(View.GONE);
                    btn_hiedOrShow.setImageResource(R.mipmap.ic_floating_show);
                }
                else
                {
                    cardView.setVisibility(View.VISIBLE);
                    btn_hiedOrShow.setImageResource(R.mipmap.ic_floating_hide);
                }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(recyclerAdapter.getItemCount()>0&&id!=R.id.action_resetView)
            presenter.showAbortDialog(id);
        else
            performAction(id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void performAction(int id) {
        if (id == R.id.action_logOut)
        {
            Preferences.saveAutoLogin(false);
            QcApplication.setUserAndSave(null);
            toLogin();
        }
        else if(id==R.id.action_switchScene)
        {   //切换工作场景的
            Preferences.saveSourceType(""+TYPE_BADLOG_EMPTY);
            toLogin();
        }
        if(id==R.id.action_resetConfig)
        {
            Intent intent=new Intent(this,SplashActivity.class);
            intent.setAction(RESET_CONFIG);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.action_resetView)
        {
            new MaterialDialog.Builder(this)
                    .title(R.string.action_resetView)
                    .content("确定要重置页面吗?")
                    .positiveText("确定").negativeText("取消")
                    .onPositive((dialog, which) -> {
                        resetView(true);
                    }).show().getWindow()
                        .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);

        }
        else if(id==R.id.action_viewReport)
        {
            Intent intent=new Intent(this,AssemblyChartActivity.class);
            startActivity(intent);
        }
    }

    private void toLogin() {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void resetView(boolean isClear) {
        edit_note.setText("");
        textBadCount.setText("");
        presenter.resetFieldData();
        recyclerAdapter.clearData();
        btn_hiedOrShow.setImageResource(R.mipmap.ic_floating_hide);
        btn_edit.setImageResource(R.mipmap.ic_edit_src);
        cardView.setVisibility(View.VISIBLE);
        getWindow().getDecorView().invalidate();
        edit_barCode.setText("");
        edit_barCode.setEnabled(true);
        edit_barCode.setVisibility(View.VISIBLE);
        hideMaterialInfo();
        if(!isClear) {
            edit_operator.requestFocus();
            presenter
                    .setProductModel(getStringOfEdit(edit_productModel.getText()));
            presenter
                    .setLineNumber(getStringOfEdit(edit_lineNumber.getText()));
            presenter
                    .setOperatorID(getStringOfEdit(edit_operator.getText()));
        }
        else {
            edit_productModel.setText("");
            edit_lineNumber.setText("");
            edit_operator.setText("");
            edit_productModel.requestFocus();
            presenter.setMaterialInfo(null);
        }
    }

    @Override
    public void resetView() {
        resetView(false);
    }

    private String getStringOfEdit(Editable editable)
    {
        if(TextUtils.isEmpty(editable))
            return "";
        else
            return editable.toString();
    }
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMaterialInfo(MaterialInfo materialInfo) {
        if(materialInfo!=null)
        {
            showMaterialInfo();
            materialName.setText(materialInfo.getMaterialName());
            materialModel.setText(materialInfo.getNorm());
            if(TextUtils.isEmpty(edit_productModel.getText()))
            {
                edit_productModel.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null&&imm.isActive())
                    imm.showSoftInput(edit_productModel,0);
            }
        }
    }

    public void hideMaterialInfo()
    {
        materialInfoLayout.setVisibility(View.GONE);
        materialName.setText("");
        materialModel.setText("");
    }

    public void showMaterialInfo()
    {
        edit_barCode.setEnabled(true);
        edit_barCode.setText("");
        edit_barCode.setVisibility(View.GONE);
        materialInfoLayout.setVisibility(View.VISIBLE);
    }
}
