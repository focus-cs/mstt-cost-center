package com.schneider.api.cost_codes.business;

import java.util.ArrayList;
import java.util.List;

public class UserLog {
	
	private static UserLog classInstance;

	private static List<String> traces;

	public static UserLog getInstance() {
		return classInstance == null ? classInstance = new UserLog() : classInstance;
	}

	public static UserLog getInstance(String csvFileName) {
		classInstance = new UserLog();
		return classInstance;
	}

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
	
}
