package com.alpha.innohacksproject.Home.Model;


import androidx.annotation.Keep;

import java.io.Serializable;
@Keep
public class smsData implements Serializable {
    public String currentDate;
    public String case_no;
    public String crime_no;
    public String ps;
    public String deadline;
    public String tid;
    public String mob_no;
    public String case_type;
    public String pushkey;

    public smsData(String currentDate, String case_no, String crime_no, String ps, String deadline, String tid, String mob_no, String case_type, String pushkey) {
        this.currentDate = currentDate;
        this.case_no = case_no;
        this.crime_no = crime_no;
        this.ps = ps;
        this.deadline = deadline;
        this.tid = tid;
        this.mob_no = mob_no;
        this.case_type = case_type;
        this.pushkey = pushkey;
    }

    public smsData() {
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCase_no() {
        return case_no;
    }

    public void setCase_no(String case_no) {
        this.case_no = case_no;
    }

    public String getCrime_no() {
        return crime_no;
    }

    public void setCrime_no(String crime_no) {
        this.crime_no = crime_no;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMob_no() {
        return mob_no;
    }

    public void setMob_no(String mob_no) {
        this.mob_no = mob_no;
    }

    public String getCase_type() {
        return case_type;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }
}
