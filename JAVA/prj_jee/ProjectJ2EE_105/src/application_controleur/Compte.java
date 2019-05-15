package application_controleur;


import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "compte")
@SessionScoped
public class Compte implements Serializable {


    private static final long serialVersionUID = -5433850275008415405L;

    private String login = "admin";
    private String password = "admin";
    
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
        
        return login.equals( "admin" ) && password.equals( "admin" ) ? "success" : "failure";
    }
    
}