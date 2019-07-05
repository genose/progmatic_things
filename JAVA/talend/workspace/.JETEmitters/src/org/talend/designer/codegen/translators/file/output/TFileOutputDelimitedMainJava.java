package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.process.AbstractNode;

public class TFileOutputDelimitedMainJava
{
  protected static String nl;
  public static synchronized TFileOutputDelimitedMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputDelimitedMainJava result = new TFileOutputDelimitedMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "                    StringBuilder sb_";
  protected final String TEXT_4 = " = new StringBuilder();";
  protected final String TEXT_5 = NL + "                synchronized (multiThreadLockWrite) {";
  protected final String TEXT_6 = NL + "                synchronized ((Object[])globalMap.get(\"lockWrite_";
  protected final String TEXT_7 = "\")) {";
  protected final String TEXT_8 = NL + "                Object[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "                synchronized (pLockWrite) {";
  protected final String TEXT_9 = NL + "                    if(isFirstCheckDyn_";
  protected final String TEXT_10 = " ";
  protected final String TEXT_11 = "&& (file_";
  protected final String TEXT_12 = ".length()==0)";
  protected final String TEXT_13 = "){";
  protected final String TEXT_14 = "&& file";
  protected final String TEXT_15 = ".length()==0";
  protected final String TEXT_16 = NL + "                                    fileOutputDelimitedUtil_";
  protected final String TEXT_17 = ".putHeaderValue_";
  protected final String TEXT_18 = "(out";
  protected final String TEXT_19 = ",OUT_DELIM_";
  protected final String TEXT_20 = ", ";
  protected final String TEXT_21 = ");";
  protected final String TEXT_22 = NL + "                        out";
  protected final String TEXT_23 = ".write(\"";
  protected final String TEXT_24 = "\");";
  protected final String TEXT_25 = NL + "                        routines.system.DynamicUtils.writeHeaderToDelimitedFile(";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ", out";
  protected final String TEXT_28 = ", OUT_DELIM_";
  protected final String TEXT_29 = ".write(OUT_DELIM_";
  protected final String TEXT_30 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_31 = ");" + NL + "                        isFirstCheckDyn_";
  protected final String TEXT_32 = " = false;" + NL + "                    }";
  protected final String TEXT_33 = NL + "                }";
  protected final String TEXT_34 = NL + "                            fileOutputDelimitedUtil_";
  protected final String TEXT_35 = ".putValue_";
  protected final String TEXT_36 = "(";
  protected final String TEXT_37 = ",sb_";
  protected final String TEXT_38 = NL + "                            if(";
  protected final String TEXT_39 = " != null) {";
  protected final String TEXT_40 = NL + "                            routines.system.DynamicUtils.writeValuesToStringBuilder(";
  protected final String TEXT_41 = ", sb_";
  protected final String TEXT_42 = NL + "\t\t\t\t\t\t\troutines.system.DynamicUtils.writeValuesToStringBuilder(";
  protected final String TEXT_43 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_44 = NL + "                        sb_";
  protected final String TEXT_45 = ".append(";
  protected final String TEXT_46 = NL + "                            FormatterUtils.format_Date(";
  protected final String TEXT_47 = ")";
  protected final String TEXT_48 = NL + "                                    FormatterUtils.format_Number(";
  protected final String TEXT_49 = ".toPlainString(), ";
  protected final String TEXT_50 = NL + "                                    FormatterUtils.format_Number(new java.math.BigDecimal(String.valueOf(";
  protected final String TEXT_51 = ")).toPlainString(), ";
  protected final String TEXT_52 = NL + "                            ";
  protected final String TEXT_53 = ".toPlainString()";
  protected final String TEXT_54 = NL + "                            java.nio.charset.Charset.forName(";
  protected final String TEXT_55 = ").decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_56 = ")).toString()";
  protected final String TEXT_57 = NL + "                        );";
  protected final String TEXT_58 = NL + "                            }";
  protected final String TEXT_59 = NL + "                            sb_";
  protected final String TEXT_60 = ".append(OUT_DELIM_";
  protected final String TEXT_61 = NL + "                    sb_";
  protected final String TEXT_62 = ".append(OUT_DELIM_ROWSEP_";
  protected final String TEXT_63 = ");" + NL + NL;
  protected final String TEXT_64 = NL + "                    synchronized (multiThreadLockWrite) {";
  protected final String TEXT_65 = NL + "                    synchronized ((Object[])globalMap.get(\"lockWrite_";
  protected final String TEXT_66 = NL + "                    Object[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "                    synchronized (pLockWrite) {";
  protected final String TEXT_67 = NL + "                    nb_line_";
  protected final String TEXT_68 = "++;" + NL + "                    resourceMap.put(\"nb_line_";
  protected final String TEXT_69 = "\", nb_line_";
  protected final String TEXT_70 = NL + "                        if(currentRow_";
  protected final String TEXT_71 = " % splitEvery_";
  protected final String TEXT_72 = "==0 && currentRow_";
  protected final String TEXT_73 = "!=0){" + NL + "                            splitedFileNo_";
  protected final String TEXT_74 = "++;" + NL + "                            out";
  protected final String TEXT_75 = ".close();" + NL + "                            //close original outputStream" + NL + "" + NL + "                            file";
  protected final String TEXT_76 = " = new java.io.File(fullName_";
  protected final String TEXT_77 = " + splitedFileNo_";
  protected final String TEXT_78 = " + extension_";
  protected final String TEXT_79 = ");" + NL;
  protected final String TEXT_80 = NL + "                            if(file";
  protected final String TEXT_81 = ".exists()) {" + NL + "                                file";
  protected final String TEXT_82 = ".delete();" + NL + "                            }";
  protected final String TEXT_83 = NL + "                            out";
  protected final String TEXT_84 = " = new ";
  protected final String TEXT_85 = "(new java.io.OutputStreamWriter(" + NL + "                                    new java.io.FileOutputStream(fullName_";
  protected final String TEXT_86 = "),";
  protected final String TEXT_87 = "));";
  protected final String TEXT_88 = NL + "                                if(file";
  protected final String TEXT_89 = ".length()==0){";
  protected final String TEXT_90 = NL + "                                        fileOutputDelimitedUtil_";
  protected final String TEXT_91 = NL + "                                        out";
  protected final String TEXT_92 = NL + "                                        routines.system.DynamicUtils.writeHeaderToDelimitedFile(";
  protected final String TEXT_93 = NL + "                                            out";
  protected final String TEXT_94 = NL + "                                    out";
  protected final String TEXT_95 = ");" + NL + "                                }";
  protected final String TEXT_96 = ".write(sb_";
  protected final String TEXT_97 = ".toString());";
  protected final String TEXT_98 = NL + "                                if(nb_line_";
  protected final String TEXT_99 = "%";
  protected final String TEXT_100 = " == 0) {" + NL + "                                out";
  protected final String TEXT_101 = ".flush();" + NL + "                                }";
  protected final String TEXT_102 = NL + "                        }else{" + NL + "                            out";
  protected final String TEXT_103 = NL + "                        }" + NL + "                        currentRow_";
  protected final String TEXT_104 = "++;" + NL + NL;
  protected final String TEXT_105 = NL + NL + "                        out";
  protected final String TEXT_106 = NL + "                        log.debug(\"";
  protected final String TEXT_107 = " - Writing the record \" + nb_line_";
  protected final String TEXT_108 = " + \".\");";
  protected final String TEXT_109 = NL + "                    }";
  protected final String TEXT_110 = NL + "                dynamic_column_count_";
  protected final String TEXT_111 = " = 1;";
  protected final String TEXT_112 = NL + "                        if(isFirstCheckDyn_";
  protected final String TEXT_113 = "){" + NL + "                            headColu";
  protected final String TEXT_114 = " = new String[";
  protected final String TEXT_115 = "-1+";
  protected final String TEXT_116 = ".getColumnCount()];";
  protected final String TEXT_117 = NL + "                                    dynamic_column_count_";
  protected final String TEXT_118 = " = ";
  protected final String TEXT_119 = ".getColumnCount();";
  protected final String TEXT_120 = "(headColu";
  protected final String TEXT_121 = ",dynamic_column_count_";
  protected final String TEXT_122 = NL + "                            headColu";
  protected final String TEXT_123 = "[";
  protected final String TEXT_124 = "+dynamic_column_count_";
  protected final String TEXT_125 = "]=\"";
  protected final String TEXT_126 = "\";";
  protected final String TEXT_127 = NL + "                             for(int mi=0;mi<dynamic_column_count_";
  protected final String TEXT_128 = ";mi++){" + NL + "                                headColu";
  protected final String TEXT_129 = "+mi]=";
  protected final String TEXT_130 = ".getColumnMetadata(mi).getName();" + NL + "                             }";
  protected final String TEXT_131 = NL + "                            CsvWriter";
  protected final String TEXT_132 = ".writeNext(headColu";
  protected final String TEXT_133 = ");" + NL + "                            CsvWriter";
  protected final String TEXT_134 = ".flush();";
  protected final String TEXT_135 = ".write(strWriter";
  protected final String TEXT_136 = ".getBuffer().toString());" + NL + "                            out";
  protected final String TEXT_137 = ".flush();" + NL + "                            strWriter";
  protected final String TEXT_138 = ".getBuffer().delete(0, strWriter";
  protected final String TEXT_139 = ".getBuffer().length());";
  protected final String TEXT_140 = NL + "                        }";
  protected final String TEXT_141 = "){" + NL + "                            CsvWriter";
  protected final String TEXT_142 = ".setEscapeChar(csvSettings_";
  protected final String TEXT_143 = ".getEscapeChar());" + NL + "                            CsvWriter";
  protected final String TEXT_144 = ".setQuoteChar(csvSettings_";
  protected final String TEXT_145 = ".getTextEnclosure());" + NL + "                            CsvWriter";
  protected final String TEXT_146 = ".setQuoteStatus(com.talend.csv.CSVWriter.QuoteStatus.FORCE);" + NL + "                            isFirstCheckDyn_";
  protected final String TEXT_147 = " = false;" + NL + "                        }";
  protected final String TEXT_148 = NL + "                        String[] row";
  protected final String TEXT_149 = "=new String[";
  protected final String TEXT_150 = "];";
  protected final String TEXT_151 = NL + "                        dynamic_column_count_";
  protected final String TEXT_152 = " =1;";
  protected final String TEXT_153 = NL + "                                dynamic_column_count_";
  protected final String TEXT_154 = NL + "                                fileOutputDelimitedUtil_";
  protected final String TEXT_155 = ",row";
  protected final String TEXT_156 = NL + "                            if (";
  protected final String TEXT_157 = " != null) {" + NL + "                                routines.system.DynamicUtils.writeValuesToStringArrayEnhance(";
  protected final String TEXT_158 = ", row";
  protected final String TEXT_159 = ",";
  protected final String TEXT_160 = NL + "                                           \"";
  protected final String TEXT_161 = "\"";
  protected final String TEXT_162 = NL + "                                           null";
  protected final String TEXT_163 = NL + "                                );" + NL + "                            }";
  protected final String TEXT_164 = NL + "                            row";
  protected final String TEXT_165 = "]=";
  protected final String TEXT_166 = ";";
  protected final String TEXT_167 = "FormatterUtils.format_Date(";
  protected final String TEXT_168 = "java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_169 = ")).toString();";
  protected final String TEXT_170 = "FormatterUtils.format_Number(";
  protected final String TEXT_171 = "FormatterUtils.format_Number(new java.math.BigDecimal(String.valueOf(";
  protected final String TEXT_172 = ".toPlainString();";
  protected final String TEXT_173 = "String.valueOf(";
  protected final String TEXT_174 = NL + "                nb_line_";
  protected final String TEXT_175 = "++;" + NL + "                resourceMap.put(\"nb_line_";
  protected final String TEXT_176 = NL + "                            if(currentRow_";
  protected final String TEXT_177 = "!=0){" + NL + "                                splitedFileNo_";
  protected final String TEXT_178 = "++;" + NL + "                                CsvWriter";
  protected final String TEXT_179 = ".close();" + NL + "                                //close original outputStream" + NL;
  protected final String TEXT_180 = NL + "                                java.io.File fileToDelete_";
  protected final String TEXT_181 = ");" + NL + "                                if(fileToDelete_";
  protected final String TEXT_182 = ".exists()) {" + NL + "                                    fileToDelete_";
  protected final String TEXT_183 = ".delete();" + NL + "                                }";
  protected final String TEXT_184 = NL + "                                out";
  protected final String TEXT_185 = ".close();" + NL + "                                out";
  protected final String TEXT_186 = " = new routines.system.BufferedOutput(new java.io.OutputStreamWriter(" + NL + "                                    new java.io.FileOutputStream(fullName_";
  protected final String TEXT_187 = "));" + NL + "                                strWriter";
  protected final String TEXT_188 = " = new java.io.StringWriter();" + NL + "                                CsvWriter";
  protected final String TEXT_189 = " = new com.talend.csv.CSVWriter(strWriter";
  protected final String TEXT_190 = ");" + NL + "                                CsvWriter";
  protected final String TEXT_191 = ".setSeparator(csvSettings_";
  protected final String TEXT_192 = ".getFieldDelim());";
  protected final String TEXT_193 = NL + "                                CsvWriter";
  protected final String TEXT_194 = " = new com.talend.csv.CSVWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "                                    new java.io.FileOutputStream(fullName_";
  protected final String TEXT_195 = ")));" + NL + "                                CsvWriter";
  protected final String TEXT_196 = NL + "                                if(csvSettings_";
  protected final String TEXT_197 = ".isUseCRLFRecordDelimiter()) {" + NL + "                                    CsvWriter";
  protected final String TEXT_198 = ".setLineEnd(\"\\r\\n\");" + NL + "                                } else {" + NL + "                                    CsvWriter";
  protected final String TEXT_199 = ".setLineEnd(\"\"+csvSettings_";
  protected final String TEXT_200 = ".getRowDelim());" + NL + "                                }";
  protected final String TEXT_201 = NL + "                                if(!csvSettings_";
  protected final String TEXT_202 = ".isUseCRLFRecordDelimiter() && csvSettings_";
  protected final String TEXT_203 = ".getRowDelim()!='\\r' && csvSettings_";
  protected final String TEXT_204 = ".getRowDelim()!='\\n') {" + NL + "                                    CsvWriter";
  protected final String TEXT_205 = NL + NL + "                                //set header.";
  protected final String TEXT_206 = NL + "                                    CsvWriter";
  protected final String TEXT_207 = ".getBuffer().toString());" + NL + "                                    strWriter";
  protected final String TEXT_208 = NL + "                                    file";
  protected final String TEXT_209 = ");" + NL + "                                    if(file";
  protected final String TEXT_210 = ".length() == 0) {" + NL + "                                        CsvWriter";
  protected final String TEXT_211 = ".getBuffer().toString());" + NL + "                                        strWriter";
  protected final String TEXT_212 = NL + "                                    }";
  protected final String TEXT_213 = NL + "                                //initialize new CsvWriter information" + NL + "                                CsvWriter";
  protected final String TEXT_214 = ".getEscapeChar());" + NL + "                                CsvWriter";
  protected final String TEXT_215 = ".getTextEnclosure());" + NL + "                                CsvWriter";
  protected final String TEXT_216 = ".setQuoteStatus(com.talend.csv.CSVWriter.QuoteStatus.FORCE);" + NL + "" + NL + "                                           CsvWriter";
  protected final String TEXT_217 = ".writeNext(row";
  protected final String TEXT_218 = ".getBuffer().toString());" + NL + "                                strWriter";
  protected final String TEXT_219 = NL + "                                    if(nb_line_";
  protected final String TEXT_220 = " == 0) {";
  protected final String TEXT_221 = NL + NL + "                            }else{" + NL + "                                           CsvWriter";
  protected final String TEXT_222 = NL + "                            }" + NL + "                            currentRow_";
  protected final String TEXT_223 = "++;";
  protected final String TEXT_224 = NL + "                                       CsvWriter";
  protected final String TEXT_225 = ".writeNextEnhance(row";
  protected final String TEXT_226 = ",\"";
  protected final String TEXT_227 = ".getBuffer().toString());" + NL + "                            strWriter";
  protected final String TEXT_228 = NL + "                            log.debug(\"";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean isTSalesforceWaveOutputBulkExec = ((cid == null) ? false : (cid.startsWith("tSalesforceWaveOutputBulkExec")));
boolean useDoubleQuoteEnclosure = "true".equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))
                                  && ("\"\"\"").equals(ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__"));
boolean applyNA4Null = isTSalesforceWaveOutputBulkExec;
String str4Nil = "#N/A";

boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
int schemaOptNum=100;
String schemaOptNumStr=ElementParameterParser.getValue(node, "__SCHEMA_OPT_NUM__");
if(schemaOptNumStr!=null && !"".equals(schemaOptNumStr) && !"\"\"".equals(schemaOptNumStr)){
    schemaOptNum  = Integer.parseInt(schemaOptNumStr);
}
int dynamic_column_index=-1;
boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(node.getProcess(), "__LOG4J_ACTIVATE__"));
if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    stringBuffer.append(TEXT_2);
    

    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {


            String encoding = ElementParameterParser.getValue(
                node,
                "__ENCODING__"
            );

            boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));

            boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));

            boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));

            boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));

            boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__"));
            String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");

            String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
            boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
            String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
            String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);

            String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
            boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;

            boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));

            boolean hasDynamic = metadata.isDynamicSchema();

            List< ? extends IConnection> conns = node.getIncomingConnections();
            for (IConnection conn : conns) {
                if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                    List<IMetadataColumn> columns = metadata.getListColumns();
                    int sizeColumns = columns.size();
                    
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
                    if(isIncludeHeader && hasDynamic){
                        if(codeGenArgument.getIsRunInMultiThread()){
                
    stringBuffer.append(TEXT_5);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {
                
    stringBuffer.append(TEXT_6);
    stringBuffer.append(((AbstractNode)node).getParallelIterator());
    stringBuffer.append(TEXT_7);
    
                        }
                        if (isParallelize) {
                
    stringBuffer.append(TEXT_8);
    
                        }
                        if(!split && compress && !isAppend){
                        
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(!useStream){
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    }
    stringBuffer.append(TEXT_13);
    
                        }else{
                    
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(!useStream){
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    }
    stringBuffer.append(TEXT_13);
    
                        }
                        for (int i = 0; i < sizeColumns; i++) {
                            IMetadataColumn column = columns.get(i);
                            if(sizeColumns> schemaOptNum){
                                if(i%schemaOptNum==0){
                                
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(i/schemaOptNum);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_21);
    
                                }
                            }else{//AA
                            if(!("id_Dynamic".equals(column.getTalendType()))) {
                         
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_24);
    
                            }else{
                         
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                            }
                            if(i != sizeColumns - 1) {
                         
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                            }
                            }//AA
                        }
                        
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
                        if (isParallelize) {
                
    stringBuffer.append(TEXT_33);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {
                
    stringBuffer.append(TEXT_33);
    
                        }
                        if(codeGenArgument.getIsRunInMultiThread()){
                
    stringBuffer.append(TEXT_33);
    
                        }
                
    
                    }
                    for (int i = 0; i < sizeColumns; i++) {

                        IMetadataColumn column = columns.get(i);
                        JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                        boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
                        if(sizeColumns> schemaOptNum){
                            if(i%schemaOptNum==0){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(i/schemaOptNum);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                            }
                        }else{//BB
                        if(!isPrimitive) {
                            
    stringBuffer.append(TEXT_38);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_39);
    
                        }
                        if(column.getTalendType().equals("id_Dynamic")){
                            dynamic_column_index = i;
                            if(advancedSeparator){

    stringBuffer.append(TEXT_40);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_21);
    
							}else{
    stringBuffer.append(TEXT_42);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    }
                        }else{
                        
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    
                            String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
                            if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {
                            
    stringBuffer.append(TEXT_46);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_47);
    
                            } else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) {
                            
     if(javaType == JavaTypesManager.BIGDECIMAL) {
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_47);
     } else { 
    stringBuffer.append(TEXT_50);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_47);
     } 
    
                            } else if(javaType == JavaTypesManager.BIGDECIMAL){
                            
    stringBuffer.append(TEXT_52);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_53);
    
                            } else if(javaType == JavaTypesManager.BYTE_ARRAY){
                            
    stringBuffer.append(TEXT_54);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_56);
    
                            } else {
                            
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    
                            }
                        
    stringBuffer.append(TEXT_57);
    
                        }
                        if(!isPrimitive) {
                            
    stringBuffer.append(TEXT_58);
    
                        }
                        if(i != sizeColumns - 1) {
                            
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                        }
                        }//BB
                    }
                    
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    
                        if(codeGenArgument.getIsRunInMultiThread()){
                    
    stringBuffer.append(TEXT_64);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {
                    
    stringBuffer.append(TEXT_65);
    stringBuffer.append(((AbstractNode)node).getParallelIterator());
    stringBuffer.append(TEXT_7);
    
                        }
                        if (isParallelize) {
                    
    stringBuffer.append(TEXT_66);
    
                        }
                    
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
                    // add a prerequisite useStream to support output stream feature:8873
                    if(!useStream && split){
                        
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    if(!isAppend) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    }
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(isInRowMode?"routines.system.BufferedOutput":"java.io.BufferedWriter");
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_86);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_87);
    
                            if(isIncludeHeader){
                                
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    
                                    //List<IMetadataColumn> columns = metadata.getListColumns();
                                    //int sizeColumns = columns.size();
                                    for (int i = 0; i < sizeColumns; i++) {
                                        IMetadataColumn column = columns.get(i);
                                        if(sizeColumns> schemaOptNum){
                                            if(i%schemaOptNum==0){

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(i/schemaOptNum);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    if(hasDynamic){
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    }
    stringBuffer.append(TEXT_21);
    
                                            }
                                        }else{//CC
                                        if(!("id_Dynamic".equals(column.getTalendType()))) {
                                        
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_24);
    
                                        }else{
                                        
    stringBuffer.append(TEXT_92);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                                        }
                                        if(i != sizeColumns - 1) {
                                            
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
                                        }
                                        }//CC
                                    }
                                    
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    
                            }
                            
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    
                            if(flushOnRow) {
                                
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    
                            }
                            
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    
                            if(flushOnRow) {
                                
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    
                            }
                            
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    
                    } else {
                        
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    
                        if(flushOnRow) {
                            
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    
                        }
                        
    
                    }
                    
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    }
    stringBuffer.append(TEXT_2);
    
                        if (isParallelize) {
                    
    stringBuffer.append(TEXT_109);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {
                    
    stringBuffer.append(TEXT_109);
    
                        }
                        if(codeGenArgument.getIsRunInMultiThread()){
                    
    stringBuffer.append(TEXT_109);
    
                        }
                    
    stringBuffer.append(TEXT_2);
    
                }
            }
        }
    }
    
    stringBuffer.append(TEXT_2);
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}else{//the following is the tFileOutputCSV component
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    stringBuffer.append(TEXT_2);
    

    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {
            String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
            String delim = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
            boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));
            boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));

            boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));

            boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));

            boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__"));
            String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");

            String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
            boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
            String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
            String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);

            String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
            boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;

            List< ? extends IConnection> conns = node.getIncomingConnections();

            boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));

            boolean useOSLineSeparator = ("true").equals(ElementParameterParser.getValue(node,"__OS_LINE_SEPARATOR_AS_ROW_SEPARATOR__"));

            boolean hasDynamic = metadata.isDynamicSchema();
            IMetadataColumn dynamicCol = metadata.getDynamicColumn();
            if(hasDynamic){
                
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    
            }
            if(conns!=null){
                if (conns.size()>0){
                    IConnection conn =conns.get(0);
                    if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                        List<IMetadataColumn> columns = metadata.getListColumns();
                        int sizeColumns = columns.size();
                        if(isIncludeHeader && hasDynamic){

                            if(codeGenArgument.getIsRunInMultiThread()){
                            
    stringBuffer.append(TEXT_64);
    
                            }
                            if (((AbstractNode)node).getParallelIterator() != null) {
                            
    stringBuffer.append(TEXT_65);
    stringBuffer.append(((AbstractNode)node).getParallelIterator());
    stringBuffer.append(TEXT_7);
    
                            }
                            if (isParallelize) {
                            
    stringBuffer.append(TEXT_66);
    
                            }
                            if(!split && compress && !isAppend){
                            
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(!useStream){
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    }
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dynamicCol.getLabel());
    stringBuffer.append(TEXT_116);
    
                            }else{
                        
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(!useStream){
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    }
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dynamicCol.getLabel());
    stringBuffer.append(TEXT_116);
    
                            }
                            for (int i = 0; i < sizeColumns; i++) {
                                IMetadataColumn column = columns.get(i);
                                if(("id_Dynamic").equals(column.getTalendType())) {
                                    
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_119);
    
                                }
                                if(sizeColumns> schemaOptNum){
                                    if(i%schemaOptNum==0){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(i/schemaOptNum);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
                                    }
                                }else{//DD
                                if(!("id_Dynamic".equals(column.getTalendType()))) {
                                    if(i>dynamic_column_index&& dynamic_column_index != -1){
                             
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_126);
    
                                    }else{
                             
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_126);
    
                                    }
                                }else{
                                dynamic_column_index = i;
                             
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_130);
    
                                }
                                }//DD
                            }
                            
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    if(isInRowMode){
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
    stringBuffer.append(TEXT_140);
    
                            if (isParallelize) {
                        
    stringBuffer.append(TEXT_109);
    
                            }
                            if (((AbstractNode)node).getParallelIterator() != null) {
                        
    stringBuffer.append(TEXT_109);
    
                            }
                            if(codeGenArgument.getIsRunInMultiThread()){
                        
    stringBuffer.append(TEXT_109);
    
                            }
                        
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    
                        }
                        
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(hasDynamic?"+"+conn.getName()+".":"" );
    stringBuffer.append(dynamicCol==null?"":dynamicCol.getLabel()+".getColumnCount()-1" );
    stringBuffer.append(TEXT_150);
    
                        if(hasDynamic){
                        
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    
                        }
                        for (int i = 0; i < sizeColumns; i++) {
                            IMetadataColumn column = columns.get(i);
                            JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                            String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
                            boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable());
                            if(("id_Dynamic").equals(column.getTalendType())) {
                                
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dynamicCol.getLabel());
    stringBuffer.append(TEXT_119);
    
                            }
                            if(sizeColumns> schemaOptNum){
                                if(i%schemaOptNum==0){

    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(i/schemaOptNum);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    if(hasDynamic){
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    }
    stringBuffer.append(TEXT_21);
    
                                }
                            }else{//EE
                            if(("id_Dynamic").equals(column.getTalendType())) {
                                dynamic_column_index=i;
                            
    stringBuffer.append(TEXT_156);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_157);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dynamic_column_index);
    stringBuffer.append(TEXT_159);
    
                                   if(applyNA4Null){
                                        
    stringBuffer.append(TEXT_160);
    stringBuffer.append( str4Nil );
    stringBuffer.append(TEXT_161);
    
                                   }else{
                                        
    stringBuffer.append(TEXT_162);
    
                                   }
                                 
    stringBuffer.append(TEXT_163);
    
                            }else{
                                if(i>dynamic_column_index && dynamic_column_index !=-1){
                            
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(isPrimitive? "":conn.getName()+"."+column.getLabel()+ " == null ? null : ");
    
                                }else{
                            
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(isPrimitive? "":conn.getName()+"."+column.getLabel()+ " == null ? null : ");
    
                                }
                                if(javaType == JavaTypesManager.STRING ){
                            
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_166);
    
                                }else if(javaType == JavaTypesManager.DATE && pattern != null){
                            
    stringBuffer.append(TEXT_167);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_21);
    
                                }else if(javaType == JavaTypesManager.BYTE_ARRAY){
                            
    stringBuffer.append(TEXT_168);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_169);
    
                                } else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) {
                                    if(javaType == JavaTypesManager.BIGDECIMAL) {
    stringBuffer.append(TEXT_170);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_21);
          } else { 
    stringBuffer.append(TEXT_171);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_21);
          }
                                } else if (javaType == JavaTypesManager.BIGDECIMAL) {
                            
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_172);
    
                                } else{
                            
    stringBuffer.append(TEXT_173);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    
                                }
                            }
                            }//EE
                        }

                          if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_5);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(((AbstractNode)node).getParallelIterator());
    stringBuffer.append(TEXT_7);
    
                        }
                        if (isParallelize) {

    stringBuffer.append(TEXT_8);
    
                        }

    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
                        if(!useStream && split){
                            
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    if(!isAppend) {
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_183);
    }
    stringBuffer.append(TEXT_2);
    if(isInRowMode){
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    }else{
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    }
    stringBuffer.append(TEXT_2);
    
                                if(!useOSLineSeparator) {
                                
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    
                                } else {
                                
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    
                                }
                                
    stringBuffer.append(TEXT_205);
    
                                if(isIncludeHeader && !isAppend){
                                    
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    if(isInRowMode){
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
                                }
                                if(isIncludeHeader && isAppend){
                                    
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    if(isInRowMode){
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
    stringBuffer.append(TEXT_212);
    
                                }
                                
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    if(isInRowMode){
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
                                if(flushOnRow) {
                                    
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_220);
    if(isInRowMode){
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    }else{
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    }
    stringBuffer.append(TEXT_212);
    
                                }
                                
    stringBuffer.append(TEXT_221);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    if(isInRowMode){
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
                                if(flushOnRow) {
                                    
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_220);
    if(isInRowMode){
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    }else{
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    }
    stringBuffer.append(TEXT_212);
    
                                }
                                
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_223);
    
                        }else{
                            
    
                               if(applyNA4Null){
                                    
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_226);
    stringBuffer.append( str4Nil );
    stringBuffer.append(TEXT_24);
    
                               }else{
                                    
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
                               }
                             
    if(isInRowMode){
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
                            if(flushOnRow) {
                                
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_220);
    if(isInRowMode){
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    }else{
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    }
    stringBuffer.append(TEXT_212);
    
                            }
                            
    

                        }
                        
    if(isLog4jEnabled){
    stringBuffer.append(TEXT_228);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    }
    stringBuffer.append(TEXT_2);
    
                        if ( isParallelize) {

    stringBuffer.append(TEXT_33);
    
                        }
                        if (((AbstractNode)node).getParallelIterator() != null) {

    stringBuffer.append(TEXT_33);
    
                        }
                        if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_33);
    
                        }

    stringBuffer.append(TEXT_2);
    
                    }

                }
            }

        }

    }
    
    stringBuffer.append(TEXT_2);
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
