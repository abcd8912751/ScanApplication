package com.furja.qc.beans;

import static com.furja.qc.utils.Constants.BUTTON_FRAGMENT_TITLE;
import static com.furja.qc.utils.Constants.KEY_FRAGMENT_TITLE;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHKEY;

/**
 * Created by zhangmeng on 2017/12/8.
 */

public class EmptyWorkScene implements WorkScene {
    @Override
    public String getDialogContent() {
        return "";
    }

    @Override
    public String getDialogTitle() {
        return "选择工作场景";
    }

    @Override
    public void selectSwitchYes() {
        Preferences.saveSourceType(TYPE_BADLOG_WITHBTN+"");
    }

    @Override
    public void selectSwitchNo() {
        Preferences.saveSourceType(TYPE_BADLOG_WITHKEY+"");
    }

    @Override
    public String getYesButtonLabel() {
        return BUTTON_FRAGMENT_TITLE;
    }

    @Override
    public String getNoButtonLabel() {
        return KEY_FRAGMENT_TITLE;
    }
}
