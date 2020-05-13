package com.furja.qc.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.furja.qc.R;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.ClearableEditTextWithIcon;
import com.furja.qc.view.InspectHistoryAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionHistoryActivity extends BaseActivity{
    @BindView(R.id.recycler_history)
    RecyclerView recyclerHistory;
    @BindView(R.id.edit_workplace)
    ClearableEditTextWithIcon editWorkplace;
    @BindView(R.id.btn_search)
    Button btn_search;
    InspectHistoryAdapter historyAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injectionhistory);
        ButterKnife.bind(this);
//        WrapLinearLayoutManager linearManager
//                = WrapLinearLayoutManager.wrapLayoutManager(this);
//        recyclerHistory.setLayoutManager(linearManager);
//        historyAdapter
//                =new InspectHistoryAdapter(R.layout.layout_inspecthistory_item);
//        historyAdapter.bindToRecyclerView(recyclerHistory);
//        btn_search.setOnClickListener(cl->{
//            CharSequence charSequence = editWorkplace.getText();
//            if (!TextUtils.isEmpty(charSequence))
//                queryHistoryByWorkplace(charSequence.toString());
//        });
//        historyAdapter.setEmptyView(R.layout.empty_input_layout,recyclerHistory);
//        DividerItemDecoration itemDecoration
//                 = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        recyclerHistory.addItemDecoration(itemDecoration);
//        AutoCapTransitionMethod autoCapMethod
//                = new AutoCapTransitionMethod();
//        editWorkplace.setTransformationMethod(autoCapMethod);
//        listenInput();
//        editWorkplace.requestFocus();
    }


//    public void listenInput() {
//        TextInputListener listener = new TextInputListener();
//        listener.bindEditText(editWorkplace);
//        SharpBus.getInstance().register(TAG_SCAN_BARCODE, String.class)
//                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(scanString -> {
//                    if (!scanString.equals("格式错误"))
//                        queryHistoryByWorkplace(scanString);
//                });
//    }

//    private void queryHistoryByWorkplace(String input) {
//        input = input.toUpperCase();
//        historyAdapter.setNewData(null);
//        historyAdapter.setEmptyView(R.layout.empty_load_layout,recyclerHistory);
//        String pattern="^[A-F]\\d{0,2}$";
//        if(Pattern.matches(pattern,input)) {
//            RetrofitHelper retrofitHelper
//                    = RetrofitBuilder.getHelperByUrl(getVertxUrl());
//            retrofitHelper.getInspectionHistory(input)
//                    .subscribeOn(Schedulers.io())
//                    .retryWhen(RetryWhenUtils.create())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .as(AutoDispose.<BaseHttpResponse<List<InspectionHistory>>>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                    .subscribe(response->{
//                        if(response.getCode()>0) {
//                            historyAdapter.setEmptyView(R.layout.empty_input_layout,recyclerHistory);
//                            historyAdapter.setNewData(response.getResult());
//                        }
//                        else
//                            historyAdapter.setEmptyView(R.layout.empty_nodata_layout,recyclerHistory);
//                    },error->{
//                       error.printStackTrace();
//                        historyAdapter.setEmptyView(R.layout.empty_offline_layout,recyclerHistory);
//                    });
//        }
//        else {
//            showToast("输入的机台号与格式不符,请重试");
//            editWorkplace.setText("");
//        }
//    }


    @Override
    protected void onDestroy() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive()) {
            imm.hideSoftInputFromWindow(editWorkplace.getWindowToken(), 0);
        }
        Utils.fixInputMethodMemoryLeak(this);
        editWorkplace.clearSelf();
        editWorkplace.clearFocus();
        super.onDestroy();
    }
}
