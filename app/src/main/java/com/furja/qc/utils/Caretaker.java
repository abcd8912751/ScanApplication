package com.furja.qc.utils;

import java.util.ArrayList;
import java.util.List;

import static com.furja.qc.utils.Utils.showLog;

/**
 * 备忘录管理者
 */

public class Caretaker {
    private List<String> memos;  //存放undo数据集
    private int mIndex,MAX=30;    //当前undo、redo索引
    public Caretaker()
    {
        memos =new ArrayList<String>(MAX);
        mIndex=0;
    }

    /**
     * 清除备忘录中存储的操作
     */
    public void clear()
    {
        if(memos !=null)
            memos.clear();

        mIndex=0;
    }

    /**
     * 往UndoList里添加记录
     * @param memorandum
     */
    public void appendUndo(String memorandum)
    {
        saveMemo(memorandum);
    }

    private void saveMemo(String memorandum) {
        if(memos.size()>MAX)
            memos.remove(0);
        memos.add(new String(memorandum));
        mIndex= memos.size()-1;
    }

    public boolean isEmpty()
    {
        return memos.isEmpty();
    }

    public List<Long> getUndoMemo() {
        if(isEmpty())
            return null;
        mIndex=mIndex>0 ? --mIndex :mIndex;
        String undos = memos.get(mIndex);
        String strings[] = undos.split(",");
        List<Long> list = new ArrayList<Long>();
        for (String str : strings)
            list.add(Long.valueOf(str));
        return list;
    }


    /**
     * redo时获取记录
     * @return
     */
    public List<Long> getRedoMemo(String tempMemo)
    {
        if(isEmpty())
            return null;
        mIndex=mIndex < memos.size()-1 ? ++mIndex :mIndex;
        String undos= memos.get(mIndex);
        String strings[]=undos.split(",");
        List<Long> list=new ArrayList<Long>();
        for(String str:strings)
            list.add(Long.valueOf(str));
        return list;
    }

}