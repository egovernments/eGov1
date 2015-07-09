/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
 * 
 *     The updated version of eGov suite of products as by eGovernments Foundation 
 *     is available at http://www.egovernments.org
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or 
 *     http://www.gnu.org/licenses/gpl.html .
 * 
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 * 
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.eb.webservice.tneb.corp;

public class FetchDataProxy implements FetchData_PortType {
  private String _endpoint = null;
  private FetchData_PortType fetchData_PortType = null;
  
  public FetchDataProxy() {
    _initFetchDataProxy();
  }
  
  public FetchDataProxy(String endpoint) {
    _endpoint = endpoint;
    _initFetchDataProxy();
  }
  
  private void _initFetchDataProxy() {
    try {
      fetchData_PortType = (new FetchData_ServiceLocator()).getFetchDataPort();
      if (fetchData_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fetchData_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fetchData_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fetchData_PortType != null)
      ((javax.xml.rpc.Stub)fetchData_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
   
  }
  
  public FetchData_PortType getFetchData_PortType() {
    if (fetchData_PortType == null)
      _initFetchDataProxy();
    return fetchData_PortType;
  }
  
  public org.egov.eb.webservice.tneb.corp.TempPostRecords3[] fetchData(java.lang.String cuscode, java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException{
    if (fetchData_PortType == null)
      _initFetchDataProxy();
    return fetchData_PortType.fetchData(cuscode, userName, password);
  }
  
  
}
