

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BDDConnector.*;
import userAuth.*;

/**
 * Servlet implementation class MyConnexion
 */
@WebServlet("/MyConnexion")
public class MyConnexion extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	final String AUTH_TOKEN_KEY = "userToken";
	final String AUTH_INFO_USERNAME_KEY = "userNameToken";
	final String AUTH_INFO_PASSWORD_KEY = "userPasswordToken";
	
	final String APP_CURRENT_FRONT_ORIGIN 						= "httpHeaderReferer";
	final String DEFAULT_PAGE_INDEX 							= "index.jsp";
	final String DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT 		= "compte.jsp";
	
	final String DEFAULT_PAGE_AUTH_CONNEXION 					= "connexion.jsp";
	
	
	final String DEFAULT_PAGE_AUTH_PASSED_WELCOME 	= "connexionReussie.jsp";
	
	private PrintStream		consoleLog 										= System.out;
	private HttpSession		userSession 									= null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyConnexion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		Boolean tbHandledValue = false;
		Map <Object, Object> oUserAuthInfo = new HashMap<Object, Object>();
		
		String loginMessage = "Vous devez etre authentifier";
		String userLogin = request.getParameter("username");
		String userPassword = request.getParameter("password");
		
		userSession = request.getSession(false); // get the current session cookie info IF EXISTS
		// if no current Session ... let s create one ...
		/* if (userSession != null   ) { 
			userSession.setAttribute(AUTH_TOKEN_KEY, "");
		}*/
		
		// get the user token previously given by Authentification process
		String userToken =(( (userSession != null) && (String.valueOf(userSession.getAttribute(AUTH_TOKEN_KEY) ).length() >5) )? ((String)userSession.getAttribute(AUTH_TOKEN_KEY)) : "**" ) ; // ::  (( request.getParameter(AUTH_TOKEN_KEY)  == null) ?  ((userSession != null && request.isRequestedSessionIdValid() )? ((String)userSession.getAttribute(AUTH_TOKEN_KEY)) : null ): request.getParameter(AUTH_TOKEN_KEY) ) ;
		
		// Lookup for origin of the request
		String applicationPageHubRefererHeader = request.getHeader("referer");
		String applicationPageHubReferer = request.getPathInfo();
		
		
		// assigning some values ... 
		tbHandledValue = ((applicationPageHubReferer !=null) 		? (( !String.valueOf( applicationPageHubReferer ).isEmpty() && (String.valueOf( applicationPageHubReferer ).length() >= 5) )? (boolean) (oUserAuthInfo.put(APP_CURRENT_FRONT_ORIGIN, applicationPageHubReferer ) == null) : false) : false);
		// more assignations value ... here for autentification process
		tbHandledValue = ((userToken !=null) 		? (( !String.valueOf( userToken ).isEmpty() && (String.valueOf( userToken ).length() > 5)  )? (boolean) (oUserAuthInfo.put(AUTH_TOKEN_KEY, userToken) == null) : false) : false);
		tbHandledValue = ((userPassword != null) 	? (( !String.valueOf( userLogin ).isEmpty() && (String.valueOf( userLogin ).length() > 0)  )? (boolean) (oUserAuthInfo.put(AUTH_INFO_PASSWORD_KEY, userPassword) == null) : false) : false);
		tbHandledValue = ((userLogin !=null) 		? (( !String.valueOf( userLogin ).isEmpty() && (String.valueOf( userLogin ).length() >= 5) )? (boolean) (oUserAuthInfo.put(AUTH_INFO_USERNAME_KEY, userLogin) == null) : false) : false);
		
		
		// redirect user thru Authentification process if necessary
		// Else forward to requested page ...
		try {
			if(doAuth( oUserAuthInfo, request, response )) 
			{
				request.setAttribute("loginToken", userLogin );
				request.setAttribute("loginUsername", userLogin );			
				// message for the user			
				request.setAttribute("loginMessage", "" );
				
				
				if( (applicationPageHubReferer.indexOf(DEFAULT_PAGE_INDEX) >= 0) || (applicationPageHubReferer.indexOf(DEFAULT_PAGE_INDEX) >= 0)) {
					// response.getWriter().append(" Hello : ").append( userLogin );
					RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
					view.forward(request, response);
				}else if(applicationPageHubReferer.indexOf(DEFAULT_PAGE_AUTH_CONNEXION) >= 0) {
					// response.getWriter().append(" Hello : ").append( userLogin );
					RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
					view.forward(request, response);
				}else {
					// do some stuff that progam doesn t really care nor know
				
					// 
					response.getWriter().append(" Hello : ").append( userLogin );
					//RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_WELCOME);
					//view.forward(request, response);
					
				}
				
				
				
				
			}else {
				
				// response.getWriter().append("Access denied ").append( userLogin ).append( userPassword );
				// request.setAttribute("loginMessage", loginMessage .append("Access denied ").append( userLogin ).append( userPassword ); );
				consoleLog.println(""+"");
				request.setAttribute("loginMessage", loginMessage );
				RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_CONNEXION);
				view.forward(request, response);
			}
			
			
		 
			
		} catch (Exception EV_ERR_DOPOST) {
			// TODO Auto-generated catch block
			EV_ERR_DOPOST.printStackTrace();
			response.sendError(500, EV_ERR_DOPOST.toString());
//			view.forward(request, response);
		}
		
		
	}
	
	protected Boolean doAuth(Map<Object, Object> oArgAuth, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Boolean bAuthValidation = false;
		// check what AUTH Method is USED
		try {
			USERAuth oAuthenticatedUser = new USERAuth();
			
			// By MD5 TOKEN ;; is may previously identified
			if(oArgAuth.containsKey(AUTH_TOKEN_KEY)){				
				
				String userToken = (String) oArgAuth.get( AUTH_TOKEN_KEY ) ;
			
				if ( oAuthenticatedUser.authByToken(userToken) ) {
						consoleLog.println(getClass().getName()+" : User(token) is identified has ("+oAuthenticatedUser.getUserInfo(oAuthenticatedUser.getmSchemaKeyInfo("USERID"))+")");
						bAuthValidation = true;	
				}else {
					consoleLog.println(getClass().getName()+" : User can t be identified with this token (["+userToken+"]:"+userToken.length()+")");
				}
			}else if(
						oArgAuth.containsKey(APP_CURRENT_FRONT_ORIGIN) &&
						oArgAuth.containsKey(AUTH_INFO_USERNAME_KEY) &&
						oArgAuth.containsKey(AUTH_INFO_PASSWORD_KEY)
					) {
				
				
				String userLogin = (String) oArgAuth.get( AUTH_INFO_USERNAME_KEY );
				String userPassword = (String) oArgAuth.get( AUTH_INFO_PASSWORD_KEY );
		 
				if ( oAuthenticatedUser.authByUserPassowrd( userLogin, userPassword) ) {
					consoleLog.println(getClass().getName()+" : User(login) is identified has ("+oAuthenticatedUser.getUserInfo(oAuthenticatedUser.getmSchemaKeyInfo("USERID"))+")");
					bAuthValidation = true;	
				}else {
					consoleLog.println(getClass().getName()+" : User can t be identified (["+userLogin+"]:"+userLogin.length()+";;["+userPassword+"]:"+userPassword.length()+")");
				}			 
			} 
		
			// check if all is good 
			if(bAuthValidation) {
				if (userSession == null ) { 
					userSession = request.getSession(true);
				}
				// assigning token
				String sUserTokenValidationFound = oAuthenticatedUser.getUserInfo(oAuthenticatedUser.getmSchemaKeyInfo("USERTOKEN"));
				userSession.setAttribute(AUTH_TOKEN_KEY, sUserTokenValidationFound );
				return true;
			}else {
				consoleLog.println(getClass().getName()+" : User can t be authenticated with promoted info (["+oArgAuth.toString()+"])");
			}
			
		} catch (Exception EV_ERR_AUTH_EXCEPTION) {
			// TODO Auto-generated catch block
			EV_ERR_AUTH_EXCEPTION.printStackTrace();
			throw EV_ERR_AUTH_EXCEPTION;
			// return false;
		} 
		
		// if no current Session ... let s create one ...
		if (userSession != null ) { 
			userSession.setAttribute(AUTH_TOKEN_KEY, "");
		}
		
		return false;
		
	}

}
