import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Math.exp;

class Evolution{
	/* Parameters for first attempt */


	protected static double startMutationRate = 0.1;
	protected static double mutationAmount = 0.4;
	protected static double crossOverAmount = 0.6;
	protected static double mutationRate;
	private static final int chromosomeFighters = 5;
	private static final boolean fittestSurvive = false;

	/* One Five Rule parameters */
	private static final boolean oneFiveActive = false;
    private static final int oneFiveGenerationFrequency = 5;
    private static final double oneFiveChangeRate = 0.90;
    private static final double oneFiveRule = 0.20;

    /* Simulated Annealing parameters */

    private static double defaultTemperature = 1.0;
    private static int defaultMaxTime = 10000;
    private static final double coolingEnhancer = 0.996;



    /* Class useful */
    private static Random random = new Random(); // SET SEED FOR TESTING
    private static int successfulMutations = 0;

    /**
     * Evolve given population to produce the next generation.
     * @param population The population to evolve.
     * @param cityList List of ciies, needed for the Chromosome constructor calls you will be doing when mutating and breeding Chromosome instances
     * @return The new generation of individuals.
     */
    public static Chromosome [] Evolve(
            Chromosome [] population,
            City [] cityList,
            int generation){
        Chromosome [] crossOverPopulation = new Chromosome [population.length]; // orignal code
        Chromosome [] mutationPopulation = new Chromosome [population.length];

        int offsetBecauseElitsm = 0;
        if(fittestSurvive)
        {
            offsetBecauseElitsm = 1;
            crossOverPopulation[0] = population[0];
            /*crossOverPopulation[0] = simluatedAnnealing(
                  crossOverPopulation[0],
                  cityList);*/
        }

        for (int i = offsetBecauseElitsm;
             i<population.length;
             i++)
        {
            Chromosome parent1 = ArenaSelection(population);
            Chromosome parent2 = ArenaSelection(population);

            crossOverPopulation[i] = BreedCrossoverSubsetFill(parent1, parent2, cityList);

        }


        for (int i = 0; i<population.length; ++i){
            mutationPopulation[i] = MutateInverse(crossOverPopulation[i], cityList);

        }

        /*
        for (int i = 0; i<population.length; ++i){
            mutationPopulation[i] = simluatedAnnealing(
                    mutationPopulation[i],
                    cityList);

        }*/

        Arrays.sort(population);
        Arrays.sort(mutationPopulation);


        if(mutationPopulation[0].getCost() < population[0].getCost()) {
            mutationPopulation[0] = simluatedAnnealing(population[0], cityList);
        }

        //checkChangeInCost(newPopulation, population);

        if(!checkValidCityIndexes(mutationPopulation)){
            System.out.println("Error in chromosomes");
        }

        if(oneFiveActive)
        {
            oneFiveRuleAddaptiveMutation(crossOverPopulation, mutationPopulation, generation);
        }

        return mutationPopulation;
    }
    /**
	 * The method used to generate a mutant of a chromosome
	 * @param original The chromosome to mutate.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Mutated chromosome.
	 */

	public static Chromosome MutateSwap(
            Chromosome original,
            City [] cityList
    )
    {
        if(random.nextFloat() < mutationRate) {
            int[] cityIndexes = original.cityIndexes;
            for (int pos1 = 0;
                 pos1 < original.cityIndexes.length;
                 pos1++) {
                if (random.nextFloat() < mutationAmount) {
                    int pos2 = (int) (original.cityIndexes.length * random.nextFloat());

                    int city2 = cityIndexes[pos2];
                    int city1 = cityIndexes[pos1];

                    cityIndexes[pos1] = city2;
                    cityIndexes[pos2] = city1;
                }
            }
            return new Chromosome(cityIndexes, cityList);
        }
        return original;
    }

    public static Chromosome MutateInverse(
            Chromosome original,
            City [] cityList)
    {
        if(random.nextFloat() < mutationAmount)
        {
            Chromosome mutated = makeNeighbor(original.cityIndexes, cityList);
            return(mutated);
        }
        return(original);
    }

	
	/**
	 * Breed two chromosomes to create a offspring
	 * @param parent1 First parent.
	 * @param parent2 Second parent.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Chromosome resuling from breeding parent.
	 */

    public static Chromosome BreedCrossoverSubsetFill(
            Chromosome parent1,
            Chromosome parent2,
            City [] cityList
    )
    {
        int [] newCityIndexes = new int[parent1.getCities().length];
        if(random.nextFloat() < crossOverAmount) {

            Arrays.fill(newCityIndexes, -1);

            int subsetStartPos = (int) (random.nextFloat() * newCityIndexes.length);
            int subsetEndPos = (int) (random.nextFloat() * newCityIndexes.length);

            for (int i = 0;
                 i < newCityIndexes.length;
                 i++) {
                if (subsetStartPos < subsetEndPos && (i > subsetStartPos && i < subsetEndPos)) {
                    newCityIndexes[i] = parent1.cityIndexes[i];
                } else if (subsetStartPos > subsetEndPos && !(i < subsetStartPos && i > subsetEndPos)) {
                    newCityIndexes[i] = parent1.cityIndexes[i];
                }
            }

            for (int i = 0;
                 i < newCityIndexes.length;
                 i++) {
                final int checkPos = parent2.cityIndexes[i];
                if (!IntStream.of(newCityIndexes).anyMatch(x -> x == checkPos)) {
                    for (int j = 0;
                         j < newCityIndexes.length;
                         j++) {
                        if (newCityIndexes[j] == -1) {
                            newCityIndexes[j] = parent2.cityIndexes[i];
                            break;
                        }
                    }
                }
            }
        }

        else{
            newCityIndexes = parent1.cityIndexes;
        }

        if(IntStream.of(newCityIndexes).anyMatch(x -> x == -1))
        {
            System.out.println("yo");
        }

        return new Chromosome(newCityIndexes, cityList);
    }


    /**
     * @param population
     * @return
     */
    public static Chromosome ArenaSelection(
           Chromosome [] population)
   {
       Chromosome [] arenaFighters = new Chromosome[chromosomeFighters];

       for(int i = 0;
           i < chromosomeFighters;
           i++)
       {
           int randomChromosome = (int) (random.nextFloat() * population[i].cityIndexes.length);
           arenaFighters[i] = population[randomChromosome]; //possibility for picking same
       }

       int lastFighterStandingPos = getFittestChromosome(arenaFighters);
       return arenaFighters[lastFighterStandingPos];
   }

    /**
     * @param population
     * @return
     */
   public static int getFittestChromosome(Chromosome [] population){
       double [] costArray = new double[population.length];

       for(int i = 0;
           i < population.length;
           i++)
       {

           costArray[i] = population[i].getCost();
       }

       return getMinimum(costArray);
   }

    /**
     * @param values
     * @return
     */
   public static int getMinimum(
           double [] values)
   {
       int smallestPos = 0;
       for(int i = 1;
           i < values.length;
           i++)
       {
           if(values[i] < values[smallestPos])
           {
               smallestPos = i;
           }
       }

       return smallestPos;
   }


    /**
     * @param newPopulation
     * @param population
     * @return
     */
   public static boolean checkChangeInCost(
           Chromosome[] newPopulation,
           Chromosome[] population
   )
   {
       int mutations = 0;

       for(int i = 0;
           i < newPopulation.length;
           i++)
       {
           if(newPopulation[i].cost != population[i].cost)
           {
               mutations++;

           }
       }
       boolean change = mutations > 0;
       return change;
   }

   public static boolean checkValidCityIndexes(
           Chromosome [] chromosomes
   )
   {
       for(Chromosome chromosome: chromosomes){
           if(Arrays.stream(chromosome.cityIndexes).distinct().count() != chromosome.cityIndexes.length){
               return false;
           }
       }
       return true;
   }

    /* SIMULATED ANNEALING local search */

    /**
     * @return
     */
    public static Chromosome simluatedAnnealing(
            Chromosome chromosome,
            City [] cityList
    ) {
        return simluatedAnnealing(
                chromosome,
                defaultTemperature ,
                defaultMaxTime ,
                cityList );
    }

    /**
     * @param initialCitySolution
     * @param temperature
     * @param maxTime
     * @param cityList
     * @return
     */
    private static Chromosome simluatedAnnealing(
            Chromosome initialCitySolution,
            double temperature,
            int maxTime,
            City [] cityList
    )
    {

        //temperature = findIntialTemperatur(timeInterval);
        Chromosome currentSolution = new Chromosome(initialCitySolution.cityIndexes, cityList);
        Chromosome bestSolution = new Chromosome(initialCitySolution.cityIndexes, cityList);

        CurrentAndBestSolution currentAndBestSolution = new CurrentAndBestSolution(currentSolution, bestSolution);

        int time = 0;
        double startTemperature = temperature;

        while(time <= maxTime)
        {
            temperature = startTemperature * Math.pow(coolingEnhancer, time);
            currentAndBestSolution = annealing(currentAndBestSolution, temperature, cityList);
            time = time + 1;
        }

        return(currentAndBestSolution.getBestSolution());
    }

    /**
     * @param currentAndBestSolution
     * @param temperature
     * @param cityList
     * @return
     */
    private static CurrentAndBestSolution annealing(
            CurrentAndBestSolution currentAndBestSolution,
            double temperature,
            City [] cityList
    )
    {
        Chromosome newSolution = neighbor(currentAndBestSolution.getCurrentSolution().cityIndexes, cityList);

        double deltaFitness = newSolution.cost - currentAndBestSolution.getCurrentSolution().cost;

        double acceptanceProbability = Math.exp(-deltaFitness/temperature);
        if(random.nextFloat() < acceptanceProbability)
        {
            currentAndBestSolution.setCurrentSolution(new Chromosome(newSolution.cityIndexes, cityList));

            if(newSolution.cost < currentAndBestSolution.getBestSolution().cost)
            {
                currentAndBestSolution.setBestSolution(new Chromosome(newSolution.cityIndexes, cityList));
            }
        }
        return currentAndBestSolution;
    }

    /*private static double findIntialTemperatur(
            int timeInterval
    )
    {
        int downHillMoves = 0;
    }*/

    private static Chromosome neighbor(
            int [] cityIndexes,
            City [] cityList
    )
    {
        int [] newIndexes = Arrays.copyOf(cityIndexes, cityIndexes.length);

        int start = 0; int stop = 0;

        while(start == stop){
            start = random.nextInt(newIndexes.length);
            stop = random.nextInt(newIndexes.length);
        }

        if(start > stop){
            int sTemp = stop;
            stop = start;
            start = sTemp;
        }

        while(start <= stop){
            int tempValue = newIndexes[start];
            newIndexes[start] = newIndexes[stop];
            newIndexes[stop] = tempValue;
            start++;
            stop--;
        }

        return new Chromosome(newIndexes, cityList);
    }

    /**
     * @param cityOrderSolution
     * @param cityList
     * @return
     */
    private static Chromosome makeNeighbor(
            int [] cityOrderSolution,
            City [] cityList
    )
    {
        int subsetStartPos = (int) (random.nextFloat() * cityOrderSolution.length);
        int subsetEndPos = (int) (random.nextFloat() * cityOrderSolution.length);

        if(subsetEndPos < subsetStartPos)
        {
            int tempPos = subsetStartPos;
            subsetStartPos = subsetEndPos;
            subsetEndPos = tempPos;
        }
        cityOrderSolution = reverseSubArray(cityOrderSolution, subsetStartPos, subsetEndPos);
        return new Chromosome(cityOrderSolution, cityList);

    }

    /**
     * @param orderArray
     * @param subsetStartPos
     * @param subsetEndPos
     * @return
     */
    protected static int [] reverseSubArray(
            int [] orderArray,
            int subsetStartPos,
            int subsetEndPos
    )
    {
        int checking = ((subsetEndPos - subsetStartPos + 1)/2);
        int j = subsetEndPos;
        for (int i = subsetStartPos;
             i < subsetStartPos + ((subsetEndPos - subsetStartPos + 1)/2);
             i++)
        {
            int tempValue = orderArray[i];
            orderArray[i] = orderArray[j];
            orderArray[j] = tempValue;
            j -= 1;
        }
        return(orderArray);
    }

    /* ONE FIVE RULE addaptive mutation rate */

    /**
     * @param crossOverPopulation
     * @param population
     * @param generation
     */
    private static void oneFiveRuleAddaptiveMutation(
            Chromosome[] crossOverPopulation,
            Chromosome[] population,
            int generation
    )
    {
        updateSuccessfulMutations(crossOverPopulation, population);

        if((generation + 1) % oneFiveGenerationFrequency == 0)
        {
            compareOneFiveRule(crossOverPopulation.length*oneFiveGenerationFrequency);
            successfulMutations = 0;
        }
    }


    /**
     * @param crossOverPopulation
     * @param population
     */
    private static void updateSuccessfulMutations(
            Chromosome[] crossOverPopulation,
            Chromosome[] population
    )
    {
        for(int i = 0;
            i < crossOverPopulation.length;
            i++)
        {
            if(crossOverPopulation[i].cost > population[i].cost)
            {
                successfulMutations++;
            }
        }
    }

    /**
     * @param mutationNumber
     */
    private static void compareOneFiveRule(
            int mutationNumber)
    {
        double successfulMutationRate = (double)successfulMutations/(double)mutationNumber;

        if(successfulMutationRate > oneFiveRule)
        {
            mutationRate = mutationRate / oneFiveChangeRate;
        }
        else if(successfulMutationRate < oneFiveRule)
        {
            mutationRate = mutationRate * oneFiveChangeRate;
        }
    }

}