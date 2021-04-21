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
| 2 | Added GUI prototypes for Customers management & Catalogue management |
| 3 | Added GUI prototypes for Support accounting & Accounts management |
| 4 | Added GUI prototypes for Sales management |
| 5| Added GUI prototypes for Warehouse management |

# Home Page - Login

![](GUI_images/Homepage.png)

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

![](GUI_images/WarehouseManagement/Inventory.png)

### Scenario 5.1 - Add product to inventory

![](GUI_images/WarehouseManagement/Inventory_New_Product.png)

![](GUI_images/WarehouseManagement/Inventory_New_Product_Filling_In.png)

### Scenario 5.2 - Add product: missing compulsory fields

![](GUI_images/WarehouseManagement/Inventory_New_Product_Missing_Compulsory_Field(s).png)

### Scenario 5.3 - Add new product: new Supplier
![](GUI_images/WarehouseManagement/Inventory_New_Supplier.png)

### Scenario 6.1 - Remove product from inventory

![](GUI_images/WarehouseManagement/Inventory_Remove_Product.png)

### Scenario 6.2 - Remove multiple products from inventory

![](GUI_images/WarehouseManagement/Inventory_Select_Products.png)

![](GUI_images/WarehouseManagement/Inventory_Remove_Products.png)

### Scenario 7.1 - Edit product

![](GUI_images/WarehouseManagement/Inventory_Edit_Product.png)

### Scenario 8.1 - Show products

![](GUI_images/WarehouseManagement/Inventory.png)

## FR_2.2 Manage orders 

### Scenario 10.1 - Place new order from 'Orders' window

![](GUI_images/WarehouseManagement/Orders.png)

![](GUI_images/WarehouseManagement/New_order.png)

### Scenario 10.2 - Place new order from 'Inventory' window

![](GUI_images/WarehouseManagement/Inventory_Select_Products.png)

![](GUI_images/WarehouseManagement/New_order.png)

### Scenario 10.3 - Place new order not possible

![](GUI_images/WarehouseManagement/Inventory_Select_Products.png)

![](GUI_images/WarehouseManagement/Inventory_Select_Products_different_Suppliers.png)

### Scenario 11.1 - Cancel order

![](GUI_images/WarehouseManagement/Orders_Cancel_Orders.png)

### Scenario 11.2 - Cancel multiple orders

![](GUI_images/WarehouseManagement/Orders_Selecting.png)

![](GUI_images/WarehouseManagement/Orders_Cancel_Orders.png)

### Scenario 12.1 - Edit order

![](GUI_images/WarehouseManagement/Orders_Edit_Order.png)

### Scenario 13.1 - Show orders

![](GUI_images/WarehouseManagement/Orders.png)


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

![](GUI_images/SupportAccounting/AccountantMenu1.png)

![](GUI_images/SupportAccounting/Show_invoices.png)

### Scenario 21.1 - Add invoice (passive)

![](GUI_images/SupportAccounting/Add_passive_invoice.png)

### Scenario 21.2 - Add credit note

![](GUI_images/SupportAccounting/Add_credit_note.png)

## FR_5.2 Show accounting data

![](GUI_images/SupportAccounting/AccountantMenu2.png)

![](GUI_images/SupportAccounting/Show_accounting_data.png)

![](GUI_images/SupportAccounting/Show_suppliers.png)

![](GUI_images/SupportAccounting/Show_balance_sheet.png)

![](GUI_images/SupportAccounting/Show_financial_statement.png)

## Scenario 22.1 - Show suppliers deadlines timetable - Up to date

![](GUI_images/SupportAccounting/Show_suppliers_deadline_timetable.png)

## Scenario 22.2 - Show suppliers deadlines timetable - Expired warning

![](GUI_images/SupportAccounting/Show_suppliers_deadline_timetable-Expired.png)

## FR_5.3 Show statistics

![](GUI_images/SupportAccounting/AccountantMenu4.png)

### Scenario 23.1 - Show revenue and expenses in a timeframe

![](GUI_images/SupportAccounting/Show_statistics_Revenues&Expenses.png)

### Scenario 23.2 - Show best selling products

![](GUI_images/SupportAccounting/Show_statistics_best_selling_products.png)

## FR_5.4 Show banking data

![](GUI_images/SupportAccounting/AccountantMenu3.png)

![](GUI_images/SupportAccounting/Show_banking_data.png)

# FR_6 Accounts management

![](GUI_images/AccountsManagement/Admin_tab.png)

## Scenario 24.1 - Add new user account

![](GUI_images/AccountsManagement/Add_new_account1.png)

![](GUI_images/AccountsManagement/Add_new_account2.png)

![](GUI_images/AccountsManagement/Add_new_account3.png)

![](GUI_images/AccountsManagement/Add_new_account4.png)

## Scenario 24.2 - Warning message

![](GUI_images/AccountsManagement/Warning-not_all_fields_completed.png)

## Scenario 25.1 - Remove account

![](GUI_images/AccountsManagement/Show_accounts1.png)

![](GUI_images/AccountsManagement/Show_accounts2.png)

![](GUI_images/AccountsManagement/Select_account_to_manage.png)

![](GUI_images/AccountsManagement/Remove_account.png)

## Scenario 26.1 - Update user data

![](GUI_images/AccountsManagement/Show_accounts2.png)

![](GUI_images/AccountsManagement/Select_account_to_manage.png)

![](GUI_images/AccountsManagement/Selection_of_updates.png)

![](GUI_images/AccountsManagement/Edit_user_data1.png)

![](GUI_images/AccountsManagement/Edit_user_data2.png)

## Scenario 26.2 - Update Account button disabled

![](GUI_images/AccountsManagement/Update_account_option_disabled.png)

## Scenario 26.3 - Update user privileges

![](GUI_images/AccountsManagement/Show_accounts2.png)

![](GUI_images/AccountsManagement/Select_account_to_manage.png)

![](GUI_images/AccountsManagement/Selection_of_updates.png)

![](GUI_images/AccountsManagement/Edit_privileges.png)

![](GUI_images/AccountsManagement/Edit_privileges2.png)

