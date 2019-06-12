package com.schneider.api.cost_codes.business;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.schneider.api.cost_codes.data.FileImport;
import com.schneider.api.cost_codes.data.LineImport;
import com.schneider.api.cost_codes.data.SciformaImport;
import com.sciforma.psnext.api.Session;

public class CSVManager {

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

    // -------------------------------------------------------------------------------------------------------------
    /**
     * Constructor method.
     */
    public CSVManager(Session session) {
        this.session = session;
    }

    // -------------------------------------------------------------------------------------------------------------
    /**
     *
     * @param inputDir
     */
    public void execute(final String refDir, final Properties properties) {
        String requests = properties.getProperty("input_dir.requests", "requests");
        String successDir = properties.getProperty("input_dir.success", "success");
        String errorDir = properties.getProperty("input_dir.error", "error");
        String logDir = properties.getProperty("input_dir.log", "log");
        this.sciforma = new SciformaImport(this.session, properties);
        // String inputDir = refDir + File.separator + requests + File.separator;

        // create log and success/error if not existing
//    File successDirRep = new File(refDir + File.separator + successDir);
//    successDirRep.mkdir();
//    File errorDirRep = new File(refDir + File.separator + errorDir);
//    errorDirRep.mkdir();
//    File logDirRep = new File(logDir);
//    logDirRep.mkdir();
        try {
            // treat all .csv file from inputDir/Requests
            File dir = new File(requests);
            String[] list = dir.list();
            List<LineImport> lines = null;
            int count = 0;

            if (this.sciforma.openGlobal()) {

                for (int i = 0; i < list.length; i++) {
                    String sourceFile = list[i];
                    // work only with csv file
                    if (sourceFile.toLowerCase().endsWith(".csv")
                            && sourceFile.toLowerCase().startsWith("importccenterdv")) {
                        count++;
                        USER_LOG = UserLog.getInstance(sourceFile);
                        final FileImport fileImport = new FileImport(requests + File.separator + sourceFile);
                        LOG.debug("File = " + fileImport);
                        Date date = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

                        try {
                            lines = fileImport.getData();
                        } catch (IOException e) {
                            e.printStackTrace();
                            LOG.error("The program cannot treat the file " + sourceFile
                                    + " as it doesnt have the right format");
                            USER_LOG.error(sourceFile, 5, 2);

                            try {
                                generateLogFile(
                                        logDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                        + "_" + dateFormat.format(date) + "_KO" + ".log");
                                File sourceCSV = new File(requests + File.separator + sourceFile);
                                File errorCSV = new File(
                                        errorDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                        + "_" + dateFormat.format(date) + "_KO" + ".csv");
                                Files.move(sourceCSV.toPath(), errorCSV.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                LOG.error("The API cannot write the log file <" + logDir + File.separator
                                        + sourceFile.substring(0, sourceFile.lastIndexOf(".")) + "_"
                                        + dateFormat.format(date) + "_KO" + ".log" + ">");
                            }

                            continue;
                        }

                        boolean allLineOK = true;

                        for (LineImport line : lines) {
                            if (allLineOK) {
                                allLineOK = this.sciforma.checkLine(line);
                            }
                        }
                        if (allLineOK) {
                            try {

                                for (LineImport line : lines) {

                                    if (this.sciforma.checkLine(line)) {

                                        if (this.sciforma.insertLine(line)) {
                                            USER_LOG.error(line.getReportingEntity() + "_" + line.getLocalCostCenterID(), 0,
                                                    0);
                                        } else {
                                            allLineOK = false;
                                        }

                                    } else {
                                        allLineOK = false;
                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                LOG.error(e);

                                try {
                                    generateLogFile(
                                            logDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                            + "_" + dateFormat.format(date) + "_KO" + ".log");
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    LOG.error(e1.getMessage());
                                    LOG.error("The API cannot write the log file <" + logDir + File.separator
                                            + sourceFile.substring(0, sourceFile.lastIndexOf(".")) + "_"
                                            + dateFormat.format(date) + "_KO" + ".log" + ">");
                                }

                                continue;
                            }
                        }
                        // copy csv file in log directory
                        // delete csv file from source directory
                        try {
                            File sourceCSV = new File(requests + File.separator + sourceFile);
                            File copyCSV;

                            if (allLineOK) {
                                generateLogFile(
                                        logDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                        + "_" + dateFormat.format(date) + "_OK" + ".log");
                                // copyCSV = new File(refDir + File.separator + successDir + File.separator +
                                // sourceFile);
                                copyCSV = new File(successDir + File.separator
                                        + sourceFile.substring(0, sourceFile.lastIndexOf(".")) + "_"
                                        + dateFormat.format(date) + "_OK" + ".csv");
                            } else {
                                generateLogFile(
                                        logDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                        + "_" + dateFormat.format(date) + "_KO" + ".log");
                                // copyCSV = new File(refDir + File.separator + errorDir + File.separator +
                                // sourceFile);
                                copyCSV = new File(
                                        errorDir + File.separator + sourceFile.substring(0, sourceFile.lastIndexOf("."))
                                        + "_" + dateFormat.format(date) + "_KO" + ".csv");
                            }

                            Files.move(sourceCSV.toPath(), copyCSV.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                            LOG.error("The API cannot write the csv file <" + logDir + File.separator + sourceFile + ">");
                        }

                    }

                }

                if (count == 0) {
                    LOG.debug("No file found.");
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            LOG.error("The API cannot reach the directory <" + requests + ">");
        } finally {
            this.sciforma.closeGlobal();
        }

    }

    protected File generateLogFile(final String fileName) {

        try {
            final FileWriter logfile = new FileWriter(fileName);

            for (String trace : USER_LOG.getTraces()) {
                logfile.write(trace + "\n");
            }

            logfile.close();

            // copy log file to
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("could not write log file", e);
        }

        return new File(fileName);
    }

}
