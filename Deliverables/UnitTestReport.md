# Unit Testing Documentation

Authors:
- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Leonardo Palmucci s288126
- Dario Lanfranco s287524

Date: 16/05/2021

| Version | Changes |
| ------- |---------|
| 1 | Added first version of Unit Test Report document. |
| 2 | Modified the document and first version of WB testing. |
| 3 | Added some new black box tests

Tests regarding leaf classes (TestEZShop_Customer, User and so on) are not included in the document since they don't have specific criteria and predicates.

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)




- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

 ### **Class *EZShop* - method *isValidBarCode***



**Criteria for method *isValidBarCode*:**
	

 - Validity of BarCode
 - BarCode matches a regexp
 - BarCode matches an algorithm


**Predicates for method *isValidBarCode*:**


| Criteria                | Predicate |
| ------------------------ | --------- |
| Validity of BarCode          | Valid        |
|                              | NULL         |
| Barcode matches a regexp     | Yes          |
|                              | No           |
| BarCode matches an algorithm | Yes          |
|                              | No           |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |


**Combination of predicates**:

| Validity of BarCode | BarCode matches a regexp | BarCode matches an algorithm | Valid / Invalid | Description of the test case                                                                                                                                                                                             | JUnit test case                                                         |
|---------------------|--------------------------|------------------------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| Valid               | Yes                      | Yes                          | Valid           | boolean isValid = ez.isValidBarCode("6291041500213"); <br>-> return true <br>or: isValid = ez.isValidBarCode("6291041500213");<br>-> return true<br>or: isValid = ez.isValidBarCode("54326476412231");<br>-> return true | Class TestEZShop_VerifyBarCode,<br>method testBarCode_12/13/14digits(); |
| ""                  | ""                       | No                           | Invalid         | boolean isValid =  ez.isValidBarCode("54326476412234");<br>-> return false                                                                                                                                               | testBarCode_algorithm():                                                |
| ""                  | No                       | *                            | Invalid         | boolean isValid = ez.isValidBarCode("54326476412b31");<br>-> return false                                                                                                                                                | testBarCode_alphanumeric(), <br>testBarCode_notEnoughDigits()           |
| NULL                | *                        | *                            | Invalid         | boolean isValid = ez.isValidBarCode(null)<br>-> return false                                                                                                                                                             | testBarCode_nullInput();                                                |

### **Class *EZShop* - method *isValidCard***



**Criteria for method *isValidCard*:**


- Validity of Loyalty card code
- Loyalty card matches a regexp


**Predicates for method *isValidCard*:**


| Criteria                | Predicate |
| ------------------------ | --------- |
| Validity of Loyalty card code     | Valid                  |
|                                   | NULL                   |
| Loyalty card matches a regexp     | Yes                    |
|                                   | No                     |


**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |


**Combination of predicates**:
    
| Validity of Loyalty card code | Loyalty card matches a regexp | Valid / Invalid | Description of the test case                                     | JUnit test case                                           |
|-------------------------------|-------------------------------|-----------------|------------------------------------------------------------------|-----------------------------------------------------------|
| Valid                         | Yes                           | Valid           | boolean isValid = ez.isValidCard("2332543219");  -> return true  | Class TestEZShop_IsValidCard, testLoyaltyCode_10digits(); |
| ""                            | No                            | Invalid         | boolean isValid = ez.isValidCard("33235"); -> return false       | testLoyaltyCode_notEnoughDigits():                        |
| NULL                          | *                             | Invalid         | boolean isValid = ez.isValidCard(null) -> return false            | testLoyaltyCode_nullInput();                              |

### **Class *EZShop* - method *isValidPosition***



**Criteria for method *isValidPosition*:**


- Validity of Position string
- Position matches a regexp


**Predicates for method *isValidPosition*:**


| Criteria                | Predicate |
| ------------------------ | --------- |
| Validity of Position string       | Valid                  |
|                                   | NULL                   |
| Position matches a regexp         | Yes                    |
|                                   | No                     |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |


**Combination of predicates**:

| Validity of Position string | Position matches a regexp | Valid / Invalid | Description of the test case                                          | JUnit test case                                                   |
|-----------------------------|---------------------------|-----------------|-----------------------------------------------------------------------|-------------------------------------------------------------------|
| Valid                       | Yes                       | Valid           | boolean isValid = ez.isValidPosition("55-ADB-44");<br>-> return true  | Class TestEZShop_IsValidPosition,<br>method testPosition_Valid(); |
| ""                          | No                        | Invalid         | boolean isValid = ez.isValidPosition("33235");<br>-> return false     | testPosition_notValid();                                          |
| NULL                        | *                         | Invalid         | boolean isValid = ez.isValidPosition(null)<br>-> return false         | testPosition_nullInput();                                         |

### **Class *EZShop* - method *isValidCreditCard***


**Criteria for method *isValidCreditCard*:**

- Credit card string matches a regexp (no alphanumeric or special characters are allowed)
- Validity of credit card string (validity decided by means of Luhn algorithm)

**Predicates for method *isValidCreditCard*:**

| Criteria                  | Predicate                                 |
| ----------------------  | ------------------------------------    |
| Credit card string matches a regexp   |            Yes                |
|                                       |            No                 |
| Validity of credit card string        | Valid                      |
|                                       | NULL/Invalid format        |


**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


|Credit card matches regexp|Validity of Credit card | Valid / Invalid | Description of the test case | JUnit test case |
------|-------|-------|-------|-----|
|Yes|Valid|Valid|  boolean isValid = ez.isValidCreditCard("4485370086510891"); <br />-> return true| Class TestEZShop_IsValidCreditCard, method testCreditCardValidity_correct()|
|''|NULL/Invalid format | Invalid | boolean isValid = ez.isValidCreditCard("1849264958268091"); | Method testCreditCardValidity_notRespectingLuhnAlgo() |
|No| * | Invalid| boolean isValid = ez.isValidCreditCard(null); <br /> -> return false <br /> or: boolean isValid = ez.isValidCreditCard("4Z85a70b8F51c89D1")<br /> -> return false <br /> or: isValidCreditCard("345");<br /> -> return false <br /> or: isValidCreditCard("#4485370086510891"); <br /> -> return false <br /> or: isValidCreditCard(";-!"); <br /> -> return false <br /> | Methods testCreditCardValidity_null(), testCreditCardValidity_alphanumeric(), testCreditCardValidity_lessDigits(), testCreditCardValidity_hashtag(), testCreditCardValidity_specialCharacters() |



### **Class *EZCustomer* - method *setCustomerName***
**Criteria for method *setCustomerName*:**


**Predicates for method *name*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|*|Valid|customer = new EZCustomer(-1, "Marco","0000000500", 0);|Class TestEZShop_Customer, method testCustomer()|
|||||
|||||
|||||
|||||



### **Class *class_name* - method *name***



**Criteria for method *name*:**


-
-





**Predicates for method *name*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |





**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


| Criteria 1 | Criteria 2 | ... | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|-------|-------|
|||||||
|||||||
|||||||
|||||||
|||||||

# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name                | JUnit test case            |
|--------------------------|----------------------------|
| Method isValidCard()     | TestEZShop_IsValidCard     |
| Method isValidBarCode()  | TestEZShop_VerifyBarCode   |
| Method isValidPosition() | TestEZShop_IsValidPosition |
| Method isValidCreditCard() | TestEZShop_isValidCreditCard |
| Method getCreditInTXTByCreditCard() | TestEZShop_getCreditInTXTByCreditCard |
| Method updateCreditInTXTbyCreditCard() | TestEZShop_updateCreditInTXTbyCreditCard |

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >

    <insert the screen>

### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|isValidCard() |//|//|TestEZShop_IsValidCard|
|isValidBarCode()|8-22|0|TestEZShop_VerifyBarCode, method testBarCode_notEnoughDigits()|
|isValidBarCode()|8-22|12+|TestEZShop_VerifyBarCode, method testBarCode_12digits(), 13digits(), 14digits()|
|isValidPosition()|//|//|TestEZShop_IsValidPosition|
|isValidCreditCard()|11-22|depends on the credit card string length |Methods in TestEZShop_isValidCreditCard (testCreditCardValidity_correct, testCreditCardValidity_null, etc.)|
|getCreditInTXTByCreditCard()|14-25|depends on the length of the CreditCards.txt file|Methods in TestEZShop_getCreditInTXTByCreditCard (testGetCredit_correct, testGetCredit_null, etc.) |
|updateCreditInTXTbyCreditCard()|17-36|depends on the length of the CreditCards.txt file|Methods in TestEZShop_updateCreditInTXTbyCreditCard (testUpdateCredit1_correct, testUpdateCredit2_negativeDouble, etc.)|



