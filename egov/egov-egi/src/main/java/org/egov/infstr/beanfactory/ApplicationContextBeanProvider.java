/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this 
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It 
	   is required that all modified versions of this material be marked in 
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program 
	   with regards to rights under trademark law for use of the trade names 
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.infstr.beanfactory;

import org.egov.infra.web.utils.ERPWebApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The Class ApplicationContextBeanProvider. 
 * A Utility class which wraps up all underlying spring<br/>
 * technique to provide and register beans
 */
public class ApplicationContextBeanProvider implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationContextBeanProvider.class);
	
	/** The ApplicationContext object attached with the current Classloader */
	private ApplicationContext applicationContext;
	
	/**
	 * Gets the application context.
	 * @return the ApplicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Gets the bean object for the given beanId.<br/>
	 * This method first checks whether bean is available in the ServletContext<br/>
	 * if its not exist then checks in the class path context.
	 * @param beanId the bean id
	 * @return the bean
	 */
	public Object getBean(final String beanId) {
		Object object = null;
		try {
			object = WebApplicationContextUtils.getWebApplicationContext(ERPWebApplicationContext.getServletContext()).getBean(beanId);
		} catch (final NoSuchBeanDefinitionException e) {
			LOG.warn("No bean named {} is available in {} context", beanId, ERPWebApplicationContext.getServletContext());
		}
		try {
			if (object == null) {
				object = this.applicationContext.getBean(beanId);
			}
		} catch (final NoSuchBeanDefinitionException e) {
			LOG.warn("No bean named {} is available in class path context", beanId);
		}
		return object;
	}
	
	/**
	 * Gets the bean object for the given beanId from the ServletContext of the given webappCtxtName.<br/>
	 * If isCrossCtxt is true and failed to get bean from the given webappCtxtName,<br/>
	 * then it will check all other ServletContext available.
	 * @param beanId the bean id
	 * @param webappCtxtName the web application context name.
	 * @param isCrossCtxt  true then checks across other ServletContext
	 * @return the bean
	 */
	public Object getBean(final String beanId, final ERPWebApplicationContext.ContextName webappCtxtName, final boolean isCrossCtxt) {
		Object object = null;
		try {
			object = WebApplicationContextUtils.getWebApplicationContext(ERPWebApplicationContext.getServletContext(webappCtxtName)).getBean(beanId);
		} catch (final NoSuchBeanDefinitionException e) {
			LOG.warn("No bean named {} is available in {} context", beanId, webappCtxtName);
		} catch (final Exception e) {
			LOG.warn("There is no servlet context available as {} or beanId as {} ", webappCtxtName, beanId);
		}
		if ((object == null) && isCrossCtxt) {
			for (final ERPWebApplicationContext.ContextName ctxName : ERPWebApplicationContext.ContextName.values()) {
				try {
					object = WebApplicationContextUtils.getWebApplicationContext(ERPWebApplicationContext.getServletContext().getContext(ctxName.toString())).getBean(beanId);
				} catch (final NoSuchBeanDefinitionException e) {
					LOG.warn("No bean named {} is available in {} context", beanId, ctxName);
				} catch (final Exception e) {
					LOG.warn("There is no servlet context as {} or beanId as {} is available ", ctxName, beanId);
				}
				if (object != null) {
					break;
				}
			}
		}
		return object;
	}
	
	/**
	 * Gets the bean object for the given beanId.<br/>
	 * If useServletCtxt set to true then it will check only in the current ServletContext <br/>
	 * else it will check inside class path context.
	 * @param beanId the bean id
	 * @param useServletCtxt to use ServletContext to search the bean or not
	 * @return the bean
	 */
	public Object getBean(final String beanId, final boolean useServletCtxt) {
		Object object = null;
		if (useServletCtxt) {
			try {
				object = WebApplicationContextUtils.getWebApplicationContext(ERPWebApplicationContext.getServletContext()).getBean(beanId);
			} catch (final NoSuchBeanDefinitionException e) {
				LOG.warn("No bean named {} is available in {} context", beanId, ERPWebApplicationContext.getServletContext());
			}
		} else {
			try {
				object = this.applicationContext.getBean(beanId);
			} catch (final NoSuchBeanDefinitionException e) {
				LOG.warn("No bean named {} is available in class path context", beanId);
			}
		}
		return object;
	}
	
	/**
	 * Gets the bean object for the given beanId from the given ctxtFileNames.
	 * @param beanId the bean id
	 * @param ctxtFileNames the Application Context file names
	 * @return the bean
	 */
	public Object getBean(final String beanId, final String... ctxtFileNames) {
		Object object = null;
		ClassPathXmlApplicationContext applicationContext = null;
		try {
			applicationContext = new ClassPathXmlApplicationContext(ctxtFileNames);
		} catch (final Exception e) {
			LOG.warn("Bean cannot be retrieved, cause : {}", e.getLocalizedMessage());
		}
		try {
			if (applicationContext != null) {
				object = applicationContext.getBean(beanId);
			}
		} catch (final NoSuchBeanDefinitionException e) {
			LOG.warn("No bean named {} is available in class path context", beanId);
		} 
		return object;
	}
	
	/**
	 * Register a bean with the given beanId for the given className to the given regCtxFileNames.<br/>
	 * if already exist, it will skip the bean registration and gives back the existing one.
	 * @param className the class name
	 * @param beanId the bean id use to register
	 * @param regCtxFileNames the Application Context file names to register
	 * @return the object
	 */
	public Object registerBean(final Class<?> className, final String beanId, final String... regCtxFileNames) {
		Object object = null;
		try {
			final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(regCtxFileNames);
			if (!applicationContext.containsBeanDefinition(beanId)) {
				final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
				beanFactory.registerBeanDefinition(beanId, BeanDefinitionBuilder.rootBeanDefinition(className.getName()).getBeanDefinition());
			}
			object = applicationContext.getBean(beanId);
		} catch (final Exception e) {
			LOG.warn("Bean registration failed, cause {} ", e.getLocalizedMessage());
		}
		return object;
	}
}