
# Project Estimation  
Authors:

- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524

Date: 30/04/2021

| Version | Changes |
| ------- |---------|
| 1 | Added first version of estimation. |
| 2 | Changed estimate calendar time, including now each step |
| 3 | Reconsidered calculations for effort, cost and activity decomposition | 
| 4 | Added new gantt chart & new version | 
| 5 | Fixed links of Contents section |

# Contents
- [Estimate by product decomposition](#Estimate-by-product-decomposition)
- [Estimate by activity decomposition ](#Estimate-by-activity-decomposition)

# Estimation approach
# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC =  Estimated number of classes to be developed   |  20          |             
| A = Estimated average size per class, in LOC       |      150               | 
| S = Estimated size of project, in LOC (= NC * A) |20*150= 3000 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |          300 person hours   |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 300*30=9000€ | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) | Two weeks (10 days, exluding weekends. 300/32 -> 9.4 -> 10) |               

For each estimation an approximated value has been given (20 -> ~20 etc...). To estimate the number of classes, we've considered the worst case scenario (=most number of classes possible.)


# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirement analysis | 105 |
| Design | 60 | 
| Coding | 100 |
| Unit testing | 20 | 
| Integration testing | 50 | 
| GUI testing | 20 |
| TOTAL | 355 | 

###
Insert here Gantt chart with above activities.

The Gantt chart is made considering that the team is made of 4 people, which dedicates 8 hours per day, 5 days per week.


```plantuml
@startuml
saturday are closed
sunday are closed

[Requirement analysis] lasts 4 days
[Design] lasts 2 days
[Coding] lasts 4 days
[Unit testing] lasts 1 days 
[Integration testing] lasts 2 days
[GUI testing] lasts 1 days

Project starts 2021-04-01
[Requirement analysis] starts 2021-04-01
[Design] starts at [Requirement analysis]'s end
[Coding] starts at [Design]'s end
[Unit testing] starts at [Coding]'s end
[Integration testing] starts at [Unit testing]'s end
[GUI testing] starts at [Integration testing]'s end
@enduml
```
