package org.talend.designer.codegen.translators.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.mapper.MapperComponent;
import org.talend.designer.mapper.external.data.ExternalMapperData;
import org.talend.designer.mapper.external.data.ExternalMapperTable;
import org.talend.designer.mapper.external.data.ExternalMapperTableEntry;
import org.talend.core.model.process.IConnection;
import org.talend.designer.mapper.language.ILanguage;
import org.talend.designer.mapper.language.generation.GenerationManagerFactory;
import org.talend.designer.mapper.language.generation.JavaGenerationManager;
import org.talend.designer.mapper.language.LanguageProvider;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.INode;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.mapper.model.tableentry.TableEntryLocation;
import org.talend.designer.mapper.utils.DataMapExpressionParser;

public class TMapBeginJava {


  protected static String nl;
  public static synchronized TMapBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMapBeginJava result = new TMapBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL;
  protected final String TEXT_2 = NL + NL + "// ###############################" + NL + "// # Lookup's keys initialization";
  protected final String TEXT_3 = NL + NL + "\tInteger currentThreadNumber_";
  protected final String TEXT_4 = " = null;  " + NL + "\t";
  protected final String TEXT_5 = NL + "\t\tsynchronized (";
  protected final String TEXT_6 = ".this.globalMap) {" + NL + "\t";
  protected final String TEXT_7 = ".this.obj) {" + NL + "\t";
  protected final String TEXT_8 = NL + "\t\t" + NL + "\t\t\tcurrentThreadNumber_";
  protected final String TEXT_9 = " = (Integer) ";
  protected final String TEXT_10 = ".this.globalMap.get(\"currentThreadNumber_";
  protected final String TEXT_11 = "\");" + NL + "\t\t" + NL + "\t\t\tif(currentThreadNumber_";
  protected final String TEXT_12 = " == null) {" + NL + "\t\t\t\tcurrentThreadNumber_";
  protected final String TEXT_13 = " = 1;" + NL + "\t\t\t} else {" + NL + "\t\t\t\tcurrentThreadNumber_";
  protected final String TEXT_14 = "++;" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_15 = ".this.globalMap.put(\"currentThreadNumber_";
  protected final String TEXT_16 = "\", currentThreadNumber_";
  protected final String TEXT_17 = ");" + NL + "\t" + NL + "\t\t}";
  protected final String TEXT_18 = NL + "\t\tint count_";
  protected final String TEXT_19 = "_";
  protected final String TEXT_20 = " = 0;" + NL + "\t\t";
  protected final String TEXT_21 = NL + NL + "\t\torg.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_22 = "LookupManager<";
  protected final String TEXT_23 = "Struct> tHash_Lookup_";
  protected final String TEXT_24 = " = " + NL + "\t\t\t(org.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_25 = "Struct>) " + NL + "\t\t\t\t((org.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_26 = "Struct>) " + NL + "\t\t\t\t\t globalMap.get( \"tHash_Lookup_";
  protected final String TEXT_27 = "\" ))" + NL + "\t\t\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t\t.clone()" + NL + "\t\t\t\t\t";
  protected final String TEXT_29 = ";" + NL + "\t";
  protected final String TEXT_30 = NL + "\t" + NL + "\t\torg.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = "null;";
  protected final String TEXT_33 = "(org.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_34 = "Struct>) " + NL + "\t\t\t\t((org.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_35 = "Struct>) " + NL + "\t\t\t\t\tglobalMap.get( \"tHash_Lookup_";
  protected final String TEXT_36 = ";";
  protected final String TEXT_37 = "\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "\t";
  protected final String TEXT_38 = "Struct> tHash_Lookup_Cache_";
  protected final String TEXT_39 = " = " + NL + "\t\t\torg.talend.designer.components.lookup.memory.AdvancedMemoryLookup." + NL + "\t\t\t\t<";
  protected final String TEXT_40 = "Struct>getLookup(org.talend.designer.components.lookup.common.ICommonLookup.MATCHING_MODE.";
  protected final String TEXT_41 = ");" + NL + "\t" + NL + "\t\torg.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_42 = "Struct> tHash_Lookup_Real_";
  protected final String TEXT_43 = " = null;" + NL + "\t" + NL + "\t";
  protected final String TEXT_44 = NL + "\t\ttHash_Lookup_";
  protected final String TEXT_45 = ".initGet();" + NL + "\t";
  protected final String TEXT_46 = " ";
  protected final String TEXT_47 = "HashKey = new ";
  protected final String TEXT_48 = "();";
  protected final String TEXT_49 = "Default = new ";
  protected final String TEXT_50 = NL + "// ###############################        " + NL + "" + NL + "// ###############################" + NL + "// # Vars initialization";
  protected final String TEXT_51 = NL + "class  ";
  protected final String TEXT_52 = "  {" + NL;
  protected final String TEXT_53 = "\t";
  protected final String TEXT_54 = ";" + NL;
  protected final String TEXT_55 = "}";
  protected final String TEXT_56 = " = new ";
  protected final String TEXT_57 = NL + "// ###############################" + NL + "" + NL + "// ###############################" + NL + "// # Outputs initialization";
  protected final String TEXT_58 = NL + "\t\t\t\tint count_";
  protected final String TEXT_59 = " = 0;" + NL + "\t\t\t\t";
  protected final String TEXT_60 = "_tmp = new ";
  protected final String TEXT_61 = NL + "// ###############################" + NL;
  protected final String TEXT_62 = NL + "\t\tList<";
  protected final String TEXT_63 = "Struct> ";
  protected final String TEXT_64 = "_List = new java.util.ArrayList<";
  protected final String TEXT_65 = "Struct>();";
  protected final String TEXT_66 = NL + "\t" + NL + "\t\tclass SortableRow_";
  protected final String TEXT_67 = " implements Comparable<SortableRow_";
  protected final String TEXT_68 = ">, routines.system.IPersistableRow<SortableRow_";
  protected final String TEXT_69 = "> { // G_TM_B_001 " + NL + "" + NL + "\t\t\tboolean is__rejectedInnerJoin;" + NL + "" + NL + "\t\t";
  protected final String TEXT_70 = " exprKey_";
  protected final String TEXT_71 = "__";
  protected final String TEXT_72 = ";" + NL + "                            \t";
  protected final String TEXT_73 = NL + "\t\t\t//";
  protected final String TEXT_74 = NL + "\t\t\t";
  protected final String TEXT_75 = NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t         public void fillFrom(";
  protected final String TEXT_76 = "Struct ";
  protected final String TEXT_77 = ") {" + NL + "" + NL + "\t\t\t\t\t";
  protected final String TEXT_78 = "this.";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = ";" + NL + "\t                \t\t";
  protected final String TEXT_81 = "this.exprKey_";
  protected final String TEXT_82 = " = exprKey_";
  protected final String TEXT_83 = ";" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_84 = NL + "                }" + NL + "" + NL + "                public void copyDataTo(";
  protected final String TEXT_85 = " = this.";
  protected final String TEXT_86 = ";" + NL + "\t                \t";
  protected final String TEXT_87 = NL + "                }" + NL + "" + NL + "\t\t" + NL + "    public String toString() {" + NL + "\t\t" + NL + "\t\tStringBuilder sb = new StringBuilder();" + NL + "\t\tsb.append(super.toString());" + NL + "\t\tsb.append(\"[\");" + NL + "\t\t";
  protected final String TEXT_88 = NL + "\t\t\t\t\t\t\tsb.append(\"";
  protected final String TEXT_89 = "\");" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_90 = NL + "\t\t\t\t\t\tsb.append(\"";
  protected final String TEXT_91 = "\");" + NL + "\t\t\t\t\t\tsb.append(\"=\");" + NL + "\t\t\t\t\t\tsb.append(String.valueOf(this.";
  protected final String TEXT_92 = "));" + NL + "\t\t\t\t\t";
  protected final String TEXT_93 = NL + "\t    sb.append(\"]\");" + NL + "\t    " + NL + "\t    return sb.toString();" + NL + "    }" + NL + "" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t        public int compareTo(SortableRow_";
  protected final String TEXT_94 = " other) {" + NL + "" + NL + "                    int returnValue = 0;" + NL + "                    ";
  protected final String TEXT_95 = NL + "                    " + NL + "                    \treturnValue = checkNullsAndCompare(this.exprKey_";
  protected final String TEXT_96 = ", other.exprKey_";
  protected final String TEXT_97 = ");" + NL + "                    \tif (returnValue != 0) {" + NL + "\t                        return returnValue;" + NL + "                    \t}" + NL + "                    ";
  protected final String TEXT_98 = NL + NL + "                    return returnValue;" + NL + "                }" + NL + "" + NL + "                private int checkNullsAndCompare(Object object1, Object object2) {" + NL + "                    int returnValue = 0;" + NL + "                    if (object1 instanceof Comparable && object2 instanceof Comparable) {" + NL + "                        returnValue = ((Comparable) object1).compareTo(object2);" + NL + "                    } else if (object1 != null && object2 != null) {" + NL + "                        returnValue = compareStrings(object1.toString(), object2" + NL + "                                .toString());" + NL + "                    } else if (object1 == null && object2 != null) {" + NL + "                        returnValue = 1;" + NL + "                    } else if (object1 != null && object2 == null) {" + NL + "                        returnValue = -1;" + NL + "                    } else {" + NL + "                        returnValue = 0;" + NL + "                    }" + NL + "" + NL + "                    return returnValue;" + NL + "                }" + NL + "" + NL + "                private int compareStrings(String string1, String string2) {" + NL + "                    return string1.compareTo(string2);" + NL + "                }" + NL + "" + NL + "" + NL + "\t\t" + NL + "    public void readData(ObjectInputStream dis) {" + NL + "" + NL + "\t\tsynchronized(";
  protected final String TEXT_99 = "Struct.commonByteArrayLock_";
  protected final String TEXT_100 = ") {" + NL + "" + NL + "\t        try {" + NL + "            " + NL + "\t\t\t\tint length = 0;" + NL + "\t\t\t" + NL + "\t\t\t\tthis.is__rejectedInnerJoin = dis.readBoolean();" + NL + "\t\t\t" + NL + "\t\t";
  protected final String TEXT_101 = NL + "\t\t\t            this.";
  protected final String TEXT_102 = " = dis.read";
  protected final String TEXT_103 = "();" + NL + "\t\t\t\t\t";
  protected final String TEXT_104 = NL + "\t\t\t            length = dis.readInt();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.";
  protected final String TEXT_105 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t\t\tif(length > ";
  protected final String TEXT_106 = "Struct.commonByteArray_";
  protected final String TEXT_107 = ".length) {" + NL + "           \t\t\t\t\t\tif(length < 1024 && ";
  protected final String TEXT_108 = ".length == 0) {" + NL + "\t               \t\t\t\t\t";
  protected final String TEXT_109 = " = new byte[1024];" + NL + "           \t\t\t\t\t\t} else {" + NL + "\t               \t\t\t\t\t";
  protected final String TEXT_110 = " = new byte[2 * length];" + NL + "\t               \t\t\t\t}" + NL + "           \t\t\t\t\t}" + NL + "               \t\t\t\tdis.readFully(";
  protected final String TEXT_111 = ", 0, length);" + NL + "               \t\t\t\tthis.";
  protected final String TEXT_112 = " = new String(";
  protected final String TEXT_113 = ", 0, length, utf8Charset);" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_114 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.";
  protected final String TEXT_115 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tthis.";
  protected final String TEXT_116 = " = new Date(dis.readLong());" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_117 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "               \t\t\t\tbyte[] byteArray = new byte[length];" + NL + "               \t\t\t\tdis.readFully(byteArray);" + NL + "               \t\t\t\tthis.";
  protected final String TEXT_118 = " = byteArray;" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_119 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_120 = " = (";
  protected final String TEXT_121 = ") dis.readObject();" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_122 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t\t\tthis.";
  protected final String TEXT_123 = "();" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_124 = NL + "\t\t\t            this.exprKey_";
  protected final String TEXT_125 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.exprKey_";
  protected final String TEXT_126 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tthis.exprKey_";
  protected final String TEXT_127 = NL + "\t\t\t            length = dis.readInt();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.exprKey_";
  protected final String TEXT_128 = ", 0, length);" + NL + "               \t\t\t\tthis.exprKey_";
  protected final String TEXT_129 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "               \t\t\t\tbyte[] byteArray = new byte[length];" + NL + "               \t\t\t\tdis.readFully(byteArray);" + NL + "               \t\t\t\tthis.exprKey_";
  protected final String TEXT_130 = NL + "           \t\t\t\tthis.exprKey_";
  protected final String TEXT_131 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t\t\tthis.exprKey_";
  protected final String TEXT_132 = NL + "        \t} catch (IOException e) {" + NL + "\t            throw new RuntimeException(e);";
  protected final String TEXT_133 = NL + NL + "\t\t";
  protected final String TEXT_134 = NL + "\t\t\t} catch(ClassNotFoundException eCNFE) {" + NL + "\t\t\t\t throw new RuntimeException(eCNFE);" + NL + "\t\t";
  protected final String TEXT_135 = NL + NL + "        }" + NL + "        " + NL + "      } " + NL + "    }" + NL + "" + NL + "    public void writeData(ObjectOutputStream dos) {" + NL + "        try {" + NL + "" + NL + "           \tdos.writeBoolean(this.is__rejectedInnerJoin);" + NL + "" + NL + "\t\t";
  protected final String TEXT_136 = NL + "\t\t            \tdos.write";
  protected final String TEXT_137 = "(this.";
  protected final String TEXT_138 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_139 = NL + "\t\t\t\t\t\tif(this.";
  protected final String TEXT_140 = " == null) {" + NL + "\t\t\t                dos.writeInt(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t                byte[] byteArray = this.";
  protected final String TEXT_141 = ".getBytes(utf8Charset);" + NL + "           \t\t\t    \tdos.writeInt(byteArray.length);" + NL + "               \t\t\t\tdos.write(byteArray);" + NL + "\t\t            \t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_142 = " == null) {" + NL + "\t\t\t                dos.writeByte(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "               \t\t\t\tdos.writeByte(0);" + NL + "           \t\t\t    \tdos.writeLong(this.";
  protected final String TEXT_143 = ".getTime());" + NL + "\t\t            \t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_144 = " == null) {" + NL + "\t\t\t                dos.writeInt(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "           \t\t\t    \tdos.writeInt(this.";
  protected final String TEXT_145 = ".length);" + NL + "               \t\t\t\tdos.write(this.";
  protected final String TEXT_146 = ");" + NL + "\t\t            \t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_147 = NL + "       \t\t\t    \tdos.writeObject(this.";
  protected final String TEXT_148 = " == null) {" + NL + "\t\t\t                dos.writeByte(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "               \t\t\t\tdos.writeByte(0);" + NL + "           \t\t\t    \tdos.write";
  protected final String TEXT_149 = "(this.exprKey_";
  protected final String TEXT_150 = NL + "\t\t\t\t\t\tif(this.exprKey_";
  protected final String TEXT_151 = " == null) {" + NL + "\t\t\t                dos.writeInt(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t                byte[] byteArray = this.exprKey_";
  protected final String TEXT_152 = " == null) {" + NL + "\t\t\t                dos.writeByte(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "               \t\t\t\tdos.writeByte(0);" + NL + "           \t\t\t    \tdos.writeLong(this.exprKey_";
  protected final String TEXT_153 = " == null) {" + NL + "\t\t\t                dos.writeInt(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "           \t\t\t    \tdos.writeInt(this.exprKey_";
  protected final String TEXT_154 = ".length);" + NL + "               \t\t\t\tdos.write(this.exprKey_";
  protected final String TEXT_155 = NL + "       \t\t\t    \tdos.writeObject(this.exprKey_";
  protected final String TEXT_156 = NL + "        }" + NL + "    }" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t} // G_TM_B_001" + NL + "\t" + NL + "\t";
  protected final String TEXT_157 = NL + "\t" + NL + "" + NL + "\torg.talend.designer.components.lookup.persistent.PersistentRowSorterIterator<SortableRow_";
  protected final String TEXT_158 = "> fsi_";
  protected final String TEXT_159 = " = " + NL + "\t\tnew org.talend.designer.components.lookup.persistent.PersistentRowSorterIterator<SortableRow_";
  protected final String TEXT_160 = ">(" + NL + "\t\t\t";
  protected final String TEXT_161 = " + \"/\"+ jobName +\"_tMapData_\" + Thread.currentThread().getId() + \"_\" + pid +\"_";
  protected final String TEXT_162 = "\", ";
  protected final String TEXT_163 = ") {" + NL + "\t\t\tpublic SortableRow_";
  protected final String TEXT_164 = " createRowInstance() {" + NL + "\t\t\t\treturn new SortableRow_";
  protected final String TEXT_165 = "();" + NL + "\t\t\t}" + NL + "\t\t};" + NL + "\t" + NL + "\tfsi_";
  protected final String TEXT_166 = ".initPut();" + NL + "" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_167 = "        " + NL + "        " + NL + "" + NL + "" + NL + "" + NL + "        " + NL + NL + NL + NL + NL + NL + NL + NL;

	public boolean hasConcurrencyContext(List<IConnection> inputConnections, List<? extends INode> graphicalNodes) {

		for(IConnection connection : inputConnections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            INode node = connection.getSource();
				return recursiveSearchIterateForConcurrency(node, graphicalNodes);
			}
		}
		return false;
	
	}

	public boolean hasIterateConnectionBefore(List<IConnection> inputConnections, List<? extends INode> graphicalNodes) {

		for(IConnection connection : inputConnections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            INode node = connection.getSource();
				return recursiveSearchIterate(node, graphicalNodes);
			}
		}
		return false;
	
	}
	
	public boolean recursiveSearchIterate(INode node, List<? extends INode> graphicalNodes) {
	
		//System.out.println(node);
	
		List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();

		for(IConnection connection : connections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            node = connection.getSource();
				//System.out.println(connection.getName() + " connectionType=" + connectionType + " connection=" + String.valueOf(connection));
    	        return recursiveSearchIterate(node, graphicalNodes);
            } else if(connectionType == EConnectionType.ITERATE) {
				//System.out.println("ITERATE return true");
    	        return true;
            }else{
            	//for virtual component
            	boolean find = false;
				for(INode loopNode : graphicalNodes) {
					if(loopNode.getUniqueName().equals(node.getUniqueName())){
						find = true;
					}
				}
				if(!find){
					List<IConnection> vConnections = (List<IConnection>) node.getIncomingConnections();
					for(IConnection vConnection : vConnections) {
						node = vConnection.getSource();
						break;
					}
					return recursiveSearchIterate(node, graphicalNodes);
				}
            }
		}
		
		//System.out.println("return false");
		return false;
	
	}

	private boolean recursiveSearchIterateForConcurrency(INode node, List<? extends INode> graphicalNodes) {
		List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();

		for(IConnection connection : connections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            node = connection.getSource();
    	        return recursiveSearchIterateForConcurrency(node, graphicalNodes);
            } else if(connectionType == EConnectionType.ITERATE) {
				boolean parallelIterate = "true".equals(ElementParameterParser.getValue(connection, "__ENABLE_PARALLEL__"));
    	        if(parallelIterate) {
    	        	return true;
    	        } else {
    	        	node = connection.getSource();
        			return recursiveSearchIterateForConcurrency(node, graphicalNodes);
    	        }
            }else{
            	//for virtual component
            	boolean find = false;
				for(INode loopNode : graphicalNodes) {
					if(loopNode.getUniqueName().equals(node.getUniqueName())){
						find = true;
					}
				}
				if(!find){
					List<IConnection> vConnections = (List<IConnection>) node.getIncomingConnections();
					for(IConnection vConnection : vConnections) {
						node = vConnection.getSource();
						break;
					}
					return recursiveSearchIterateForConcurrency(node, graphicalNodes);
				}
            }
		}
		
		return false;
	
	}

	public INode searchSubProcessStartNode(IConnection connection) {
       	INode source = connection.getSource();
    	//System.out.println(" source=" +  source);
    	INode subprocessStartNode = null;
    	if(source != null) {
			String searchedComponentName = source.getUniqueName();
        	//System.out.println(" searchedComponentName=" +  searchedComponentName);
			List<? extends INode> generatedNodes = source.getProcess().getGeneratingNodes();
			for(INode loopNode : generatedNodes) {
				if(loopNode.getUniqueName().equals(searchedComponentName)) {
					subprocessStartNode = loopNode.getSubProcessStartNode(false);
		        	//System.out.println(" subprocessStartNode=" +  subprocessStartNode.getUniqueName());
				}
			}
		}
	
		return subprocessStartNode;
	}
    public boolean hasJoinedTable(String tableNameToTest, ExternalMapperData data) {
        for (ExternalMapperTable table : data.getOutputTables()) {
            if (table.getIsJoinTableOf() != null && table.getIsJoinTableOf().equals(tableNameToTest)) {
                return true;
            }
        }
        return false;
    }
    
    
  public boolean isErrorReject(ExternalMapperTable table){
        String errorMessage = null;
        String errorStackTrace = null; 
        if(table!=null&&table.getName()!=null&&table.getName().endsWith("ErrorReject")){
            for(ExternalMapperTableEntry entry:table.getMetadataTableEntries()){
                if("errorMessage".equals(entry.getName())){
                    errorMessage = entry.getName();
                }else if("errorStackTrace".equals(entry.getName())){
                    errorStackTrace = entry.getName();
                }
            }
            if(errorMessage!=null&&errorStackTrace!=null){
                return true;
            }
        }
        return false;
    }

	public String getExpressionWithAutoConvertFunction(
		String expression, 
		ExternalMapperTableEntry targetTableEntry, 
		IMetadataColumn targetColumn,
		DataMapExpressionParser expressionParser, 
		Map<TableEntryLocation, ExternalMapperTableEntry> locationMap4Entry,
		Map<TableEntryLocation, IMetadataColumn> locationMap4Column, 
		Map<String,String> autoConverterMap, 
		boolean enable_auto_convert_type) {
		
		if(!enable_auto_convert_type) {
			return expression;
		}
		
		if(expression == null || expression.isEmpty()) {
			return expression;
		}
	
		TableEntryLocation uniqueLocation = getUniqueEntryLocation(expression);
		
        if(uniqueLocation == null) {
        	return expression;
        }
        
    	ExternalMapperTableEntry uniqueSourceEntry = locationMap4Entry.get(uniqueLocation);
    	
    	if(uniqueSourceEntry == null) {
    		return expression;
    	}
    	
    	String source_talend_type = uniqueSourceEntry.getType();
        String target_talend_type = targetTableEntry.getType();
        
        String convertFunction = autoConverterMap.get(contact(source_talend_type, ":", target_talend_type));
        
        if(convertFunction!=null && !convertFunction.isEmpty()) {
        	StringBuilder strbuilder = new StringBuilder();
        	
	        if(uniqueSourceEntry.isNullable()) {
	      		strbuilder.append("(((").append(expression).append(") == null) ? null : (");
	        }
        
        	expression = convertFunction.replace("${0}", expression);
        	
        	String pattern = null;
        	if((JavaTypesManager.STRING.getId().equals(source_talend_type)||JavaTypesManager.OBJECT.getId().equals(source_talend_type)) && JavaTypesManager.DATE.getId().equals(target_talend_type)) {
        		if(targetColumn!=null) {
        			pattern = targetColumn.getPattern();
        		}
        		
        		if(pattern == null || pattern.isEmpty()) {
        			pattern = "\"dd-MM-yyyy\"";
        		}
        		
        	} else if(JavaTypesManager.DATE.getId().equals(source_talend_type) && JavaTypesManager.STRING.getId().equals(target_talend_type)) {
        		IMetadataColumn sourceColumn = locationMap4Column.get(uniqueLocation);
        		
        		if(sourceColumn!=null) {
        			pattern = sourceColumn.getPattern();
        		}
        		
        		if(pattern == null || pattern.isEmpty()) {
        			pattern = "\"dd-MM-yyyy\"";
        		}
        	}
        	
        	if(pattern != null) {
        		expression = expression.replace("${1}", pattern);
        	}
        	
        	strbuilder.append(expression);
        	
        	if(uniqueSourceEntry.isNullable()) {
      			strbuilder.append("))");
        	}
        	
        	return strbuilder.toString();
        }
        
        return expression;
	}
	
	java.util.regex.Pattern locationPattern = java.util.regex.Pattern.compile(LanguageProvider.getJavaLanguage().getLocationPattern());
	
	private TableEntryLocation getUniqueEntryLocation(String expression) {
		java.util.regex.Matcher matcher = locationPattern.matcher(expression);
		if(matcher.matches()) {
			return new TableEntryLocation(matcher.group(1), matcher.group(2));
		}
		return null;
	}
	
	StringBuilder strbuilder = new StringBuilder(20);
	
	private String contact(String... content) {
		strbuilder.setLength(0);
		for(String each : content) {
			strbuilder.append(each);
		}
		return strbuilder.toString();
	}

    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_1);
    

//org.talend.core.model.utils.NodeUtil


	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	MapperComponent node = (MapperComponent) codeGenArgument.getArgument();
	boolean stats = codeGenArgument.isStatistics();
	
	String componentName = node.getUniqueName();
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ExternalMapperData data = (ExternalMapperData) node.getExternalData();
        List<ExternalMapperTable> varsTables = data.getVarsTables();
        ILanguage currentLanguage = LanguageProvider.getJavaLanguage();
        JavaGenerationManager gm = (JavaGenerationManager) GenerationManagerFactory.getInstance().getGenerationManager(currentLanguage);

		String tempFolder = ElementParameterParser.getValue(node, "__TEMPORARY_DATA_DIRECTORY__");
		if (("").equals(tempFolder)) {
			tempFolder=ElementParameterParser.getValue(node.getProcess(), "__COMP_DEFAULT_FILE_DIR__") + "/temp";
			tempFolder=TalendTextUtils.addQuotes(tempFolder);
		}
		String rowsBufferSize = ElementParameterParser.getValue(node, "__ROWS_BUFFER_SIZE__");
		
		
		boolean isLog4jEnabled = ("true").equals(ElementParameterParser.getValue(node.getProcess(), "__LOG4J_ACTIVATE__"));
		
		

    stringBuffer.append(TEXT_2);
    

List<IConnection> inputConnections = (List<IConnection>) node.getIncomingConnections();

boolean hasConcurrencyContext = false;
boolean isVirtualIn = componentName.endsWith("TMAP_IN");
boolean isVirtualOut = componentName.endsWith("TMAP_OUT");

String uniqueNameComponent = componentName.replaceAll("_TMAP_IN", "");
uniqueNameComponent = uniqueNameComponent.replaceAll("_TMAP_OUT", "");


if(isVirtualIn) {
	List<IConnection> localInputConnections = inputConnections;
	String searchedComponentName = componentName.replaceAll("TMAP_IN", "TMAP_OUT");
	List<? extends INode> generatedNodes = node.getProcess().getGeneratingNodes();
	for(INode loopNode : generatedNodes) {
		if(loopNode.getUniqueName().equals(searchedComponentName)) {
			localInputConnections = (List<IConnection>) loopNode.getIncomingConnections();
			break;
		}
	}
	hasConcurrencyContext = hasConcurrencyContext(localInputConnections, node.getProcess().getGraphicalNodes());
} else {
	hasConcurrencyContext = hasConcurrencyContext(inputConnections, node.getProcess().getGraphicalNodes());
}

if(hasConcurrencyContext && !isVirtualIn) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_4);
    
	// if codeGenArgument.getIsRunInMultiThread() is true, the job.this.globalMap will wrapped with synchronizedMap, use synchronized(job.this.globalMap)
	// when codeGenArgument.getIsRunInMultiThread() is false, the job.this.globalMap is HashMap, use synchronized(job.this.object) when do the job.this.globalMap.put() operation(tMap,tIterateToFlow).
	if(codeGenArgument.getIsRunInMultiThread()){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(node.getProcess().getName());
    stringBuffer.append(TEXT_6);
    }else{
    stringBuffer.append(TEXT_5);
    stringBuffer.append(node.getProcess().getName());
    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( node.getProcess().getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_13);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( node.getProcess().getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( uniqueNameComponent );
    stringBuffer.append(TEXT_17);
    
}

HashMap<String, IConnection> hNameToConnection = new HashMap<String, IConnection>();
for (IConnection connection : inputConnections) {
    hNameToConnection.put(connection.getName(), connection);
}

List<ExternalMapperTable> inputTablesWithInvalid = new ArrayList<ExternalMapperTable>(data.getInputTables());

List<ExternalMapperTable> inputTables = new ArrayList<ExternalMapperTable>();
for(int i=0; i < inputTablesWithInvalid.size(); i++) {
	ExternalMapperTable currentLoopTable = inputTablesWithInvalid.get(i);
	if(isLog4jEnabled){
		if(!isVirtualOut) {
		
    stringBuffer.append(TEXT_18);
    stringBuffer.append(currentLoopTable.getName());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_20);
    
		}
	}
	if(hNameToConnection.get(currentLoopTable.getName()) != null) {
		inputTables.add(currentLoopTable);
	}
}

int lstSizeInputs = inputTables.size();
for (int i = 1; i < lstSizeInputs; i++) {
    ExternalMapperTable inputTable = (ExternalMapperTable) inputTables.get(i);
    String tableName = inputTable.getName();

    List<ExternalMapperTableEntry> tableEntries = inputTable.getMetadataTableEntries();
    if (tableEntries == null) {
        continue;
    }
    String className = tableName + "Struct";
    
    boolean isAllRows = "ALL_ROWS".equals(inputTable.getMatchingMode());
    
	String lookupMode = inputTable.getLookupMode();
	boolean isReloadLookupMode = org.talend.designer.mapper.model.table.LOOKUP_MODE.RELOAD.name().equals(lookupMode);
	boolean isCacheOrReloadLookupMode = org.talend.designer.mapper.model.table.LOOKUP_MODE.CACHE_OR_RELOAD.name().equals(lookupMode);
	boolean isOneOfReloadLookupMode = isReloadLookupMode || isCacheOrReloadLookupMode;
    
    if(inputTable.isPersistent()) {

		
    stringBuffer.append(TEXT_21);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_27);
    
					if(hasConcurrencyContext) {
					
    stringBuffer.append(TEXT_28);
    }
    stringBuffer.append(TEXT_29);
    
	
	} else {
	
	
    stringBuffer.append(TEXT_30);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_31);
    
		
		if(isOneOfReloadLookupMode) {
			 
    stringBuffer.append(TEXT_32);
    
		} else {
			
    stringBuffer.append(TEXT_33);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_27);
    
					if(hasConcurrencyContext) {
					
    stringBuffer.append(TEXT_28);
    }
    stringBuffer.append(TEXT_36);
    
		}
					
	
    stringBuffer.append(TEXT_37);
    
	}

	String matchingModeStr = inputTable.getMatchingMode();
	
	if(isCacheOrReloadLookupMode) {
	
    stringBuffer.append(TEXT_30);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append( matchingModeStr );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_43);
    }

	if(isAllRows && !isOneOfReloadLookupMode) {
	
    stringBuffer.append(TEXT_44);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_45);
    
}





    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_1);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(TEXT_1);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_48);
    
        }

    stringBuffer.append(TEXT_50);
    

        /////////////////////////////////////////////////////////////////////////
        gm.setVarsTables(varsTables);

        List<ExternalMapperTable> varTablesList = new ArrayList<ExternalMapperTable>(varsTables);

        // constants
        for (ExternalMapperTable table : varTablesList) {
            List<ExternalMapperTableEntry> tableEntries = table.getMetadataTableEntries();
            if (tableEntries == null) {
                continue;
            }
            String tableName = table.getName();
            
            String instanceVarName = tableName + "__" + node.getUniqueName();
            String className = instanceVarName + "__Struct";
            

    stringBuffer.append(TEXT_51);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_52);
    
            int lstSize = tableEntries.size();
            for (int i = 0; i < lstSize; i++) {
                ExternalMapperTableEntry varTableEntry = (ExternalMapperTableEntry) tableEntries.get(i);
                String javaType = varTableEntry.getType();


    stringBuffer.append(TEXT_53);
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(javaType, varTableEntry.isNullable()) );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( varTableEntry.getName() );
    stringBuffer.append(TEXT_54);
    
            }

    stringBuffer.append(TEXT_55);
    stringBuffer.append(TEXT_1);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( instanceVarName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_48);
    
        }

    stringBuffer.append(TEXT_57);
    

		  List<IConnection> outputConnections = (List<IConnection>) node.getOutgoingConnections();
		  Map<String, IConnection> nameToOutputConnection = new HashMap<String, IConnection>();
        for (IConnection connection : outputConnections) {
		  		nameToOutputConnection.put(connection.getName(), connection);
		  }

        List<ExternalMapperTable> outputTablesList = new ArrayList<ExternalMapperTable>(data.getOutputTables());
        // constants
        for (ExternalMapperTable table : outputTablesList) {
        
            List<ExternalMapperTableEntry> tableEntries = table.getMetadataTableEntries();
            if (tableEntries == null || nameToOutputConnection.get(table.getName()) == null) {
                continue;
            }
            String tableName = table.getName();
			if(isLog4jEnabled){
				if(!isVirtualOut) {
				
    stringBuffer.append(TEXT_58);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_59);
    
				}
			}
            
            String instanceVarName = tableName + "__" + node.getUniqueName();
            String className = tableName + "Struct";
            

    stringBuffer.append(TEXT_1);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_48);
    
        }

    stringBuffer.append(TEXT_61);
    
        for (ExternalMapperTable table : data.getOutputTables()) {
            if (hasJoinedTable(table.getName(),data)&&nameToOutputConnection.get(table.getName())!=null) {

    stringBuffer.append(TEXT_62);
    stringBuffer.append( table.getName());
    stringBuffer.append(TEXT_63);
    stringBuffer.append( table.getName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append( table.getName());
    stringBuffer.append(TEXT_65);
    
            }
        }


    stringBuffer.append(TEXT_1);
    

HashMap<String, ExternalMapperTableEntry> hExternalInputTableEntries = new HashMap<String, ExternalMapperTableEntry>();

int currentJoinedTableIndex = 0;

int sizeInputTables = inputTables.size();
        
List<IConnection> cumulatedInputConnections = new ArrayList<IConnection>();

String comma;
for (int iInputTable = 0; iInputTable < sizeInputTables - 1; iInputTable++) { // T_TM_B_101
        
	ExternalMapperTable currentInputTable = inputTables.get(iInputTable);
	ExternalMapperTable nextInputTable = null; 
	
	if(sizeInputTables > 1) {
		nextInputTable = inputTables.get(iInputTable + 1);
	}
	
	currentJoinedTableIndex++;

	String currentTableName = currentInputTable.getName();
    IConnection currentConection = hNameToConnection.get(currentTableName);
    if (currentConection == null) {
        continue;
    }
	
	
    cumulatedInputConnections.add(currentConection);
	
	if(nextInputTable != null && nextInputTable.isPersistent()) { // T_TM_B_103
	
		List<IMetadataColumn> nextColumnsKeys = new ArrayList<IMetadataColumn>();

	        String nextTableName = nextInputTable.getName();
            IConnection nextConection = hNameToConnection.get(nextTableName);
            if (nextConection == null) {
                continue;
            }
	
		
    stringBuffer.append(TEXT_66);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_69);
    
	        
	        
                IMetadataTable nextMetadataTable = nextConection.getMetadataTable();
                if (nextInputTable != null) {
                    hExternalInputTableEntries.clear();
                    List<ExternalMapperTableEntry> metadataTableEntries = nextInputTable.getMetadataTableEntries();
                    if (metadataTableEntries == null) {
                        continue;
                    }
                    for (ExternalMapperTableEntry nextInputTableEntry : metadataTableEntries) {
                        hExternalInputTableEntries.put(nextInputTableEntry.getName(), nextInputTableEntry);
                    }
                    List<IMetadataColumn> listColumns = nextMetadataTable.getListColumns();
                    for (IMetadataColumn column : listColumns) {
                        String columnName = column.getLabel();
                        ExternalMapperTableEntry externalInputTableEntry = hExternalInputTableEntries.get(columnName);
                        if (externalInputTableEntry != null) {
                            String expressionKey = externalInputTableEntry.getExpression();
                            if (expressionKey != null && !"".equals(expressionKey.trim())) {
                            
                            	nextColumnsKeys.add(column);
                            
                            	
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_72);
    
 		                           
                            }
                        }
                    }
				}
			
			
			// properties declarations 
			
    stringBuffer.append(TEXT_73);
    stringBuffer.append( currentInputTable.getName() );
    stringBuffer.append(TEXT_74);
    
			
        
                IMetadataTable currentMetadataTable = currentConection.getMetadataTable();
                if (currentInputTable != null) {

					for(IConnection connection : cumulatedInputConnections) {
                    	IMetadataTable joinedTable = connection.getMetadataTable();
	                    List<IMetadataColumn> listColumns = joinedTable.getListColumns();
                    	for (IMetadataColumn column : listColumns) {
	                        String columnName = column.getLabel();
                            
                            
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_36);
    
						}
                    }

                    hExternalInputTableEntries.clear();
                    List<ExternalMapperTableEntry> metadataTableEntries = currentInputTable.getMetadataTableEntries();
                    if (metadataTableEntries == null) {
                        continue;
                    }
                    for (ExternalMapperTableEntry currentInputTableEntry : metadataTableEntries) {
                        hExternalInputTableEntries.put(currentInputTableEntry.getName(), currentInputTableEntry);
                    }

				}
				
		
    stringBuffer.append(TEXT_75);
    
		         
			        comma = "";
		         	for(IConnection connection : cumulatedInputConnections) {
		         	
		         		IConnection realConnection = org.talend.core.model.utils.NodeUtil.getRealConnectionTypeBased(connection);
		         	
			        	
    stringBuffer.append(comma);
    stringBuffer.append(TEXT_46);
    stringBuffer.append( realConnection.getName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( connection.getName() );
    
		         		comma = ", ";
		         	}
                    int sizeNextColumns = nextColumnsKeys.size();
                    for (int iColumn = 0; iColumn < sizeNextColumns; iColumn++) { // T_TM_B_105
                    	IMetadataColumn column = nextColumnsKeys.get(iColumn);

		         		
    stringBuffer.append(comma);
    stringBuffer.append(TEXT_46);
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    
                    
                    } // T_TM_B_105
                    
		         
    stringBuffer.append(TEXT_77);
    
		        	for(IConnection connection : cumulatedInputConnections) {
		        	
		        		IMetadataTable table = connection.getMetadataTable();
						List<IMetadataColumn> listColumns = table.getListColumns();
                    	for (IMetadataColumn column : listColumns) {
							
    stringBuffer.append(TEXT_78);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_79);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_80);
    
                		}
                	}
                	
    
                    sizeNextColumns = nextColumnsKeys.size();
                    for (int iColumn = 0; iColumn < sizeNextColumns; iColumn++) { // T_TM_B_104
                    	IMetadataColumn column = nextColumnsKeys.get(iColumn);

						
    stringBuffer.append(TEXT_81);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_83);
    
                    
                    } // T_TM_B_104
                    
    stringBuffer.append(TEXT_84);
    
		         
		         comma = "";
		         for(IConnection connection : cumulatedInputConnections) {

		            IConnection realConnection = org.talend.core.model.utils.NodeUtil.getRealConnectionTypeBased(connection);

			         
    stringBuffer.append(comma);
    stringBuffer.append( realConnection.getName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( connection.getName() );
    
		         	comma = ", ";
		         }
		         
    stringBuffer.append(TEXT_77);
    
		        	for(IConnection connection : cumulatedInputConnections) {
		        	
		        		IMetadataTable table = connection.getMetadataTable();
						List<IMetadataColumn> listColumns = table.getListColumns();
                    	for (IMetadataColumn column : listColumns) {
						
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_79);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_85);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_86);
    
                		}
                	}
                	
    stringBuffer.append(TEXT_87);
    	
		comma = "";
       	for(IConnection connection : cumulatedInputConnections) {
		        	
       		IMetadataTable metadata = connection.getMetadataTable();
			if (metadata !=null) {
				for (IMetadataColumn column: metadata.getListColumns()) {
						if(comma.length() > 0) {
						
    stringBuffer.append(TEXT_88);
    stringBuffer.append( comma );
    stringBuffer.append(TEXT_89);
    
						}
						
    stringBuffer.append(TEXT_90);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_92);
    
					comma = ", ";
				}
    		}
    	}
	    
    stringBuffer.append(TEXT_93);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_94);
    
                    sizeNextColumns = nextColumnsKeys.size();
                    for (int iColumn = 0; iColumn < sizeNextColumns; iColumn++) { // T_TM_B_102
                    	IMetadataColumn column = nextColumnsKeys.get(iColumn);
                    
    stringBuffer.append(TEXT_95);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_97);
    
                    } // T_TM_B_102
                    
    stringBuffer.append(TEXT_98);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_100);
    	
		boolean hasAtLeastOneRead = false;
		boolean hasAtLeastOneObjectType = false;
		if (currentMetadataTable !=null) {
		
		        	for(IConnection connection : cumulatedInputConnections) {
		        	
		        	
		        	
		        		IMetadataTable table = connection.getMetadataTable();
						List<IMetadataColumn> listColumns = table.getListColumns();
                    	for (IMetadataColumn column : listColumns) {
			  	hasAtLeastOneRead = true;
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
			 	
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_101);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_103);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_104);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_107);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_108);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_109);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append( codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_113);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_114);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_115);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_116);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_104);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_117);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_118);
    
				} else if(("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
			 		hasAtLeastOneObjectType = true;
					
    stringBuffer.append(TEXT_119);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_120);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_121);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_114);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_123);
    
				}


                		}
                	}
                	
    
                    sizeNextColumns = nextColumnsKeys.size();
                    for (int iColumn = 0; iColumn < sizeNextColumns; iColumn++) { // T_TM_B_104
                    	IMetadataColumn column = nextColumnsKeys.get(iColumn);

			  	hasAtLeastOneRead = true;
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());

				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_124);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_103);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_125);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_126);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_116);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_127);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_107);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_108);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_109);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_128);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( currentTableName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_113);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_127);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_129);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_118);
    
				} else if(("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
			 		hasAtLeastOneObjectType = true;
					
    stringBuffer.append(TEXT_130);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_120);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_121);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_125);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_123);
    
				}
                    
                    } // T_TM_B_104
		
		
		



    	}
	    if(hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_132);
    
        }
        
    stringBuffer.append(TEXT_133);
    
		if(hasAtLeastOneObjectType) {
		
    stringBuffer.append(TEXT_134);
    
		}
		
    stringBuffer.append(TEXT_135);
    	
		boolean hasAtLeastOneWrite = false;
		if (currentMetadataTable !=null) {
			
			
		        	for(IConnection connection : cumulatedInputConnections) {
		        	
		        		IMetadataTable table = connection.getMetadataTable();
						List<IMetadataColumn> listColumns = table.getListColumns();
                    	for (IMetadataColumn column : listColumns) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				hasAtLeastOneWrite = true;
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_136);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_137);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_138);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_139);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_140);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_141);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_139);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_142);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_143);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_139);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_144);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_145);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_146);
    
				} else if(("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_147);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_138);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_139);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_148);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_137);
    stringBuffer.append( connection.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_146);
    
				
				}
                		}
                	}
                	
    
                    sizeNextColumns = nextColumnsKeys.size();
                    for (int iColumn = 0; iColumn < sizeNextColumns; iColumn++) { // T_TM_B_104
                    	IMetadataColumn column = nextColumnsKeys.get(iColumn);

				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				hasAtLeastOneWrite = true;
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_136);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_149);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_138);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_150);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_151);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_141);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_150);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_152);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_143);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_150);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_153);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_154);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_146);
    
				} else if(("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_155);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_138);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_150);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_148);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_149);
    stringBuffer.append( nextInputTable.getName() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_146);
    
				
				}
                    
                    } // T_TM_B_104
			
			


    	}
	    if(hasAtLeastOneWrite) {
	    
    stringBuffer.append(TEXT_132);
    
        }
        
    stringBuffer.append(TEXT_156);
    
	String threadInsertedString = "thread\" + currentThreadNumber_" + uniqueNameComponent + " + \""; 
	
    stringBuffer.append(TEXT_157);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_160);
    stringBuffer.append( tempFolder );
    stringBuffer.append(TEXT_161);
    stringBuffer.append( hasConcurrencyContext ? threadInsertedString + "_" : "" );
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_162);
    stringBuffer.append( rowsBufferSize );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(uniqueNameComponent);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(currentJoinedTableIndex);
    stringBuffer.append(TEXT_166);
    
	} // T_TM_B_103
} // T_TM_B_101      


    stringBuffer.append(TEXT_167);
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}