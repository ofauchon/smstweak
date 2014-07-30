package com.oflabs.smstweak;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 7/31/11
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmsConditionAction {

    String condition = null ;
    String action = null;

    public SmsConditionAction(String condition, String action) {
        this.condition = condition;
        this.action = action;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
