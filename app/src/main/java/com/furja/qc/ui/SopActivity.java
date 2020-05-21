package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.furja.qc.R;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.presenter.InjectionLogPresenter;
import com.furja.qc.services.NetworkChangeReceiver;
import com.furja.qc.utils.Constants;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.view.CleanableEditText;
import com.furja.qc.view.SopRecyclerAdapter;
import com.furja.qc.view.WrapLinearLayoutManager;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.TextInputListener.INPUT_ERROR;
import static com.furja.qc.utils.Utils.showToast;

public class SopActivity extends BaseActivity  implements InjectionLogContract.View {
    InjectionLogPresenter presenter;
    @BindView(R.id.text_materialName)
    TextView textMaterialName;
    @BindView(R.id.text_materialModel)
    TextView textMaterialModel;
    @BindView(R.id.edit_barCode)
    CleanableEditText editBarCode;
    @BindView(R.id.materialInfo)
    ConstraintLayout materialInfoView;
    @BindView(R.id.recycler_sop)
    RecyclerView recyclerSop;
    private MaterialInfo materialInfo;
    SopRecyclerAdapter sopAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFresco();
        setContentView(R.layout.activity_sop);
        ButterKnife.bind(this);
        presenter = new InjectionLogPresenter(this);
        initView();
    }

    private void initView() {
        TextInputListener listener
                = new TextInputListener();
        listener.bindEditText(editBarCode);
        WrapLinearLayoutManager wrapLinearLayoutManager
                = new WrapLinearLayoutManager(this);
        recyclerSop.setLayoutManager(wrapLinearLayoutManager);
        sopAdapter = new SopRecyclerAdapter(R.layout.recycler_sop_item);
        sopAdapter.bindToRecyclerView(recyclerSop);
        sopAdapter.setEmptyView(R.layout.empty_sop_layout,recyclerSop);
        sopAdapter.setOnItemClickListener((adapter,view,position)->{
            Intent intent=new Intent(SopActivity.this, ZoomImageActivity.class);
            intent.putStringArrayListExtra(Constants.ZOOM_EXTRA_NAME,
                    (ArrayList<String>) sopAdapter.getData());
            intent.setAction(position+"");
            startActivity(intent);
        });
        listenSharpBus();
    }

    private void listenSharpBus() {
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
    }

    @Override
    public void resetView() {
        materialInfoView
                .setVisibility(View.GONE);
        enableBarCodeInput();
        presenter.resetFieldData();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event!=null)
            if (keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                    || keyCode == KEYCODE_F7) {
                if(editBarCode.isEnabled()) {
                    enableBarCodeInput();
                    materialInfoView
                            .setVisibility(View.GONE);
                    editBarCode.requestFocus();
                }
            }
        return super.onKeyDown(keyCode, event);
    }

    private void enableBarCodeInput() {
        editBarCode.setText("");
        editBarCode.setEnabled(true);
        editBarCode.setVisibility(View.VISIBLE);
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
            List<String> newUrls = new ArrayList<String>(),urls=materialInfo.getUrls();
            for (String url:urls) {
                if(NetworkChangeReceiver.isInnerNet())
                    url=url.replace("www.nbfurja.com","192.168.8.46");
                newUrls.add(url);
            }
            sopAdapter.setNewData(newUrls);
        }
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LifecycleOwner getLifeCycle() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fresco.getImagePipeline().clearMemoryCaches();
        Fresco.shutDown();
    }
}
