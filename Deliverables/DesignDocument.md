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

GUIEZShop ..> EZShop
@enduml
```

GUIEZShop contains view and controller, while EZShop contains model and logic. The architetural pattern choosed is MVC.

# Low level design

<for each package, report class diagram>

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
    +List<Order> getAllOrders() throws UnauthorizedException;
    
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

class UserAccount{
    +username
    +password
    +privilege
}

Shop -- "*" UserAccount

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
    +barCode
    +description
    +sellPrice
    +quantity
    +discountRate
    +notes
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






# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>


!!! Useful link:    https://www.tablesgenerator.com/markdown_tables# 


| FR ID | Shop | UserAccount | Administrator | Order | ProductType | Product | Position | SaleTransaction | Quantity | LoyaltyCard | Customer | ReturnTransaction | AccountBook | FinancialTransaction | Credit | Debit | Sale | Return |
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

```plantuml
@startuml
Shopmanager --> Shop: Insert product descrition
Shopmanager --> Shop: Insert new bar code
Shopmanager --> Shop: Insert price per unit
Shopmanager --> Shop: Insert product notes
Shopmanager --> Shop: Insert location
Shopmanager --> Shop: Confirm
@enduml
```