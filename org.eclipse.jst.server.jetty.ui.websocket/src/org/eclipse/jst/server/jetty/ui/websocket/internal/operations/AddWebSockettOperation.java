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

import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.INIT_PARAM;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.IS_SERVLET_TYPE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.URL_MAPPINGS;
import static org.eclipse.jst.j2ee.internal.web.operations.INewWebClassDataModelProperties.DESCRIPTION;
import static org.eclipse.jst.j2ee.internal.web.operations.INewWebClassDataModelProperties.DISPLAY_NAME;

import java.util.List;

import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.Description;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.jst.j2ee.internal.web.operations.AddWebClassOperation;
import org.eclipse.jst.j2ee.webapplication.InitParam;
import org.eclipse.jst.j2ee.webapplication.JSPType;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.ServletMapping;
import org.eclipse.jst.j2ee.webapplication.ServletType;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.jst.javaee.core.DisplayName;
import org.eclipse.jst.javaee.core.JavaeeFactory;
import org.eclipse.jst.javaee.core.UrlPatternType;
import org.eclipse.jst.javaee.web.IWebCommon;
import org.eclipse.jst.javaee.web.WebFactory;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditProviderOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * This class, AddServlet Operation is a IDataModelOperation following the IDataModel wizard and operation framework.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * 
 *      This operation subclasses the ArtifactEditProviderOperation so the changes made to the deployment descriptor models are saved to the artifact edit
 *      model.
 * @see org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditProviderOperation
 * 
 *      It is the operation which should be used when adding a new servlet to a web app, whether that be an annotated servlet or a non annotated servlet. This
 *      uses the NewServletClassDataModelProvider to retrieve properties set by the user in order to create the custom servet.
 * @see org.eclipse.jst.j2ee.internal.web.operations.NewServletClassDataModelProvider
 * 
 *      In the non annotated case, this operation will add the metadata necessary into the web deployment descriptor. In the annotated case, it will not, it
 *      will leave this up to the parsing of the annotations to build the deployment descriptor artifacts. To actually create the java class for the servlet,
 *      the operation uses the NewServletClassOperation. The NewServletClassOperation shares the same data model provider.
 * @see org.eclipse.jst.j2ee.internal.web.operations.NewServletClassOperation
 * 
 *      Clients may subclass this operation to provide their own behaviour on servlet creation. The execute method can be extended to do so. Also,
 *      generateServletMetaData and creteServletClass are exposed.
 * 
 *      The use of this class is EXPERIMENTAL and is subject to substantial changes.
 */
public class AddWebSockettOperation extends AddWebClassOperation
{

    /**
     * This is the constructor which should be used when creating the operation. It will not accept null parameter. It will not return null.
     * 
     * @see ArtifactEditProviderOperation#ArtifactEditProviderOperation(IDataModel)
     * 
     * @param dataModel
     * @return AddServletOperation
     */

    public AddWebSockettOperation(IDataModel dataModel)
    {
        super(dataModel);
    }

    @Override
    protected NewJavaEEArtifactClassOperation getNewClassOperation()
    {
        return new NewWebSocketServletClassOperation(getDataModel());
    }

    @Override
    protected void generateMetaData(IDataModel model, String qualifiedClassName)
    {
        boolean isServletType = model.getBooleanProperty(IS_SERVLET_TYPE);
        generateMetaData(model,qualifiedClassName,isServletType);
    }

    /**
     * Subclasses may extend this method to add their own generation steps for the creation of the metadata for the web deployment descriptor. This
     * implementation uses the J2EE models to create the Servlet model instance, any init params specified, and any servlet mappings. It then adds these to the
     * web application model. This will then be written out to the deployment descriptor file. This method does not accept null parameters.
     * 
     * @see Servlet
     * @see AddWebSockettOperation#createServlet(String, boolean)
     * @see AddWebSockettOperation#setUpInitParams(List, Servlet)
     * @see AddWebSockettOperation#setUpURLMappings(List, Servlet)
     * 
     * @param aModel
     * @param qualifiedClassName
     * @param isServletType
     */
    protected void generateMetaData(IDataModel aModel, String qualifiedClassName, boolean isServletType)
    {
        // Set up the servlet modelled object
        Object servlet = createServlet(qualifiedClassName,isServletType);

        // Set up the InitParams if any
        List initParamList = (List)aModel.getProperty(INIT_PARAM);
        if (initParamList != null)
            setUpInitParams(initParamList,servlet);

        // Set up the servlet URL mappings if any
        List urlMappingList = (List)aModel.getProperty(URL_MAPPINGS);
        if (urlMappingList != null)
            setUpURLMappings(urlMappingList,servlet);
    }

    /**
     * This method is intended for private use only. This method is used to create the servlet modelled object, to set any parameters specified in the data
     * model, and then to add the servlet instance to the web application model. This method does not accpet null parameters. It will not return null.
     * 
     * @see AddWebSockettOperation#generateServletMetaData(NewServletClassDataModel, String, boolean)
     * @see WebapplicationFactory#createServlet()
     * @see Servlet
     * 
     * @param qualifiedClassName
     * @param isServletType
     * @return Servlet instance
     */
    private Object createServlet(String qualifiedClassName, boolean isServletType)
    {
        // Get values from data model
        String displayName = model.getStringProperty(DISPLAY_NAME);
        String description = model.getStringProperty(DESCRIPTION);

        // Create the servlet instance and set up the parameters from data model
        Object modelObject = provider.getModelObject(WEB_APP_XML_PATH);
        if (modelObject instanceof org.eclipse.jst.j2ee.webapplication.WebApp)
        {

            Servlet servlet = WebapplicationFactory.eINSTANCE.createServlet();
            servlet.setDisplayName(displayName);
            servlet.setServletName(displayName);
            servlet.setDescription(description);
            // Handle servlet case
            if (isServletType)
            {
                ServletType servletType = WebapplicationFactory.eINSTANCE.createServletType();
                servletType.setClassName(qualifiedClassName);
                servlet.setWebType(servletType);
            }
            // Handle JSP case
            else
            {
                JSPType jspType = WebapplicationFactory.eINSTANCE.createJSPType();
                jspType.setJspFile(qualifiedClassName);
                servlet.setWebType(jspType);
            }
            // Add the servlet to the web application model

            // WebApp webApp = (WebApp) artifactEdit.getContentModelRoot();
            WebApp webApp = (WebApp)modelObject;
            webApp.getServlets().add(servlet);
            return servlet;
        }
        else if (modelObject instanceof IWebCommon)
        {

            org.eclipse.jst.javaee.web.Servlet servlet = WebFactory.eINSTANCE.createServlet();

            DisplayName displayNameObj = JavaeeFactory.eINSTANCE.createDisplayName();
            displayNameObj.setValue(displayName);
            servlet.getDisplayNames().add(displayNameObj);

            servlet.setServletName(displayName);

            org.eclipse.jst.javaee.core.Description descriptionObj = JavaeeFactory.eINSTANCE.createDescription();
            descriptionObj.setValue(description);
            servlet.getDescriptions().add(descriptionObj);

            // Handle servlet case
            if (isServletType)
            {
                servlet.setServletClass(qualifiedClassName);
            }
            // Handle JSP case
            else
            {
                servlet.setJspFile(qualifiedClassName);
            }
            // Add the servlet to the web application model

            // WebApp webApp = (WebApp) artifactEdit.getContentModelRoot();
            IWebCommon webApp = (IWebCommon)modelObject;
            webApp.getServlets().add(servlet);
            return servlet;
        }
        // Return the servlet instance
        return null;
    }

    /**
     * This method is intended for internal use only. This is used to create any init params for the new servlet metadata. It will not accept null parameters.
     * The init params are set on the servlet modelled object.
     * 
     * @see AddWebSockettOperation#generateServletMetaData(NewServletClassDataModel, String, boolean)
     * @see WebapplicationFactory#createInitParam()
     * 
     * @param initParamList
     * @param servlet
     */
    private void setUpInitParams(List initParamList, Object servletObj)
    {
        // Get the web app instance from the data model
        Object modelObject = provider.getModelObject();
        if (modelObject instanceof org.eclipse.jst.j2ee.webapplication.WebApp)
        {
            WebApp webApp = (WebApp)modelObject;
            Servlet servlet = (Servlet)servletObj;

            // If J2EE 1.4, add the param value and description info instances to the servlet init params
            if (webApp.getJ2EEVersionID() >= J2EEVersionConstants.J2EE_1_4_ID)
            {
                for (int iP = 0; iP < initParamList.size(); iP++)
                {
                    String[] stringArray = (String[])initParamList.get(iP);
                    // Create 1.4 common param value
                    ParamValue param = CommonFactory.eINSTANCE.createParamValue();
                    param.setName(stringArray[0]);
                    param.setValue(stringArray[1]);
                    // Create 1.4 common descripton value
                    Description descriptionObj = CommonFactory.eINSTANCE.createDescription();
                    descriptionObj.setValue(stringArray[2]);
                    // Set the description on the param
                    param.getDescriptions().add(descriptionObj);
                    param.setDescription(stringArray[2]);
                    // Add the param to the servlet model list of init params
                    servlet.getInitParams().add(param);
                }
            }
            // If J2EE 1.2 or 1.3, use the servlet specific init param instances
            else
            {
                for (int iP = 0; iP < initParamList.size(); iP++)
                {
                    String[] stringArray = (String[])initParamList.get(iP);
                    // Create the web init param
                    InitParam ip = WebapplicationFactory.eINSTANCE.createInitParam();
                    // Set the param name
                    ip.setParamName(stringArray[0]);
                    // Set the param value
                    ip.setParamValue(stringArray[1]);
                    // Set the param description
                    ip.setDescription(stringArray[2]);
                    // Add the init param to the servlet model list of params
                    servlet.getParams().add(ip);
                }
            }
        }
        else if (modelObject instanceof org.eclipse.jst.javaee.web.WebApp)
        {
            org.eclipse.jst.javaee.web.Servlet servlet = (org.eclipse.jst.javaee.web.Servlet)servletObj;

            for (int iP = 0; iP < initParamList.size(); iP++)
            {
                String[] stringArray = (String[])initParamList.get(iP);
                // Create 1.4 common param value
                org.eclipse.jst.javaee.core.ParamValue param = JavaeeFactory.eINSTANCE.createParamValue();
                param.setParamName(stringArray[0]);
                param.setParamValue(stringArray[1]);

                org.eclipse.jst.javaee.core.Description descriptionObj = JavaeeFactory.eINSTANCE.createDescription();
                descriptionObj.setValue(stringArray[2]);
                // Set the description on the param
                param.getDescriptions().add(descriptionObj);
                // Add the param to the servlet model list of init params
                servlet.getInitParams().add(param);
            }
        }
    }

    /**
     * This method is intended for internal use only. This method is used to create the servlet mapping modelled objects so the metadata for the servlet
     * mappings is store in the web deployment descriptor. This method will not accept null parameters. The servlet mappings are added to the web application
     * modelled object.
     * 
     * @see AddWebSockettOperation#generateServletMetaData(NewServletClassDataModel, String, boolean)
     * @see WebapplicationFactory#createServletMapping()
     * 
     * @param urlMappingList
     * @param servlet
     */
    private void setUpURLMappings(List urlMappingList, Object servletObj)
    {
        // Get the web app modelled object from the data model
        // WebApp webApp = (WebApp) artifactEdit.getContentModelRoot();
        Object modelObject = provider.getModelObject(WEB_APP_XML_PATH);
        // Create the servlet mappings if any
        if (modelObject instanceof org.eclipse.jst.j2ee.webapplication.WebApp)
        {
            WebApp webApp = (WebApp)modelObject;
            Servlet servlet = (Servlet)servletObj;
            for (int iM = 0; iM < urlMappingList.size(); iM++)
            {
                String[] stringArray = (String[])urlMappingList.get(iM);
                // Create the servlet mapping instance from the web factory
                ServletMapping mapping = WebapplicationFactory.eINSTANCE.createServletMapping();
                // Set the servlet and servlet name
                mapping.setServlet(servlet);
                mapping.setName(servlet.getServletName());
                // Set the URL pattern to map the servlet to
                mapping.setUrlPattern(stringArray[0]);
                // Add the servlet mapping to the web application modelled list
                webApp.getServletMappings().add(mapping);
            }
        }
        else if (modelObject instanceof IWebCommon)
        {
            IWebCommon webApp = (IWebCommon)modelObject;
            org.eclipse.jst.javaee.web.Servlet servlet = (org.eclipse.jst.javaee.web.Servlet)servletObj;

            // Create the servlet mappings if any
            if (urlMappingList.size() > 0)
            {
                // Create the servlet mapping instance from the web factory
                org.eclipse.jst.javaee.web.ServletMapping mapping = WebFactory.eINSTANCE.createServletMapping();

                mapping.setServletName(servlet.getServletName());
                for (int i = 0; i < urlMappingList.size(); i++)
                {
                    String[] stringArray = (String[])urlMappingList.get(i);
                    // Set the URL pattern to map the servlet to
                    UrlPatternType url = JavaeeFactory.eINSTANCE.createUrlPatternType();
                    url.setValue(stringArray[0]);
                    mapping.getUrlPatterns().add(url);
                }
                // Add the servlet mapping to the web application model list
                webApp.getServletMappings().add(mapping);
            }
        }
    }

}
