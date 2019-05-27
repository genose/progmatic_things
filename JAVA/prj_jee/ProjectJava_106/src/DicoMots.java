/**
 * 
 */

/**
 * @author 59013-36-18
 *
 */
public class DicoMots {

	final String DEFAULT_DICO_LANG = "Francais";
	private String sLangueDico =  DEFAULT_DICO_LANG;

	/**
	 * @param sLangueDico
	 */
	public DicoMots(String sLangueDicoToSet) {
		super();
		this.sLangueDico = ( ( ( sLangueDicoToSet != null ) && (String.valueOf( sLangueDicoToSet ).length() >2) )? sLangueDico : DEFAULT_DICO_LANG );
	}

	public String getLangueDico() {
		return sLangueDico;
	}

	public void setLangueDico(String sLangueDicoToSet) {
		
		this.sLangueDico = ( ( ( sLangueDicoToSet != null ) && (String.valueOf( sLangueDicoToSet ).length() >2) )? sLangueDico : DEFAULT_DICO_LANG );
	}
	
	
	
	
}
