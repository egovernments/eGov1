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
$(document).ready(function()
{
	// jQuery plugin to prevent double submission of forms
	jQuery.fn.preventDoubleSubmission = function() {
	  $(this).on('submit',function(e){
	    var $form = $(this);
	 
	    if ($form.data('submitted') === true) {
	      // Previously submitted - don't submit again
	      e.preventDefault();
	    } else {
	      // Mark it so that the next submit can be ignored
	      $form.data('submitted', true);
	    }
	  });
	 
	  // Keep chainability
	  return this;
	};
	
	$('form').preventDoubleSubmission();
	
	$(".is_valid_number").on("input", function(){
        var regexp = /[^0-9]/g;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	$(".is_valid_alphabetWithsplchar").on("input", function(){
		var regexp = /[^A-Z_-]*$/g;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	$(".is_valid_alphabet").on("input", function(){
		var regexp = /[^a-zA-Z ]/g;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	
	$(".is_valid_alphaNumWithsplchar").on("input", function(){
		var regexp =  /[^a-zA-Z0-9_@./#&+-]*$/;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	
	$(".is_valid_alphanumeric").on("input", function(){
		var regexp = /[^a-zA-Z _0-9]/g;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	$(".is_valid_letters_space_hyphen_underscore").on("input", function(){
        var regexp = /[^a-zA-Z _0-9_-]/g;
		if($(this).val().match(regexp)){
			$(this).val( $(this).val().replace(regexp,'') );
		}
	});
	try { $('.twitter-typeahead').css('display','block'); } catch(e){}
	
	try { $(":input").inputmask(); }catch(e){}
	
	try { 
		$(".datepicker").datepicker({
			format: "dd/mm/yyyy",
			autoclose: true 
		}); 

		var d = new Date();
		var currDate = d.getDate();
		var currMonth = d.getMonth();
		var currYear = d.getFullYear();
		var startDate = new Date(currYear,currMonth,currDate);
		$('.today').datepicker('setDate',startDate);

		}catch(e){
		console.warn("No Date Picker");
	}
	
	try { 
		$('[data-toggle="tooltip"]').tooltip({
			'placement': 'bottom'
		});
		}catch(e){
		console.warn("No tooltip");
	}
	
	$("a.open-popup").click(function(e) {
		window.open(this.href, ''+$(this).attr('data-strwindname')+'', 'width=900, height=700, top=300, left=260,scrollbars=yes'); 
		return false;
	});
	
	$(".form-horizontal").submit(function( event ) {
		$('.loader-class').modal('show', {backdrop: 'static'});
	});
	
	//fade out success message
	$(".alert-success").fadeTo(2000, 500).slideUp(500, function(){
        $(".alert-success").alert('close');
    });

	$('.upload-file').change(function(e){
		var fileInput = $(this);
   		var maxSize = 20971520; //file size  in bytes(20MB)
		var inMB = maxSize/1024/1024;
		if(fileInput.get(0).files.length){
			var fileSize = this.files[0].size; // in bytes
			var charlen = (this.value.split('/').pop().split('\\').pop()).length;
			if(charlen > 50){
				alert('File length should not exceed 50 characters!');
				fileInput.replaceWith(fileInput.val('').clone(true));
				return false;			
			}else if(fileSize > maxSize){
				alert('File size should not exceed '+inMB+' MB!');
				fileInput.replaceWith(fileInput.val('').clone(true));
				return false;
			    }
		
			
		}
	});
	
	/*$(".alert-danger").fadeTo(2000, 500).slideUp(500, function(){
        $(".alert-danger").alert('close');
    }); */
	
	$('.inboxload').click(function(){
		window.close();
		parent.window.opener.inboxloadmethod();
	});
	
});

/*$(".refreshInBox refeshDraft").on('click', function() {
	if(window.opener)
		window.opener.refreshInbox();
});*/
