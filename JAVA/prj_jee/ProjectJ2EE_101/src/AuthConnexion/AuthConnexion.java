package AuthConnexion;
 
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omg.CORBA.portable.InputStream;

import userAuth.*;

/**
 * Servlet implementation class AuthConnexion
 */
@WebServlet(
		name = "AuthConnexion",
		description = "Authentification process thru jsp page",
		urlPatterns = {"/AuthConnexion","/*"}
		)
 
public class AuthConnexion extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/* ************************************************** */
	public String DEFAULT_BASEPATH = "keyDefaultBasePath";
	/* ************************************************** */
	
	
	final String AUTH_TOKEN_KEY = "userToken";
	final String AUTH_INFO_USERNAME_KEY = "userNameToken";
	final String AUTH_INFO_PASSWORD_KEY = "userPasswordToken";
	/* ************************************************** */
	final String APP_CURRENT_FRONT_ORIGIN 						= "httpHeaderReferer";
	final String APP_CURRENT_FRONT_ORIGIN_REQUEST_URI 			= "httpHeaderRefererRequestURI";
	final String APP_CURRENT_FRONT_ORIGIN_SERVLET 				= "httpHeaderRefererServletOrigin";
	
	
	
	final String DEFAULT_PAGE_INDEX 							= "_index.jsp";
	final String DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT 		= "compte.jsp";
	/* ************************************************** */
	final String DEFAULT_PAGE_AUTH_CONNEXION 					= "/AuthConnexion";
	/* ************************************************** */
	
	final String DEFAULT_PAGE_AUTH_PASSED_WELCOME 	= "connexionReussie.jsp";
	/* ************************************************** */
	private PrintStream		consoleLog 										= System.out;
	private HttpSession		userSession 									= null;
	/* ************************************************** */
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthConnexion() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getRequestURI());
		consoleLog.println(this.getServletContext().getServletContextName()+" :: GET :: Served at: "+request.getRequestURI() );
		String requestURIServlet = request.getServletPath();
		String contextppathURI = this.getServletContext().getContextPath();
		String documentroots = this.getServletContext().getRealPath("/");
		// checking for non Servlet ressource ...
		
		if(
				(requestURIServlet.toLowerCase().indexOf(".css") >=1) 
				|| (requestURIServlet.toLowerCase().indexOf(".js") >=1)
				|| (requestURIServlet.toLowerCase().indexOf(".png") >=1)

				|| (requestURIServlet.toLowerCase().indexOf(".gif") >=1)
				|| (requestURIServlet.toLowerCase().indexOf(".tiff") >=1)
				
				|| (requestURIServlet.toLowerCase().indexOf(".jpg") >=1)
				|| (requestURIServlet.toLowerCase().indexOf(".jpeg") >=1) 
				)
		{
			final int ARBITARY_SIZE = 1048;
			// RequestDispatcher view = request.getRequestDispatcher(request.getServletPath());
			// view.forward(request, response);
			 FileInputStream fstream = new FileInputStream(documentroots+""+requestURIServlet);

	        DataInputStream dis = new DataInputStream(fstream);
	        FileChannel fc = fstream.getChannel();
	        ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,(int) fc.size());
	        Charset cs = Charset.forName("8859_1");
	        CharsetDecoder cd = cs.newDecoder();
	        CharBuffer cb = cd.decode(bb);  
	        response.setContentType("text/css");
			// response.getWriter().append(bb.());
			
			
	        
	        // resp.setHeader("Content-disposition", "attachment; filename=sample.txt");
	 
	        try{
	        	ServletContext curcontext = request.getServletContext();
	        	
	        	if(curcontext  == null )
	        		consoleLog.println(" NULL context");
 
	        	OutputStream out = response.getOutputStream();

	 
	            byte[] buffer = new byte[ARBITARY_SIZE];
	        
	            int numBytesRead;
	            
	            while ((numBytesRead = dis.read(buffer) ) > 0)
	            	  out.write(buffer, 0, numBytesRead);
	          
	        }catch(Exception EV_ERR_FILEREAD){
	        	consoleLog.println(" :: Error "+EV_ERR_FILEREAD.getMessage());
	        }
	  
			
			
			return;
			
		}
		handleRequest(request, response, 1);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		consoleLog.println(this.getServletContext().getServletContextName()+"  :: POST :: Served at: "+request.getRequestURI() );
		handleRequest(request, response, 2);
	}
	
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response, int handleType) throws ServletException, IOException {
		
		 		request.setAttribute(DEFAULT_BASEPATH, request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath()); 
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
				
				// Lookup and collecting info about origin of the request
				
				String applicationPageHub_Referer 								= request.getPathInfo();
				String applicationPageHub_Referer_Header 						= request.getHeader("referer");
				String applicationPageHub_Referer_Servlet 						= request.getServletPath();
				
				String applicationPageHub_Request 								= String.valueOf( request.getRequestURL());
				String applicationPageHub_Request_URI 							= request.getRequestURI();
				String applicationPageHub_Request_QueryString 					= request.getQueryString();				
				
				// assigning some values ... 
				tbHandledValue = ((applicationPageHub_Referer_Header !=null) 		? (( !String.valueOf( applicationPageHub_Referer_Header ).isEmpty() && (String.valueOf( applicationPageHub_Referer_Header ).length() >= 5) )? (boolean) (oUserAuthInfo.put(APP_CURRENT_FRONT_ORIGIN, applicationPageHub_Referer_Header ) == null) : false) : false);
				
				tbHandledValue = ((applicationPageHub_Request_URI !=null) 		? (( !String.valueOf( applicationPageHub_Request_URI ).isEmpty() && (String.valueOf( applicationPageHub_Request_URI ).length() >= 1) )? (boolean) (oUserAuthInfo.put(APP_CURRENT_FRONT_ORIGIN_REQUEST_URI, applicationPageHub_Request_URI ) == null) : false) : false);
				tbHandledValue = ((applicationPageHub_Referer_Servlet !=null) 		? (( !String.valueOf( applicationPageHub_Referer_Servlet ).isEmpty() && (String.valueOf( applicationPageHub_Referer_Servlet ).length() >= 5) )? (boolean) (oUserAuthInfo.put(APP_CURRENT_FRONT_ORIGIN_SERVLET, applicationPageHub_Referer_Servlet ) == null) : false) : false);
				
				// more assignations value ... here for autentification process
				tbHandledValue = ((userToken !=null) 		? (( !String.valueOf( userToken ).isEmpty() && (String.valueOf( userToken ).length() > 5)  )? (boolean) (oUserAuthInfo.put(AUTH_TOKEN_KEY, userToken) == null) : false) : false);
				tbHandledValue = ((userPassword != null) 	? (( !String.valueOf( userPassword ).isEmpty() && (String.valueOf( userPassword ).length() > 0)  )? (boolean) (oUserAuthInfo.put(AUTH_INFO_PASSWORD_KEY, userPassword) == null) : false) : false);
				tbHandledValue = ((userLogin !=null) 		? (( !String.valueOf( userLogin ).isEmpty() && (String.valueOf( userLogin ).length() >= 5) )? (boolean) (oUserAuthInfo.put(AUTH_INFO_USERNAME_KEY, userLogin) == null) : false) : false);
				
				
				// redirect user thru Authentification process if necessary
				// Else forward to requested page ...
				try {
					
					
					consoleLog.println(getClass().getName()+" :: AUTH : Entering request with : "+oUserAuthInfo.toString());
					if(doAuth( oUserAuthInfo, request, response )) 
					{
						request.setAttribute("loginToken", userLogin );
						request.setAttribute("loginUsername", userLogin );			
						// message for the user			
						request.setAttribute("loginMessage", "" );
						
						if( (applicationPageHub_Referer.indexOf( DEFAULT_PAGE_INDEX) >0 )  && (applicationPageHub_Request.indexOf(DEFAULT_PAGE_INDEX) >= 0))
						{
							
						}else
						if( (applicationPageHub_Request.indexOf(DEFAULT_PAGE_INDEX) >= 0) && (applicationPageHub_Request_URI.indexOf(DEFAULT_PAGE_INDEX) <= 0)) {
							// response.getWriter().append(" Hello : ").append( userLogin );
							RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
							view.forward(request, response);
						}else if(applicationPageHub_Referer_Servlet.indexOf(DEFAULT_PAGE_AUTH_CONNEXION) >= 0) {
							// response.getWriter().append(" Hello : ").append( userLogin );
							RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
							view.forward(request, response);
						}else {
							// do some stuff that progam doesn t really care nor know 
							response.getWriter().append(" Hello : ").append( userLogin );
							//RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_WELCOME);
							//view.forward(request, response);
							
						} 
					}else {
						
						// response.getWriter().append("Access denied ").append( userLogin ).append( userPassword );
						// request.setAttribute("loginMessage", loginMessage .append("Access denied ").append( userLogin ).append( userPassword ); );
						consoleLog.println(getClass().getName()+" : AUTH : Redirecting user to default auth page ... "+DEFAULT_PAGE_INDEX);
						request.setAttribute("loginMessage", loginMessage );
						if( (applicationPageHub_Referer == null) ||  (applicationPageHub_Referer.indexOf( DEFAULT_PAGE_INDEX) >0 )  && (applicationPageHub_Request.indexOf(DEFAULT_PAGE_INDEX) >= 0))
						{
							response.getWriter().append(" <a href=\""+request.getContextPath()+"\">Veuillez vous authentifier ...</a> : ").append( userToken );
						}else {
							RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_INDEX);
							view.forward(request, response);
						}
					}
					
					
				 
					
				} catch (Exception EV_ERR_DOPOST) {
					// TODO Auto-generated catch block
					EV_ERR_DOPOST.printStackTrace();
					response.sendError(500, EV_ERR_DOPOST.toString());
//					view.forward(request, response);
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
					consoleLog.println(getClass().getName()+" : User(token)  can t be identified with this token (["+userToken+"]:"+userToken.length()+")");
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
					consoleLog.println(getClass().getName()+" : User(login) can t be identified (["+userLogin+"]:"+userLogin.length()+";;["+userPassword+"]:"+userPassword.length()+")");
				}			 
			}else {
				consoleLog.println(getClass().getName()+" : User(Params fail) can t be authenticated with promoted info (["+oArgAuth.toString()+"])");
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
