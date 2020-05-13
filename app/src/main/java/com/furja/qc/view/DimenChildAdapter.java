package com.furja.qc.view;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.qc.R;
import com.furja.qc.beans.DimenChildItem;
import com.furja.qc.utils.TextChanger;

import java.util.ArrayList;
import java.util.List;

import static com.furja.qc.utils.Constants.TYPE_FEELER_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_HARDNESS_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_NODIMEN_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_RULER_GAUGE;
import static com.furja.qc.utils.Constants.TYPE_PIN_GAUGE;
import static com.furja.qc.utils.TextChanger.flat;
import static com.furja.qc.utils.Utils.doubleOf;
import static com.furja.qc.utils.Utils.shakeOwnSelf;
import static com.furja.qc.utils.Utils.showToast;
import static com.furja.qc.utils.Utils.textOf;

public class DimenChildAdapter extends BaseMultiItemQuickAdapter<DimenChildItem,BaseViewHolder> {
    private double upperLimit;  //上限
    private double lowerLimit;  //下限
    private int childType;
    public DimenChildAdapter(@Nullable List<DimenChildItem> data) {
        super(data);
        addItemType(TYPE_PIN_GAUGE, R.layout.layout_pinchild_item);
        addItemType(TYPE_RULER_GAUGE, R.layout.layout_rulerchild_item);
        addItemType(TYPE_HARDNESS_GAUGE,R.layout.layout_hardnesschild_item);
        addItemType(TYPE_FEELER_GAUGE,R.layout.layout_feelerchild_item);
    }
    public DimenChildAdapter() {
        this(null);
    }

    @Override
    protected void convert(BaseViewHolder helper, DimenChildItem item) {
        int adapterPosition = helper.getAdapterPosition();
        helper.setText(R.id.label_check,"检验值 "+(adapterPosition+1));
        helper.getView(R.id.btn_deleteChild).setOnClickListener(v->{
            if (childSize()>1)
                remove(adapterPosition);
            else
                showToast("最后一个了,务必留下");
        });
        item.setPosition(adapterPosition);
        item.setItemType(getChildType());
        switch (getChildType()) {
            case TYPE_PIN_GAUGE:
                renderPinGaugeType(helper, item);
                break;
            case TYPE_RULER_GAUGE:
                renderRulerGaugeType(helper, item);
                break;
            case TYPE_HARDNESS_GAUGE:
                renderHardnessGaugeType(helper, item);
                break;
            case TYPE_FEELER_GAUGE:
                renderFeelerGaugeType(helper, item);
                break;
        }
    }

    /**
     * 渲染 塞尺检验方式 的type
     * @param helper
     * @param item
     */
    private void renderFeelerGaugeType(BaseViewHolder helper, DimenChildItem item) {
        EditText editFlatness=helper.getView(R.id.edit_flatness);
        editFlatness.setText(textOf(item.getFlatness()));
        if (item.getFlatness()>upperLimit) {
            editFlatness.setTextColor(Color.RED);
            if (item.getFlatness()!=0)
                shakeOwnSelf(editFlatness);
        }
        else
            editFlatness.setTextColor(Color.BLACK);
        if (editFlatness.getTag() == null){
            editFlatness.setTag("hadTextChanger");
            editFlatness.addTextChangedListener(flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    item.setFlatness(doubleOf(text));
                    if (item.getFlatness()>upperLimit)
                        editFlatness.setTextColor(Color.RED);
                    else
                        editFlatness.setTextColor(Color.BLACK);
                    int adapterPos = helper.getAdapterPosition();
                    if (adapterPos != -1)
                        mData.set(adapterPos,item);
                }
            }));
            editFlatness.setOnFocusChangeListener((v,hasFocus)->{
                if (!hasFocus) {
                    CharSequence charSequence=editFlatness.getText();
                    if(!TextUtils.isEmpty(charSequence)){
                        double dimen = doubleOf(charSequence.toString());
                        if(dimen>upperLimit){
                            showToast("输入的平面度不合格,请核实");
                            shakeOwnSelf(v);
                        }
                    }
                }else{
                    if (upperLimit <= 0)
                        showToast("请输入上限");
                    else if (TextUtils.isEmpty(editFlatness.getText()))
                        editFlatness.setHint(" ≤"+upperLimit );
                    showSoftInput(v);
                }
            });
        }

    }

    /**
     * 渲染 硬度检验 的Type
     * @param helper
     * @param item
     */
    private void renderHardnessGaugeType(BaseViewHolder helper, DimenChildItem item) {
        EditText editHardness=helper.getView(R.id.edit_hardness);
        editHardness.setText(textOf(item.getHardness()));
        if (item.getHardness()<lowerLimit||item.getHardness()>upperLimit) {
            editHardness.setTextColor(Color.RED);
            if (item.getHardness()!=0)
                shakeOwnSelf(editHardness);
        }
        else
            editHardness.setTextColor(Color.BLACK);
        if (editHardness.getTag() == null){
            editHardness.setTag("hadTextChanger");
            editHardness.addTextChangedListener(flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    item.setHardness(doubleOf(text));
                    if (item.getHardness()<lowerLimit||item.getHardness()>upperLimit)
                        editHardness.setTextColor(Color.RED);
                    else
                        editHardness.setTextColor(Color.BLACK);
                    int adapterPos = helper.getAdapterPosition();
                    if (adapterPos!=-1)
                        mData.set(adapterPos,item);
                }
            }));
            editHardness.setOnFocusChangeListener((v,hasFocus)->{
                if (!hasFocus) {
                    CharSequence charSequence=editHardness.getText();
                    if(!TextUtils.isEmpty(charSequence)){
                        double hardness=doubleOf(charSequence.toString());
                        if(hardness<lowerLimit||hardness>upperLimit){
                            showToast("输入的硬度不合格,请核实");
                            shakeOwnSelf(v);
                        }
                    }
                }else {
                    if (lowerLimit <= 0||upperLimit <= 0)
                        showToast("请输入相应硬度的上/下限");
                    else if (TextUtils.isEmpty(editHardness.getText()))
                        editHardness.setHint(" "+lowerLimit+"~"+upperLimit );
                    showSoftInput(v);
                }
            });
        }
    }

    private void renderPinGaugeType(BaseViewHolder helper, DimenChildItem item) {
        EditText editDimenEnd=helper.getView(R.id.edit_dimenEnd);
        EditText editDimenStart=helper.getView(R.id.edit_dimenStart);
        if(item.getEndDimen() != 0)
            editDimenEnd.setText(item.getEndDimen()+"");
        else
            editDimenEnd.setText("");
        if(item.getStartDimen() != 0)
            editDimenStart.setText(item.getStartDimen()+"");
        else
            editDimenStart.setText("");
        if (editDimenStart.getTag()==null) {
            editDimenStart.setTag("hasAddTheListener");
            TextChanger textWatcher = TextChanger.flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    CharSequence dimenEndInput = editDimenEnd.getText();
                    CharSequence dimenStartInput = editDimenStart.getText();
                    item.setEndDimen(doubleOf(dimenEndInput));
                    item.setStartDimen(doubleOf(dimenStartInput));
                    int adapterPos = helper.getAdapterPosition();
                    if (adapterPos!=-1)
                        mData.set(adapterPos,item);
                }
            });
            editDimenEnd.addTextChangedListener(textWatcher);
            editDimenStart.addTextChangedListener(textWatcher);
        }
    }

    private void renderRulerGaugeType(BaseViewHolder helper, DimenChildItem item) {
        EditText editDimen=helper.getView(R.id.edit_dimen);
        if (item.getDimen()!=0)
            editDimen.setText(item.getDimen()+"");
        else
            editDimen.setText("");
        if (item.getDimen()<lowerLimit||item.getDimen()>upperLimit) {
            editDimen.setTextColor(Color.RED);
            if (item.getDimen()!=0)
                shakeOwnSelf(editDimen);
        }
        else
            editDimen.setTextColor(Color.BLACK);
        if (editDimen.getTag()==null) {
            editDimen.addTextChangedListener(flat(text-> {
                            if (lowerLimit > 0&&upperLimit > 0) {
                                double dimen=doubleOf(text);
                                item.setDimen(dimen);
                                if (dimen!=0) {
                                    if (dimen < lowerLimit||dimen > upperLimit)
                                        editDimen.setTextColor(Color.RED);
                                    else
                                        editDimen.setTextColor(Color.BLACK);
                                }
                            }
                            int adapterPos = helper.getAdapterPosition();
                            if (adapterPos!=-1)
                                mData.set(adapterPos,item);
                        }
                    ));
            editDimen.setOnFocusChangeListener((v,hasFocus)->{
                if (!hasFocus) {
                    CharSequence charSequence=editDimen.getText();
                    if(!TextUtils.isEmpty(charSequence)){
                        double dimen=doubleOf(charSequence);
                        if(dimen<lowerLimit||dimen>upperLimit){
                            showToast("输入的尺寸不合格,请核实");
                            shakeOwnSelf(v);
                        }
                    }
                } else {
                    if (lowerLimit <= 0||upperLimit <= 0)
                        showToast("请输入相应尺寸的上/下限");
                    else if (TextUtils.isEmpty(editDimen.getText()))
                        editDimen.setHint(" "+lowerLimit+"~"+upperLimit );
                    showSoftInput(v);
                }
            });
            editDimen.setTag("添加TextChangedListener");
        }
    }

    public int childSize() {
        return this.mData.size();
    }

    public DimenChildAdapter setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
        return this;
    }

    public DimenChildAdapter setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
        if (lowerLimit > 0)
            notifyDataSetChanged();
        return this;
    }

    public DimenChildAdapter setChildType(int childType) {
        this.childType = childType;
        if(childType != TYPE_NODIMEN_GAUGE) {
            List<DimenChildItem> newItems = new ArrayList<>();
            for(DimenChildItem item : mData){
                item.setItemType(childType);
                newItems.add(item);
            }
            setNewData(newItems);
            RecyclerView recyclerView = getRecyclerView();
            if(recyclerView.getVisibility() != View.VISIBLE)
                recyclerView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public int getChildType() {
        return childType;
    }

    /**
     *检查是否有尺寸漏输或值过大
     * @return
     */
    public boolean hasInvalidInput() {
        int maxValue = 10000000;
        for (DimenChildItem childItem : mData) {
            switch (getChildType()) {
                case TYPE_RULER_GAUGE:
                    if(childItem.getDimen()==0) {
                        showToast("发现空的尺寸");
                        return true;
                    } else if(childItem.getDimen()>maxValue) {
                        showToast("尺寸过大");
                        return true;
                    }
                    break;
                case TYPE_PIN_GAUGE:
                    if(childItem.getStartDimen()==0
                            ||childItem.getEndDimen()==0) {
                        showToast("发现空的通/止尺寸");
                        return true;
                    }else if(childItem.getStartDimen()>maxValue
                            ||childItem.getEndDimen()>maxValue) {
                        showToast("通/止尺寸过大");
                        return true;
                    }
                    break;
                case TYPE_HARDNESS_GAUGE:
                    if(childItem.getHardness()==0) {
                        showToast("发现空的硬度值");
                        return true;
                    }else if(childItem.getHardness()>maxValue) {
                        showToast("硬度值过大");
                        return true;
                    }
                    break;
                case TYPE_FEELER_GAUGE:
                    if(childItem.getFlatness()==0) {
                        showToast("发现空的平面度");
                        return true;
                    }else if(childItem.getFlatness()>maxValue) {
                        showToast("平面度过大");
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    public void showSoftInput(View view) {
        Context context = getRecyclerView().getContext();
        InputMethodManager imm
                = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive())
            imm.showSoftInput(view, 0);
    }
}
