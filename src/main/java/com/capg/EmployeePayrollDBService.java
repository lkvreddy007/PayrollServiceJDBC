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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
	private  static EmployeePayrollDBService employeePayrollDBService; //To make Singleton object
	private  PreparedStatement employeePayrollDataStatement;
	private int connectionCounter = 0; 
	
	private EmployeePayrollDBService() {
		
	}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}
	
	public List<EmployeePayrollData> readData() {
		String sql = "Select * from employee_payroll where is_active='true';";
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

	private Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/emp_payroll_service?useSSL=false";
		String userName = "root";
		String password = System.getenv().get("sql_password");
		Connection connection;
		System.out.println("Connecting to database "+jdbcURL);
		System.out.println("Processing Thread: "+Thread.currentThread().getName()							
							+"Conecting to database with Id:"+connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Processing Thread: "+Thread.currentThread().getName()+
								"Id: "+connectionCounter+"Connection is succesfull "+connection);
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
	
	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "Select gender, Avg(salary) as avg_salary from employee_payroll group by gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}
	
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		 List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		 try {
			 while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				String is_active = resultSet.getString("is_active");
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate,is_active));
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
			String sql = "Select * from employee_payroll where name = ? and is_active='true';";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate start, String gender) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format("Insert into employee_payroll (name,gender,salary,start) values ('%s','%s',%s,'%s')", name,gender,salary,Date.valueOf(start));
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId = resultSet.getInt(1);
				}
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayrollUC9(String name, double salary, LocalDate start, String gender) {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try(Statement statement = connection.createStatement()){
			String sql = String.format("Insert into employee_payroll (name,gender,salary,start) values ('%s','%s',%s,'%s')", name,gender,salary,Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId = resultSet.getInt(1);
				}
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
		}
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		try(Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("insert into payroll "
									 + "(emp_id, basic_pay,deductions,taxable_pay, tax,net_pay)"
									 + "values(%s, %s, %s, %s, %s, %s)", employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start, String gender, String[] deptList) {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try(Statement statement = connection.createStatement()){
			String sql = String.format("Insert into employee_payroll (name,gender,salary,start) values ('%s','%s',%s,'%s')", name,gender,salary,Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId = resultSet.getInt(1);
				}
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
		}
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		try(Statement statement = connection.createStatement()){
			int i=0;
			int rowAffected = 0;
			String sql = String.format("Insert into department (emp_id,dept_name) values ('%s','%s')", employeeId,deptList[i]);
			for(;i<deptList.length;i++) {
				rowAffected += statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			}
			if(rowAffected == i+1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId = resultSet.getInt(1);
				}
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
		}
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		try(Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("insert into payroll "
									 + "(emp_id, basic_pay,deductions,taxable_pay, tax,net_pay)"
									 + "values(%s, %s, %s, %s, %s, %s)", employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}

	public void deleteEmployee(String name) {
		String sql = String.format("update employee_payroll set is_active = 'false' where name = '%s';", name);
		try (Connection connection = getConnection()) {
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
