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

- Credit card string is valid or null
- Credit card string matches a regexp (no alphanumeric or special characters are allowed)
- Validity of credit card string (validity decided by means of Luhn algorithm)

**Predicates for method *isValidCreditCard*:**

| Criteria                  | Predicate                                 |
| ----------------------  | ------------------------------------    |
| Credit card string is valid or null   |            Valid               |
|                                       |            NULL                 |
| Credit card string matches a regexp   |            Yes                |
|                                       |            No                 |
| Validity of credit card string (Luhn) | Valid                      |
|                                       | Invalid format        |


**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


|valid or null|Credit card matches regexp|Validity of Credit card | Valid / Invalid | Description of the test case | JUnit test case |
----|------|-------|-------|-------|-----|
|NULL|*|*|Invalid|boolean isValid = ez.isValidCreditCard(null); <br /> -> return false|Class TestEZShop_IsValidCreditCard, method testCreditCardValidity_null()|
|Valid|Yes|Valid|Valid|  boolean isValid = ez.isValidCreditCard("4485370086510891"); <br />-> return true| Class TestEZShop_IsValidCreditCard, method testCreditCardValidity_correct()|
|''|''|NULL/Invalid format | Invalid | boolean isValid = ez.isValidCreditCard("1849264958268091"); | Class TestEZShop_IsValidCreditCard, method testCreditCardValidity_notRespectingLuhnAlgo() |
|''|No| * | Invalid| boolean isValid = ez.isValidCreditCard("4Z85a70b8F51c89D1")<br /> -> return false <br /> or: isValidCreditCard("345");<br /> -> return false <br /> or: isValidCreditCard("#4485370086510891"); <br /> -> return false <br /> or: isValidCreditCard(";-!"); <br /> -> return false <br /> | Class TestEZShop_IsValidCreditCard, methods testCreditCardValidity_alphanumeric(), testCreditCardValidity_lessDigits(), testCreditCardValidity_hashtag(), testCreditCardValidity_specialCharacters() |


### **Class *EZShop* - method *getCreditInTXTByCreditCard***


**Criteria for method *getCreditInTXTByCreditCard*:**


- Validity of credit card string
- Correct card balance (verifying CreditCards.txt file)



**Predicates for method *getCreditInTXTByCreditCard*:**

| Criteria | Predicate |
| -------- | --------- |
|    Validity of credit card string      |     Valid                    |
|                                        |     NULL/Invalid format      |
|    Correct card balance                |     Correct                  |
|                                        |     Incorrect                |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Validity of credit card string | Correct card balance | Valid / Invalid | Description of the test case | JUnit test case |
-------|-------|-------|-------|-------|
|NULL/Invalid|*|Invalid|boolean value = ez.getCreditInTXTbyCardNumber(null); <br/>or:<br/> boolean value = ez.getCreditInTXTbyCardNumber("4Z85a70b8F51c89D1");<br/>or:<br/>boolean value = ez.getCreditInTXTbyCardNumber("345");<br/>or:<br/>boolean value = ez.getCreditInTXTbyCardNumber("#");<br/>or:<br/>boolean value = ez.getCreditInTXTbyCardNumber(";-!");|Class TestEZShop_getCreditInTXTByCreditCard, methods testGetCredit_null(), testGetCredit_alphanumeric(), testGetCredit_lessDigits(), testGetCredit_hashtag(), testGetCredit_specialCharacters() |
|Valid|Correct|Valid| value = ez.getCreditInTXTbyCardNumber("4485370086510891");|Class TestEZShop_getCreditInTXTByCreditCard, method testGetCredit_correct()|
|''|Incorrect|Invalid|''|''|



### **Class *EZShop* - method *updateCreditInTXTbyCreditCard***


**Criteria for method *updateCreditInTXTbyCreditCard*:**


- Validity of credit card string
- Correct updating of card balance (verifying CreditCards.txt file)



**Predicates for method *updateCreditInTXTbyCreditCard*:**

| Criteria | Predicate |
| -------- | --------- |
|    Validity of credit card string      |     Valid                    |
|                                        |     NULL/Invalid format      |
|    Correct updating of card balance    |     Correct                  |
|                                        |     Incorrect                |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Validity of credit card string | Correct updating of card balance | Valid / Invalid | Description of the test case | JUnit test case |
-------|-------|-------|-------|-------|
|NULL/Invalid|*|Invalid|boolean isCorrect = ez.updateCreditInTXTbyCardNumber(null, 50.0);<br/>or:<br/>boolean isCorrect = ez.updateCreditInTXTbyCardNumber("A4DJSKID91864F", 50.0);<br/>or:<br/>boolean isCorrect = ez.updateCreditInTXTbyCardNumber("#", -50.0);<br/>or:<br/>boolean isCorrect = ez.updateCreditInTXTbyCardNumber(";-!", -50.0); | Class TestEZShop_updateCreditInTXTbyCreditCard, methods testUpdateCredit3_nullCard(), testUpdateCredit4_invalidCard(), testUpdateCredit5_hashtag(), testUpdateCredit6_specialCharacters() |
|Valid|Correct|Valid|boolean isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", 50.0);<br/>or:<br/>boolean isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", -50.0);<br/>or:<br/> boolean isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", Double.MIN_VALUE);<br/>or:<br/>boolean isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", -Double.MIN_VALUE);|Class TestEZShop_updateCreditInTXTbyCreditCard, methods testUpdateCredit1_correct(), testUpdateCredit2_negativeDouble(), testUpdateCredit7_Min(), testUpdateCredit8_Min_Neg()|
|''|Incorrect|Invalid|''|''|


### **Class *EZCustomer* - method *setCustomer***
**Criteria for method *setCustomer*:**


**Predicates for method *setCustomer*:**

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

### **Class *EZSaleTransaction* - method *setTicketNumber***
**Criteria for method *setTicketNumber*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setTicketNumber*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setTicketNumber(200); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setEntries***
**Criteria for method *setEntries*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setEntries*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setEntries(list); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setDiscountRate***
**Criteria for method *setDiscountRate*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setDiscountRate*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setDiscountRate(0.90); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setPrice***
**Criteria for method *setPrice*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setPrice*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setPrice(400.12); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setStatus***
**Criteria for method *setStatus*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setStatus*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setStatus(EZSaleTransaction.STPayed); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setAttachedCard***
**Criteria for method *setAttachedCard*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setAttachedCard*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setAttachedCard("0000000001"); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|


### **Class *EZSaleTransaction* - method *setReturns***
**Criteria for method *setReturns*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setReturns*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.setReturns(list_r); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|''|''|

### **Class *EZSaleTransaction* - method *hasRequiredStatus***
**Criteria for method *hasRequiredStatus*:**

- Valid input

**Predicates for method *hasRequiredStatus*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid input        |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid input | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| sale.hasRequiredStatus(null) |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|Invalid|Invalid|sale.hasRequiredStatus(EZSaleTransaction.STClosed)|''|


### **Class *EZSaleTransaction* - method *updatePrice***
**Criteria for method *updatePrice*:**

- Correct updating of the price (checked by means of the related get method)

**Predicates for method *updatePrice*:**

| Criteria | Predicate |
| -------- | --------- |
|    Correct price updating     |      Yes         |
|                               |      No          |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Correct price updating | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Yes|Valid| sale.updatePrice(30.20);<br/>or:<br/>sale.updatePrice(-30.20); |Class TestEZShop_SaleTransaction, method testSaleTransaction()|
|No|Invalid|''|''|

### **Class *EZAccountBook* - method *updateBalance***
**Criteria for method *updateBalance*:**

- Correct updating of the balance (checked by means of the related get method)

**Predicates for method *updateBalance*:**

| Criteria | Predicate |
| -------- | --------- |
|    Correct balance updating      |       Yes         |
|                                  |       No          |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Correct balance updating | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| book.updateBalance(100.55); | Class TestEZShop_AccountBook, method TestEZShop_AccountBook()|
|Invalid|Invalid|''|''|


### **Class *EZAccountBook* - method *setCurrentBalance***
**Criteria for method *setCurrentBalance*:**

- Valid setting (checked by means of the related get method)

**Predicates for method *setCurrentBalance*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid setting      |       Valid         |
|                       |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid setting | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| book.setCurrentBalance(12345.67); |Class TestEZShop_AccountBook, method TestEZShop_AccountBook()|
|Invalid|Invalid|''|''|


### **Class *EZAccountBook* - method *addBalanceOperation***
**Criteria for method *addBalanceOperation*:**

- Valid insertion of a new balance operation

**Predicates for method *addBalanceOperation*:**

| Criteria | Predicate |
| -------- | --------- |
|    Valid insertion of balance operation      |       Valid         |
|                                              |      Invalid        |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |

**Combination of predicates**:


| Valid insertion of balance operation | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Valid|Valid| book.addBalanceOperation(shopDB2, 150.33, balanceOperations)<br/>or:<br/>book.addBalanceOperation(shopDB2, -150.33, balanceOperations)<br/>or:<br/>book.addBalanceOperation(shopDB2, -10.0, balanceOperations) |Class TestEZShop_AccountBook, method TestEZShop_AccountBook()|
|Invalid|Invalid|''|''|




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
|isValidCreditCard()|11-22|equals to the number of characters in credit card string |Methods in TestEZShop_isValidCreditCard (testCreditCardValidity_correct, testCreditCardValidity_null, etc.)|
|getCreditInTXTByCreditCard()|14-25|equals to the number of lines in CreditCards.txt file|Methods in TestEZShop_getCreditInTXTByCreditCard (testGetCredit_correct, testGetCredit_null, etc.) |
|updateCreditInTXTbyCreditCard()|17-36|equals to the number of lines in CreditCards.txt file|Methods in TestEZShop_updateCreditInTXTbyCreditCard (testUpdateCredit1_correct, testUpdateCredit2_negativeDouble, etc.)|



