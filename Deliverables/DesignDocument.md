# Design Document 

Authors:

- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524

Date: 25/04/2021

| Version | Changes |
| ------- |---------|
| 1 | Added first version of design document. |
| 2 | Added functions in Shop | 
| 3 | Added sequence diagrams |

# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

<discuss architectural styles used, if any>
<report package diagram>

```plantuml
@startuml
package GUIEZShop
package EZShop
package Readers
GUIEZShop ..> EZShop
Readers ..> EZShop
@enduml
```

GUIEZShop contains view and controller, while EZShop contains model and logic. The architetural pattern choosed is MVC.

# Low level design

## EZShop Class Diagram

```plantuml
@startuml
left to right direction


class Shop{
    .. Reset ..
    +void reset()

    .. UC1 - Manage Products ..
    +Integer createProductType(String description, String productCode, double pricePerUnit, String note)
    +boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
    +boolean deleteProductType(Integer id)
    +List<ProductType> getAllProductTypes()
    +ProductType getProductTypeByBarCode(String barCode)
    +List<ProductType> getProductTypesByDescription(String description)

    .. UC2 - Manage user accounts and rights ..
    +Integer createUser(String username, String password, String role)
    +boolean deleteUser(Integer id)
    +List<User> getAllUsers()
    +User getUser(Integer id)
    +boolean updateUserRights(Integer id, String role)  
    
    .. UC3 - Manage Inventory and Orders ..
    +boolean updateQuantity(Integer productId, int toBeAdded)
    +boolean updatePosition(Integer productId, String newPos)
    +Integer issueReorder(String productCode, int quantity, double pricePerUnit)
    +Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
    +boolean payOrder(Integer orderId)
    +boolean recordOrderArrival(Integer orderId)
    +List<Order> getAllOrders()
    
    .. UC4 - Manage Customers and Cards ..
    +Integer defineCustomer(String customerName)
    +boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard)
    +boolean deleteCustomer(Integer id)
    +Customer getCustomer(Integer id)
    +List<Customer> getAllCustomers()
    +String createCard()
    +boolean attachCardToCustomer(String customerCard, Integer customerId)
    +boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded)

    .. UC5 ..
    +User login(String username, String password)
    +boolean logout()

    .. UC6 ..
    +Integer startSaleTransaction() throws UnauthorizedException
    +boolean addProductToSale(Integer transactionId, String productCode, int amount)
    +boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
    +boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
    +boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
    +int computePointsForSale(Integer transactionId)
    +boolean closeSaleTransaction(Integer transactionId)
    +boolean deleteSaleTicket(Integer ticketNumber)
    +Ticket getSaleTicket(Integer transactionId)
    +Ticket getTicketByNumber(Integer ticketNumber)

    .. UC7 ..
    +double receiveCashPayment(Integer ticketNumber, double cash)
    +boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard)

    .. UC8 ..
    +Integer startReturnTransaction(Integer ticketNumber)
    +boolean returnProduct(Integer returnId, String productCode, int amount)
    +boolean endReturnTransaction(Integer returnId, boolean commit)
    +boolean deleteReturnTransaction(Integer returnId)
    

    .. UC9 - Accounting ..
    +boolean recordBalanceUpdate(double toBeAdded)
    +List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to)
    +double computeBalance()

    .. UC10 - Manage return ..
    +double returnCashPayment(Integer returnId)
    +double returnCreditCardPayment(Integer returnId, String creditCard)
}

class User{
    +username
    +password
    +privilege
}

Shop -- "*" User

class AccountBook 
AccountBook - Shop
class FinancialTransaction {
 +description
 +amount
 +date
}
AccountBook -- "*" FinancialTransaction

class Credit 
class Debit

Credit --|> FinancialTransaction
Debit --|> FinancialTransaction

class Order
class Sale
class Return

Order --|> Debit
Sale --|> Credit
Return --|> Debit


class ProductType{
    +ID
    +barCode
    +description
    +sellPrice
    +quantity
    +discountRate
    +notes
    +updatePrice()
}

Shop - "*" ProductType

class SaleTransaction {
    +ID 
    +date
    +time
    +cost
    +paymentType
    +discount rate
}
SaleTransaction - "*" ProductType

class Quantity {
    +quantity
}
(SaleTransaction, ProductType)  .. Quantity

class LoyaltyCard {
    +ID
    +points
}

class Customer {
    +name
    +surname
}

LoyaltyCard "0..1" - Customer

SaleTransaction "*" -- "0..1" LoyaltyCard

class Product {
    
}

class Position {
    +aisleID
    +rackID
    +levelID
    +updatePosition()
}

ProductType - "0..1" Position

ProductType -- "*" Product : describes

class Order {
  +supplier
  +pricePerUnit
  +quantity
  +status
}

Order "*" - ProductType

class ReturnTransaction {
  +quantity
  +returnedValue
}

ReturnTransaction "*" - SaleTransaction
ReturnTransaction "*" - ProductType

note "ID is a number on 10 digits " as N1  
N1 .. LoyaltyCard
note "bar code is a number on 12 to 14  digits,\ncompliant to GTIN specifications, see \nhttps://www.gs1.org/services/how-calculate-check-digit-manually " as N2  
N2 .. ProductType
note "ID is a unique identifier of a transaction, \nprinted on the receipt (ticket number) " as N3
N3 .. SaleTransaction
@enduml
```

## Readers Class Diagram


```plantuml
@startuml

class BarCodeReader {
   +read()
}

class CreditCardReader {
   +read()
   +validate()
}

@enduml
```


# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>


!!! Useful link:    https://www.tablesgenerator.com/markdown_tables# 


| FR ID | Shop | User | Administrator | Order | ProductType | Product | Position | SaleTransaction | Quantity | LoyaltyCard | Customer | ReturnTransaction | AccountBook | FinancialTransaction | Credit | Debit | Sale | Return |
|:-----:|:----:|:-----------:|:-------------:|:-----:|:-----------:|:-------:|:--------:|:---------------:|:--------:|:-----------:|:--------:|:-----------------:|:-----------:|:--------------------:|:------:|:-----:|:----:|:------:|
|  FR1  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |      |        |
|  ---  |      |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |      |        |
|  FR3  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |      |        |
|  FR4  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |      |        |
|  FR5  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |      |        |
|  FR6  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |         X         |             |                      |        |       |      |        |
|  FR7  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |         X         |             |                      |        |       |      |        |
|  FR8  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |      X      |           X          |    X   |   X   |   X  |    X   |










# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

## UC1 

### Scenario 1-1

```plantuml
@startuml
User --> Shop: Insert product descrition
User --> Shop: Insert new bar code
User --> Shop: Insert price per unit
User --> Shop: Insert product notes
User --> Shop: Insert location
User --> Shop: Confirms
Shop --> ProductType : createProductType()
ProductType --> Shop : return ID
Shop --> User : succesful message
@enduml
```

### Scenario 1-2

```plantuml
@startuml
User --> Shop: getProductTypeByBarCode()
User --> Shop: return ProductType
User --> Shop: Selects record
User --> Shop: Select a new product location
Shop --> Position: updatePosition()
ProductType --> Shop : return boolean
Shop --> User : succesful message
@enduml
```

### Scenario 1-3

```plantuml
@startuml
User --> Shop: getProductTypeByBarCode()
User --> Shop: return ProductType
User --> Shop: Selects record
User --> Shop: Inserts a new price > 0
User --> Shop: Confirms
Shop --> ProductType: updatePrice()
ProductType --> Shop : return boolean
Shop --> User : succesful message
@enduml
```


## UC2 

## UC3

## UC4

## UC5 

### Scenario 5-1

```plantuml
@startuml
User --> Shop: login()
Shop --> User : success/error message
@enduml
```

### Scenario 5-2

```plantuml
@startuml
User --> Shop: logout()
Shop --> User : return
@enduml
```

## UC6 

### Scenario 6-1

```plantuml
@startuml
@enduml

### Scenario 6-2

```plantuml
@startuml
@enduml

### Scenario 6-3

```plantuml
@startuml
@enduml

### Scenario 6-4

```plantuml
@startuml
@enduml

### Scenario 6-5

```plantuml
@startuml
@enduml

### Scenario 6-6

```plantuml
@startuml
@enduml

## UC7

### Scenario 7-1

| Scenario |  Manage payment by valid credit card |
| ------------- |:-------------:| 
|  Precondition     | Credit card C exists  |
|  Post condition     | C.Balance -= Price  |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  Ask to credit sale price |
|  4    |  Price payed |
|  5    |  exit with success |

[//]: # "Dubbi su questo scenario"

```plantuml
@startuml
User --> Shop: receiveCreditCardPayment()
Shop --> SaleTransaction: receiveCreditCardPayment()
SaleTransaction --> CreditCardReader: read()
CreditCardReader --> SaleTransaction: return CreditCardCode
SaleTransaction --> CreditCardReader: validate()
CreditCardReader --> SaleTransaction: return true
SaleTransaction --> CreditCardReader: collectSalePrice()
SaleTransaction --> Shop: return true
Shop --> AccountingBook: recordBalanceUpdate()
AccountingBook --> Shop: return true
Shop --> User : succesful message
@enduml
```

### Scenario 7-2

| Scenario |  Manage payment by invalid credit card |
| ------------- |:-------------:| 
|  Precondition     | Credit card C does not exist  |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  C.number invalid, issue warning |
|  4    |  Exit with error |

```plantuml
@startuml
User --> Shop: receiveCreditCardPayment()
Shop --> SaleTransaction: receiveCreditCardPayment()
SaleTransaction --> CreditCardReader: read()
CreditCardReader --> SaleTransaction: return CreditCardCode
SaleTransaction --> CreditCardReader: validate() 
CreditCardReader --> SaleTransaction: return false
SaleTransaction --> Shop: return false
Shop --> User : error message
@enduml
```

### Scenario 7-3

| Scenario |  Manage credit card payment with not enough credit |
| ------------- |:-------------:| 
|  Precondition     | Credit card C exists  |
| | C.Balance < Price |
|  Post condition     | C.Balance not changed  |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  Ask to credit sale price |
|  4    |  Balance not sufficient, issue warning |
|  5    |  Exit with error |

```plantuml
@startuml
User --> Shop: receiveCreditCardPayment()
Shop --> SaleTransaction: receiveCreditCardPayment()
SaleTransaction --> CreditCardReader: read()
CreditCardReader --> SaleTransaction: return CreditCardCode
SaleTransaction --> CreditCardReader: validate() 
CreditCardReader --> SaleTransaction: return true
SaleTransaction --> CreditCardReader: collectSalePrice()
CreditCardReader --> SaleTransaction: return false
SaleTransaction --> Shop: return false
Shop --> User : error message
@enduml
```

### Scenario 7-4

| Scenario |  Manage cash payment |
| ------------- |:-------------:| 
|  Precondition     | Cash >= Price  |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Collect banknotes and coins |
|  2    |  Compute cash quantity |  
|  3    |  Record cash payment |
|  4    |  Compute change |
|  5    |  Return change |

```plantuml
@startuml
User --> User: Collect banknotes and coins
User --> User: Compute cash quantity
User --> Shop: receiveCashPayment() 
Shop --> SaleTransaction: receiveCashPayment()
SaleTransaction --> Shop: return true
Shop --> AccountingBook: recordBalanceUpdate()
AccountingBook --> Shop: return true
Shop --> User : succesful message
@enduml
```

## UC8

## UC9

## UC10 
