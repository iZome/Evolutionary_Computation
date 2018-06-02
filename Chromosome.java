import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.HashMap;

final class Chromosome {

    /**
     * The list of cities, which are the genes of this chromosome.
     */
    protected int[] cityIndexes;

    /**
     * The cost of following the cityList order of this chromosome.
     */
    protected double cost;
    
    /**
     * Stores extra data about Chromosome needed for advaned mutations
     * and crossovers
     */
    protected HashMap<String, Object> metaData = new HashMap<>();


    protected static double[][] distanceMatrix;




    /**
     * @param cities The order that this chromosome would visit the cities.
     */
    Chromosome(int[] cityIndexes, City [] cities) {
    	this.cityIndexes = Arrays.copyOf(cityIndexes, cityIndexes.length);
        
        calculateCost(cities);
    }

    /**
     * @param cityIndexes
     * @param cost
     */
    Chromosome(int[] cityIndexes, double cost)  {
        this.cityIndexes = Arrays.copyOf(cityIndexes, cityIndexes.length);

        this.cost = cost;
    }

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    void calculateCost(City[] cities) {
        cost = 0;
        for (int i = 0; i < cityIndexes.length - 1; i++) {
            double dist = distanceMatrix[cityIndexes[i]][cityIndexes[i + 1]];
            cost += dist;
        }

        cost += distanceMatrix[cityIndexes[0]][cityIndexes[cityIndexes.length - 1]]; //Adding return home

        
    }

    /**
     * Get the cost for this chromosome. This is the amount of distance that
     * must be traveled.
     */
    double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    int getCity(int i) {
        return cityIndexes[i];
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    void setCities(int[] list) {
        for (int i = 0; i < cityIndexes.length; i++) {
            cityIndexes[i] = list[i];
        }
    }
    
    /**
     * Get the order of cities that this chromosome would visit.
     */
    public int [] getCities() {
        return Arrays.copyOfRange(cityIndexes, 0, cityIndexes.length);
    }

    /**
     * Set the index'th city in the city list.
     *
     * @param index The city index to change
     * @param value The city number to place into the index.
     */
    void setCity(int index, int value) {
        cityIndexes[index] = value;
    }

    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    public static void sortChromosomes(Chromosome chromosomes[], int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < num - 1; i++) {
                if (chromosomes[i].getCost() > chromosomes[i + 1].getCost()) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i + 1];
                    chromosomes[i + 1] = ctemp;
                    swapped = true;
                }
            }
        }
    }

    /**
     * Gets data in metaData HashMap
     * @return MetaData of Chromosome
     */
	public HashMap<String, Object> getMetaData() {
		return metaData;
	}

	public static Chromosome RandomChromosome(City [] cities) {
		int [] cityIndexes = java.util.stream.IntStream.rangeClosed(0, cities.length - 1).toArray();

		// Changes made to avoid loop and initialize the range with built-in functions
		/*for (int i = 0; i<cityIndexes1.length; ++i) {
			cityIndexes1[i] = i;
		}*/
		
		cityIndexes = ShuffleUtils.ShuffleArray(cityIndexes);
		
		return new Chromosome(cityIndexes, cities);
	}

	
    
    
    
}
