package com.alpha.innohacksproject.Home.Model;


import androidx.annotation.Keep;

import java.io.Serializable;
@Keep
public class Excel_data implements Serializable {

    public String A;
    public String B;
    public String C;
    public String D;
    public String E;
    public String F;
    public String G;
    public String H;
    public String I;
    public String J;
    public String K;
    public String L;
    public String M;
    public String N;
    public String date;
    public String type;
    public String pushkey;
    public String reminded;
    public String seen;
    public String date_of_alert;
    public String sent;
    public String number;
    public String url;
    public String uid;

    public Excel_data(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String date, String type, String pushkey, String reminded, String seen, String date_of_alert, String sent, String number, String url, String uid) {
        this.A = a;
        this.B = b;
        this.C = c;
        this.D = d;
        this.E = e;
        this.F = f;
        this.G = g;
        this.H = h;
        this.I = i;
        this.J = j;
        this.K = k;
        this.L = l;
        this.M = m;
        this.N = n;
        this.date = date;
        this.type = type;
        this.pushkey = pushkey;
        this.reminded = reminded;
        this.seen = seen;
        this.date_of_alert = date_of_alert;
        this.sent = sent;
        this.number = number;
        this.url = url;
        this.uid = uid;
    }

    public Excel_data() {
    }

    public String getUid() {
        return uid;
    }

    public String getUrl() {
        return url;
    }

    public String getSent() {
        return sent;
    }

    public String getSeen() {
        return seen;
    }

    public String getAa() {
        return A;
    }

    public String getBb() {
        return B;
    }

    public String getCc() {
        return C;
    }

    public String getDd() {
        return D;
    }

    public String getEe() {
        return E;
    }

    public String getFf() {
        return F;
    }

    public String getGg() {
        return G;
    }

    public String getHh() {
        return H;
    }

    public String getIi() {
        return I;
    }

    public String getJj() {
        return J;
    }

    public String getKk() {
        return K;
    }

    public String getLl() {
        return L;
    }

    public String getMm() {
        return M;
    }

    public String getNn() {
        return N;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getPushkey() {
        return pushkey;
    }

    public String getReminded() {
        return reminded;
    }

    public String getDate_of_alert() {
        return date_of_alert;
    }

    public String getNumber() {
        return number;
    }
}

