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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassOperation;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditProviderOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * The NewServletClassOperation is an IDataModelOperation following the IDataModel wizard and operation framework.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * 
 *      It extends ArtifactEditProviderOperation to provide servlet specific java class generation.
 * @see org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditProviderOperation
 * 
 *      This operation is used by the AddServletOperation to generate either an annotated or non annotated java class for an added servlet. It shares the
 *      NewServletClassDataModelProvider with the AddServletOperation to store the appropriate properties required to generate the new servlet.
 * @see org.eclipse.jst.j2ee.internal.web.operations.AddServletOperation
 * @see org.eclipse.jst.j2ee.internal.web.operations.NewServletClassDataModelProvider
 * 
 *      In the annotated case, a WTPJetEmitter servlet template is created and used to generate the servlet java class with the embedded annotated tags.
 * @see org.eclipse.jst.j2ee.internal.project.WTPJETEmitter
 * @see org.eclipse.jst.j2ee.internal.web.operations.CreateServletTemplateModel
 * 
 *      In the non annotated case, the same emitter is used to create the class with the non annotated servlet template so the annotated tags are omitted.
 * 
 *      Subclasses may extend this operation to provide their own specific servlet java class generation. The execute method may be extended to do so. Also,
 *      generateUsingTemplates is exposed.
 * 
 *      The use of this class is EXPERIMENTAL and is subject to substantial changes.
 */
public class NewWebSocketServletClassOperation extends NewWebClassOperation
{

    /**
     * folder location of the servlet creation templates diretory
     */
    protected static final String TEMPLATE_DIR = "/templates/"; //$NON-NLS-1$

    /**
     * location of the servlet creation template file
     */
    protected static final String TEMPLATE_FILE = "/templates/websocket_servlet.javajet"; //$NON-NLS-1$

    /**
     * This is the constructor which should be used when creating a NewServletClassOperation. An instance of the NewServletClassDataModel should be passed in.
     * This does not accept null parameter. It will not return null.
     * 
     * @see ArtifactEditProviderOperation#ArtifactEditProviderOperation(IDataModel)
     * @see NewServletClassDataModel
     * 
     * @param dataModel
     * @return NewServletClassOperation
     */
    public NewWebSocketServletClassOperation(IDataModel dataModel)
    {
        super(dataModel);
    }

    /**
     * This method will create an instance of the CreateServletTemplate model to be used in conjunction with the WTPJETEmitter. This method will not return
     * null.
     * 
     * @see CreateWebSocketTemplateModel
     * @see NewWebSocketServletClassOperation#generateUsingTemplates(IProgressMonitor, IPackageFragment)
     * 
     * @return CreateServletTemplateModel
     */
    @Override
    protected CreateWebSocketTemplateModel createTemplateModel()
    {
        return new CreateWebSocketTemplateModel(model);
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected WebSocketServletTemplate getTemplateImplementation()
    {
        return WebSocketServletTemplate.create(null);
    }

}
