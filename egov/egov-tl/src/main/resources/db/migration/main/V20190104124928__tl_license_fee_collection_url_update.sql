UPDATE eg_action set url='/license/fee/collect/' WHERE name='TL_LICENSE_FEE_COLLECTION';
DELETE FROM eg_feature_action WHERE action=(SELECT id FROM eg_action WHERE name='TL_LICENSE_BILL_COLLECT');
DELETE FROM eg_roleaction WHERE actionid=(SELECT id FROM eg_action WHERE name='TL_LICENSE_BILL_COLLECT');
DELETE FROM eg_action WHERE name='TL_LICENSE_BILL_COLLECT';


select * from newone.egtl_license where appl_num='00004-2019-YB';
select * from newone.eg_demand where id=19