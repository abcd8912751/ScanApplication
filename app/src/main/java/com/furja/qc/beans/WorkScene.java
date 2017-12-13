package com.furja.qc.beans;

/**
 * Created by zhangmeng on 2017/12/8.
 */

public interface WorkScene {
    String getDialogContent();
    String getDialogTitle();
    void selectSwitchYes();
    void selectSwitchNo();
    String getYesButtonLabel();    //根据工作场景返回
    String getNoButtonLabel();
}
