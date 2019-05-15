package application_controleur;


import java.io.Serializable;


import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.sun.javafx.collections.MappingChange.Map;

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
    
    
    public String retourAction() {
    	//Map<String, String> params =(Map<String, String>) FacesContext.getCurrentInstance().
        //**        getExternalContext().getRequestParameterMap();
		 System.out.println(" ....;; ");
		 //System.out.println(" .... "+String.valueOf(params.map("password"))); 
		 
        return login.equals( "admin" ) && password.equals( "admin" ) ? "success" : "failure";
    }
    
}