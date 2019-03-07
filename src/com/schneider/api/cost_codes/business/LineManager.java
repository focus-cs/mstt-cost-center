/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.schneider.api.cost_codes.business;

import com.schneider.api.cost_codes.data.LineImport;
import com.schneider.api.cost_codes.data.SciformaImport;
import com.sciforma.psnext.api.Session;
import java.io.File;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author lahou
 */
public class LineManager {

    /**
     * UserLog instance.
     */
    private static UserLog USER_LOG = UserLog.getInstance();

    /**
     * Logger instance.
     */
    private static final Logger LOG = Logger.getLogger(CSVManager.class);

    /**
     * Sciforma Session.
     */
    private Session session;

    private SciformaImport sciforma;

    public LineManager(Session session) {
        this.session = session;
    }

    public boolean execute(final Properties properties, List<LineImport> lines) {
        this.sciforma = new SciformaImport(this.session, properties);
         boolean allLineOK = true;
        try {
            if (this.sciforma.openGlobal()) {
                try {
                    for (LineImport line : lines) {
                        if (this.sciforma.checkLine(line)) {
                            if (this.sciforma.insertLine(line)) {
                                USER_LOG.error(line.getReportingEntity() + "_" + line.getLocalCostCenterID(), 0,                                        0);
                            } else {
                                allLineOK = false;
                            }
                        } else {
                            allLineOK = false;
                        }
                    }
                    
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        } catch (NullPointerException e) {
            LOG.error(e);
        } finally {
            this.sciforma.closeGlobal();
        }
        return allLineOK;
    }

}
