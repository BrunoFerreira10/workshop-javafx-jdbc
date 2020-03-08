package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll(){
		List<Department> list;
		DepartmentDao dao = DaoFactory.createDepartmentDao();
		list = dao.findAll();
		return list;
	}

}
