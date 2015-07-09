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
package com.exilant.exility.pagemanager;

import java.util.Enumeration;
import java.util.Iterator;


import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


import com.exilant.exility.common.DataCollection;
import com.exilant.exility.common.MessageList;
import com.exilant.exility.common.Messages;

/**
 * PageMap describes the mapping between the data elements in a page on the client side
 *  and the entity/attribute on the server.
 *  It has a method to read this mapping from an XML file. 
 *
 * @author Raghu Bhandi
 */
public class PageMap  {
	private static final Logger LOGGER = Logger.getLogger(PageMap.class);

	public String type; //tag name is loaded to type
	public String name;
	public FieldMap[] fieldMaps;
	public GridMap[]  gridMaps;
		
	public PageMap(){
				
	}
	public String toJavaScript(DataCollection dc){
		StringBuffer sbf = new StringBuffer();
		// chosen a cryptic 'dc' as the name for dc to reduce size of html..
		sbf.append("var dc=new Object();\n");
		sbf.append("dc.values=new Object();\n");
		//sbf.append("d.valueLists=new Object();\n"); // value list is not used at this time
		
		sbf.append("dc.grids=new Object();\n");
		sbf.append("dc.messages=new Array();\n");
		sbf.append("dc.success=true;\n");
		/*
		 * output fields as d.values['fieldName'] = 'fieldValue';
		 */
		String nam;
		String val;
		/*
		 * If fieldMap is not loaded, output all the fields from dc
		 */
		if(this.type == null){
			Iterator iter = dc.getFieldNames();
			while(iter.hasNext()){
				nam = (String) iter.next();
				/*added in egf*/
				String formattedStr=dc.getValue(nam);
				formattedStr=formattedStr.replaceAll("\n"," ");
				formattedStr=formattedStr.replaceAll("\r"," ");
//				this.addValue(sbf,nam,dc.getValue(nam));
				/*added in egf*/
				this.addValue(sbf,nam,formattedStr);
			}
		}else{
			for (int i=0; i<fieldMaps.length; i++){
				nam = (fieldMaps[i].serverFieldName != null)?fieldMaps[i].serverFieldName : fieldMaps[i].name;
				val = dc.getValue(nam);
				if (val == null)val = "";//we will have to send some value. If dc does not have it, let us send ""
				val=val.replaceAll("\n"," ");
				val=val.replaceAll("\r"," ");
				this.addValue(sbf,fieldMaps[i].name,val);
			}
		}
		sbf.append("\n");
		/*
		 * output grids.
		 */
		if(this.type == null){ //pageMap not specified
			Iterator iter = dc.getGridNames();
			while(iter.hasNext()){
				nam = (String) iter.next();
				this.addGrid(sbf, dc.getGrid(nam), nam); 
			}
			
		}else{
			for (int i=0; i<gridMaps.length; i++){
				sbf.append(gridMaps[i].toJavaScript(dc, "dc"));
			}
		}
		sbf.append("\n");

		/*
		 * Messages:  each message is d.messges[i] = 'message text';
		 */
		 MessageList ml = dc.getMessageList();
		 int messageSize = ml.size();
		 for (int i=0; i<messageSize; i++){
			sbf.append("dc.messages[");
			sbf.append(i);
			sbf.append("]='");
			sbf.append(ml.getMessage(i).replaceAll("'", "\\'").replaceAll("\n"," "));
			sbf.append("';\n");
		 }

		/*
		 * And, finally, success=true/false. it is set to true in the beginning
		 */
		 if (dc.getSevirity()> Messages.WARNING){
		 	sbf.append("dc.success=false;\n");
		 }
	 
		return sbf.toString();
	}
	
	private void addValue(StringBuffer sbf, String nam, String val){
		sbf.append("dc.values['");
		sbf.append(nam);
		sbf.append("']=\'");
		sbf.append(val.replaceAll("'", "\\\\'"));
		sbf.append("\';\n");
		                                                                    
	}

	private void addGrid(StringBuffer sbf, String[][] grid, String nam){

		if (0 != grid.length){
			sbf.append("dc.grids['");
			sbf.append(nam);
			sbf.append("']=[['");
			sbf.append(grid[0][0]);
			for(int i=1; i<grid[0].length; i++){
				sbf.append("','");
				//sbf.append(grid[0][i].replaceAll("'", "\\'").replaceAll("\n", " ").replaceAll("\r", " "));
				sbf.append(grid[0][i].replaceAll("'", "\\\\'"));
			}
			sbf.append("']\n");
		
			for (int j=1; j<grid.length; j++){
				sbf.append(",['");
			
				//sbf.append(grid[j][0].replaceAll("'", "\\'").replaceAll("\n", " ").replaceAll("\r", " "));
				sbf.append(grid[j][0].replaceAll("'", "\\\\'"));
				for(int i=1; i<grid[j].length; i++){
					sbf.append("','");
					//sbf.append(grid[j][i].replaceAll("'", "\\'").replaceAll("\n", " ").replaceAll("\r", " "));
					sbf.append(grid[j][i].replaceAll("'", "\\\\'"));
				}
				sbf.append("']\n");
			}
			sbf.append("];\n");
		}
	}
	/*
	 * Create a DataCollection from the given HTTP request
	 */
	public DataCollection createDataCollection(HttpServletRequest req){
		
		DataCollection dc = new DataCollection();
		String val;
		String nam;
		/*
		 * put values as per fieldmap
		 */
		if (fieldMaps == null){ // no map is available, dump all fields
			String param ;
			String[] values;
			Enumeration params = req.getParameterNames();
			while(params.hasMoreElements()){
				param = (String)params.nextElement();
				values = req.getParameterValues(param);
				if (values == null) continue;
				if (values.length == 1)dc.addValue(param, values[0]);
				else dc.addValueList(param, values);
			}
		}else{
			for (int i=0; i<this.fieldMaps.length; i++){
				Object obj = req.getAttribute((this.fieldMaps[i].name));
				val = (obj == null)? "" : obj.toString();
				nam = (this.fieldMaps[i].serverFieldName == "")? this.fieldMaps[i].name : this.fieldMaps[i].serverFieldName ;
				val=val.replaceAll("\n"," ");
				val=val.replaceAll("\r"," ");
				dc.addValue(nam,val);
			}
		}
		/*
		 * get the grids
		 */
		//for each grid map 
		if (this.gridMaps != null){
			for (int j=0; j<this.gridMaps.length; j++){
				this.gridMaps[j].addGrid(dc, req);
			}
		}else {
			if(LOGGER.isDebugEnabled())     LOGGER.debug("addedn in createdatacoolection\\\\\\\\\\");
		}
		return dc;
	}
}
