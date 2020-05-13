package com.furja.qc.databases;


import com.furja.qc.QcApplication;
import com.furja.qc.beans.BadLogEntry;

import java.util.List;

public class TourInspectionLog {
    private int FID;
    private String materialISN;
    private String operatorID;
    private String workplaceID;
    private String moldCavity;
    private String standardCycle;
    private String actualCycle;
    private String nozzleRatio;
    private int totalProduction;
    private int sampleSize;
    private int numberOfDefective;
    private String solution;
    private String traceResult;
    private String reasonOfDefective;
    private boolean isOk;
    private String workingClass;
    private List<BadLogEntry> badLogEntries; //外观检验记录的不良qingk
    private boolean existPreAssembly;  //是否做预装
    private boolean preAssemblyIsOk;
    private boolean existShockTest;    //是否做冲击测试
    private boolean shockTestIsOk;
    private String produceDate;
    private String timePeriod;
    private String badCode;
    private String moldNo;
    private long codeCount;
    private String closeStatus;
    private String batchDisposal;   //批号处置
    private int batchSubmission; //送检数量
    public TourInspectionLog() {
        operatorID=QcApplication.getUserID();
    }

    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getCloseStatus() {
        return closeStatus;
    }

    public void setCloseStatus(String closeStatus) {
        this.closeStatus = closeStatus;
    }

    public String getWorkplaceID() {
        return workplaceID;
    }

    public void setWorkplaceID(String workplaceID) {
        this.workplaceID = workplaceID;
    }

    public int getFID() {
        return FID;
    }

    public void setFID(int FID) {
        this.FID = FID;
    }

    public String getMoldCavity() {
        return moldCavity;
    }

    public String getWorkingClass() {
        return workingClass;
    }

    public void setWorkingClass(String workingClass) {
        this.workingClass = workingClass;
    }

    public void setMoldCavity(String moldCavity) {
        this.moldCavity = moldCavity;
    }

    public List<BadLogEntry> getBadLogEntries() {
        return badLogEntries;
    }

    public int getBatchSubmission() {
        return batchSubmission;
    }

    public void setBatchSubmission(int batchSubmission) {
        this.batchSubmission = batchSubmission;
    }

    public void setBadLogEntries(List<BadLogEntry> badLogEntries) {
        this.badLogEntries = badLogEntries;
    }

    public String getBatchDisposal() {
        return batchDisposal;
    }

    public void setBatchDisposal(String batchDisposal) {
        this.batchDisposal = batchDisposal;
    }

    public String getStandardCycle() {
        return standardCycle;
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

    public void setStandardCycle(String standardCycle) {
        this.standardCycle = standardCycle;
    }

    public String getMoldNo() {
        return moldNo;
    }

    public void setMoldNo(String moldNo) {
        this.moldNo = moldNo;
    }

    public String getActualCycle() {
        return actualCycle;
    }

    public void setActualCycle(String actualCycle) {
        this.actualCycle = actualCycle;
    }

    public String getNozzleRatio() {
        return nozzleRatio;
    }

    public void setNozzleRatio(String nozzleRatio) {
        this.nozzleRatio = nozzleRatio;
    }

    public int getTotalProduction() {
        return totalProduction;
    }

    public void setTotalProduction(int totalProduction) {
        this.totalProduction = totalProduction;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public int getNumberOfDefective() {
        return numberOfDefective;
    }

    public void setNumberOfDefective(int numberOfDefective) {
        this.numberOfDefective = numberOfDefective;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getTraceResult() {
        return traceResult;
    }

    public void setTraceResult(String traceResult) {
        this.traceResult = traceResult;
    }

    public String getReasonOfDefective() {
        return reasonOfDefective;
    }

    public void setReasonOfDefective(String reasonOfDefective) {
        this.reasonOfDefective = reasonOfDefective;
    }

    public boolean isExistPreAssembly() {
        return existPreAssembly;
    }

    public void setExistPreAssembly(boolean existPreAssembly) {
        this.existPreAssembly = existPreAssembly;
    }

    public boolean isPreAssemblyIsOk() {
        return preAssemblyIsOk;
    }

    public void setPreAssemblyIsOk(boolean preAssemblyIsOk) {
        this.preAssemblyIsOk = preAssemblyIsOk;
    }

    public boolean isExistShockTest() {
        return existShockTest;
    }

    public void setExistShockTest(boolean existShockTest) {
        this.existShockTest = existShockTest;
    }

    public boolean isShockTestIsOk() {
        return shockTestIsOk;
    }

    public void setShockTestIsOk(boolean shockTestIsOk) {
        this.shockTestIsOk = shockTestIsOk;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getBadCode() {
        return badCode;
    }

    public void setBadCode(String badCode) {
        this.badCode = badCode;
    }

    public long getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(long codeCount) {
        this.codeCount = codeCount;
    }
}
