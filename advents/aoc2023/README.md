# Advent of Code 2022

## Day 1
Calorie Counting
- Parsing and operations with lists

## Day 2
Rock Paper Scissors
- Parsing and operations with lists

## Day 3
Rucksack Reorganization
- Parsing and operations with lists

## Day 4
Camp cleanup
- Parsing and operations with lists

## Day 5
Supply Stacks
- Tower of Hanoi

## Day 6
Tuning Trouble
- Windowed operations with lists

## Day 7
No Space Left On Device
- Directory tree parsing

## Day 8
Treetop Tree House
- 2D grid

## Day 9
Rope Bridge
- 2D grid with rope simulation

## Day 10
Cathode-Ray Tube
- Microprocessor simulation and sprite display

## Day 11
Monkey in the Middle
- Use common maximum divisor to avoid the number explosion

## Day 12
Hill Climbing Algorithm
- Path finding

## Day 13
Distress Signal
- Parsing nested lists and calculation comparison

## Day 14
Regolith Reservoir
- 2D grid parsing and evolution calculation

## Day 15
Beacon Exclusion Zone
- 2D grid parsing and calculate exclusion zones using recursive shapes

## Day 16
Proboscidea Volcanium
- Non directed graph, with single and double path finding 

## Day 17
Pyroclastic Flow
- Tetris like. Find repetition patterns to avoid calculating the whole game.

## Day 18
Boiling Boulders
- 3D shape surface calculation. Make a negative of the exterior of the shape to calculate its external surface.

## Day 19
Not Enough Minerals
- Decision tree finding (path finding).
- In the second part the number of open paths were untreatable. To reduce complexity, precalculate de best 100 results for 22 steps to use it as seed for the calculation of the 32 steps

## Day 20
Grove Positioning System
- Displacement of elements in a list that is circular.

## Day 21
Monkey Math
- Apply operations to find a missing number. Instead of directly calculating the result, each node feeds its value to its dependent using a callback, so that the whole result is spread over the tree structure. The end result is retrieved using an observer. 
- For the second part, invert the operation to be able to calculate one of the bottom elements without having to calculate the whole tree for different values of this bottom node

## Day 22
Monkey Map
- Parse an irregular 2D grid and apply the movement rules
- In the second part the movement rules because the 2D grid is converted to a cube and a mapping between both is necessary

## Day 23
Unstable Diffusion
- 2D grid and apply movement rules

## Day 24
Blizzard Basin
- Path finding in a 2D map that changes with every step. Treat it as a 3D map with time as the third dimension. Precalculate the map through time and the apply a path finding algorithm 

## Day 25
Full of Hot Air
- Change numbers from base 10 to a pseudo base 5 with a displacement of the zero. It needs an adjustment of the number depending on the number of digits in base 5.

