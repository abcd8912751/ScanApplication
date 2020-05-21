package com.furja.qc.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.furja.qc.R;
import com.furja.qc.beans.BaseHttpResponse;
import com.furja.qc.beans.InspectionHistory;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetrofitHelper;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.TextInputListener;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.AutoCapTransitionMethod;
import com.furja.qc.view.CleanableEditText;
import com.furja.qc.view.InspectHistoryAdapter;
import com.furja.qc.view.WrapLinearLayoutManager;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class InspectionHistoryActivity extends BaseActivity{
    @BindView(R.id.recycler_history)
    RecyclerView recyclerHistory;
    @BindView(R.id.edit_workplace)
    CleanableEditText editWorkplace;
    @BindView(R.id.btn_search)
    Button btn_search;
    InspectHistoryAdapter historyAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injectionhistory);
        ButterKnife.bind(this);
        WrapLinearLayoutManager linearManager
                = WrapLinearLayoutManager.wrapLayoutManager(this);
        recyclerHistory.setLayoutManager(linearManager);
        historyAdapter
                =new InspectHistoryAdapter(R.layout.layout_inspecthistory_item);
        historyAdapter.bindToRecyclerView(recyclerHistory);
        btn_search.setOnClickListener(cl->{
            CharSequence charSequence = editWorkplace.getText();
            if (!TextUtils.isEmpty(charSequence))
                queryHistoryByWorkplace(charSequence.toString());
        });
        historyAdapter.setEmptyView(R.layout.empty_input_layout,recyclerHistory);
        DividerItemDecoration itemDecoration
                 = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerHistory.addItemDecoration(itemDecoration);
        AutoCapTransitionMethod autoCapMethod
                = new AutoCapTransitionMethod();
        editWorkplace.setTransformationMethod(autoCapMethod);
        listenInput();
        editWorkplace.requestFocus();
    }


    public void listenInput() {
        TextInputListener listener = new TextInputListener();
        listener.bindEditText(editWorkplace);
        SharpBus.getInstance().register(TAG_SCAN_BARCODE, String.class)
                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(scanString -> {
                    if (!scanString.equals("格式错误"))
                        queryHistoryByWorkplace(scanString);
                });
    }

    private void queryHistoryByWorkplace(String input) {
        input = input.toUpperCase();
        historyAdapter.setNewData(null);
        historyAdapter.setEmptyView(R.layout.empty_load_layout,recyclerHistory);
        String pattern="^[A-F]\\d{0,2}$";
        if(Pattern.matches(pattern,input)) {
            RetrofitHelper retrofitHelper
                    = RetrofitBuilder.getHelperByUrl(getVertxUrl());
            retrofitHelper.getInspectionHistory(input)
                    .subscribeOn(Schedulers.io())
                    .retryWhen(RetryWhenUtils.create())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.<BaseHttpResponse<List<InspectionHistory>>>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(response->{
                        if(response.getCode()>0) {
                            historyAdapter.setEmptyView(R.layout.empty_input_layout,recyclerHistory);
                            historyAdapter.setNewData(response.getResult());
                        }
                        else
                            historyAdapter.setEmptyView(R.layout.empty_nodata_layout,recyclerHistory);
                    },error->{
                       error.printStackTrace();
                        historyAdapter.setEmptyView(R.layout.empty_offline_layout,recyclerHistory);
                    });
        }
        else {
            showToast("输入的机台号与格式不符,请重试");
            editWorkplace.setText("");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
