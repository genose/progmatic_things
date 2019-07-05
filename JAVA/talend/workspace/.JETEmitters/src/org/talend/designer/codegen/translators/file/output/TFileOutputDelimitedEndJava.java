package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.process.AbstractNode;

public class TFileOutputDelimitedEndJava
{
  protected static String nl;
  public static synchronized TFileOutputDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputDelimitedEndJava result = new TFileOutputDelimitedEndJava();
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
  protected final String TEXT_18 = NL;
  protected final String TEXT_19 = NL;
  protected final String TEXT_20 = NL + "\t\t";
  protected final String TEXT_21 = NL + "\t   \t\tsynchronized (multiThreadLockWrite) {" + NL + "\t\t";
  protected final String TEXT_22 = NL + "\t\t\tsynchronized ((Object[])globalMap.get(\"lockWrite_";
  protected final String TEXT_23 = "\")) {" + NL + "\t\t";
  protected final String TEXT_24 = NL + "\t\t\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\t\t\tsynchronized (pLockWrite) {" + NL + "\t\t";
  protected final String TEXT_25 = NL + "\t\t\t";
  protected final String TEXT_26 = NL + "\t\t\t\t\tif(out";
  protected final String TEXT_27 = "!=null) {" + NL + "\t\t\t\t\t\tout";
  protected final String TEXT_28 = ".flush();" + NL + "\t\t\t\t\t\tout";
  protected final String TEXT_29 = ".close();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_30 = ".flush();" + NL + "\t\t\t\t\t\twriter_";
  protected final String TEXT_31 = " = null;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_32 = NL + "\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_33 = "_NB_LINE\",nb_line_";
  protected final String TEXT_34 = ");" + NL + "\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_35 = "_FILE_NAME\",fileName_";
  protected final String TEXT_36 = ");" + NL + "\t\t\t";
  protected final String TEXT_37 = NL + "\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_38 = NL + "\t\t\t\t\tif(CsvWriter";
  protected final String TEXT_39 = "!=null) {" + NL + "\t\t\t\t    \tCsvWriter";
  protected final String TEXT_40 = ".close();" + NL + "\t\t\t\t    }" + NL + "\t\t\t\t\t";
  protected final String TEXT_41 = NL + "\t\t\t\t\t\tif(out";
  protected final String TEXT_42 = "!=null) {" + NL + "\t\t\t\t\t\t\tout";
  protected final String TEXT_43 = ".flush();" + NL + "\t\t\t\t\t\t\tout";
  protected final String TEXT_44 = ".close();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_45 = NL + "\t\t\t\t\t\tif(CsvWriter";
  protected final String TEXT_46 = "!=null) {" + NL + "\t\t\t\t\t\t\tCsvWriter";
  protected final String TEXT_47 = ".close();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif(out";
  protected final String TEXT_48 = ".flush();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif(outWriter_";
  protected final String TEXT_49 = "!=null) {" + NL + "\t\t\t\t\t\t\toutWriter_";
  protected final String TEXT_50 = ".flush();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_51 = ".flush();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif(bufferWriter_";
  protected final String TEXT_52 = "!=null) {" + NL + "\t\t\t\t\t\t\tbufferWriter_";
  protected final String TEXT_53 = ".flush();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tCsvWriter";
  protected final String TEXT_54 = " = null;" + NL + "\t\t\t\t\t";
  protected final String TEXT_55 = NL + "\t\t    \tglobalMap.put(\"";
  protected final String TEXT_56 = NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_57 = NL + "\t\t\tif(isFileGenerated_";
  protected final String TEXT_58 = " && nb_line_";
  protected final String TEXT_59 = " == 0){" + NL + "\t\t\t\t";
  protected final String TEXT_60 = NL + "\t\t\t\t\tfile_";
  protected final String TEXT_61 = ".delete();" + NL + "\t\t\t\t";
  protected final String TEXT_62 = NL + "\t\t\t\t\tfile";
  protected final String TEXT_63 = NL + "\t\t\t}\t\t" + NL + "\t\t";
  protected final String TEXT_64 = NL + "\t\tresourceMap.put(\"finish_";
  protected final String TEXT_65 = "\", true);" + NL + "\t";

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
    boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
    boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));
    
    String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__")); 
	boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
	
	boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));
	
	String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
	boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;
	
	boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));
	IProcess process = node.getProcess();
	boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(process, "__LOG4J_ACTIVATE__"));

    stringBuffer.append(TEXT_19);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
	
    stringBuffer.append(TEXT_20);
     
	    if(codeGenArgument.getIsRunInMultiThread()){
		
    stringBuffer.append(TEXT_21);
    
		}
		if (((AbstractNode)node).getParallelIterator() != null) {
		
    stringBuffer.append(TEXT_22);
    stringBuffer.append(((AbstractNode)node).getParallelIterator());
    stringBuffer.append(TEXT_23);
     
		}
		if (isParallelize) {
		
    stringBuffer.append(TEXT_24);
     
		}
		
    stringBuffer.append(TEXT_25);
    
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(!useStream){
				
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    
				}else{
				
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
				}
				
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}else{//the following is the tFileOutputCSV component
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
    stringBuffer.append(TEXT_37);
    
				if(!useStream){
				
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
					if(isInRowMode){
					
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
					}
				}else{
					if(isInRowMode){
					
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    
					}else{
					
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    
					}
				}
				
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
			}
			
    stringBuffer.append(TEXT_20);
     
		if (isParallelize) {
		
    stringBuffer.append(TEXT_56);
    
		}
		if (((AbstractNode)node).getParallelIterator() != null) {
		
    stringBuffer.append(TEXT_56);
     
		}
		if(codeGenArgument.getIsRunInMultiThread()){
		
    stringBuffer.append(TEXT_56);
    
		}
		
    stringBuffer.append(TEXT_20);
    if(!useStream && isDeleteEmptyFile){
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    if(compress && !isAppend && !split){
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    }else{
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    }
    stringBuffer.append(TEXT_63);
    }
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
	}
}

    
log4jFileUtil.writeDataFinishInfo(node);

    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
