package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TUniqRowEndJava
{
  protected static String nl;
  public static synchronized TUniqRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TUniqRowEndJava result = new TUniqRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "globalMap.put(\"";
  protected final String TEXT_3 = "_NB_UNIQUES\",nb_uniques_";
  protected final String TEXT_4 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_5 = "_NB_DUPLICATES\",nb_duplicates_";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL + "\tlog.info(\"";
  protected final String TEXT_8 = " - Unique records count: \" + (nb_uniques_";
  protected final String TEXT_9 = ")+\" .\");" + NL + "\tlog.info(\"";
  protected final String TEXT_10 = " - Duplicate records count: \" + (nb_duplicates_";
  protected final String TEXT_11 = ")+\" .\");";
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
final boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(node.getProcess(), "__LOG4J_ACTIVATE__"));


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
if(isLog4jEnabled){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
}

    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
