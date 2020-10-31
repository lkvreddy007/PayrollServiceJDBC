package com.capg;

import java.time.LocalDate;

public class EmployeePayrollData {

	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
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
