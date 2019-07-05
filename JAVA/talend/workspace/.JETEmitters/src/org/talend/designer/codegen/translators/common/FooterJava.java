package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;

import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.CorePlugin;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.ProcessUtils;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.EComponentType;

public class FooterJava
{
  protected static String nl;
  public static synchronized FooterJava create(String lineSeparator)
  {
    nl = lineSeparator;
    FooterJava result = new FooterJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    public String resuming_logs_dir_path = null;" + NL + "    public String resuming_checkpoint_path = null;" + NL + "    public String parent_part_launcher = null;" + NL + "    private String resumeEntryMethodName = null;" + NL + "    private boolean globalResumeTicket = false;" + NL + "" + NL + "    public boolean watch = false;" + NL + "    // portStats is null, it means don't execute the statistics" + NL + "    public Integer portStats = null;" + NL + "    public int portTraces = 4334;" + NL + "    public String clientHost;" + NL + "    public String defaultClientHost = \"localhost\";" + NL + "    public String contextStr = \"";
  protected final String TEXT_2 = "\";" + NL + "    public boolean isDefaultContext = true;" + NL + "    public String pid = \"0\";" + NL + "    public String rootPid = null;" + NL + "    public String fatherPid = null;" + NL + "    public String fatherNode = null;" + NL + "    public long startTime = 0;" + NL + "    public boolean isChildJob = false;" + NL + "    public String log4jLevel = \"\";" + NL + "    " + NL + "    private boolean enableLogStash;" + NL + "" + NL + "    private boolean execStat = true;" + NL + "" + NL + "    private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {" + NL + "        protected java.util.Map<String, String> initialValue() {" + NL + "            java.util.Map<String,String> threadRunResultMap = new java.util.HashMap<String, String>();" + NL + "            threadRunResultMap.put(\"errorCode\", null);" + NL + "            threadRunResultMap.put(\"status\", \"\");" + NL + "            return threadRunResultMap;" + NL + "        };" + NL + "    };" + NL;
  protected final String TEXT_3 = NL + "    private SyncInt runningThreadCount =new SyncInt();" + NL + "" + NL + "    private class SyncInt" + NL + "    {" + NL + "        private int count = 0;" + NL + "        public synchronized void add(int i)" + NL + "        {" + NL + "            count +=i;" + NL + "        }" + NL + "" + NL + "        public synchronized int getCount()" + NL + "        {" + NL + "            return count;" + NL + "        }" + NL + "    }";
  protected final String TEXT_4 = NL + NL + "    private PropertiesWithType context_param = new PropertiesWithType();" + NL + "    public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();" + NL + "" + NL + "    public String status= \"\";" + NL + "    ";
  protected final String TEXT_5 = NL + "    private final org.talend.components.common.runtime.SharedConnectionsPool connectionPool = new org.talend.components.common.runtime.SharedConnectionsPool() {" + NL + "    \tpublic java.sql.Connection getDBConnection(String dbDriver, String url, String userName, String password, String dbConnectionName)" + NL + "            throws ClassNotFoundException, java.sql.SQLException {" + NL + "            return SharedDBConnection.getDBConnection(dbDriver, url, userName, password, dbConnectionName);" + NL + "        }" + NL + "" + NL + "    \tpublic java.sql.Connection getDBConnection(String dbDriver, String url, String dbConnectionName)" + NL + "            throws ClassNotFoundException, java.sql.SQLException {" + NL + "            return SharedDBConnection.getDBConnection(dbDriver, url, dbConnectionName);" + NL + "        }" + NL + "    };" + NL + "    " + NL + "    private static final String GLOBAL_CONNECTION_POOL_KEY = \"GLOBAL_CONNECTION_POOL\";" + NL + "    " + NL + "    {" + NL + "    \tglobalMap.put(GLOBAL_CONNECTION_POOL_KEY, connectionPool);" + NL + "    }";
  protected final String TEXT_6 = NL + NL + "    public static void main(String[] args){" + NL + "        final ";
  protected final String TEXT_7 = " ";
  protected final String TEXT_8 = "Class = new ";
  protected final String TEXT_9 = "();" + NL + "" + NL + "        int exitCode = ";
  protected final String TEXT_10 = "Class.runJobInTOS(args);";
  protected final String TEXT_11 = NL + "\t        if(exitCode==0){" + NL + "\t\t        log.info(\"TalendJob: '";
  protected final String TEXT_12 = "' - Done.\");" + NL + "\t        }";
  protected final String TEXT_13 = NL + NL + "        System.exit(exitCode);" + NL + "    }" + NL;
  protected final String TEXT_14 = NL + "      @Test" + NL + "        public void test";
  protected final String TEXT_15 = "() throws java.lang.Exception{";
  protected final String TEXT_16 = NL + "         if(";
  protected final String TEXT_17 = "<=0){" + NL + "            throw new java.lang.Exception(\"There is no tAssert in your test case!\");" + NL + "          }" + NL + "            junitGlobalMap.put(\"tests.log\",new String());" + NL + "            junitGlobalMap.put(\"tests.nbFailure\",new Integer(0));" + NL + "            final ";
  protected final String TEXT_18 = "();" + NL + "            java.util.List<String> paraList_";
  protected final String TEXT_19 = " = new java.util.ArrayList<String>();" + NL + "            paraList_";
  protected final String TEXT_20 = ".add(\"--context=";
  protected final String TEXT_21 = "\");";
  protected final String TEXT_22 = NL + NL + "            String instance_";
  protected final String TEXT_23 = "=null;";
  protected final String TEXT_24 = NL + "                try {" + NL + "                    instance_";
  protected final String TEXT_25 = " = new String(java.util.Base64.getMimeDecoder().decode(\"";
  protected final String TEXT_26 = "\"), utf8Charset);" + NL + "                } catch (java.lang.Exception e) {" + NL + "                    e.printStackTrace();" + NL + "                }" + NL;
  protected final String TEXT_27 = NL + "                java.net.URL url_";
  protected final String TEXT_28 = " = getClass().getResource(\"";
  protected final String TEXT_29 = "\");" + NL + "                instance_";
  protected final String TEXT_30 = "=\"";
  protected final String TEXT_31 = "=\" + java.net.URLDecoder.decode(url_";
  protected final String TEXT_32 = ".getPath(), \"";
  protected final String TEXT_33 = "\");" + NL;
  protected final String TEXT_34 = NL + "            paraList_";
  protected final String TEXT_35 = ".add(\"--context_param\");" + NL + "            paraList_";
  protected final String TEXT_36 = ".add(instance_";
  protected final String TEXT_37 = ");";
  protected final String TEXT_38 = NL + "        String[] arrays = new String[paraList_";
  protected final String TEXT_39 = ".size()];" + NL + "        for(int i=0;i<paraList_";
  protected final String TEXT_40 = ".size();i++){" + NL + "            arrays[i] = (String)paraList_";
  protected final String TEXT_41 = ".get(i);" + NL + "        }";
  protected final String TEXT_42 = NL + "        ";
  protected final String TEXT_43 = "Class.runJobInTOS(arrays);" + NL + "" + NL + "        String errors = (String)junitGlobalMap.get(\"tests.log\");" + NL + "        Integer nbFailure = (Integer)junitGlobalMap.get(\"tests.nbFailure\");" + NL + "        assertTrue(\"Failure=\"+nbFailure+java.lang.System.getProperty(\"line.separator\")+errors, errors.isEmpty());" + NL + "" + NL + "         if(";
  protected final String TEXT_44 = "Class.exception!=null){" + NL + "            throw ";
  protected final String TEXT_45 = "Class.exception;" + NL + "        }" + NL + "        }";
  protected final String TEXT_46 = NL + NL + "    public String[][] runJob(String[] args) {" + NL + "" + NL + "        int exitCode = runJobInTOS(args);";
  protected final String TEXT_47 = NL + "        String[][] bufferValue = (String[][])globalBuffer.toArray(new String[globalBuffer.size()][]);";
  protected final String TEXT_48 = NL + "        String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };";
  protected final String TEXT_49 = NL + NL + "        return bufferValue;" + NL + "    }" + NL + "" + NL + "    public boolean hastBufferOutputComponent() {" + NL + "\t\tboolean hastBufferOutput = false;" + NL + "    \t";
  protected final String TEXT_50 = NL + "        \t\thastBufferOutput = true;" + NL + "    \t";
  protected final String TEXT_51 = NL + "        return hastBufferOutput;" + NL + "    }" + NL + "" + NL + "    public int runJobInTOS(String[] args) {" + NL + "\t   \t// reset status" + NL + "\t   \tstatus = \"\";" + NL + "\t   \t" + NL + "        String lastStr = \"\";" + NL + "        for (String arg : args) {" + NL + "            if (arg.equalsIgnoreCase(\"--context_param\")) {" + NL + "                lastStr = arg;" + NL + "            } else if (lastStr.equals(\"\")) {" + NL + "                evalParam(arg);" + NL + "            } else {" + NL + "                evalParam(lastStr + \" \" + arg);" + NL + "                lastStr = \"\";" + NL + "            }" + NL + "        }" + NL;
  protected final String TEXT_52 = NL + "\t        if(!\"\".equals(log4jLevel)){" + NL + "\t\t\t\tif(\"trace\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.TRACE);" + NL + "\t\t\t\t}else if(\"debug\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.DEBUG);" + NL + "\t\t\t\t}else if(\"info\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.INFO);" + NL + "\t\t\t\t}else if(\"warn\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.WARN);" + NL + "\t\t\t\t}else if(\"error\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.ERROR);" + NL + "\t\t\t\t}else if(\"fatal\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.FATAL);" + NL + "\t\t\t\t}else if (\"off\".equalsIgnoreCase(log4jLevel)){" + NL + "\t\t\t\t\tlog.setLevel(org.apache.log4j.Level.OFF);" + NL + "\t\t\t\t}" + NL + "\t\t\t\torg.apache.log4j.Logger.getRootLogger().setLevel(log.getLevel());" + NL + "    \t    }" + NL + "        \tlog.info(\"TalendJob: '";
  protected final String TEXT_53 = "' - Start.\");" + NL + "    \t";
  protected final String TEXT_54 = NL + "    \t" + NL + "    \t";
  protected final String TEXT_55 = NL + "\t\t\tif(enableLogStash) {" + NL + "\t\t\t\tjava.util.Properties properties_";
  protected final String TEXT_56 = " = new java.util.Properties();" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_57 = ".setProperty(\"root.logger\", \"audit\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_58 = ".setProperty(\"encoding\", \"UTF-8\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_59 = ".setProperty(\"application.name\", \"Talend Studio\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_60 = ".setProperty(\"service.name\", \"Talend Studio Job\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_61 = ".setProperty(\"instance.name\", \"Talend Studio Job Instance\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_62 = ".setProperty(\"propagate.appender.exceptions\", \"none\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_63 = ".setProperty(\"log.appender\", \"file\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_64 = ".setProperty(\"appender.file.path\", ";
  protected final String TEXT_65 = ");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_66 = ".setProperty(\"appender.file.maxsize\", \"52428800\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_67 = ".setProperty(\"appender.file.maxbackup\", \"20\");" + NL + "\t\t\t\tproperties_";
  protected final String TEXT_68 = ".setProperty(\"host\", \"false\");" + NL + "\t\t\t\t" + NL + "\t\t\t\tauditLogger_";
  protected final String TEXT_69 = " = org.talend.job.audit.JobEventAuditLoggerFactory.createJobAuditLogger(properties_";
  protected final String TEXT_70 = ");" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_71 = NL + NL + "        if(clientHost == null) {" + NL + "            clientHost = defaultClientHost;" + NL + "        }" + NL + "" + NL + "        if(pid == null || \"0\".equals(pid)) {" + NL + "            pid = TalendString.getAsciiRandomString(6);" + NL + "        }" + NL + "" + NL + "        if (rootPid==null) {" + NL + "            rootPid = pid;" + NL + "        }" + NL + "        if (fatherPid==null) {" + NL + "            fatherPid = pid;" + NL + "        }else{" + NL + "            isChildJob = true;" + NL + "        }" + NL;
  protected final String TEXT_72 = NL + "        if (portStats != null) {" + NL + "            // portStats = -1; //for testing" + NL + "            if (portStats < 0 || portStats > 65535) {" + NL + "                // issue:10869, the portStats is invalid, so this client socket can't open" + NL + "                System.err.println(\"The statistics socket port \" + portStats + \" is invalid.\");" + NL + "                execStat = false;" + NL + "            }" + NL + "        } else {" + NL + "            execStat = false;" + NL + "        }";
  protected final String TEXT_73 = NL + NL + "        try {" + NL + "            //call job/subjob with an existing context, like: --context=production. if without this parameter, there will use the default context instead." + NL + "            java.io.InputStream inContext = ";
  protected final String TEXT_74 = ".class.getClassLoader().getResourceAsStream(\"";
  protected final String TEXT_75 = "/contexts/\" + contextStr + \".properties\");" + NL + "            if (inContext == null) {" + NL + "                inContext = ";
  protected final String TEXT_76 = ".class.getClassLoader().getResourceAsStream(\"config/contexts/\" + contextStr + \".properties\");" + NL + "            }" + NL + "            if (inContext != null) {" + NL + "                //defaultProps is in order to keep the original context value" + NL + "                if(context != null && context.isEmpty()) {" + NL + "\t                defaultProps.load(inContext);" + NL + "\t                context = new ContextProperties(defaultProps);" + NL + "                }" + NL + "                " + NL + "                inContext.close();" + NL + "            } else if (!isDefaultContext) {" + NL + "                //print info and job continue to run, for case: context_param is not empty." + NL + "                System.err.println(\"Could not find the context \" + contextStr);" + NL + "            }" + NL + "" + NL + "            if(!context_param.isEmpty()) {" + NL + "                context.putAll(context_param);" + NL + "\t\t\t\t//set types for params from parentJobs" + NL + "\t\t\t\tfor (Object key: context_param.keySet()){" + NL + "\t\t\t\t\tString context_key = key.toString();" + NL + "\t\t\t\t\tString context_type = context_param.getContextType(context_key);" + NL + "\t\t\t\t\tcontext.setContextType(context_key, context_type);" + NL + "" + NL + "\t\t\t\t}" + NL + "            }";
  protected final String TEXT_77 = NL + "            class ContextProcessing {" + NL + "                private void processContext_0() {";
  protected final String TEXT_78 = NL + "                        }" + NL + "" + NL + "                private void processContext_";
  protected final String TEXT_79 = "() {";
  protected final String TEXT_80 = NL + "                        context.setContextType(\"";
  protected final String TEXT_81 = "\", \"";
  protected final String TEXT_82 = NL + "                            String pwd_";
  protected final String TEXT_83 = "_value = context.getProperty(\"";
  protected final String TEXT_84 = "\");" + NL + "                            context.";
  protected final String TEXT_85 = " = null;" + NL + "                            if(pwd_";
  protected final String TEXT_86 = "_value!=null) {" + NL + "                                if(context_param.containsKey(\"";
  protected final String TEXT_87 = "\")) {//no need to decrypt if it come from program argument or parent job runtime" + NL + "                                    context.";
  protected final String TEXT_88 = " = pwd_";
  protected final String TEXT_89 = "_value;" + NL + "                                } else if (!pwd_";
  protected final String TEXT_90 = "_value.isEmpty()) {" + NL + "                                    try {" + NL + "                                        context.";
  protected final String TEXT_91 = " = routines.system.PasswordEncryptUtil.decryptPassword(pwd_";
  protected final String TEXT_92 = "_value);" + NL + "                                        context.put(\"";
  protected final String TEXT_93 = "\",context.";
  protected final String TEXT_94 = ");" + NL + "                                    } catch (java.lang.RuntimeException e) {" + NL + "                                        //do nothing" + NL + "                                    }" + NL + "                                }" + NL + "                            }";
  protected final String TEXT_95 = NL + "                        try{" + NL + "                            String context_";
  protected final String TEXT_96 = "\");" + NL + "                            if (context_";
  protected final String TEXT_97 = "_value == null){" + NL + "                                context_";
  protected final String TEXT_98 = "_value = \"\";" + NL + "                            }" + NL + "                            int context_";
  protected final String TEXT_99 = "_pos = context_";
  protected final String TEXT_100 = "_value.indexOf(\";\");" + NL + "                            String context_";
  protected final String TEXT_101 = "_pattern =  \"yyyy-MM-dd HH:mm:ss\";" + NL + "                            if(context_";
  protected final String TEXT_102 = "_pos > -1){" + NL + "                                context_";
  protected final String TEXT_103 = "_pattern = context_";
  protected final String TEXT_104 = "_value.substring(0, context_";
  protected final String TEXT_105 = "_pos);" + NL + "                                context_";
  protected final String TEXT_106 = "_value = context_";
  protected final String TEXT_107 = "_value.substring(context_";
  protected final String TEXT_108 = "_pos + 1);" + NL + "                            }" + NL + "" + NL + "                            context.";
  protected final String TEXT_109 = "=(java.util.Date)(new java.text.SimpleDateFormat(context_";
  protected final String TEXT_110 = "_pattern).parse(context_";
  protected final String TEXT_111 = "_value));" + NL + "" + NL + "                        } catch(ParseException e) {";
  protected final String TEXT_112 = NL + "                                log.warn(String.format(\"";
  protected final String TEXT_113 = "\", e.getMessage()));";
  protected final String TEXT_114 = NL + "                                System.err.println(String.format(\"";
  protected final String TEXT_115 = NL + "                            context.";
  protected final String TEXT_116 = "=null;" + NL + "                        }";
  protected final String TEXT_117 = "=(";
  protected final String TEXT_118 = ") context.getProperty(\"";
  protected final String TEXT_119 = "= new java.text.StringCharacterIterator(context.getProperty(\"";
  protected final String TEXT_120 = "\")).first();";
  protected final String TEXT_121 = NL + "                            try{" + NL + "                                context.";
  protected final String TEXT_122 = "=routines.system.ParserUtils.parseTo_";
  protected final String TEXT_123 = " (context.getProperty(\"";
  protected final String TEXT_124 = "\"));" + NL + "                            } catch(NumberFormatException e){";
  protected final String TEXT_125 = NL + "                                context.";
  protected final String TEXT_126 = "=null;" + NL + "                            }";
  protected final String TEXT_127 = NL + "                } ";
  protected final String TEXT_128 = NL + "                public void processAllContext() {";
  protected final String TEXT_129 = NL + "                        processContext_";
  protected final String TEXT_130 = "();";
  protected final String TEXT_131 = NL + "                }" + NL + "            }" + NL + "" + NL + "            new ContextProcessing().processAllContext();" + NL + "        } catch (java.io.IOException ie) {" + NL + "            System.err.println(\"Could not load context \"+contextStr);" + NL + "            ie.printStackTrace();" + NL + "        }" + NL + "" + NL + "        // get context value from parent directly" + NL + "        if (parentContextMap != null && !parentContextMap.isEmpty()) {";
  protected final String TEXT_132 = "if (parentContextMap.containsKey(\"";
  protected final String TEXT_133 = "\")) {" + NL + "                context.";
  protected final String TEXT_134 = " = (";
  protected final String TEXT_135 = ") parentContextMap.get(\"";
  protected final String TEXT_136 = "\");" + NL + "            }";
  protected final String TEXT_137 = NL + "        }" + NL + "" + NL + "        //Resume: init the resumeUtil" + NL + "        resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);" + NL + "        resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);" + NL + "        resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);" + NL + "" + NL + "\t\tList<String> parametersToEncrypt = new java.util.ArrayList<String>();";
  protected final String TEXT_138 = NL + "\t\t\tparametersToEncrypt.add(\"";
  protected final String TEXT_139 = NL + "        //Resume: jobStart" + NL + "        resumeUtil.addLog(\"JOB_STARTED\", \"JOB:\" + jobName, parent_part_launcher, Thread.currentThread().getId() + \"\", \"\",\"\",\"\",\"\",resumeUtil.convertToJsonText(context,parametersToEncrypt));" + NL;
  protected final String TEXT_140 = NL + "if(execStat) {" + NL + "    try {" + NL + "        runStat.openSocket(!isChildJob);" + NL + "        runStat.setAllPID(rootPid, fatherPid, pid, jobName);" + NL + "        runStat.startThreadStat(clientHost, portStats);" + NL + "        runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);" + NL + "    } catch (java.io.IOException ioException) {" + NL + "        ioException.printStackTrace();" + NL + "    }" + NL + "}";
  protected final String TEXT_141 = NL;
  protected final String TEXT_142 = NL + "    try {" + NL + "        runTrace.openSocket(!isChildJob);" + NL + "        runTrace.startThreadTrace(clientHost, portTraces);" + NL + "        if (runTrace.isPause()) {" + NL + "\t\t\twhile (runTrace.isPause()) {" + NL + "\t\t\t\tThread.sleep(100);" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "    } catch (java.io.IOException ioException) {" + NL + "        ioException.printStackTrace();" + NL + "    } catch (InterruptedException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t}";
  protected final String TEXT_143 = NL + "\t";
  protected final String TEXT_144 = NL + "\t    java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();" + NL + "\t    globalMap.put(\"concurrentHashMap\", concurrentHashMap);" + NL + "\t";
  protected final String TEXT_145 = NL + NL + "    long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();" + NL + "    long endUsedMemory = 0;" + NL + "    long end = 0;" + NL + "" + NL + "    startTime = System.currentTimeMillis();";
  protected final String TEXT_146 = ".addMessage(\"begin\");";
  protected final String TEXT_147 = NL + "    System.err.println(\"Only one hadoop configuration is allowed in one job!\");";
  protected final String TEXT_148 = NL + NL + "this.globalResumeTicket = true;//to run tPreJob" + NL;
  protected final String TEXT_149 = NL + NL;
  protected final String TEXT_150 = NL + "        try {";
  protected final String TEXT_151 = NL + "            ";
  protected final String TEXT_152 = "Process(globalMap);" + NL + "        } catch (java.lang.Exception e) {" + NL + "            e.printStackTrace();" + NL + "        }";
  protected final String TEXT_153 = NL + "\t\tif(enableLogStash) {" + NL + "\t        ";
  protected final String TEXT_154 = ".addJobStartMessage();" + NL + "\t        try {" + NL + "\t            ";
  protected final String TEXT_155 = "Process(globalMap);" + NL + "\t        } catch (java.lang.Exception e) {" + NL + "\t            e.printStackTrace();" + NL + "\t        }" + NL + "        }";
  protected final String TEXT_156 = NL + NL + "this.globalResumeTicket = false;//to run others jobs" + NL;
  protected final String TEXT_157 = "final Thread launchingThread = Thread.currentThread();";
  protected final String TEXT_158 = NL + "        runningThreadCount.add(1);" + NL + "        new Thread(){" + NL + "            public void run() {" + NL + "                java.util.Map threadRunResultMap = new java.util.HashMap();" + NL + "                threadRunResultMap.put(\"errorCode\", null);" + NL + "                threadRunResultMap.put(\"status\", \"\");" + NL + "                threadLocal.set(threadRunResultMap);" + NL;
  protected final String TEXT_159 = NL + "                ";
  protected final String TEXT_160 = NL + "                finally {" + NL + "                    Integer localErrorCode = (Integer)(((java.util.Map)threadLocal.get()).get(\"errorCode\"));" + NL + "                    String localStatus = (String)(((java.util.Map)threadLocal.get()).get(\"status\"));" + NL + "                    if (localErrorCode != null) {" + NL + "                        if (errorCode == null || localErrorCode.compareTo(errorCode) > 0) {" + NL + "                           errorCode = localErrorCode;" + NL + "                        }" + NL + "                    }" + NL + "                    if (!status.equals(\"failure\")){" + NL + "                        status = localStatus;" + NL + "                    }" + NL + "" + NL + "                    if (\"true\".equals(((java.util.Map) threadLocal.get()).get(\"JobInterrupted\"))) {" + NL + "                        launchingThread.interrupt();" + NL + "                    }" + NL + "" + NL + "                    runningThreadCount.add(-1);" + NL + "                }" + NL + "            }" + NL + "        }.start();" + NL;
  protected final String TEXT_161 = NL + "    boolean interrupted = false;" + NL + "    while (runningThreadCount.getCount() > 0) {" + NL + "        try {" + NL + "            Thread.sleep(10);" + NL + "        } catch (java.lang.InterruptedException e) {" + NL + "            interrupted = true;" + NL + "        } catch (java.lang.Exception e) {" + NL + "            e.printStackTrace();" + NL + "        }" + NL + "    }" + NL + "" + NL + "    if (interrupted) {" + NL + "        Thread.currentThread().interrupt();" + NL + "    }" + NL + NL;
  protected final String TEXT_162 = NL + NL + "this.globalResumeTicket = true;//to run tPostJob" + NL;
  protected final String TEXT_163 = NL + NL + "        end = System.currentTimeMillis();" + NL + "" + NL + "        if (watch) {" + NL + "            System.out.println((end-startTime)+\" milliseconds\");" + NL + "        }" + NL + "" + NL + "        endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();" + NL + "        if (false) {" + NL + "            System.out.println((endUsedMemory - startUsedMemory) + \" bytes memory increase when running : ";
  protected final String TEXT_164 = "\");" + NL + "        }";
  protected final String TEXT_165 = ".addMessage(status==\"\"?\"end\":status, (end-startTime));" + NL + "        try {";
  protected final String TEXT_166 = ".addJobEndMessage(startTime, end, status);" + NL + "\t        try {" + NL + "\t            ";
  protected final String TEXT_167 = NL + "    closeJmsConnections();";
  protected final String TEXT_168 = NL + "    closeScpConnections();";
  protected final String TEXT_169 = NL + "if (execStat) {" + NL + "    runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);" + NL + "    runStat.stopThreadStat();" + NL + "}";
  protected final String TEXT_170 = NL + "        runTrace.stopThreadTrace();";
  protected final String TEXT_171 = NL + "    int returnCode = 0;" + NL + "    if(errorCode == null) {" + NL + "         returnCode = status != null && status.equals(\"failure\") ? 1 : 0;" + NL + "    } else {" + NL + "         returnCode = errorCode.intValue();" + NL + "    }" + NL + "    resumeUtil.addLog(\"JOB_ENDED\", \"JOB:\" + jobName, parent_part_launcher, Thread.currentThread().getId() + \"\", \"\",\"\" + returnCode,\"\",\"\",\"\");" + NL + "" + NL + "    return returnCode;" + NL + "" + NL + "  }" + NL + "" + NL + "    // only for OSGi env" + NL + "    public void destroy() {";
  protected final String TEXT_172 = NL + "    closeSqlDbConnections();";
  protected final String TEXT_173 = NL + "    closeCassandraConnections();";
  protected final String TEXT_174 = NL + "    closeHBaseConnections();";
  protected final String TEXT_175 = NL + "    closeFtpConnections();";
  protected final String TEXT_176 = NL + "    closeS3Connections();";
  protected final String TEXT_177 = NL + "    closeLDAPConnections();";
  protected final String TEXT_178 = NL + "    closeMDMConnections();";
  protected final String TEXT_179 = NL + "    closePaloConnections();";
  protected final String TEXT_180 = NL + "    closeEXistConnections();";
  protected final String TEXT_181 = NL + "    closeGSConnections();";
  protected final String TEXT_182 = NL + NL + NL + "    }" + NL;
  protected final String TEXT_183 = NL + "    private void closeJmsConnections() {" + NL + "        try {" + NL + "            Object obj_conn;";
  protected final String TEXT_184 = NL + "            obj_conn = globalMap.remove(\"connection_";
  protected final String TEXT_185 = "\");" + NL + "            if (null != obj_conn) {" + NL + "                ((javax.jms.Connection) obj_conn).close();" + NL + "            }";
  protected final String TEXT_186 = NL + "        } catch (java.lang.Exception e) {" + NL + "        }" + NL + "    }";
  protected final String TEXT_187 = NL + "    private void closeScpConnections() {" + NL + "        try {" + NL + "            Object obj_conn;";
  protected final String TEXT_188 = NL + "            obj_conn = globalMap.remove(\"conn_";
  protected final String TEXT_189 = "\");" + NL + "            if (null != obj_conn) {" + NL + "                ((ch.ethz.ssh2.Connection) obj_conn).close();" + NL + "            }";
  protected final String TEXT_190 = NL + "    private void closeSqlDbConnections() {" + NL + "        try {" + NL + "            Object obj_conn;";
  protected final String TEXT_191 = "\");" + NL + "            if (null != obj_conn) {" + NL + "                ((java.sql.Connection) obj_conn).close();" + NL + "            }";
  protected final String TEXT_192 = NL + "    private void closeCassandraConnections() {" + NL + "        try {" + NL + "            Object obj_cluster;" + NL + "            Object obj_conn;";
  protected final String TEXT_193 = NL + "            obj_cluster = globalMap.remove(\"cluster_";
  protected final String TEXT_194 = "\");" + NL + "            obj_conn = globalMap.remove(\"connection_";
  protected final String TEXT_195 = "\");" + NL + "            if(obj_conn != null) {" + NL + "                ((com.datastax.driver.core.Session) obj_conn).close();" + NL + "            }" + NL + "            if(obj_cluster != null) {" + NL + "                ((com.datastax.driver.core.Cluster) obj_cluster).close();" + NL + "            }";
  protected final String TEXT_196 = NL + "            obj_conn = globalMap.remove(\"cluster_";
  protected final String TEXT_197 = "\");" + NL + "            if(obj_conn!=null) {" + NL + "                ((me.prettyprint.hector.api.Cluster) obj_conn).getConnectionManager().shutdown();" + NL + "            }";
  protected final String TEXT_198 = NL + "    private void closeHBaseConnections() {" + NL + "        try {";
  protected final String TEXT_199 = NL + "   \t\t\torg.apache.hadoop.conf.Configuration conn_";
  protected final String TEXT_200 = " =(org.apache.hadoop.conf.Configuration)globalMap.get(\"conn_";
  protected final String TEXT_201 = "\");" + NL + "\t\t\tif(conn_";
  protected final String TEXT_202 = " != null )" + NL + "\t        {" + NL + "\t\t\t";
  protected final String TEXT_203 = NL + "                                org.apache.hadoop.hbase.client.HConnection hConnection_";
  protected final String TEXT_204 = " = org.apache.hadoop.hbase.client.HConnectionManager.getConnection(conn_";
  protected final String TEXT_205 = NL + "                                org.apache.hadoop.hbase.client.Connection hConnection_";
  protected final String TEXT_206 = " = org.apache.hadoop.hbase.client.ConnectionFactory.createConnection(conn_";
  protected final String TEXT_207 = NL + "\t\t        if ((hConnection_";
  protected final String TEXT_208 = " != null) && (!hConnection_";
  protected final String TEXT_209 = ".isClosed())) {" + NL + "\t\t\t\t    hConnection_";
  protected final String TEXT_210 = ".close();" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_211 = NL + "\t\t\t\torg.apache.hadoop.hbase.client.HConnectionManager.deleteConnection(conn_";
  protected final String TEXT_212 = ",true);" + NL + "\t\t\t";
  protected final String TEXT_213 = NL + "\t\t\t}" + NL + "      globalMap.remove(\"conn_";
  protected final String TEXT_214 = "\");" + NL + "\t";
  protected final String TEXT_215 = NL + "    private void closeFtpConnections() {" + NL + "        try {" + NL + "            Object obj_conn;";
  protected final String TEXT_216 = "\");" + NL + "            if (obj_conn != null) {";
  protected final String TEXT_217 = NL + "                ((com.enterprisedt.net.ftp.FTPClient) obj_conn).quit();";
  protected final String TEXT_218 = NL + "                ((com.jcraft.jsch.ChannelSftp) obj_conn).quit();";
  protected final String TEXT_219 = NL + "                ((org.apache.commons.net.ftp.FTPSClient) obj_conn).disconnect();";
  protected final String TEXT_220 = NL + "            }";
  protected final String TEXT_221 = NL + "    private void closeS3Connections() {" + NL + "        try {";
  protected final String TEXT_222 = NL + "    \tcom.amazonaws.services.s3.AmazonS3Client conn_";
  protected final String TEXT_223 = " = (com.amazonaws.services.s3.AmazonS3Client)globalMap.get(\"conn_";
  protected final String TEXT_224 = "\");" + NL + "\t    if(conn_";
  protected final String TEXT_225 = " !=null){" + NL + "\t\t\tconn_";
  protected final String TEXT_226 = ".shutdown();" + NL + "\t\t}";
  protected final String TEXT_227 = NL + "    private void closeLDAPConnections() {" + NL + "        try {" + NL + "            Object obj_conn;";
  protected final String TEXT_228 = "\");" + NL + "            if (obj_conn != null) {" + NL + "                ((javax.naming.ldap.InitialLdapContext) obj_conn).close();" + NL + "            }";
  protected final String TEXT_229 = NL + "    private void closeMDMConnections() {" + NL + "        try {" + NL + "            java.util.Iterator keySets = globalMap.keySet().iterator();" + NL + "            while (keySets.hasNext()) {" + NL + "                String key = keySets.next().toString();" + NL + "                if (key.startsWith(\"TMDMService_tMDMConnection_\")) {" + NL + "                    org.talend.mdm.webservice.TMDMService service = (org.talend.mdm.webservice.TMDMService)globalMap.get(key);" + NL + "                    if (service != null) {" + NL + "                        service.logout(new org.talend.mdm.webservice.WSLogout());" + NL + "                    }" + NL + "                }" + NL + "            }" + NL + "        } catch (java.lang.Exception e) {" + NL + "        }" + NL + "    }";
  protected final String TEXT_230 = NL + "    private void closePaloConnections() {" + NL + "        try {";
  protected final String TEXT_231 = NL + "    \torg.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_232 = " = (org.talend.jpalo.paloconnection)globalMap.get(\"pConn_";
  protected final String TEXT_233 = "\");" + NL + "\t    if(pConn_";
  protected final String TEXT_234 = " !=null){" + NL + "\t\t\t\t\tpConn_";
  protected final String TEXT_235 = ".logout();" + NL + "" + NL + "\t\t}";
  protected final String TEXT_236 = NL + "    private void closeEXistConnections() {" + NL + "        try {";
  protected final String TEXT_237 = NL + "\t\torg.xmldb.api.base.Collection col_";
  protected final String TEXT_238 = " = (org.xmldb.api.base.Collection)globalMap.get(\"col_";
  protected final String TEXT_239 = "\");" + NL + "\t    if(col_";
  protected final String TEXT_240 = " !=null){" + NL + "\t\t\t\t\tcol_";
  protected final String TEXT_241 = ".close();" + NL + "" + NL + "\t\t}";
  protected final String TEXT_242 = NL + "    private void closeGSConnections() {" + NL + "        try {";
  protected final String TEXT_243 = NL + "\t\torg.jets3t.service.impl.rest.httpclient.GoogleStorageService service_";
  protected final String TEXT_244 = " =" + NL + "\t\t\t\t(org.jets3t.service.impl.rest.httpclient.GoogleStorageService)globalMap.get(\"service_";
  protected final String TEXT_245 = "\");" + NL + "\t\tif(service_";
  protected final String TEXT_246 = " !=null){" + NL + "\t\t\t\tservice_";
  protected final String TEXT_247 = ".shutdown();" + NL + "\t    }" + NL;
  protected final String TEXT_248 = NL + NL + "    private java.util.Map<String, Object> getSharedConnections4REST() {" + NL + "        java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();";
  protected final String TEXT_249 = NL + "            connections.put(\"connection_";
  protected final String TEXT_250 = "\", globalMap.get(\"connection_";
  protected final String TEXT_251 = "\"));" + NL + "            connections.put(\"session_";
  protected final String TEXT_252 = "\", globalMap.get(\"session_";
  protected final String TEXT_253 = "\"));";
  protected final String TEXT_254 = NL + "            connections.put(\"conn_";
  protected final String TEXT_255 = "\", globalMap.get(\"conn_";
  protected final String TEXT_256 = "\"));" + NL + "            connections.put(\"cluster_";
  protected final String TEXT_257 = "\", globalMap.get(\"cluster_";
  protected final String TEXT_258 = NL + "            connections.put(\"cluster_";
  protected final String TEXT_259 = "\"));" + NL + "            connections.put(\"connBaseDN_";
  protected final String TEXT_260 = "\", globalMap.get(\"connBaseDN_";
  protected final String TEXT_261 = NL + "            connections.put(\"mdmUrl_";
  protected final String TEXT_262 = "\", globalMap.get(\"mdmUrl_";
  protected final String TEXT_263 = "\"));" + NL + "            connections.put(\"username_";
  protected final String TEXT_264 = "\", globalMap.get(\"username_";
  protected final String TEXT_265 = "\"));" + NL + "            connections.put(\"password_";
  protected final String TEXT_266 = "\", globalMap.get(\"password_";
  protected final String TEXT_267 = "\"));" + NL + "            connections.put(\"useTransaction_";
  protected final String TEXT_268 = "\", globalMap.get(\"useTransaction_";
  protected final String TEXT_269 = "\"));" + NL + "            connections.put(\"useClientTranId_";
  protected final String TEXT_270 = "\", globalMap.get(\"useClientTranId_";
  protected final String TEXT_271 = NL + "            connections.put(\"p_";
  protected final String TEXT_272 = "\", globalMap.get(\"p_";
  protected final String TEXT_273 = "\"));" + NL + "            connections.put(\"pConn_";
  protected final String TEXT_274 = "\", globalMap.get(\"pConn_";
  protected final String TEXT_275 = NL + "            connections.put(\"col_";
  protected final String TEXT_276 = "\", globalMap.get(\"col_";
  protected final String TEXT_277 = NL + "            connections.put(\"service_";
  protected final String TEXT_278 = "\", globalMap.get(\"service_";
  protected final String TEXT_279 = NL + NL + NL + "        return connections;" + NL + "    }" + NL + "" + NL + "    private void evalParam(String arg) {" + NL + "        if (arg.startsWith(\"--resuming_logs_dir_path\")) {" + NL + "            resuming_logs_dir_path = arg.substring(25);" + NL + "        } else if (arg.startsWith(\"--resuming_checkpoint_path\")) {" + NL + "            resuming_checkpoint_path = arg.substring(27);" + NL + "        } else if (arg.startsWith(\"--parent_part_launcher\")) {" + NL + "            parent_part_launcher = arg.substring(23);" + NL + "        } else if (arg.startsWith(\"--watch\")) {" + NL + "            watch = true;" + NL + "        } else if (arg.startsWith(\"--stat_port=\")) {" + NL + "            String portStatsStr = arg.substring(12);" + NL + "            if (portStatsStr != null && !portStatsStr.equals(\"null\")) {" + NL + "                portStats = Integer.parseInt(portStatsStr);" + NL + "            }" + NL + "        } else if (arg.startsWith(\"--trace_port=\")) {" + NL + "            portTraces = Integer.parseInt(arg.substring(13));" + NL + "        } else if (arg.startsWith(\"--client_host=\")) {" + NL + "            clientHost = arg.substring(14);" + NL + "        } else if (arg.startsWith(\"--context=\")) {" + NL + "            contextStr = arg.substring(10);" + NL + "            isDefaultContext = false;" + NL + "        } else if (arg.startsWith(\"--father_pid=\")) {" + NL + "            fatherPid = arg.substring(13);" + NL + "        } else if (arg.startsWith(\"--root_pid=\")) {" + NL + "            rootPid = arg.substring(11);" + NL + "        } else if (arg.startsWith(\"--father_node=\")) {" + NL + "            fatherNode = arg.substring(14);" + NL + "        } else if (arg.startsWith(\"--pid=\")) {" + NL + "            pid = arg.substring(6);" + NL + "        } else if (arg.startsWith(\"--context_type\")) {" + NL + "            String keyValue = arg.substring(15);" + NL + "\t\t\tint index = -1;" + NL + "            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {" + NL + "                if (fatherPid==null) {" + NL + "                    context_param.setContextType(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));" + NL + "                } else { // the subjob won't escape the especial chars" + NL + "                    context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1) );" + NL + "                }" + NL + "" + NL + "            }" + NL + "" + NL + "\t\t} else if (arg.startsWith(\"--context_param\")) {" + NL + "            String keyValue = arg.substring(16);" + NL + "            int index = -1;" + NL + "            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {" + NL + "                if (fatherPid==null) {" + NL + "                    context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));" + NL + "                } else { // the subjob won't escape the especial chars" + NL + "                    context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1) );" + NL + "                }" + NL + "            }" + NL + "        } else if (arg.startsWith(\"--log4jLevel=\")) {" + NL + "            log4jLevel = arg.substring(13);" + NL + "\t\t} else if (arg.startsWith(\"--monitoring=\")) {//for trunjob call" + NL + "\t\t\tenableLogStash = \"true\".equalsIgnoreCase(arg.substring(13));" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tif(!enableLogStash) {" + NL + "\t\t\tenableLogStash = \"true\".equalsIgnoreCase(System.getProperty(\"monitoring\"));" + NL + "\t\t}" + NL + "    }" + NL + "    " + NL + "    private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = \"<TALEND_NULL>\";" + NL + "" + NL + "    private final String[][] escapeChars = {" + NL + "        {\"\\\\\\\\\",\"\\\\\"},{\"\\\\n\",\"\\n\"},{\"\\\\'\",\"\\'\"},{\"\\\\r\",\"\\r\"}," + NL + "        {\"\\\\f\",\"\\f\"},{\"\\\\b\",\"\\b\"},{\"\\\\t\",\"\\t\"}" + NL + "        };" + NL + "    private String replaceEscapeChars (String keyValue) {" + NL + "" + NL + "\t\tif (keyValue == null || (\"\").equals(keyValue.trim())) {" + NL + "\t\t\treturn keyValue;" + NL + "\t\t}" + NL + "" + NL + "\t\tStringBuilder result = new StringBuilder();" + NL + "\t\tint currIndex = 0;" + NL + "\t\twhile (currIndex < keyValue.length()) {" + NL + "\t\t\tint index = -1;" + NL + "\t\t\t// judege if the left string includes escape chars" + NL + "\t\t\tfor (String[] strArray : escapeChars) {" + NL + "\t\t\t\tindex = keyValue.indexOf(strArray[0],currIndex);" + NL + "\t\t\t\tif (index>=0) {" + NL + "" + NL + "\t\t\t\t\tresult.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0], strArray[1]));" + NL + "\t\t\t\t\tcurrIndex = index + strArray[0].length();" + NL + "\t\t\t\t\tbreak;" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\t// if the left string doesn't include escape chars, append the left into the result" + NL + "\t\t\tif (index < 0) {" + NL + "\t\t\t\tresult.append(keyValue.substring(currIndex));" + NL + "\t\t\t\tcurrIndex = currIndex + keyValue.length();" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "" + NL + "\t\treturn result.toString();" + NL + "    }" + NL + "" + NL + "    public Integer getErrorCode() {" + NL + "        return errorCode;" + NL + "    }" + NL + "" + NL + "" + NL + "    public String getStatus() {" + NL + "        return status;" + NL + "    }" + NL + "" + NL + "    ResumeUtil resumeUtil = null;" + NL + "}";

    private static String end_multiThread = "if ( !\"failure\".equals(((java.util.Map)threadLocal.get()).get(\"status\")) ) {\n((java.util.Map) threadLocal.get()).put(\"status\", \"end\");\n}";
    private static String end_singleThread = "if(!\"failure\".equals(status)) { status = \"end\"; }";
    private static String failure_multiThread = "((java.util.Map) threadLocal.get()).put(\"status\", \"failure\");";
    private static String failure_singleThread = "status = \"failure\";";
    private static String errorCode_multiThread = "((java.util.Map) threadLocal.get()).put(\"errorCode\", null);";
    private static String errorCode_singleThread = "errorCode = null;";
    
    // add the list of the connection names to avoid to declare two times the same name.
    public String createCallProcess(INode rootNode, String className, boolean isMultiThread) {
        String toReturn = "";
        toReturn =  "try {\n";
        if(isMultiThread) {
            toReturn +=  errorCode_multiThread;
        }else{
            toReturn +=  errorCode_singleThread;
        }        
        
        toReturn += rootNode.getUniqueName() + "Process(globalMap);\n";
        
        if(isMultiThread) {
            toReturn +=  end_multiThread;
        }else{
            toReturn +=  end_singleThread;
        }
        
        toReturn += "\n}catch (TalendException e_" + rootNode.getUniqueName() + ") {\n";
        
//        if(isMultiThread) {
//            toReturn +=  failure_multiThread;
//        }else{
//            toReturn +=  failure_singleThread;
//        }
        
        toReturn += "globalMap.put(\""+rootNode.getUniqueName()+ "_SUBPROCESS_STATE\", -1);\n";
        
        toReturn += "\ne_" + rootNode.getUniqueName() + ".printStackTrace();\n";
        
      
       //List< ? extends IConnection> onSubJobErrorConns = rootNode.getOutgoingConnections(EConnectionType.ON_SUBJOB_ERROR);
       //if(onSubJobErrorConns!=null){
       //    for(IConnection conn : onSubJobErrorConns) {               
       //        toReturn += createCallProcess(conn.getTarget(),  className, isMultiThread);
       //    }
       //}            
        if(isMultiThread){
            toReturn += "\n}catch (java.lang.Error e_" + rootNode.getUniqueName() + ") {\n";
            toReturn += "globalMap.put(\""+rootNode.getUniqueName()+ "_SUBPROCESS_STATE\", -1);\n";
            toReturn += failure_multiThread;
            toReturn += "throw e_" + rootNode.getUniqueName() + ";\n";
        }
       toReturn += "\n}"; 
       return toReturn;
    }
		 
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    Vector v = (Vector) codeGenArgument.getArgument();
    IProcess process = (IProcess)v.get(0);
    String className = process.getName();
    boolean isTestContainer=ProcessUtils.isTestContainer(process);
    if (isTestContainer) {
        className = className + "Test";
    }

    List<INode> rootNodes = (List<INode>)v.get(1);
	boolean containsTPartitioner = process.getNodesOfType("tPartitioner").size() > 0 ? true : false;
    boolean stats = codeGenArgument.isStatistics();
    boolean trace = codeGenArgument.isTrace();
    boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();
    List<IContextParameter> params = new ArrayList<IContextParameter>();
    params=process.getContextManager().getDefaultContext().getContextParameterList();

	String jobFolderName = "";
	IProcess baseProcess = ProcessUtils.getTestContainerBaseProcess(process);
	if (baseProcess != null) {
		jobFolderName = JavaResourcesHelper.getJobFolderName(baseProcess.getName(), baseProcess.getVersion()) + '/';
	}
	jobFolderName = jobFolderName + JavaResourcesHelper.getJobFolderName(process.getName(), process.getVersion());
	String jobClassPackageFolder = codeGenArgument.getCurrentProjectName().toLowerCase() + '/' + jobFolderName;

    boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(process, "__LOG4J_ACTIVATE__"));

    boolean exist_tParallelize = false;
    List<? extends INode> tParallelizeList = process.getNodesOfType("tParallelize");
    if(tParallelizeList != null && tParallelizeList.size() > 0){
        exist_tParallelize = true;
    }

    boolean exist_tMOM = false;
    List<INode> momComponentsList = (List<INode>)process.getNodesOfType("tMomConnection");
    momComponentsList.addAll((List<INode>)process.getNodesOfType("tMomInput"));
    momComponentsList.addAll((List<INode>)process.getNodesOfType("tMomOutput"));
    if (momComponentsList.size() > 0) {
        exist_tMOM = true;
    }

    String[] sqlDbComponents = new String[] {
        "tAccessConnection",
        "tAmazonMysqlConnection",
        "tAmazonOracleConnection",
        "tAS400Connection",
        "tDB2Connection",
        "tFirebirdConnection",
        "tGreenplumConnection",
        "tHiveConnection",
        "tInformixConnection",
        "tIngresConnection",
        "tInterbaseConnection",
        "tJDBCConnection",
        "tMSSqlConnection",
        "tMysqlConnection",
        "tNetezzaConnection",
        "tOracleConnection",
        "tParAccelConnection",
        "tPostgresPlusConnection",
        "tPostgresqlConnection",
        "tRedshiftConnection",
        "tSQLiteConnection",
        "tSybaseConnection",
        "tTeradataConnection",
        "tVectorWiseConnection",
        "tVerticaConnection"
    };
    boolean exist_tSQLDB = false;
    List<INode> sqlDbComponentsList =  new ArrayList<INode>();
    for (String sqlDbComponent : sqlDbComponents) {
        sqlDbComponentsList.addAll((List<INode>)process.getNodesOfType(sqlDbComponent));
    }
    if (sqlDbComponentsList.size() > 0) {
        exist_tSQLDB = true;
    }

    boolean exist_tSCP = false;
    List<INode> scpComponentsList = (List<INode>)process.getNodesOfType("tSCPConnection");
    if (scpComponentsList.size() > 0) {
        exist_tSCP = true;
    }

    boolean exist_tCassandra = false;
    List<INode> cassandraComponentsList = (List<INode>)process.getNodesOfType("tCassandraConnection");
    if (cassandraComponentsList.size() > 0) {
        exist_tCassandra = true;
    }
    boolean exist_tHBase = false;
    List<INode> hbaseComponentsList = (List<INode>)process.getNodesOfType("tHBaseConnection");
    if (hbaseComponentsList.size() > 0) {
        exist_tHBase = true;
    }

    boolean exist_tFtp = false;
    List<INode> ftpComponentsList = (List<INode>)process.getNodesOfType("tFTPConnection");
    if (ftpComponentsList.size() > 0) {
        exist_tFtp = true;
    }

    boolean exist_tS3 = false;
    List<INode> s3ComponentsList = (List<INode>)process.getNodesOfType("tS3Connection");
    if (s3ComponentsList.size() > 0) {
        exist_tS3 = true;
    }

	boolean exist_tLDAP = false;
    List<INode> ldapComponentsList = (List<INode>)process.getNodesOfType("tLDAPConnection");
    if (ldapComponentsList.size() > 0) {
        exist_tLDAP = true;
    }

    boolean exist_tMDM = false;
    List<INode> mdmComponentsList = (List<INode>)process.getNodesOfType("tMDMConnection");
    if (mdmComponentsList.size() > 0) {
        exist_tMDM = true;
    }

    boolean exist_tPalo = false;
    List<INode> paloComponentsList = (List<INode>)process.getNodesOfType("tPaloConnection");
    if (paloComponentsList.size() > 0) {
        exist_tPalo = true;
    }

    boolean exist_tEXist= false;
    List<INode> existComponentsList = (List<INode>)process.getNodesOfType("tEXistConnection");
    if (existComponentsList.size() > 0) {
        exist_tEXist = true;
    }

    boolean exist_tSalesforce= false;
    List<INode> salesforceComponentsList = (List<INode>)process.getNodesOfType("tSalesforceConnection");
    if (salesforceComponentsList.size() > 0) {
        exist_tSalesforce = true;
    }

    boolean exist_tSAP= false;
    List<INode> sapComponentsList = (List<INode>)process.getNodesOfType("tSAPConnection");
    if (sapComponentsList.size() > 0) {
        exist_tSAP = true;
    }

    boolean exist_tGS= false;
    List<INode> gsComponentsList = (List<INode>)process.getNodesOfType("tGSConnection");
    if (gsComponentsList.size() > 0) {
        exist_tGS = true;
    }

    boolean hasSAM4RS = false;
    for (INode node : process.getNodesOfType("tRESTClient")) {
        if (Boolean.valueOf(ElementParameterParser.getValue(node, "__SERVICE_ACTIVITY_MONITOR__"))) {
            hasSAM4RS = true;
            break;
        }
    }
    boolean hasSAM4WS = false;
    for (INode node : process.getNodesOfType("tESBConsumer")) {
        if (Boolean.valueOf(ElementParameterParser.getValue(node, "__SERVICE_ACTIVITY_MONITOR__"))) {
            hasSAM4WS = true;
            break;
        }
    }
    class API_selector{
        public boolean useDatastax(INode node){
            String dbVersion = ElementParameterParser.getValue(node, "__DB_VERSION__");
            String apiType = ElementParameterParser.getValue(node, "__API_TYPE__");
            return "CASSANDRA_2_2".equals(dbVersion) || "CASSANDRA_3_0".equals(dbVersion) || ("CASSANDRA_2_0_0".equals(dbVersion) && "DATASTAX".equals(apiType));
        }
    }

    stringBuffer.append(TEXT_1);
    stringBuffer.append(codeGenArgument.getContextName() );
    stringBuffer.append(TEXT_2);
    
    if(isRunInMultiThread || exist_tParallelize){

    stringBuffer.append(TEXT_3);
    
    }

    stringBuffer.append(TEXT_4);
    
    boolean containGenericComponents = false;
    nodeloop:
	for(INode node : process.getGeneratingNodes()) {
		IComponent component = node.getComponent();
		for (ModuleNeeded module : component.getModulesNeeded(node)) {
		    if (module.getModuleName() != null && module.getModuleName().startsWith("components-common")) {
		        containGenericComponents = true;
		        break nodeloop;
		    }
		}
	}
	if(containGenericComponents) {
    
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_10);
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_11);
    stringBuffer.append(codeGenArgument.getJobName());
    stringBuffer.append(TEXT_12);
    }
    stringBuffer.append(TEXT_13);
    
    if (isTestContainer) {
         List<String> instanceList =  ProcessUtils.getTestInstances(process);
         String encoding = System.getProperty("file.encoding");
         for(String instance : instanceList)
         {
         String context = ProcessUtils.getInstanceContext(process,instance);
        
    stringBuffer.append(TEXT_14);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_15);
    
        int assertNum = ProcessUtils.getAssertAmount(process);
        
    stringBuffer.append(TEXT_16);
    stringBuffer.append(assertNum);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(context);
    stringBuffer.append(TEXT_21);
    
        for(String testData : ProcessUtils.getTestData(process,instance)){
            String path =  ProcessUtils.getTestDataValue(process, instance, testData);
            String paramemterName = ProcessUtils.getTestDataParamemter(process, instance, testData);
            if(path!=null&&path.length()>0){
            
    stringBuffer.append(TEXT_22);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_23);
    
            if(ProcessUtils.isSpark(process)){
            String lineSeparator = "\r\n";
            String fileContentEnCodeStr = "";
            try {
                fileContentEnCodeStr = java.util.Base64.getMimeEncoder().encodeToString(path.getBytes("UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            StringBuilder result = new StringBuilder();
            String[] arrayStr = fileContentEnCodeStr.split(lineSeparator);
            for(String item : arrayStr){
                result.append(item);
            }
            String testDataValue = result.toString();
            
    stringBuffer.append(TEXT_24);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(testDataValue);
    stringBuffer.append(TEXT_26);
    
            }else if(paramemterName!=null){
            
    stringBuffer.append(TEXT_27);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(path);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(paramemterName);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_33);
    
            }else{
            
    stringBuffer.append(TEXT_27);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(path);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_21);
    
            }
            
    stringBuffer.append(TEXT_34);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(testData);
    stringBuffer.append(TEXT_37);
    
         }

        }
        
    stringBuffer.append(TEXT_38);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_45);
    
        }
    }
    
    stringBuffer.append(TEXT_46);
    
        List<INode> tBufferNodes = (List<INode>)process.getNodesOfType("tBufferOutput");
        if(tBufferNodes != null && tBufferNodes.size() > 0) {
    
    stringBuffer.append(TEXT_47);
    
        } else {
    
    stringBuffer.append(TEXT_48);
    
        }
    
    stringBuffer.append(TEXT_49);
    //For TDI-28558
        	List<INode> tBufferNodesList = (List<INode>)process.getNodesOfType("tBufferOutput");
        	if(tBufferNodesList != null && tBufferNodesList.size() > 0) {
    	
    stringBuffer.append(TEXT_50);
    
        	}
		
    stringBuffer.append(TEXT_51);
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_52);
    stringBuffer.append(codeGenArgument.getJobName());
    stringBuffer.append(TEXT_53);
    }
    stringBuffer.append(TEXT_54);
    
    	INode jobCatcherNode = null;
		for (INode nodeInProcess : process.getGeneratingNodes()) {
			String componentName = nodeInProcess.getComponent().getName();
			if("tJobStructureCatcher".equals(componentName)) {
				jobCatcherNode = nodeInProcess;
				break;
			}
		}
    	if(jobCatcherNode!=null) {
    		String location = ElementParameterParser.getValue(jobCatcherNode, "__LOCATION__");
		
    stringBuffer.append(TEXT_55);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_57);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_58);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_62);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_63);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(location);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_67);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_68);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_69);
    stringBuffer.append(jobCatcherNode.getUniqueName());
    stringBuffer.append(TEXT_70);
    
		}
    	
    stringBuffer.append(TEXT_71);
    
        if (stats) {

    stringBuffer.append(TEXT_72);
    
        }

    stringBuffer.append(TEXT_73);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(jobClassPackageFolder);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_76);
    
            String warningMessageFormat = "Null value will be used for context parameter %s: %s";
            final int maxContextInOneMethodToProcess = 100;
            int lastMethodNumber = 0;

    stringBuffer.append(TEXT_77);
    
                    for (int i = 1; i <= params.size(); i++) {
                        IContextParameter ctxParam = params.get(i-1);
                        if (i % maxContextInOneMethodToProcess == 0) {
                            /* close previous method and declare new */
                            lastMethodNumber++;

    stringBuffer.append(TEXT_78);
    stringBuffer.append(lastMethodNumber);
    stringBuffer.append(TEXT_79);
    
                        } //endIf

    stringBuffer.append(TEXT_80);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getType());
    stringBuffer.append(TEXT_21);
    
                        if (ctxParam.getType().equals("id_Password")) {

    stringBuffer.append(TEXT_82);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_83);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_85);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_87);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_88);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_89);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_90);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_92);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_93);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_94);
    
                            continue;
                        }
                        String typeToGenerate ="String";
                        if( !(ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory") ||ctxParam.getType().equals("id_List Of Value"))) {
                           typeToGenerate=JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
                        }
                        if(typeToGenerate.equals("java.util.Date")) {

    stringBuffer.append(TEXT_95);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_83);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_97);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_100);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_101);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_102);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_104);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_105);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_106);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_108);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_109);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_111);
    
                            if (isLog4jEnabled) {

    stringBuffer.append(TEXT_112);
    stringBuffer.append(warningMessageFormat );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getName() );
    stringBuffer.append(TEXT_113);
    
                            } else {

    stringBuffer.append(TEXT_114);
    stringBuffer.append(warningMessageFormat );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getName() );
    stringBuffer.append(TEXT_113);
    
                            }

    stringBuffer.append(TEXT_115);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_116);
    
                        } else if(typeToGenerate.equals("Object")||typeToGenerate.equals("String")||typeToGenerate.equals("java.lang.String")) {

    stringBuffer.append(TEXT_115);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_117);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_21);
    
                        } else if(typeToGenerate.equals("Character")&&ctxParam.getName()!=null) {

    stringBuffer.append(TEXT_115);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_119);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_120);
    
                        } else {

    stringBuffer.append(TEXT_121);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_122);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_124);
    
                             if (isLog4jEnabled) {

    stringBuffer.append(TEXT_112);
    stringBuffer.append(warningMessageFormat );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getName() );
    stringBuffer.append(TEXT_113);
    
                             } else {

    stringBuffer.append(TEXT_114);
    stringBuffer.append(warningMessageFormat );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getName() );
    stringBuffer.append(TEXT_113);
    
                             }

    stringBuffer.append(TEXT_125);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_126);
    
                        }
                    }

    stringBuffer.append(TEXT_127);
    /*close last method*/
    stringBuffer.append(TEXT_128);
    
                    for (int i = 0; i <=lastMethodNumber; i++) {

    stringBuffer.append(TEXT_129);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_130);
    
                    }

    stringBuffer.append(TEXT_131);
     for (IContextParameter ctxParam :params){
            //about the type, they are same as header.javajet
            String typeToGenerate = "String";
            if(ctxParam.getType().equals("id_List Of Value") || ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory")){
                typeToGenerate = "String";
            }else{
                typeToGenerate = JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
            }
        
    stringBuffer.append(TEXT_132);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_133);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_134);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_136);
    }
    stringBuffer.append(TEXT_137);
     		for(IContextParameter ctxParam :params) {
        	if ("id_Password".equals(ctxParam.getType())) {

    stringBuffer.append(TEXT_138);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_21);
    
        	}
        }

    stringBuffer.append(TEXT_139);
    
        if (stats) {

    stringBuffer.append(TEXT_140);
    
    }

    stringBuffer.append(TEXT_141);
    
    if (trace) {

    stringBuffer.append(TEXT_142);
    
    }

    stringBuffer.append(TEXT_141);
    stringBuffer.append(TEXT_141);
    stringBuffer.append( ElementParameterParser.getValue(process, "__HEADER_CODE__"));
    stringBuffer.append(TEXT_143);
    //if(containsTPartitioner){//as tMatchGroupIn and tFuzzyUniqRow use this concurrentHashMap, so generate always
    stringBuffer.append(TEXT_144);
    //}
    stringBuffer.append(TEXT_145);
    
        //1. send the begin msg

        for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_146);
    
        }
        
        List<INode> prejobNodes = new ArrayList<INode>();

        List<INode> postjobNodes = new ArrayList<INode>();

        List<INode> hadoopConfNodes = new ArrayList<INode>();
        Set<String> hadoopClusterIds = new HashSet<String>();

        INode implicit_Context = null;

        for (INode rootNode : rootNodes) {
            String componentName = rootNode.getComponent().getName();
            String uniqueName = rootNode.getUniqueName();

            if (componentName.equals("tPrejob")) {
                prejobNodes.add(rootNode);
                continue;
            }

            if (componentName.equals("tPostjob")) {
                postjobNodes.add(rootNode);
                continue;
            }

            if (componentName.equals("tHadoopConfManager")) {
                hadoopConfNodes.add(rootNode);
                org.talend.core.model.process.IElementParameter ciParam = rootNode.getElementParameter("CLUSTER_ID");
                if (ciParam != null) {
                    Object repIdObj = ciParam.getValue();
                    if (repIdObj != null) {
                        hadoopClusterIds.add(String.valueOf(repIdObj));
                    }
                }
                continue;
            }

            if (uniqueName.startsWith("Implicit_Context_")) {
                implicit_Context = rootNode;
                continue;
            }
        }

    stringBuffer.append(TEXT_141);
    if (hadoopClusterIds.size() > 1) {
    stringBuffer.append(TEXT_147);
    }
    stringBuffer.append(TEXT_141);
    
// run tHadoopConfManager component if there is it.
 if(!hadoopConfNodes.isEmpty()) {
    INode hadoopConfNode = hadoopConfNodes.get(0);

    stringBuffer.append(TEXT_141);
    stringBuffer.append(createCallProcess(hadoopConfNode, className, false) );
    
 }

    stringBuffer.append(TEXT_141);
    
//2. load implicit contextload
 if(implicit_Context != null) {

    stringBuffer.append(TEXT_141);
    stringBuffer.append(createCallProcess(implicit_Context, className, false) );
    
 }

    stringBuffer.append(TEXT_148);
    
//3. run pre-job
 if(!prejobNodes.isEmpty()) {
    for(INode preNode:prejobNodes) {

    stringBuffer.append(TEXT_141);
    stringBuffer.append(createCallProcess(preNode, className, false) );
    
    }
 }

    stringBuffer.append(TEXT_149);
    
        //4. flush the begin msg
        for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_150);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_152);
    
        }
        
        if(jobCatcherNode!=null) {

    stringBuffer.append(TEXT_153);
    stringBuffer.append(jobCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(jobCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_155);
    
        }

    stringBuffer.append(TEXT_156);
    

    //5. all others sub-job (MultiThread mode)
    if(isRunInMultiThread){
        
    stringBuffer.append(TEXT_157);
    
        for (INode rootNode : rootNodes) {
            String componentName = rootNode.getComponent().getName();
            String uniqueName = rootNode.getUniqueName();

            //filter the special subjob
            if("tPrejob".equals(componentName)) continue;
            if("tPostjob".equals(componentName)) continue;
            if("tHadoopConfManager".equals(componentName)) continue;

            //bug16808 when use parallel option at Output components, that will multi thread
            if(componentName.startsWith("tAsyncIn")) continue;
            //end bug16808

            if(implicit_Context!=null && implicit_Context.getUniqueName().equals(uniqueName)) continue;

            if (!componentName.equals("tJobStructureCatcher") && !componentName.equals("tLogCatcher") && !componentName.equals("tFlowMeterCatcher") && !componentName.equals("tAssertCatcher") && !componentName.equals("tStatCatcher")) {

    stringBuffer.append(TEXT_158);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(createCallProcess(rootNode, className, true) );
    stringBuffer.append(TEXT_160);
    
            }
        }

    stringBuffer.append(TEXT_161);
    
    } else { // isRunInMultiThread  //5. all others sub-job (SingleThread  mode)
        for (INode rootNode : rootNodes) {
            String componentName = rootNode.getComponent().getName();
            String uniqueName = rootNode.getUniqueName();

            //filter the special subjob
            if("tPrejob".equals(componentName)) continue;
            if("tPostjob".equals(componentName)) continue;
            if("tHadoopConfManager".equals(componentName)) continue;
            if(implicit_Context!=null && implicit_Context.getUniqueName().equals(uniqueName)) continue;

            if (!componentName.equals("tJobStructureCatcher") && !componentName.equals("tLogCatcher") && !componentName.equals("tFlowMeterCatcher") && !componentName.equals("tAssertCatcher") && !componentName.equals("tStatCatcher") && !componentName.equals("tAsyncIn")) {

    stringBuffer.append(TEXT_141);
    stringBuffer.append(createCallProcess(rootNode, className, false) );
        }
    }
}// end if(isRunInMultiThread)

    stringBuffer.append(TEXT_162);
    
//6. run the post-job
 if(!postjobNodes.isEmpty()){
    for(INode postNode:postjobNodes){

    stringBuffer.append(TEXT_141);
    stringBuffer.append(createCallProcess(postNode, className, false) );
    
    }
 }

    stringBuffer.append(TEXT_141);
    stringBuffer.append(TEXT_141);
    stringBuffer.append( ElementParameterParser.getValue(process, "__FOOTER_CODE__"));
    stringBuffer.append(TEXT_163);
    stringBuffer.append(className );
    stringBuffer.append(TEXT_164);
    
    //7. send & flush the end msg to statcatcher
    for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_152);
    
    }
    
    if(jobCatcherNode!=null) {

    stringBuffer.append(TEXT_153);
    stringBuffer.append(jobCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(jobCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_155);
    
    }

    stringBuffer.append(TEXT_141);
     if (exist_tMOM) { 
    stringBuffer.append(TEXT_167);
     } 
    stringBuffer.append(TEXT_141);
     if (exist_tSCP) { 
    stringBuffer.append(TEXT_168);
     } 
    stringBuffer.append(TEXT_141);
    
    if (stats) {

    stringBuffer.append(TEXT_169);
    
    }

    if (trace) {

    stringBuffer.append(TEXT_170);
    
    }

    stringBuffer.append(TEXT_171);
     if(exist_tMOM) { 
    stringBuffer.append(TEXT_167);
     } 
     if(exist_tSCP) { 
    stringBuffer.append(TEXT_168);
     } 
     if (exist_tSQLDB) { 
    stringBuffer.append(TEXT_172);
     } 
     if (exist_tCassandra) { 
    stringBuffer.append(TEXT_173);
     } 
     if (exist_tHBase) { 
    stringBuffer.append(TEXT_174);
     } 
     if (exist_tFtp) { 
    stringBuffer.append(TEXT_175);
     } 
     if (exist_tS3) { 
    stringBuffer.append(TEXT_176);
     } 
     if (exist_tLDAP) { 
    stringBuffer.append(TEXT_177);
     } 
     if (exist_tMDM) { 
    stringBuffer.append(TEXT_178);
     } 
     if (exist_tPalo) { 
    stringBuffer.append(TEXT_179);
     } 
     if (exist_tEXist) { 
    stringBuffer.append(TEXT_180);
     } 
     if (exist_tGS) { 
    stringBuffer.append(TEXT_181);
     } 
    stringBuffer.append(TEXT_182);
    
    if(exist_tMOM) {

    stringBuffer.append(TEXT_183);
    
            for (INode momNode : momComponentsList) {
            // fix for TESB-16026
            if (!(Boolean.valueOf(ElementParameterParser.getValue(momNode, "__USE_CONNECTION__")) || Boolean.valueOf(ElementParameterParser.getValue(momNode, "__USE_SHARED_CONNECTION__")))) {
            // end fix for TESB-16026

    stringBuffer.append(TEXT_184);
    stringBuffer.append(momNode.getUniqueName() );
    stringBuffer.append(TEXT_185);
    
            }
            // fix for TESB-16026
            }
            // end fix for TESB-16026

    stringBuffer.append(TEXT_186);
    
    }

    stringBuffer.append(TEXT_141);
    
    if(exist_tSCP) {

    stringBuffer.append(TEXT_187);
    
            for (INode scpNode : scpComponentsList) {

    stringBuffer.append(TEXT_188);
    stringBuffer.append(scpNode.getUniqueName() );
    stringBuffer.append(TEXT_189);
    
            }

    stringBuffer.append(TEXT_186);
    
    }

    stringBuffer.append(TEXT_141);
      if(exist_tSQLDB) { 
    stringBuffer.append(TEXT_190);
        for (INode sqlDbComponent : sqlDbComponentsList) { 
    stringBuffer.append(TEXT_188);
    stringBuffer.append(sqlDbComponent.getUniqueName() );
    stringBuffer.append(TEXT_191);
        } 
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_141);
     if (exist_tCassandra) { 
    stringBuffer.append(TEXT_192);
      for (INode cassandraDbNode : cassandraComponentsList) {
        if((new API_selector()).useDatastax(cassandraDbNode)) { 
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cassandraDbNode.getUniqueName());
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cassandraDbNode.getUniqueName());
    stringBuffer.append(TEXT_195);
          } else { 
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cassandraDbNode.getUniqueName());
    stringBuffer.append(TEXT_197);
          }
    }
    stringBuffer.append(TEXT_186);
       }//End if

    stringBuffer.append(TEXT_141);
      if(exist_tHBase) { 
    stringBuffer.append(TEXT_198);
    
	for (INode hbaseComponent : hbaseComponentsList) {

        String hbaseDistribution = ElementParameterParser.getValue(hbaseComponent, "__DISTRIBUTION__");
	    String hbaseVersion = ElementParameterParser.getValue(hbaseComponent, "__HBASE_VERSION__");

        org.talend.hadoop.distribution.component.HBaseComponent hbaseDistrib = null;
        try {
            hbaseDistrib = (org.talend.hadoop.distribution.component.HBaseComponent) org.talend.hadoop.distribution.DistributionFactory.buildDistribution(hbaseDistribution, hbaseVersion);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            return "";
        }

        boolean isCustom = hbaseDistrib instanceof org.talend.hadoop.distribution.custom.CustomDistribution;

	    boolean isHadoop2 = "HADOOP_2".equals(ElementParameterParser.getValue(hbaseComponent, "__HADOOP_CUSTOM_VERSION__"));
    stringBuffer.append(TEXT_199);
    stringBuffer.append(hbaseComponent.getUniqueName());
    stringBuffer.append(TEXT_200);
    stringBuffer.append(hbaseComponent.getUniqueName());
    stringBuffer.append(TEXT_201);
    stringBuffer.append(hbaseComponent.getUniqueName());
    stringBuffer.append(TEXT_202);
    
		        if((isCustom && isHadoop2) || (!isCustom && hbaseDistrib.doSupportNewHBaseAPI())) {
		            if (!hbaseDistrib.doSupportHBase2x()) {
    stringBuffer.append(TEXT_203);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_37);
     } else {
    stringBuffer.append(TEXT_205);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_37);
     }
    stringBuffer.append(TEXT_207);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_210);
    } else {
    stringBuffer.append(TEXT_211);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_212);
    }
    stringBuffer.append(TEXT_213);
    stringBuffer.append(hbaseComponent.getUniqueName() );
    stringBuffer.append(TEXT_214);
    }
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_141);
     if (exist_tFtp) { 
    stringBuffer.append(TEXT_215);
      for (INode ftpNode : ftpComponentsList) { 
    stringBuffer.append(TEXT_188);
    stringBuffer.append(ftpNode.getUniqueName() );
    stringBuffer.append(TEXT_216);
              String type;
            if (("true").equals(ElementParameterParser.getValue(ftpNode, "__SFTP__"))) {
                type = "sftp";
            } else if (("true").equals(ElementParameterParser.getValue(ftpNode, "__FTPS__"))) {
                type = "ftps";
            } else {
                type = "ftp";
            }
            if ("ftp".equals(type)) { 
    stringBuffer.append(TEXT_217);
    } else if ("sftp".equals(type)) { 
    stringBuffer.append(TEXT_218);
    } else { 
    stringBuffer.append(TEXT_219);
    }
    stringBuffer.append(TEXT_220);
      } 
    stringBuffer.append(TEXT_186);
    }
    stringBuffer.append(TEXT_141);
      if(exist_tS3) { 
    stringBuffer.append(TEXT_221);
        for (INode s3Component : s3ComponentsList) { 
    stringBuffer.append(TEXT_222);
    stringBuffer.append(s3Component.getUniqueName() );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(s3Component.getUniqueName() );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(s3Component.getUniqueName() );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(s3Component.getUniqueName() );
    stringBuffer.append(TEXT_226);
        } 
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_141);
     if (exist_tLDAP) { 
    stringBuffer.append(TEXT_227);
      for (INode ldapNode : ldapComponentsList) { 
    stringBuffer.append(TEXT_188);
    stringBuffer.append(ldapNode.getUniqueName() );
    stringBuffer.append(TEXT_228);
      } 
    stringBuffer.append(TEXT_186);
    }
    stringBuffer.append(TEXT_141);
      if(exist_tMDM) { 
    stringBuffer.append(TEXT_229);
      } 
    stringBuffer.append(TEXT_149);
      if(exist_tPalo) { 
    stringBuffer.append(TEXT_230);
        for (INode paloComponent : paloComponentsList) { 
    stringBuffer.append(TEXT_231);
    stringBuffer.append(paloComponent.getUniqueName());
    stringBuffer.append(TEXT_232);
    stringBuffer.append(paloComponent.getUniqueName());
    stringBuffer.append(TEXT_233);
    stringBuffer.append(paloComponent.getUniqueName());
    stringBuffer.append(TEXT_234);
    stringBuffer.append(paloComponent.getUniqueName() );
    stringBuffer.append(TEXT_235);
        } 
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_141);
      if(exist_tEXist) { 
    stringBuffer.append(TEXT_236);
        for (INode existComponent : existComponentsList) { 
    stringBuffer.append(TEXT_237);
    stringBuffer.append(existComponent.getUniqueName());
    stringBuffer.append(TEXT_238);
    stringBuffer.append(existComponent.getUniqueName());
    stringBuffer.append(TEXT_239);
    stringBuffer.append(existComponent.getUniqueName());
    stringBuffer.append(TEXT_240);
    stringBuffer.append(existComponent.getUniqueName());
    stringBuffer.append(TEXT_241);
        } 
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_141);
      if(exist_tGS) { 
    stringBuffer.append(TEXT_242);
        for (INode gsComponent : gsComponentsList) { 
    stringBuffer.append(TEXT_243);
    stringBuffer.append(gsComponent.getUniqueName());
    stringBuffer.append(TEXT_244);
    stringBuffer.append(gsComponent.getUniqueName());
    stringBuffer.append(TEXT_245);
    stringBuffer.append(gsComponent.getUniqueName());
    stringBuffer.append(TEXT_246);
    stringBuffer.append(gsComponent.getUniqueName());
    stringBuffer.append(TEXT_247);
        } 
    stringBuffer.append(TEXT_186);
      } 
    stringBuffer.append(TEXT_248);
    
        for (INode momNode : momComponentsList) {

    stringBuffer.append(TEXT_249);
    stringBuffer.append(momNode.getUniqueName() );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(momNode.getUniqueName() );
    stringBuffer.append(TEXT_251);
    stringBuffer.append(momNode.getUniqueName() );
    stringBuffer.append(TEXT_252);
    stringBuffer.append(momNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
    
        }

    
        for (INode scpNode : scpComponentsList) {

    stringBuffer.append(TEXT_254);
    stringBuffer.append(scpNode.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(scpNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
    
        }

    
        for (INode sqlDbNode : sqlDbComponentsList) {

    stringBuffer.append(TEXT_254);
    stringBuffer.append(sqlDbNode.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(sqlDbNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
    
        }

    

        for (INode cassandraDbNode : cassandraComponentsList) {
            if((new API_selector()).useDatastax(cassandraDbNode)) { 
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cassandraDbNode.getUniqueName() );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(cassandraDbNode.getUniqueName() );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cassandraDbNode.getUniqueName() );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cassandraDbNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
              } else { 
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cassandraDbNode.getUniqueName() );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cassandraDbNode.getUniqueName());
    stringBuffer.append(TEXT_253);
              }
        }

    stringBuffer.append(TEXT_141);
    
        for (INode hbaseNode : hbaseComponentsList) {

    stringBuffer.append(TEXT_254);
    stringBuffer.append(hbaseNode.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(hbaseNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
    
        }

    stringBuffer.append(TEXT_141);
          for (INode ftpNode : ftpComponentsList) { 
    stringBuffer.append(TEXT_254);
    stringBuffer.append(ftpNode.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(ftpNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
          } 
    
        for (INode s3Node : s3ComponentsList) {

    stringBuffer.append(TEXT_254);
    stringBuffer.append(s3Node.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(s3Node.getUniqueName() );
    stringBuffer.append(TEXT_253);
    
        }

          for (INode ldapNode : ldapComponentsList) { 
    stringBuffer.append(TEXT_254);
    stringBuffer.append(ldapNode.getUniqueName() );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(ldapNode.getUniqueName() );
    stringBuffer.append(TEXT_259);
    stringBuffer.append(ldapNode.getUniqueName() );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(ldapNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
          } 
    stringBuffer.append(TEXT_141);
          for (INode mdmNode : mdmComponentsList) {
    stringBuffer.append(TEXT_261);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_263);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_269);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_270);
    stringBuffer.append(mdmNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
          } 
    stringBuffer.append(TEXT_141);
          for (INode paloNode : paloComponentsList) { 
    stringBuffer.append(TEXT_271);
    stringBuffer.append(paloNode.getUniqueName() );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(paloNode.getUniqueName() );
    stringBuffer.append(TEXT_273);
    stringBuffer.append(paloNode.getUniqueName() );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(paloNode.getUniqueName() );
    stringBuffer.append(TEXT_253);
          } 
          for (INode existNode : existComponentsList) { 
    stringBuffer.append(TEXT_275);
    stringBuffer.append(existNode.getUniqueName());
    stringBuffer.append(TEXT_276);
    stringBuffer.append(existNode.getUniqueName());
    stringBuffer.append(TEXT_253);
          } 
    stringBuffer.append(TEXT_141);
          for (INode salesforceNode : salesforceComponentsList) { 
    stringBuffer.append(TEXT_254);
    stringBuffer.append(salesforceNode.getUniqueName());
    stringBuffer.append(TEXT_255);
    stringBuffer.append(salesforceNode.getUniqueName());
    stringBuffer.append(TEXT_253);
          } 
          for (INode sapNode : sapComponentsList) { 
    stringBuffer.append(TEXT_254);
    stringBuffer.append(sapNode.getUniqueName());
    stringBuffer.append(TEXT_255);
    stringBuffer.append(sapNode.getUniqueName());
    stringBuffer.append(TEXT_253);
          } 
          for (INode gsNode : gsComponentsList) { 
    stringBuffer.append(TEXT_277);
    stringBuffer.append(gsNode.getUniqueName());
    stringBuffer.append(TEXT_278);
    stringBuffer.append(gsNode.getUniqueName());
    stringBuffer.append(TEXT_253);
          } 
    stringBuffer.append(TEXT_279);
    stringBuffer.append(TEXT_141);
    return stringBuffer.toString();
  }
}