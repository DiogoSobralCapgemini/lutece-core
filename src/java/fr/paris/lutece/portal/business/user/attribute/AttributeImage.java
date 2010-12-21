/*
 * Copyright (c) 2002-2010, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.business.user.attribute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.FileSystemUtil;


/**
 *
 * AttributeComboBox
 *
 */
public class AttributeImage extends AbstractAttribute
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String CONSTANT_UNDERSCORE = "_";

    // Parameters
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_HELP_MESSAGE = "help_message";
    private static final String PARAMETER_MANDATORY = "mandatory";
    private static final String PARAMETER_ATTRIBUTE = "attribute";
    private static final String PARAMETER_WIDTH = "width";
    private static final String PARAMETER_HEIGHT = "height";
    private static final String PARAMETER_UPDATE_ATTRIBUTE = "update_attribute";

    // Properties
    private static final String PROPERTY_TYPE_IMAGE = "portal.users.attribute.type.image";
    private static final String PROPERTY_CREATE_IMAGE_PAGETITLE = "portal.users.create_attribute.pageTitleAttributeImage";
    private static final String PROPERTY_MODIFY_IMAGE_PAGETITLE = "portal.users.modify_attribute.pageTitleAttributeImage";
    private static final String PROPERTY_MESSAGE_NO_ARITHMETICAL_CHARACTERS = "portal.users.message.noArithmeticalCharacters";

    // Templates
    private static final String TEMPLATE_CREATE_ATTRIBUTE = "admin/user/attribute/image/create_attribute_image.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/user/attribute/image/modify_attribute_image.html";
    private static final String TEMPLATE_HTML_FORM_ATTRIBUTE = "admin/user/attribute/image/html_code_form_attribute_image.html";

    private static final String REGEX_ID = "-?[0-9]+";
    
    /**
     * Constructor
     */
    public AttributeImage(  )
    {
    	setAttributeImage( true );
    }

    /**
     * Get the template create an attribute
     * @return The URL of the template
     */
    public String getTemplateCreateAttribute(  )
    {
        return TEMPLATE_CREATE_ATTRIBUTE;
    }

    /**
     * Get the template modify an attribute
     * @return The URL of the template
     */
    public String getTemplateModifyAttribute(  )
    {
        return TEMPLATE_MODIFY_ATTRIBUTE;
    }

    /**
     * Get the template html form attribute
     * @return the template
     */
    public String getTemplateHtmlFormAttribute(  )
    {
        return TEMPLATE_HTML_FORM_ATTRIBUTE;
    }

    /**
     * Get the template html form search attribute
     * @return the template
     */
    public String getTemplateHtmlFormSearchAttribute(  )
    {
        return EMPTY_STRING;
    }

    /**
     * Get page title for create page
     * @return page title
     */
    public String getPropertyCreatePageTitle(  )
    {
        return PROPERTY_CREATE_IMAGE_PAGETITLE;
    }

    /**
     * Get page title for modify page
     * @return page title
     */
    public String getPropertyModifyPageTitle(  )
    {
        return PROPERTY_MODIFY_IMAGE_PAGETITLE;
    }

    /**
     * Set the data of the attribute
     * @param request HttpServletRequest
     * @return null if there are no errors
     */
    public String setAttributeData( HttpServletRequest request )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );
        
        String strError = EMPTY_STRING;

        if ( StringUtils.isNotBlank( strTitle ) )
        {
        	setTitle( strTitle );
            setHelpMessage( strHelpMessage );
            setMandatory( strMandatory != null );
            // Never show an image in the search box
            setShownInSearch( false );

            if ( getListAttributeFields(  ) == null )
            {
                List<AttributeField> listAttributeFields = new ArrayList<AttributeField>(  );
                AttributeField attributeField = new AttributeField(  );
                listAttributeFields.add( attributeField );
                setListAttributeFields( listAttributeFields );
            }
            
        	if ( StringUtils.isNotBlank( strWidth ) && strWidth.matches( REGEX_ID ) )
            {
        		int nWidth = Integer.parseInt( strWidth );
        		getListAttributeFields(  ).get( 0 ).setWidth( nWidth );
            }
        	
        	if ( StringUtils.isNotBlank( strHeight ) && strHeight.matches( REGEX_ID ) )
            {
        		int nHeight = Integer.parseInt( strHeight );
        		getListAttributeFields(  ).get( 0 ).setHeight( nHeight );
            }
        	
        	if ( StringUtils.isNotBlank( strWidth ) && !strWidth.matches( REGEX_ID ) ||
        			StringUtils.isNotBlank( strHeight ) && !strHeight.matches( REGEX_ID ) )
        	{
        		strError = PROPERTY_MESSAGE_NO_ARITHMETICAL_CHARACTERS;
        	}
        	if ( EMPTY_STRING.equals( strError ) )
        	{
        		return null;
        	}
        }
        else
        {
        	strError = Messages.MANDATORY_FIELDS;
        }
        
        return AdminMessageService.getMessageUrl( request, strError, AdminMessage.TYPE_STOP );
    }

    /**
     * Set attribute type
     * @param locale locale
     */
    public void setAttributeType( Locale locale )
    {
        AttributeType attributeType = new AttributeType(  );
        attributeType.setLocale( locale );
        attributeType.setClassName( this.getClass(  ).getName(  ) );
        attributeType.setLabelType( PROPERTY_TYPE_IMAGE );
        setAttributeType( attributeType );
    }

    /**
     * Get the data of the user fields
     * @param request HttpServletRequest
     * @param user user
     * @return user field data
     */
    public List<AdminUserField> getUserFieldsData( HttpServletRequest request, AdminUser user )
    {
    	String strUpdateAttribute = request.getParameter( PARAMETER_UPDATE_ATTRIBUTE + CONSTANT_UNDERSCORE + _nIdAttribute );
        List<AdminUserField> listUserFields = new ArrayList<AdminUserField>(  );

        try 
        {
        	if ( StringUtils.isNotBlank( strUpdateAttribute ) )
            {
            	MultipartHttpServletRequest multipartRequest = ( MultipartHttpServletRequest ) request;
                FileItem fileItem = multipartRequest.getFile( PARAMETER_ATTRIBUTE + CONSTANT_UNDERSCORE + _nIdAttribute );

                if ( ( fileItem != null ) && ( fileItem.getName(  ) != null ) && !EMPTY_STRING.equals( fileItem.getName(  ) ) )
                {
                    File file = new File(  );
                    PhysicalFile physicalFile = new PhysicalFile(  );
                    physicalFile.setValue( fileItem.get(  ) );
                    file.setTitle( FileUploadService.getFileNameOnly( fileItem ) );
                    file.setSize( ( int ) fileItem.getSize(  ) );
                    file.setPhysicalFile( physicalFile );
                    file.setMimeType( FileSystemUtil.getMIMEType( FileUploadService.getFileNameOnly( fileItem ) ) );
                    
                    if ( file != null )
                    {
                    	//verify that the file is an image
                    	ImageIO.read( new ByteArrayInputStream( file.getPhysicalFile(  ).getValue(  ) ) );
                    }
                    AdminUserField userField = new AdminUserField(  );
                    userField.setUser( user );
                    userField.setAttribute( this );
                    List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( getIdAttribute(  ) );
                    userField.setAttributeField( listAttributeFields.get( 0 ) );
                    userField.setFile( file );
                    
                    listUserFields.add( userField );
                }
            }
        	else
        	{
        		AdminUserFieldFilter auFieldFilter = new AdminUserFieldFilter(  );
        		auFieldFilter.setIdAttribute( getIdAttribute(  ) );
        		listUserFields = AdminUserFieldHome.findByFilter( auFieldFilter );
        		for ( AdminUserField userField : listUserFields )
        		{
        			if ( userField.getFile(  ) != null )
        			{
        				File file = FileHome.findByPrimaryKey( userField.getFile(  ).getIdFile(  ) );
        				userField.setFile( file );
        				int nIdPhysicalFile = file.getPhysicalFile(  ).getIdPhysicalFile(  );
        				PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( nIdPhysicalFile );
        				userField.getFile(  ).setPhysicalFile( physicalFile );
        			}
        		}
        	}
        }
        catch ( IOException e )
		{
			AppLogService.error( e );
		}
        
        return listUserFields;
    }
}