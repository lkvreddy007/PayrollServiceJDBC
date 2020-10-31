package com.capg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	public List<EmployeePayrollData> readData() {
		
		String sql = "Select * from employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id,name, salary, startDate));
			}
			connection.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		for(EmployeePayrollData emp:employeePayrollList) {
			System.out.println(emp);
		}
		return employeePayrollList;
	
	}
	
	private Connection getConnection() {
		
		String jdbcURL = "jdbc:mysql://localhost:3306/emp_payroll_service?useSSL=false";
		String userName = "root";
		String password = "XXXXX";
		Connection connection = null;
		System.out.println("Connecting to database "+jdbcURL);
		try {
			System.out.println("Connecting to database:"+jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is successful "+connection);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	
	}

}
