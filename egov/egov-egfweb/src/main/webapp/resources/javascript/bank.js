/*#-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------*/
var existingCode = '';
var existingName = '';

function checkUniqueBankCode(obj) {
	document.getElementById('codeuniquecode').style.display = 'none';
	var code = obj.value;
	if (code != "" && code != existingCode) {
		var url = "bank.action?model.code=" + code + "&rnd="+ Math.random() + "&mode=UNQ_CODE";
		var callback = {
			success : function(oResponse) {
				if (oResponse.responseText == "false") {
					document.getElementById('codeuniquecode').style.display = 'inline';
					obj.value = "";
				}
			},
			failure : function(oResponse) {
				alert("Server error occurred");
			}
		};
		YAHOO.util.Connect.asyncRequest("GET", url, callback);
	}
}

function checkUniqueBankName(obj) {
	document.getElementById('nameuniquename').style.display = 'none';
	var name = obj.value;
	if (name !== "" && existingName != name) {
		var url = "bank.action?model.name=" + name + "&rnd="+ Math.random() + "&mode=UNQ_NAME";
		var callback = {
			success : function(oResponse) {
				if (oResponse.responseText == "false") {
					document.getElementById('nameuniquename').style.display = 'inline';
					obj.value = "";
				}
			},
			failure : function(oResponse) {
				alert("Server error occurred");
			}
		};
		YAHOO.util.Connect.asyncRequest("GET", url, callback);
	}
}

function check_MICR(e) {
	var branchId = jQuery("#id").val();
	var value = jQuery(e.target).val();
	if (value != '') {
		jQuery.ajax({
			url : 'bankBranch.action?mode=CHECK_UNQ_MICR',
			data : {
				branchMICR : value,
				id : branchId
			},
			type : 'POST',
			async : false,
			datatype : 'text',
			success : function(data) {
				if (data == 'false') {
					alert('MICR code already exist');
					jQuery(e.target).val("");
				}
			}
		});
	}
}

function initializeGrid() {
	jQuery("#listsg11")
			.jqGrid(
					{
						caption : "Branch Details",
						url : 'bankBranch.action?mode=LIST_BRANCH&bankId='+ jQuery("#bank_id").val(),
						editurl : 'bankBranch.action?mode=CRUD&bankId='+ jQuery("#bank_id").val(),
						datatype : "json",
						height : 300,
						width : 800,
						hiddengrid : true,
						colNames : [ 'Srl No', 'Branch Name', 'Branch Code','MICR', 'Address', 'Contact Person', 'Phone Number', 'Narration', 'Active' ],
						colModel : [ {name : 'id',index : 'id',key : true,hidden : true,width : 55,	editable : true,editoptions : {readonly : true, size : 10}}, 
						             {name : 'branchname', index : 'branchname', width : 90, editable : true, editoptions : {size : 25},editrules : {required : true}}, 
						             {name : 'branchcode', index : 'branchcode', width : 90, editable : true, editoptions : {size : 25}, editrules : {required : true}}, 
						             {name : 'branchMICR', index : 'branchMICR', width : 90, editable : true, searchoptions: { sopt: ['eq','ne','lt','le','gt','ge', 'in', 'ni'] },editoptions : {size : 25, dataEvents : [ {type : 'blur', fn : check_MICR} ]}}, 
						             {name : 'branchaddress1', index : 'branchaddress1', width : 100, sortable : false, editable : true, edittype : "textarea", editoptions : {rows : "2", cols : "20"}, editrules : {required : true}},
						             {name : 'contactperson', index : 'contactperson', width : 80, editable : true, editoptions : {size : 25}}, 
						             {name : 'branchphone', index : 'branchphone', width : 80, editable : true, editoptions : {size : 25}}, 
						             {name : 'narration', index : 'narration', width : 80, sortable : false, editable : true, edittype : "textarea", editoptions : {rows : "2", cols : "20"}}, 
						             {name : 'isActive', index : 'isActive', width : 80, sortable : false, editable : true, edittype : "checkbox",searchoptions: { sopt: ['eq','ne']}, editoptions : { value : "1:0"}} 
						            ],
						rowNum : 20,
						rowList : [ 20, 30, 40, 50 ],
						pager : '#pagersg11',
						sortname : 'id',
						viewrecords : true,
						sortorder : "desc",
						multiselect : false,
						subGrid : true,
						subGridRowExpanded : function(subgrid_id, row_id) {
							var subgrid_table_id, pager_id;
							subgrid_table_id = subgrid_id + "_t";
							pager_id = "p_" + subgrid_table_id;
							jQuery("#" + subgrid_id).html("<table id='"+ subgrid_table_id+ "' class='scroll'></table><div id='"+ pager_id+ "' class='scroll'></div>");
							jQuery("#" + subgrid_table_id)
									.jqGrid(
											{
												caption : "Account Details",
												url : 'bankAccount.action?mode=LIST_BRANCH_ACC&q=2&bankBranchId='+ row_id,
												editurl : 'bankAccount.action?mode=CRUD&bankBranchId='+ row_id,
												colNames : [ 'ID', 'Account No:', 'Fund', 'Account Type', 'Description', 'Pay To', 'Type', 'Active' , 'GlCode'],
												colModel : [{name : 'id', index : 'id', key : true, hidden : true, width : 55, editable : true, editoptions : {readonly : true, size : 10}},
												            {name : "accountnumber", index : "accountnumber", width : 80, key : true, editable : true,searchoptions: { sopt: ['eq','ne','lt','le','gt','ge', 'in', 'ni'] }, editoptions : {size : 25}, editrules : { required : true}},
												            {name : "fundname", index : "fundname", width : 130, editable : true, edittype : "select", editoptions : {value : fundJson}, editrules : { required : true}},
												            {name : "accounttype", index : "accounttype", width : 70, editable :true, edittype : "select", editoptions : {value : accTypeJson}, editrules : {required : true}},
												            {name : "narration", index : "narration", width : 70, editable : true, edittype : "textarea", editoptions : { rows : "2", cols : "20" } },
												            {name : "payto", index : "payto", width : 70, editable : true, editoptions : {size : 25}},
												            {name : "typename", index : "typename", width : 70, editable : true, edittype : "select", editoptions : {value : bankAccTypeJson}},
												            {name : "active", index : "active", width : 70, editable : true, edittype : "checkbox",searchoptions: { sopt: ['eq','ne']}, editoptions : { value : "Y:N"}},
												            {name : 'glcode', index : 'glcode', key : true, width : 60, editable : false, editoptions : {readonly : true, size : 20}}
												           ],
												datatype : "json",
												rowNum : 20,
												width : 700,
												pager : pager_id,
												multiselect : false,
												viewrecords : true,
												sortname : 'id',
												sortorder : "asc",
												height : '100%'
											});
							jQuery("#" + subgrid_table_id).jqGrid('navGrid',"#" + pager_id, 
									{edit : true, add : true, del : true},
									{
										closeAfterEdit:true,
										resize : true,
										editCaption: "Edit Bank Account",
										beforeShowForm:function(response,data){
											jQuery("#accounttype").prop('disabled',true);
										},
										afterSubmit: function(response,data){
											return afterSubmit(response.responseText,data,"Bank Account Update, ");
										}				
									},
									{
										closeAfterAdd:true,
										resize : true,
										addCaption: "Add Bank Branch",
										beforeShowForm:function(response,data){
											jQuery("#accounttype").prop('disabled',false);
										},
										afterSubmit: function(response,data){
											return afterSubmit(response.responseText,data,"Bank Account Add, ");
										}
									},{
										 caption: "Remove Bank Account",
										 msg: "Remove the selected Bank Account ?"
									});
						},
						subGridRowColapsed : function(subgrid_id, row_id) {
							// this function is called before removing the data
							// var subgrid_table_id;
							// subgrid_table_id = subgrid_id+"_t";
							// jQuery("#"+subgrid_table_id).remove();
						}
					});
		jQuery("#listsg11").jqGrid('navGrid', '#pagersg11', 
				{add : true,edit : true, del : true},
				{
					closeAfterEdit:true,
					checkOnUpdate:true,
					checkOnSubmit:true,					
					editCaption: "Edit Bank Branch",
					resize : true,
					afterSubmit: function(response,data){
						return afterSubmit(response.responseText,data,"Bank Branch Update, ");
					}				
				},
				{
					closeAfterAdd:true,
					checkOnUpdate:true,
					addCaption: "Add Bank Branch",
					resize : true,
					afterSubmit: function(response,data){
						return afterSubmit(response.responseText,data,"Bank Branch Add, ");
					}
				},{
					caption: "Remove Bank Branch",
					 msg: "Remove the selected Bank Branch ?"
				},{multipleSearch:true});
}

function showMessage (msg) {
	jQuery.jgrid.info_dialog("Info","<div class=\"ui-state-highlight\" style=\"padding:5px;\">"+msg+"!</div>");
	jQuery("#info_dialog").delay(3000).fadeOut();
	jQuery(".ui-widget-overlay").hide();
}

function afterSubmit(reply,data, action) {
	var isSuccess = false;
	var message='';
	if (reply == "success") {
		showMessage(action+"successful.");
		isSuccess = true;
	} else {
		message = action+"failed.";
	}
	return [isSuccess,message,data.id];
}