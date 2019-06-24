package service;

import java.util.ArrayList;

import dao.ParametresDAO;

public class ParametreBean
{
	private String dernierRepertoire;
	private ArrayList<String> fichiersRecents;
	private ParametresDAO parametresDAO;

	public ParametreBean()
	{
		parametresDAO = new ParametresDAO();
		dernierRepertoire = parametresDAO.getRepertoire();
		fichiersRecents = parametresDAO.getFichiersRecents();
	}

	public String getDernierRepertoire()
	{
		return dernierRepertoire;
	}

	public void setDernierRepertoire(String dernierRepertoire)
	{
		int pos = dernierRepertoire.lastIndexOf('\\');
		this.dernierRepertoire = dernierRepertoire.substring(0,pos+1);
	}

	public ArrayList<String> getFichiersRecents()
	{
		return fichiersRecents;
	}

	public void sauver()
	{
		parametresDAO.sauver(this);
	}

}
