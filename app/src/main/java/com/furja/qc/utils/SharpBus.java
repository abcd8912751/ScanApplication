package com.furja.qc.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 基于RxJava的SharpBus
 */

public class SharpBus {
    private volatile static SharpBus sharpBus;   //静态也是唯一的实例
    private ConcurrentHashMap<String, Subject> obseorvableMap;
    public static SharpBus getInstance()
    {
        if(sharpBus==null)
            sharpBus=new SharpBus();
        return sharpBus;
    }

    /**
     * 私有化构造方法以锁定全局只有一个实例
     */
    private SharpBus()
    {
        obseorvableMap=new ConcurrentHashMap<>();
    }

    /**
     * 注册观察者并放入相应Map
     *
     */
    public synchronized <T> Observable<T> register(@NonNull String tag) {
        Subject subject = PublishSubject.create().toSerialized();
        obseorvableMap.put(tag,subject);
        return subject;
    }


    /**
     * 解注册
     * @param tag
     */
    public synchronized void unregister(String tag)
    {
        obseorvableMap.remove(tag);
    }

    /**
     * 发送事件
     * @param tag
     * @param event
     */
    public  void post(@NonNull String tag,@NonNull Object event)
    {
        Subject subject=obseorvableMap.get(tag);
        synchronized (this)
        {
            if(subject!=null)
                subject.onNext(event);
        }
    }

}
