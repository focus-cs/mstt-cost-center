/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.schneider.api.cost_codes.dao;

import com.schneider.api.cost_codes.business.UserLog;
import com.schneider.api.cost_codes.data.LineImport;
import com.schneider.api.cost_codes.database.DbConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author lahou
 */
public class BaseTamponDao {

    private DbConnection dbcon;
    private static final Logger LOG = Logger.getLogger(BaseTamponDao.class);
    private static final UserLog USER_LOG = UserLog.getInstance();
    private List<LineImport> importLineList;
    private final static String CREATION_OPER = "creation";
    private final static String RENAME_OPER = "rename";
    private final static String CLOSE_OPER = "close";
    private final static String REOPEN_OPER = "reopen";
    private static final String CHANGE_ID_OPER = "changeid";

    public BaseTamponDao(DbConnection dbcon) {
        this.dbcon = dbcon;
    }

    public DbConnection getDbcon() {
        return dbcon;
    }

    public void setDbcon(DbConnection dbcon) {
        this.dbcon = dbcon;
    }

    public List<LineImport> readDB() {
        try {
            importLineList = new ArrayList<LineImport>();
            String query;
            query = "SELECT \"Country\", \"Access Cost Code\", \"Reporting Entity\", \"Local Cost Center ID\", \"Local Cost Center name\", \"Internal Cost Center Id\", \"Hourly labor rate\", \"Hourly labor rate date\", \"Last update date\", \"Yearly Projects hours\", \"Growth Index\", \"Monthly labor rate\", \"Monthly labor rate date\", \"Global Cost Center Id\", \"RE global cost center\", \"Global Cost Center name\", \"Global Profit center ID\", \"Global Profit center name\", \"Labor rate currency\", \"Action\" FROM psnext.\"C5_mstt-cost-center_IN\"";
            LOG.debug(query);
            ResultSet rs = dbcon.executeRequete(query);
            while (rs.next()) {
                LineImport currentLine = new LineImport();
                //taskImport.setGlobalID(rs.getString(4));
            }

        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return importLineList;
    }

    public void cleanData() {
        try {
            String query;
            query = "DELETE FROM psnext.\"C5_mstt-cost-center_IN\"";
            LOG.debug(query);
            dbcon.executeRequete(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }

}
