package com.nju.urbangreen.zhenjiangurbangreen.events;

import java.io.Serializable;

/**
 * Created by ljy on 2017/9/27.
 */
//事件记录类
public class OneEvent implements Serializable{

    private String UGE_ID;
    private String UGE_Code;
    private String UGE_DamageDegree;
    private String UGE_Name;
    private boolean UGE_EventOrActivity;  //false 事件，true 活动
    private String UGE_Type;
    private String UGE_Description;
    private String UGE_Location;
    private String UGE_Time;
    private String UGE_Endtime;
    private String UGE_Reason;
    private String UGE_LoggerPID;
    private String UGE_LogTime;
    private String UGE_LastEditorPID;
    private String UGE_LastEditTime;
    private boolean UGE_IsConcluded;
    private String UGE_ConcludePID;
    private String UGE_ConcludeTime;
    private String UGE_ConcludeDescription;
    private String UGE_TotalFee;
    private String UGE_LostFee;
    private String UGE_Compensation;
    private String UGE_RelevantPerson;
    private String UGE_RelevantLicensePlate;
    private String UGE_RelevantContact;
    private String UGE_RelevantCompany;
    private String UGE_RelevantAddress;
    private String UGE_RelevantDescription;
    private String UGE_LastStateID;
    private String UGE_LoggerName;
    private String UGE_LastEditorName;
    private String UGE_ConcludeName;
    private String UGO_IDs;




    private String registrar;
    private String reason;
    private int state;//0 :未提交；1 :已提交

    public OneEvent(String UGE_ID,String UGE_Code, String UGE_DamageDegree, String UGE_Name, boolean UGE_EventOrActivity, String UGE_Type,
                    String UGE_Description, String UGE_Location, String UGE_Time, String UGE_Endtime, String UGE_Reason,
                    String UGE_LoggerPID, String UGE_LogTime, String UGE_LastEditorPID, String UGE_LastEditTime,
                    boolean UGE_IsConcluded, String UGE_ConcludePID, String UGE_ConcludeTime, String UGE_ConcludeDescription,
                    String UGE_TotalFee, String UGE_LostFee, String UGE_Compensation, String UGE_RelevantPerson,
                    String UGE_RelevantLicensePlate, String UGE_RelevantContact, String UGE_RelevantCompany, String UGE_RelevantAddress,
                    String UGE_RelevantDescription, String UGE_LastStateID, String UGE_LoggerName, String UGE_LastEditorName, String UGE_ConcludeName,String UGO_IDs)
    {
        this.UGE_ID = UGE_ID;
        this.UGE_Code = UGE_Code;
        this.UGE_DamageDegree = UGE_DamageDegree;
        this.UGE_Name = UGE_Name;
        this.UGE_EventOrActivity = UGE_EventOrActivity;
        this.UGE_Type = UGE_Type;
        this.UGE_Description = UGE_Description;
        this.UGE_Location = UGE_Location;
        this.UGE_Time = UGE_Time;
        this.UGE_Endtime = UGE_Endtime;
        this.UGE_Reason = UGE_Reason;
        this.UGE_LoggerPID = UGE_LoggerPID;
        this.UGE_LogTime = UGE_LogTime;
        this.UGE_LastEditorPID = UGE_LastEditorPID;
        this.UGE_LastEditTime = UGE_LastEditTime;
        this.UGE_IsConcluded = UGE_IsConcluded;
        this.UGE_ConcludePID = UGE_ConcludePID;
        this.UGE_ConcludeTime = UGE_ConcludeTime;
        this.UGE_ConcludeDescription = UGE_ConcludeDescription;
        this.UGE_TotalFee = UGE_TotalFee;
        this.UGE_LostFee = UGE_LostFee;
        this.UGE_Compensation = UGE_Compensation;
        this.UGE_RelevantPerson = UGE_RelevantPerson;
        this.UGE_RelevantLicensePlate = UGE_RelevantLicensePlate;
        this.UGE_RelevantContact = UGE_RelevantContact;
        this.UGE_RelevantCompany = UGE_RelevantCompany;
        this.UGE_RelevantAddress = UGE_RelevantAddress;
        this.UGE_RelevantDescription = UGE_RelevantDescription;
        this.UGE_LastStateID = UGE_LastStateID;
        this.UGE_LoggerName = UGE_LoggerName;
        this.UGE_LastEditorName = UGE_LastEditorName;
        this.UGE_ConcludeName = UGE_ConcludeName;
        this.UGO_IDs=UGO_IDs;
    }
    public OneEvent(){}

    public String getUGE_ID() {
        return UGE_ID;
    }
    public String getUGE_Code() {
        return UGE_Code;
    }

    public String getUGE_DamageDegree() {
        return UGE_DamageDegree;
    }

    public String getUGE_Name() {
        return UGE_Name;
    }

    public boolean isUGE_EventOrActivity() {
        return UGE_EventOrActivity;
    }

    public String getUGE_Type() {
        return UGE_Type;
    }

    public String getUGE_Location() {
        return UGE_Location;
    }

    public String getUGE_Time() {

        return UGE_Time;
    }

    public String getUGE_Description() {
        return UGE_Description;
    }

    public String getUGE_Endtime() {
        return UGE_Endtime;
    }

    public String getUGE_LastEditorPID() {
        return UGE_LastEditorPID;
    }

    public String getUGE_Reason() {
        return UGE_Reason;
    }

    public String getUGE_LoggerPID() {
        return UGE_LoggerPID;
    }

    public String getUGE_LogTime() {
        return UGE_LogTime;
    }

    public String getUGE_LastEditTime() {
        return UGE_LastEditTime;
    }

    public boolean isUGE_IsConcluded() {
        return UGE_IsConcluded;
    }

    public String getUGE_ConcludePID() {
        return UGE_ConcludePID;
    }

    public String getUGE_ConcludeTime() {
        return UGE_ConcludeTime;
    }

    public String getUGE_ConcludeDescription() {
        return UGE_ConcludeDescription;
    }

    public String getUGE_TotalFee() {
        return UGE_TotalFee;
    }

    public String getUGE_LostFee() {
        return UGE_LostFee;
    }

    public String getUGE_Compensation() {
        return UGE_Compensation;
    }

    public String getUGE_RelevantPerson() {
        return UGE_RelevantPerson;
    }

    public String getUGE_RelevantLicensePlate() {
        return UGE_RelevantLicensePlate;
    }

    public String getUGE_RelevantContact() {
        return UGE_RelevantContact;
    }

    public String getUGE_RelevantCompany() {
        return UGE_RelevantCompany;
    }

    public String getUGE_RelevantDescription() {
        return UGE_RelevantDescription;
    }

    public String getUGE_LastStateID() {
        return UGE_LastStateID;
    }

    public String getUGE_LoggerName() {
        return UGE_LoggerName;
    }

    public String getUGE_LastEditorName() {
        return UGE_LastEditorName;
    }

    public String getUGE_ConcludeName() {
        return UGE_ConcludeName;
    }

    public String getUGE_RelevantAddress() {
        return UGE_RelevantAddress;
    }

    public void setUGE_ID(String UGE_ID) {
        this.UGE_ID = UGE_ID;
    }
    public void setUGE_Code(String UGE_Code) {
        this.UGE_Code = UGE_Code;
    }

    public void setUGE_DamageDegree(String UGE_DamageDegree) {
        this.UGE_DamageDegree = UGE_DamageDegree;
    }

    public void setUGE_Name(String UGE_Name) {
        this.UGE_Name = UGE_Name;
    }

    public void setUGE_EventOrActivity(boolean UGE_EventOrActivity) {
        this.UGE_EventOrActivity = UGE_EventOrActivity;
    }

    public void setUGE_Type(String UGE_Type) {
        this.UGE_Type = UGE_Type;
    }

    public void setUGE_Description(String UGE_Description) {
        this.UGE_Description = UGE_Description;
    }

    public void setUGE_Location(String UGE_Location) {
        this.UGE_Location = UGE_Location;
    }

    public void setUGE_Time(String UGE_Time) {
        this.UGE_Time = UGE_Time;
    }

    public void setUGE_Endtime(String UGE_Endtime) {
        this.UGE_Endtime = UGE_Endtime;
    }

    public void setUGE_Reason(String UGE_Reason) {
        this.UGE_Reason = UGE_Reason;
    }

    public void setUGE_LoggerPID(String UGE_LoggerPID) {
        this.UGE_LoggerPID = UGE_LoggerPID;
    }

    public void setUGE_LogTime(String UGE_LogTime) {
        this.UGE_LogTime = UGE_LogTime;
    }

    public void setUGE_LastEditorPID(String UGE_LastEditorPID) {
        this.UGE_LastEditorPID = UGE_LastEditorPID;
    }

    public void setUGE_LastEditTime(String UGE_LastEditTime) {
        this.UGE_LastEditTime = UGE_LastEditTime;
    }

    public void setUGE_IsConcluded(boolean UGE_IsConcluded) {
        this.UGE_IsConcluded = UGE_IsConcluded;
    }

    public void setUGE_ConcludePID(String UGE_ConcludePID) {
        this.UGE_ConcludePID = UGE_ConcludePID;
    }

    public void setUGE_ConcludeTime(String UGE_ConcludeTime) {
        this.UGE_ConcludeTime = UGE_ConcludeTime;
    }

    public void setUGE_ConcludeDescription(String UGE_ConcludeDescription) {
        this.UGE_ConcludeDescription = UGE_ConcludeDescription;
    }

    public void setUGE_TotalFee(String UGE_TotalFee) {
        this.UGE_TotalFee = UGE_TotalFee;
    }

    public void setUGE_LostFee(String UGE_LostFee) {
        this.UGE_LostFee = UGE_LostFee;
    }

    public void setUGE_Compensation(String UGE_Compensation) {
        this.UGE_Compensation = UGE_Compensation;
    }

    public void setUGE_RelevantPerson(String UGE_RelevantPerson) {
        this.UGE_RelevantPerson = UGE_RelevantPerson;
    }

    public void setUGE_RelevantLicensePlate(String UGE_RelevantLicensePlate) {
        this.UGE_RelevantLicensePlate = UGE_RelevantLicensePlate;
    }

    public void setUGE_RelevantContact(String UGE_RelevantContact) {
        this.UGE_RelevantContact = UGE_RelevantContact;
    }

    public void setUGE_RelevantCompany(String UGE_RelevantCompany) {
        this.UGE_RelevantCompany = UGE_RelevantCompany;
    }

    public void setUGE_RelevantAddress(String UGE_RelevantAddress) {
        this.UGE_RelevantAddress = UGE_RelevantAddress;
    }

    public void setUGE_RelevantDescription(String UGE_RelevantDescription) {
        this.UGE_RelevantDescription = UGE_RelevantDescription;
    }

    public void setUGE_LastStateID(String UGE_LastStateID) {
        this.UGE_LastStateID = UGE_LastStateID;
    }

    public void setUGE_LoggerName(String UGE_LoggerName) {
        this.UGE_LoggerName = UGE_LoggerName;
    }

    public void setUGE_LastEditorName(String UGE_LastEditorName) {
        this.UGE_LastEditorName = UGE_LastEditorName;
    }

    public void setUGE_ConcludeName(String UGE_ConcludeName) {
        this.UGE_ConcludeName = UGE_ConcludeName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRegistrar() {
        return registrar;
    }

    public void setRegistrar(String registrar) {
        this.registrar = registrar;
    }
    public String getUGO_IDs() {
        return UGO_IDs;
    }

    public void setUGO_IDs(String UGO_IDs) {

        this.UGO_IDs = UGO_IDs;
    }

}



