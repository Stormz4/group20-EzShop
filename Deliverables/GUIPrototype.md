# Graphical User Interface Prototype  

Authors: 
- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524 

Date: 16/04/2021

| Version | Changes |
| ------- |---------|
| 1 | Added template|

\<Report here the GUI that you propose. You are free to organize it as you prefer. A suggested presentation matches the Use cases and scenarios defined in the Requirement document. The GUI can be shown as a sequence of graphical files (jpg, png)  >

# FR_7.1 - Login

# FR_1 - Sales management

## FR_1.1 - Provide Shopping Cart and Total Amount

### Scenario 1 - Provide Shopping Cart and Total Amount

![](GUI_images/1.0.1.png)

![](GUI_images/1.0.2.png)

![](GUI_images/1.0.3.png)

![](GUI_images/1.0.4.png)

### Scenario 1.1 - Creating new Fidelity Card during Sale

See "Scenario 18.1 - Add fidelity card"

### Scenario 1.2 - Inserting Product/Card Code manually

![](GUI_images/1.2.1.png)

![](GUI_images/1.0.2.png)

![](GUI_images/1.2.3.png)

### Scenario 1.3 - Remove Product from Shopping Cart

No difference with respect to 1.0.2, the only difference is that there is not the removed product anymore

## FR_1.2 - Handle Fidelity Card

### Scenario 2 - Authentication of a Fidelity Card

As in "Scenario 1.2 - Inserting Product/Card Code manually", but there is no warning message if scanning the Card produces no error

## FR_1.3 - Handle Payment

### Scenario 3 - Handle a Payment via Credit Card

![](GUI_images/3.0.1.png)

![](GUI_images/3.0.2.png)

![](GUI_images/3.0.3.png)

![](GUI_images/3.0.4.png)

![](GUI_images/3.0.5.png)

### Scenario 3.1 - Credit Card not recognized

![](GUI_images/3.1.1.png)

![](GUI_images/3.0.2.png)

### Scenario 3.2 - Handle a Failed Payment via Credit Card

![](GUI_images/3.2.1.png)

![](GUI_images/3.0.2.png)

### Scenario 3.3 - Change Payment Method

![](GUI_images/3.0.2.png)

![](GUI_images/3.0.1.png)

### Scenario 4 - Handle a Payment via Cash

![](GUI_images/4.0.png)

# FR_2 - Warehouse management

## FR_2.1 - Inventory management

### Scenario 9.1 - Show products

### Scenario 5.1 - Add product to inventory

### Scenario 6.1 - Remove product from inventory

### Scenario 7.1 - Update product

### Scenario 8.1 - Manage Low Stock Threshold

## FR_2.2 Manage orders 

### Scenario 13.1 - Show orders

### Scenario 10.1 - Place new order

### Scenario 11.1 - Cancel existing order

### Scenario 11.2 - 

### Scenario 12.1 - Edit existing order

### Scenario 12.2 - Edit existing order

# FR_3 Catalogue management

## Scenario 17.1 - Show products in the catalogue

![](GUI_images/17.1.png)

## Scenario 14.1 - Update selling price of product

![](GUI_images/14.1.png)

## Scenario 15.1 - Add product

![](GUI_images/15.1.png)

## Scenario 15.2 - Product already present

![](GUI_images/15.2.png)

## Scenario 16.1 - Remove product

![](GUI_images/16.1.png)

# FR_4 Customers management

![](GUI_images/CustomerManagement.png)

![](GUI_images/CashierTab.png)

## Scenario 20.1 - Show fidelity cards & card points

![](GUI_images/20.1.png)

## Scenario 18.1 - Add fidelity card

![](GUI_images/18.1.png)

## Scenario 18.2 - Card already present

![](GUI_images/18.2.png)

## Scenario 19.1 - Remove fidelity card

![](GUI_images/19.1.png)

![](GUI_images/19.1warning.png)

# FR_5 Support accounting

## FR_5.1 Update finance 

### Scenario 21.1 - Add invoice (passive)

### Scenario 21.2 - Add credit note

## FR_5.2 Show accounting data

## Scenario 22.1 - Show suppliers deadlines timetable - Up to date

## Scenario 22.2 - Show suppliers deadlines timetable - Expired warning 

## FR_5.3 Show statistics

### Scenario 23.1 - Show revenue and expenses in a timeframe

### Scenario 23.2 - Show best selling products

# FR_6 Accounts management

## Scenario 24.1 - Add new user account

## Scenario 24.1 - Warning message

## Scenario 25.1 - Remove account

## Scenario 26.1 - Update account
