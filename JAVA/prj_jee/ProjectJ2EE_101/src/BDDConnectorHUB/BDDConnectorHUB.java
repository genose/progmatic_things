package BDDConnectorHUB;
/**
 * 
 */

import java.io.PrintStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 59013-03-13 - Cotillard Sebastien
 * @version 1.0 - 15/04/2019 
 *
 */
public class BDDConnectorHUB {


	private PrintStream 					consoleLog 										= System.out;

	private static final String SGBDDriverName = "com.mysql.jdbc.Driver";
	/* ****************************
	 * 
	 ****************************** */
	private Boolean 	SGBDconnected 								= false;


	private String 		SGBDUsername 								= "root";
	private String 		SGBDUserPassword 							= "root";
	private String 		SGBDConnectionString 						= "";
	private String 		SGBDConnectionStringDriver 					= "jdbc:mysql://localhost";
	private String 		SGBDConnectionStringSeletedDatabase 		= "bdgestionformation";


	/* ****************************
	 * 
	 ****************************** */
	private Connection 						SGBDConnectionLink 										 = null;
	private Statement 						SGBDConnectionLinkStatement 							 = null;
	private ResultSet 						SGBDConnectionLinkResultSet 							 = null;
	private Map<Object,Object> 				SGBDConnectionLinkStoredReturnedResultSet				 = new HashMap<Object, Object>();

	/* ****************************
	 * 
	 ****************************** */

	private Map<Object, String>				SGBDConnectionLinkResultSetColumnMapDescriptor 			 = new HashMap<Object, String>();
	private Map<String, Object> 			SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS = new HashMap<String, Object>();
	private Map<String, String>				SGBDConnectionLinkResultSetColumnMapDescriptorCONST = new HashMap<String, String>();

	/* ****************************
	 * 
	 ****************************** */



	private int 							SGBDConnectionLinkResultSetCount 				= 0;
	private int 							SGBDConnectionLinkResultSetCountPTR 			= 0;


	private String 							SGBDConnectionLinkStatementLastQuery			= "";

	/* ****************************
	 * 
	 ***************************** */

	public String getSGBDConnectionStringSeletedDatabase() {
		return SGBDConnectionStringSeletedDatabase;
	}

	public void setSGBDConnectionStringSeletedDatabase(String sGBDConnectionStringSeletedDatabase) {
		SGBDConnectionStringSeletedDatabase = sGBDConnectionStringSeletedDatabase;
	}

	public PrintStream getConsoleLog() {
		return consoleLog;
	}

	public void setConsoleLog(PrintStream consoleLog) {
		this.consoleLog = consoleLog;
	}

	public static String getSgbddrivername() {
		return SGBDDriverName;
	}

	public String getSGBDConnectionStringDriver() {
		return SGBDConnectionStringDriver;
	}

	public Connection getSGBDConnectionLink() {
		return SGBDConnectionLink;
	}

	public Statement getSGBDConnectionLinkStatement() {
		return SGBDConnectionLinkStatement;
	}

	public ResultSet getSGBDConnectionLinkResultSet() {
		return SGBDConnectionLinkResultSet;
	}

	public Map<Object, String> getSGBDConnectionLinkResultSetColumnMapDescriptor() {
		return SGBDConnectionLinkResultSetColumnMapDescriptor;
	}

	public Map<String, Object> getSGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS() {
		return SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS;
	}

	public Map<String, String> getSGBDConnectionLinkResultSetColumnMapDescriptorCONST() {
		return SGBDConnectionLinkResultSetColumnMapDescriptorCONST;
	}

	public int getSGBDConnectionLinkResultSetCountPTR() {
		return SGBDConnectionLinkResultSetCountPTR;
	}

	public Boolean getSGBDconnected() throws Exception {
		SGBDconnected = false;
		try {

			if(SGBDConnectionLink == null) { 
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : WARNING :: can t checking Status on closed connection ... ");

				return false;
			}

			SGBDconnected = !SGBDConnectionLink.isClosed();

		}catch(Exception EV_EXCEPTION_CHECKING_CONNECTION){
			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : FATAL ::  can t checking Status : ERROR : "+EV_EXCEPTION_CHECKING_CONNECTION.getMessage()+" ... ");

			throw EV_EXCEPTION_CHECKING_CONNECTION;
			// :: ;; return false ;;
		}
		// consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Checked Status : connection is "+((SGBDconnected)?"Currently Open":"Currently Closed")+" ... ");
		return SGBDconnected;
	}

	public int getSGBDConnectionLinkResultSetCount() {
		return SGBDConnectionLinkResultSetCount;
	}

	public int Count() {
		return SGBDConnectionLinkResultSetCount;
	}

	public String getQuery() {
		return SGBDConnectionLinkStatementLastQuery;
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

		SGBDConnectionString = SGBDConnectionStringDriver +"/"+SGBDConnectionStringSeletedDatabase;

		return SGBDConnectionString;
	}
	public void setSGBDConnectionString(String sGBDConnectionString) {
		SGBDConnectionString = sGBDConnectionString;
	}
	/**
	 * 
	 */
	public BDDConnectorHUB() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @return 1 for connected state, 0 : not connected
	 */
	public int Open() throws Exception {

		try {
			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Start proceeding to open connection ... ");

			if(SGBDConnectionLink != null ) {
				SGBDconnected = SGBDConnectionLink.isClosed();
			}

			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Previous Status : connection is "+((getSGBDconnected())?"Still Open":"Still Closed")+" ... ");

			if(getSGBDconnected() ){
				this.Close();
			}
			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Lookup for driver : "+SGBDDriverName);

			// Loading / find driver in user namespace
			try{
				Class.forName(""+SGBDDriverName);
			}catch(Exception EV_ERR_NOTFOUND_DRIVER) {
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : NO driver : "+SGBDDriverName +" : "+EV_ERR_NOTFOUND_DRIVER.getMessage());

				EV_ERR_NOTFOUND_DRIVER.printStackTrace();
				throw EV_ERR_NOTFOUND_DRIVER;
				// :: ;; return 0 ;;
			}
			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Opening connection with driver : "+SGBDDriverName+" : "+getSGBDConnectionString());
			try {
				SGBDConnectionLink = DriverManager.getConnection(getSGBDConnectionString(), SGBDUsername, SGBDUserPassword);
			}catch(Exception EV_ERR_GETCONNECTION) {
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Error in proceed connection with : "+SGBDDriverName+" : "+getSGBDConnectionString());
				throw new Exception(EV_ERR_GETCONNECTION);
				// :: ;; return 0 ;; 
			}

			if(SGBDConnectionLink == null  ) {
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : ERROR : "+SGBDDriverName +" : NULL PTR");
				throw new Exception(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : ERROR : "+SGBDDriverName +" : NULL PTR");
				// :: ;; return 0 ;; 
			}

			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Connected with driver : "+SGBDDriverName+" : "+getSGBDConnectionString());

			SGBDConnectionLinkStatement = SGBDConnectionLink.createStatement();

			// consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Connection Status :  "+((!SGBDConnectionLink.isClosed())?"Now Open":"Still Closed")+" ... ");

			/*if(SGBDConnectionLinkResultSet != null) {
				SGBDConnectionLinkResultSet.clearWarnings();
			}*/

			pingConnection();

			// check if we get connected ...	
			if(!getSGBDconnected()){
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Fatal : Can t proceed to open connection ... ");
				// FATAL ERROR 
				this.Close();
				throw new Exception("Impossible de se connecter ... "+getSGBDConnectionString());
			}else{

				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkStatement.getFetchSize();
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : NOW Status : connection is "+((getSGBDconnected())?"Still Open":"Still Closed")+" ... ");
				return  (SGBDConnectionLinkResultSetCount | (getSGBDconnected()?1:0));
			}

		} catch (SQLException EV_CONNEXION_OPEN_ERROR){
			consoleLog.println("Fatal : Something goes wrong when proceed to load package / open connection ... ");
			EV_CONNEXION_OPEN_ERROR.printStackTrace();
			throw new Exception(EV_CONNEXION_OPEN_ERROR);
		}
		// error
		// :: ;; return 0 ;;
	}

	/**
	 * Close the active connection
	 * @param args
	 */
	public int Close() throws  Exception {
		try{

			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Previous Status : connection is "+((getSGBDconnected() )?"Still Open":"Still Closed")+" ... ");

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

			SGBDconnected = SGBDConnectionLink.isClosed();

			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : connection is NOW "+(( getSGBDconnected() )?"Still Open":"Still Closed")+" ... ");

			return 1;
		} catch (SQLException EV_CONNEXION_CLOSE_ERROR){
			EV_CONNEXION_CLOSE_ERROR.printStackTrace();
			throw EV_CONNEXION_CLOSE_ERROR;
		}


		// :: ;; return 0 ;;
	}

	/**
	 * check if the connection is valid
	 * @param args
	 */
	public Boolean pingConnection() throws Exception {

		try {
			if(SGBDConnectionLink.isValid(30) && !SGBDConnectionLink.isClosed() ) {
				return true;
			}
		} catch (SQLException EV_CONNEXION_PING_ERROR) {
			EV_CONNEXION_PING_ERROR.printStackTrace();
			throw EV_CONNEXION_PING_ERROR;
		}

		return false;
	}

	/**
	 * Send a query to the Database 
	 * @param args
	 */
	public int SendQuery(String sArgSql) throws Exception {
		try {
			SGBDConnectionLinkResultSetColumnMapDescriptor.clear();

			SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.clear();
			SGBDConnectionLinkResultSetColumnMapDescriptorCONST.clear();

			SGBDConnectionLinkResultSetCountPTR = 0;
			SGBDConnectionLinkResultSetCount = 0;
		}catch(Exception EV_ERR_RESET_VALUES) {
			EV_ERR_RESET_VALUES.printStackTrace();
			throw EV_ERR_RESET_VALUES;
		}

		try {
			if(SGBDConnectionLinkResultSet != null) {
				SGBDConnectionLinkResultSet.clearWarnings();
			}
		}catch(Exception EV_ERR_CLEARSTATEMENT) { 
			// nothing to do ;; maybe no previous resultset
		}

		if(!getSGBDconnected()){
			consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : Fetch : DO Open Connection for : "+sArgSql);
			Open();

		}

		// Check which Operation is requested and Always Return Numerical result for status
		if( sArgSql.substring(0, 8).toUpperCase().startsWith("SELECT" ) ) {

			try {
				int iArgSqlPosSelect = sArgSql.toLowerCase().indexOf("select");
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : Fetch : "+sArgSql);
				SGBDConnectionLinkResultSet = SGBDConnectionLinkStatement.executeQuery(sArgSql);

				// point to to first record in resultset
				SGBDConnectionLinkResultSet.first();

				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkStatement.getMaxRows() | SGBDConnectionLinkResultSet .getFetchSize();

				// some server or client doesnt allow count statements ...
				// so we force counting options ... 
				if((SGBDConnectionLinkResultSetCount == 0) && SGBDConnectionLinkResultSet.first() ){

					iArgSqlPosSelect = (( iArgSqlPosSelect == (-1) )? 1: iArgSqlPosSelect  );
					String sArgSqlWithForceCounting = " SELECT FOUND_ROWS() as rows_in_resultset, AATable.* from ( ";
					sArgSqlWithForceCounting +=  sArgSql;
					sArgSqlWithForceCounting += " ) as AATable";

					consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : Fetch : ForceCounted "+String.format("%d", iArgSqlPosSelect)+": "+sArgSqlWithForceCounting);

					SGBDConnectionLinkResultSet = SGBDConnectionLinkStatement.executeQuery( sArgSqlWithForceCounting );

					SGBDConnectionLinkResultSet.first();
					SGBDConnectionLinkResultSetCount = SGBDConnectionLinkResultSet.getInt("rows_in_resultset");
				}
				// check for NON NULL result / FATAL resultset
				if(SGBDConnectionLinkResultSetCount >= 0) {

					try {
						// :: build column list to enable column name matching and exception for table / query field version
						ResultSetMetaData rsmd = SGBDConnectionLinkResultSet.getMetaData();
						int columnCount = rsmd.getColumnCount();

						// consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : generate resultset of ("+(String.format("%d Columns for %d row(s) ", columnCount, SGBDConnectionLinkResultSetCount) )+") ... ");

						// The column count starts from 1
						int iOffset = 0;
						for (int i = 1; i <= columnCount; i++ ) {
							String name = rsmd.getColumnName(i);
							// skipping special column
							int iPosColumn = (i-iOffset);

							if( ( name.toLowerCase().compareTo("rows_in_resultset") == 0) ) {

								// name = rsmd.getColumnName(i+1);
								// consoleLog.println(String.format("Pass to %s %s : %d", " Name : ", name, iPosColumn));
								// SGBDConnectionLinkResultSetColumnMapDescriptor.put(i, name) ;
								// SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.put(name, i);

								iOffset = (( (i-1) >0)? (i - 1) : i);

								// i++;
								//consoleLog.println(String.format("%s %s : %d", " Name : ", name, i) +" :: "+SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.toString());

								continue;
							}
							// Do stuff with name

							SGBDConnectionLinkResultSetColumnMapDescriptor.put(iPosColumn, name) ;
							SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.put(name, iPosColumn );
							SGBDConnectionLinkResultSetColumnMapDescriptorCONST.put(name.toUpperCase(), name);
							// consoleLog.println(String.format("%s %s : %d : %d", " Name : ", name, i, iPosColumn ) +" :: "+SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.toString());

						}


						consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : generate resultset of ("+

											(String.format("%d Columns for %d row(s) ", 
													SGBDConnectionLinkResultSetColumnMapDescriptor.size(),
													SGBDConnectionLinkResultSetCount)

													)+ ") ... ("+ SGBDConnectionLinkResultSetColumnMapDescriptorINTEGERPOS.toString()+")");

					}catch(Exception EV_ERROR_PROCESSING_COLUMNNAMES) {
						consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Something wen wront in query processing :: fetching resultset  ... "+EV_ERROR_PROCESSING_COLUMNNAMES.getMessage());
						throw EV_ERROR_PROCESSING_COLUMNNAMES;
						// :: ;; return 0 ;; 
					}

				}else {
					consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : empty : resultset  ... ");

				}
				// return potential row number / results number
				return SGBDConnectionLinkResultSetCount;

			} catch (Exception EV_ERR_QUERY_PROCESSING) {
				// can t let connection / session open ... so we prevent this by requesting a Close handling ...
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Something went wrong in query processing :: fetching resultset  ... "+EV_ERR_QUERY_PROCESSING.getMessage());

				this.Close(); 
				// TODO Auto-generated catch block
				EV_ERR_QUERY_PROCESSING.printStackTrace();
				throw EV_ERR_QUERY_PROCESSING;
				// :: ;; return 0 ;;
			}

		}else{

			try {
				consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +" : Query : Execute : "+sArgSql);
				SGBDConnectionLinkResultSetCount = SGBDConnectionLinkStatement.executeUpdate(sArgSql);
				SGBDConnectionLinkResultSet = SGBDConnectionLinkStatement.getResultSet();
				// return non Zero if Row in Database was touched 
				// this was a unary expression (ternary) expression, short-end for IF ELSE Statement
				return  ( (SGBDConnectionLinkResultSet .rowDeleted() |
						SGBDConnectionLinkResultSet .rowUpdated() |
						SGBDConnectionLinkResultSet .rowInserted() ) ? 
								// check if non null value was returned
								((SGBDConnectionLinkResultSetCount >=1)? (SGBDConnectionLinkResultSetCount | SGBDConnectionLinkStatement.getUpdateCount())  : 1)
								// else no row was touched by last operation ... returning Zero value  
								: 0 ) ;
			} catch (SQLException EV_ERR_EXECUTESQL) {
				// can t let connection / session open ... so we prevent this by requesting a Close handling ...
				this.Close(); 
				// TODO Auto-generated catch block
				EV_ERR_EXECUTESQL.printStackTrace();
				throw EV_ERR_EXECUTESQL;

			}
		}
		// :: ;; return 0 ;; 

	}
	/* *****************************************
	 * Control set for Query ResultSet :
	 * ;; Next, First, Prev, Last ;;
	 * 
	 * Utility for ResultSet :
	 * ;; extractResultset ;;
	 * 
	 * the Following will point to the (Row) in resultset
	 * before using it the Methods : Next, First, Prev, Last ;; will configure the Pointed Row in the Resultset
	 */
	private Boolean extractResultset()  throws Exception {
		SGBDConnectionLinkStoredReturnedResultSet = new HashMap<Object,Object>();


		// checking if connection resource is valid and connection status is not closed
		if(!pingConnection()) {

			throw new Exception("Can t fetch Next Row in Resultset for closed connection");
			// :: ;; return null ;;
		}

		// return null, no resultset occurs
		if(SGBDConnectionLinkResultSetCount <= 0 ) {
			return false;
		}

		// return null, last reached OR no resultset occurs
		if(SGBDConnectionLinkResultSet.isLast() ) {
			// can t let connection / session open ... so we prevent this by requesting a Close handling ...
			//this.Close(); 
			//return null;
		}

		if( (SGBDConnectionLinkResultSet.wasNull())
				|| SGBDConnectionLinkResultSet.isClosed() ) {
			// can t let connection / session open ... so we prevent this by requesting a Close handling ...
			this.Close(); 
			throw new Exception("Can t fetch Next Row in NULL Resultset / for invalid connection");
			// :: ;; return null ;;
		}

		// return null, EOF for this resultset has occurs
		/*if(SGBDConnectionLinkResultSetCountPTR >= SGBDConnectionLinkResultSetCount ) {
			// can t let connection / session open ... so we prevent this by requesting a Close handling ...
			// this.Close(); 
			return false;
		}*/

		if( SGBDConnectionLinkResultSetColumnMapDescriptor.size() == 0) {
			// can t let connection / session open ... so we prevent this by requesting a Close handling ...
			this.Close(); 
			return false;
		}

		try {

			// :: else 

			// fetch the current PTR position in Resultset
			SGBDConnectionLinkResultSetCountPTR = SGBDConnectionLinkResultSet.getRow();

			for (int i = 1; i <= (SGBDConnectionLinkResultSetColumnMapDescriptor.size()); i++ ) {
				// Do stuff with name
				SGBDConnectionLinkStoredReturnedResultSet.put(
						SGBDConnectionLinkResultSetColumnMapDescriptor.get(i),
						SGBDConnectionLinkResultSet.getObject(SGBDConnectionLinkResultSetColumnMapDescriptor.get(i))
						);
			}


		}catch(Exception EV_ERR_FETCHROW_INRESULTSET){
			EV_ERR_FETCHROW_INRESULTSET.printStackTrace();
			throw EV_ERR_FETCHROW_INRESULTSET;
			// :: return false;
		}
		return true;

	}

	/** *****************************************
	 *  ;; Next, First, Prev, Last ;;
	 * 
	 * the Followings will configure the Pointed Row in the Resultset
	 * And Return Structured Associative Object<Object,Object>
	 */

	/** *****************************************
	 *  First, will rewind to the topmost row in the resultset
	 */
	public Map<Object,Object>  First() throws Exception {

		if( getSGBDconnected() && (getSGBDConnectionLinkResultSetCount() >= 0) ) { 
			// cant do this operation without be connected
			SGBDConnectionLinkResultSet.first();
		}

		return ( (extractResultset()) ? SGBDConnectionLinkStoredReturnedResultSet : null);
	}

	/** *****************************************
	 *  Last, will point to the bottom most row in the resultset
	 */
	public Map<Object,Object>  Last() throws Exception {

		if( getSGBDconnected() && (getSGBDConnectionLinkResultSetCount() >= 0) ) {
			// cant do this operation without be connected
			SGBDConnectionLinkResultSet.last();
		}

		return ( (extractResultset()) ? SGBDConnectionLinkStoredReturnedResultSet : null);
	}

	/** *****************************************
	 *  Current, will point to the currently in use row in the resultset
	 */
	public Map<Object,Object>  Current() throws Exception {

		return ( (extractResultset()) ? SGBDConnectionLinkStoredReturnedResultSet : null);
	}

	/** *****************************************
	 *  Next, will point to the following row in the resultset
	 */
	public Map<Object,Object>  Next() throws Exception {

		if(getSGBDconnected() && !SGBDConnectionLinkResultSet.isLast()) {
			// cant do this operation without be connected
			SGBDConnectionLinkResultSet.next();
			return ( (extractResultset()) ? SGBDConnectionLinkStoredReturnedResultSet : null);
		}

		return null;
	}
	/** *****************************************
	 *  Next, will point to the following row in the resultset
	 */
	public Map<Object,Object>  Prev() throws Exception {

		if(getSGBDconnected() && (getSGBDConnectionLinkResultSetCount() >= 0) ) {
			// cant do this operation without be connected
			SGBDConnectionLinkResultSet.previous();
			return ( (extractResultset()) ? SGBDConnectionLinkStoredReturnedResultSet : null);
		}

		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BDDConnectorHUB bddTestClass =  new BDDConnectorHUB(); 
		PrintStream consoleLog = System.out;
		// TODO Auto-generated method stub
		consoleLog.println("Test Case for class : "+(bddTestClass.getClass().getCanonicalName()));
		try {
			if (  bddTestClass.Open() != 1 ){

				bddTestClass.getConsoleLog().println("Main : Error : Can t open connection ... "+bddTestClass.getSGBDConnectionString());
			}else {

				bddTestClass.getConsoleLog().println("Connected .... now proceed tests ");
				bddTestClass.getConsoleLog().println("TEST 1 ....  ");
				try {
					if( bddTestClass.SendQuery("Select * from stagiaire ") != 0 ) {

						bddTestClass.getConsoleLog().println(bddTestClass.getQuery());
						bddTestClass.getConsoleLog().println(" Generated a Total "+bddTestClass.Count() +" Rows ");

						Map<Object,Object> resultset = new HashMap<Object,Object>();
						Map<Object, String> columnsNames = bddTestClass.getSGBDConnectionLinkResultSetColumnMapDescriptor();

						while( (resultset = bddTestClass.Current()) != null )
						{


							bddTestClass.getConsoleLog().println("***************************************");
							bddTestClass.getConsoleLog().print("|");
							int columnCount = columnsNames.size();
							for (int i = 1; i <= columnCount; i++ ) {
								bddTestClass.getConsoleLog().print(" "+resultset.get( columnsNames.get(i))+" |");
							}
							bddTestClass.getConsoleLog().println("");
							bddTestClass.Next();
						}
						bddTestClass.getConsoleLog().println("***************************************");

						bddTestClass.Close();
					}else{ 

						bddTestClass.getConsoleLog().println(bddTestClass.getQuery());
						bddTestClass.getConsoleLog().println(" Give an Empty Count : "+bddTestClass.Count());
						bddTestClass.Close();
					}
				}catch(Exception EV_ERR_QUERY){
					bddTestClass.getConsoleLog().println(" ERROR in query processing ... "); 
					bddTestClass.getConsoleLog().println(bddTestClass.getQuery()); 
					bddTestClass.getConsoleLog().println(EV_ERR_QUERY.getMessage() );
					return;
				}

				bddTestClass.getConsoleLog().println("TEST 2 ....  ");
				try {
					if( bddTestClass.SendQuery("Select * from user ") != 0 ) {

						bddTestClass.getConsoleLog().println(bddTestClass.getQuery());
						bddTestClass.getConsoleLog().println(" Generated a Total "+bddTestClass.Count() +" Rows ");

						Map<Object,Object> resultset = new HashMap<Object,Object>();
						Map<Object, String> columnsNames = bddTestClass.getSGBDConnectionLinkResultSetColumnMapDescriptor();

						while( (resultset = bddTestClass.Current()) != null )
						{


							bddTestClass.getConsoleLog().println("***************************************");
							bddTestClass.getConsoleLog().print("|");
							int columnCount = columnsNames.size();
							for (int i = 1; i <= columnCount; i++ ) {
								bddTestClass.getConsoleLog().print(" "+resultset.get( columnsNames.get(i))+" |");
							}
							bddTestClass.getConsoleLog().println("");
							bddTestClass.Next();
						}
						bddTestClass.getConsoleLog().println("***************************************");

						bddTestClass.Close();
					}else{ 

						bddTestClass.getConsoleLog().println(bddTestClass.getQuery());
						bddTestClass.getConsoleLog().println(" Give an Empty Count : "+bddTestClass.Count());
						bddTestClass.Close();
					}
				}catch(Exception EV_ERR_QUERY){
					bddTestClass.getConsoleLog().println(" ERROR in query processing ... "); 
					bddTestClass.getConsoleLog().println(bddTestClass.getQuery()); 
					bddTestClass.getConsoleLog().println(EV_ERR_QUERY.getMessage() );
					return;
				}

				bddTestClass.getConsoleLog().println("TEST 3 ....  ");
				bddTestClass.Close();

				bddTestClass.getConsoleLog().println("TEST 4 ....  ");
				bddTestClass.Prev();

			}
		} catch (Exception e) {
			consoleLog.println("Main : Fatal : Can t proceed to open connection ... ");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
