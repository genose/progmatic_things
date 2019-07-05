package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;
import org.talend.core.model.process.IConnectionCategory;

public class TMemorizeRowsMainJava
{
  protected static String nl;
  public static synchronized TMemorizeRowsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMemorizeRowsMainJava result = new TMemorizeRowsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    for (int i_";
  protected final String TEXT_2 = " = iRows_";
  protected final String TEXT_3 = " - 1 ; i_";
  protected final String TEXT_4 = " > 0; i_";
  protected final String TEXT_5 = "--) {";
  protected final String TEXT_6 = NL + "        ";
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = "[i_";
  protected final String TEXT_9 = "] = ";
  protected final String TEXT_10 = " - 1];  ";
  protected final String TEXT_11 = NL + "    }";
  protected final String TEXT_12 = NL + "      ";
  protected final String TEXT_13 = "[0] = ";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ";    ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();

if ((metadatas != null) && (metadatas.size() > 0)) {
  IMetadataTable metadata = metadatas.get(0);
  List<? extends IConnection> connsIn = node.getIncomingConnections();
    	for(IConnection conn:connsIn){//-----111
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//-----222
  IConnection connIn = conn;
  String sFlowName = connIn.getName();
  List<IMetadataColumn> preColumns = connIn.getMetadataTable().getListColumns();
  List<Map<String, String>> listTableCols = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__SPECIFY_COLS__");
  List<String> listCheckedColsName = null;
  
  for (IMetadataColumn column : preColumns){
    String sColumnName = column.getLabel();
    int iInColIndex = preColumns.indexOf(column);
    Map<String, String> checkedColumn = listTableCols.get(iInColIndex);
    boolean bMemorize = "true".equals(checkedColumn.get("MEMORIZE_IT")); 
    
    if (bMemorize){
      if (listCheckedColsName == null){
        listCheckedColsName = new java.util.ArrayList();
      }
      listCheckedColsName.add(sColumnName);
    }
  }
  
  if (listCheckedColsName != null){
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
      for (String columnName : listCheckedColsName){
      
    stringBuffer.append(TEXT_6);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
      }
      
    stringBuffer.append(TEXT_11);
    
    for (String columnName : listCheckedColsName){
    
    stringBuffer.append(TEXT_12);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(sFlowName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_15);
    
    }
  }
  		}//-----222
	}//-----111
}

    return stringBuffer.toString();
  }
}
