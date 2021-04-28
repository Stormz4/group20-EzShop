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
| 4 | Added sequence diagrams for UC9, fixed the class diagram along with the new requirements |

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
package EZShop.gui
package EZShop
package EZShop.data
package EZShop.exceptions
EZShop.gui -- EZShop
EZShop.exceptions --|> EZShop
EZShop.data --|> EZShop
@enduml
```

EZShop.gui contains view and controller, while EZShop contains model and logic. The architetural pattern choosed is MVC.

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
    +Integer issueOrder(String productCode, int quantity, double pricePerUnit)
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
    +boolean endSaleTransaction(Integer transactionId)
    +boolean deleteSaleTransaction(transactionId)
    +SaleTransaction getSaleTransaction(Integer transactionId)
  

    .. UC7 ..
    +double receiveCashPayment(Integer transactionId, double cash)
    +boolean receiveCreditCardPayment(Integer transactionId, String creditCard)

    .. UC8 ..
    +Integer startReturnTransaction(Integer transactionId)
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
    +userID: Integer
    +username: String
    +password: String
    +role: String
    +Integer createUser(String username, String password, String role)
    +boolean deleteUser(Integer id)
    +List<User> getAllUsers()
    +User getUser(Integer id)
    +boolean updateUserRights(Integer id, String role)
}

Administrator --|> User

Shop -- "*" User

class AccountBook {
 +boolean recordBalanceUpdate(double toBeAdded)
}
AccountBook - Shop
class BalanceOperation {
 +transactionID: Integer
 +description: String 
 +amount: int
 +date: LocalDate
+boolean recordBalanceUpdate(double toBeAdded)
}
AccountBook -- "*" BalanceOperation

class Credit 
class Debit

Credit --|> BalanceOperation
Debit --|> BalanceOperation

class Order

Order --|> Debit
SaleTransaction --|> Credit
ReturnTransaction --|> Debit


class ProductType{
    +productID: Integer
    +barCode: String
    +description: String
    +pricePerUnit: double
    +quantity: int
    +discountRate: float
    +notes: String
    +Integer createProductType(String description, String productCode, double pricePerUnit, String note)
    +boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
    +boolean deleteProductType(Integer id)
    +ProductType getProductTypeByBarCode(String barCode)
}

Shop - "*" ProductType

class SaleTransaction {
    +time: LocalDate
    +paymentType: String
    +discountRate: float
    +double receiveCashPayment(Integer transactionId, double cash)
    +boolean receiveCreditCardPayment(Integer transactionId, String creditCard)
}
SaleTransaction - "*" ProductType

class Quantity {
    +quantity: int
}
(SaleTransaction, ProductType)  .. Quantity

class LoyaltyCard {
    +cardID: Integer
    +points: int
}

class Customer {
    +name: String
    +surname: String
}

LoyaltyCard "0..1" - Customer

SaleTransaction "*" -- "0..1" LoyaltyCard

class Position {
    +aisleID: Integer
    +rackID: Integer
    +levelID: Integer
    +boolean updatePosition(Integer productId, String newPos)
}

ProductType - "0..1" Position

ProductType -- "*" Product : describes

class Order {
  +supplier: String
  +pricePerUnit: double
  +quantity: int
  +status: int
}

Order "*" - ProductType

class ReturnTransaction {
  +quantity: int
  +returnedValue: double
  +Integer startReturnTransaction(Integer transactionId)
  +boolean returnProduct(Integer returnId, String productCode, int amount)
  +boolean endReturnTransaction(Integer returnId, boolean commit)
  +boolean deleteReturnTransaction(Integer returnId)
  +double returnCashPayment(Integer returnId)
  +double returnCreditCardPayment(Integer returnId, String creditCard)
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


# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>


!!! Useful link:    https://www.tablesgenerator.com/markdown_tables# 


| FR ID | Shop | User | Administrator | Order | ProductType | Product | Position | SaleTransaction | Quantity | LoyaltyCard | Customer | ReturnTransaction | AccountingBook | FinancialTransaction | Credit | Debit | 
|:-----:|:----:|:-----------:|:-------------:|:-----:|:-----------:|:-------:|:--------:|:---------------:|:--------:|:-----------:|:--------:|:-----------------:|:-----------:|:--------------------:|:------:|:-----:|
|  FR1  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR3  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR4  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR5  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR6  |   X  |      X      |       X       |       |      X      |         |          |                 |          |             |          |         X         |      X      |                      |        |       |
|  FR7  |   X  |      X      |       X       |       |      X      |         |          |                 |          |             |          |         X         |      X      |                      |        |       |
|  FR8  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |      X      |           X          |    X   |   X   |










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
Shop --> User : successful message
@enduml
```

### Scenario 1-2

```plantuml
@startuml
User --> Shop: Searches by bar code
Shop --> ProductType: getProductTypeByBarCode()
ProductType --> Shop: return ProductType

User --> Shop: Selects record
User --> Shop: Select a new product location
Shop --> Position: updatePosition()
Position --> Shop : return boolean
Shop --> User : successful message
@enduml
```

### Scenario 1-3

```plantuml
@startuml
User --> Shop: Search by bar code
Shop --> ProductType: getProductTypeByBarCode()
ProductType --> Shop: return ProductType
User --> Shop: Selects record
User --> Shop: Inserts a new price > 0
User --> Shop: Confirms
Shop --> ProductType: updateProduct()
ProductType --> Shop : return boolean
Shop --> User : successful message
@enduml
```

## UC2 
### Scenario 2-1
```plantuml
@startuml
autonumber
Administrator -> Shop: Insert username
Administrator -> Shop: Insert password
Administrator -> Shop: Insert role
Shop -> User: createUser()
User --> Shop: return Integer (unique identifier)
Administrator -> Shop: Selects user rights
Shop -> User: updateUserRights()
User --> Shop: return boolean
Administrator -> Shop: Confirms
Shop --> Administrator: successful message
@enduml
```


### Scenario 2-2
```plantuml
@startuml
autonumber
Administrator -> Shop: Select an account to be deleted
Shop -> User: getUser()
User --> Shop: return User
Shop -> User: deleteUser()
User --> Shop: return boolean
Shop --> Administrator: successful message
@enduml
```


### Scenario 2-3
```plantuml
@startuml
autonumber
Administrator -> Shop: Select an account to be updated
Shop -> User: getUser()
User --> Shop: return User
Administrator -> Shop: Select new rights for the account
Shop -> User: updateUserRights()
User --> Shop: return boolean
Shop --> Administrator: successful message
@enduml
```


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
User --> Shop: start Sale Transaction
Shop --> SaleTransaction: startSaleTransaction()
SaleTransaction --> Shop: return TransactionID

SaleTransaction --> BarCodeReader: read()
BarCodeReader --> SaleTransaction : return BarCode

Shop --> SaleTransaction: addProductToSale()
SaleTransaction --> SaleTransaction : addProductToSale()
SaleTransaction --> SaleTransaction : return boolean
SaleTransaction --> Shop : return boolean
Shop --> ProductType : updateQuantity()
ProductType --> Shop: return boolean
User --> Shop: endSaleTransaction()
Shop --> Shop: getSaleTransaction()
Shop --> User: show Sale review
Shop --> User: ask Payment Type (See UC7)
@enduml
```

### Scenario 6-4

```plantuml
@startuml
User --> Shop: start Sale Transaction
Shop --> SaleTransaction: startSaleTransaction()
SaleTransaction --> Shop: return TransactionID

Shop --> BarCodeReader: read()
BarCodeReader --> Shop : return BarCode

Shop --> SaleTransaction: addProductToSale()
SaleTransaction --> SaleTransaction : addProductToSale()
SaleTransaction --> SaleTransaction : return boolean
SaleTransaction --> Shop : return boolean
Shop --> ProductType : updateQuantity()
ProductType --> Shop: return boolean
User --> Shop: endSaleTransaction()
Shop --> Shop: getSaleTransaction()
Shop --> User: show Sale review
Shop --> User: ask Payment Type

Shop --> LoyaltyCardReader: read()
LoyaltyCardReader --> Shop : return CardCode

Shop --> User: show Card
User --> Shop: manage Payment (see UC7)
Shop --> LoyaltyCard: modifyPointsOnCard()
LoyaltyCard --> Shop: return boolean
Shop --> User: return boolean
Shop --> User: print Sale
@enduml
```

## UC7

### Scenario 7-1

[//]: # "Dubbi su questo scenario e i successivi"

```plantuml
@startuml
User --> Shop: Read credit card number
Shop --> SaleTransaction: receiveCreditCardPayment()
SaleTransaction --> Shop: return true
Shop --> AccountingBook: recordBalanceUpdate()
AccountingBook --> Shop: return true
Shop --> User : succesful message
@enduml
```

### Scenario 7-4

```plantuml
@startuml
User --> User: Collect banknotes and coins
User --> User: Compute cash quantity
User --> Shop: Record cash payment
Shop --> SaleTransaction: receiveCashPayment()
SaleTransaction --> Shop: return true
Shop --> AccountingBook: recordBalanceUpdate()
AccountingBook --> Shop: return true
Shop --> User : succesful message
@enduml
```

## UC8

### Scenario 8-1

```plantuml
@startuml
autonumber
User -> Shop: Insert transaction ID
Shop -> ReturnTransaction: startReturnTransaction()
User -> Shop: scan product BarCode
User -> Shop: Insert quantity of returned items
Shop -> ReturnTransaction: returnProduct()
ReturnTransaction -> ProductType: getProductTypeByBarCode()
ProductType --> ReturnTransaction: return ProductType
ReturnTransaction -> ProductType: Update quantity by N
ReturnTransaction --> Shop: return boolean
ReturnTransaction --> Shop: return Integer (ReturnTransaction ID)
ref over Shop, User
Manage credit card return (go to UC10)
end ref
User -> Shop: Close return transaction
Shop -> ReturnTransaction: endReturnTransaction()
ReturnTransaction --> Shop: return boolean

ref over ReturnTransaction, AccountingBook
Update balance (go to UC10)
end ref

ReturnTransaction --> Shop: Amount returned
Shop --> User: Successful message
@enduml
```

### Scenario 8-2

```plantuml
@startuml
autonumber
User -> Shop: Insert transaction ID
Shop -> ReturnTransaction: startReturnTransaction()
User -> Shop: scan product BarCode
User -> Shop: Insert quantity of returned items
Shop -> ReturnTransaction: returnProduct()
ReturnTransaction -> ProductType: getProductTypeByBarCode()
ProductType --> ReturnTransaction: return ProductType
ReturnTransaction -> ProductType: Update quantity by N
ReturnTransaction --> Shop: return boolean
ReturnTransaction --> Shop: return Integer (ReturnTransaction ID)
ref over Shop, User
Manage cash return (go to UC10)
end ref
User -> Shop: Close return transaction
Shop -> ReturnTransaction: endReturnTransaction()
ReturnTransaction --> Shop: return boolean

ref over ReturnTransaction, AccountingBook
Update balance (go to UC10)
end ref

ReturnTransaction --> Shop: Amount returned
Shop --> User: Successful message
@enduml
```

## UC9

### Scenario 9-1

| Scenario |  List credits and debits |
| ------------- |:-------------:| 
|  Precondition     | Manager C exists and is logged in |
|  Post condition     | Transactions list displayed  |
| Step#        | Description  |
|  1    |  C selects a start date |  
|  2    |  C selects an end date |
|  3    |  C sends transaction list request to the system |
|  4    |  The system returns the transactions list |
|  5    |  The list is displayed  |


```plantuml
@startuml
User --> Shop: Selects a start date
User --> Shop: Selects an end date
User --> Shop: Send transaction list request
Shop --> AccountingBook: getCreditsAndDebits()
AccountingBook --> Shop: return transactions list
Shop --> User: display list
@enduml
```

## UC10 

### Scenario 10-1

```plantuml
@startuml
autonumber
User -> Shop: Scan credit card number
Shop -> ReturnTransaction: returnCreditCardPayment()
ReturnTransaction -> AccountingBook: recordBalanceUpdate()
AccountingBook --> ReturnTransaction: return boolean
ReturnTransaction --> Shop: Amount returned
Shop --> User: Successful message
@enduml
```

### Scenario 10-2

```plantuml
@startuml
autonumber
User -> User: Collect banconotes and coins
User -> Shop: Record cash return
Shop -> ReturnTransaction: returnCashPayment()
ReturnTransaction -> AccountingBook: recordBalanceUpdate()
AccountingBook --> ReturnTransaction: return boolean
ReturnTransaction --> Shop: Amount returned
Shop --> User: Successful message
@enduml
```

