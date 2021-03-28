package model.service;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	DepartmentDao dep = DaoFactory.createDepartmentDao();
	public List<Department> findAll(){
		return dep.findAll();
	}
}
