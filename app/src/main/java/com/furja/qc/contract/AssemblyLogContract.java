package com.furja.qc.contract;

import android.content.Context;

import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.BadLogEntry;

import java.util.List;

public interface AssemblyLogContract {
    interface Model {
    }

    interface View {
        List<BadLogEntry> getDatas();
        Context getContext();
        void showMaterialInfo(MaterialInfo materialInfo);
        void resetView();
        void performAction(int id);
    }

    interface Presenter {
        void resetFieldData();
    }
}
