
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (nextval('seq_eg_wf_matrix'), 'ANY', 'WaterConnectionDetails', 'Rejected', NULL, 'Pending approval by Assistant Engineer', 'Revenue Clerk', 'NEWCONNECTION', 'Payment done against Estimation', 'AssistantEng Approval pending', 'Assistant engineer', NULL, 'Forward,Reject', NULL, NULL, '2015-08-01', '2099-04-01');

INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (nextval('seq_eg_wf_matrix'), 'ANY', 'WaterConnectionDetails', 'Rejected', NULL, 'Pending approval by Assistant Engineer', 'Revenue Clerk', 'ADDNLCONNECTION', 'Payment done against Estimation', 'AssistantEng Approval pending', 'Assistant engineer', NULL, 'Forward,Reject', NULL, NULL, '2015-08-01', '2099-04-01');

INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (nextval('seq_eg_wf_matrix'), 'ANY', 'WaterConnectionDetails', 'Rejected', NULL, 'Pending approval by Assistant Engineer', 'Revenue Clerk', 'CHANGEOFUSE', 'Payment done against Estimation', 'AssistantEng Approval pending', 'Assistant engineer', NULL, 'Forward,Reject', NULL, NULL, '2015-08-01', '2099-04-01');

