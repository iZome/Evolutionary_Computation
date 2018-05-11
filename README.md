# EC-TSP-UCT
Starting code for the Evolutionary Computing TSP Assignment (UCT)

Original code by Geoff Nitschke, with anti-cheating modifications by Steven Tupper.

## Instructions
See the assignment instructions on your Vula tab for the full set of instructions. The following deals with the code of the assignment. You must do all your coding within the **Evolution.java** file. The code counts how many times you call the **Chromosome.calculateCost()** method. You may call the method up to **100\*(numRuns+1)\*populationSize** times. calculateCost() is the function for evaluating a Chromosome, and in real situations the evaluation cost may be much more computationally expensive. For this reason the calculateCost() method can only be called a limited amount of times, for learning purposes.

You can't modify the Chromosome class but it has a dictionary **"metaData"** for storing special data related to your own solution's Chromosomes e.g. mutation rates that vary by Chromosome.
