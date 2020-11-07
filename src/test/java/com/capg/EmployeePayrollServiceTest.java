package com.capg;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.capg.EmployeePayrollService.IOService.DB_IO;
import static com.capg.EmployeePayrollService.IOService.REST_IO;
import static org.junit.Assert.assertNotNull;

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
	
	@Test
	public void given6Employees_WhenDBUpdateUsingThreads_ShouldSyncWithDB() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0,"Jeff Bezos",500000.00),
				new EmployeePayrollData(0, "Mark Zuckerberg",300000.00),
				new EmployeePayrollData(0, "Sunder",700000.00),
			};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(DB_IO);
		employeePayrollService.updateEmployeePayrollDBWithThreads(Arrays.asList(arrayOfEmps));
		boolean result = employeePayrollService .checkEmployeePayrollInSyncWithDB("Jeff Bezos");
		Assert.assertTrue(result);
	}
		
	@Before 
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	
	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee_payroll");
		System.out.println("EMPLOYEE PAYROLL ENTRIES IN JSONServer:\n"+response.asString());
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(),EmployeePayrollData[].class);
		return arrayOfEmps;
	}
	
	public Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		request.body(empJson);
		return request.post("/employee_payroll");
	}
	
	@Test 
	public void givenEmployeeDataInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		long entries = employeePayrollService.countEntries(REST_IO);
		Assert.assertEquals(2, entries);
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldMatch201ResponseCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		EmployeePayrollData e,employeePayrollData = null;
		employeePayrollData = new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now());
		
		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.addEmployeesToPayroll(employeePayrollData, REST_IO);
		long entries = employeePayrollService.countEntries(REST_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test 
	public void givenListOfNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		EmployeePayrollData[] arrayOfEmpPayrolls = {
				new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now()),
		};
		
		for(EmployeePayrollData employeePayrollData :arrayOfEmpPayrolls) {
			Response response = addEmployeeToJsonServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			
			
			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			employeePayrollService.addEmployeesToPayroll(employeePayrollData, REST_IO);
		}
		
		long entries = employeePayrollService.countEntries(REST_IO);
		Assert.assertEquals(6, entries);
	}
	
	@Test 
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch200Response() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		employeePayrollService.updateEmployeeSalary("Anil", 3000000.00, REST_IO);
		EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Anil");
		
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		request.body(empJson);
		Response response = request.put("/employee_payroll/"+employeePayrollData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}

}
