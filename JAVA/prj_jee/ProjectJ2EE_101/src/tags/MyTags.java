/**
 * 
 */
package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author 59013-03-13
 *
 */

public class MyTags extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String login = "";
	private String pwd;
	private String action = "";
	/**
	 * 
	 */
	public MyTags() {
		// TODO Auto-generated constructor stub
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override
	public void release() {
		super.release();
		login = "";
		pwd = "";
		action = null;
	}
	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			
			// prepare an HTML form element an send it to the output buffer
			out.print("<form method=\"POST\" ");
			
			if(action != null && action.length() >0 ) {
				out.print("action='" + action + "' ");
			}
			
			out.println("style='border: 1px solid black; width: 60%; margin: auto;'>");
			
			out.println("<label>Login : </label>");
			out.println("<input name=\"login\" value=\"" + login + "\" autofocus />");
			out.println("<br />");
			
			out.println("<label>Mot de passe : </label>");
			out.println("<input name=\"mdp\" type=\"password\" value=\"" + pwd + "\" />");
			out.println("<br /><br />");
			
			out.println("<input type=\"submit\" value=\"Connecter\" />");
			out.println("<br /><br />");
		} catch(Exception EV_ERR_PRINT_TAGS) {
			EV_ERR_PRINT_TAGS.printStackTrace();
			throw new JspException(EV_ERR_PRINT_TAGS);
		}
		return 2;
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			out.print("</form>");
		} catch(Exception EV_ERR_PRINT_TAGS) {
			EV_ERR_PRINT_TAGS.printStackTrace();
			throw new JspException(EV_ERR_PRINT_TAGS);
		}
		return 4;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
