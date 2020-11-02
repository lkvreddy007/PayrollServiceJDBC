package com.capg;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	private  static EmployeePayrollDBService employeePayrollDBService; //To make Singleton object
	private  PreparedStatement employeePayrollDataStatement;
	
	private EmployeePayrollDBService() {
		
	}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}
	
	public List<EmployeePayrollData> readData() {
		String sql = "Select * from employee_payroll;";
		return this.getEmployeePayrollUsingDB(sql);
	}
	
	public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("select * from employee_payroll where start between '%s' and '%s';", Date.valueOf(startDate),Date.valueOf(endDate));
		return this.getEmployeePayrollUsingDB(sql);
	}

	private List<EmployeePayrollData> getEmployeePayrollUsingDB(String sql) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private Connection getConnection() {
		String jdbcURL = "jdbc:mysql://localhost:3306/emp_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Ritz@5369";
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

	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int updateEmployeeDataUsingPrepared(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		String sql = "update employee_payroll set salary = ? where name = ?;";
		try(Connection connection = this.getConnection()){
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setDouble(1, salary);
			prepStatement.setString(2, name);
			return prepStatement.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList = null;
		if(this.employeePayrollDataStatement == null) {
			this.prepareStatementForEmployeeData();
		}
		try {
			employeePayrollDataStatement.setString(1,name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		 List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		 try {
			 while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		 }
		 catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "Select * from employee_payroll where name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
