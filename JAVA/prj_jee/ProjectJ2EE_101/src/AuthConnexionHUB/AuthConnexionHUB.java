package AuthConnexionHUB;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
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
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



import org.omg.CORBA.portable.InputStream;

import userAuth.*;

/**
 * Servlet implementation class AuthConnexionHUB
 */
@WebServlet(
	name = "AuthConnexionHUB",
	description = "Authentification process thru jsp page",
	urlPatterns = {"/AuthConnexionHUB","/AuthConnexionHUB/*"}
	)

/* ************
 * 
<filter-mapping>
    <filter-name>UrlRewriteFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>FORWARD</dispatcher>
</filter-mapping>

 ************ */

public class AuthConnexionHUB extends HttpServlet implements Filter {
    private static final long serialVersionUID = 1L;

    /* ************************************************** */
    public String DEFAULT_BASEPATH = "keyDefaultBasePath";
    /* ************************************************** */


    final String AUTH_TOKEN_KEY = "userToken";
    final String AUTH_INFO_USERNAME_KEY = "userNameToken";
    final String AUTH_INFO_PASSWORD_KEY = "userPasswordToken";
    /* ************************************************** */
    final String APP_CURRENT_FRONT_ORIGIN 					= "httpHeaderReferer";
    final String APP_CURRENT_FRONT_ORIGIN_REQUEST_URI 			= "httpHeaderRefererRequestURI";
    final String APP_CURRENT_FRONT_ORIGIN_SERVLET 				= "httpHeaderRefererServletOrigin";



    final String DEFAULT_PAGE_INDEX 					= "_index.jsp";
    final String DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT 			= "compte.jsp";
    /* ************************************************** */
    final String DEFAULT_PAGE_AUTH_CONNEXION 				= "/AuthConnexionHUB";
    /* ************************************************** */

    final String DEFAULT_PAGE_AUTH_PASSED_WELCOME 	= "connexionReussie.jsp";
    /* ************************************************** */
    private PrintStream		consoleLog 										= System.out;
    private HttpSession		userSession 									= null;
    /* ************************************************** */
    String classIndentedName 							= "";
    String applicationPageHub_Referer						= "";
    String applicationPageHub_Referer_Header					= "";
    String applicationPageHub_Referer_Servlet					= "";

    String applicationPageHub_Request_HTTP_URI					= "";
    String applicationPageHub_Request_URI						= "";
    String applicationPageHub_Request_QueryString 					= "";
    String applicationPageHub_Request_ContextPath 					= "";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthConnexionHUB() {
	super();
	// TODO Auto-generated constructor stub

	classIndentedName = getClass().getName();
	classIndentedName = classIndentedName .substring(
		classIndentedName.lastIndexOf(".")+1,
		String.valueOf(classIndentedName).length() );
    }
    /*
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
     throws ServletException,
            java.io.IOException {
    	consoleLog.println("Service HttpServletRequest called ... "+request.getQueryString());
    }*/

    /*public void service(ServletRequest request,
            ServletResponse response)
     throws ServletException,
            java.io.IOException {

    	consoleLog.println("Service ServletRequest called ... "+request.getServletContext().getContextPath()+"::"+request.getServletContext().getServletContextName());
    	//if(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT))
    	{
    		// RequestDispatcher view = request.getRequestDispatcher("/"+DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
    		//view.include(request, response);
    	}
		//consoleLog.println("Buffer size : "+String.valueOf(request.getContentLength())+"::"+String.valueOf(request.getInputStream().available())+" :: OUTPUT :: "+String.valueOf(response.getBufferSize())+"::"+String.valueOf(response.getContentType()) );
		consoleLog.println("Buffer Reader size : "+String.valueOf(request.getReader().lines().count()));
		response.flushBuffer();
    }
     */

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// TODO Auto-generated method stub
	// response.getWriter().append("Served at: ").append(request.getRequestURI());

	// checking for non Servlet ressource ...


	applicationPageHub_Referer						= request.getPathInfo(); // a JSP page request in URI / Execution context
	applicationPageHub_Referer_Header					= request.getHeader("referer");
	applicationPageHub_Referer_Servlet					= request.getServletPath();

	applicationPageHub_Request_HTTP_URI					= String.valueOf( request.getRequestURL());
	applicationPageHub_Request_URI						= request.getRequestURI();
	applicationPageHub_Request_QueryString 					= request.getQueryString();
	applicationPageHub_Request_ContextPath 					= request.getContextPath();
	consoleLog.println("************************** \n HTTP ENV Received  ....");
	consoleLog.println(getServletContext().getServletContextName()+" :: GET :: Served at: "+request.getRequestURI() );
	consoleLog.println("\n   ;; applicationPageHub_Referer = "+String.valueOf( applicationPageHub_Referer ));
	consoleLog.println("\n   ;; applicationPageHub_Referer_Header = "+String.valueOf(applicationPageHub_Referer_Header ));
	consoleLog.println("\n   ;; applicationPageHub_Referer_Servlet = "+String.valueOf(applicationPageHub_Referer_Servlet ));
	consoleLog.println("\n   ;; applicationPageHub_Request_URI = "+String.valueOf( applicationPageHub_Request_URI ));
	consoleLog.println("\n   ;; applicationPageHub_Request_QueryString = "+String.valueOf( applicationPageHub_Request_QueryString ));
	consoleLog.println("************************** \n ");


	String contextppathURI = this.getServletContext().getContextPath();
	String documentroots = this.getServletContext().getRealPath("/");


	String requestURIServlet = request.getRequestURI();
	String ressourceNameLower = String.valueOf(applicationPageHub_Request_URI.toLowerCase()).replaceFirst("xxsrc", "src").replaceAll(classIndentedName.toLowerCase(), "").replaceFirst(contextppathURI.toLowerCase(), "").replaceAll("//", "/");

	boolean bForwardTypeIOStream_CSS 	= (ressourceNameLower.indexOf(".css") >=1);
	boolean bForwardTypeIOStream_JS 	= (ressourceNameLower.indexOf(".js") >=1) && (ressourceNameLower.indexOf(".jsp") == (-1)) ;
	boolean bForwardTypeIOStream_PNG 	= (ressourceNameLower.indexOf(".png") >=1);

	boolean bForwardTypeIOStream_GIF 	= (ressourceNameLower.indexOf(".gif") >=1);
	boolean bForwardTypeIOStream_TIFF 	= (ressourceNameLower.indexOf(".tiff") >=1);

	boolean bForwardTypeIOStream_JPG 	= (ressourceNameLower.indexOf(".jpg") >=1);
	boolean bForwardTypeIOStream_JPEG 	= (ressourceNameLower.indexOf(".jpeg") >=1); 


	boolean bForwardTypeIOStream = bForwardTypeIOStream_CSS  
		|| bForwardTypeIOStream_JS 
		|| bForwardTypeIOStream_PNG
		|| bForwardTypeIOStream_GIF
		|| bForwardTypeIOStream_TIFF
		|| bForwardTypeIOStream_JPG
		|| bForwardTypeIOStream_JPEG;


	boolean bRequestTypeValidate = (ressourceNameLower.indexOf(".jsp") >=1); 


	if( bForwardTypeIOStream )
	{


	    try{

		final int ARBITARY_SIZE = 1048;
		// RequestDispatcher view = request.getRequestDispatcher(request.getServletPath());
		// view.forward(request, response);
		ressourceNameLower = String.valueOf(documentroots+""+ressourceNameLower).replaceAll(contextppathURI, "").replaceAll("//", "/");
		FileInputStream fstream = new FileInputStream(ressourceNameLower);

		DataInputStream dis = new DataInputStream(fstream);
		FileChannel fc = fstream.getChannel();
		ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,(int) fc.size());
		Charset cs = Charset.forName("8859_1");
		CharsetDecoder cd = cs.newDecoder();
		CharBuffer cb = cd.decode(bb);
		// response.setContentType("text/css");
		// response.getWriter().append(bb.());
		// resp.setHeader("Content-disposition", "attachment; filename=sample.txt");

		ServletContext curcontext = request.getServletContext();
		
		if(curcontext  == null )
		    consoleLog.println(" NULL context");

		OutputStream out = response.getOutputStream();


		byte[] buffer = new byte[ARBITARY_SIZE];

		int numBytesRead;
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.setContentType(curcontext.getMimeType("text/css"));
		consoleLog.println(getServletContext().getServletContextName()+"::"+getClass().getName()+" :: GET :: Providing forward IOStream for : "+ressourceNameLower+" \n:: Served AT :"+request.getRequestURI() );

		while ((numBytesRead = dis.read(buffer) ) > 0)
		    out.write(buffer, 0, numBytesRead);

	    }catch(Exception EV_ERR_FILEREAD){
	    	consoleLog.println(getServletContext().getServletContextName()+"::"+getClass().getName()+" :: Error Providing Forward IOStream : "+ressourceNameLower+" : "+EV_ERR_FILEREAD.getMessage());
	    	response.sendError(404);
	    	return;
	    }


	    consoleLog.println(getServletContext().getServletContextName()+"::"+getClass().getName()+" :: GET :: END :: forward IOStream for : "+ressourceNameLower+" \n:: Served AT :"+request.getRequestURI() );
	    return;

	}

	//HttpServletResponse aAltResponse = new HttpServletResponseWrapper(response);



	handleRequest(request, response, 1);

	String outpuResponse = String.valueOf(response.getBufferSize());
	// String outpuResponseAlt = String.valueOf(aAltResponse.getBufferSize());

	//aAltResponse.getOutputStream().flush();
	// aAltResponse.flushBuffer();
	//aAltResponse.getOutputStream().close();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	applicationPageHub_Referer						= request.getPathInfo(); // a JSP page request in URI / Execution context
	applicationPageHub_Referer_Header					= request.getHeader("referer");
	applicationPageHub_Referer_Servlet					= request.getServletPath();

	applicationPageHub_Request_HTTP_URI					= String.valueOf( request.getRequestURL());
	applicationPageHub_Request_URI						= request.getRequestURI();
	applicationPageHub_Request_QueryString 					= request.getQueryString();
	applicationPageHub_Request_ContextPath 					= request.getContextPath();
	consoleLog.println("************************** \n HTTP ENV Received  ....");
	consoleLog.println(this.getServletContext().getServletContextName()+"  :: POST :: Served at: "+request.getRequestURI() );
	consoleLog.println("\n   ;; applicationPageHub_Referer = "+String.valueOf( applicationPageHub_Referer ));
	consoleLog.println("\n   ;; applicationPageHub_Referer_Header = "+String.valueOf(applicationPageHub_Referer_Header ));
	consoleLog.println("\n   ;; applicationPageHub_Referer_Servlet = "+String.valueOf(applicationPageHub_Referer_Servlet ));
	consoleLog.println("\n   ;; applicationPageHub_Request_URI = "+String.valueOf( applicationPageHub_Request_URI ));
	consoleLog.println("\n   ;; applicationPageHub_Request_QueryString = "+String.valueOf( applicationPageHub_Request_QueryString ));
	consoleLog.println("************************** \n ");




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


	    consoleLog.println(getClass().getName()+" :: AUTH ("+request.getDispatcherType().toString()+"::"+request.getMethod()+"::"+request.getContentType()+"::"+String.valueOf(userToken)+") : Entering request with : "+oUserAuthInfo.toString());
	    if(doAuth( oUserAuthInfo, request, response )) 
	    {
		request.setAttribute("loginToken", userLogin );
		request.setAttribute("loginUsername", userLogin );			
		// message for the user			
 
		if (userSession != null ){
			userSession.setAttribute("loginMessage", "" ); 
		};
		
		if(userToken != null)
		{

		    // :: TODO :: response.sendRedirect(applicationPageHub_Request_ContextPath+"/"+DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
		    // applicationPageHub_Request_ContextPath

		    // applicationPageHub_Referer_Header

		    consoleLog.println(getClass().getName()+" : AUTH : Following user to TOKENIZED auth page ... "+applicationPageHub_Request_HTTP_URI+" ::  "+applicationPageHub_Referer_Header);
		    String requestedThruForwardAuthUri = "/";

		    if(String.valueOf(applicationPageHub_Referer_Header).indexOf(applicationPageHub_Request_ContextPath) != (-1) ) {
			requestedThruForwardAuthUri = String.valueOf(applicationPageHub_Referer_Header).substring(
				String.valueOf(applicationPageHub_Referer_Header).indexOf(applicationPageHub_Request_ContextPath),
				String.valueOf(applicationPageHub_Referer_Header).length() );
			// + String.valueOf(applicationPageHub_Request_ContextPath)

		    }
		    consoleLog.println("A requestedThruForwardAuthUri = "+requestedThruForwardAuthUri+" :: "+getClass().getName());


		    if(String.valueOf(requestedThruForwardAuthUri).indexOf(classIndentedName ) != (-1) ) {
			requestedThruForwardAuthUri = String.valueOf(requestedThruForwardAuthUri).substring(
				String.valueOf(requestedThruForwardAuthUri).indexOf(classIndentedName ),
				String.valueOf(requestedThruForwardAuthUri).length() );
			// :: + String.valueOf(applicationPageHub_Request_ContextPath)

		    }

		    consoleLog.println("B requestedThruForwardAuthUri = "+requestedThruForwardAuthUri);
		    
		    if(String.valueOf(requestedThruForwardAuthUri).indexOf(applicationPageHub_Request_ContextPath) != (-1) ) {
			requestedThruForwardAuthUri = String.valueOf(requestedThruForwardAuthUri).substring(
				String.valueOf(requestedThruForwardAuthUri).indexOf(applicationPageHub_Request_ContextPath)+applicationPageHub_Request_ContextPath.length(),
				String.valueOf(requestedThruForwardAuthUri).length() );
			// + String.valueOf(applicationPageHub_Request_ContextPath)

		    }

		    if(String.valueOf(requestedThruForwardAuthUri).length() <= 2) {

			if(String.valueOf(applicationPageHub_Referer).indexOf(".jsp") != (-1) ) {

			    consoleLog.println("C Got JSP in Request :: requestedThruForwardAuthUri = "+requestedThruForwardAuthUri);
			    requestedThruForwardAuthUri = applicationPageHub_Referer;
			}else {
			    consoleLog.println("C Got NOPE in Request :: let welcome User :: requestedThruForwardAuthUri = "+requestedThruForwardAuthUri);
			    requestedThruForwardAuthUri = (applicationPageHub_Referer == null || String.valueOf(applicationPageHub_Referer).length() <=2 )? null : applicationPageHub_Referer;
			    
			    // :: DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT;
			}

			consoleLog.println("base path redirect requestedThruForwardAuthUri = "+requestedThruForwardAuthUri);

		    }
		    
		    if(requestedThruForwardAuthUri == null ) {
			consoleLog.println(getClass().getName()+" : AUTH : Following user to TOKENIZED auth page 404 ... "+applicationPageHub_Request_ContextPath+"/"+requestedThruForwardAuthUri+"::"+requestedThruForwardAuthUri);
			response.sendError(404, "Impossible de determiner l URI demandee : ");
			return;
		    }else
		    if( (request.getDispatcherType().toString().indexOf("FORWARD") == (-1))
			    && (request.getDispatcherType().toString().indexOf("INCLUDE") == (-1)) 
			    ) {
			consoleLog.println(getClass().getName()+" : AUTH : Following user to TOKENIZED auth page REDIRECT ... "+applicationPageHub_Request_ContextPath+"/"+requestedThruForwardAuthUri+"::"+requestedThruForwardAuthUri);	
			response.sendRedirect(applicationPageHub_Request_ContextPath+"/"+requestedThruForwardAuthUri);
		    }else
			if(request.getDispatcherType().toString().indexOf("INCLUDE") == (-1) ) {
			    consoleLog.println(getClass().getName()+" : AUTH : Following user to TOKENIZED auth page INCLUDE ... "+applicationPageHub_Request_ContextPath+"/"+requestedThruForwardAuthUri+"::"+requestedThruForwardAuthUri);							    
			    response.setContentType("text/html");
			    try {
				RequestDispatcher view = request.getRequestDispatcher(requestedThruForwardAuthUri);
				view.include(request, response);
			    }catch(Exception EV_ERR_PROCESS_INCLUDE) {
				consoleLog.println("EV_ERR_PROCESS_INCLUDE::"+EV_ERR_PROCESS_INCLUDE.getMessage());
				response.sendError(404, URLDecoder.decode(EV_ERR_PROCESS_INCLUDE.getMessage(),"UTF-8"));
				return;
			    }
			}else if(request.getDispatcherType().toString().indexOf("INCLUDE") != (-1) ) {
			    consoleLog.println(getClass().getName()+" : AUTH : NOT sure what to do INCLUDE ... "+applicationPageHub_Request_ContextPath+"/"+requestedThruForwardAuthUri+"::"+requestedThruForwardAuthUri);							    
 
			}else {
			    consoleLog.println(getClass().getName()+" : Followed Auth PATh :: Unsure what to do ... "+request.hashCode());
			}

		    consoleLog.println(getClass().getName()+" : Followed Auth PATh :: DONE ... ");
		}else if( (applicationPageHub_Referer.indexOf( DEFAULT_PAGE_INDEX) >0 )  && (applicationPageHub_Request_HTTP_URI.indexOf(DEFAULT_PAGE_INDEX) >= 0))
		{

		} else if ((applicationPageHub_Request_HTTP_URI.indexOf(DEFAULT_PAGE_INDEX) >= 0) && (applicationPageHub_Request_URI.indexOf(DEFAULT_PAGE_INDEX) <= 0)) {
		    // response.getWriter().append(" Hello : ").append( userLogin );
		    RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
		    view.forward(request, response);
		}else if(applicationPageHub_Referer_Servlet.indexOf(DEFAULT_PAGE_AUTH_CONNEXION) >= 0) {
		    // response.getWriter().append(" Hello : ").append( userLogin );
		    RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_VIEW_MYACCOUNT);
		    view.forward(request, response);
		}else {
		    // do some stuff that progam doesn t really care nor know 
		    // response.getWriter().append(" Hello : ").append( userLogin );
		    response.setContentType("text/html");
		    RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_WELCOME);
		    // view.forward( request, response );


		    //RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_AUTH_PASSED_WELCOME);
		    //view.forward(request, response); 
		} 
	    }else {

		// response.getWriter().append("Access denied ").append( userLogin ).append( userPassword );
		// request.setAttribute("loginMessage", loginMessage .append("Access denied ").append( userLogin ).append( userPassword ); );
		consoleLog.println(getClass().getName()+" : AUTH ("+String.format("%d::%s", response.getContentType(), request.getDispatcherType().toString())+"): Redirecting user to default auth page ... "+DEFAULT_PAGE_INDEX);
		if (userSession != null ){
			userSession.setAttribute("loginMessage", loginMessage ); 
		};
		// response.getWriter().append(" <a href=\""+request.getContextPath()+"\">Veuillez vous authentifier ...</a> : ").append( userToken ); 

		// 
		consoleLog.println(" Actual "+request.getDispatcherType().toString()+" context ... :: "+String.format("%s", request.hashCode())+"::"+applicationPageHub_Referer );
		// if( String.valueOf(applicationPageHub_Referer).length() <= 2 )
		String referepageservlet = String.valueOf(DEFAULT_PAGE_INDEX).toLowerCase().replaceAll(".jsp", "");

		if(String.valueOf(applicationPageHub_Referer).length() >1 && (String.valueOf(applicationPageHub_Referer).indexOf(referepageservlet) != (-1) ) ) {
		    consoleLog.println(" Enter NOTE "+request.getDispatcherType().toString()+"  context ... :: MAYBE get "+DEFAULT_PAGE_INDEX );

		}else
		    if(String.valueOf(applicationPageHub_Referer).indexOf(referepageservlet) == (-1) )
		    {




			// REQUEST
			// INCLUDE
			//  
			if( applicationPageHub_Referer == null 
				&& request.getDispatcherType().toString().indexOf("REQUEST") == (-1)  
				&&  (request.getDispatcherType().toString().indexOf("FORWARD") == (-1))
				){
			    // Action action = ActionFactory.getAction(request);
			    //        String view = action.execute(request, response);
			    consoleLog.println(" OUTPUT "+request.getDispatcherType().toString()+"  context ... :: "+String.format("%s", request.hashCode())+"::"+applicationPageHub_Referer );

			}else if(applicationPageHub_Referer != null 
				&& request.getDispatcherType().toString().indexOf("FORWARD") != (-1) ){
			    RequestDispatcher view = request.getRequestDispatcher("/"+DEFAULT_PAGE_INDEX);
			    consoleLog.println(" CC Enter "+request.getDispatcherType().toString()+"  context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX );
			    view.include(request, response);
			    consoleLog.println(" CC Back to  "+request.getDispatcherType().toString()+" context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX  );

			}else if(applicationPageHub_Referer != null 
				&& request.getDispatcherType().toString().indexOf("INCLUDE") == (-1) ){
			    RequestDispatcher view = request.getRequestDispatcher("/"+DEFAULT_PAGE_INDEX);
			    consoleLog.println(" Enter "+request.getDispatcherType().toString()+"  context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX );
			    //view.forward(request, response);
			    consoleLog.println(" DD Back to  "+request.getDispatcherType().toString()+" context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX  );
			}else if(applicationPageHub_Referer == null 
				&& request.getDispatcherType().toString().indexOf("REQUEST") != (-1) ){

			    RequestDispatcher view = request.getRequestDispatcher("/"+DEFAULT_PAGE_INDEX);
			    consoleLog.println(" CC Enter "+request.getDispatcherType().toString()+"  context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX );
			    view.include(request, response);
			    consoleLog.println(" CC Back to  "+request.getDispatcherType().toString()+" context ... :: "+String.format("%s", view.hashCode())+" :: "+DEFAULT_PAGE_INDEX  );



			}else  {
			    consoleLog.println(" DD OUTPUT something for "+request.getDispatcherType().toString()+" context ... :: "+String.format("%s", request.hashCode()) );
			}
		    }else if(applicationPageHub_Referer == null) {



		    }

		/*
						if( (applicationPageHub_Referer != null) && (applicationPageHub_Referer_Header == null)) {
							consoleLog.println(getClass().getName()+" : AUTH : Following user to NULL Referer page ... "+applicationPageHub_Request_HTTP_URI);
							response.getWriter().append("Followed  :: "+applicationPageHub_Request_HTTP_URI);
							//RequestDispatcher view = request.getRequestDispatcher(applicationPageHub_Referer);
							//view.include(request, response);
						}else*/

		/* if( (String.valueOf(applicationPageHub_Referer).compareTo("/") == 0 ) && (applicationPageHub_Referer_Header == null) ) { //  && (applicationPageHub_Referer.length() >=3)
							consoleLog.println(getClass().getName()+" : AUTH : Redirecting user to ENDPOINT DEEFAULT page ... ("+String.valueOf(applicationPageHub_Referer)+")");
							// RequestDispatcher view = request.getRequestDispatcher(DEFAULT_PAGE_INDEX);
							request.getInputStream();
							view.forward(request, response);

							String outpuResponse = String.valueOf(response.getBufferSize());

							//response.sendRedirect(applicationPageHub_Request_ContextPath+"/"+DEFAULT_PAGE_INDEX);

						}else

							/*if(
								   ( (applicationPageHub_Referer != null) && (applicationPageHub_Referer.indexOf( DEFAULT_PAGE_INDEX) >0 ) )  
								&& (  (applicationPageHub_Referer_Header == null) )
							)
						{
							consoleLog.println(getClass().getName()+" : AUTH : Redirecting user to ENDPOINT page ... (null)");
							response.getWriter().append(" <a href=\""+request.getContextPath()+"\">Veuillez vous authentifier ...</a> : ").append( userToken );
						}else {
							consoleLog.println(getClass().getName()+" : AUTH : Redirecting user to default auth page output ... ");
							// RequestDispatcher view = request.getRequestDispatcher("/"+DEFAULT_PAGE_INDEX);
							// view.notifyAll();
							// view.forward(request, response);
							// response.sendRedirect(applicationPageHub_Request_ContextPath+"/" );
							// throw new Exception("Error interne prevu");
							String outpuResponse = String.valueOf(request.getContentLength());
							ServletResponse responseHandled  = ((request.isAsyncStarted())? request.getAsyncContext().getResponse(): null);
							//view.notify();
						    consoleLog.println(" Flush ... :: "+String.format("%d::%s", request.getContentLength(), String.format("%s", view.hashCode()) ) );
							response.getOutputStream().flush();
							// response.getOutputStream().flush();
							// response.flushBuffer();
							response.getOutputStream().print("Response :: ");
							response.getOutputStream().println(outpuResponse+" :: "+response.toString());
							// response.getOutputStream().flush();

						}*/
	    }



	} catch (IOException EV_ERR_DOHANDLE_IOREQUEST) {
	    consoleLog.println("EV_ERR_DOHANDLE_IOREQUEST::"+EV_ERR_DOHANDLE_IOREQUEST.getMessage());

	} catch (ServletException EV_ERR_DOHANDLE_ServletException_REQUEST) {
	    // TODO Auto-generated catch block
	    // consoleLog.println("EV_ERR_DOHANDLE_REQUEST::"+EV_ERR_DOHANDLE_ServletException_REQUEST.getMessage());
	    // EV_ERR_DOHANDLE_ServletException_REQUEST.printStackTrace();

	    response.sendError(500, EV_ERR_DOHANDLE_ServletException_REQUEST.getCause() +" :: "+EV_ERR_DOHANDLE_ServletException_REQUEST.getMessage());
	    consoleLog.println("EV_ERR_DOHANDLE_REQUEST::FATAL:: "+EV_ERR_DOHANDLE_ServletException_REQUEST.getCause() +" :: "+EV_ERR_DOHANDLE_ServletException_REQUEST.getMessage() +"\n:: EXIT ... ("+applicationPageHub_Request_URI+")");
	    //					view.forward(request, response);
	}catch (Exception EV_ERR_DOHANDLE_REQUEST) {
	    consoleLog.println("EV_ERR_DOHANDLE_REQUEST::"+EV_ERR_DOHANDLE_REQUEST.getMessage());
	    response.sendError(500, EV_ERR_DOHANDLE_REQUEST.getCause() +" :: "+EV_ERR_DOHANDLE_REQUEST.getMessage());
	} 



    }
    /* ** 
     * @Args
     * 
     */



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
	    consoleLog.println(getClass().getName() +"::"+getClass().getEnclosingMethod() +":: Error :: "+EV_ERR_AUTH_EXCEPTION.getMessage());
	    EV_ERR_AUTH_EXCEPTION.printStackTrace();
	    throw new Exception(EV_ERR_AUTH_EXCEPTION);
	    // return false;
	} 

	// if no current Session ... let s create one ...
	if (userSession != null ) { 
	    userSession.setAttribute(AUTH_TOKEN_KEY, "");
	}

	return false;

    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
	    throws IOException, ServletException {
	// TODO Auto-generated method stub
	consoleLog.println("doFilter : "+arg0.getParameterNames().toString());

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
	// TODO Auto-generated method stub
	consoleLog.println("Init Filter : "+arg0.getInitParameterNames().toString());

    }

}
