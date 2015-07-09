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
package org.egov.pgr.web.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.egov.builder.entities.DepartmentBuilder;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.pgr.entity.Complainant;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.service.ComplaintService;
import org.egov.pgr.web.controller.complaint.ViewComplaintController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;

public class ViewComplaintControllerTest extends AbstractContextControllerTest<ViewComplaintController> {

	private MockMvc mockMvc;
	public String crnNo;

	@Mock
	private ComplaintService complaintService;
	
	@Mock
        private DepartmentService departmentService;

	@Before
	public void before() {
        mockMvc = mvcBuilder.build();
        Department department = new DepartmentBuilder().withCode("DC").build();
        when(departmentService.getDepartmentByCode(anyString())).thenReturn(department);

        ComplaintType complaintType = new ComplaintType();
        complaintType.setName("existing");
        complaintType.setDepartment(department);
        
        Complainant complainant = new Complainant();
        complainant.setEmail("abc@gmail.com");
        complainant.setName("xyz");
        
        Complaint complaint = new Complaint();
        complaint.setComplaintType(complaintType);
        complaint.setComplainant(complainant);
        crnNo = "CRN-rmd1";
        when(complaintService.getComplaintByCRN(crnNo)).thenReturn(complaint);
        
	}

	@Override
	protected ViewComplaintController initController() {
		initMocks(this);
		return new ViewComplaintController(complaintService);
	}

	@Test
	public void getViewComplaintResult() throws Exception {
		this.mockMvc.perform(get("/complaint/view/"+crnNo))
				.andExpect(view().name("view-complaint"))
				.andExpect(status().isOk());

	}

}
