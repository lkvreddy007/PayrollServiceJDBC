package com.capg;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeePayrollData {

	public int id;
	public String name;
	public double salary;
	public String gender;
	public LocalDate startDate;
	public String companyName;
	public ArrayList<String> departmentName;
	public String is_active;

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, String is_active) {
		this(id, name, salary, startDate);
		this.is_active = is_active;
	}
	
	public EmployeePayrollData(int id,String name,String gender,double salary, LocalDate startDate) {
		this(id, name, salary, startDate);
		this.gender = gender;
	}

	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, ArrayList<String> departmentName) {
		this(id, name, salary, startDate);
		this.departmentName = departmentName;
	}

	@Override
	public String toString() {
		return "id: "+id+", name: "+name+" ,salary: "+salary+", startDate: "+startDate;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if(this == obj) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		EmployeePayrollData that = (EmployeePayrollData) obj;
		return id == that.id && Double.compare(that.salary, salary) == 0 && name.equals(that.name);
	} 
	
}
