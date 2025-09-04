package com.distributed.service;

import java.io.Serializable;

// If employee data is used in remote calls, it must implement Serializable.
public class EMP implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String eno;
    private String ename;
    private String title;

    public EMP(String no, String name, String title) {
        this.eno = no;
        this.ename = name;
        this.title = title;
    }

    public String getENO() {
        return eno;
    }

    public String getName() {
        return ename;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return eno + " " + ename + " " + title + " ";
    }
}
