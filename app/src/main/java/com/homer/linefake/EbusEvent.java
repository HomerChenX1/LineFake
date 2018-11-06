package com.homer.linefake;

import org.greenrobot.eventbus.EventBus;

// need public for index
public class EbusEvent {
    private String eventMsg;

    public EbusEvent(String eventMsg) {
            super();
            setEventMsg(eventMsg);
            }
    public String getEventMsg() { return eventMsg; }
    public void setEventMsg(String eventMsg) { this.eventMsg = eventMsg; }
}