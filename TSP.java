import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;

import static java.lang.Math.round;

public final class TSP {

   private static final int cityShiftAmount = 60; //DO NOT CHANGE THIS.

   private static Random randomGenerator = new Random();

   /**
    * How many cities to use.
    */
   protected static int cityCount;

   /**
    * How many chromosomes to use.
    */
   protected static int populationSize = 100; //DO NOT CHANGE THIS.

   /**
    * The part of the population eligable for mating.
    */
   protected static int matingPopulationSize;

   /**
    * The part of the population selected for mating.
    */
   protected static int selectedParents;

   /**
    * The current generation
    */
   protected static int generation;

   /**
    * The list of cities (with current movement applied).
    */
   protected static City[] cities;

   /**
    * The list of cities that will be used to determine movement.
    */
   private static City[] originalCities;

   /**
    * The list of chromosomes.
    */
   protected static Chromosome[] chromosomes;

   /**
   * Frame to display cities and paths
   */
   private static JFrame frame;

   /**
    * Integers used for statistical data
    */
   private static double min;
   private static double avg;
   private static double max;
   private static double sum;
   private static double genMin;

   /**
    * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
    */
   private static int width = 600;
   private static int height = 600;


   private static Panel statsArea;
   private static TextArea statsText;



   /*
    * Writing to an output file with the costs.
    */
   private static void writeLog(String content) {
      String filename = "results.out";
      FileWriter out;

      try {
         out = new FileWriter(filename, true);
         out.write(content + "\n");
         out.close();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

    private static void logProgress(String filename, String content, int run) {
        FileWriter out;
        boolean append = true;

        if(run == 1)
        {
            append = false;
        }

        try {
            out = new FileWriter(filename, append);
            out.write(content + "\n");
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

   /*
    *  Deals with printing same content to System.out and GUI
    */
   private static void print(boolean guiEnabled, String content) {
      if(guiEnabled) {
         statsText.append(content + "\n");
      }

      System.out.println(content);
   }

   public static void evolve(int generation) {
      chromosomes = Evolution.Evolve(chromosomes, cities, generation);
   }

   /**
    * Update the display
    */
   public static void updateGUI() {
      Image img = frame.createImage(width, height);
      Graphics g = img.getGraphics();
      FontMetrics fm = g.getFontMetrics();

      g.setColor(Color.black);
      g.fillRect(0, 0, width, height);

      if (true && (cities != null)) {
         for (int i = 0; i < cityCount; i++) {
            int xpos = cities[i].getx();
            int ypos = cities[i].gety();
            g.setColor(Color.green);
            g.fillOval(xpos - 5, ypos - 5, 10, 10);

            //// SHOW Outline of movement boundary
            // xpos = originalCities[i].getx();
            // ypos = originalCities[i].gety();
            // g.setColor(Color.darkGray);
            // g.drawLine(xpos + cityShiftAmount, ypos, xpos, ypos + cityShiftAmount);
            // g.drawLine(xpos, ypos + cityShiftAmount, xpos - cityShiftAmount, ypos);
            // g.drawLine(xpos - cityShiftAmount, ypos, xpos, ypos - cityShiftAmount);
            // g.drawLine(xpos, ypos - cityShiftAmount, xpos + cityShiftAmount, ypos);
         }

         g.setColor(Color.gray);
         for (int i = 0; i < cityCount; i++) {
            int icity = chromosomes[0].getCity(i);
            if (i != 0) {
               int last = chromosomes[0].getCity(i - 1);
               g.drawLine(
                  cities[icity].getx(),
                  cities[icity].gety(),
                  cities[last].getx(),
                  cities[last].gety());
            }
         }

         int homeCity = chromosomes[0].getCity(0);
         int lastCity = chromosomes[0].getCity(cityCount - 1);

         //Drawing line returning home
         g.drawLine(
                cities[homeCity].getx(),
                cities[homeCity].gety(),
                cities[lastCity].getx(),
                cities[lastCity].gety());
      }
      frame.getGraphics().drawImage(img, 0, 0, frame);
   }

   private static City[] LoadCitiesFromFile(String filename, City[] citiesArray) {
      ArrayList<City> cities = new ArrayList<City>();
      try
      {
         FileReader inputFile = new FileReader(filename);
         BufferedReader bufferReader = new BufferedReader(inputFile);
         String line;
         while ((line = bufferReader.readLine()) != null) {
            String [] coordinates = line.split(", ");
            cities.add(new City(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
         }

         bufferReader.close();

      }
      catch (Exception e) {
         System.out.println("Error while reading file line by line:" + e.getMessage());
      }

      citiesArray = new City[cities.size()];
      return cities.toArray(citiesArray);
   }

   private static City[] MoveCities(City[]cities) {
      City[] newPositions = new City[cities.length];
      //Random randomGenerator = new Random(); // SET SEED FOR TESTING

      for(int i = 0; i < cities.length; i++) {
         int x = cities[i].getx();
         int y = cities[i].gety();

         int position = randomGenerator.nextInt(5);

         if(position == 1) {
            y += cityShiftAmount;
         }
         else if(position == 2) {
            x += cityShiftAmount;
         }
         else if(position == 3) {
            y -= cityShiftAmount;
         }
         else if(position == 4) {
            x -= cityShiftAmount;
         }

         newPositions[i] = new City(x, y);
      }

      return newPositions;
   }

   private static double[][] updataDistanceMatrix(double[][] distanceMatrix, City[] cities)
   {
       if(!(distanceMatrix.length == cities.length && distanceMatrix[0].length == cities.length))
       {
           throw new Error("Uncorrect dimensions for distance matrix.");
       }

       for (int i = 0;
            i < cities.length;
            i++)
       {
           for (int j = 0;
                j < cities.length;
                j++){
                distanceMatrix[i][j] = cities[i].proximity(cities[j]);
           }
       }
       return(distanceMatrix);
   }

   public static void main(String[] args) throws Exception {
      /*
          Enable assertions
       */
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      loader.setDefaultAssertionStatus(true);

      DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
      Date today = Calendar.getInstance().getTime();
      String currentTime  = df.format(today);

      int runs;
      boolean display = false;
      String formatMessage = "Usage: java TSP 1 [gui] \n java TSP [Runs] [gui]";

      if (args.length < 1) {
         System.out.println("Please enter the arguments");
         System.out.println(formatMessage);
         display = false;
      }
      else {

         if (args.length > 1) {
            display = true;
         }

         try {
            cityCount = 50;
            populationSize = 100;
            runs = Integer.parseInt(args[0]);

            if(display) {
               frame = new JFrame("Traveling Salesman");
               statsArea = new Panel();

               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.pack();
               frame.setSize(width + 300, height);
               frame.setResizable(false);
               frame.setLayout(new BorderLayout());

               statsText = new TextArea(35, 35);
               statsText.setEditable(false);

               statsArea.add(statsText);
               frame.add(statsArea, BorderLayout.EAST);

               frame.setVisible(true);
            }


            min = 0;
            avg = 0;
            max = 0;
            sum = 0;

            String currentWorkingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
            originalCities = cities = LoadCitiesFromFile(currentWorkingDirectory+"/"+"CityList.txt", cities);

            /* ADDED BY STNKAR012 */
            Chromosome.distanceMatrix = new double [cities.length][cities.length];
            Chromosome.distanceMatrix = updataDistanceMatrix(Chromosome.distanceMatrix, cities);
            String progressString; //log generation progress for all runs

            String results_karsten = "results_karsten" +  ".txt";//randomGenerator.nextInt() + ".txt";
            /* ADDED BY STNKAR012 */




            final long startTime = System.currentTimeMillis();
            writeLog("Run Stats for experiment at: " + currentTime);
            for (int y = 1; y <= runs; y++) {
               progressString = "";
               Evolution.mutationRate = Evolution.startMutationRate;
               genMin = 0;
               print(display,  "Run " + y + "\n");

            // create the initial population of chromosomes
               chromosomes = new Chromosome[populationSize];
               for (int x = 0; x < populationSize; x++) {
                  chromosomes[x] = Chromosome.RandomChromosome(cities);
               }

               Chromosome.sortChromosomes(chromosomes, populationSize);

               generation = 0;
               double thisCost = 0.0;

               while (generation < 100) {
                  evolve(generation);
                  if(generation % 5 == 0 )
                  {
                      cities = MoveCities(originalCities); //Move from original cities, so they only move by a maximum of one unit.
                      Chromosome.distanceMatrix = updataDistanceMatrix(Chromosome.distanceMatrix, cities);
                  }
                  generation++;

                  Chromosome.sortChromosomes(chromosomes, populationSize);
                  double cost = chromosomes[0].getCost();
                  thisCost = cost;

                  if (thisCost < genMin || genMin == 0) {
                     genMin = thisCost;
                  }

                  NumberFormat nf = NumberFormat.getInstance();
                  nf.setMinimumFractionDigits(2);
                  nf.setMinimumFractionDigits(2);

                  print(display, "Gen: " + generation + " Cost: " + (int) thisCost);

                  if(display) {
                     updateGUI();
                  }
				  if(generation < 100)
				  {
               	      progressString += thisCost + ", ";
				  }
				  else{
					  progressString += thisCost;
				  }
               }

               writeLog(genMin + "");

               logProgress(results_karsten, String.valueOf(genMin), y);
               logProgress("progress.out", progressString, y);

               if (genMin > max) {
                  max = genMin;
               }

               if (genMin < min || min == 0) {
                  min = genMin;
               }

               sum +=  genMin;

               print(display, "");
            }

            final long endTime = System.currentTimeMillis();
            avg = sum / runs;
            print(display, "Statistics after " + runs + " runs");
            print(display, "Solution found after " + generation + " generations." + "\n");
            print(display, "Statistics of minimum cost from each run \n");
            print(display, "Lowest: " + min + "\nAverage: " + avg + "\nHighest: " + max + "\n");

            print(display, "Time used for the " + runs + " runs is: " + round((endTime - startTime)/1000.0) + " seconds");


         }
         catch (NumberFormatException e) {
            System.out.println("Please ensure you enter integers for cities and population size");
            System.out.println(formatMessage);
         }
      }
   }
}
