package model.service;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll(){
		List<Department> lista = new ArrayList<Department>();
		lista.add(new Department(1, "Eletronics"));
		lista.add(new Department(2, "ServiceTAX"));
		lista.add(new Department(3, "Books"));
		return lista;
	}
}
