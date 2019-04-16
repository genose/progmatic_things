/**
 * 
 */

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 59013-03-13 - Cotillard Sebastien
 * @version 1.0 - 15/04/2019 
 *
 */
public class BDDConnector {


	/* ****************************
	 * 
	 ****************************** */
	private Boolean 	SGBDconnected 				= false;
	private String 		SGBDUsername 				= "root";
	private String 		SGBDUserPassword 			= "rootroot";
	private String 		SGBDConnectionString 		= "jdbc:mysql://localhost/stagiaire";
	
	
	/* ****************************
	 * 
	 ****************************** */
	private Connection 		SGBDConnectionLink 				= null;
	private Statement 		SGBDConnectionLinkStatement 	= null;
	private ResultSet 		SGBDConnectionLinkResultSet 	= null;
	private int 			SGBDConnectionLinkResultSetCount 	= 0;
	private int 			SGBDConnectionLinkResultSetCountPTR = 0;
	

	/* ****************************
	 * 
	 ***************************** */
	
	public Boolean getSGBDconnected() throws Exception {
		SGBDconnected = false;
		try {
			
			SGBDconnected = SGBDConnectionLink.isClosed();
			
		}catch(Exception EV_Exception){
			throw EV_Exception;
		}
		
		return SGBDconnected;
	}
	/* public void setSGBDconnected(Boolean sGBDconnected) {
		SGBDconnected = sGBDconnected;
	}*/
	
	public int getSGBDConnectionLinkResultSetCount() {
		return SGBDConnectionLinkResultSetCount;
	}

	public void setSGBDConnectionLinkResultSetCount(int sGBDConnectionLinkResultSetCount) {
		SGBDConnectionLinkResultSetCount = sGBDConnectionLinkResultSetCount;
	}

	public String getSGBDUsername() {
		return SGBDUsername;
	}
	public String getSGBDUserPassword() {
		return SGBDUserPassword;
	}

	public void setSGBDUserPassword(String sGBDUserPassword) {
		SGBDUserPassword = sGBDUserPassword;
	}

	/* ********************************** */
	public void setSGBDUsername(String sGBDUsername) {
		SGBDUsername = sGBDUsername;
	}
	/* ********************************** */
	public String getSGBDConnectionString() {
		return SGBDConnectionString;
	}
	public void setSGBDConnectionString(String sGBDConnectionString) {
		SGBDConnectionString = sGBDConnectionString;
	}
	/**
	 * 
	 */
	public BDDConnector() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @return 1 for connected state, 0 : not connected
	 */
	public int Open() throws Exception {

		try {
			
			SGBDconnected = SGBDConnectionLink.isClosed();
			
			if(getSGBDconnected() ){
				this.Close();
			}
				
			Class.forName("com.mysql.jdbc.Driver");
			SGBDConnectionLink = DriverManager.getConnection(SGBDConnectionString, SGBDUsername, SGBDUserPassword);
			SGBDConnectionLinkStatement = SGBDConnectionLink.createStatement();
			SGBDConnectionLinkResultSet.clearWarnings();
			// check if we get connected ...	
			if(!getSGBDconnected()){
				// FATAL ERROR 
				this.Close();
				throw new Exception("Impossible de se connecter ... "+getSGBDConnectionString());
			}else{
				
				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkStatement.getFetchSize();
				return  (SGBDConnectionLinkResultSetCount);
			}
			
		} catch (SQLException EV_CONNEXION_OPEN_ERROR){
			EV_CONNEXION_OPEN_ERROR.printStackTrace();
		}
		// error
		return 0;
	}
	
	/**
	 * @param args
	 */
	public int Close() throws  Exception {
		try{
			
			SGBDConnectionLinkResultSetCountPTR = 0;
			SGBDConnectionLinkResultSetCount = 0;
			try {
				SGBDConnectionLinkResultSet.clearWarnings();
			}catch(Exception EV_ERR_CLEARSTATEMENT)
			{
				// nothing to do ;; maybe nno previous resultset
			}
				
			// closing connection and statement
			SGBDConnectionLinkStatement.close();
			SGBDConnectionLink.close();
			return 1;
		} catch (SQLException EV_CONNEXION_CLOSE_ERROR){
			EV_CONNEXION_CLOSE_ERROR.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @param args
	 */
	public int SendQuery(String sArgSql) throws Exception {
		
		
		SGBDConnectionLinkResultSetCountPTR = 0;
		SGBDConnectionLinkResultSetCount = 0;
		
		try {
			SGBDConnectionLinkResultSet.clearWarnings();
		}catch(SQLException EV_ERR_CLEARSTATEMENT) { 
			// nothing to do ;; maybe no previous resultset
		}
		// Check which Operation is requested and Always Return Numerical result for status
		if( sArgSql.substring(0, 8).toUpperCase().startsWith("SELECT" ) ) {

			try {
				SGBDConnectionLinkResultSet.clearWarnings();
		
				SGBDConnectionLinkResultSet = SGBDConnectionLinkStatement.executeQuery(sArgSql);
				
				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkResultSet.getFetchSize();
				
				SGBDConnectionLinkResultSet.first();
				
				return SGBDConnectionLinkResultSetCount;
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			
			try {
				
				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkStatement.executeUpdate(sArgSql);
				SGBDConnectionLinkResultSet = SGBDConnectionLinkStatement.getResultSet();
				// return non Zero if Row in Database was touched 
				// this was a unary expression (ternary) expression, short-end for IF ELSE Statement
				return  ( (SGBDConnectionLinkResultSet .rowDeleted() |
						SGBDConnectionLinkResultSet .rowUpdated() |
						SGBDConnectionLinkResultSet .rowInserted() ) ? 
								// check if non null value was returned
								((SGBDConnectionLinkResultSetCount >=1)? SGBDConnectionLinkResultSetCount : 1)
								// else no row was touched by last operation ... returning Zero value  
								: 0 ) ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return 0;
		
	}
	
	public Map Next() throws Exception {
		Map ReturnedResultSet = new HashMap();
		
		// cheching if connection resource is valid and connection status is not closed
		if(!pingConnection()) {
			
			throw new Exception("Can t fetch Next Row in Resultset for closed connection");
		}
		
		// return null, no resultset occurs
		if(SGBDConnectionLinkResultSetCount <= 0 ) {
			return null;
		}
		
		// return null, EOF for this resultset has occurs
		if(SGBDConnectionLinkResultSetCount >= SGBDConnectionLinkResultSetCountPTR ) {
			return null;
		}
		
		// return null, last reached OR no resultset occurs
		if(SGBDConnectionLinkResultSet.isLast() ) {
			return null;
		}
		
		if( (SGBDConnectionLinkResultSet.wasNull())
				|| SGBDConnectionLinkResultSet.isClosed() ) {
			throw new Exception("Can t fetch Next Row in NULL Resultset / for invalid connection");
		}
		
		// :: else 
		
		// fetch the current PTR position in Resultset
		SGBDConnectionLinkResultSetCountPTR = SGBDConnectionLinkResultSet.getRow();
		
		ResultSetMetaData rsmd = SGBDConnectionLinkResultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		// The column count starts from 1
		for (int i = 1; i <= columnCount; i++ ) {
		  String name = rsmd.getColumnName(i);
		  // Do stuff with name
		  ReturnedResultSet.put(name, SGBDConnectionLinkResultSet.getObject(name));
		}
		

		return ReturnedResultSet;
		
	}
	
	
	/**
	 * @param args
	 */
	public Boolean pingConnection() throws Exception {
		
		try {
			if(SGBDConnectionLink.isValid(30) && SGBDConnectionLink.isClosed() ) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
