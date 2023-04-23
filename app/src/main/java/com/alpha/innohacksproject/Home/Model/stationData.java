package com.alpha.innohacksproject.Home.Model;

import androidx.annotation.Keep;

import java.io.Serializable;
@Keep
public class stationData implements Serializable {
    String name;
    String num;

    public stationData(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }
}
