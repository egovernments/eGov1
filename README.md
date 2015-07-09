# eGov Opensource [![Build Status](http://ci.egovernments.org/buildStatus/icon?job=eGov-Github-Master)](http://ci.egovernments.org/job/eGov-Github-Master/) [![Join the chat at https://gitter.im/egovernments/eGov](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/egovernments/eGov?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

eGovernments Foundation transforms urban governance with the use of scalable and replicable technology solutions that enable efficient and effective municipal operations, better decision making, and contact-less urban service delivery.

Our comprehensive software products enable Governments to put their resources to efficient use by minimising overheads. We also help bring in transparency, accountability and citizen centricity in the delivery of Government services.

eGovernments Foundation has been in the forefront of implementing eGovernance solutions since 2003. Our products have been serving over 275 ULBs across the country. Our time tested products have impacted the ULBs in a large way. We have also been involved in several eGovernance initiatives in the country.

Our primary business motivator is to increase the footprint of eGovernance across the country and help adoption in as many ULBs as possible. Going opensource with our products is a measure in this direction. It also gives us the ability to tap into the immense talent pool in India for strengthening and improving our cities. Open source also blends well with our ethical fabric of being open and transparent in our business.

#### Issue Tracking
Report issues via the [eGov Opensource JIRA][].
#### License
The eGov suit is released under version 3.0 of the [GPL][].
## User Guide
This section contains steps that are involved in build and deploy the application.

#### Prerequisites

* Install [maven >= v3.2.x][Maven]
* Install [PostgreSQL >= v9.3 ][PostgreSQL]
* Install [Elastic Search >= v1.4.2][Elastic Search]
* Install [Jboss Wildfly v9.0.x][Wildfly Customized]
* [Git][] and [JDK 8 update 20 or later][JDK8 build]

#### Database Setup
Create a database and user in postgres

#### Elastic Search Setup
Elastic seach server properties needs to be configured in `elasticsearch.yml` under `<ELASTICSEARCH_INSTALL_DIR>/config`
```properties
cluster.name: elasticsearch-<username> ## Your local elasticsearch clustername, DO NOT use default clustername
transport.tcp.port: 9300 ## This is the default port

```

#### Building Source
1. Clone the eGov repository
```bash
$ mkdir egovgithub
$ cd egovgithub
$ git clone https://github.com/egovernments/eGov.git
```
2. Change directory to `<CLONED_REPO_DIR>/egov/egov-config/src/main/resources/config/` and create a file called `egov-erp-<username>.properties` and enter the following values based on your environment config.

 ```properties
 search.hosts=localhost
 search.port=9300
 search.clusterName=elasticsearch-<username>

 mail.enabled=false ##Enables or disabled email sending, this is enabled (true) by default
 mail.port=465
 mail.host=smtp.gmail.com
 mail.protocol=smtps
 mail.sender.username=abc123@gmail.com
 mail.sender.password=12345

 sms.enabled=false  ##Enables or disables SMS sending, this is enabled (true) by default
 sms.provider.url=http://some.sms.provider.url
 sms.sender.username=sms_username
 sms.sender.password=sms_user_password
 sms.sender=sms_sender_id

 #Following are the http sms request parameter names, replace with sms provider specific request param name.
 sms.sender.req.param.name=senderid
 sms.sender.username.req.param.name=username
 sms.sender.password.req.param.name=password
 sms.destination.mobile.req.param.name=mobileno
 sms.message.req.param.name=content

 #In addition to the above standard parameters, any additional static parameters can be added here with 
 #respective key=value, delimit with &
 sms.extra.req.params=foo=bar

 #SMS response error codes, replace with sms provider specific error code
 sms.error.codes=401,403,404,405,406,407,408,409,410,411,412,413,414

#If sms gategway response doesn't contain error message, to log error messages for the above code then add error message entry like following
#<sms_errorcode>=<sms_error_message>
#eg:401=Invalid Username or Password

 ```
One can override any default settings available in `/egov/egov-egi/src/main/resources/config/application-config.properties` by adding an entry in `egov-erp-<username>.properties`.

  Database properties are defined in the `persistence-config.properties`.

  ```properties
  db.url=jdbc:postgresql://localhost:5432/postgres
  db.username=erp_owner
  db.password=erp_owner
  ```

3. Change directory back to `<CLONED_REPO_DIR>/egov`
4. Run the following commands, this will cleans, compiles, tests, migrates database and generates ear artifact along with jars and wars appropriately

 ```bash
 mvn clean package -s settings.xml -Pdb
 ```

#### Deploying Application

##### Configuring JBoss Wildfly

1. Download and install customized JBoss Wildfly Server from [here][Wildfly Customized]. This server contains some additional jars that are required for the ERP.
2. In case properties needs to be overridden, edit the below file (This is only required if `egov-erp-<username>.properties` is not present)

  ```
  <JBOSS_HOME>/modules/system/layers/base/

  org
  └── egov
    └── settings
      └── main
          ├── config
          │   └── egov-erp-override.properties
          └── module.xml
  ```
3. Update settings in `standalone.xml` under `<JBOSS_HOME>/standalone/configuration`
 * Check Datasource setting is in sync with your database details.
  ```
  <connection-url>jdbc:postgresql://localhost:5432/<YOUR_DB_NAME></connection-url>
  <security>
    <user-name><YOUR_DB_USER_NAME></user-name>
    <password><YOUR_DB_USER_PASSWORD></password
  </security>
  ```
 * Check HTTP port configuration is correct in
  ```
  <socket-binding name="http" port="${jboss.http.port:8080}"/>
  ```
4. Change directory back to `<CLONED_REPO_DIR>/egov` and run the below command
  ```
   $  chmod +x deploy-local.sh
$ ./deploy-local.sh
  ```

 Alternatively this can be done manually by following the below steps.

  * Copy the generated exploded ear `<CLONED_REPO_DIR>/egov/egov-ear/target/egov-ear-1.0-SNAPSHOT` in to your JBoss deployment folder `<JBOSS_HOME>/standalone/deployments`
  * Rename the copied folder `egov-ear-1.0-SNAPSHOT` to `egov-ear-1.0-SNAPSHOT.ear`
  * Create or touch a file named `egov-ear-1.0-SNAPSHOT.ear.dodeploy` to make sure JBoss picks it up for auto deployment

5. Start the wildfly server by executing the below command

  ```
   $ cd <JBOSS_HOME>/bin/
   $ nohup ./standalone.sh -Dspring.profiles.active=production -b 0.0.0.0 &

  ```
  `-b 0.0.0.0` only required if application accessed using IP address or  domain name.

6. Monitor the logs and in case of successful deployment, just hit `http://localhost:<YOUR_HTTP_PORT>/egi` in your favorite browser.
7. Login using username as `egovernments` and password `demo`

#### Accessing the application using IP address and domain name

This section is to be referred only if you want the application to run using any ip address or domain name.

###### 1. To access the application using IP address:
* Have an entry in eg_city_website table in database with an IP address of the machine where application server is running (for ex: citibaseurl="172.16.2.164") to access application using IP address.
* Access the application using an url http://172.16.2.164:8080/egi/ where 172.16.2.164 is the IP and 8080 is the port of the machine where application server is running.

###### 2. To access the application using domain name:

* Have an entry in eg_city_website table in database with domain name (for ex: citibaseurl= "www.egoverpphoenix.org") to access application using domain name.
* Add the entry in hosts file of your system with details as 172.16.2.164    www.egoverpphoenix.org (This needs to be done both in server machine as well as the machines in which the application needs to be accessed since this is not a public domain).
* Access the application  using an url http://www.egoverpphoenix.org:8080/egi/ where www.egoverpphoenix.org is the domain name and 8080 is the port of the machine where application server is running.

Always start the wildfly server with the below command to access the application using IP address or  domain name.
```
 nohup ./standalone.sh -b 0.0.0.0 -Dspring.profiles.active=production &
```

## Developer Guide
This section gives more details regarding developing and contributing to eGov suit.

#### Repository Structure
`egov` - folder contains all the source code of eGov opensource projects
#### Check out sources
`git clone git@github.com:egovernments/eGov.git` or `git clone https://github.com/egovernments/eGov.git`
#### Prerequisites

* Install [maven >= v3.2.x][Maven]
* Install your favorite IDE for java project. Recommended Eclipse or IntelliJ
* Install [PostgreSQL >= v9.3 ][PostgreSQL]
* Install [Elastic Search >= v1.4.2][Elastic Search]
* Install [Jboss Wildfly v9.0.x][Wildfly Customized]
* [Git][] and [JDK 8 update 20 or later][JDK8 build]

__Note__: Please check in [eGov Tools Repository] for any of the above software installables before downloading from internet.


##### 1. Eclipse Deployment

* Install [Eclipse Mars] [Eclipse Mars]
* Import the cloned git repo using maven Import Existing Project.
* Install Jboss Tools and configure Wildfly Server.
* Since jasperreport related jar's are not available in maven central, we have to tell eclipse to find jar's in alternative place for that navigate to `Windows -> Preference -> Maven -> User Settings -> Browse Global Settings` and point settings.xml available under egov-erp/
* Double click on wildfly9.x --> open launch configurations --> edit VM arguments and add string '-Dspring.profiles.active=production' at the end of existing VM arguments.
* Now add your EAR project into the configured Wildfly server.
* Start Wildfly in debug mode, this will enable hot deployment.

##### 2. Intellij Deployment

* TODO - Contribute

##### 3. Database Migration Procedure

* Any new sql files created should be added under directory `<CLONED_REPO_DIR>/egov/egov-database/src/main/resources/sql`
* Uses the database properties from `<CLONED_REPO_DIR>/egov/egov-database/src/main/resources/liquibase.properties` for migration
* All sql scripts should be named with incremental number prefix and .sql suffix
* Format `<sequence>_<module>_<description>_<database-statement-type>.sql`

##### Examples
```
1_egi_create-deparment_DDL.sql
2_eis_add-employee-role_DML.sql
```
For More details refer [Liquibase]


##### 4. Targets to Build, Package and Upgrade database
* Run the following commands in developement enviornment when there is no database changes.
```bash
mvn -s settings.xml clean compile ## Cleans your build directory and compiles your java code
mvn -s settings.xml clean test    ## Cleans, compiles and runs unit, integration tests
mvn -s settings.xml package       ## Cleans, compiles, tests and generates ear artifact along with jars and wars approproiately
```
* When there is a database change then database upgrade also needs to be done before building. That case use the command
```bash
mvn -s settings.xml package -Pdb  ## Cleans, compiles, tests, migrates database and generates ear artifact along with jars and wars approproiately
```

#  
Note: This system is supported only on Linux environment. Supported browser are-
* Chrome
* Firefox


[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[eGov Opensource JIRA]: http://issues.egovernments.org/browse/PHOENIX
[Wildfly Customized]: http://downloads.egovernments.org/wildfly-9.0.0.Final.zip
[Eclipse Mars]: https://eclipse.org/downloads/packages/release/Mars/M1
[Elastic Search]: https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.4.2.zip
[Spring Profiles]: http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-environment
[Liquibase]: http://www.liquibase.org/documentation/index.html
[eGov Tools Repository]: http://182.74.137.193/downloads/
[PostgreSQL]: http://www.postgresql.org/download/
[Maven]: http://maven.apache.org/download.cgi
[GPL]: http://www.gnu.org/licenses/
