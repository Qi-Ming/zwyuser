package com.baowu.zwyuser.bean;

import java.io.Serializable;

public class BearingDevice implements Serializable {
    private String type = "轴承式";
    private String companyName;
    private String factoryName;
    private String siteName;
    private double dianjia;
    private double dianjib;
    private double dianjibaojing;
    private double dianjix1;
    private int id;
    private double jixiebaojing;
    private double jixiec;
    private double jixied;
    private double jixiex2;
    private String name;
    private int siteId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public double getDianjia() {
        return dianjia;
    }

    public void setDianjia(double dianjia) {
        this.dianjia = dianjia;
    }

    public double getDianjib() {
        return dianjib;
    }

    public void setDianjib(double dianjib) {
        this.dianjib = dianjib;
    }

    public double getDianjibaojing() {
        return dianjibaojing;
    }

    public void setDianjibaojing(double dianjibaojing) {
        this.dianjibaojing = dianjibaojing;
    }

    public double getDianjix1() {
        return dianjix1;
    }

    public void setDianjix1(double dianjix1) {
        this.dianjix1 = dianjix1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getJixiebaojing() {
        return jixiebaojing;
    }

    public void setJixiebaojing(double jixiebaojing) {
        this.jixiebaojing = jixiebaojing;
    }

    public double getJixiec() {
        return jixiec;
    }

    public void setJixiec(double jixiec) {
        this.jixiec = jixiec;
    }

    public double getJixied() {
        return jixied;
    }

    public void setJixied(double jixied) {
        this.jixied = jixied;
    }

    public double getJixiex2() {
        return jixiex2;
    }

    public void setJixiex2(double jixiex2) {
        this.jixiex2 = jixiex2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
}