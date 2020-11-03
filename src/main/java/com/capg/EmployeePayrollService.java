package com.capg;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

	public enum IOService {CONSOLE_IO, FILE_IO,DB_IO, REST_IO}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;
	
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}
	
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDBService.readData();
		}
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if(result == 0) {
			return;
		}
		EmployeePayrollData employeePayrollData =this.getEmployeePayrollData(name);
		if(employeePayrollData != null) {
			employeePayrollData.salary = salary;
		}
	}
	

	public void updateEmployeeSalaryUsingPreparedStatement(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeDataUsingPrepared(name, salary);
		if(result == 0) {
			return;
		}
		EmployeePayrollData employeePayrollData =this.getEmployeePayrollData(name);
		if(employeePayrollData != null) {
			employeePayrollData.salary = salary;
		}
		
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData = this.employeePayrollList.stream()
							  .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name) && employeePayrollDataItem.is_active.equals("true"))
							  .findFirst()
							  .orElse(null);
		return employeePayrollData;
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getAverageSalaryByGender();
      }
		return null;
	}

	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, 
																	 LocalDate startDate, LocalDate endDate) {
		if(ioService.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
		}
		return null;
	}

	public void addEmployeeToPayrollUC9(String name, double salary, LocalDate start, String gender) {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollUC9(name, salary, start,gender));
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate start, String gender,
			ArrayList<String> deptList) {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, start,gender,deptList));
	}

	public void deleteEmployee(String name) {
		employeePayrollDBService.deleteEmployee(name);
	}
  
}
