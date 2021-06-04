# Design assessment

# Levelized structure map
![](Structure101_images/Dependency_Graph_LSM.png)
<br/><br/>

# Structural over complexity chart
![](Structure101_images/Structural_over_complexity_Chart.png)
<br/><br/>

# Size metrics
| Metric                                    | Measure     |
| ----------------------------------------- | ----------- |
| Packages                                  |    5        |
| Classes (outer)                           |    39       |
| Classes (all)                             |    39       |
| NI (number of bytecode instructions)      |    8986     |
| LOC (non comment non blank lines of code) |   ~ 3864    |
<br/><br/>

# Items with XS
| Item | Tangled | Fat  | Size | XS   |
| ---- | ------- | ---- | ---- | ---- |
|  ezshop.it.polito.ezshop.data.EZShop                                                                          |-|223|5289|2442|
|  ezshop.it.polito.ezshop.data.EZShop.endReturnTransaction(java.lang.Integer, boolean):boolean                 |-|21 |304 |86  |
|  ezshop.it.polito.ezshop.data.EZShop.deleteProductFromSale(java.lang.Integer, java.lang.String, int):boolean  |-|18 |307 |51  |
|  ezshop.it.polito.ezshop.data.EZShop.deleteReturnTransaction(java.lang.Integer):boolean                       |-|17 |229 |26  |
<br/><br/>

# Package level tangles
Since classes and interfaces are in the same package, there are no package-level tangles.<br/>
![](Structure101_images/Structural_over_complexity_Chart_ZeroTangle.png)
![](Structure101_images/XS_Sources_PieChart.png)

# Summary analysis
The main differences between the two designs are regarding the 0-1 relationships and regarding the hierarchy of Balance Operations:
- We thought that having two different classes for Position and LoyaltyCard was not useful, and for that reason we included them respectively 
    into ProductType and Customer.
- In order to follow the API, we have decided to not extend BalanceOperations anymore in the following classes: Order, 
    SaleTransaction, ReturnTransaction.

Also, in the design delivered on April 30th we didn't include the SQLiteDB class and we did not plan to implement interfaces 
in every leaf class, which is now the class. We didn't plan to add TicketEntry neither, which is now present in order to implement the associated interface.<br/>
For simplicity, we replaced Enums with constants.

Weaknesses:
* Code readability
* Complex code maintainability

These weaknesses are due to the fatness of EZShop class and the methods endReturnTransaction, deleteProductFromSale and deleteReturnTransaction.<br/>

One major improvement that could be done in the design is to move some methods of EZShop to some other classes.

