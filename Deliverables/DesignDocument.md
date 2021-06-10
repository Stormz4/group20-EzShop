# Design Document 

Authors:

- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524

Date: 09/06/2021

| Version | Changes |
| ------- |---------|
| 1  | Added first version of design document. |
| 2  | Added functions in EZShop |
| 3  | Added sequence diagrams |
| 4  | Added sequence diagrams for UC9, fixed the class diagram along with the new requirements |
| 5  | Updated class diagram |
| 6  | Fixed some sequence diagrams, added method to AccountBook |
| 7  | Modified use case diagrams, class diagram and verification matrix |
| 8  | Last fixes. Final version |
| 9  | Post-coding fixes |
| 10 | Post-testing fixes |
| 11 | Updated according to final design |
| 12 | Updated according to the Change Request regarding RFIDs |

# Contents

- [High level design](#high-level-design)
- [Low level design](#low-level-design)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

```plantuml
@startuml
package it.polito.ezshop.gui
package it.polito.ezshop.data
package it.polito.ezshop.exceptions
package it.polito.ezshop.utils
it.polito.ezshop.gui -- it.polito.ezshop.data
it.polito.ezshop.exceptions <-- it.polito.ezshop.data
it.polito.ezshop.utils -- it.polito.ezshop.data
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
    +boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom)
    
    .. UC4 - Manage Customers and Cards ..
    +Integer defineCustomer(String customerName)
    +boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard)
    +boolean deleteCustomer(Integer id)
    +Customer getCustomer(Integer id)
    +List<Customer> getAllCustomers()
    +String createCard()
    +boolean attachCardToCustomer(String customerCard, Integer customerId)
    +boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded)

    .. UC5 - Authenticate, authorize ..
    +User login(String username, String password)
    +boolean logout()

    .. UC6 - Manage sale transaction ..
    +Integer startSaleTransaction() throws UnauthorizedException
    +boolean addProductToSale(Integer transactionId, String productCode, int amount)
    +boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
    +boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
    +boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
    +int computePointsForSale(Integer transactionId)
    +boolean endSaleTransaction(Integer transactionId)
    +boolean deleteSaleTransaction(transactionId)
    +SaleTransaction getSaleTransaction(Integer transactionId)
    +boolean addProductToSaleRFID(Integer transactionId, String RFID)
    +boolean deleteProductFromSaleRFID(Integer transactionId, String RFID)

    .. UC7 - Manage payment ..
    +double receiveCashPayment(Integer transactionId, double cash)
    +boolean receiveCreditCardPayment(Integer transactionId, String creditCard)

    .. UC8 - Manage return transaction ..
    +Integer startReturnTransaction(Integer transactionId)
    +boolean returnProduct(Integer returnId, String productCode, int amount)
    +boolean endReturnTransaction(Integer returnId, boolean commit)
    +boolean deleteReturnTransaction(Integer returnId)
    +boolean returnProductRFID(Integer returnId, String RFID)

    .. UC9 - Accounting ..
    +boolean recordBalanceUpdate(double toBeAdded)
    +List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to)
    +double computeBalance()

    .. UC10 - Manage return ..
    +double returnCashPayment(Integer returnId)
    +double returnCreditCardPayment(Integer returnId, String creditCard)
}
}

package it.polito.ezshop.data{
 class EZShop{

}
}

class EZShop implements EZShopInterface
@enduml
```
<br/><br/>
The data package contains the following persistent classes.<br/>

```plantuml
@startuml
package it.polito.ezshop.data{
class EZShop{
    +shopDB: SQLiteDB
    +currUser: EZUser
    +accountingBook: EZAccountBook
    +loadDataFromDB(void)
    +clearData(void)
    +isValidPosition(String position)
    +isValidCard(String card)
    +getSaleTransactionById(Integer saleNumber)
    +getReturnTransactionById(Integer returnId)
    +isValidCreditCard(String creditCard)
    +getCreditInTXTbyCardNumber(String cardNumber)
    +updateCreditInTXTbyCardNumber(String cardNumber, double toBeAdded)
    +getAllProducts(void)
    +isValidRFID(String rfid)
}

Class SQLiteDB {
    
}
EZShop -- SQLiteDB

class EZProductType{
    +productID: Integer
    +barCode: String
    +location: String
    +description: String
    +pricePerUnit: double
    +quantity: int
    +discountRate: float
    +notes: String
    +editQuantity(int toBeAdded)
}

class EZUser{
    +<u>URNoRole: String = ""
    +<u>URAdministrator: String = "Administrator"
    +<u>URShopManager: String = "ShopManager"
    +<u>URCashier: String = "Cashier"
    +userID: Integer
    +username: String
    +password: String
    +role: String
    +hasRequiredRole(String ...requiredRoles)
}


EZShop -- "*" EZUser

class EZAccountBook {
 +currentBalance: double
 +nextBalanceId: int
 +boolean addBalanceOperation(Integer transactionID)
 +boolean updateBalance(double toBeAdded)
 }
EZAccountBook -down- EZShop
class EZBalanceOperation {
 +<u>Credit: String = "CREDIT"
 +<u>Debit: String = "DEBIT"
 +balanceId: int
 +date: LocalDate 
 +money: double
 +type: String
}
EZAccountBook -up- "*" EZBalanceOperation

EZOrder -- EZBalanceOperation
EZSaleTransaction -- EZBalanceOperation
EZReturnTransaction -- EZBalanceOperation

EZShop -down- "*" EZProductType

class EZSaleTransaction {
    +<u>STOpened: String = "OPENED"
    +<u>STClosed: String = "CLOSED" 
    +<u>STPayed: String = "PAYED"
    +ticketNumber: Integer
    +time: LocalDate
    +paymentType: String
    +discountRate: double
    +price: double
    +status: String
    +attachedCard: String
    +hasRequiredStatus(String ...requiredStatus)
    +getTicketEntryByBarCode(String barCode)
    +updatePrice(double toBeAdded)
}

class EZTicketEntry {
    +barCode: String
    +productDescription: String
    +amount: int
    +pricePerUnit: double
    +discountRate: double
}
EZSaleTransaction -up-"*" EZTicketEntry
EZReturnTransaction -up-"*" EZTicketEntry

class EZCustomer {
    +id: Integer
    +customerName: String
    +customerCard: String
    +points: Integer
}

class EZOrder {
  +<u>OSIssued: String = "ISSUED"
  +<u>OSPayed: String = "PAYED"
  +<u>OSCompleted: String = "COMPLETED"
  +orderId: Integer  
  +balanceId: Integer
  +productCode: String
  +pricePerUnit: double
  +quantity: int
  +status: String
}

EZOrder "*" - EZProductType

class EZReturnTransaction {
  +<u> RTOpened: String = "OPENED"
  +<u> RTClosed: String = "CLOSED"
  +<u> RTPayed: String = "PAYED"
  +returnId: Integer  
  +quantity: int
  +returnedValue: double
  +saleTransactionId: Integer
  +status: String
}

class EZProduct {
    +RFID: String
    +ProdTypeID: Integer
    +SaleID: Integer
    +returnID: Integer
}

EZProductType --"*" EZProduct: describes

EZShop -down- "*" EZCustomer

EZReturnTransaction "*" - EZSaleTransaction

note "bar code is a number on 12 to \n14 digits, compliant to GTIN \nspecifications, see https://www.gs1\n.org/services/how-calculate-check\n-digit-manually " as N2  
N2 .. EZProductType
note "ID is a unique identifier of a transaction, \nprinted on the receipt (ticket number) " as N3
N3 .right. EZSaleTransaction

note "Map to \nimplement 1..n" as N6
note "Map to \nimplement 1..n" as N7
note "Map to \nimplement 1..n" as N8
note "Map to \nimplement 1..n" as N9
note "Map to \nimplement 1..n" as N10
note "Map to \nimplement 1..n" as N11
note "Map to \nimplement 1..n" as N12
note "Map to \nimplement 1..n" as N13
note "Map to \nimplement 1..n" as N14


EZAccountBook .. N6
N6 .. EZBalanceOperation
EZShop .. N7
N7 .. EZCustomer
N8 .. EZSaleTransaction
EZProductType .. N9
N9 .. EZShop
EZShop .. N10
N10 .. EZUser
EZShop .. N11
EZSaleTransaction .. N12
N12 .. EZReturnTransaction
EZTicketEntry .. N13
N13 .. EZSaleTransaction
EZTicketEntry .. N14
N14 .. EZReturnTransaction
}
@enduml
```
In order to have a readable diagram, we chose not to include the following Interfaces:
* BalanceOperation
* Customer
* Order
* ProductType
* SaleTransaction
* TicketEntry
* User
<br/><br/>

SQLite DB class has been separated for diagram's readability:

```plantuml
@startuml
Class SQLiteDB {
    +connect(void)
    +isConnected(void)
    +closeConnection(void)
    +createNewDatabase(void)
    +initDatabase(void)
    +clearDatabase(void)
    +clearTable(String tableName)
    +lastInsertRowId(void)
    +createCustomersTable(void)
    +selectAllCustomers(void)
    +insertCustomer(String customerName, String customerCard)
    +deleteCustomer(Integer id)
    +updateCustomer(Integer customerId, String customerName, String customerCard)
    +createBalanceOperationsTable(void)
    +selectAllBalanceOperations(void)
    +insertBalanceOperation(LocalDate date, double money, String type)
    +deleteBalanceOperation(Integer id)
    +updateBalanceOperation(Integer id, LocalDate date, double money, String type)
    +selectTotalBalance(void)
    +createOrdersTable(void)
    +selectAllOrders(void)
    +insertOrder(Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
    +updateOrder(Integer id, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
    +createUsersTable(void)
    +selectAllUsers(void)
    +insertUser(String userName, String password, String role)
    +deleteUser(Integer id)
    +updateUser(Integer id, String userName, String password, String role)
    +createCardsTable(void)
    +selectAllCards(void)
    +insertCard(Integer points)
    +deleteCard(String cardCode)
    +updateCard(String cardCode, Integer points)
    +createProductTypesTable(void)
    +selectAllProductTypes(void)
    +insertProductType(Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit)
    +deleteProductType(Integer id)
    +updateProductType(Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit)
    +createTransactionsTable(void)
    +selectAllSaleTransactions(void)
    +selectAllReturnTransactions(void)
    +insertSaleTransaction(List<TicketEntry> entries, double discountRate, double price, String status)
    +insertReturnTransaction(List<TicketEntry> entries, int saleID, double price, String status)
    +deleteTransaction(Integer transactionID)
    +updateSaleTransaction(Integer transactionID, double discountRate, double price, String status)
    +updateReturnTransaction(Integer transactionID, int saleID, double price, String status)
    +createProductsPerSaleTable(void)
    +insertProductPerSale(String barCode, Integer transactionID, int amount, double discountRate)
    +deleteProductPerSale(String barCode, Integer transactionID)
    +deleteAllProductsPerSale(Integer transactionID)
    +updateProductPerSale(String barCode, Integer transactionID, int amount, double discountRate)
    +createProductsTable(void)
    +insertProduct(Long RFID, Integer prodTypeID, Integer saleID, Integer returnID)
    +deleteProduct(Long RFID)
    +selectAllProducts(void)
    +updateProduct(Long RFID, Integer prodTypeID, Integer saleID, Integer returnID)
}
@enduml
 ```



# Verification traceability matrix

| FR ID | EZShop | EZUser | Administrator | EZOrder | EZProductType | EZProduct | EZSaleTransaction | EZCustomer | EZReturnTransaction | EZAccountBook | EZBalanceOperation |
|:-------:|:------:|:------:|:---------------:|:-------:|:------:|:-------:|:----------:|:-----------------:|:-------------:|:----------:|:-------------------:|
| FR1   | X    | X    | X             |       |           |   |                 |          |                   |             |                   |
| FR3   | X    | X    | X             |       | X         | X |                 |          |                   |             |                   |
| FR4   | X    | X    | X             | X     | X         | X |                 |          |                   | X           | X                 |
| FR5   | X    | X    | X             |       |           |   |                 | X        |                   |             |                   |
| FR6   | X    | X    | X             |       | X         | X | X               |          | X                 | X           | X                 |
| FR7   | X    | X    | X             |       |           |   | X               |          | X                 | X           | X                 |
| FR8   | X    | X    | X             |       |           |   | X               |          |        X          | X           | X                 |

# Verification sequence diagrams 

The User will communicate with the GUI, which will invoke EZShop's methods (instead of making the User communicate with the EZShop directly).

## UC1 

### Scenario 1-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Insert product descrition
EZUser -> GUI: Insert new bar code
EZUser -> GUI: Insert price per unit
EZUser -> GUI: Insert product notes
EZUser -> GUI: Insert location
EZUser -> GUI: Confirms
GUI -> EZShop: createProductType()
EZShop -> EZProductType : new EZProductType
EZProductType --> EZShop : return ID
EZShop --> EZUser : successful message
@enduml
```

### Scenario 1-2

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Searches by bar code
GUI -> EZShop: getProductTypeByBarCode()
EZShop --> GUI: return EZProductType

EZUser -> GUI: Selects record
EZUser -> GUI: Select a new product location
GUI -> EZShop: updatePosition()
EZShop --> GUI : return boolean
GUI --> EZUser : successful message
@enduml
```


## UC2 
### Scenario 2-1
```plantuml
@startuml
Actor Administrator
autonumber
Administrator -> GUI: Insert username
Administrator -> GUI: Insert password
Administrator -> GUI: Insert role
GUI -> EZShop: createUser()
EZShop -> EZUser: new EZUser()
EZUser --> EZShop: return EZUser
EZShop --> GUI: return Integer (unique identifier)
Administrator -> GUI: Selects EZUser rights
GUI -> EZShop: updateUserRights()
EZShop --> GUI: return boolean
Administrator -> GUI: Confirms
GUI --> Administrator: Successful message
@enduml
```


### Scenario 2-2
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Select an account to be deleted
GUI -> EZShop: deleteUser()
EZShop --> GUI: return boolean
GUI --> EZUser: Successful message
@enduml
```


### Scenario 2-3
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Select an account to be updated
GUI -> EZShop: getUser()
EZShop --> GUI: return EZUser
EZUser -> GUI: Select new rights for the account
GUI -> EZShop: updateUserRights()
EZShop --> GUI: return boolean
GUI --> EZUser: Successful message
@enduml
```


#
## UC3
### Scenario 3-1
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Create new order O for product PT
GUI -> EZShop: issueOrder()
EZShop -> EZOrder: new Order()
EZOrder --> EZShop: return Order
EZShop -> EZOrder: setStatus(Issued)
EZShop --> GUI: return orderID
GUI --> EZUser: show outcome message
@enduml
```

### Scenario 3-2
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Create new order O for product PT
GUI -> EZShop: getAllOrders()
EZShop --> GUI: returns List<Order>
GUI --> EZUser: Show orders
EZUser -> GUI: Register payment done for O
GUI -> EZShop: payOrder(orderID)
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return boolean
EZShop -> EZOrder: setStatus(Payed)
EZShop --> GUI: return boolean
GUI --> EZUser: Show outcome message
@enduml
```

### Scenario 3-5
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Create new order O for product PT
GUI -> EZShop: getAllOrders()
EZShop --> GUI: returns List<Order>
GUI --> EZUser: Show orders
EZUser -> GUI: Register payment done for O
GUI -> EZShop: payOrder(orderID)
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return boolean
EZShop -> EZOrder: setStatus(Payed)
EZShop --> GUI: return boolean
GUI --> EZUser: Show outcome message
EZUser -> GUI: Order O is going to be received
GUI -> EZShop: Order O as an input
EZShop -> EZShop: find quantity Q of order O
EZShop -> EZShop: find PT of order O
EZUser -> GUI: starting RFID
GUI -> EZShop: RFIDfrom
EZShop -> EZProduct: create new Products
EZShop --> GUI: boolean
GUI --> EZUser: Show outcome message
@enduml
```

#
## UC4
### Scenario 4-1
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Asks Cu personal data
GUI -> EZShop: getCustomer(id)
EZShop --> GUI: return Customer
EZUser -> GUI: Fills fields with Cu's personal data
EZUser -> GUI: Confirm
GUI -> EZShop: modifyCustomer(id, ...)
EZShop -> EZCustomer: update()
EZShop --> GUI: return boolean
GUI --> EZUser: Show outcome message
@enduml
```

### Scenario 4-2
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Creates a new Loyalty card L
GUI -> EZShop: createCard()
EZShop --> GUI: return cardCode
GUI --> EZUser: Show outcome message
EZUser -> GUI: EZUser attaches L to U
GUI -> EZShop: attachCardToCustomer()
EZShop --> GUI: return boolean
GUI --> EZUser: Show outcome message
@enduml
```

### Scenario 4-3
```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: EZUser selects customer record U
GUI -> EZShop: getCustomer()
EZShop --> GUI: return Customer
EZUser -> GUI: EZUser detaches L from U
GUI -> EZShop: modifyCustomer()
EZShop -> EZCustomer: setCard()
EZShop --> GUI: return boolean
GUI --> EZUser: Show outcome message 
@enduml
```


## UC5 

### Scenario 5-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI : Insert username
EZUser -> GUI : Insert password
EZUser -> GUI : confirm
GUI -> EZShop: login()
EZShop --> GUI : return EZUser
GUI --> EZUser: Show functionalities
@enduml
```

### Scenario 5-2

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Log out
GUI -> EZShop: logout()
EZShop --> GUI : return boolean
GUI --> EZUser : Change page
@enduml
```

## UC6 

### Scenario 6-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: start Sale Transaction
GUI -> EZShop: startSaleTransaction()
EZShop -> EZSaleTransaction: new EZSaleTransaction()
EZShop --> GUI: return TransactionID
EZUser -> GUI: Insert product BarCode
GUI -> EZShop: addProductToSale()
EZShop -> EZShop: getProductByBarCode()
EZShop -> EZTicketEntry: new EZTicketEntry()
EZTicketEntry --> EZShop: return EZTicketEntry
EZShop -> EZShop : updateQuantity()
EZUser -> GUI: close Sale Transaction
GUI -> EZShop: endSaleTransaction()
EZShop --> GUI: return boolean
GUI --> EZUser: ask Payment Type
EZUser -> GUI: Select payment type
ref over GUI, EZUser, EZShop, EZAccountBook
 manage Payment and update balance (see UC7)
end ref
@enduml
```

### Scenario 6-3

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: start Sale Transaction
GUI -> EZShop: startSaleTransaction()
EZShop -> EZSaleTransaction: new EZSaleTransaction()
EZSaleTransaction --> EZShop: return TransactionID
EZShop --> GUI: return boolean
EZUser -> GUI: Insert product BarCode
GUI -> EZShop: addProductToSale()
EZShop -> EZShop: getProductByBarCode()
EZShop -> EZTicketEntry: new EZTicketEntry()
EZTicketEntry --> EZShop: return EZTicketEntry
EZShop -> EZShop : updateQuantity()
EZShop --> GUI: return boolean
EZUser -> GUI: insert discount rate
GUI -> EZShop: applyDiscountRateToSale()
EZShop --> GUI: return boolean
EZUser -> GUI: close Sale Transaction
GUI -> EZShop: endSaleTransaction()
EZShop --> GUI: return boolean
GUI --> EZUser: ask Payment Type
EZUser -> GUI: Select payment type
ref over GUI, EZUser, EZShop, EZAccountBook
 manage Payment and update balance (see UC7)
end ref
@enduml
```

### Scenario 6-8

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: start Sale Transaction
GUI -> EZShop: startSaleTransaction()
EZShop -> EZSaleTransaction: new EZSaleTransaction()
EZShop --> GUI: return TransactionID
EZUser -> GUI: Insert product RFID
GUI -> EZShop: addProductToSaleRFID()
EZShop -> EZProduct: reset returnID
EZShop -> EZProduct: setSaleID()
EZUser -> GUI: close Sale Transaction
GUI -> EZShop: endSaleTransaction()
EZShop --> GUI: return boolean
GUI --> EZUser: ask Payment Type
EZUser -> GUI: Select payment type
ref over GUI, EZUser, EZShop, EZAccountBook
 manage Payment and update balance (see UC7)
end ref
@enduml
```

## UC7

### Scenario 7-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Insert credit card number
GUI -> EZShop: receiveCreditCardPayment()
EZShop -> EZShop: getCreditInTXTbyCardNumber()
EZShop -> EZShop: isValidCreditCard()
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return boolean
EZShop -> EZShop: updateCreditInTXTbyCardNumber()
EZShop --> GUI: return true
GUI --> EZUser: successful message
@enduml
```

### Scenario 7-4

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> EZUser: Collect banknotes and coins
EZUser -> EZUser: Compute cash quantity
EZUser -> GUI: Record cash payment
GUI -> EZShop: receiveCashPayment()
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return true
EZShop --> GUI: return double
GUI --> EZUser: return double
@enduml
```

## UC8

### Scenario 8-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Insert transaction ID
GUI -> EZShop: startReturnTransaction()
EZShop -> EZReturnTransaction: new EZReturnTransaction()
EZReturnTransaction --> EZShop: return EZReturnTransaction
EZShop --> GUI: return Integer (EZReturnTransaction ID)
EZUser -> GUI: Insert product BarCode
EZUser -> GUI: Insert quantity of returned items
GUI -> EZShop: returnProduct()
EZShop -> EZShop: getProductTypeByBarCode()
EZShop -> EZTicketEntry: new EZTicketEntry()
EZTicketEntry --> EZShop: return EZTicketEntry
EZShop --> GUI: return boolean
EZUser -> GUI: Close return transaction
GUI -> EZShop: endReturnTransaction()
EZShop -> EZShop: update related sale transaction
EZShop --> GUI: return boolean
GUI --> EZUser: Successful message
ref over GUI, EZUser, EZShop, EZAccountBook
Manage credit card return and update balance (go to UC10)
end ref
@enduml
```

### Scenario 8-4

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Insert transaction ID
GUI -> EZShop: startReturnTransaction()
EZShop -> EZReturnTransaction: new EZReturnTransaction()
EZReturnTransaction --> EZShop: return EZReturnTransaction
EZShop --> GUI: return Integer (EZReturnTransaction ID)
EZUser -> GUI: Insert product RFID
GUI -> EZShop: returnProductRFID()
EZShop -> EZProduct: setReturnID()
EZShop -> EZProduct: reset saleID
EZShop --> GUI: return boolean
EZUser -> GUI: Close return transaction
GUI -> EZShop: endReturnTransaction()
EZShop -> EZShop: update related sale transaction
EZShop --> GUI: return boolean
GUI --> EZUser: Successful message
ref over GUI, EZUser, EZShop, EZAccountBook
Manage credit card return and update balance (go to UC10)
end ref
@enduml
```

## UC9

### Scenario 9-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Selects a start date
EZUser -> GUI: Selects an end date
EZUser -> GUI: Send transaction list request
GUI -> EZShop: getCreditsAndDebits()
EZShop --> GUI: return transactions list
GUI --> EZUser: display list
@enduml
```

## UC10 

### Scenario 10-1

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> GUI: Insert credit card number
GUI -> EZShop: returnCreditCardPayment()
EZShop -> EZShop: getCreditInTXTbyCardNumber()
EZShop -> EZShop: isValidCreditCard()
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return boolean
EZShop -> EZShop: updateCreditInTXTbyCardNumber()
EZShop --> GUI: Amount returned
GUI --> EZUser: Successful message
@enduml
```

### Scenario 10-2

```plantuml
@startuml
Actor EZUser
autonumber
EZUser -> EZUser: Collect banconotes and coins
EZUser -> GUI: Record cash return
GUI -> EZShop: returnCashPayment()
EZShop -> EZShop: recordBalanceUpdate()
EZShop -> EZAccountBook: addBalanceOperation()
EZAccountBook -> EZAccountBook: new EZBalanceOperation()
EZAccountBook --> EZShop: return true
EZShop --> GUI: Amount returned
GUI --> EZUser: Successful message
@enduml
```

