# SOAP service 

A SOAP service which returns XML Strings for different user queries like getting all appointment reservations, adding appointments, Phlebotomists, physicians to the database.  This service is also capable of validating data layer according to the methods implemented in the business layer. Functionality of this service can be seen using "SOAPUI" Tool by pointing a WSDL file of this project, you can get WSDL file by deploying this project to glassfish server.

### Service Capabilities:

* Get all reserved appointments .
* Get single appointment based on specific ID.
* Add new appointment to the database (Business layer checks for any overlapping and duplication).
* Get all registered Phlebotomists. 
* Add new Phlebotomists.
* Get all registered Physicians.
* Add new Physicians.
* Get all PSC.
* Add new PSC.

### Technology Stack

* SOAP Service- Simple Object Access Protocol
* Java
* Java Hibernate
* SoapUI
