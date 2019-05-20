/**
 * 
 */
package application_controleur;
import application_controleur.*;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 59013-03-13
 *
 */
@Named(value = "account_manager_handler")
public class account_manager_handler {

	/**
	 * 
	 */
	public account_manager_handler() { 
		// TODO Auto-generated constructor stub
	}

	
	 public String actionHandler_createUser() {
		// HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		 //System.out.println(" .... "+String.valueOf(req.getParameter("password"))); 
		 
		 return ApplicationConfig_HUBResponse.APPLICATIONCONFIG_HUBRESPONSE_SUCCESS.getCodeNameValue();
	 }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
