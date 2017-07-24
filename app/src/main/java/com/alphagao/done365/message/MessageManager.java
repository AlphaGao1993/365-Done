package com.alphagao.done365.message;

import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alpha on 2017/3/15.
 * 事件处理中心
 * 单例模式：双重检查锁定
 */

public class MessageManager implements Subject {

    private List<Observer> observers;
    private List<Message> messages;
    private static volatile MessageManager manager;

    private MessageManager(List<Observer> observers) {
        this.observers = observers;
        messages = new ArrayList<>();
    }

    /*protected MessageManager(Parcel in) {
        messages = in.createTypedArrayList(Message.CREATOR);
    }*/

    public static MessageManager getInstance() {
        if (manager == null) {//第一次检查，避免不必要的同步
            synchronized (MessageManager.class) {
                if (manager == null) {//第二次检查，保证线程安全
                    manager = new MessageManager(new ArrayList<Observer>());
                }
            }
        }
        return manager;
    }

    public synchronized void publishMessage(Message message) {
        messages.add(message);
        notifyObservers();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }


    @Override
    public synchronized void removeObserver(Observer observer) {
        boolean deleted = false;
        for (Iterator<Observer> iterator = observers.iterator(); iterator.hasNext(); ) {
            Observer o = iterator.next();
            if (o == observer) {
                deleted = true;
            }
        }
        if (deleted) {
            observers.remove(observer);
        }
    }

    @Override
    public synchronized void notifyObservers() {
        while (messages.size() > 0) {
            Message m = null;
            for (Observer o : observers) {
                Message msg = messages.get(0);
                o.onNewMessage(msg);
                m = msg;
            }
            if (m != null) {
                messages.remove(m);
            }
        }
    }

/*    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(observers);
        dest.writeList(messages);
        dest.writeValue(manager);
    }

    public static final Creator<MessageManager> CREATOR = new Creator<MessageManager>() {
        @Override
        public MessageManager createFromParcel(Parcel in) {
            return new MessageManager(in);
        }

        @Override
        public MessageManager[] newArray(int size) {
            return new MessageManager[size];
        }
    };*/
}
