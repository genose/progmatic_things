/**
 * 
 */
package application_controleur;


import javax.enterprise.context.ApplicationScoped;

import javax.faces.annotation.FacesConfig;
import javax.faces.annotation.FacesConfig.Version;



/**
 * @author 59013-03-13
 *
 */
@ApplicationScoped
@FacesConfig( version = FacesConfig.Version.JSF_2_3 )         
public class ApplicationConfig {
    
	
	public Enum<String> applicationHUBResponse = {
			applicationHUBResponse("ss")
		
	};
}
