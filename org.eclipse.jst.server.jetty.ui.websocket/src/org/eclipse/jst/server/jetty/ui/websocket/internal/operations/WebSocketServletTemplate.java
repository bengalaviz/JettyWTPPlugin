package org.eclipse.jst.server.jetty.ui.websocket.internal.operations;

import java.util.*;
import org.eclipse.jst.j2ee.internal.common.operations.*;

public class WebSocketServletTemplate
{
  protected static String nl;
  public static synchronized WebSocketServletTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    WebSocketServletTemplate result = new WebSocketServletTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "package ";
  protected final String TEXT_2 = ";";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = NL + "import ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = "/**" + NL + " * Jetty WebSocketServlet implementation class ";
  protected final String TEXT_9 = NL + " *" + NL + " * @web.servlet" + NL + " *   name=\"";
  protected final String TEXT_10 = "\"" + NL + " *   display-name=\"";
  protected final String TEXT_11 = "\"";
  protected final String TEXT_12 = NL + " *   description=\"";
  protected final String TEXT_13 = "\"";
  protected final String TEXT_14 = NL + " *" + NL + " * @web.servlet-mapping" + NL + " *   url-pattern=\"";
  protected final String TEXT_15 = "\"";
  protected final String TEXT_16 = NL + " *" + NL + " * @web.servlet-init-param" + NL + " *    name=\"";
  protected final String TEXT_17 = "\"" + NL + " *    value=\"";
  protected final String TEXT_18 = "\"";
  protected final String TEXT_19 = NL + " *    description=\"";
  protected final String TEXT_20 = "\"";
  protected final String TEXT_21 = NL + " */";
  protected final String TEXT_22 = NL + "@WebServlet";
  protected final String TEXT_23 = "(\"";
  protected final String TEXT_24 = "\")";
  protected final String TEXT_25 = "({ ";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = "\"";
  protected final String TEXT_28 = "\"";
  protected final String TEXT_29 = " })";
  protected final String TEXT_30 = "(";
  protected final String TEXT_31 = ", ";
  protected final String TEXT_32 = NL + "\t\t";
  protected final String TEXT_33 = " = \"";
  protected final String TEXT_34 = "\"";
  protected final String TEXT_35 = " = { ";
  protected final String TEXT_36 = ", ";
  protected final String TEXT_37 = NL + "\t\t\t\t";
  protected final String TEXT_38 = "\"";
  protected final String TEXT_39 = "\"";
  protected final String TEXT_40 = NL + "\t\t";
  protected final String TEXT_41 = " ";
  protected final String TEXT_42 = "}";
  protected final String TEXT_43 = " = { ";
  protected final String TEXT_44 = ", ";
  protected final String TEXT_45 = NL + "\t\t\t\t@WebInitParam(name = \"";
  protected final String TEXT_46 = "\", value = \"";
  protected final String TEXT_47 = "\"";
  protected final String TEXT_48 = ", description = \"";
  protected final String TEXT_49 = "\"";
  protected final String TEXT_50 = ")";
  protected final String TEXT_51 = NL + "\t\t}";
  protected final String TEXT_52 = ")";
  protected final String TEXT_53 = NL + "public ";
  protected final String TEXT_54 = "abstract ";
  protected final String TEXT_55 = "final ";
  protected final String TEXT_56 = "class ";
  protected final String TEXT_57 = " extends ";
  protected final String TEXT_58 = " implements ";
  protected final String TEXT_59 = ", ";
  protected final String TEXT_60 = " {";
  protected final String TEXT_61 = NL + "\tprivate static final long serialVersionUID = 1L;";
  protected final String TEXT_62 = NL + NL + "    /**" + NL + "     * Default constructor. " + NL + "     */" + NL + "    public ";
  protected final String TEXT_63 = "() {" + NL + "        // TODO Auto-generated constructor stub" + NL + "    }";
  protected final String TEXT_64 = NL + "       " + NL + "    /**" + NL + "     * @see ";
  protected final String TEXT_65 = "#";
  protected final String TEXT_66 = "(";
  protected final String TEXT_67 = ")" + NL + "     */" + NL + "    public ";
  protected final String TEXT_68 = "(";
  protected final String TEXT_69 = ") {" + NL + "        super(";
  protected final String TEXT_70 = ");" + NL + "        // TODO Auto-generated constructor stub" + NL + "    }";
  protected final String TEXT_71 = NL + NL + "\t/**" + NL + "     * @see ";
  protected final String TEXT_72 = "#";
  protected final String TEXT_73 = "(";
  protected final String TEXT_74 = ")" + NL + "     */" + NL + "    public ";
  protected final String TEXT_75 = " ";
  protected final String TEXT_76 = "(";
  protected final String TEXT_77 = ") {" + NL + "        // TODO Auto-generated method stub";
  protected final String TEXT_78 = NL + "\t\t\treturn ";
  protected final String TEXT_79 = ";";
  protected final String TEXT_80 = NL + "    }";
  protected final String TEXT_81 = NL + "\tprivate final Set";
  protected final String TEXT_82 = "<";
  protected final String TEXT_83 = ">";
  protected final String TEXT_84 = " members = new CopyOnWriteArraySet";
  protected final String TEXT_85 = "<";
  protected final String TEXT_86 = ">";
  protected final String TEXT_87 = "();";
  protected final String TEXT_88 = NL + NL + "\t/**" + NL + "\t * @see Servlet#init(ServletConfig)" + NL + "\t */" + NL + "\tpublic void init(ServletConfig config) throws ServletException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_89 = NL + NL + "\t/**" + NL + "\t * @see Servlet#destroy()" + NL + "\t */" + NL + "\tpublic void destroy() {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_90 = NL + NL + "\t/**" + NL + "\t * @see Servlet#getServletConfig()" + NL + "\t */" + NL + "\tpublic ServletConfig getServletConfig() {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t\treturn null;" + NL + "\t}";
  protected final String TEXT_91 = NL + NL + "\t/**" + NL + "\t * @see Servlet#getServletInfo()" + NL + "\t */" + NL + "\tpublic String getServletInfo() {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t\treturn null; " + NL + "\t}";
  protected final String TEXT_92 = NL + NL + "\t/**" + NL + "\t * @see Servlet#service(ServletRequest request, ServletResponse response)" + NL + "\t */" + NL + "\tpublic void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_93 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)" + NL + "\t */" + NL + "\tprotected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_94 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)" + NL + "\t */" + NL + "\tprotected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_95 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)" + NL + "\t */" + NL + "\tprotected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\tgetServletContext().getNamedDispatcher(\"default\").forward(request, response);" + NL + "\t}" + NL + "\t" + NL + "\t/*" + NL + "\t * @see org.eclipse.jetty.websocket.WebSocketServlet#doWebSocketConnect(javax.servlet.http.HttpServletRequest, java.lang.String)" + NL + "\t */" + NL + "\tprotected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {" + NL + "\t\treturn new ";
  protected final String TEXT_96 = "();" + NL + "\t}" + NL + "\t" + NL + "\tclass ";
  protected final String TEXT_97 = " implements WebSocket {" + NL + "\t\tprivate Outbound outbound;" + NL + "" + NL + "\t\tpublic void onConnect(Outbound outbound) {" + NL + "\t\t\tthis.outbound = outbound;" + NL + "\t\t\tmembers.add(this);" + NL + "\t\t}" + NL + "" + NL + "\t\t/*" + NL + "\t\t * @see org.eclipse.jetty.websocket.WebSocket#onMessage(byte, byte[]," + NL + "\t\t * int, int)" + NL + "\t\t */" + NL + "\t\tpublic void onMessage(byte frame, byte[] data, int offset, int length) {" + NL + "\t\t}" + NL + "" + NL + "\t\t/*" + NL + "\t\t * @see org.eclipse.jetty.websocket.WebSocket#onMessage(byte," + NL + "\t\t * java.lang.String)" + NL + "\t\t */" + NL + "\t\tpublic void onMessage(byte frame, String data) {" + NL + "\t\t\t";
  protected final String TEXT_98 = NL + "\t\t\tfor (";
  protected final String TEXT_99 = " member : members) {" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tmember.outbound.sendMessage(frame, data);" + NL + "\t\t\t\t} catch (IOException e) {" + NL + "\t\t\t\t\t// org.eclipse.jetty.util.log.Log.warn(e);" + NL + "\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_100 = NL + "\t\t\tfor (Iterator iterator = members.iterator(); iterator.hasNext();) {" + NL + "\t\t\t\t";
  protected final String TEXT_101 = " member = (";
  protected final String TEXT_102 = ") iterator.next();" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tmember.outbound.sendMessage(frame, data);" + NL + "\t\t\t\t} catch (IOException e) {" + NL + "\t\t\t\t\t// org.eclipse.jetty.util.log.Log.warn(e);" + NL + "\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_103 = NL + "\t\t}" + NL + "" + NL + "\t\t/*" + NL + "\t\t * @see org.eclipse.jetty.websocket.WebSocket#onDisconnect()" + NL + "\t\t */" + NL + "\t\tpublic void onDisconnect() {" + NL + "\t\t\tmembers.remove(this);" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/*" + NL + "\t\t * " + NL + "\t\t * @see org.eclipse.jetty.websocket.WebSocket#onFragment(boolean, byte," + NL + "\t\t * byte[], int, int)" + NL + "\t\t */" + NL + "\t\tpublic void onFragment(boolean more, byte opcode, byte[] data," + NL + "\t\t\t\tint offset, int length) {" + NL + "" + NL + "\t\t}" + NL + "\t\t" + NL + "\t}" + NL;
  protected final String TEXT_104 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)" + NL + "\t */" + NL + "\tprotected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_105 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)" + NL + "\t */" + NL + "\tprotected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_106 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse)" + NL + "\t */" + NL + "\tprotected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_107 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doOptions(HttpServletRequest, HttpServletResponse)" + NL + "\t */" + NL + "\tprotected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_108 = NL + NL + "\t/**" + NL + "\t * @see HttpServlet#doTrace(HttpServletRequest, HttpServletResponse)" + NL + "\t */" + NL + "\tprotected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t}";
  protected final String TEXT_109 = NL + NL + "}";
  protected final String TEXT_110 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     CreateWebSocketTemplateModel model = (CreateWebSocketTemplateModel) argument; 
    
	model.removeFlags(CreateJavaEEArtifactTemplateModel.FLAG_QUALIFIED_SUPERCLASS_NAME); 

    
	if (model.getJavaPackageName() != null && model.getJavaPackageName().length() > 0) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append( model.getJavaPackageName() );
    stringBuffer.append(TEXT_2);
     
	}

    stringBuffer.append(TEXT_3);
     
	Collection<String> imports = model.getImports();
	for (String anImport : imports) { 

    stringBuffer.append(TEXT_4);
    stringBuffer.append( anImport );
    stringBuffer.append(TEXT_5);
     
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append( model.getClassName() );
     
	if (model.isAnnotated()) { 

    stringBuffer.append(TEXT_9);
    stringBuffer.append( model.getServletName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( model.getServletName() );
    stringBuffer.append(TEXT_11);
     
		if (model.getDescription() != null && model.getDescription().length() > 0) { 

    stringBuffer.append(TEXT_12);
    stringBuffer.append( model.getDescription() );
    stringBuffer.append(TEXT_13);
     
		} 
		
		List<String[]> mappings = model.getServletMappings();
 		if (mappings != null && mappings.size() > 0) {
			for (int i = 0; i < mappings.size(); i++) {
				String map = model.getServletMapping(i); 
    stringBuffer.append(TEXT_14);
    stringBuffer.append( map );
    stringBuffer.append(TEXT_15);
     
			} 
		}
 		List<String[]> initParams = model.getInitParams();
 		if (initParams != null && initParams.size() > 0) {
    		for (int i = 0; i < initParams.size(); i++) {
				String name = model.getInitParam(i, CreateWebSocketTemplateModel.NAME);
				String value = model.getInitParam(i, CreateWebSocketTemplateModel.VALUE);
 				String description = model.getInitParam(i, CreateWebSocketTemplateModel.DESCRIPTION); 

    stringBuffer.append(TEXT_16);
    stringBuffer.append( name );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( value );
    stringBuffer.append(TEXT_18);
     
				if (description != null && description.length() > 0) { 

    stringBuffer.append(TEXT_19);
    stringBuffer.append( description );
    stringBuffer.append(TEXT_20);
    
				}
			} 
		} 
	} 

    stringBuffer.append(TEXT_21);
     
	if ("3.0".equals(model.getJavaEEVersion())) { 

    stringBuffer.append(TEXT_22);
    
		Map<String, Object> params = model.getClassAnnotationParams();
		if (params.size() == 1 && params.containsKey(CreateWebSocketTemplateModel.ATT_URL_PATTERNS)) {
			List<String[]> mappings = (List<String[]>) params.get(CreateWebSocketTemplateModel.ATT_URL_PATTERNS);
			if (mappings.size() == 1) {
				String value = mappings.get(0)[0];

    stringBuffer.append(TEXT_23);
    stringBuffer.append( value );
    stringBuffer.append(TEXT_24);
    
			} else {

    stringBuffer.append(TEXT_25);
    
				boolean needComma = false;
				for (String[] mapping : mappings) {
					if (needComma) {

    stringBuffer.append(TEXT_26);
    
					}

    stringBuffer.append(TEXT_27);
    stringBuffer.append( mapping[0] );
    stringBuffer.append(TEXT_28);
    
					needComma = true;
				}

    stringBuffer.append(TEXT_29);
    
			}
		} else if (!params.isEmpty()) { 

    stringBuffer.append(TEXT_30);
    
			Set<String> keys = params.keySet();
			boolean needNewLine = keys.contains(CreateWebSocketTemplateModel.ATT_INIT_PARAMS) || 
					(keys.contains(CreateWebSocketTemplateModel.ATT_URL_PATTERNS) && 
							((List<String[]>) params.get(CreateWebSocketTemplateModel.ATT_URL_PATTERNS)).size() > 1);
			boolean needComma = false;
			for (String key : keys) {
				if (needComma) {

    stringBuffer.append(TEXT_31);
    
				}
				
				if (needNewLine) {

    stringBuffer.append(TEXT_32);
    
				} 
			
				if (key.equals(CreateWebSocketTemplateModel.ATT_NAME) || key.equals(CreateWebSocketTemplateModel.ATT_DESCRIPTION)) { 
					String value = (String) params.get(key);

    stringBuffer.append( key );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( value );
    stringBuffer.append(TEXT_34);
    
				} else if (key.equals(CreateWebSocketTemplateModel.ATT_URL_PATTERNS)) {

    stringBuffer.append( key );
    stringBuffer.append(TEXT_35);
    
					List<String[]> mappings = (List<String[]>) params.get(key);
					boolean needComma2 = false;
					boolean needNewLine2 = mappings.size() > 1;
					for (String[] mapping : mappings) {
						if (needComma2) {

    stringBuffer.append(TEXT_36);
    
						}
				
						if (needNewLine2) {

    stringBuffer.append(TEXT_37);
    
						} 

    stringBuffer.append(TEXT_38);
    stringBuffer.append( mapping[0] );
    stringBuffer.append(TEXT_39);
    				
						needComma2 = true;
					}
				
					if (needNewLine2) {

    stringBuffer.append(TEXT_40);
    
					} else {

    stringBuffer.append(TEXT_41);
    
					}

    stringBuffer.append(TEXT_42);
    
				} else if (key.equals(CreateWebSocketTemplateModel.ATT_INIT_PARAMS)) {

    stringBuffer.append( key );
    stringBuffer.append(TEXT_43);
    
					List<String[]> initParams = (List<String[]>) params.get(key);
					boolean needComma2 = false;
					for (String[] initParam : initParams) {
						if (needComma2) {

    stringBuffer.append(TEXT_44);
    
						}
						
						String name = initParam[CreateWebSocketTemplateModel.NAME];
						String value = initParam[CreateWebSocketTemplateModel.VALUE];
						String description = initParam[CreateWebSocketTemplateModel.DESCRIPTION];

    stringBuffer.append(TEXT_45);
    stringBuffer.append( name );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( value );
    stringBuffer.append(TEXT_47);
    				
						if (description != null && description.length() > 0) {

    stringBuffer.append(TEXT_48);
    stringBuffer.append( description );
    stringBuffer.append(TEXT_49);
    
						}

    stringBuffer.append(TEXT_50);
    
						needComma2 = true;
					}

    stringBuffer.append(TEXT_51);
    
				}
			
				needComma = true;
  			}

    stringBuffer.append(TEXT_52);
    
		}
	}

    
	if (model.isPublic()) { 

    stringBuffer.append(TEXT_53);
     
	} 

	if (model.isAbstract()) { 

    stringBuffer.append(TEXT_54);
    
	}

	if (model.isFinal()) {

    stringBuffer.append(TEXT_55);
    
	}

    stringBuffer.append(TEXT_56);
    stringBuffer.append( model.getClassName() );
    
	String superClass = model.getSuperclassName();
 	if (superClass != null && superClass.length() > 0) {

    stringBuffer.append(TEXT_57);
    stringBuffer.append( superClass );
    
	}

	List<String> interfaces = model.getInterfaces(); 
 	if ( interfaces.size() > 0) { 

    stringBuffer.append(TEXT_58);
    
	}
	
 	for (int i = 0; i < interfaces.size(); i++) {
   		String INTERFACE = interfaces.get(i);
   		if (i > 0) {

    stringBuffer.append(TEXT_59);
    
		}

    stringBuffer.append( INTERFACE );
    
	}

    stringBuffer.append(TEXT_60);
     
	if (model.isGenericServletSuperclass()) { 

    stringBuffer.append(TEXT_61);
     
	} 

     
	if (!model.hasEmptySuperclassConstructor()) { 

    stringBuffer.append(TEXT_62);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_63);
     
	} 

	if (model.shouldGenSuperclassConstructors()) {
		List<Constructor> constructors = model.getConstructors();
		for (Constructor constructor : constructors) {
			if (constructor.isPublic() || constructor.isProtected()) { 

    stringBuffer.append(TEXT_64);
    stringBuffer.append( model.getSuperclassName() );
    stringBuffer.append(TEXT_65);
    stringBuffer.append( model.getSuperclassName() );
    stringBuffer.append(TEXT_66);
    stringBuffer.append( constructor.getParamsForJavadoc() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_68);
    stringBuffer.append( constructor.getParamsForDeclaration() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( constructor.getParamsForCall() );
    stringBuffer.append(TEXT_70);
    
			} 
		} 
	} 

    
	if (model.shouldImplementAbstractMethods()) {
		for (Method method : model.getUnimplementedMethods()) { 

    stringBuffer.append(TEXT_71);
    stringBuffer.append( method.getContainingJavaClass() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append( method.getName() );
    stringBuffer.append(TEXT_73);
    stringBuffer.append( method.getParamsForJavadoc() );
    stringBuffer.append(TEXT_74);
    stringBuffer.append( method.getReturnType() );
    stringBuffer.append(TEXT_75);
    stringBuffer.append( method.getName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( method.getParamsForDeclaration() );
    stringBuffer.append(TEXT_77);
     
			String defaultReturnValue = method.getDefaultReturnValue();
			if (defaultReturnValue != null) { 

    stringBuffer.append(TEXT_78);
    stringBuffer.append( defaultReturnValue );
    stringBuffer.append(TEXT_79);
    
			} 

    stringBuffer.append(TEXT_80);
     
		}
	} 

    stringBuffer.append(TEXT_81);
     if (model.canSupportAnnotation()) {
    stringBuffer.append(TEXT_82);
    stringBuffer.append(model.getWebSocketClassName());
    stringBuffer.append(TEXT_83);
    }
    stringBuffer.append(TEXT_84);
     if (model.canSupportAnnotation()) {
    stringBuffer.append(TEXT_85);
    stringBuffer.append(model.getWebSocketClassName());
    stringBuffer.append(TEXT_86);
    }
    stringBuffer.append(TEXT_87);
     if (model.shouldGenInit()) { 
    stringBuffer.append(TEXT_88);
     } 
     if (model.shouldGenDestroy()) { 
    stringBuffer.append(TEXT_89);
     } 
     if (model.shouldGenGetServletConfig()) { 
    stringBuffer.append(TEXT_90);
     } 
     if (model.shouldGenGetServletInfo()) { 
    stringBuffer.append(TEXT_91);
     } 
     if (model.shouldGenService() && !model.isHttpServletSuperclass()) { 
    stringBuffer.append(TEXT_92);
     } 
     if (model.shouldGenService() && model.isHttpServletSuperclass()) { 
    stringBuffer.append(TEXT_93);
     } 
     if (model.shouldGenDoGet()) { 
    stringBuffer.append(TEXT_94);
     } 
    stringBuffer.append(TEXT_95);
    stringBuffer.append( model.getWebSocketClassName() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( model.getWebSocketClassName() );
    stringBuffer.append(TEXT_97);
     if (model.canSupportAnnotation()) {
    stringBuffer.append(TEXT_98);
    stringBuffer.append(model.getWebSocketClassName());
    stringBuffer.append(TEXT_99);
    } else { 
    stringBuffer.append(TEXT_100);
    stringBuffer.append(model.getWebSocketClassName());
    stringBuffer.append(TEXT_101);
    stringBuffer.append(model.getWebSocketClassName());
    stringBuffer.append(TEXT_102);
    } 
    stringBuffer.append(TEXT_103);
     if (model.shouldGenDoPut()) { 
    stringBuffer.append(TEXT_104);
     } 
     if (model.shouldGenDoDelete()) { 
    stringBuffer.append(TEXT_105);
     } 
     if (model.shouldGenDoHead()) { 
    stringBuffer.append(TEXT_106);
     } 
     if (model.shouldGenDoOptions()) { 
    stringBuffer.append(TEXT_107);
     } 
     if (model.shouldGenDoTrace()) { 
    stringBuffer.append(TEXT_108);
     } 
    stringBuffer.append(TEXT_109);
    stringBuffer.append(TEXT_110);
    return stringBuffer.toString();
  }
}
