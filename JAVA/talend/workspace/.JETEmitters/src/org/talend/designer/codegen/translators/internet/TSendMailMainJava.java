package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;

public class TSendMailMainJava
{
  protected static String nl;
  public static synchronized TSendMailMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSendMailMainJava result = new TSendMailMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\tlog.debug(\"";
  protected final String TEXT_2 = " - Retrieving records from the datasource.\");" + NL + "\t\t\t";
  protected final String TEXT_3 = NL + "\t\t\t\tlog.debug(\"";
  protected final String TEXT_4 = " - Retrieved records count: \"+ nb_line_";
  protected final String TEXT_5 = " + \" .\");" + NL + "\t\t\t";
  protected final String TEXT_6 = " - Retrieved records count: \"+ globalMap.get(\"";
  protected final String TEXT_7 = "_NB_LINE\") + \" .\");" + NL + "\t\t\t";
  protected final String TEXT_8 = " - Written records count: \" + nb_line_";
  protected final String TEXT_9 = NL + "\t\t\t\tfinal StringBuffer log4jSb_";
  protected final String TEXT_10 = " = new StringBuffer();" + NL + "\t\t\t";
  protected final String TEXT_11 = " - Retrieving the record \" + (nb_line_";
  protected final String TEXT_12 = ") + \".\");" + NL + "\t\t\t";
  protected final String TEXT_13 = " - Writing the record \" + nb_line_";
  protected final String TEXT_14 = " + \" to the file.\");" + NL + "\t\t\t";
  protected final String TEXT_15 = " - Processing the record \" + nb_line_";
  protected final String TEXT_16 = " + \".\");" + NL + "\t\t\t";
  protected final String TEXT_17 = " - Processed records count: \" + nb_line_";
  protected final String TEXT_18 = NL + " ";
  protected final String TEXT_19 = NL + NL + "\tString smtpHost_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";";
  protected final String TEXT_22 = NL + "        String smtpPort_";
  protected final String TEXT_23 = " = \"";
  protected final String TEXT_24 = "\";";
  protected final String TEXT_25 = NL + "\tString from_";
  protected final String TEXT_26 = " = (";
  protected final String TEXT_27 = ");" + NL + "    String to_";
  protected final String TEXT_28 = ").replace(\";\",\",\");" + NL + "    String cc_";
  protected final String TEXT_29 = " = ((";
  protected final String TEXT_30 = ")==null || \"\".equals(";
  protected final String TEXT_31 = "))?null:(";
  protected final String TEXT_32 = ").replace(\";\",\",\");" + NL + "    String bcc_";
  protected final String TEXT_33 = ").replace(\";\",\",\");" + NL + "    String subject_";
  protected final String TEXT_34 = ");" + NL + "    " + NL + "\tjava.util.List<java.util.Map<String, String>> headers_";
  protected final String TEXT_35 = " = new java.util.ArrayList<java.util.Map<String,String>>();";
  protected final String TEXT_36 = NL + "\t\tjava.util.Map<String,String> headerMap_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = " = new java.util.HashMap<String,String>();" + NL + "\t\theaderMap_";
  protected final String TEXT_39 = ".put(\"KEY\", ";
  protected final String TEXT_40 = ");" + NL + "\t\theaderMap_";
  protected final String TEXT_41 = ".put(\"VALUE\", ";
  protected final String TEXT_42 = ");" + NL + "\t\theaders_";
  protected final String TEXT_43 = ".add(headerMap_";
  protected final String TEXT_44 = ");\t";
  protected final String TEXT_45 = NL + "\tjava.util.List<String> attachments_";
  protected final String TEXT_46 = " = new java.util.ArrayList<String>();" + NL + "\tjava.util.List<String> contentTransferEncoding_";
  protected final String TEXT_47 = " = new java.util.ArrayList<String>();";
  protected final String TEXT_48 = NL + "\t\tattachments_";
  protected final String TEXT_49 = ".add(";
  protected final String TEXT_50 = ");" + NL + "\t\tcontentTransferEncoding_";
  protected final String TEXT_51 = ".add(\"";
  protected final String TEXT_52 = "\");\t";
  protected final String TEXT_53 = NL + NL + "\tString message_";
  protected final String TEXT_54 = ") == null || \"\".equals(";
  protected final String TEXT_55 = ")) ? \"\\\"\\\"\" : (";
  protected final String TEXT_56 = ") ;" + NL + "\tjava.util.Properties props_";
  protected final String TEXT_57 = " = System.getProperties();     " + NL + "\tprops_";
  protected final String TEXT_58 = ".put(\"mail.smtp.host\", smtpHost_";
  protected final String TEXT_59 = ");" + NL + "\tprops_";
  protected final String TEXT_60 = ".put(\"mail.smtp.port\", smtpPort_";
  protected final String TEXT_61 = ");";
  protected final String TEXT_62 = NL + "\t\tprops_";
  protected final String TEXT_63 = ".put(\"mail.smtp.localhost\", ";
  protected final String TEXT_64 = ".put(\"mail.mime.encodefilename\", \"true\");";
  protected final String TEXT_65 = ".put(\"mail.smtp.starttls.enable\",\"true\");";
  protected final String TEXT_66 = "  " + NL + "\t\tprops_";
  protected final String TEXT_67 = ".put(\"mail.smtp.socketFactory.class\", \"javax.net.ssl.SSLSocketFactory\");" + NL + "\t\tprops_";
  protected final String TEXT_68 = ".put(\"mail.smtp.socketFactory.fallback\", \"false\");" + NL + "\t\tprops_";
  protected final String TEXT_69 = ".put(\"mail.smtp.socketFactory.port\", smtpPort_";
  protected final String TEXT_70 = "     " + NL + "\ttry {" + NL + "\t\t";
  protected final String TEXT_71 = NL + "\t\t\tlog.info(\"";
  protected final String TEXT_72 = " - Connection attempt to '\" + smtpHost_";
  protected final String TEXT_73 = " +\"'.\");" + NL + "\t\t";
  protected final String TEXT_74 = NL + "\t\t";
  protected final String TEXT_75 = "  " + NL + "\t\t\tprops_";
  protected final String TEXT_76 = ".put(\"mail.smtp.auth\", \"false\");" + NL + "\t\t\tjavax.mail.Session session_";
  protected final String TEXT_77 = " = javax.mail.Session.getInstance(props_";
  protected final String TEXT_78 = ", null);    " + NL + "\t\t";
  protected final String TEXT_79 = NL + "\t\t\tprops_";
  protected final String TEXT_80 = ".put(\"mail.smtp.auth\", \"true\");" + NL + "\t\t\tjavax.mail.Session session_";
  protected final String TEXT_81 = ", new javax.mail.Authenticator(){         " + NL + "\t\t\t\tprotected javax.mail.PasswordAuthentication getPasswordAuthentication() {" + NL + "\t\t\t\t";
  protected final String TEXT_82 = NL + "                ";
  protected final String TEXT_83 = " " + NL + "\tfinal String decryptedPassword_";
  protected final String TEXT_84 = " = routines.system.PasswordEncryptUtil.decryptPassword(";
  protected final String TEXT_85 = NL + "\tfinal String decryptedPassword_";
  protected final String TEXT_86 = "; ";
  protected final String TEXT_87 = NL + "\t\t\t\t" + NL + "\t\t\t\t" + NL + "\t\t\t\treturn new javax.mail.PasswordAuthentication(";
  protected final String TEXT_88 = ", decryptedPassword_";
  protected final String TEXT_89 = "); " + NL + "\t\t\t\t}         " + NL + "\t\t\t});   " + NL + "\t\t";
  protected final String TEXT_90 = " - Connection to '\" + smtpHost_";
  protected final String TEXT_91 = " + \"' has succeeded.\");" + NL + "\t\t";
  protected final String TEXT_92 = NL + "\t\tjavax.mail.Message msg_";
  protected final String TEXT_93 = " = new javax.mail.internet.MimeMessage(session_";
  protected final String TEXT_94 = ");" + NL + "\t\tmsg_";
  protected final String TEXT_95 = ".setFrom(new javax.mail.internet.InternetAddress(from_";
  protected final String TEXT_96 = ", ";
  protected final String TEXT_97 = "));" + NL + "\t\tmsg_";
  protected final String TEXT_98 = ".setRecipients(javax.mail.Message.RecipientType.TO,javax.mail.internet.InternetAddress.parse(to_";
  protected final String TEXT_99 = ", false));" + NL + "\t\tif (cc_";
  protected final String TEXT_100 = " != null) msg_";
  protected final String TEXT_101 = ".setRecipients(javax.mail.Message.RecipientType.CC, javax.mail.internet.InternetAddress.parse(cc_";
  protected final String TEXT_102 = ", false));" + NL + "\t\tif (bcc_";
  protected final String TEXT_103 = ".setRecipients(javax.mail.Message.RecipientType.BCC, javax.mail.internet.InternetAddress.parse(bcc_";
  protected final String TEXT_104 = ", false));" + NL + "\t\tmsg_";
  protected final String TEXT_105 = ".setSubject(subject_";
  protected final String TEXT_106 = ");" + NL + "" + NL + "\t\tfor (int i_";
  protected final String TEXT_107 = " = 0; i_";
  protected final String TEXT_108 = " < headers_";
  protected final String TEXT_109 = ".size(); i_";
  protected final String TEXT_110 = "++) {" + NL + "\t\t\tjava.util.Map<String, String> header_";
  protected final String TEXT_111 = " = headers_";
  protected final String TEXT_112 = ".get(i_";
  protected final String TEXT_113 = ");" + NL + "\t\t\tmsg_";
  protected final String TEXT_114 = ".setHeader(header_";
  protected final String TEXT_115 = ".get(\"KEY\"), header_";
  protected final String TEXT_116 = ".get(\"VALUE\"));    " + NL + "\t\t}  " + NL + "\t\tmsg_";
  protected final String TEXT_117 = ".setSentDate(new Date());" + NL + "\t\tmsg_";
  protected final String TEXT_118 = ".setHeader(\"X-Priority\", \"";
  protected final String TEXT_119 = "\"); //High->1 Normal->3 Low->5" + NL + "\t\tjavax.mail.Multipart mp_";
  protected final String TEXT_120 = " = new javax.mail.internet.MimeMultipart();" + NL + "\t\tjavax.mail.internet.MimeBodyPart mbpText_";
  protected final String TEXT_121 = " = new javax.mail.internet.MimeBodyPart();" + NL + "\t\tmbpText_";
  protected final String TEXT_122 = ".setText(message_";
  protected final String TEXT_123 = ",";
  protected final String TEXT_124 = ", \"";
  protected final String TEXT_125 = "\");" + NL + "\t\tmp_";
  protected final String TEXT_126 = ".addBodyPart(mbpText_";
  protected final String TEXT_127 = ");" + NL + "  " + NL + "\t\tjavax.mail.internet.MimeBodyPart mbpFile_";
  protected final String TEXT_128 = " = null;" + NL + "" + NL + "\t\tfor (int i_";
  protected final String TEXT_129 = " < attachments_";
  protected final String TEXT_130 = "++){" + NL + "\t\t\tString filename_";
  protected final String TEXT_131 = " = attachments_";
  protected final String TEXT_132 = ");" + NL + "\t\t\tjavax.activation.FileDataSource fds_";
  protected final String TEXT_133 = " = null;" + NL + "\t\t\tjava.io.File file_";
  protected final String TEXT_134 = " = new java.io.File(filename_";
  protected final String TEXT_135 = ");" + NL + "\t\t\t";
  protected final String TEXT_136 = NL + "\t\t\t\tif (!file_";
  protected final String TEXT_137 = ".exists()){" + NL + "\t\t\t\t\tcontinue;" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_138 = NL + "    \t\tif (file_";
  protected final String TEXT_139 = ".isDirectory()){" + NL + "\t\t\t\tjava.io.File[] subFiles_";
  protected final String TEXT_140 = " = file_";
  protected final String TEXT_141 = ".listFiles();" + NL + "\t\t\t\tfor(java.io.File subFile_";
  protected final String TEXT_142 = " : subFiles_";
  protected final String TEXT_143 = "){" + NL + "\t\t\t\t\tif (subFile_";
  protected final String TEXT_144 = ".isFile()){" + NL + "\t\t\t\t\t\tfds_";
  protected final String TEXT_145 = " = new javax.activation.FileDataSource(subFile_";
  protected final String TEXT_146 = ".getAbsolutePath());" + NL + "\t\t\t\t\t\tmbpFile_";
  protected final String TEXT_147 = " = new javax.mail.internet.MimeBodyPart();" + NL + "\t\t\t\t\t\tmbpFile_";
  protected final String TEXT_148 = ".setDataHandler(new javax.activation.DataHandler(fds_";
  protected final String TEXT_149 = "));" + NL + "\t\t\t\t\t\tmbpFile_";
  protected final String TEXT_150 = ".setFileName(javax.mail.internet.MimeUtility.encodeText(fds_";
  protected final String TEXT_151 = ".getName()));" + NL + "\t\t\t\t\t\tif(contentTransferEncoding_";
  protected final String TEXT_152 = ").equalsIgnoreCase(\"base64\")){" + NL + "\t\t\t\t\t\t\tmbpFile_";
  protected final String TEXT_153 = ".setHeader(\"Content-Transfer-Encoding\", \"base64\");" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tmp_";
  protected final String TEXT_154 = ".addBodyPart(mbpFile_";
  protected final String TEXT_155 = ");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "    \t\t}else{" + NL + "\t\t\t\tmbpFile_";
  protected final String TEXT_156 = " = new javax.mail.internet.MimeBodyPart();" + NL + "\t\t\t\tfds_";
  protected final String TEXT_157 = " = new javax.activation.FileDataSource(filename_";
  protected final String TEXT_158 = ");" + NL + "\t\t\t\tmbpFile_";
  protected final String TEXT_159 = ")); " + NL + "\t\t\t\tmbpFile_";
  protected final String TEXT_160 = ".getName()));" + NL + "\t\t\t\tif(contentTransferEncoding_";
  protected final String TEXT_161 = ").equalsIgnoreCase(\"base64\")){" + NL + "\t\t\t\t\tmbpFile_";
  protected final String TEXT_162 = ".setHeader(\"Content-Transfer-Encoding\", \"base64\");" + NL + "\t\t\t\t}" + NL + "\t\t\t\tmp_";
  protected final String TEXT_163 = ");" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t// -- set the content --" + NL + "\t\tmsg_";
  protected final String TEXT_164 = ".setContent(mp_";
  protected final String TEXT_165 = ");" + NL + "\t\t// add handlers for main MIME types" + NL + "\t\tjavax.activation.MailcapCommandMap mc_";
  protected final String TEXT_166 = " = ( javax.activation.MailcapCommandMap)javax.activation.CommandMap.getDefaultCommandMap();" + NL + "\t\tmc_";
  protected final String TEXT_167 = ".addMailcap(\"text/html;; x-java-content-handler=com.sun.mail.handlers.text_html\");" + NL + "\t\tmc_";
  protected final String TEXT_168 = ".addMailcap(\"text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml\");" + NL + "\t\tmc_";
  protected final String TEXT_169 = ".addMailcap(\"text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain\");" + NL + "\t\tmc_";
  protected final String TEXT_170 = ".addMailcap(\"multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed\");" + NL + "\t\tmc_";
  protected final String TEXT_171 = ".addMailcap(\"message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822\");" + NL + "\t\tjavax.activation.CommandMap.setDefaultCommandMap(mc_";
  protected final String TEXT_172 = ");" + NL + "\t\t// -- Send the message --" + NL + "\t\tjavax.mail.Transport.send(msg_";
  protected final String TEXT_173 = ");" + NL + "\t} catch(java.lang.Exception e){" + NL + "  \t\t";
  protected final String TEXT_174 = NL + "\t\t\tthrow(e);" + NL + "\t\t";
  protected final String TEXT_175 = NL + "\t\t\t";
  protected final String TEXT_176 = NL + "\t\t\t\tlog.error(\"";
  protected final String TEXT_177 = " - \" + e.toString());" + NL + "\t\t\t";
  protected final String TEXT_178 = NL + "  \t\t\tSystem.err.println(e.toString());" + NL + "\t\t";
  protected final String TEXT_179 = NL + "\t}finally{" + NL + "\t\tprops_";
  protected final String TEXT_180 = ".remove(\"mail.smtp.host\");" + NL + "\t\tprops_";
  protected final String TEXT_181 = ".remove(\"mail.smtp.port\");" + NL + "\t\t";
  protected final String TEXT_182 = ".remove(\"mail.smtp.localhost\");" + NL + "\t\t";
  protected final String TEXT_183 = ".remove(\"mail.mime.encodefilename\");" + NL + "\t\t";
  protected final String TEXT_184 = ".remove(\"mail.smtp.starttls.enable\");" + NL + "\t\t";
  protected final String TEXT_185 = ".remove(\"mail.smtp.socketFactory.class\");" + NL + "\t\t\tprops_";
  protected final String TEXT_186 = ".remove(\"mail.smtp.socketFactory.fallback\");" + NL + "\t\t\tprops_";
  protected final String TEXT_187 = ".remove(\"mail.smtp.socketFactory.port\");" + NL + "\t\t";
  protected final String TEXT_188 = ".remove(\"mail.smtp.auth\");     " + NL + "\t}";
  protected final String TEXT_189 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//this util class use by set log4j debug paramters
	class DefaultLog4jFileUtil {
	
		INode node = null;
	    String cid = null;
 		boolean isLog4jEnabled = false;
 		String label = null;
 		
 		public DefaultLog4jFileUtil(){
 		}
 		public DefaultLog4jFileUtil(INode node) {
 			this.node = node;
 			this.cid = node.getUniqueName();
 			this.label = cid;
			this.isLog4jEnabled = ("true").equals(org.talend.core.model.process.ElementParameterParser.getValue(node.getProcess(), "__LOG4J_ACTIVATE__"));
 		}
 		
 		public void setCid(String cid) {
 			this.cid = cid;
 		}
 		
		//for all tFileinput* components 
		public void startRetriveDataInfo() {
			if (isLog4jEnabled) {
			
    stringBuffer.append(TEXT_1);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_2);
    
			}
		}
		
		public void retrievedDataNumberInfo() {
			if (isLog4jEnabled) {
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
			}
		}
		
		public void retrievedDataNumberInfoFromGlobalMap(INode node) {
			if (isLog4jEnabled) {
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
			}
		}
		
		//for all tFileinput* components 
		public void retrievedDataNumberInfo(INode node) {
			if (isLog4jEnabled) {
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
			}
		}
		
		public void writeDataFinishInfo(INode node) {
			if(isLog4jEnabled){
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
			}
		}
		
 		//TODO delete it and remove all log4jSb parameter from components
		public void componentStartInfo(INode node) {
			if (isLog4jEnabled) {
			
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
			}
		}
		
		//TODO rename or delete it
		public void debugRetriveData(INode node,boolean hasIncreased) {
			if(isLog4jEnabled){
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(hasIncreased?"":"+1");
    stringBuffer.append(TEXT_12);
    
			}
		}
		
		//TODO rename or delete it
		public void debugRetriveData(INode node) {
			debugRetriveData(node,true);
		}
		
		//TODO rename or delete it
		public void debugWriteData(INode node) {
			if(isLog4jEnabled){
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
			}
		}
		
		public void logCurrentRowNumberInfo() {
			if(isLog4jEnabled){
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
			}
		}
		
		public void logDataCountInfo() {
			if(isLog4jEnabled){
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
			}
		}
	}
	
	final DefaultLog4jFileUtil log4jFileUtil = new DefaultLog4jFileUtil((INode)(((org.talend.designer.codegen.config.CodeGeneratorArgument)argument).getArgument()));
	
    stringBuffer.append(TEXT_18);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String needAuth = ElementParameterParser.getValue(node, "__NEED_AUTH__");
String authUsername = ElementParameterParser.getValue(node, "__AUTH_USERNAME__");
String authPassword = ElementParameterParser.getValue(node, "__AUTH_PASSWORD__");
String message = org.talend.core.model.utils.NodeUtil.replaceCRLFInMEMO_SQL(ElementParameterParser.getValue(node, "__MESSAGE__"));
String importance = ElementParameterParser.getValue(node, "__IMPORTANCE__");
String priority = "Normal".equals(importance) ? "3" : (("High").equals(importance) ? "1" : "5");
boolean ifSSL = "true".equals(ElementParameterParser.getValue(node, "__SSL__"));
boolean startTLS = "true".equals(ElementParameterParser.getValue(node, "__STARTTLS__"));
boolean checkAttachementOnSend = "true".equals(ElementParameterParser.getValue(node, "__CHECK_ATTACHMENT__"));
boolean needPersonalName = "true".equals(ElementParameterParser.getValue(node, "__NEED_PERSONAL_NAME__"));
String personalName = needPersonalName ? ElementParameterParser.getValue(node, "__PERSONAL_NAME__") : null;
String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
String mimesubtype = ElementParameterParser.getValue(node, "__TEXT_SUBTYPE__");  
boolean setLocalhost = "true".equals(ElementParameterParser.getValue(node, "__SET_LOCALHOST__"));  
String localhost = ElementParameterParser.getValue(node, "__LOCALHOST__");  
boolean isDieOnError = "true".equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));
boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(node.getProcess(), "__LOG4J_ACTIVATE__"));
	//Bug12952, move all begin part to here below
	String smtpHost    = ElementParameterParser.getValue(node, "__SMTP_HOST__");
	String smtpPort    = ElementParameterParser.getValue(node, "__SMTP_PORT__");
	String to          = ElementParameterParser.getValue(node, "__TO__");
	String from        = ElementParameterParser.getValue(node, "__FROM__");
	String cc          = ElementParameterParser.getValue(node, "__CC__");
	String bcc          = ElementParameterParser.getValue(node, "__BCC__");
	String subject     = ElementParameterParser.getValue(node, "__SUBJECT__");
	
	List<Map<String, String>> tableValues = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ATTACHMENTS__");
	List<Map<String, String>> headers = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HEADERS__");
	
	// component id
	to = to.replace(";",",");

	//in order to be compatible with TOS 2.4.1, @see:issue:6282
	if(cc == null || cc.trim().length()==0){
		cc = "\"\"";
	}
	if(bcc == null || bcc.trim().length()==0){
		bcc = "\"\"";
	}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(smtpHost );
    stringBuffer.append(TEXT_21);
    
    try {
        int port = Integer.parseInt(smtpPort);

    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(smtpPort );
    stringBuffer.append(TEXT_24);
    
    } catch (java.lang.Exception e) {

    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(smtpPort );
    stringBuffer.append(TEXT_21);
    
    }

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(from );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(to );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(subject );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    
	for (int i = 0; i < headers.size(); i++) {
   		Map<String, String> header = headers.get(i);

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(header.get("KEY"));
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(header.get("VALUE"));
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_44);
    
	}

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    
	for (int i = 0; i < tableValues.size(); i++) {
    	Map<String,String> attachment = tableValues.get(i);

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(attachment.get("FILE") );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(attachment.get("CONTENT_TRANSFER_ENCODING") );
    stringBuffer.append(TEXT_52);
    
	}
	//end of the former begin part

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
     
	if (setLocalhost){
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(localhost);
    stringBuffer.append(TEXT_61);
     }
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
     
	if (startTLS){
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    
	}
	if (ifSSL){

    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    
	}

    stringBuffer.append(TEXT_70);
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    }
    stringBuffer.append(TEXT_74);
    if ("false".equals(needAuth)) { 
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    } else{
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    
                String passwordFieldName = "__AUTH_PASSWORD__";
                
    stringBuffer.append(TEXT_82);
    if (ElementParameterParser.canEncrypt(node, passwordFieldName)) {
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(ElementParameterParser.getEncryptedValue(node, passwordFieldName));
    stringBuffer.append(TEXT_61);
    } else {
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append( ElementParameterParser.getValue(node, passwordFieldName));
    stringBuffer.append(TEXT_86);
    }
    stringBuffer.append(TEXT_87);
    stringBuffer.append(authUsername );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    }
    stringBuffer.append(TEXT_74);
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    }
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(personalName);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(priority );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(mimesubtype );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_135);
    if (!checkAttachementOnSend) {
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    }
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    if (isDieOnError){
    stringBuffer.append(TEXT_174);
    }else{
    stringBuffer.append(TEXT_175);
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    }
    stringBuffer.append(TEXT_178);
    }
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
     
		if (setLocalhost){
		
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
     
		}
		
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
     
			if (startTLS){
		
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    
		}
		if (ifSSL){
		
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    
		}
		
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(TEXT_189);
    return stringBuffer.toString();
  }
}
