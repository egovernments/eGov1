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
var tableContainer;
jQuery(document).ready(function ($) {
	    $(":input").inputmask();
		
	    $(".datepicker").datepicker({
	        format: "dd/mm/yyyy"
		});
	    tableContainer=$('#aplicationSearchResults');
	    $('#searchapplication').click(function () {
	    	$.post("/wtms/elastic/appSearch/", $('#applicationSearchRequestForm').serialize())
			.done(function (searchResult) {
				console.log(JSON.stringify(searchResult));
				
				tableContainer.dataTable({
					destroy:true,
					"sPaginationType": "bootstrap",
					"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
					//"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-6 col-md-3 col-left'i><'col-xs-6 col-md-3 text-right col-left'l><'col-xs-12 col-md-3 col-right'<'export-data'T>><'col-xs-12 col-md-3 col-right'p>>",
					"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
					"autoWidth": false,
					searchable:true,
					data: searchResult,
					columns: [
					{title: 'Application Type', data: 'resource.clauses.applicationtype'},
					{title: 'url' ,data: 'resource.searchable.url',"bVisible": false},
					{title: 'Applicant No.', data: 'resource.searchable.applicationnumber'},
					{title: 'Application Date',
						render: function (data, type, full) {
							if(full!=null && full.resource!=undefined &&  full.resource.searchable.applicationdate != undefined) {
								var regDateSplit = full.resource.searchable.applicationdate.split("T")[0].split("-");		
								return regDateSplit[2] + "/" + regDateSplit[1] + "/" + regDateSplit[0];
							}
							else return "";
				    	}
					},
					{title: 'Application Name', data: 'resource.searchable.applicantname'},
					{title: 'Application Address', data: 'resource.common.applicantAddress'},
					
					{title: 'Status', data: 'resource.clauses.status'}
					
					
					]
				});
			})
		});
		
	
	
	    $(".checkdate").focus(function () {
			
	        $(".checkdate").change(function () {
				console.log("custom dates");
			});
			
		});
		
		$(".checkdate").focusout(function () {
			
	        var start = $('#start_date').val();
	        var end = $('#end_date').val();
			
	        if (start != "" && end != "") {
				var stsplit = start.split("/");
				var ensplit = end.split("/");
				
				start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
				end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
				
				ValidRange(start, end);
			}
			
		});
		
		function ValidRange(start, end) {
	        var startDate = Date.parse(start);
	        var endDate = Date.parse(end);
			
	        // Check the date range, 86400000 is the number of milliseconds in one day
	        var difference = (endDate - startDate) / (86400000 * 7);
	        if (difference < 0) {
				alert("The start date must come before the end date.");
				return false;
				} else {
				return true;
			}
			
	        return true;
			
			
		}
		
		$("form").submit(function(event){
			
			if($('#start_date').val() != '' && $('#end_date').val() != ''){
					var start = $('#start_date').val();
					var end = $('#end_date').val();
					
					if (start != "" && end != "") {
						var stsplit = start.split("/");
						var ensplit = end.split("/");
						
						start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
						end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
						
						ValidRange(start, end);
					}
					}else{
					alert("Select the date");
				}
				
				event.preventDefault();
			
		});
	
	$('#searchapp').keyup(function(){
		tableContainer.fnFilter(this.value);
	});
	
	$('#aplicationSearchResults').on('click','tbody tr',function() {
		tableContainer.$('tr.row_selected').removeClass('row_selected');
        $(this).addClass('row_selected');
        var url= tableContainer.fnGetData( this,1);
        //var url = '/wtms/application/view/'+applicationNumber ;
		$('#applicationSearchRequestForm').attr('method', 'get');
		$('#applicationSearchRequestForm').attr('action', url);
		$('#applicationSearchRequestForm').attr('mode', 'search');
		//window.location=url;
		window.open(url,'window','scrollbars=yes,resizable=yes,height=700,width=800,status=yes');
		 
	});
		
});
