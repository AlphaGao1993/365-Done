package com.alphagao.done365.message;

/**
 * Created by Alpha on 2017/3/15.
 * 可观察接口，通知事件的发出者
 */

public interface Subject {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
