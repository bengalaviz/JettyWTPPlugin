/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.ui.websocket.internal.operations;

import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.ABSTRACT_METHODS;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.CLASS_NAME;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.INTERFACES;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.SUPERCLASS;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DESTROY;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_DELETE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_GET;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_HEAD;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_OPTIONS;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_POST;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_PUT;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.DO_TRACE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.GET_SERVLET_CONFIG;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.GET_SERVLET_INFO;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.INIT;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.INIT_PARAM;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.IS_SERVLET_TYPE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.NON_ANNOTATED_TEMPLATE_FILE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.SERVICE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.TEMPLATE_FILE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.TO_STRING;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.URL_MAPPINGS;
import static org.eclipse.jst.j2ee.internal.web.operations.INewWebClassDataModelProperties.DISPLAY_NAME;
import static org.eclipse.jst.j2ee.web.IServletConstants.QUALIFIED_SERVLET;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jst.j2ee.commonarchivecore.internal.util.J2EEFileUtil;
import org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.operations.NewServletClassOperation;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassDataModelProvider;
import org.eclipse.jst.j2ee.internal.web.operations.ServletSupertypesValidator;
import org.eclipse.jst.j2ee.internal.web.operations.WebMessages;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.j2ee.web.validation.UrlPattern;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * The NewServletClassDataModelProvider is a subclass of NewWebClassDataModelProvider and follows the IDataModel Operation and Wizard frameworks.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation
 * 
 *      This data model provider is a subclass of the NewWebClassDataModelProvider, which stores base properties necessary in the creation of a web artifact
 *      class.
 * 
 * @see NewWebClassDataModelProvider
 * 
 *      The NewServletClassDataModelProvider provides more specific properties for java class creation that are required in creating a servlet java class. The
 *      data model provider is used to store these values for the NewServletClassOperation.
 * 
 * @see INewServletClassDataModelProperties
 * 
 *      That operation will create the servlet java class based on the settings defined here in the data model.
 * 
 * @see NewServletClassOperation
 * 
 *      This data model properties implements the IAnnotationsDataModel to get the USE_ANNOTATIONS property for determining whether or not to generate an
 *      annotated java class.
 * 
 * @see org.eclipse.jst.j2ee.application.internal.operations.IAnnotationsDataModel
 * 
 *      Clients can subclass this data model provider to cache and provide their own specific attributes. They should also provide their own validation methods,
 *      properties interface, and default values for the properties they add.
 */
public class NewWebSocketClassDataModelProvider extends NewWebClassDataModelProvider
{

    /**
     * The fully qualified default servlet superclass: HttpServlet.
     */
    private final static String SERVLET_SUPERCLASS = "org.eclipse.jetty.websocket.WebSocketServlet";

    public static final String SUPPORT_ANNOTATION = "NewWebSocketClassDataModelProvider.SUPPORT_ANNOTATION";

    /**
     * String array of the default, minimum required fully qualified Servlet interfaces
     */
    private final static String[] SERVLET_INTERFACES =
    { QUALIFIED_SERVLET };

    private final static String ANNOTATED_TEMPLATE_DEFAULT = "websocket_servlet.javajet"; //$NON-NLS-1$

    private final static String NON_ANNOTATED_TEMPLATE_DEFAULT = "websocket_servlet.javajet"; //$NON-NLS-1$

    /**
     * Subclasses may extend this method to provide their own default operation for this data model provider. This implementation uses the AddServletOperation
     * to drive the servlet creation. It will not return null.
     * 
     * @see IDataModel#getDefaultOperation()
     * 
     * @return IDataModelOperation AddServletOperation
     */
    @Override
    public IDataModelOperation getDefaultOperation()
    {
        return new AddWebSockettOperation(model);
    }

    /**
     * Subclasses may extend this method to add their own data model's properties as valid base properties.
     * 
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
     */
    @Override
    public Set getPropertyNames()
    {
        // Add servlet specific properties defined in this data model
        Set propertyNames = super.getPropertyNames();

        propertyNames.add(INIT);
        propertyNames.add(DESTROY);
        propertyNames.add(GET_SERVLET_CONFIG);
        propertyNames.add(GET_SERVLET_INFO);
        propertyNames.add(SERVICE);
        propertyNames.add(DO_GET);
        propertyNames.add(DO_POST);
        propertyNames.add(DO_PUT);
        propertyNames.add(DO_DELETE);
        propertyNames.add(DO_HEAD);
        propertyNames.add(DO_OPTIONS);
        propertyNames.add(DO_TRACE);
        propertyNames.add(TO_STRING);
        propertyNames.add(IS_SERVLET_TYPE);
        propertyNames.add(INIT_PARAM);
        propertyNames.add(URL_MAPPINGS);
        propertyNames.add(NON_ANNOTATED_TEMPLATE_FILE);
        propertyNames.add(TEMPLATE_FILE);
        propertyNames.add(SUPPORT_ANNOTATION);
        return propertyNames;
    }

    @Override
    public boolean isPropertyEnabled(String propertyName)
    {
        if (ABSTRACT_METHODS.equals(propertyName))
        {
            return ServletSupertypesValidator.isGenericServletSuperclass(model);
        }
        else if (INIT.equals(propertyName) || DESTROY.equals(propertyName) || GET_SERVLET_CONFIG.equals(propertyName) || GET_SERVLET_INFO.equals(propertyName)
                || SERVICE.equals(propertyName))
        {
            boolean genericServlet = ServletSupertypesValidator.isGenericServletSuperclass(model);
            boolean inherit = model.getBooleanProperty(ABSTRACT_METHODS);
            return genericServlet && inherit;
        }
        else if (DO_GET.equals(propertyName) || DO_POST.equals(propertyName) || DO_PUT.equals(propertyName) || DO_DELETE.equals(propertyName)
                || DO_HEAD.equals(propertyName) || DO_OPTIONS.equals(propertyName) || DO_TRACE.equals(propertyName))
        {
            boolean httpServlet = ServletSupertypesValidator.isHttpServletSuperclass(model);
            boolean inherit = model.getBooleanProperty(ABSTRACT_METHODS);
            return httpServlet && inherit;
        }
        if (propertyName.equals(SUPPORT_ANNOTATION))
            return true;
        // Otherwise return super implementation
        return super.isPropertyEnabled(propertyName);
    }

    /**
     * Subclasses may extend this method to provide their own default values for any of the properties in the data model hierarchy. This method does not accept
     * a null parameter. It may return null. This implementation sets annotation use to be true, and to generate a servlet with doGet and doPost.
     * 
     * @see NewWebClassDataModelProvider#getDefaultProperty(String)
     * @see IDataModelProvider#getDefaultProperty(String)
     * 
     * @param propertyName
     * @return Object default value of property
     */
    @Override
    public Object getDefaultProperty(String propertyName)
    {
        // Generate a doPost and doGet methods by default only if a class
        // extending HttpServlet is selected
        if (propertyName.equals(DO_POST) || propertyName.equals(DO_GET))
        {
            if (ServletSupertypesValidator.isHttpServletSuperclass(model))
                return Boolean.TRUE;
        }

        // Generate a service method by default only if a class
        // not extending HttpServlet is selected
        if (propertyName.equals(SERVICE))
        {
            if (!ServletSupertypesValidator.isHttpServletSuperclass(model))
                return Boolean.TRUE;
        }

        if (propertyName.equals(INIT) || propertyName.equals(DESTROY) || propertyName.equals(GET_SERVLET_CONFIG) || propertyName.equals(GET_SERVLET_INFO))
        {
            if (!ServletSupertypesValidator.isGenericServletSuperclass(model))
                return Boolean.TRUE;
        }

        // Use servlet by default
        else if (propertyName.equals(IS_SERVLET_TYPE))
            return Boolean.TRUE;
        else if (propertyName.equals(DISPLAY_NAME))
        {
            String className = getStringProperty(CLASS_NAME);
            if (className.endsWith(J2EEFileUtil.DOT_JSP))
            {
                int index = className.lastIndexOf("/"); //$NON-NLS-1$
                className = className.substring(index + 1,className.length() - 4);
            }
            else
            {
                className = Signature.getSimpleName(className);
            }
            return className;
        }
        else if (propertyName.equals(URL_MAPPINGS))
            return getDefaultUrlMapping();
        else if (propertyName.equals(INTERFACES))
            return getServletInterfaces();
        else if (propertyName.equals(SUPERCLASS))
            return SERVLET_SUPERCLASS;
        else if (propertyName.equals(TEMPLATE_FILE))
            return ANNOTATED_TEMPLATE_DEFAULT;
        else if (propertyName.equals(NON_ANNOTATED_TEMPLATE_FILE))
            return NON_ANNOTATED_TEMPLATE_DEFAULT;
        else if (propertyName.equals(SUPPORT_ANNOTATION))
            return isAnnotationsSupported();
        // Otherwise check super for default value for property
        return super.getDefaultProperty(propertyName);
    }

    /**
     * Subclasses may extend this method to provide their own validation on any of the valid data model properties in the hierarchy. This implementation adds
     * validation for the init params, servlet mappings, display name, and existing class fields specific to the servlet java class creation. It does not accept
     * a null parameter. This method will not return null.
     * 
     * @see NewWebClassDataModelProvider#validate(String)
     * 
     * @param propertyName
     * @return IStatus is property value valid?
     */
    @Override
    public IStatus validate(String propertyName)
    {
        // Validate super class
        if (propertyName.equals(SUPERCLASS))
            return validateSuperClassName(getStringProperty(propertyName));
        // Validate init params
        if (propertyName.equals(INIT_PARAM))
            return validateInitParamList((List)getProperty(propertyName));
        // Validate servlet mappings
        if (propertyName.equals(URL_MAPPINGS))
            return validateURLMappingList((List)getProperty(propertyName));
        // Validate the servlet name in DD
        if (propertyName.equals(DISPLAY_NAME))
            return validateDisplayName(getStringProperty(propertyName));

        // Otherwise defer to super to validate the property
        return super.validate(propertyName);
    }

    @Override
    public boolean propertySet(String propertyName, Object propertyValue)
    {
        boolean result = false;

        if (SUPERCLASS.equals(propertyName))
        {
            model.notifyPropertyChange(ABSTRACT_METHODS,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(INIT,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DESTROY,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(GET_SERVLET_CONFIG,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(GET_SERVLET_INFO,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(SERVICE,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_GET,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_POST,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_PUT,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_DELETE,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_HEAD,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_OPTIONS,IDataModel.ENABLE_CHG);
            model.notifyPropertyChange(DO_TRACE,IDataModel.ENABLE_CHG);

            if (!hasSuperClass())
            {
                model.setProperty(ABSTRACT_METHODS,null);
                model.setProperty(INIT,null);
                model.setProperty(DESTROY,null);
                model.setProperty(GET_SERVLET_CONFIG,null);
                model.setProperty(GET_SERVLET_INFO,null);
                model.setProperty(SERVICE,null);
                model.setProperty(DO_GET,null);
                model.setProperty(DO_POST,null);
                model.setProperty(DO_PUT,null);
                model.setProperty(DO_DELETE,null);
                model.setProperty(DO_HEAD,null);
                model.setProperty(DO_OPTIONS,null);
                model.setProperty(DO_TRACE,null);
            }

            model.notifyPropertyChange(ABSTRACT_METHODS,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(INIT,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DESTROY,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(GET_SERVLET_CONFIG,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(GET_SERVLET_INFO,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(SERVICE,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_GET,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_POST,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_PUT,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_DELETE,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_HEAD,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_OPTIONS,IDataModel.DEFAULT_CHG);
            model.notifyPropertyChange(DO_TRACE,IDataModel.DEFAULT_CHG);

            if (!ServletSupertypesValidator.isServletSuperclass(model))
            {
                List ifaces = (List)model.getProperty(INTERFACES);
                ifaces.add(QUALIFIED_SERVLET);
            }
        }

        return result || super.propertySet(propertyName,propertyValue);
    }

    /**
     * Subclasses may extend this method to provide their own validation of the specified java classname. This implementation will ensure the class name is not
     * set to Servlet and then will forward on to the NewJavaClassDataModel to validate the class name as valid java. This method does not accept null as a
     * parameter. It will not return null. It will check if the super class extends the javax.servlet.Servlet interface also.
     * 
     * @param className
     * @return IStatus is java classname valid?
     */
    protected IStatus validateSuperClassName(String superclassName)
    {
        // If the servlet implements javax.servlet.Servlet, we do not need a
        // super class
        if (ServletSupertypesValidator.isGenericServletSuperclass(model))
            return WTPCommonPlugin.OK_STATUS;

        // Check the super class as a java class
        IStatus status = null;
        if (superclassName.trim().length() > 0)
        {
            status = super.validate(SUPERCLASS);
            if (status.getSeverity() == IStatus.ERROR)
                return status;
        }

        if (!ServletSupertypesValidator.isServletSuperclass(model))
            return WTPCommonPlugin.createErrorStatus(WebMessages.ERR_SERVLET_INTERFACE);

        return status;
    }

    /**
     * Returns the default Url Mapping depending upon the display name of the Servlet
     * 
     * @return List containting the default Url Mapping
     */
    private Object getDefaultUrlMapping()
    {
        List urlMappings = null;
        String text = (String)getProperty(DISPLAY_NAME);
        if (text != null)
        {
            urlMappings = new ArrayList();
            urlMappings.add(new String[]
            { "/" + text }); //$NON-NLS-1$
        }
        return urlMappings;
    }

    /**
     * This method is intended for internal use only. It will be used to validate the init params list to ensure there are not any duplicates. This method will
     * accept a null paramter. It will not return null.
     * 
     * @see NewWebSocketClassDataModelProvider#validate(String)
     * 
     * @param prop
     * @return IStatus is init params list valid?
     */
    private IStatus validateInitParamList(List prop)
    {
        if (prop != null && !prop.isEmpty())
        {
            // Ensure there are not duplicate entries in the list
            boolean dup = hasDuplicatesInStringArrayList(prop);
            if (dup)
            {
                String msg = WebMessages.ERR_DUPLICATED_INIT_PARAMETER;
                return WTPCommonPlugin.createErrorStatus(msg);
            }
        }
        // Return OK
        return WTPCommonPlugin.OK_STATUS;
    }

    /**
     * This method is intended for internal use only. This will validate the servlet mappings list and ensure there are not duplicate entries. It will accept a
     * null parameter. It will not return null.
     * 
     * @see NewWebSocketClassDataModelProvider#validate(String)
     * 
     * @param prop
     * @return IStatus is servlet mapping list valid?
     */
    private IStatus validateURLMappingList(List prop)
    {
        if (prop != null && !prop.isEmpty())
        {
            // Ensure there are not duplicates in the mapping list
            boolean dup = hasDuplicatesInStringArrayList(prop);
            if (dup)
            {
                String msg = WebMessages.ERR_DUPLICATED_URL_MAPPING;
                return WTPCommonPlugin.createErrorStatus(msg);
            }
            String isValidValue = validateValue(prop);
            if (isValidValue != null && isValidValue.length() > 0)
            {
                NLS.bind(WebMessages.ERR_URL_PATTERN_INVALID,isValidValue);
                String resourceString = WebMessages.getResourceString(WebMessages.ERR_URL_PATTERN_INVALID,new String[]
                { isValidValue });
                return WTPCommonPlugin.createErrorStatus(resourceString);
            }
        }
        else
        {
            String msg = WebMessages.ERR_URL_MAPPING_LIST_EMPTY;
            return WTPCommonPlugin.createErrorStatus(msg);
        }
        // Return OK
        return WTPCommonPlugin.OK_STATUS;
    }

    /**
     * This method is intended for internal use only. It provides a simple algorithm for detecting if there are invalid pattern's value in a list. It will
     * accept a null parameter.
     * 
     * @see NewWebSocketClassDataModelProvider#validateURLMappingList(List)
     * 
     * @param input
     * @return String first invalid pattern's value?
     */
    private String validateValue(List prop)
    {
        if (prop == null)
        {
            return ""; //$NON-NLS-1$
        }
        int size = prop.size();
        for (int i = 0; i < size; i++)
        {
            String urlMappingValue = ((String[])prop.get(i))[0];
            if (!UrlPattern.isValid(urlMappingValue))
                return urlMappingValue;
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * This method will return the list of servlet interfaces to be implemented for the new servlet java class. It will intialize the list using lazy
     * initialization to the minimum interfaces required by the data model SERVLET_INTERFACES. This method will not return null.
     * 
     * @see #SERVLET_INTERFACES
     * 
     * @return List of servlet interfaces to be implemented
     */
    private List getServletInterfaces()
    {
        if (interfaceList == null)
        {
            interfaceList = new ArrayList();
            // Add minimum required list of servlet interfaces to be implemented
            for (int i = 0; i < SERVLET_INTERFACES.length; i++)
            {
                interfaceList.add(SERVLET_INTERFACES[i]);
            }
            // Remove the javax.servlet.Servlet interface from the list if the
            // superclass already implements it
            if (ServletSupertypesValidator.isServletSuperclass(model))
            {
                interfaceList.remove(QUALIFIED_SERVLET);
            }
        }
        // Return interface list
        return interfaceList;
    }

    /**
     * This method is intended for internal use only. This will validate whether the display name selected is a valid display name for the servlet in the
     * specified web application. It will make sure the name is not empty and that it doesn't already exist in the web app. This method will accept null as a
     * parameter. It will not return null.
     * 
     * @see NewWebSocketClassDataModelProvider#validate(String)
     * 
     * @param prop
     * @return IStatus is servlet display name valid?
     */
    private IStatus validateDisplayName(String prop)
    {
        // Ensure the servlet display name is not null or empty
        if (prop == null || prop.trim().length() == 0)
        {
            String msg = WebMessages.ERR_DISPLAY_NAME_EMPTY;
            return WTPCommonPlugin.createErrorStatus(msg);
        }
        if (getTargetProject() == null || getTargetComponent() == null)
            return WTPCommonPlugin.OK_STATUS;

        IModelProvider provider = ModelProviderManager.getModelProvider(getTargetProject());
        Object mObj = provider.getModelObject();
        if (mObj instanceof org.eclipse.jst.j2ee.webapplication.WebApp)
        {
            org.eclipse.jst.j2ee.webapplication.WebApp webApp = (org.eclipse.jst.j2ee.webapplication.WebApp)mObj;

            List servlets = webApp.getServlets();
            boolean exists = false;
            // Ensure the display does not already exist in the web application
            if (servlets != null && !servlets.isEmpty())
            {
                for (int i = 0; i < servlets.size(); i++)
                {
                    String name = ((org.eclipse.jst.j2ee.webapplication.Servlet)servlets.get(i)).getServletName();
                    if (prop.equals(name))
                        exists = true;
                }
            }
            // If the servlet name already exists, throw an error
            if (exists)
            {
                String msg = WebMessages.getResourceString(WebMessages.ERR_SERVLET_NAME_EXIST,new String[]
                { prop });
                return WTPCommonPlugin.createErrorStatus(msg);
            }
        }
        else if (mObj instanceof org.eclipse.jst.javaee.web.WebApp)
        {
            org.eclipse.jst.javaee.web.WebApp webApp = (org.eclipse.jst.javaee.web.WebApp)mObj;

            List servlets = webApp.getServlets();
            boolean exists = false;
            // Ensure the display does not already exist in the web application
            if (servlets != null && !servlets.isEmpty())
            {
                for (int i = 0; i < servlets.size(); i++)
                {
                    String name = ((org.eclipse.jst.javaee.web.Servlet)servlets.get(i)).getServletName();
                    if (prop.equals(name))
                        exists = true;
                }
            }
            // If the servlet name already exists, throw an error
            if (exists)
            {
                String msg = WebMessages.getResourceString(WebMessages.ERR_SERVLET_NAME_EXIST,new String[]
                { prop });
                return WTPCommonPlugin.createErrorStatus(msg);
            }
        }

        // Otherwise, return OK
        return WTPCommonPlugin.OK_STATUS;
    }

}
