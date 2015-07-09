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
jQuery(document).ready(function($)
{	
	// Instantiate the Bloodhound suggestion engine
	// Complaint Type auto-complere 
	var complaintType = new Bloodhound({
		datumTokenizer: function (datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/pgr/complaint/router/complaintTypes?complaintTypeName=%QUERY',
			filter: function (data) {
				// Map the remote source JSON array to a JavaScript object array
				return $.map(data, function (ct) {
					return {
						name:ct.name,
						value: ct.id
					};
				});
			}
		}
	});
	
	complaintType.initialize();
	
	$('#com_type').typeahead({
		hint: true,
		highlight: true,
		minLength: 3
		}, {
		displayKey: 'name',
		source: complaintType.ttAdapter()
		}).on('typeahead:selected', function(event, data){            
			$("#complaintTypeId").val(data.value);    
	    }).on('change',function(event,data){
    		if($('#com_type').val() == ''){
    			$("#complaintTypeId").val('');
    		}
        });
	
	
	
	//Boundary auto-complete
	$("#boundary_type_id").change(function(){
		$('#com_boundry').typeahead('destroy');
		var b_id = $("#boundary_type_id").val();
		$("#hiddenBoundaryTypeId").val(b_id);    
		var boundaries = new Bloodhound(
				{
					datumTokenizer : function(datum) {
						return Bloodhound.tokenizers
								.whitespace(datum.value);
					},
					queryTokenizer : Bloodhound.tokenizers.whitespace,
					remote : {
						url : '/pgr/complaint/router/boundaries-by-type?boundaryName=%QUERY&boundaryTypeId='+ b_id,
						filter : function(data) {
							// Map the remote source JSON array to a
							// JavaScript object array
							return $.map(data, function(boundList) {
								return {
									name: boundList.name,
									value: boundList.id
								};
							});
						}
					}
				});
	boundaries.initialize();
	$('#com_boundry').typeahead({
		hint: true,
		highlight: true,
		minLength: 3
		}, {
		displayKey: 'name',
		source: boundaries.ttAdapter()
		}).on('typeahead:selected', function(event, data){            
			$("#boundaryId").val(data.value);    
	    }).on('change',function(event,data){
    		if($('#com_boundry').val() == ''){
    			$("#boundaryId").val('');
    		}
        });
	});

	$('#routerSearch').click(function(e){
		if($('#boundaryId').val()=="")
			var bndryId=0;
		else
			var bndryId=$('#boundaryId').val();
		
		if($('#complaintTypeId').val()=="")
			var cmTypeId=0;
		else
			var cmTypeId=$('#complaintTypeId').val();
		oTable= $('#com_routing_search');
		oTable.dataTable({
			"sPaginationType": "bootstrap",
			"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
			"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			"autoWidth": false,
			"bDestroy": true,
			"ajax": {
			        	url : "/pgr/router/resultList-view",
			        	data : {
			        		boundaryTypeId : $('#boundary_type_id').val(),
			        		boundaryId : bndryId,
			        		complaintTypeId : cmTypeId
			        	}
			        },
			"columns" : [
			  { "mData" : "boundaryType",
				"sTitle" : "Boundary Type"
			  },
			  { "mData" : "boundary",
				"sTitle" : "Boundary"
			  }, 
			  { "mData" : "complaintType",
				"sTitle" : "Complaint Type"
			  }, 
			  { "mData" : "position",
				"sTitle" : "Position"
			  },
			  { "mData" : "routerId",
				"visible": false
			  }]
				});
		e.stopPropagation();
	});
	
	$('#com_routing_search').on('click','tbody tr',function() {
		if ($(this).hasClass('apply-background')) {
			$(this).removeClass('apply-background');
		}else {
			$('#com_routing_search tbody tr')
					.removeClass('apply-background');
			$(this).addClass('apply-background');
		}
		oTable.$('tr.row_selected').removeClass('row_selected');
        $(this).addClass('row_selected');
        var fid= oTable.fnGetData( this, 4 );
        window.open('/pgr/router/view/'+ fid, '', 'height=800,width=800');
	
	});
		
});
