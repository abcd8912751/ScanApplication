package com.furja.qc.beans;

public class InspectionHistory {
    private int logType;
    private String produceDate;
    private String timePeriod;
    private String workplaceID;
    private String updateDate;
    private String note;
    public InspectionHistory(){

    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public String getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(String produceDate) {
        this.produceDate = produceDate;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getWorkplaceID() {
        return workplaceID;
    }

    public void setWorkplaceID(String workplaceID) {
        this.workplaceID = workplaceID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
