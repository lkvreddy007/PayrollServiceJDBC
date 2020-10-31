package com.capg;

import java.util.List;

public class EmployeePayrollService {

	public enum IOService {CONSOLE_IO, FILE_IO,DB_IO, REST_IO}

	private List<EmployeePayrollData> employeePayrollList;
	
	public EmployeePayrollService() {
		
	}
	
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		
		this.employeePayrollList = employeePayrollList;
	
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		
		if(ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		}
		return this.employeePayrollList;
	
	}
	
}
