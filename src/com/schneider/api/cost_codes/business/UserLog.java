package com.schneider.api.cost_codes.business;

import com.schneider.api.cost_codes.dao.BaseTamponDao;
import com.schneider.api.cost_codes.database.DbConnection;
import java.util.ArrayList;
import java.util.List;

public class UserLog {

    private static UserLog classInstance;

    private static List<String> traces;

    //private static BaseTamponDao dao;

    public UserLog(DbConnection dbcon) {
        this.traces = new ArrayList<String>();
        //this.dao = new BaseTamponDao(dbcon);
    }

    public static UserLog getInstance() {
        return classInstance == null ? classInstance = new UserLog() : classInstance;
    }

    public static UserLog getInstance(String csvFileName) {
        classInstance = new UserLog();
        return classInstance;
    }
    
    /*public void setConnection(DbConnection dbcon) {
        this.dao = new BaseTamponDao(dbcon);
    }*/

    public UserLog() {
        traces = new ArrayList<String>();
    }

    public void info(final String msg) {
        traces.add("INFO: " + msg);
    }

    public List<String> getTraces() {
        return traces;
    }

    public void error(final String id, int errorCode, int breakcode) {
        traces.add(id + ";" + errorCode + ";" + breakcode);
    }
    
    public void error(final String id, int errorCode, int breakcode , String packageName) {
        //dao.writeLog(id, errorCode, breakcode, packageName);
    }

}
