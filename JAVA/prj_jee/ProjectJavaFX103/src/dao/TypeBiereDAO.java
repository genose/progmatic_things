package dao;

import java.sql.Connection;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.TypeBiere;

public class TypeBiereDAO extends DAO<TypeBiere> {

	public TypeBiereDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public TypeBiere getByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TypeBiere> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TypeBiere> select(TypeBiere obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(TypeBiere obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(TypeBiere obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(TypeBiere obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
