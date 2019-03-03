package com.schneider.api.cost_codes.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileImport {

	private final static String SEPARATOR = ";";

	private final transient String sourceFile;

	private static final int COUNTRY = 0;

	private static final int ACCESS_COST_CODE = 1;

	private static final int REPORTING_ENTITY = 2;

	private static final int LOCAL_COST_CENTER_ID = 3;

	private static final int LOCAL_COST_CENTER_NAME = 4;

	private static final int INTERNAL_COST_CENTER_ID = 5;

	private static final int HOURLY_LABOR_RATE = 6;

	private static final int HOURLY_LABOR_RATE_DATE = 7;

	private static final int LAST_UPDATE_DATE = 8;

	private static final int YEARLY_PROJECTS_HOURS = 9;

	private static final int GROWTH_INDEX = 10;

	private static final int MONTHY_LABOR_RATE = 11;

	private static final int MONTHY_LABOR_RATE_DATE = 12;

	private static final int GLOBAL_COST_CENTER_ID = 13;

	private static final int RE_GLOBAL_COST_CENTER = 14;

	private static final int GLOBAL_COST_CENTER_NAME = 15;

	private static final int GLOBAL_PROFIT_CENTER_ID = 16;

	private static final int GLOBAL_PROFIT_CENTER_NAME = 17;

	private static final int LABOR_RATE_CURRENCY = 18;

	private static final int ACTION = 19;

	private ArrayList<LineImport> allLineList;

	public FileImport(final String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public List<LineImport> getData() throws IOException {
		this.allLineList = new ArrayList<LineImport>();
		BufferedReader buff = null;

		try {
			// Loading file
			buff = new BufferedReader(new InputStreamReader(new FileInputStream(this.sourceFile)));

			// To skip the header line
			buff.readLine();

			String ligne;

			while ((ligne = buff.readLine()) != null) {
				LineImport currentLine = new LineImport();
				final String[] row = ligne.split(SEPARATOR);

				if (row.length == 20) { // 19 columns in the csv file
					// get access country
					currentLine.setCountry(row[COUNTRY].trim());

					// get project accessCostCode.
					currentLine.setAccessCostCode(row[ACCESS_COST_CODE].trim());

					// get task reportingEntity.
					currentLine.setReportingEntity(row[REPORTING_ENTITY].trim());

					// get task localCostCenterID.
					currentLine.setLocalCostCenterID(row[LOCAL_COST_CENTER_ID].trim());

					// get new localCostCenterName.
					currentLine.setLocalCostCenterName(row[LOCAL_COST_CENTER_NAME].trim());

					// get new internalCosetCenterID.
					currentLine.setInternalCostCenterID(row[INTERNAL_COST_CENTER_ID].trim());

					// get task hourlyLaborRate.
					currentLine.setHourlyLaborRate(row[HOURLY_LABOR_RATE].trim());

					// get task hourlyLaborRateDate.
					currentLine.setHourlyLaborRateDate(row[HOURLY_LABOR_RATE_DATE].trim());

					// get task lastUpdateDate.
					currentLine.setLastUpdateDate(row[LAST_UPDATE_DATE].trim());

					// get the yearlyProjectsHours.
					currentLine.setYearlyProjectsHours(row[YEARLY_PROJECTS_HOURS].trim());

					// get the growthIndex
					currentLine.setGrowthIndex(row[GROWTH_INDEX].trim());

					// get the monthyLaborRate
					currentLine.setMonthyLaborRate(row[MONTHY_LABOR_RATE].trim());

					// get the monthyLaborRateDate
					currentLine.setMonthyLaborRateDate(row[MONTHY_LABOR_RATE_DATE].trim());

					// get the globalCostCenterID
					currentLine.setGlobalCostCenterID(row[GLOBAL_COST_CENTER_ID].trim());

					// get the reGlobalCostCenterID
					currentLine.setReGlobalCostCenter(row[RE_GLOBAL_COST_CENTER].trim());

					// get the globalCostCenterName
					currentLine.setGlobalCostCenterName(row[GLOBAL_COST_CENTER_NAME].trim());

					// get the globalProfitCenterID
					currentLine.setGlobalProfitCenterID(row[GLOBAL_PROFIT_CENTER_ID].trim());

					// get the globalProfitCenterName
					currentLine.setGlobalProfitCenterName(row[GLOBAL_PROFIT_CENTER_NAME].trim());

					// get the laborRateCurrency
					currentLine.setLaborRateCurrency(row[LABOR_RATE_CURRENCY].trim());

					// get the action
					currentLine.setAction(row[ACTION].trim());
				} else {
					throw new IOException(this.sourceFile + ";" + 5 + ";" + 2);
				}
				
				this.allLineList.add(currentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(this.sourceFile + ";" + 5 + ";" + 2);
		} finally {

			if (buff != null) {
				
				try {
					buff.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new IOException(this.sourceFile + ";" + 5 + ";" + 2);
				}
				
			}

		}

		return this.allLineList;
	}

}
