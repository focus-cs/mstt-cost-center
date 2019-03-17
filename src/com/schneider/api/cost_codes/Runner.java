package com.schneider.api.cost_codes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.schneider.api.cost_codes.business.LineManager;
import com.schneider.api.cost_codes.dao.BaseTamponDao;
import com.schneider.api.cost_codes.data.LineImport;
import com.schneider.api.cost_codes.data.SciformaImport;
import com.schneider.api.cost_codes.database.DbConnection;
import com.schneider.api.cost_codes.database.DbController;
import com.schneider.api.cost_codes.database.DbError;
import com.sciforma.psnext.api.Session;
import java.util.List;

public class Runner {

    public static final String APP_INFO = "MSTT Cost Center v1.3";

    /**
     * Logger Class instance.
     */
    private static final Logger LOG = Logger.getLogger(Runner.class);
    private static DbConnection dbcon;
    private static  List<String> packages;
    

    // -------------------------------------------------------------------------------------------------------------
    /**
     * @param args
     */
    public static void main(final String[] args) {
		// configure log4j logger
        // PropertyConfigurator.configure(PROPERTY_FILE);
        PropertyConfigurator.configure(args[1]);

        // display application informations.
        LOG.info(APP_INFO);

        if (args.length == 2) {

            try {
                // load PSNext properties
                final Properties properties = readPropertiesFromFile(args[0]);
                LOG.debug(args[0] + " properties file loaded");

                try {
                    // init session
                    Session session = new SciformaImport().getSession(properties.getProperty("psnext.url"),
                            properties.getProperty("psnext.login"),
                            properties.getProperty("psnext.password"));

                    // Reconfigure log4j logger
                    Boolean allowPurge = Boolean.parseBoolean(properties.getProperty("allow.purge.data"));
                    PropertyConfigurator.configure(args[1]);
                    initDB(properties);
                    LOG.debug("Database connected .. ");
                    // Launch process
                    //new CSVManager(session).execute("", properties);
                    BaseTamponDao dao = new BaseTamponDao(dbcon);
                    packages = dao.getAllPackageName();
                    for (String currentPackage : packages) {
                        List<LineImport> lines = dao.readDB(currentPackage);
                        LineManager manager = new LineManager(session, dbcon);
                        if(manager.execute(properties, lines, currentPackage)){
                            if(allowPurge){
                                LOG.debug("All lines ok => clean data in DB ... ");
                                dao.cleanData(currentPackage);
                            }
                        }else{
                            LOG.debug("All lines are not ok => keep data in DB ... ");
                        }
                    }
                } catch (Exception e) {
                    // Exception to connect to PSNext
                    e.printStackTrace();
                    LOG.error(e.getMessage());
                }

            } catch (Exception e) {
                // exception to load properties file.
                e.printStackTrace();
                LOG.error(e);
            }

        } else {
            LOG.error("Use : Main psconnect.properties directory");
        }

        // Exit process
        Runtime.getRuntime().exit(0);
    }

    // -------------------------------------------------------------------------------------------------------------
    /**
     * Read Properties file using the path in input parameter
     *
     * @return Properties
     * @throws IOException
     */
    public static Properties readPropertiesFromFile(final String path) throws IOException {
        final Properties properties = new Properties();

        final File file = new File(path);

        final InputStream resourceAsStream = new FileInputStream(file);
        properties.load(resourceAsStream);
        return properties;
    }
    
    private static void initDB(Properties properties) {
        try {
            DbController dbc = new DbController();
            dbc.readDbConfiguration(properties);
            dbcon = new DbConnection();
            dbcon.setDbModel(dbc.getDbModel());
            dbcon.connexion();
        } catch (DbError ex) {
            LOG.error("Fail to connect DB");
        } catch (Exception ex) {
            LOG.error("Fail to connect DB");
        }
    }

	// -------------------------------------------------------------------------------------------------------------
}
