package com.furja.qc.view;

import android.content.res.Resources;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.qc.R;
import com.furja.qc.beans.DimenChildItem;
import com.furja.qc.beans.DimenGroupItem;
import com.furja.qc.utils.TextChanger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static com.furja.qc.utils.Constants.TYPE_FEELER_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_HARDNESS_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_NODIMEN_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_RULER_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_PIN_GAUGE;
import static com.furja.qc.utils.Utils.doubleOf;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;
import static com.furja.qc.view.WrapLinearLayoutManager.wrapLayoutManager;

public class DimenGroupAdapter extends BaseQuickAdapter<DimenGroupItem,BaseViewHolder> {
    List<DimenChildAdapter> dimenChildAdapters;
    NestedScrollView nestedScrollView;
    boolean isRender=true;  //该标记 为true时,点击检验方式条时不申请焦点
    public DimenGroupAdapter(int layoutResId) {
        super(layoutResId);
        dimenChildAdapters=new ArrayList<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, DimenGroupItem groupItem) {
        DimenChildAdapter dimenChildAdapter = initDimenChild(helper,groupItem);
        helper.setText(R.id.groupItem_title,"尺寸 "+(helper.getAdapterPosition()+1));
        RecyclerView recyclerView=helper.getView(R.id.recycler_dimenChild);
        ImageButton btn_addDimenChild
                =helper.getView(R.id.btn_newDimenInput);
        AppCompatTextView text_ruleGauge = helper.getView(R.id.text_ruleGauge);
        AppCompatTextView text_pinGauge = helper.getView(R.id.text_pinGauge);
        AppCompatTextView text_noDimenGauge = helper.getView(R.id.text_noDimenGauge);
        AppCompatTextView text_hardnessGauge = helper.getView(R.id.text_hardnessGauge);
        AppCompatTextView text_feelerGauge = helper.getView(R.id.text_feelerGauge);
        EditText edit_lowerLimit=helper.getView(R.id.edit_lowerLimit);
        text_hardnessGauge.setOnClickListener(v -> {
            if (!isRender)
                edit_lowerLimit.requestFocus();
            selectGauge(text_hardnessGauge);
            unSelect(text_ruleGauge);
            unSelect(text_pinGauge);
            unSelect(text_noDimenGauge);
            unSelect(text_feelerGauge);
            groupItem.setGaugeType(TYPE_HARDNESS_GAUGE);
            dimenChildAdapter.setChildType(TYPE_HARDNESS_GAUGE);
            btn_addDimenChild.setVisibility(View.VISIBLE);
            int groupIndex = helper.getAdapterPosition();
            if (groupIndex!=-1)
                mData.set(groupIndex,groupItem);
        });
        text_feelerGauge.setOnClickListener(v -> {
            if (!isRender) {
                edit_lowerLimit.requestFocus();
            }
            selectGauge(text_feelerGauge);
            unSelect(text_ruleGauge);
            unSelect(text_pinGauge);
            unSelect(text_noDimenGauge);
            unSelect(text_hardnessGauge);
            groupItem.setGaugeType(TYPE_FEELER_GAUGE);
            dimenChildAdapter.setChildType(TYPE_FEELER_GAUGE);
            btn_addDimenChild.setVisibility(View.VISIBLE);
            int groupIndex = helper.getAdapterPosition();
            if (groupIndex!=-1)
                mData.set(groupIndex,groupItem);
        });
        text_ruleGauge.setOnClickListener(v->{
            if (!isRender)
                edit_lowerLimit.requestFocus();
            selectGauge(text_ruleGauge);
            unSelect(text_pinGauge);
            unSelect(text_noDimenGauge);
            unSelect(text_feelerGauge);
            unSelect(text_hardnessGauge);
            groupItem.setGaugeType(TYPE_RULER_GAUGE);
            dimenChildAdapter.setChildType(TYPE_RULER_GAUGE);
            btn_addDimenChild.setVisibility(View.VISIBLE);
            int groupIndex = helper.getAdapterPosition();
            if (groupIndex!=-1) {
                mData.set(groupIndex,groupItem);
            }
        });
        text_pinGauge.setOnClickListener( v-> {
            if (!isRender){
                edit_lowerLimit.requestFocus();
            }
            selectGauge(text_pinGauge);
            unSelect(text_ruleGauge);
            unSelect(text_noDimenGauge);
            unSelect(text_feelerGauge);
            unSelect(text_hardnessGauge);
            groupItem.setGaugeType(TYPE_PIN_GAUGE);
            dimenChildAdapter.setChildType(TYPE_PIN_GAUGE);
            btn_addDimenChild.setVisibility(View.VISIBLE);
            int groupIndex = helper.getAdapterPosition();
            if (groupIndex!=-1)
                mData.set(groupIndex,groupItem);
        });
        text_noDimenGauge.setOnClickListener( v-> {
            if (!isRender){
                edit_lowerLimit.requestFocus();
            }
            selectGauge(text_noDimenGauge);
            unSelect(text_pinGauge);
            unSelect(text_ruleGauge);
            unSelect(text_feelerGauge);
            unSelect(text_hardnessGauge);
            groupItem.setGaugeType(TYPE_NODIMEN_GAUGE);
            dimenChildAdapter.setChildType(TYPE_NODIMEN_GAUGE);
            recyclerView.setVisibility(View.GONE);
            btn_addDimenChild.setVisibility(View.GONE);
            int groupIndex = helper.getAdapterPosition();
            if (groupIndex!=-1)
                mData.set(groupIndex,groupItem);
        });
        switch (groupItem.getGaugeType()) {
            case TYPE_PIN_GAUGE:
                text_pinGauge.performClick();
                break;
            case TYPE_RULER_GAUGE:
                text_ruleGauge.performClick();
                break;
            case TYPE_FEELER_GAUGE:
                text_feelerGauge.performClick();
                break;
            case TYPE_HARDNESS_GAUGE:
                text_hardnessGauge.performClick();
                break;
            case TYPE_NODIMEN_GAUGE:
                text_noDimenGauge.performClick();
                break;
        }
        btn_addDimenChild.setOnClickListener(view->{
            dimenChildAdapter
                    .addData(new DimenChildItem(dimenChildAdapter.getChildType()));
        });
        helper.getView(R.id.btn_delete).setOnClickListener(l->{
            if (size()>1) {
                final int adapterPosition = helper.getAdapterPosition();
                if (adapterPosition!=0)
                    new MaterialDialog.Builder(l.getContext())
                        .title("确定要删除这一尺寸吗?")
                        .positiveText("确定").negativeText("取消")
                        .onNegative((dialog, which) -> {dialog.cancel();})
                        .onPositive((dialog, which) -> {
                            dialog.cancel();
                            showLog("删除前:"+dimenChildAdapters.toString());
                            if(adapterPosition<dimenChildAdapters.size())
                                dimenChildAdapters.remove(adapterPosition);
                            remove(adapterPosition);
                            getRecyclerView().requestLayout();
                            showLog(dimenChildAdapter.toString()+"删除后:"+dimenChildAdapters.toString());
                        }).show().getWindow()
                        .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                else
                    showToast("首尺寸请保留");
            }
            else
                showToast("最后一个尺寸,须保留");
        });
        renderOffsetAndLimit(helper, groupItem);
    }


    private void renderOffsetAndLimit(BaseViewHolder helper, DimenGroupItem groupItem) {
        TextInputLayout input_lowerLimit
                = helper.getView(R.id.input_lowerLimit);
        EditText edit_upperLimit=helper.getView(R.id.edit_upperLimit);
        EditText edit_lowerLimit=helper.getView(R.id.edit_lowerLimit);
        EditText edit_upperOffset=helper.getView(R.id.edit_upperOffset);
        EditText edit_lowerOffset=helper.getView(R.id.edit_lowerOffset);
        EditText edit_standardDimen=helper.getView(R.id.edit_standardDimen);
        DimenChildAdapter dimenChildAdapter
                = dimenChildAdapters.get(helper.getAdapterPosition());
        if (groupItem.getStandardDimen()==0)
            edit_standardDimen.setText("");
        else
            edit_standardDimen.setText(groupItem.getStandardDimen()+"");
        if (groupItem.getUpperOffset()==0)
            edit_upperOffset.setText("");
        else
            edit_upperOffset.setText(groupItem.getUpperOffset()+"");
        if (groupItem.getLowerOffset()==0)
            edit_lowerOffset.setText("");
        else
            edit_lowerOffset.setText(groupItem.getLowerOffset()+"");
        if (groupItem.getLowerLimit()==0)
            edit_lowerLimit.setText("");
        else{
            edit_lowerLimit.setText(groupItem.getLowerLimit()+"");
            dimenChildAdapter.setLowerLimit(groupItem.getLowerLimit());
        }
        if (groupItem.getUpperLimit()==0)
            edit_upperLimit.setText("");
        else{
            edit_upperLimit.setText(groupItem.getUpperLimit()+"");
            dimenChildAdapter.setUpperLimit(groupItem.getUpperLimit());
        }
        if (edit_lowerLimit.getTag()==null) {
            edit_lowerLimit.setTag("hadFocusChangeListener");
            TextChanger textWatcher = TextChanger.flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    int groupIndex = helper.getAdapterPosition();
                    DimenChildAdapter dimenChildAdapter
                            = dimenChildAdapters.get(groupIndex);
                    CharSequence upperSequence=edit_upperLimit.getText();
                    CharSequence lowerSequence=edit_lowerLimit.getText();
                    double upperLimit=0,lowerLimit=0;
                    if (!TextUtils.isEmpty(upperSequence))
                        upperLimit=doubleOf(upperSequence.toString());
                    if (!TextUtils.isEmpty(lowerSequence)) {
                        lowerLimit=doubleOf(lowerSequence.toString());
                        if (lowerLimit < 0) {
                            input_lowerLimit.setErrorEnabled(true);
                            input_lowerLimit.setError("下限 <0,请修正");
                        } else
                            input_lowerLimit.setErrorEnabled(false);
                    }else
                        input_lowerLimit.setErrorEnabled(false);
                    if (lowerLimit>upperLimit) {
                        input_lowerLimit.setErrorEnabled(true);
                        input_lowerLimit.setError("下限 >上限,请修正");
                        lowerLimit=0;
                    }
                    groupItem.setLowerLimit(lowerLimit);
                    groupItem.setUpperLimit(upperLimit);
                    if(TextUtils.isEmpty(upperSequence))
                        dimenChildAdapter.setUpperLimit(-1);
                    else
                        dimenChildAdapter.setUpperLimit(upperLimit);
                    if(TextUtils.isEmpty(lowerSequence))
                        dimenChildAdapter.setLowerLimit(-1);
                    else
                        dimenChildAdapter.setLowerLimit(lowerLimit);
                    if (groupIndex!=-1)
                        mData.set(groupIndex,groupItem);
                }
            });
            TextChanger textWatcher1 = TextChanger.flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    CharSequence standardDimenText = edit_standardDimen.getText();
                    CharSequence upperoffsetText = edit_upperOffset.getText();
                    double standardDimen = 0,upperOffset = 0,lowerOffset = 0;
                    if (!TextUtils.isEmpty(standardDimenText)) {
                        standardDimen = doubleOf(standardDimenText.toString());
                        groupItem.setStandardDimen(standardDimen);
                    }
                    else
                        groupItem.setStandardDimen(0);
                    if (!TextUtils.isEmpty(upperoffsetText)) {
                        upperOffset = doubleOf(upperoffsetText.toString());
                        groupItem.setUpperOffset(upperOffset);
//                        if(!isRender)
//                            edit_lowerOffset.setText(upperoffsetText);
                    }
                    else{
                        groupItem.setUpperOffset(0);
                        edit_lowerOffset.setText("");
                    }
                    if (standardDimen != 0&&!isRender) {
                        if (upperOffset !=0 ){
                            double value = standardDimen+upperOffset;
                            edit_upperLimit.setText(String.format("%.3f",value));
                        }
                    }
                    int groupIndex = helper.getAdapterPosition();
                    if (groupIndex!=-1)
                        mData.set(groupIndex,groupItem);
                }
            });
            TextChanger textWatcher2 = TextChanger.flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    CharSequence standardDimenText = edit_standardDimen.getText();
                    CharSequence loweroffsetText = edit_lowerOffset.getText();
                    double standardDimen = 0,lowerOffset = 0;
                    if (!TextUtils.isEmpty(standardDimenText))
                        standardDimen = doubleOf(standardDimenText.toString());
                    if (!TextUtils.isEmpty(loweroffsetText)) {
                        lowerOffset = doubleOf(loweroffsetText.toString());
                        groupItem.setLowerOffset(lowerOffset);
                    }
                    else
                        groupItem.setLowerOffset(0);
                    if (standardDimen != 0)
                        if (lowerOffset != 0)
                            edit_lowerLimit.setText(String.format("%.3f",standardDimen-lowerOffset));
                    int groupIndex = helper.getAdapterPosition();
                    if (groupIndex!=-1)
                        mData.set(groupIndex,groupItem);
                }
            });
            edit_lowerLimit.addTextChangedListener(textWatcher);
            edit_upperLimit.addTextChangedListener(textWatcher);
            edit_standardDimen.addTextChangedListener(textWatcher1);
            edit_upperOffset.addTextChangedListener(textWatcher1);
            edit_lowerOffset.addTextChangedListener(textWatcher2);
        }
    }

    /**
     * 初始化显示DimenChildAdapter(设定FootBar等)
     * @param helper
     * @param groupItem
     */
    private DimenChildAdapter initDimenChild(BaseViewHolder helper, DimenGroupItem groupItem) {
        int groupIndex = helper.getAdapterPosition();
        DimenChildAdapter dimenChildAdapter;
        if (dimenChildAdapters.size()>groupIndex)
            dimenChildAdapter = dimenChildAdapters.get(groupIndex);
        else {
            dimenChildAdapter = new DimenChildAdapter(groupItem.getChildItems());
            dimenChildAdapters.add(dimenChildAdapter);
            RecyclerView recyclerView=helper.getView(R.id.recycler_dimenChild);
            recyclerView.setLayoutManager(wrapLayoutManager(recyclerView.getContext()));
            dimenChildAdapter.bindToRecyclerView(recyclerView);
        }
        RadioButton radioBtn_Ok=helper.getView(R.id.radioBtn_ok);
        RadioButton radioBtn_Ng=helper.getView(R.id.radioBtn_ng);
        if (groupItem.isOk())
            radioBtn_Ok.setChecked(true);
        else
            radioBtn_Ng.setChecked(true);
        if (radioBtn_Ng.getTag()==null) {
            CompoundButton.OnCheckedChangeListener changeListener
                    =new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked)
                        return;
                    if (buttonView.getId()==R.id.radioBtn_ok){
                        radioBtn_Ng.setChecked(false);
                        groupItem.setOk(true);
                    }else{
                        radioBtn_Ok.setChecked(false);
                        groupItem.setOk(false);
                    }
                    int adapterPosion = helper.getAdapterPosition();
                    if (adapterPosion!=-1)
                        mData.set(adapterPosion,groupItem);
                }
            };
            radioBtn_Ng.setOnCheckedChangeListener(changeListener);
            radioBtn_Ok.setOnCheckedChangeListener(changeListener);
            radioBtn_Ng.setTag("已添加ChangeListener");
        }
        return dimenChildAdapter;
    }


    public void clear() {
        mData.clear();
        dimenChildAdapters.clear();
        notifyDataSetChanged();
    }

    private void selectGauge(TextView textView){
        Resources resources = textView.getResources();
        int selectColor
                = resources.getColor(R.color.colorPrimary);
        textView.setTextColor(selectColor);
        textView.setTextSize(18);
        textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_selectend,0);
    }

    private void unSelect(TextView textView){
        Resources resources = textView.getResources();
        int unselectColor = resources.getColor(R.color.color_text);
        textView.setTextColor(unselectColor);
        textView.setTextSize(14);
        textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
    }

    public int size() {
        return mData.size();
    }

    /**
     * 检查是否存在未输入上下限或空尺寸
     * 将DimenChildItem列表的值转换成JSON字符串赋值,将List置空
     * @return
     */
    public List<DimenGroupItem> toJsonGroupsWithNoEmpty() {
        List<DimenGroupItem> groupItems=new ArrayList<>();
        int index=0;
        for (DimenGroupItem groupItem : mData){
            DimenChildAdapter childAdapter  = dimenChildAdapters.get(index);
            groupItem.setGaugeType(childAdapter.getChildType());
            if(groupItem.hasInvalidValue()) {
                scrollToErrorItem(index);
                return null;
            }
            int gaugeType = groupItem.getGaugeType();
            if (gaugeType != TYPE_NODIMEN_GAUGE) {
                if (childAdapter.hasInvalidInput()) {
                    scrollToErrorItem(index);
                    return null;
                }
                else {
                    List<DimenChildItem> dimenChildItems = childAdapter.getData();
                    groupItem.setDimenJson(JSON.toJSONString(dimenChildItems));
                }
            }
            groupItem.setChildItems(null);
            groupItems.add(groupItem);
            index++;
        }
        return groupItems;
    }

    /**
     * 滚动至异常Item
     * @param index
     */
    private void scrollToErrorItem(int index) {
        try {
            View view = getRecyclerView().getChildAt(index);
            view.findViewById(R.id.recycler_dimenChild).requestFocus();
            nestedScrollView.scrollTo(0, (int) (view.getY() + view.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Data数据,将json字符串反序列成集合,再setNewData()
     * @param oldGroupItems
     */
    public void setDataOfJSON(List<DimenGroupItem> oldGroupItems) {
        startRenderAutoFalse();
        setDataOfJSON(oldGroupItems,false);
    }

    /**
     * 设置Data数据,将json字符串反序列成集合,再setNewData()
     * 当isDefault为真时,只显示每个尺寸的上下限,子布局初始化呈现
     * @param oldGroupItems
     */
    public void setDataOfJSON(List<DimenGroupItem> oldGroupItems,boolean isDefault) {
        List<DimenGroupItem> groupItems = new ArrayList<>();
        for (DimenGroupItem groupItem : oldGroupItems) {
            if (isDefault)
                groupItem.setGaugeType(TYPE_RULER_GAUGE);
            if (groupItem.getGaugeType()!=TYPE_NODIMEN_GAUGE) {
                List<DimenChildItem> childItems;
                if (groupItem.getDimenJson()!=null)
                    childItems = JSON.parseArray(groupItem.getDimenJson(),DimenChildItem.class);
                else {
                    childItems = new ArrayList<>();
                    childItems.add(new DimenChildItem());
                    childItems.add(new DimenChildItem());
                    childItems.add(new DimenChildItem());
                }
                groupItem.setChildItems(childItems);
            }
            groupItems.add(groupItem);
        }
        dimenChildAdapters.clear();
        setNewData(groupItems);
    }

    public NestedScrollView getNestedScrollView() {
        return nestedScrollView;
    }

    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        this.nestedScrollView = nestedScrollView;
    }


    private void scrollTo(float y) {
        if (nestedScrollView != null)
            nestedScrollView.scrollTo(0, (int) y);
    }

    /**
     * 延时 2S ——>渲染完成后将该标记置 False
     */
    public void startRenderAutoFalse() {
        isRender = true;
        Observable.just("").delay(2, TimeUnit.SECONDS)
                .subscribe(event->{
                    isRender = false;
                });
    }
}
