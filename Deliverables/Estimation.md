1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
# Project Estimation  
Authors:

- Mattia Lisciandrello s286329
- Christian Casalini s281823
- Palmucci Leonardo s288126
- Dario Lanfranco s287524

Date: 21/04/2021

| Version | Changes |
| ------- |---------|
| 1 | Added first version of estimation. |
| 2 | Changed estimate calendar time, including now each step |
| 3 | Reconsidered calculations for effort, cost and activity decomposition | 

# Contents
- [Estimate by product decomposition]
- [Estimate by activity decomposition ]
# Estimation approach
<Consider the EZGas  project as described in YOUR requirement document, assume that you are going to develop the project INDEPENDENT of the deadlines of the course>
# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC =  Estimated number of classes to be developed   |  20          |             
| A = Estimated average size per class, in LOC       |      80               | 
| S = Estimated size of project, in LOC (= NC * A) |20*80= 1600 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |          Considering that behind a LOC there is a lot of work (requirement, design etc) -> 475 person hours  (160 for coding)    |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 475*30=14250 | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) | Almost 3 weeks |               

For each estimation an approximated value has been given (20 -> ~20 etc...). To estimate the number of classes, we've considered the worst case scenario (=most number of classes possible.)


# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirement analysis | 105 |
| Design | 60 | 
| Coding | 160 |
| Testing | 150 | 

###
Insert here Gantt chart with above activities.

The Gantt chart is made considering that the team is made of 4 people, which dedicates 8 hours per day, 5 days per week.


```plantuml
@startuml
2021/04/04 is colored in salmon
2021/04/03 is colored in salmon
2021/04/10 is colored in salmon
2021/04/11 is colored in salmon
2021/04/17 is colored in salmon
2021/04/18 is colored in salmon

[Requirement analysis] lasts 6 days
[Design] lasts 2 days
[Coding + testing (unit, integration, GUI)] lasts 14 days

Project starts 2021-04-01
[Requirement analysis] starts 2021-04-01
[Design] starts at [Requirement analysis]'s end
[Coding + testing (unit, integration, GUI)] starts at [Design]'s end
@enduml
```

(We added extra days for weekends, since the team works 5 days per week)

This second Gantt chart is made considering the hours the team of 4 people dedicates to the project (and not "8 hours per day, 5 days per week"). Requirement analysis was 4 days, went to 6. Coding + testing goes from 10 to 14 

```plantuml
@startuml
[Requirement analysis] lasts 21 days
[Design] lasts 8 days
[Coding + testing (unit, integration, GUI)] lasts 25 days

Project starts 2021-04-01
[Requirement analysis] starts 2021-04-01
[Design] starts 2021-04-23
[Coding + testing (unit, integration, GUI)] starts 2021-05-02
@enduml
```