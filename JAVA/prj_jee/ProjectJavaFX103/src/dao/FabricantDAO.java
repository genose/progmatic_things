package dao;

import java.sql.Connection;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.Fabricant;

public class FabricantDAO extends DAO<Fabricant> {

	public FabricantDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public Fabricant getByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Fabricant> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Fabricant> select(Fabricant obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(Fabricant obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(Fabricant obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(Fabricant obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
