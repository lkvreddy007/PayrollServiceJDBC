package com.capg;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	
	@Test
	public void givenNewSalaryForEmpoyee_WhenUpdatedUsingPreparedStatement_ShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
		employeePayrollService.updateEmployeeSalaryUsingPreparedStatement("Terisa",3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) && averageSalaryByGender.get("F").equals(3000000.00));
	}
  
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		LocalDate startDate = LocalDate.of(2018,01,01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollForDateRange(DB_IO, startDate, endDate);
		Assert.assertEquals(3, employeePayrollData.size());
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		String[] deptList = {"Sales"};
		employeePayrollService.addEmployeeToPayroll("Mark",5000000.00,LocalDate.now(),"M",deptList);
		boolean result = employeePayrollService .checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenNewEmployee_WhenDeleted_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		employeePayrollService.deleteEmployee("Mark");
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
		System.out.println(employeePayrollData);
		Assert.assertEquals(3, employeePayrollData.size());
	}
	
	@Test
	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
			new EmployeePayrollData(0, "Jeff Bezos","M", 100000.00, LocalDate.now()),
			new EmployeePayrollData(0, "Bill Gates","M", 200000.00, LocalDate.now()),
			new EmployeePayrollData(0, "Mark Zuckerberg","M", 300000.00, LocalDate.now()),
			new EmployeePayrollData(0, "Sunder","M", 600000.00, LocalDate.now()),
			new EmployeePayrollData(0, "Mukesh","M", 1000000.00, LocalDate.now()),
			new EmployeePayrollData(0, "Anil","M", 200000.00, LocalDate.now()),
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeeToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: "+Duration.between(start, end));
		Assert.assertEquals(7, employeePayrollService.countEntries(DB_IO));
	}
	
	@Test
	public void given6Employees_WhenAddedToDBUsingThreads_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "Jeff Bezos","M", 100000.00, LocalDate.now()) ,
				new EmployeePayrollData(0, "Bill Gates","M", 200000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Mark Zuckerberg","M", 300000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Sunder","M", 600000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh","M", 1000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Anil","M", 200000.00, LocalDate.now()),
			};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeeToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: "+Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: "+Duration.between(threadStart, threadEnd));
		Assert.assertEquals(13, employeePayrollService.countEntries(DB_IO));
	}
	
	@Test
	public void given6Employees_WhenAddedToERDBUsingThreads_ShouldMatchEmployeeEntries() {
		String[] deptJeff = {"Sales"};
		String[] deptBill = {"Marketing"};
		String[] deptMark = {"Sales","Marketing"};
		String[] deptSunder = {"Administrator"};
		String[] deptMukesh = {"Administrator","Marketing"};
		String[] deptAnil = {"Sales"};
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0,"Jeff Bezos","M", 100000.00, LocalDate.now(), deptJeff),
				new EmployeePayrollData(0,"Bill Gates","M", 200000.00, LocalDate.now(), deptBill),
				new EmployeePayrollData(0, "Mark Zuckerberg","M", 300000.00, LocalDate.now(), deptMark),
				new EmployeePayrollData(0, "Sunder","M", 600000.00, LocalDate.now(), deptSunder),
				new EmployeePayrollData(0, "Mukesh","M", 1000000.00, LocalDate.now(), deptMukesh),
				new EmployeePayrollData(0, "Anil","M", 200000.00, LocalDate.now(), deptAnil),
			};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeeToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: "+Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollERDBWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: "+Duration.between(threadStart, threadEnd));
		Assert.assertEquals(13, employeePayrollService.countEntries(DB_IO));
	}
	
}
