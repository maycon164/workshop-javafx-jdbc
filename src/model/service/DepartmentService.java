package model.service;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	private DepartmentDao dep = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return dep.findAll();
	}

	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null) {
			dep.insert(obj);
		} else {
			dep.update(obj);
		}
	}

	public void remove(Department obj) {
		dep.deleteById(obj.getId());
	}
}
