import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class Evolution{
	/* Parameters for first attempt */

	protected static double startMutationRate = 0.05;
	protected static double mutationRate;
	private static final int chromosomeFighters = 5;
	private static final boolean fittestSurvive = true;
    private static final int oneFiveGenerationFrequency = 10;
    private static final double oneFiveChangeRate = 0.90;
    private static final double oneFiveRule = 0.20;

    /* Class useful */
    private static Random random = new Random(42); // SET SEED FOR TESTING
    private static int successfulMutations = 0;
	/**
	 * The method used to generate a mutant of a chromosome
	 * @param original The chromosome to mutate.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Mutated chromosome.
	 */
	public static Chromosome Mutate(Chromosome original, City [] cityList){
      int [] cityIndexes = original.getCities();
      int [] newCityIndexes = Arrays.copyOf(cityIndexes, cityIndexes.length);
      
      return new Chromosome(newCityIndexes, cityList);
	}

	public static Chromosome MutateSwap(
            Chromosome original,
            City [] cityList
    )
    {
        int [] cityIndexes = original.cityIndexes;
        for(int pos1 = 0;
            pos1 < original.cityIndexes.length;
            pos1++)
        {
            if(random.nextFloat() < mutationRate)
            {
                int pos2 = (int) (original.cityIndexes.length * random.nextFloat());

                int city2 = cityIndexes[pos2];
                int city1 = cityIndexes[pos1];

                cityIndexes[pos1] = city2;
                cityIndexes[pos2] = city1;
            }
        }
        return new Chromosome(cityIndexes, cityList);
    }

	
	/**
	 * Breed two chromosomes to create a offspring
	 * @param parent1 First parent.
	 * @param parent2 Second parent.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Chromosome resuling from breeding parent.
	 */
	public static Chromosome Breed(Chromosome parent1, Chromosome parent2, City [] cityList){
	      int [] cityIndexes = parent1.getCities();
	      int [] newCityIndexes = Arrays.copyOf(cityIndexes, cityIndexes.length);
	      
	      return new Chromosome(newCityIndexes, cityList);
    }

    public static Chromosome BreedCrossoverSubsetFill(
            Chromosome parent1,
            Chromosome parent2,
            City [] cityList
    )
    {
        int [] newCityIndexes = new int[parent1.getCities().length];
        Arrays.fill(newCityIndexes, -1);

        int subsetStartPos = (int) (random.nextFloat() * newCityIndexes.length);
        int subsetEndPos = (int) (random.nextFloat() * newCityIndexes.length);

        for(int i = 0;
            i < newCityIndexes.length;
            i++)
        {
            if(subsetStartPos < subsetEndPos && (i > subsetStartPos && i < subsetEndPos))
            {
                newCityIndexes[i] = parent1.cityIndexes[i];
            }
            else if (subsetStartPos > subsetEndPos && !(i < subsetStartPos && i > subsetEndPos))
            {
                newCityIndexes[i] = parent1.cityIndexes[i];
            }
        }

        for(int i = 0;
            i < newCityIndexes.length;
            i++)
        {
            final int checkPos = parent2.cityIndexes[i];
            if(!IntStream.of(newCityIndexes).anyMatch(x -> x == checkPos))
            {
                for(int j = 0;
                    j < newCityIndexes.length;
                    j++)
                {
                    if(newCityIndexes[j] == -1)
                    {
                        newCityIndexes[j] = parent2.cityIndexes[i];
                        break;
                    }
                }
            }
        }

        if(IntStream.of(newCityIndexes).anyMatch(x -> x == -1))
        {
            System.out.println("yo");
        }

        return new Chromosome(newCityIndexes, cityList);
    }

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

      int offsetBecauseElitsm = 0;
      if(fittestSurvive)
      {
          offsetBecauseElitsm = 1;
          crossOverPopulation[0] = population[0];
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
         population[i] = MutateSwap(crossOverPopulation[i], cityList);
      }

      //checkChangeInCost(newPopulation, population);

       oneFiveRuleAddaptiveMutation(crossOverPopulation, population, generation);

      return population;
   }


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

   public static boolean checkDuplicates(
           int[] cityIndexes
   )
   {
       for (int i = 0;
           i < cityIndexes.length -1;
           i++){
           for (int j = i + 1;
               j < cityIndexes.length;
               j++)
           {
               if(cityIndexes[i] == cityIndexes[j])
               {
                   System.out.println(i);
                   System.out.println(j);
                   System.out.println(cityIndexes[i]);
                   return true;
               }
           }
       }
       return false;
   }

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
}