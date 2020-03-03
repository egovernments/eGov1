create sequence SEQ_APPEAL_REASON;
CREATE TABLE egpt_appeal_reason
(
  id bigint NOT NULL,
  version numeric DEFAULT 0,
  name character varying(50),
  description character varying(50),
  code character varying(50),
  order_id bigint,
isactive boolean NOT NULL DEFAULT true
);

COMMENT ON TABLE egpt_appealpetition_reason IS 'Master table for property appeal petition list';
COMMENT ON COLUMN egpt_appealpetition_reason.id IS 'Primary Key';
COMMENT ON COLUMN egpt_appealpetition_reason.name IS 'Name of appeal petition reason';


-------------------------document table for Appeal Petition--------------
CREATE TABLE EGPT_APPEAL_PETITION_REASON
(
  petition bigint,
  appeal_reasons bigint
);


Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG PLINTH AREA','Wrong Plinth Area','WRONG PLINTH',1);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG ZONE','Wrong Zone','WRONG ZONE',2);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG CLASSIFICATION','Wrong Classification','WRONG CLASSIFICATION',3);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG BUILDING AGE','Wrong Age of the Building','WRONG BUILDING AGE',4);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG OCCUPANCY','Wrong Occupancy','WRONG OCCUPANCY',5);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'WRONG UNAUTH PENALTY','Wrong Unauthorized Construction Penalty','WRONG UNAUTH PENALTY',6);

Insert into egpt_appeal_reason (id,version,name,description,code,order_id) values (nextval('SEQ_APPEAL_REASON'),1,'OTHERS','Others','OTHERS',7);

