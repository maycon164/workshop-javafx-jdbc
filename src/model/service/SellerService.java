package model.service;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	private SellerDao dep = DaoFactory.createSellerDao();

	public List<Seller> findAll() {
		return dep.findAll();
	}

	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			dep.insert(obj);
		} else {
			dep.update(obj);
		}
	}

	public void remove(Seller obj) {
		dep.deleteById(obj.getId());
	}
}
