package com.schneider.api.cost_codes.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.schneider.api.cost_codes.business.UserLog;
import com.sciforma.psnext.api.DataFormatException;
import com.sciforma.psnext.api.DataViewRow;
import com.sciforma.psnext.api.FieldDefinitions;
import com.sciforma.psnext.api.Global;
import com.sciforma.psnext.api.LockException;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.PickListItem;
import com.sciforma.psnext.api.Session;
import com.sciforma.psnext.api.SharedPickList;
import com.sciforma.psnext.api.SystemData;

public class SciformaImport {

	private static final UserLog USER_LOG = UserLog.getInstance();

	private static final Logger LOG = Logger.getLogger(FileImport.class);

	private Session session;

	private Global global;

	private List<DataViewRow> dataViewRowList;

	private String csv_numeric_separator;

	private String psnext_numeric_separator;

	public SciformaImport() {

	}
	
	@SuppressWarnings("unchecked")
	public SciformaImport(Session session, Properties props) {
		this.session = session;
		this.global = new Global();
		this.csv_numeric_separator = props.getProperty("csv.numeric.separator", ".");
		this.psnext_numeric_separator = props.getProperty("psnext.numeric.separator", ".");
		
		try {
			this.dataViewRowList = (ArrayList<DataViewRow>) session.getDataViewRowList("Cost Centers Management",
					this.global);
		} catch (PSException e) {
			USER_LOG.error("Cost Centers Management dataview", 6, 2);
			LOG.error(e);
			e.printStackTrace();
		}

	}

	public Session getSession(String url, String login, String password) {
		Session session = null;
		
		try {
			LOG.debug("Connecting to " + url);
			session = new Session(url);
			session.login(login, password.toCharArray());
			LOG.debug("Connected to PSNext");
		} catch (PSException e) {
			LOG.error(e);
			e.printStackTrace();
		}
		
		return session;
	}
	
	public boolean checkLine(LineImport line) {
		boolean verif = false;
		String reportingEntity = line.getReportingEntity();
		String localCostCenterID = line.getLocalCostCenterID();
		String internalCostCenterID = line.getInternalCostCenterID();
		String action = line.getAction();
		String country = line.getCountry();
		String accessCostCode = line.getAccessCostCode();
		String hourlyLaborRate = line.getHourlyLaborRate();
		String hourlyLaborRateDate = line.getHourlyLaborRateDate();
		String lastUpdateDate = line.getLastUpdateDate();
		String yearlyProjectsHours = line.getYearlyProjectsHours();
		String growthIndex = line.getGrowthIndex();
		String monthyLaborRate = line.getMonthyLaborRate();
		String labourRateCurrency = line.getLaborRateCurrency();
		String monthyLaborRateDate = line.getMonthyLaborRateDate();
		String globalCostCenterID = line.getGlobalCostCenterID();
		String reGlobalCostCenter = line.getReGlobalCostCenter();
		String globalCostCenterName = line.getGlobalCostCenterName();
		String globalProfitCenterID = line.getGlobalProfitCenterID();
		String globalProfitCenterName = line.getGlobalProfitCenterName();
		FieldDefinitions fd;
		
		try {
			fd = (FieldDefinitions) session.getSystemData(SystemData.FIELD_DEFINITIONS);
		} catch (PSException e) {
			e.printStackTrace();
			LOG.error("Field Definitions error");
			LOG.error(e);
			return false;
		}

		@SuppressWarnings("unchecked")
		ArrayList<SharedPickList> pickList = (ArrayList<SharedPickList>) fd.getSharedPickLists();

		/****************** Test Reporting entity *************************/

//		verif = false;
//
//		for (SharedPickList picklist : pickList) {
//
//			if (picklist.getName().equals("Reporting entity")) {
//				@SuppressWarnings("unchecked")
//				List<PickListItem> picklistitem = (List<PickListItem>) picklist.getPickListItems();
//
//				for (int i = 0; i < picklistitem.size(); i++) {
//
//					PickListItem currentItem = (PickListItem) picklistitem.get(i);
//
//					if (reportingEntity.equals(currentItem.getName())) {
//						verif = true;
//						break;
//					}
//
//				}
//
//			}
//
//		}
//
//		if (!verif) {
//			USER_LOG.error(reportingEntity + "_" + localCostCenterID, 10, 1);
//			return false;
//		}
		
		if (reportingEntity.isEmpty()) {
			USER_LOG.error(reportingEntity + "_" + localCostCenterID, 2, 1);
			return false;
		}

		/****************** Test Local cost center ID *************************/

		if (localCostCenterID.length() >= 255) {
			USER_LOG.error(reportingEntity + "_" + localCostCenterID, 3, 1);
			return false;
		}

		/****************** Test Internal Cost Center Id *************************/
		if (!(reportingEntity + "_" + localCostCenterID).equals(internalCostCenterID)) {
			USER_LOG.error(reportingEntity + "_" + localCostCenterID, 5, 1);
			LOG.debug("reportingEntity = '" + reportingEntity + "'");
			LOG.debug("localCostCenterID = '" + localCostCenterID + "'");
			LOG.debug("concat = '" + reportingEntity + "_" + localCostCenterID + "'");
			LOG.debug("internalCostCenterID = '" + internalCostCenterID + "'");
			return false;
		}

		/****************** Test Country *************************/

		if (!"Closing".equals(action)) {
			verif = false;

			for (SharedPickList picklist : pickList) {

				if (picklist.getName().equals("Country")) {

					@SuppressWarnings("unchecked")
					List<PickListItem> picklistitem = (List<PickListItem>) picklist.getPickListItems();

					for (int i = 0; i < picklistitem.size(); i++) {
						PickListItem currentItem = (PickListItem) picklistitem.get(i);

						if (country.equals(currentItem.getName())) {
							verif = true;
							break;
						}

					}

				}

			}

			if (!verif) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 0, 1);
				return false;
			}

			/****************** Test Access cost code *************************/

			verif = false;

			for (SharedPickList picklist : pickList) {
				if (picklist.getName().equals("Access cost code")) {
					@SuppressWarnings("unchecked")
					List<PickListItem> picklistitem = (List<PickListItem>) picklist.getPickListItems();

					for (int i = 0; i < picklistitem.size(); i++) {
						PickListItem currentItem = (PickListItem) picklistitem.get(i);

						if (accessCostCode.equals(currentItem.getName())) {
							verif = true;
							break;
						}

					}

				}

			}

			if (!verif) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 9, 1);
				return false;
			}

			/****************** Test Hourly labor rate *************************/

			try {
				Double.parseDouble(hourlyLaborRate);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 6, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Hourly labor rate date *************************/

			try {
				new SimpleDateFormat("yyyyMMdd").parse(hourlyLaborRateDate);
			} catch (ParseException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 7, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Last update date *************************/

			try {
				new SimpleDateFormat("yyyyMMdd").parse(lastUpdateDate);
			} catch (ParseException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 8, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Yearly Projects hours *************************/

			try {
				Double.parseDouble(yearlyProjectsHours);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 9, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Growth index *************************/

			try {
				Double.parseDouble(growthIndex);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 10, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Monthly labor rate *************************/

			try {
				Double.parseDouble(monthyLaborRate);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 11, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Labour rate currency value *************************/
			
			if (labourRateCurrency.isEmpty()) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 18, 1);
				return false;
			}
			
			/****************** Test Monthly labor rate date *************************/

			try {
				new SimpleDateFormat("yyyyMMdd").parse(monthyLaborRateDate);
			} catch (ParseException e) {
				e.printStackTrace();
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 12, 1);
				LOG.error(e);
				return false;
			}

			/****************** Test Global cost center ID *************************/

			if (globalCostCenterID.length() >= 255) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 13, 1);
				return false;
			}

			/****************** Test RE Global cost center *************************/

			if (reGlobalCostCenter.length() >= 255) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 14, 1);
				return false;
			}

			/****************** Test Global cost center name *************************/
			if (globalCostCenterName.length() >= 255) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 15, 1);
				return false;
			}

			/****************** Test Global Profit center ID *************************/
			if (globalProfitCenterID.length() >= 255) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 16, 1);
				return false;
			}

			/****************** Test Global Profit center Name *************************/
			if (globalProfitCenterName.length() >= 255) {
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 17, 1);
				return false;
			}
			
		}
		
		return true;
	}

	public boolean insertLine(LineImport line) {
		boolean valid = true;
		String reportingEntity = line.getReportingEntity();
		String localCostCenterID = line.getLocalCostCenterID();
		String localCostCenterName = line.getLocalCostCenterName();
		String internalCostCenterID = line.getInternalCostCenterID();
		String action = line.getAction();
		String country = line.getCountry();
		String accessCostCode = line.getAccessCostCode();
		String hourlyLaborRate = line.getHourlyLaborRate();
		String lastUpdateDate = line.getLastUpdateDate();
		String yearlyProjectsHours = line.getYearlyProjectsHours();
		String growthIndex = line.getGrowthIndex();
		String monthyLaborRate = line.getMonthyLaborRate();
		String labourRateCurrency = line.getLaborRateCurrency();
		String globalCostCenterID = line.getGlobalCostCenterID();
		String reGlobalCostCenter = line.getReGlobalCostCenter();
		String globalCostCenterName = line.getGlobalCostCenterName();
		String globalProfitCenterID = line.getGlobalProfitCenterID();
		String globalProfitCenterName = line.getGlobalProfitCenterName();
				
		if ("Creation".equals(action)) {

			for (DataViewRow dataRow : this.dataViewRowList) {

				try {
					
					if ((reportingEntity + "_" + localCostCenterID).equals(dataRow.getStringField("Reporting entity")
							+ "_" + dataRow.getStringField("Local cost center ID"))) {
						// id already existing
						USER_LOG.error(reportingEntity + "_" + localCostCenterID, 17, 1);
						valid = false;
						break;
					}
					
				} catch (DataFormatException e) {
					e.printStackTrace();
					LOG.error("Unable to get data");
					LOG.error(e);
					LOG.debug("Unable to get data : " + e.toString());
				} catch (PSException e) {
					e.printStackTrace();
					LOG.error("Unable to lock Dataview");
					LOG.error(e);
					LOG.debug("Unable to lock Dataview : " + e.toString());
				}

			}

			if (valid) {
				
				try {
					DataViewRow dataRow = new DataViewRow("Cost Centers Management", this.global);
					insertCountry(dataRow, country);
					insertaccessCostCode(dataRow, accessCostCode);
					insertreportingEntity(dataRow, reportingEntity);
					insertlocalCostCenterID(dataRow, localCostCenterID);
					insertinternalCostCenterID(dataRow, internalCostCenterID);
					insertlocalCostCenterName(dataRow, localCostCenterName);
//					inserthourlyLaborRate(dataRow, hourlyLaborRate);
					inserthourlyLaborRate(dataRow, hourlyLaborRate, labourRateCurrency);
					insertlastUpdateDate(dataRow, lastUpdateDate);
					insertyearlyProjectsHours(dataRow, yearlyProjectsHours);
					insertgrowthIndex(dataRow, growthIndex);
//					insertmonthyLaborRate(dataRow, monthyLaborRate);
					insertmonthyLaborRate(dataRow, monthyLaborRate, labourRateCurrency);
					insertglobalCostCenterID(dataRow, globalCostCenterID);
					insertReGlobalCostCenter(dataRow, reGlobalCostCenter);
					insertglobalCostCenterName(dataRow, globalCostCenterName);
					insertglobalProfitCenterID(dataRow, globalProfitCenterID);
					insertglobalProfitCenterName(dataRow, globalProfitCenterName);
					insertLocalcostCenterStatusOPEN(dataRow);
				} catch (PSException e) {
					e.printStackTrace();
					LOG.error("Unable to lock Dataview");
					LOG.debug("Unable to lock Dataview : " + e.toString());
					LOG.error(e);
				} catch (ParseException e) {
					e.printStackTrace();
					LOG.error("Unable to parse Date");
					LOG.debug("Unable to parse Date : " + e.toString());
					LOG.error(e);
				}
				
			}

		} else {
			boolean foundInDataview = false;

			for (DataViewRow dataRow : this.dataViewRowList) {

				try {

					if ((reportingEntity + "_" + localCostCenterID)
							.equals(dataRow.getStringField("Reporting entity") + "_"
									+ dataRow.getStringField("Local cost center ID"))) {
						foundInDataview = true;

						if ("Modification".equals(action)) {
							insertCountry(dataRow, country);
							insertaccessCostCode(dataRow, accessCostCode);
							insertinternalCostCenterID(dataRow, internalCostCenterID);
							insertlocalCostCenterName(dataRow, localCostCenterName);
//							inserthourlyLaborRate(dataRow, hourlyLaborRate);
							inserthourlyLaborRate(dataRow, hourlyLaborRate, labourRateCurrency);
							insertlastUpdateDate(dataRow, lastUpdateDate);
							insertyearlyProjectsHours(dataRow, yearlyProjectsHours);
							insertgrowthIndex(dataRow, growthIndex);
//							insertmonthyLaborRate(dataRow, monthyLaborRate);
							insertmonthyLaborRate(dataRow, monthyLaborRate, labourRateCurrency);
							insertglobalCostCenterID(dataRow, globalCostCenterID);
							insertReGlobalCostCenter(dataRow, reGlobalCostCenter);
							insertglobalCostCenterName(dataRow, globalCostCenterName);
							insertglobalProfitCenterID(dataRow, globalProfitCenterID);
							insertglobalProfitCenterName(dataRow, globalProfitCenterName);
							insertLocalcostCenterStatusOPEN(dataRow);
						} else if ("Moving".equals(action)) {
							insertlocalCostCenterName(dataRow, localCostCenterName);
//							inserthourlyLaborRate(dataRow, hourlyLaborRate);
							inserthourlyLaborRate(dataRow, hourlyLaborRate, labourRateCurrency);
							insertlastUpdateDate(dataRow, lastUpdateDate);
							insertyearlyProjectsHours(dataRow, yearlyProjectsHours);
//							insertmonthyLaborRate(dataRow, monthyLaborRate);
							insertmonthyLaborRate(dataRow, monthyLaborRate, labourRateCurrency);
							insertglobalCostCenterID(dataRow, globalCostCenterID);
							insertReGlobalCostCenter(dataRow, reGlobalCostCenter);
							insertglobalCostCenterName(dataRow, globalCostCenterName);
							insertglobalProfitCenterID(dataRow, globalProfitCenterID);
							insertglobalProfitCenterName(dataRow, globalProfitCenterName);
							insertLocalcostCenterStatusOPEN(dataRow);
						} else if ("Closing".equals(action)) {
							insertLocalcostCenterStatusCLOSING(dataRow);
						} else {
							USER_LOG.error("Type action not found for line: " + line, 7, 2);
							LOG.debug("Type action not found for line: " + line);
						}

						break;
					} else {
						continue;
					}

				} catch (PSException e) {
					e.printStackTrace();
					USER_LOG.error("Cost Centers Management dataview", 7, 2);
					LOG.debug("Cost Centers Management dataview error : " + e.getMessage());
					valid = false;
				} catch (ParseException e) {
					e.printStackTrace();
					USER_LOG.error("Cost Centers Management dataview", 7, 2);
					LOG.debug("Cost Centers Management dataview error : " + e.getMessage());
					valid = false;
				}

			} // fin for

			if (!foundInDataview) {
				// id not existing
				USER_LOG.error(reportingEntity + "_" + localCostCenterID, 16, 1);
				valid = false;
			}
			
		}

		return valid;
	}

	public boolean openGlobal() {
		boolean result = false;
		LOG.debug("Locking global");
		
		if (this.global == null) {
			this.global = new Global();
		}

		try {
			this.global.lock();
			result = true;
			LOG.debug("Global locked");
		} catch (LockException e) {
			e.printStackTrace();
			LOG.error("Global locked by: " + e.getLockingUser());
			LOG.error("Unable to lock Global" + e.toString());
		} catch (PSException e) {
			e.printStackTrace();
			LOG.error("Unable to lock Global" + e.toString());
		}

		return result;
	}

	public boolean closeGlobal() {
		boolean result = false;
		LOG.debug("Unlocking global");
		
		try {
			this.global.save(false);
		} catch (PSException e) {
			e.printStackTrace();
			USER_LOG.error("Cost Centers Management dataview", 7, 2);
			LOG.error("Unable to save dataview");
			LOG.debug("Unable to save Dataview : " + e.toString());
		}

		try {
			this.global.unlock();
			result = true;
			LOG.debug("Global unlocked");
		} catch (PSException e) {
			e.printStackTrace();
			LOG.error("Unable to unlock Dataview");
			LOG.debug("Unable to unlock Dataview : " + e.toString());
		}

		return result;
	}

	public void insertLocalcostCenterStatusCLOSING(DataViewRow dataRow) throws DataFormatException, PSException {
		dataRow.setStringField("Local cost center status", "closed");
	}

	public void insertLocalcostCenterStatusOPEN(DataViewRow dataRow) throws DataFormatException, PSException {
		dataRow.setStringField("Local cost center status", "opened");
	}

	public void insertCountry(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Country", value);
	}

	public void insertaccessCostCode(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Access costcode", value);
	}

	public void insertreportingEntity(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Reporting entity", value);
	}

	public void insertlocalCostCenterID(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Local cost center ID", value);
	}

	public void insertinternalCostCenterID(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Internal cost center ID", value);
	}

	public void insertlocalCostCenterName(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Local cost center name", value);
	}

//	public void inserthourlyLaborRate(DataViewRow dataRow, String value) throws DataFormatException, PSException {
//		dataRow.setDoubleField("Hourly labor rate", Double.parseDouble(value));
//	}

	public void inserthourlyLaborRate(DataViewRow dataRow, String value, String currency) throws DataFormatException, PSException {
		double dValue = Double.parseDouble(value);
		String strFinalValue = "" + dValue + " " + currency + "/h";
		strFinalValue = strFinalValue.replace(this.csv_numeric_separator.charAt(0), this.psnext_numeric_separator.charAt(0));
		dataRow.setValueUsingString("Hourly labor rate", strFinalValue);
	}
	
	public void insertlastUpdateDate(DataViewRow dataRow, String value)
			throws DataFormatException, PSException, ParseException {
		dataRow.setDateField("Last update date", new SimpleDateFormat("yyyyMMdd").parse(value));
	}

	public void insertyearlyProjectsHours(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setDoubleField("Yearly projects hours", Double.parseDouble(value));
	}

	public void insertgrowthIndex(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setDoubleField("Growth index", Double.parseDouble(value));
	}

//	public void insertmonthyLaborRate(DataViewRow dataRow, String value) throws DataFormatException, PSException, ParseException {
//		dataRow.setDoubleField("Monthly labor rate", Double.parseDouble(value) / 224);
//	}
	
	public void insertmonthyLaborRate(DataViewRow dataRow, String value, String currency) throws DataFormatException, PSException, ParseException {
		double dValue = Double.parseDouble(value);
		String strFinalValue = "" + dValue + " " + currency + "/m";
		strFinalValue = strFinalValue.replace(this.csv_numeric_separator.charAt(0), this.psnext_numeric_separator.charAt(0));
		dataRow.setValueUsingString("Monthly labor rate", strFinalValue);
	}
	
	public void insertglobalCostCenterID(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Global cost center ID", value);
	}

	public void insertReGlobalCostCenter(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("RE global cost center", value);
	}

	public void insertglobalCostCenterName(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Global cost center name", value);
	}

	public void insertglobalProfitCenterID(DataViewRow dataRow, String value) throws DataFormatException, PSException {
		dataRow.setStringField("Global profit center ID", value);
	}

	public void insertglobalProfitCenterName(DataViewRow dataRow, String value)
			throws DataFormatException, PSException {
		dataRow.setStringField("Global profit center name", value);
	}

}
