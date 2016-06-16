# Pizza [![Build Status](https://travis-ci.org/sp-niemand/pizza.svg?branch=master)](https://travis-ci.org/sp-niemand/pizza)

## Problem description

Tieu owns a pizza restaurant and he manages it in his own way. While in a normal restaurant, a
customer is served by following the first-come, first-served rule, Tieu simply minimizes the average
waiting time of his customers. So he gets to decide who is served first, regardless of how sooner or later
a person comes.

Different kinds of pizzas take different amounts of time to cook. Also, once he starts cooking a pizza, he
cannot cook another pizza until the first pizza is completely cooked. Let's say we have three customers
who come at time t=0, t=1, & t=2 respectively, and the time needed to cook their pizzas is 3, 9, & 6
respectively. If Tieu applies first-come, first-served rule, then the waiting time of three customers is 3,
11, & 16 respectively. The average waiting time in this case is (3 + 11 + 16) / 3 = 10. This is not an
optimized solution. After serving the first customer at time t=3, Tieu can choose to serve the third
customer. In that case, the waiting time will be 3, 7, & 17 respectively. Hence the average waiting time
is (3 + 7 + 17) / 3 = 9.

Help Tieu achieve the minimum average waiting time. For the sake of simplicity, just find the integer part
of the minimum average waiting time.

## Remarks on solution

This is a scheduling problem of class `1|r>=0|mft` (1-machine, 
with release times, without preemption, optimize by mean flow time).

It is an NP-hard problem, so for a large number of jobs it has no 
optimal solution in realistic time. Here an approximation algorithm (PSW) is used. It is
based on the "shortest remaining processing time" rule.

## Run
* Clone this repository
* Use SBT (`sbt run`)
* Enter the data

OR

* You can pipe the data to the program using something like 
`cat <datafile> | sbt run`

## Tests
`sbt test`
