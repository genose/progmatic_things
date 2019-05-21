package application_controleur;


import java.io.Serializable;


import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.sun.javafx.collections.MappingChange.Map;

import users_accounts.Account;

@Named(value = "compte")
@SessionScoped
public class Compte implements Serializable {


    private static final long serialVersionUID = -5433850275008415405L;

    private String login = "admin";
    private String password = "loli";
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getPassword() {
        
        return password;
    }
    
    public void setPassword(String password) {
        
        this.password = password;
    }
    
    
    public String actionHandler_ConnectionValidate() throws Exception {
    	//Map<String, String> params =(Map<String, String>) FacesContext.getCurrentInstance().
        //**        getExternalContext().getRequestParameterMap();
		 System.out.println(" ....;; ");
		 //System.out.println(" .... "+String.valueOf(params.map("password"))); 
		 Account aLoginAccount = null;
		try {
			
			System.out.println("*********Find account for ("+login+"::"+password+")**********\n");
			aLoginAccount = Account.find(login, password);
			System.out.println("*******************\n  result : "+aLoginAccount);
			System.out.println("*******************\n");
			
			
		} catch (Exception EV_ERR_FINDENT) {
			// TODO Auto-generated catch block
			System.out.println("******* action ERROR ******\n"+EV_ERR_FINDENT);
			throw new Exception("ERROR While retrieve entity ... "+EV_ERR_FINDENT);
			// EV_ERR_FINDENT.printStackTrace();
		}
		
        return ( (aLoginAccount != null) && (aLoginAccount.getIdAccount() != 0) ) ? 
        		ApplicationConfig_HUBResponse.APPLICATIONCONFIG_HUBRESPONSE_AUTH_SUCCESS.getCodeNameValue() :
        		ApplicationConfig_HUBResponse.APPLICATIONCONFIG_HUBRESPONSE_AUTH_FAILURE.getCodeNameValue();
    }
    
}