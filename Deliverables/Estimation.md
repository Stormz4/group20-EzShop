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
| A = Estimated average size per class, in LOC       |      40               | 
| S = Estimated size of project, in LOC (= NC * A) |20*40= 800 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |          S/Productivity = 800/10=80         |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 80*30=2400 | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) |          80 person hours/4 = 20 person hours each. Around 1 calendar week for coding (and another 1 for testing).          |               

For each estimation an approximated value has been given (20 -> ~20 etc...). To estimate the number of classes, we've considered the worst case scenario (=most number of classes possible.)

# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Review existing systems| 15 |
| Perform work analysis | 15 |
| Model process | 5 |
| Identify user requirements | 60 |
| Identify performance requirements | 10 |
| TOTAL | 105 |

###
Insert here Gantt chart with above activities

```plantuml
@startuml
@startuml
[Review existing systems] lasts 3 days
[Perform work analysis] lasts 4 days
[Model process] lasts 1 days
[Identify user requirements] lasts 12 days
[Identify performance requirements] lasts 2 days

Project starts 2021-04-01
[Review existing systems] starts 2021-04-01
[Perform work analysis] starts 2021-04-03 
[Model process] starts 2021-04-07 
[Identify user requirements] starts 2021-04-08
[Identify performance requirements] starts 2021-04-20
@enduml
```