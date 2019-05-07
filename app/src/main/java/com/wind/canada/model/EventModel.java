package com.wind.canada.model;

/**
 * Created by yuanht on 2017/5/27.
 */

public class EventModel {
    public enum Event {
        LoginSuccess,Logout,CloseAll,
    }

    public EventModel(Event event, Object data) {
        this.event = event;
        this.data = data;
    }
    public EventModel(Event event) {
        this.event = event;
    }

    private Event event;
    private Object data;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
