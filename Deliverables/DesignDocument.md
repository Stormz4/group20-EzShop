# Design Document 

Authors:

- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524

Date: 29/04/2021

| Version | Changes |
| ------- |---------|
| 1 | Added first version of design document. |
| 2 | Added functions in Shop | 
| 3 | Added sequence diagrams |
| 4 | Added sequence diagrams for UC9, fixed the class diagram along with the new requirements |
| 5 | Updated class diagram |
| 6 | Fixed some sequence diagrams, added method to AccountBook |

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
package it.polito.ezshop.gui
package it.polito.ezshop.model
package it.polito.ezshop.data
package it.polito.ezshop.exceptions
it.polito.ezshop.gui -- it.polito.ezshop.model
it.polito.ezshop.exceptions <-- it.polito.ezshop.model
it.polito.ezshop.data <-- it.polito.ezshop.model
@enduml
```

it.polito.ezshop.gui contains view and controller. The architetural pattern choosed is MVC+3 tier. 

it.polito.ezshop.exceptions contains the exceptions used in the API.


# Low level design

## EZShop Class Diagram

The packages are related in this way:

```plantuml
@startuml
package it.polito.ezshop.data{
interface EZShopInterface{
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
}

package it.polito.ezshop.model{
 class Shop{

}
}

class Shop implements EZShopInterface
@enduml
```

The model contains the following classes, which are persistent:

```plantuml
@startuml

left to right direction


package it.polito.ezshop.model{
class Shop{

}

class User{
    +userID: Integer
    +username: String
    +password: String
    +role: String
}

Administrator --|> User

Shop -- "*" User

class AccountBook {
 +balance: double
 +boolean addBalanceOperation(Integer transactionID)
 +boolean updateBalance(double toBeAdded)
 +List<BalanceOperation> getAllTransactions()
 +boolean updateBalanceOperation(Integer transactionID)
}
AccountBook - Shop
class BalanceOperation {
 +transactionID: Integer
 +description: String 
 +amount: int
 +date: LocalDate
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
}

Shop - "*" ProductType

class SaleTransaction {
    +time: LocalDate
    +paymentType: String
    +discountRate: float
    +boolean: isClosed
    +HashMap<Integer, int>
}
SaleTransaction - "*" ProductType

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
}

ProductType - "0..1" Position


class Order {
  +supplier: String
  +pricePerUnit: double
  +quantity: int
  +status: OrderStatusEnum
}

Order "*" - ProductType

Enum OrderStatusEnum {
    Issued
    Ordered
    Payed
    Completed
}

Order -[hidden]-> OrderStatusEnum


class ReturnTransaction {
  +quantity: int
  +returnedValue: double
  +boolean: isClosed
}

Shop -- "*" LoyaltyCard
Shop -- "*" Customer

ReturnTransaction "*" - SaleTransaction
ReturnTransaction "*" - ProductType

note "ID is a number on 10 digits " as N1  
N1 .. LoyaltyCard
note "bar code is a number on 12 to 14  digits,\ncompliant to GTIN specifications, see \nhttps://www.gs1.org/services/how-calculate-check-digit-manually " as N2  
N2 .. ProductType
note "ID is a unique identifier of a transaction, \nprinted on the receipt (ticket number) " as N3
N3 .. SaleTransaction
note "AccountBook contains a list of balance operations" as N4
N4 .. AccountBook
note "Shop contains data structures for User, Products, Customers/Loyalty cards and a copy of AccountBook" as N5
N5 .. Shop
note "SaleTransaction contains a list of Products" as N6
N6 .. SaleTransaction
note "ReturnTransaction contains a list of Products" as N7
N7 .. ReturnTransaction
note "LoyaltyCard keeps also track of the customer which is related to" as N8
N8 .. LoyaltyCard
}
@enduml
```


# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>


!!! Useful link:    https://www.tablesgenerator.com/markdown_tables# 


| FR ID | Shop | User | Administrator | Order | ProductType | Product | Position | SaleTransaction | Quantity | LoyaltyCard | Customer | ReturnTransaction | AccountBook | FinancialTransaction | Credit | Debit | 
|:-----:|:----:|:-----------:|:-------------:|:-----:|:-----------:|:-------:|:--------:|:---------------:|:--------:|:-----------:|:--------:|:-----------------:|:-----------:|:--------------------:|:------:|:-----:|
|  FR1  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR3  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR4  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR5  |   X  |             |               |       |             |         |          |                 |          |             |          |                   |             |                      |        |       |
|  FR6  |   X  |      X      |       X       |       |      X      |         |          |        X        |     X    |      X      |          |         X         |      X      |                      |        |       |
|  FR7  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |      X      |                      |        |       |
|  FR8  |   X  |      X      |       X       |       |             |         |          |                 |          |             |          |                   |      X      |           X          |    X   |   X   |










# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

The User will communicate with the GUI, which will invoke Shop's methods (instead of making the User communicate with the Shop directly).

## UC1 

### Scenario 1-1

```plantuml
@startuml
User --> GUI: Insert product descrition
User --> GUI: Insert new bar code
User --> GUI: Insert price per unit
User --> GUI: Insert product notes
User --> GUI: Insert location
User --> GUI: Confirms
GUI --> Shop: createProductType()
Shop --> ProductType : new ProductType
ProductType --> Shop : return ID
Shop --> User : successful message
@enduml
```

### Scenario 1-2

```plantuml
@startuml
User --> GUI: Searches by bar code
GUI --> Shop: getProductTypeByBarCode()
Shop --> GUI: return ProductType

User --> GUI: Selects record
User --> GUI: Select a new product location
GUI --> Shop: updatePosition()
Shop --> GUI : return boolean
GUI --> User : successful message
@enduml
```


## UC2 
### Scenario 2-1
```plantuml
@startuml
autonumber
Administrator -> GUI: Insert username
Administrator -> GUI: Insert password
Administrator -> GUI: Insert role
GUI -> Shop: createUser()
Shop -> User: new User()
User --> Shop: return User
Shop --> GUI: return Integer (unique identifier)
Administrator -> GUI: Selects user rights
GUI -> Shop: updateUserRights()
Shop --> GUI: return boolean
Administrator -> GUI: Confirms
GUI --> Administrator: successful message
@enduml
```


### Scenario 2-2
```plantuml
@startuml
autonumber
Administrator -> GUI: Select an account to be deleted
GUI -> Shop: deleteUser()
Shop --> GUI: return boolean
GUI --> Administrator: successful message
@enduml
```


### Scenario 2-3
```plantuml
@startuml
autonumber
Administrator -> GUI: Select an account to be updated
GUI -> Shop: getUser()
Shop --> GUI: return User
Administrator -> GUI: Select new rights for the account
GUI -> Shop: updateUserRights()
Shop --> GUI: return boolean
GUI --> Administrator: successful message
@enduml
```


#
## UC3

```plantuml
@startuml
autonumber
User -> GUI: Create new order O for product PT
GUI -> Shop: payOrderFor()
Shop -> Order: new Order()
Order --> Shop: return Order
Shop -> Order: setStatus()
Shop -> BalanceOperation: new BalanceOperation()
BalanceOperation --> Shop: return BalanceOperation
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: return boolean
Shop -> Order: setStatus()
GUI --> Manager: show outcome message
GUI -> Shop: recordOrderArrival()
Shop -> Shop: updateQuantity()
Shop --> GUI: return boolean
GUI --> User: Show outcome message
@enduml
```   

### Scenario 3-1
```plantuml
@startuml
autonumber
Manager -> GUI: Create new order O for product PT
GUI -> Shop: issueReorder()
Shop -> Order: new Order()
Order --> Shop: return Order
Shop -> Order: setStatus(Issued)
Shop --> GUI: return orderID
GUI -> Manager: show outcome message
@enduml
```   

### Scenario 3-2
```plantuml
@startuml
autonumber
Manager -> GUI: Create new order O for product PT
GUI -> Shop: getAllOrders()
Shop --> GUI: returns List<Order>
GUI --> Manager: Show orders
Manager -> GUI: Register payment done for O
GUI -> Shop: payOrder(orderID)
Shop -> BalanceOperation: new BalanceOperation()
BalanceOperation --> Shop: return BalanceOperation
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop -> Order: setStatus(Payed)
Shop --> GUI: return boolean
GUI --> Manager: Show outcome message
@enduml
```   

#
## UC4
### Scenario 4-1
```plantuml
@startuml
autonumber
User -> GUI: Asks Cu personal data
GUI -> Shop: getCustomer(id)
Shop --> GUI: return Customer
User -> GUI: Fills fields with Cu's personal data
User -> GUI: Confirm
GUI -> Shop: modifyCustomer(id, ...)
Shop -> Customer: updateCustomer()
Shop --> GUI: return boolean
GUI --> User: Show outcome message
@enduml
```   

### Scenario 4-2
```plantuml
@startuml
autonumber
User -> GUI: Creates a new Loyalty card L
GUI -> Shop: createCard()
Shop --> GUI: return cardCode
GUI --> User: Show outcome message
User -> GUI: User attaches L to U
GUI -> Shop: attachCardToCustomer()
Shop --> GUI: return boolean
GUI --> User: Show outcome message
@enduml
```   

### Scenario 4-3
```plantuml
@startuml
autonumber
User -> GUI: User selects customer record U
GUI -> Shop: getCustomer()
Shop --> GUI: return Customer
User -> GUI: User detaches L from U
GUI -> Shop: modifyCustomer()
Shop -> Customer: setCard()
Shop --> GUI: return boolean
GUI -> User: Show outcome message 
@enduml
```   


## UC5 

### Scenario 5-1

```plantuml
@startuml
User --> GUI : Insert username
User --> GUI : Insert password
User --> GUI : confirm
GUI --> Shop: login()
Shop --> GUI : return user
GUI --> User: Show functionalities
@enduml
```

### Scenario 5-2

```plantuml
@startuml
User --> GUI: Log out
GUI --> Shop: logout()
Shop --> GUI : return boolean
GUI --> User : Change page
@enduml
```

## UC6 

### Scenario 6-1

```plantuml
@startuml
User -> GUI: start Sale Transaction
GUI -> Shop: startSaleTransaction()
Shop -> SaleTransaction: new SaleTransaction()
SaleTransaction --> Shop: return TransactionID
Shop --> GUI: return boolean
User -> GUI: Insert product BarCode
GUI -> Shop: addProductToSale()
Shop -> Shop: getProductByBarCode()
Shop -> Shop : updateQuantity()
User -> GUI: close Sale Transaction
GUI -> Shop: endSaleTransaction()
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: return boolean
GUI --> User: ask Payment Type
User -> GUI: Select payment type
ref over GUI, User, Shop, AccountBook
 manage Payment and update balance (see UC7)
end ref
@enduml
```

### Scenario 6-3

```plantuml
@startuml
User -> GUI: start Sale Transaction
GUI -> Shop: startSaleTransaction()
Shop -> SaleTransaction: new SaleTransaction()
SaleTransaction --> Shop: return TransactionID
Shop --> GUI: return boolean
User -> GUI: Insert product BarCode
GUI -> Shop: addProductToSale()
Shop -> Shop: getProductByBarCode()
Shop -> Shop : updateQuantity()
Shop --> GUI: return boolean
User -> GUI: insert discount rate
GUI -> Shop: applyDiscountRateToSale()
Shop --> GUI: return boolean
User -> GUI: close Sale Transaction
GUI -> Shop: endSaleTransaction()
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: return boolean
GUI --> User: ask Payment Type
User -> GUI: Select payment type
ref over GUI, User, Shop, AccountBook
 manage Payment and update balance (see UC7)
end ref
@enduml
```


## UC7

### Scenario 7-1

[//]: # "Dubbi su questo scenario e i successivi"

```plantuml
@startuml
User --> GUI: Read credit card number
GUI --> Shop: receiveCreditCardPayment()
Shop --> Shop: recordBalanceUpdate()
Shop --> GUI: return true
GUI --> User: succesful message
@enduml
```

### Scenario 7-4

```plantuml
@startuml
User --> User: Collect banknotes and coins
User --> User: Compute cash quantity
User --> GUI: Record cash payment
GUI --> Shop: receiveCashPayment()
Shop --> Shop: recordBalanceUpdate()
Shop --> AccountBook: addBalanceOperation()
AccountBook --> Shop: return true
Shop --> GUI: return double
GUI --> User: return double
@enduml
```

## UC8

### Scenario 8-1

```plantuml
@startuml
autonumber
User -> GUI: Insert transaction ID
GUI -> Shop: startReturnTransaction()
Shop -> ReturnTransaction: new ReturnTransaction()
ReturnTransaction --> Shop: return ReturnTransaction
User -> GUI: Insert product BarCode
User -> GUI: Insert quantity of returned items
GUI -> Shop: returnProduct()
Shop -> Shop: getProductTypeByBarCode()
Shop -> Shop : updateQuantity()
Shop --> GUI: return boolean
Shop --> GUI: return Integer (ReturnTransaction ID)
ref over GUI, User, Shop, AccountBook
Manage credit card return and update balance (go to UC10)
end ref
User -> GUI: Close return transaction
GUI -> Shop: endReturnTransaction()
Shop -> AccountBook: updateBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: return boolean
GUI --> User: Successful message
@enduml
```

## UC9

### Scenario 9-1


```plantuml
@startuml
User --> GUI: Selects a start date
User --> GUI: Selects an end date
User --> GUI: Send transaction list request
GUI --> Shop: getCreditsAndDebits()
Shop --> AccountBook: getAllTransactions()
AccountBook --> Shop: return transaction list
Shop --> GUI: return transactions list
GUI --> User: display list
@enduml
```

## UC10 

### Scenario 10-1

```plantuml
@startuml
autonumber
User -> GUI: Insert credit card number
GUI -> Shop: returnCreditCardPayment()
Shop --> Shop: recordBalanceUpdate()
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: Amount returned
GUI --> User: Successful message
@enduml
```

### Scenario 10-2

```plantuml
@startuml
autonumber
User -> User: Collect banconotes and coins
User -> GUI: Record cash return
GUI -> Shop: returnCashPayment()
Shop --> Shop: recordBalanceUpdate()
Shop -> AccountBook: addBalanceOperation()
AccountBook --> Shop: return boolean
Shop --> GUI: Amount returned
GUI --> User: Successful message
@enduml
```

