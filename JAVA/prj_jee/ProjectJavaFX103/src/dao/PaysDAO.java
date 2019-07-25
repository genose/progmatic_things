package dao;

import java.sql.Connection;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.Pays;

public class PaysDAO extends DAO<Pays>{

	public PaysDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public Pays getByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Pays> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Pays> select(Pays obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(Pays obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(Pays obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(Pays obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
