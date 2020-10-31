package com.capg;

import java.util.List;

import org.junit.Test;

import static com.capg.EmployeePayrollService.IOService.DB_IO;

import junit.framework.Assert;

public class EmployeePayrollServiceTest {
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrived_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayRollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayRollService.readEmployeePayrollData(DB_IO);
		Assert.assertEquals(3, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmpoyee_WhenUpdated_ShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa",3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}
	
}
