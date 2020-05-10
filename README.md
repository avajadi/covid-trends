# Simple graph visualising the speed at which COVID-19 is spreading in terms of case doubling time

This project was created from https://start.vaadin.com and then shoved into this form to do what I wanted it to do. This entire project is a simple, mostly-for-my-own-amusement kinda thing, so please use it with that in mind.
Doubling time, in days, is simply calculated for each day based on the number of confirmed cases that day and the previous day. Each data series is filtered to ignore values until there are 100 confirmed cases to at least give this some level of correctness in terms of calculations and the basis for them.
As indicated in the graph, all data is fetched from the John Hopkins University datasets available on github.
