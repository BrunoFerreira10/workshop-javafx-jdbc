package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Seller entity) {
		if(entity.getId() == null) {
			dao.insert(entity);
		} else {
			dao.update(entity);			
		}
	}
	
	public void remove(Seller entity) {
		dao.deleteById(entity.getId());
	}

}
