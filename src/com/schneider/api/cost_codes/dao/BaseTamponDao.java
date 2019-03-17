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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 *
 * @author lahou
 */
public class BaseTamponDao {

    private DbConnection dbcon;
    private static final Logger LOG = Logger.getLogger(BaseTamponDao.class);
    private List<LineImport> importLineList;
    private SimpleDateFormat sdf;
    private Calendar cal;

    public BaseTamponDao(DbConnection dbcon) {
        this.dbcon = dbcon;
        this.cal = Calendar.getInstance();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    public DbConnection getDbcon() {
        return dbcon;
    }

    public void setDbcon(DbConnection dbcon) {
        this.dbcon = dbcon;
    }

    public List<LineImport> readDB(String currentPackage) {
        try {
            importLineList = new ArrayList<LineImport>();
            String query;
            query = "SELECT \"Country\", \"Access Cost Code\", \"Reporting Entity\", \"Local Cost Center ID\", \"Local Cost Center name\", "
                    + "\"Internal Cost Center Id\", \"Hourly labor rate\", \"Hourly labor rate date\", \"Last update date\", "
                    + "\"Yearly Projects hours\", \"Growth Index\", \"Monthly labor rate\", \"Monthly labor rate date\", \"Global Cost Center Id\", "
                    + "\"RE global cost center\", \"Global Cost Center name\", \"Global Profit center ID\", \"Global Profit center name\", \"Labor rate currency\", "
                    + "\"Action\" FROM psnext.\"C5_mstt-cost-center_IN\" WHERE \"PackageName\" ='" +currentPackage+"'";
            LOG.debug(query);
            ResultSet rs = dbcon.executeRequete(query);
            while (rs.next()) {
                LineImport currentLine = new LineImport();
                currentLine.setAccessCostCode(rs.getString(2));
                currentLine.setAction(rs.getString(19));
                currentLine.setCountry(rs.getString(1));
                currentLine.setGlobalCostCenterID(rs.getString(14));
                currentLine.setGlobalCostCenterName(rs.getString(15));
                currentLine.setGlobalProfitCenterID(rs.getString(16));
                currentLine.setGlobalProfitCenterName(rs.getString(17));
                currentLine.setGrowthIndex(rs.getString(11));
                currentLine.setHourlyLaborRate(rs.getString(7));
                currentLine.setHourlyLaborRateDate(rs.getString(8));
                currentLine.setInternalCostCenterID(rs.getString(6));
                currentLine.setLaborRateCurrency(rs.getString(18));
                currentLine.setLastUpdateDate(rs.getString(9));
                currentLine.setLocalCostCenterID(rs.getString(4));
                currentLine.setLocalCostCenterName(rs.getString(5));
                currentLine.setMonthyLaborRate(rs.getString(12));
                currentLine.setMonthyLaborRateDate(rs.getString(13));
                currentLine.setReGlobalCostCenter(rs.getString(15));
                currentLine.setReportingEntity(rs.getString(3));
                currentLine.setYearlyProjectsHours(rs.getString(10));
                importLineList.add(currentLine);
                //taskImport.setGlobalID(rs.getString(4));
            }

        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return importLineList;
    }

    public void cleanData(String packageName) {
        try {
            String query;
            query = "DELETE FROM psnext.\"C5_mstt-cost-center_IN\" WHERE \"PackageName\" ='" +packageName+"'";
            LOG.debug(query);
            dbcon.executeRequete(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }
    
    public void writeLog(String id, int errorCode, int breakCode, String packageName){
        String query = "INSERT INTO psnext.\"C5_mstt-cost-center_OUT\"(\"ID\", \"ERROR_CODE\", \"BREAK_CODE\", \"PackageName\", \"ExecutionDate\") "
                     + "VALUES ('"+id+"', "+errorCode+", "+breakCode+", '"+packageName+"', '"+sdf.format(cal.getTime())+"');";
        try {
            LOG.debug(query);
            dbcon.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }
    
    public List<String> getAllPackageName(){
        List<String> packages = new ArrayList<>();
        try {
            String query;
            query = "UPDATE psnext.\"C5_mstt-cost-center_IN\" SET \"PackageName\"='"+UUID.randomUUID().toString()+"' WHERE \"PackageName\" is null and \"ExecutionDate\" is null";
            LOG.debug(query);
            dbcon.executeUpdate(query);
            query = "SELECT distinct \"PackageName\" FROM psnext.\"C5_mstt-cost-center_IN\" WHERE \"ExecutionDate\" is null";
            LOG.debug(query);
            ResultSet rs = dbcon.executeRequete(query);
            while (rs.next()) {
                packages.add(rs.getString(1).trim());
            }
            query = "UPDATE psnext.\"C5_mstt-cost-center_IN\" SET \"ExecutionDate\"='"+sdf.format(cal.getTime())+"' WHERE \"ExecutionDate\" is null";
            LOG.debug(query);
            dbcon.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return packages;
    }

}
