package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ECodePart;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Component_part_footerJava
{
  protected static String nl;
  public static synchronized Component_part_footerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Component_part_footerJava result = new Component_part_footerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "                if(log.is";
  protected final String TEXT_3 = "Enabled())";
  protected final String TEXT_4 = NL + "            log.";
  protected final String TEXT_5 = "(\"";
  protected final String TEXT_6 = " - \" ";
  protected final String TEXT_7 = " + (";
  protected final String TEXT_8 = ") ";
  protected final String TEXT_9 = ");";
  protected final String TEXT_10 = NL + "            if (log.isDebugEnabled()) {" + NL + "                class BytesLimit65535_";
  protected final String TEXT_11 = "{" + NL + "                    public void limitLog4jByte() throws Exception{" + NL + "                    StringBuilder ";
  protected final String TEXT_12 = " = new StringBuilder();";
  protected final String TEXT_13 = NL + "                    ";
  protected final String TEXT_14 = ".append(\"Parameters:\");";
  protected final String TEXT_15 = NL + "                            ";
  protected final String TEXT_16 = ".append(\"";
  protected final String TEXT_17 = "\" + \" = \" + String.valueOf(";
  protected final String TEXT_18 = ").substring(0, 4) + \"...\");     ";
  protected final String TEXT_19 = "\" + \" = \" + ";
  protected final String TEXT_20 = NL + "                        ";
  protected final String TEXT_21 = ".append(\" | \");";
  protected final String TEXT_22 = NL + "                    } " + NL + "                } " + NL + "            new BytesLimit65535_";
  protected final String TEXT_23 = "().limitLog4jByte();" + NL + "            }";
  protected final String TEXT_24 = NL + "            StringBuilder ";
  protected final String TEXT_25 = " = new StringBuilder();    ";
  protected final String TEXT_26 = ".append(";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = NL + "                    if(";
  protected final String TEXT_29 = " == null){";
  protected final String TEXT_30 = ".append(\"<null>\");" + NL + "                    }else{";
  protected final String TEXT_31 = ");" + NL + "                    }   ";
  protected final String TEXT_32 = NL + "                ";
  protected final String TEXT_33 = ".append(\"|\");";
  protected final String TEXT_34 = NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_35 = NL + "\t\tif(execStat){" + NL + "\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_37 = "\"+iterateId,2, 0);" + NL + "\t\t\t";
  protected final String TEXT_38 = NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_39 = NL + "\t\tif(enableLogStash){" + NL + "\t\t\t";
  protected final String TEXT_40 = NL + "\t\t\t\trunStat.logStatOnConnection(\"";
  protected final String TEXT_41 = NL + "\t";
  protected final String TEXT_42 = NL + "\t\t\t";
  protected final String TEXT_43 = NL + "\t\t\tif(execStat){" + NL + "\t\t\t\tif(resourceMap.get(\"inIterateVComp\") == null || !((Boolean)resourceMap.get(\"inIterateVComp\"))){" + NL + "\t \t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_44 = "\"+iterateId,2, 0);" + NL + "\t\t\t \t}" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_45 = NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_46 = NL + "\t\t\tif(enableLogStash){" + NL + "\t\t\t\tif(resourceMap.get(\"inIterateVComp\") == null || !((Boolean)resourceMap.get(\"inIterateVComp\"))){" + NL + "\t\t\t \t\t";
  protected final String TEXT_47 = NL + "\t\t\t \t\tRunStat.StatBean talend_statebean = runStat.logStatOnConnection(\"";
  protected final String TEXT_48 = "\"+iterateId,2, 0);" + NL + "\t\t\t \t\t" + NL + "\t\t\t \t\t";
  protected final String TEXT_49 = ".addConnectionMessage(" + NL + "\t\t\t\t\t    \"";
  protected final String TEXT_50 = "\", " + NL + "\t\t\t\t\t    \"";
  protected final String TEXT_51 = "\", " + NL + "\t\t\t\t\t    false," + NL + "\t\t\t\t\t    \"";
  protected final String TEXT_52 = "\"," + NL + "\t\t\t\t\t    \"";
  protected final String TEXT_53 = "\"," + NL + "\t\t\t\t\t    talend_statebean.getNbLine()," + NL + "\t\t\t\t\t    talend_statebean.getStartTime()," + NL + "\t\t\t\t\t    talend_statebean.getEndTime()" + NL + "\t\t\t\t\t);" + NL + "\t\t\t\t\t" + NL + "\t\t\t \t\t";
  protected final String TEXT_54 = "\", " + NL + "\t\t\t\t\t    true," + NL + "\t\t\t\t\t    \"input\"," + NL + "\t\t\t\t\t    \"";
  protected final String TEXT_55 = "\"," + NL + "\t\t\t\t\t    talend_statebean.getNbLine()," + NL + "\t\t\t\t\t    talend_statebean.getStartTime()," + NL + "\t\t\t\t\t    talend_statebean.getEndTime()" + NL + "\t\t\t\t\t);" + NL + "\t\t\t\t\t";
  protected final String TEXT_56 = "Process(globalMap);" + NL + "\t\t\t\t\t";
  protected final String TEXT_57 = NL + "\t\t\t \t}" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_58 = NL + "\t\t";
  protected final String TEXT_59 = NL + " ";
  protected final String TEXT_60 = NL + "     ";
  protected final String TEXT_61 = " = ";
  protected final String TEXT_62 = ";";
  protected final String TEXT_63 = NL + "    globalMap.put(\"USE_CONDITION\",Boolean.TRUE);" + NL + "    globalMap.put(\"TRACE_CONDITION\", Boolean.FALSE);";
  protected final String TEXT_64 = NL + "  " + NL + "  if (";
  protected final String TEXT_65 = " !=null) {" + NL + "  globalMap.put(\"ENABLE_TRACES_CONNECTION_";
  protected final String TEXT_66 = "\",Boolean.TRUE);" + NL + "  if (runTrace.isPause()) {" + NL + "   while (runTrace.isPause()) {" + NL + "   \tThread.sleep(100);" + NL + "   }" + NL + "  }\t else {" + NL + "  \t" + NL + "   \t// here we dump the line content for trace purpose" + NL + "   \tjava.util.LinkedHashMap<String, String> runTraceData = new java.util.LinkedHashMap<String,String>();" + NL + "\t";
  protected final String TEXT_67 = NL + "\t\trunTraceData.put(\"";
  protected final String TEXT_68 = "\", String.valueOf(";
  protected final String TEXT_69 = "));" + NL + "\t";
  protected final String TEXT_70 = NL + "   \trunTrace.sendTrace(\"";
  protected final String TEXT_71 = "\",\"";
  protected final String TEXT_72 = "\", runTraceData);" + NL + "\t}" + NL + "\t";
  protected final String TEXT_73 = "     \t\t" + NL + "       if (";
  protected final String TEXT_74 = NL + "           ";
  protected final String TEXT_75 = "(";
  protected final String TEXT_76 = "    ";
  protected final String TEXT_77 = NL + "              ";
  protected final String TEXT_78 = NL + "\t              ";
  protected final String TEXT_79 = NL + "                  ";
  protected final String TEXT_80 = "  ";
  protected final String TEXT_81 = NL + "             )";
  protected final String TEXT_82 = NL + "         ";
  protected final String TEXT_83 = NL + "        true";
  protected final String TEXT_84 = "      " + NL + "    ){" + NL + "    \tglobalMap.put(\"TRACE_CONDITION\", Boolean.TRUE);" + NL + "    }";
  protected final String TEXT_85 = NL + "  }";
  protected final String TEXT_86 = NL + NL + "ok_Hash.put(\"";
  protected final String TEXT_87 = "\", true);" + NL + "end_Hash.put(\"";
  protected final String TEXT_88 = "\", System.currentTimeMillis());" + NL;
  protected final String TEXT_89 = NL;
  protected final String TEXT_90 = ".addMessage(\"end\",\"";
  protected final String TEXT_91 = "\", end_Hash.get(\"";
  protected final String TEXT_92 = "\")-start_Hash.get(\"";
  protected final String TEXT_93 = "\"));";
  protected final String TEXT_94 = "Process(globalMap);";
  protected final String TEXT_95 = NL + "\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_96 = "\", 0, \"ok\");" + NL + "\t\t\t\t}";
  protected final String TEXT_97 = NL + "\t\t\t\t";
  protected final String TEXT_98 = NL + "   \t\t\tif (";
  protected final String TEXT_99 = ") {" + NL + "   \t\t\t\t";
  protected final String TEXT_100 = NL + "\t\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_101 = "\", 0, \"true\");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_102 = NL + "    \t\t\t";
  protected final String TEXT_103 = "Process(globalMap);" + NL + "   \t\t\t}" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_104 = "   " + NL + "   \t\t\t\telse{" + NL + "\t\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_105 = "\", 0, \"false\");" + NL + "\t\t\t\t\t}   \t " + NL + "   \t\t\t\t}";
  protected final String TEXT_106 = NL + "\t\t\tmtp_";
  protected final String TEXT_107 = ".waitForEndOfQueue();" + NL + "\t\t\t" + NL + "\t\t\tTalendThread errorThread_";
  protected final String TEXT_108 = " = mtp_";
  protected final String TEXT_109 = ".getErrorThread();" + NL;
  protected final String TEXT_110 = NL + "\tif(errorThread_";
  protected final String TEXT_111 = " != null) {" + NL + "\t\tif (errorThread_";
  protected final String TEXT_112 = ".errorCode != null) {" + NL + "\t\t\tif (errorCode == null" + NL + "\t\t\t\t\t|| errorThread_";
  protected final String TEXT_113 = ".errorCode.compareTo(errorCode) > 0) {" + NL + "\t\t\t\terrorCode = errorThread_";
  protected final String TEXT_114 = ".errorCode;" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!status.equals(\"failure\")) {" + NL + "\t\t\tstatus = errorThread_";
  protected final String TEXT_115 = ".status;" + NL + "\t\t}" + NL + "\t\tif(errorThread_";
  protected final String TEXT_116 = ".exception!=null){" + NL + "\t\t\tthrow errorThread_";
  protected final String TEXT_117 = ".exception;" + NL + "\t\t}" + NL + "\t\tif(errorThread_";
  protected final String TEXT_118 = ".error!=null){" + NL + "\t\t\tthrow errorThread_";
  protected final String TEXT_119 = ".error;" + NL + "\t\t}" + NL + "\t}else{\t\t\t\t" + NL + "\t\tInteger threadErrorCode = mtp_";
  protected final String TEXT_120 = ".getTalendThreadResult().getErrorCode();" + NL + "\t\tString threadStatus = mtp_";
  protected final String TEXT_121 = ".getTalendThreadResult().getStatus();" + NL + "\t\t" + NL + "\t\tif (threadErrorCode != null) {" + NL + "\t\t\tif (errorCode == null" + NL + "\t\t\t\t\t|| threadErrorCode.compareTo(errorCode) > 0) {" + NL + "\t\t\t\terrorCode = threadErrorCode;" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!status.equals(\"failure\")) {" + NL + "\t\t\tstatus = threadStatus;" + NL + "\t\t}" + NL + "\t }\t\t\t";
  protected final String TEXT_122 = " != null) {" + NL + "\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal.get()).get(\"errorCode\"));" + NL + "\t\tString localStatus = (String) (((java.util.Map) threadLocal.get()).get(\"status\"));" + NL + "\t\t" + NL + "\t\tif (errorThread_";
  protected final String TEXT_123 = ".errorCode != null) {" + NL + "\t\t\tif (localErrorCode == null || errorThread_";
  protected final String TEXT_124 = ".errorCode.compareTo(localErrorCode) > 0) {" + NL + "\t\t\t\t((java.util.Map) threadLocal.get()).put(\"errorCode\", errorThread_";
  protected final String TEXT_125 = ".errorCode);" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!localStatus.equals(\"failure\")) {" + NL + "\t\t\t((java.util.Map) threadLocal.get()).put(\"status\", errorThread_";
  protected final String TEXT_126 = ".status);" + NL + "\t\t}" + NL + "\t\tif(errorThread_";
  protected final String TEXT_127 = ".exception;" + NL + "\t\t}\t\t" + NL + "\t}else{" + NL + "\t\tInteger threadErrorCode = mtp_";
  protected final String TEXT_128 = ".getTalendThreadResult().getStatus();" + NL + "\t\t" + NL + "\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal" + NL + "\t\t\t\t.get()).get(\"errorCode\"));" + NL + "\t\tString localStatus = (String) (((java.util.Map) threadLocal" + NL + "\t\t\t\t.get()).get(\"status\"));" + NL + "" + NL + "\t\tif (threadErrorCode != null) {" + NL + "\t\t\tif (localErrorCode == null" + NL + "\t\t\t\t\t|| threadErrorCode.compareTo(localErrorCode) > 0) {" + NL + "\t\t\t\t((java.util.Map) threadLocal.get()).put(\"errorCode\"," + NL + "\t\t\t\t\t\tthreadErrorCode);" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!localStatus.equals(\"failure\")) {" + NL + "\t\t\t((java.util.Map) threadLocal.get()).put(\"status\"," + NL + "\t\t\t\t\tthreadStatus);" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_129 = NL + "\ttos_count_";
  protected final String TEXT_130 = "++;";
  protected final String TEXT_131 = NL + NL + "/**" + NL + " * [";
  protected final String TEXT_132 = " ";
  protected final String TEXT_133 = " ] stop" + NL + " */";

    // add the list of the connection names to avoid to declare two times the same name.
    public String createPrivateClassInstance(INode node, String parentLastRoot, boolean force) {
    	return createPrivateClassInstance(node, parentLastRoot, force, new HashSet<String>());
    }

    public String createPrivateClassInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + " = new " + conn.getName()
	                            + "Struct();\n";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + " = " + parentLastRoot + ";\n";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPriveClassMethodDeclaration(INode n, String parentLastRoot, boolean force, Set<String> names) {
    	String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPrivateClassMethodInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force) || (n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                        parentLastRoot = conn.getName();
                    }
                } else {
                    // copy
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                    }
                }
                if (!conn.getTarget().getComponent().useMerge()) {
                    toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)
                    || conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
                toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
		 
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
class BasicLogUtil{
    protected String cid  = "";
    protected org.talend.core.model.process.INode node = null;
    protected boolean log4jEnabled = false;
    private String logID = "";
    
    private BasicLogUtil(){}
    
    public BasicLogUtil(org.talend.core.model.process.INode node){
        this.node = node;
        String cidx = this.node.getUniqueName();
        if(cidx.matches("^.*?tAmazonAuroraOutput_\\d+_out$") || cidx.matches("^.*?tDBOutput_\\d+_out$")){
             cidx = cidx.substring(0,cidx.length()-4);// 4 ==> "_out".length();
        }
        this.cid = cidx;
        this.log4jEnabled = ("true").equals(org.talend.core.model.process.ElementParameterParser.getValue(this.node.getProcess(), "__LOG4J_ACTIVATE__"));
        this.log4jEnabled = this.log4jEnabled &&
                            this.node.getComponent().isLog4JEnabled() && !"JOBLET".equals(node.getComponent().getComponentType().toString());
        this.logID = this.cid;
    }
    
    public String var(String varName){
        return varName + "_" + this.cid;
    }
    public String str(String content){
        return "\"" + content + "\"";
    }
    
    public void info(String... message){
        log4j("info", message);
    }
    
    public void debug(String... message){
        log4j("debug", message);
    }
    
    public void warn(String... message){
        log4j("warn", message);
    }
    
    public void error(String... message){
        log4j("error", message);
    }
    
    public void fatal(String... message){
        log4j("fatal", message);
    }
    
    public void trace(String... message){
        log4j("trace", message);
    }
    java.util.List<String> checkableList = java.util.Arrays.asList(new String[]{"info", "debug", "trace"});     
    public void log4j(String level, String... messages){
        if(this.log4jEnabled){
            if(checkableList.contains(level)){
            
    stringBuffer.append(TEXT_2);
    stringBuffer.append(level.substring(0, 1).toUpperCase() + level.substring(1));
    stringBuffer.append(TEXT_3);
    
            }
            
    stringBuffer.append(TEXT_4);
    stringBuffer.append(level);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(logID);
    stringBuffer.append(TEXT_6);
    for(String message : messages){
    stringBuffer.append(TEXT_7);
    stringBuffer.append(message);
    stringBuffer.append(TEXT_8);
    }
    stringBuffer.append(TEXT_9);
    
        }
    }
    
    public boolean isActive(){
        return this.log4jEnabled;
    }
}

class LogUtil extends BasicLogUtil{
    
    private LogUtil(){
    }
    
    public LogUtil(org.talend.core.model.process.INode node){
        super(node);
    }
    
    public void startWork(){
        debug(str("Start to work."));
    }
    
    public void endWork(){
        debug(str("Done."));
    }
    
    public void logIgnoredException(String exception){
        warn(exception);
    }
    
    public void logPrintedException(String exception){
        error(exception);
    }
    
    public void logException(String exception){
        fatal(exception);
    }
    
    public void logCompSetting(){
    
    
        if(log4jEnabled){
        
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(var("log4jParamters"));
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(var("log4jParamters"));
    stringBuffer.append(TEXT_14);
    
                    java.util.Set<org.talend.core.model.process.EParameterFieldType> ignoredParamsTypes = new java.util.HashSet<org.talend.core.model.process.EParameterFieldType>(); 
                    ignoredParamsTypes.addAll(
                        java.util.Arrays.asList(
                            org.talend.core.model.process.EParameterFieldType.SCHEMA_TYPE,
                            org.talend.core.model.process.EParameterFieldType.LABEL,
                            org.talend.core.model.process.EParameterFieldType.EXTERNAL,
                            org.talend.core.model.process.EParameterFieldType.MAPPING_TYPE,
                            org.talend.core.model.process.EParameterFieldType.IMAGE,
                            org.talend.core.model.process.EParameterFieldType.TNS_EDITOR,
                            org.talend.core.model.process.EParameterFieldType.WSDL2JAVA,
                            org.talend.core.model.process.EParameterFieldType.GENERATEGRAMMARCONTROLLER,
                            org.talend.core.model.process.EParameterFieldType.GENERATE_SURVIVORSHIP_RULES_CONTROLLER,
                            org.talend.core.model.process.EParameterFieldType.REFRESH_REPORTS,
                            org.talend.core.model.process.EParameterFieldType.BROWSE_REPORTS,
                            org.talend.core.model.process.EParameterFieldType.PALO_DIM_SELECTION,
                            org.talend.core.model.process.EParameterFieldType.GUESS_SCHEMA,
                            org.talend.core.model.process.EParameterFieldType.MATCH_RULE_IMEX_CONTROLLER,
                            org.talend.core.model.process.EParameterFieldType.MEMO_PERL,
                            org.talend.core.model.process.EParameterFieldType.DBTYPE_LIST,
                            org.talend.core.model.process.EParameterFieldType.VERSION,
                            org.talend.core.model.process.EParameterFieldType.TECHNICAL,
                            org.talend.core.model.process.EParameterFieldType.ICON_SELECTION,
                            org.talend.core.model.process.EParameterFieldType.JAVA_COMMAND,
                            org.talend.core.model.process.EParameterFieldType.TREE_TABLE,
                            org.talend.core.model.process.EParameterFieldType.VALIDATION_RULE_TYPE,
                            org.talend.core.model.process.EParameterFieldType.DCSCHEMA,
                            org.talend.core.model.process.EParameterFieldType.SURVIVOR_RELATION,
                            org.talend.core.model.process.EParameterFieldType.REST_RESPONSE_SCHEMA_TYPE
                            )
                    );
                    for(org.talend.core.model.process.IElementParameter ep : org.talend.core.model.utils.NodeUtil.getDisplayedParameters(node)){
                        if(!ep.isLog4JEnabled() || ignoredParamsTypes.contains(ep.getFieldType())){
                            continue;
                        }
                        String name = ep.getName();
                        if(org.talend.core.model.process.EParameterFieldType.PASSWORD.equals(ep.getFieldType())){
                            String epName = "__" + name + "__";
                            String password = "";
                            if(org.talend.core.model.process.ElementParameterParser.canEncrypt(node, epName)){
                                password = org.talend.core.model.process.ElementParameterParser.getEncryptedValue(node, epName);
                            }else{
                                String passwordValue = org.talend.core.model.process.ElementParameterParser.getValue(node, epName);
                                if (passwordValue == null || "".equals(passwordValue.trim())) {// for the value which empty
                                    passwordValue = "\"\"";
                                } 
                                password = "routines.system.PasswordEncryptUtil.encryptPassword(" + passwordValue + ")";
                            } 
                            
    stringBuffer.append(TEXT_15);
    stringBuffer.append(var("log4jParamters"));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_18);
    
                        }else{
                            String value = org.talend.core.model.utils.NodeUtil.getNormalizeParameterValue(node, ep);
                            if(value.length()>10000){
                            value = org.talend.core.model.utils.NodeUtil.replaceCRLFInMEMO_SQL(value);
                            }
                            
    stringBuffer.append(TEXT_15);
    stringBuffer.append(var("log4jParamters"));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_9);
    
                        }   
                        
    stringBuffer.append(TEXT_20);
    stringBuffer.append(var("log4jParamters"));
    stringBuffer.append(TEXT_21);
    
                    }
                    debug(var("log4jParamters")); 
                    
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
        } 
        
    
    } 
    
    //no use for now, because we log the data by rowStruct
    public void traceData(String rowStruct, java.util.List<org.talend.core.model.metadata.IMetadataColumn> columnList, String nbline){
        if(log4jEnabled){
        
    stringBuffer.append(TEXT_24);
    stringBuffer.append(var("log4jSb"));
    stringBuffer.append(TEXT_25);
    
            for(org.talend.core.model.metadata.IMetadataColumn column : columnList){
                org.talend.core.model.metadata.types.JavaType javaType = org.talend.core.model.metadata.types.JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                String columnName = column.getLabel();
                boolean isPrimit = org.talend.core.model.metadata.types.JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable());
                if(isPrimit){
                
    stringBuffer.append(TEXT_13);
    stringBuffer.append(var("log4jSb"));
    stringBuffer.append(TEXT_26);
    stringBuffer.append(rowStruct);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_9);
    
                }else{
                
    stringBuffer.append(TEXT_28);
    stringBuffer.append(rowStruct);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(var("log4jSb"));
    stringBuffer.append(TEXT_30);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(var("log4jSb"));
    stringBuffer.append(TEXT_26);
    stringBuffer.append(rowStruct);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_31);
    
                }
                
    stringBuffer.append(TEXT_32);
    stringBuffer.append(var("log4jSb"));
    stringBuffer.append(TEXT_33);
    
            }
        }
        trace(str("Content of the record "), nbline, str(": "), var("log4jSb"));
        
    
    }
}

class LogHelper{
    
    java.util.Map<String, String> pastDict = null;
    
    public LogHelper(){
        pastDict = new java.util.HashMap<String, String>();
        pastDict.put("insert", "inserted");
        pastDict.put("update", "updated");
        pastDict.put("delete", "deleted");
        pastDict.put("upsert", "upserted");
    }   
    
    public String upperFirstChar(String data){ 
        return data.substring(0, 1).toUpperCase() + data.substring(1);
    }
    
    public String toPastTense(String data){
        return pastDict.get(data);
    }
}
LogHelper logHelper = new LogHelper();

LogUtil log = null;

    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
log = new LogUtil(node);
IProcess process = node.getProcess();
String startNodeCid=node.getDesignSubjobStartNode().getUniqueName();
 
NodesSubTree subTree = (NodesSubTree) codeGenArgument.getSubTree();
ECodePart codePart = codeGenArgument.getCodePart();
boolean trace = codeGenArgument.isTrace();
boolean stat = codeGenArgument.isStatistics();

boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();

Set<IConnection> connSet =  new HashSet<IConnection>();
connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MAIN));
connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MERGE));

Set<IConnection> iterateConnSet =  new HashSet<IConnection>();
iterateConnSet.addAll(node.getOutgoingConnections(EConnectionType.ITERATE));

List<IConnection> allSubProcessConnection = codeGenArgument.getAllMainSubTreeConnections();

String cid = node.getUniqueName();

List<? extends INode> jobCatcherNodes = process.getNodesOfType("tJobStructureCatcher");
boolean enableLogStash = jobCatcherNodes != null && !jobCatcherNodes.isEmpty();
boolean logstashCurrent = !cid.startsWith("tJobStructureCatcher") && !cid.startsWith("talend") && enableLogStash;

if((codePart.equals(ECodePart.END))&&(stat || logstashCurrent)){
	boolean iterateInVFComp = (node.getVirtualLinkTo() != null && node.getVirtualLinkTo() == EConnectionType.ITERATE);
	if(iterateInVFComp){
		List<String> needToEndConnNames = new ArrayList<String>();
		INode nextNode = node.getOutgoingConnections(EConnectionType.ITERATE).get(0).getTarget();
		NodeUtil.fillConnectionsForStat(needToEndConnNames, nextNode);
		
    stringBuffer.append(TEXT_34);
    if(stat) {
    stringBuffer.append(TEXT_35);
    
			for(String connName : needToEndConnNames){
			
    stringBuffer.append(TEXT_36);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_37);
    
			}
			
    stringBuffer.append(TEXT_38);
    }
    stringBuffer.append(TEXT_34);
    if(logstashCurrent) {//now only finish the log, not send, TODO
    stringBuffer.append(TEXT_39);
    
			for(String connName : needToEndConnNames){
			
    stringBuffer.append(TEXT_40);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_37);
    
			}
			
    stringBuffer.append(TEXT_38);
    
		}
		
    stringBuffer.append(TEXT_41);
    
	}
	if(connSet.size()>0){
	  	for(IConnection con:connSet){
	  		INode source = con.getSource();
			String sourceNodeId = source.getUniqueName();
			String sourceNodeComponent = source.getComponent().getName();
		
    stringBuffer.append(TEXT_42);
    if(stat) {
    stringBuffer.append(TEXT_43);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_44);
    }
    stringBuffer.append(TEXT_45);
    if(logstashCurrent) {
    stringBuffer.append(TEXT_46);
    
		 			for (INode jobStructureCatcher : jobCatcherNodes) {
		 			
    stringBuffer.append(TEXT_47);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_48);
    stringBuffer.append(jobStructureCatcher.getUniqueName() );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(sourceNodeId);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(sourceNodeComponent);
    stringBuffer.append(TEXT_51);
    stringBuffer.append("REJECT".equals(con.getConnectorName()) ? "reject" : "output");
    stringBuffer.append(TEXT_52);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(jobStructureCatcher.getUniqueName() );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(node.getUniqueName());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(node.getComponent().getName());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(jobStructureCatcher.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_56);
    
						break;
					}
					
    stringBuffer.append(TEXT_57);
    }
    stringBuffer.append(TEXT_58);
    
	  	}
	}
}

    stringBuffer.append(TEXT_59);
    

 if (codePart.equals(ECodePart.MAIN)) {
 //================================TDI-17183 start========================================
/**
	if the node is autopropagate the schema, then the output row should evaluate the input row
	before the track code part(TDI-17183)
*/
  String inputColName = null;
  if (node.getIncomingConnections()!=null) {
   for (IConnection incomingConn : node.getIncomingConnections()) {
    if (incomingConn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
     inputColName = incomingConn.getName();
     break;
    }
   }
  }
  List<IMetadataTable> metadatas = node.getMetadataList();
  if ((!node.isSubProcessStart())&&(NodeUtil.isDataAutoPropagated(node))) {
   if (inputColName!=null) {
    if ((metadatas != null) && (metadatas.size() > 0)) {
     IMetadataTable outputMetadata = metadatas.get(0);
     if (outputMetadata != null) {
      for (IConnection outputConnection : node.getOutgoingConnections()) {
       if (outputConnection.getLineStyle().equals(EConnectionType.FLOW_MAIN) || outputConnection.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
       
       	//void the case: row1.username=row1.username
       	if(!outputConnection.getName().equals(inputColName)){//111
			//void the case: row1Struct row1 = new row1Struct(); row1Struct row3 = row1;
            //for (IMetadataColumn column : outputMetadata.getListColumns()) {
    
    stringBuffer.append(TEXT_60);
    stringBuffer.append(outputConnection.getName() );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(inputColName );
    stringBuffer.append(TEXT_62);
     
           	//}
       	}//111
       	
       }
      }
     }
    }
   }
  }
//======================================TDI-17183 end=====================================
  boolean traceCodeGenerated = false;     
  for (IConnection conn : node.getOutgoingConnections()) {
     boolean use_reaceenble = ("true").equals(ElementParameterParser.getValue(conn, "__TRACES_CONNECTION_ENABLE__"));
  	if(node.isSubProcessStart() && node.isSubProcessContainTraceBreakpoint() && !traceCodeGenerated) {
  	  traceCodeGenerated = true;
  
    stringBuffer.append(TEXT_63);
    
    }
   IMetadataTable metadata = conn.getMetadataTable();
   if (metadata!=null) {
    if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) ||
        conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
     if (trace && use_reaceenble) { 
  
    stringBuffer.append(TEXT_64);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(startNodeCid);
    stringBuffer.append(TEXT_66);
    
	for(IMetadataColumn column : metadata.getListColumns()){
	
    stringBuffer.append(TEXT_67);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_68);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_27);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_69);
    
    }
	
    stringBuffer.append(TEXT_70);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_71);
    stringBuffer.append(startNodeCid);
    stringBuffer.append(TEXT_72);
    
	    
     boolean use_breakout = ("true").equals(ElementParameterParser.getValue(conn, "__ACTIVEBREAKPOINT__"));
     boolean use_advanced = ("true").equals(ElementParameterParser.getValue(conn, "__USE_ADVANCED__"));
      List<Map<String, String>> keyColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue( conn,  "__CONDITIONS__");
   		String advancedCondition = ElementParameterParser.getValue(conn, "__ADVANCED_COND__");
     String logical = ElementParameterParser.getValue(conn,"__LOGICAL_OP__");
      if (metadata != null) {
      if(use_breakout && use_reaceenble){
    stringBuffer.append(TEXT_73);
     
      if ((keyColumns!=null &&keyColumns.size() > 0)|| use_advanced) { //111
        int ii = 0;

        for (Map<String, String> keyColumn : keyColumns){ // 222 -> 
          ii++;
          
    stringBuffer.append(TEXT_74);
    stringBuffer.append( ii == 1 ? "" : logical );
    stringBuffer.append(TEXT_75);
     
            if (!("").equals(keyColumn.get("FUNCTION"))) {
            
    stringBuffer.append(TEXT_76);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(keyColumn.get("FUNCTION").replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
            }else {
              IMetadataColumn rightColumn = metadata.getColumn(keyColumn.get("INPUT_COLUMN"));
              JavaType javaType = JavaTypesManager.getJavaTypeFromId(rightColumn.getTalendType());

              if (!javaType.isPrimitive()) {
                //this is only for bug:8133, when "Oject" type, and "Empty" function, and compare with "null"
                if (keyColumn.get("RVALUE") != null && keyColumn.get("RVALUE").equals("null")){
	            
    stringBuffer.append(TEXT_78);
    stringBuffer.append("$source $operator $target".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    }else{
    stringBuffer.append(TEXT_79);
    stringBuffer.append("$source == null? false : $source.compareTo($target) $operator 0".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
	            }
              } else {
              
    stringBuffer.append(TEXT_80);
    stringBuffer.append(TEXT_32);
    stringBuffer.append("$source $operator $target".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
	          }
            }
            
            
    stringBuffer.append(TEXT_81);
    
        } // <- 222
        
    stringBuffer.append(TEXT_82);
    stringBuffer.append(use_advanced? (keyColumns.size() < 1 ? "" : logical) + advancedCondition.replace("input_row", conn.getName()) : "");
     
      } else { //111 
      
    stringBuffer.append(TEXT_83);
    
      } //111
      
    stringBuffer.append(TEXT_84);
    
       }

    stringBuffer.append(TEXT_85);
    
      }
     }
    }
   }
  }
  
 }
 if (codePart.equals(ECodePart.END)) {
 log.endWork();

    stringBuffer.append(TEXT_86);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_88);
    
  String statCatcher = ElementParameterParser.getValue(node,"__TSTATCATCHER_STATS__");
  if ((node.getProcess().getNodesOfType("tStatCatcher").size() > 0) & (statCatcher.equals("true"))) {
   for (INode statCatcherNode : node.getProcess().getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_89);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_94);
    
   }
  }
  
  for (IConnection outgoingConn : node.getOutgoingConnections()) {
  	if (outgoingConn.getTarget().isActivate()) {
		
	         
  	    if (outgoingConn.getLineStyle().equals(EConnectionType.ON_COMPONENT_OK)) { 
			//send the ok status to socket
			if(stat){

    stringBuffer.append(TEXT_95);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_96);
    
			}
			
			
			/*
			This boolean is created for the ON_COMPONENT_OK virtual components. 
			This technic is used on the tWriteXMLField for example. Since the sub method is called in the Thread, we do not have to generate again in the component footer.
			*/
			boolean generateMethodCall = false; 
			if((!node.isVirtualGenerateNode())) {
				generateMethodCall = true;
			} else {
				if(node.getVirtualLinkTo()==null ||  node.getVirtualLinkTo()!=EConnectionType.ON_COMPONENT_OK) {
					generateMethodCall = true;
				}
				// Else, that means the component is a ON_COMPONENT_OK virtual component typed.
			}
			
			if(generateMethodCall) {

    stringBuffer.append(TEXT_97);
    stringBuffer.append(outgoingConn.getTarget().getUniqueName() );
    stringBuffer.append(TEXT_94);
      
			}
  	    }        
       

   		if (outgoingConn.getLineStyle().equals(EConnectionType.RUN_IF)) {
		
    stringBuffer.append(TEXT_98);
    stringBuffer.append(outgoingConn.getCondition() );
    stringBuffer.append(TEXT_99);
    //send the true status to socket
				if(stat){
				
    stringBuffer.append(TEXT_100);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_101);
    }
    stringBuffer.append(TEXT_102);
    stringBuffer.append(outgoingConn.getTarget().getUniqueName() );
    stringBuffer.append(TEXT_103);
    
			//send the false status to socket
			if(stat){
			
    stringBuffer.append(TEXT_104);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_105);
    
			}
   		}
   	}
  }
 }

    stringBuffer.append(TEXT_89);
     
	//This part in order to feedback with the iterate_subprocess_header.javajet and iterate_subprocess_footer.javajet
	
	boolean parallelIterate = false;
	for (IConnection iterateConn : iterateConnSet) { 
		parallelIterate = "true".equals(ElementParameterParser.getValue(iterateConn, "__ENABLE_PARALLEL__"));
		if (parallelIterate) {
			if (codePart.equals(ECodePart.END)) {
			String iterateTargetNodeName = iterateConn.getTarget().getUniqueName();				

    stringBuffer.append(TEXT_106);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_109);
    if(!isRunInMultiThread){
    stringBuffer.append(TEXT_110);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_121);
    }else{
    stringBuffer.append(TEXT_110);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_128);
    }
    stringBuffer.append(TEXT_45);
    
			}
			continue;
		}
	}

    stringBuffer.append(TEXT_89);
    if(codePart.equals(ECodePart.MAIN)){ 
    stringBuffer.append(TEXT_129);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_130);
    }
    stringBuffer.append(TEXT_131);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(codePart );
    stringBuffer.append(TEXT_133);
    return stringBuffer.toString();
  }
}