package dao;

import java.sql.Connection;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.Marque;

public class MarqueDAO extends DAO<Marque> {

	public MarqueDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public Marque getByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Marque> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Marque> select(Marque obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(Marque obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(Marque obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(Marque obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
