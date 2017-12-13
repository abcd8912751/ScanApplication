package com.furja.qc.beans;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHKEY;

/**
 * Created by zhangmeng on 2017/12/8.
 */

public class KeyWorkScene implements WorkScene {
    @Override
    public String getDialogContent() {

        return "当前工作场景是装配车间";
    }

    @Override
    public String getDialogTitle() {
        return "是否更换工作场景";
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
        return "切换";
    }

    @Override
    public String getNoButtonLabel() {
        return "否";
    }

}
