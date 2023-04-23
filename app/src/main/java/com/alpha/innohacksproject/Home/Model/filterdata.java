package com.alpha.innohacksproject.Home.Model;

import androidx.annotation.Keep;

import java.io.Serializable;
@Keep
public class filterdata implements Serializable {
    String cn,year,stn,dis_n;

    public filterdata() {
    }

    public filterdata(String cn, String year, String stn, String dis_n) {
        this.cn = cn;
        this.year = year;
        this.stn = stn;
        this.dis_n = dis_n;
    }
    public String getCn() {
        return cn;
    }

    public String getYear() {
        return year;
    }

    public String getStn() {
        return stn;
    }

    public String getDis_n() {
        return dis_n;
    }
}
