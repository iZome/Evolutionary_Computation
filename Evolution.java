import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class Evolution{
	/* Parameters for first attempt */

	private static final double mutationRate = 0.001;
	private static final int chromosomeFighters = 10;
	//private static final boolean fittestSurvive = true;
    private static Random random = new Random(42); // SET SEED FOR TESTING
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

	public static  Chromosome MutateSwap(
	        Chromosome original,
            City [] cityList
    )
    {
        int [] cityIndexes = original.getCities();
        for(int pos1 = 0;
            pos1 < original.cityIndexes.length;
            pos1++)
        {
            if(random.nextFloat() < mutationRate)
            {
                int pos2 = (int) (original.cityIndexes.length * random.nextFloat());

                int city1 = original.cityIndexes[pos2];
                int city2 = original.cityIndexes[pos1];

                original.cityIndexes[pos1] = city1;
                original.cityIndexes[pos2] = city2;
            }
        }
        //original.calculateCost(cityList);
        return(original);
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
        int [] cityIndexes = parent1.getCities();
        int [] newCityIndexes = new int[cityIndexes.length];
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
   public static Chromosome [] Evolve(Chromosome [] population, City [] cityList){
      Chromosome [] newPopulation = new Chromosome [population.length]; // orignal code

      for (int i = 0;
           i<population.length;
           i++)
      {
          Chromosome parent1 = ArenaSelection(population);
          Chromosome parent2 = ArenaSelection(population);

          newPopulation[i] = BreedCrossoverSubsetFill(parent1, parent2, cityList);

      }

      for (int i = 0; i<population.length; ++i){
         population[i] = MutateSwap(newPopulation[i], cityList);
      }
      
      return population;
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
}