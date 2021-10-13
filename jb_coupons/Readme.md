# John Bryce Coupon System Project - stage 1.

This is the first stage of John Bryce course 822-120 Coupon System project.
This stage represents system core, which is unit that includes user input validation, login credentials check, business logic, database connections and basic tests.

### Prerequisites
To run this app you need: 
* Eclipse IDE 
* Maven plugin for eclipse
* MySql server 

### Getting Started
Before you start the app you should get MySql server running.

You should add MySql user account with those credentials:

**username: jbcp**

**password: admin**

as this is the account the app is set to use.

MySQL command you can use to create MySQL user account account:
`mysql> CREATE USER 'jbcp'@'localhost' IDENTIFIED BY 'admin';`

To start the app you should run **testModule.Program**.

### Using the app
As you launch the application, it should connect to MySQL database and search for schema **jb_coupons**.

If it doesn't find this schema, which is preferred flow,  it should create the schema and then copy values of category description from *javaBeans.Category*  into *jb_coupons.categories* table.
If it finds existing schema it should only synchronize values  of category description from *javaBeans.Category*  into *jb_coupons.categories* table. You can disable this operations by commenting out line 41 in *testModule.hardCodedTests.Test class*.

If for some reason application can not create database, you can create it manually using MySQL commands which will be posted at the botton of the document. Then you will need to populate categories table by uncommenting lines 49-50 in *testModule.hardCodedTests.Test class*. If for some reason application fails to populate *categories* table, you will need to do this manually in order to run hard-coded tests.

After this stage is complete, the application should launch daily task of cleaning expired coupons. This task is intended to run automatically every 24 hours and delete coupons whose end date has expired.

After this stage is complete the application should perform series of hard-coded tests to test all operations of client facades. It calls three methods to test each client facade separately.

After this stage is complete the application should launch interactive console menu which will allow more flexible selective testing. These menus allow testing of almost all functions of client facade.
To use these tests you should follow onscreen instructions.
To stop application you should hit '4' and the application will quit.

In order to use menu tests you need to log in as administrator first (option '1'), then you can see/create/edit/delete other clients.

**To log in as administrator use these credentials:**

login: **admin@admin.com**

password: **admin**  


**Note**: all hard-coded tests use constant client/coupon/company IDs, which are fixed to match appropriate database entries during the first run only. On each next run hard-coded tests on the same schema, some tests will fail or perform inconsistently. To avoid this you should delete schema 'jb_coupons' before each next run of application, or truncate tables *coupons, customers, companies, customers_vs_coupons*. You may use MySQL command: `DROP SCHEMA jb_coupons;`.

### Internal structure of application
This unit consists of several modules:
* dailyJob 
* dao
* exceptions
* facadesModule
* javaBeans 
* jdbc
* loginManager
* testModule
* testModule.consoleMenu
* testModule.databaseSetup
* testModule.hardCodedTests
* utilityModule

**dailyJob.CouponExpirationDailyJob** starts automatically and performs daily task of cleaning coupons whose end date has expired.

**exceptions** contains custom exceptions:

*InvalidInputException* extends *Exception* - is used to indicate failure caused by incorrect user input.

*DBOperationException* extends *Exception* - is used to indicate general failure of connecting to database / working with database. 

*DBCompanyOperationException* extends *DBOperationExpetion* - is used to indicate failure when company object attempts to perform CRUD operation database.

*DBCustomerOperationException* extends *DBOperationExpetion* - is used to indicate failure when customer object attempts to perform CRUD operation database.

*DBCouponOperationException* extends *DBOperationExpetion* - is used to indicate failure when coupon object attempts to perform CRUD operation database.

**facadesModule** contains client type enum consisting of three client types and three client facades classes accordingly: *adminFacade*, *companyFacade*, *customerFacade*. These facades perform most of business logic of the application. They use *clientMsg* variable for feedback about status of database operation request. This *clientMsg* message variable is used by all tests and may be used by front end module.

**javaBeans** contains POJO objects.

**jdbc** contains ConnectionPool class used to connect to database.

**loginManager** contains LoginManager class used log in a client.

**ulilityModule** contains *dataValidator* class which is used to validate user input. *dataValidator* object is created by *clientFacade* object each time a *clientFacade* object is created. All data that comes from user must be checked by *dataValidator* object for any invalid or harmful input. Input data that is not validated should not pass for further processing. *dataValidator* uses *clientMsg* message variable for feedback about input status. *clientMsg* variable of *dataValidator* object is used by facade object which created *dataValidator* object to inform user about status of database operation request. 

**testModule** contains *Program* class which is used to run the app.

**testModule.databaseSetup** contains *mySqlSchemaSetup* class which is used to setup MySQL database for testing purposes. This class does not use *jdbc.connectionPool* class, it has its own database connection setup.

**testModule.consoleMenu** contains four console menu classes for testing purposes. *MainMenu* - main menu class which creates objects of three other classes: *AdminMenu*, *CompanyMenu*, *CustomerMenu*, which present user with appropriate menus.

**testModule.hardCodedTests** contains *test* class, which is called by *Main* method, in *testModule.Program* and runs all the tasks and tests. It creates *databaseSetupObject* to prepare database, then starts *dailyJob*, then runs all methods that perform hard-coded tests, launches console menu, and at the end stops *dailyJob* when application is requested to stop.

**dao** contains data  access objects which are used to communicate with database. There are three DAO APIs to connect to database and perform CRUD operations, and three classes that implement those APIs accordingly. 
Besides project requirements regarding all methods these classes should contain, there are some additional methods that have been added for convenience.

Here are the lists of all methods in each DAO APIs and DAO implementations: 
 
*CompaniesDAO* methods:

* `void addCompany(Company company);`
* `void updateCompany(Company company);`
* `void deleteCompany(int companyID);`
* `ArrayList<Company> getAllCompanies();`
* `Company getOneCompany(int companyID);`
* `Company getCompanyByName(String name);`
* `Company getCompanyByEmail(String email); `

*CouponsDAO* methods:
* `void addCoupon(Coupon coupon);`
* `void updateCoupon(Coupon coupon);`
* `void deleteCoupon(int CouponID);`
* `ArrayList<Coupon>getAllCoupons();`
* `ArrayList<Coupon> getCouponsByCompany(int companyID);`
* `ArrayList<Coupon> getCouponsByCompany(int companyID, Category category);`
* `ArrayList<Coupon> getCouponsByCompany(int companyID, double maxPrice);`
* `ArrayList<Coupon> getCouponsByCustomer(int customerID);`
* `ArrayList<Coupon> getCouponsByCustomer(int customerID, Category category);`
* `ArrayList<Coupon> getCouponsByCustomer(int customerID, double maxPrice);`
* `Coupon getOneCoupon(int couponID);`
* `boolean isPurchased(int customerID, int couponID); `
* `void addCouponPurchase(int customerID, int couponID);`
* `void deleteCouponPurchase(int customerID, int couponID);`
* `int deleteCouponsOlderThan(Date currentDate);`

*CustomersDAO* methods:
* `boolean isCustomerExists(String email, String password);`
* `void addCustomer(Customer customer);`
* `void updateCustomer(Customer customer);`
* `void deleteCustomer(int customerID);`
* `ArrayList<Customer> getAllCustomers();`
* `Customer getOneCustomer(int customerID);`
* `Customer getOneCustomer(String email);`

### MySQL statements for creating schema
Here are the list of MySQL statements which were used in this application but also may be used manually to create or alter database: 

```
CREATE SCHEMA IF NOT EXISTS JB_COUPONS;
```
```
USE JB_COUPONS;
```
```
CREATE TABLE COMPANIES 
	(ID INT UNSIGNED NOT NULL AUTO_INCREMENT, 
	NAME VARCHAR(128) UNIQUE, 
	EMAIL VARCHAR(128) UNIQUE,
	PASSWORD VARCHAR(128),
	PRIMARY KEY(ID),
	INDEX (NAME)
);
```
```
CREATE TABLE CUSTOMERS (
	  ID INT UNSIGNED NOT NULL AUTO_INCREMENT, 
	  FIRST_NAME VARCHAR(128),
	  LAST_NAME VARCHAR(128), 
	  EMAIL VARCHAR(128) UNIQUE,
	  PASSWORD VARCHAR(128),
	  PRIMARY KEY(ID),
	  INDEX (EMAIL)
	);
```
```
CREATE TABLE CATEGORIES (
	  ID INT UNSIGNED NOT NULL AUTO_INCREMENT, 
	  NAME VARCHAR(64),
	  PRIMARY KEY(ID),
	  INDEX (NAME)
	);
```
```
CREATE TABLE COUPONS (
	  ID INT UNSIGNED NOT NULL AUTO_INCREMENT, 
	  COMPANY_ID INT UNSIGNED, 
	  CATEGORY_ID INT UNSIGNED,
	  TITLE VARCHAR(128),
	  DESCRIPTION VARCHAR(255),
	  START_DATE DATE,
	  END_DATE DATE,
	  AMOUNT INT,
	  PRICE DOUBLE,
	  IMAGE VARCHAR(255),
	  PRIMARY KEY(ID),
	  INDEX (TITLE),
	  CONSTRAINT FOREIGN KEY (COMPANY_ID) REFERENCES COMPANIES (ID)
	    ON DELETE CASCADE ON UPDATE CASCADE,
	  CONSTRAINT FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES (ID)
	    ON DELETE CASCADE ON UPDATE CASCADE
	);
```
```
CREATE TABLE CUSTOMERS_VS_COUPONS (
	  CUSTOMER_ID INT UNSIGNED, 
	  COUPON_ID INT UNSIGNED,
	  PRIMARY KEY(CUSTOMER_ID, COUPON_ID),
	  CONSTRAINT FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMERS (ID)
	    ON DELETE CASCADE ON UPDATE CASCADE,
	  CONSTRAINT FOREIGN KEY (COUPON_ID) REFERENCES COUPONS (ID)
	    ON DELETE CASCADE ON UPDATE CASCADE
	);
```

