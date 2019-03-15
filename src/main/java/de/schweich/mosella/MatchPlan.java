/*
 * Copyright 2018 Commerzbank AG
 *
 * mosella-game-schedule
 * MatchPlan.java
 */
package de.schweich.mosella;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author GA2REMO <timo.reichert@commerzbank.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchPlan {

    private boolean success;
    private String html;
    private int lastIndex;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    @Override
    public String toString() {
        return "MatchPlan{" 
                + "\n\tsuccess=" + success 
                + "\n\thtml=" + html 
                + "\n\tlastIndex=" + lastIndex 
                + "\n}";
    }
    
    

}
