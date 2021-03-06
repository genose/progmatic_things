/**
 * 
 */
package userAuth;

import java.util.HashMap;
import java.util.Map;

import BDDConnectorHUB.*;
 
/**
 * @author 59013-03-13
 *
 */
public class USERAuth {

	final private String BDDSCHEMA_TABLE_NAME_USERS = "user";
	
	private BDDConnectorHUB BDDUserLink = null;
	private Map<Object, Object> mSchemaKeyInfo;
 
	
	/**
	 * 
	 */
	public USERAuth()  throws Exception {
		// TODO Auto-generated constructor stub
		BDDUserLink = new BDDConnectorHUB();
		
		mSchemaKeyInfo = new HashMap<Object, Object>();
		
		
		try {
			// describe database column
			BDDUserLink.SendQuery("Select usr.*, md5(concat(userid, login, password)) as userToken from "+BDDSCHEMA_TABLE_NAME_USERS+" usr limit 1 ");
			
			mSchemaKeyInfo.putAll( BDDUserLink.getSGBDConnectionLinkResultSetColumnMapDescriptorCONST());
			
		} catch (Exception EV_ERR_CLASSINIT_EXCEPTION) {
		    EV_ERR_CLASSINIT_EXCEPTION.printStackTrace();
		    throw new Exception(EV_ERR_CLASSINIT_EXCEPTION);
		}
	 
	}
	
	public Map<Object, Object> getmSchemaKeyInfo() {
		return mSchemaKeyInfo;
	}
	
	public String getmSchemaKeyInfo(String sArgKeyInfo) throws Exception {
		
		if(sArgKeyInfo == null ) {
			throw new Exception(this.getClass().getName()+" : the resquested key (<NULL>) is not acceptable and doens t map to shema ("+BDDSCHEMA_TABLE_NAME_USERS+")");
		}
		
		if(!mSchemaKeyInfo.containsKey(sArgKeyInfo.toUpperCase()))
		{
			throw new Exception(this.getClass().getName()+" : the resquested key ("+sArgKeyInfo+") doens t map to shema ("+BDDSCHEMA_TABLE_NAME_USERS+")");
		}
		
		return mSchemaKeyInfo.get(sArgKeyInfo.toUpperCase()).toString();
	}

	public Map<Object, Object> getUserInfo() throws Exception {

		Map<Object, Object> aMapUserInfo = BDDUserLink.Current();
		// :: BDDUserLink.Close();
		return aMapUserInfo;
	}

	public String getUserInfo(String sArgInfo) throws Exception {
		
		if(sArgInfo == null ) {
			throw new Exception(this.getClass().getName()+" : the resquested key (<NULL>) is not acceptable and doens t map to shema ("+BDDSCHEMA_TABLE_NAME_USERS+")");
		}
		
		if(!mSchemaKeyInfo.containsKey(sArgInfo.toUpperCase()))
		{
			throw new Exception(this.getClass().getName()+" : the resquested key ("+sArgInfo+") wrong typo or doens t map to shema ("+BDDSCHEMA_TABLE_NAME_USERS+")");
		}
		
		Map<Object, Object> sArgFoundUserInfo = BDDUserLink.Current();
		// :: BDDUserLink.Close();
		if(sArgFoundUserInfo == null) {
			return null;
		}
		Object sArgFoundUserInfoFound =	sArgFoundUserInfo.get(sArgInfo);
		return sArgFoundUserInfoFound.toString() ;
	}

	public Boolean authByToken(String sArgToken) throws Exception {
		
		try {
			BDDUserLink.SendQuery("Select usr.*, md5(concat(userid, login, password)) as userToken from "+BDDSCHEMA_TABLE_NAME_USERS+" usr where md5(concat(userid, login, password)) = '"+sArgToken+"' ");
		} catch (Exception EV_ERR_AUTHBYTOKEN) {
			// TODO Auto-generated catch block
			EV_ERR_AUTHBYTOKEN.printStackTrace();
			throw EV_ERR_AUTHBYTOKEN;
			// :: ;; return false ;;
		}
		
		if(BDDUserLink.Count() >= 1) {
			return true;
		}
		
		return false;
	}
	
	public Boolean authByUserPassowrd(String sArgLogin, String sArgPassword) throws Exception {
		
		try {
			BDDUserLink.SendQuery("Select usr.*, md5(concat(userid, login, password)) as userToken from "+BDDSCHEMA_TABLE_NAME_USERS+" usr where  login = '"+sArgLogin+"' and password = '"+sArgPassword+"' ");
		} catch (Exception EV_ERR_AUTHBYUSERPASSWORD) {
			// TODO Auto-generated catch block
			EV_ERR_AUTHBYUSERPASSWORD.printStackTrace();
			throw EV_ERR_AUTHBYUSERPASSWORD;
			// :: ;; return false ;;
		}
		
		if(BDDUserLink.Count() >= 1) {
			return true;
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
