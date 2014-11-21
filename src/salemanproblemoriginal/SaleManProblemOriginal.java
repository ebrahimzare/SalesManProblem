/*
 */
package salemanproblemoriginal;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;     
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaleManProblemOriginal {
   
  
   static int[][]  weightMatrix;  //matrix of edge weights               
   static String[] city;  //array of city names              
   static int      n; //dimention for wt and city               
  
   static ArrayList<Tour> ToursArrayList = new ArrayList<Tour>();  //array of tours in a list
   //bestTour which is initilalized in initMatrix() function
   static int      bestTour;           
   static int      upperBound;     
   // Show accept/reject decisions
   static boolean  AcceptDecision   =  true; 
   // Show all tours discovered
   static boolean  VERBOSE =  true;    
   private static int[] vect;

   private static class Tour implements Comparable
   {  
      int[] ToursArrayList;
      int   index;      // In branch-and-bound, start of variable
      int   dist;
      static int nTours = 0;
      // Best-first based on dist, or DFS based on maxheap of index
      static boolean DFS =  true;

      /* Presumable edges up to [index-1] have been verified before
       * this constructor has been called.  So compute the fixed
       * distance from [0] up to [index-1] as dist.
       */
      private Tour(int[] vect, int index, int[][] weightMatrix)
      {  dist = 0;
         // Add edges   
         for (int k = 1; k < index; k++)         
            dist += weightMatrix[vect[k-1]][vect[k]];
         // Return edge
         if ( index == n )
            dist += weightMatrix[vect[n-1]][vect[0]];     
         ToursArrayList = new int[n];
         //copy the tour into ToursArrayList 
         System.arraycopy(vect, 0, ToursArrayList, 0, n);
         // Index to permute
         this.index = index;
         // Count # of tours
         nTours++;                                  
      }
      //compare the tour object with the current tour object
      public int compareTo ( Object o )
      {  Tour rt = (Tour)o;
         int  c1 = rt.index - this.index,
              c2 = this.dist - rt.dist;
         if ( DFS )
            return c1 == 0 ? c2 : c1;
         else
            return c2;
      }

      //show the current state in string format
      public String toString()
      { 
         StringBuilder val = new StringBuilder( city[ToursArrayList[0]] );
         for ( int k = 1; k < n; k++ )
            val.append(", " + city[ToursArrayList[k]]);
         val.append(", " + city[ToursArrayList[0]]);
         val.append( String.format(" for %d", dist) );
         return val.toString();
      }
   }

   //initialize the matrix and citu and n variable based on the matrixFile
   //matrixFile is a file created for each instance to hold the distances between the citied and the number of cities
   private static void initMatrix(Scanner MatrixFile)
   {  //int i,
      //    j;
      //each line in the file 
      String line;
      //get the number from the first line which is the number of vertix
      n = MatrixFile.nextInt();
      //define a two dimentional array to store the weight between cities
      weightMatrix = new int[n][n];
      //define an array of city to keep the  name of the cities
      city = new String[n];
      // initialize the matrix weight with -1
      for ( int i = 0; i < n; i++ )
         Arrays.fill(weightMatrix[i], -1);
      
      //go to the next line
      MatrixFile.nextLine(); 
      //read the name of the cities from the file and add them to the array city
      for (int i = 0; i < n; i++ )
         city[i] = MatrixFile.nextLine();
      //sort the city array for binarySearch
      Arrays.sort(city);     
      //skip the blank spacing line
      MatrixFile.nextLine();  
      
      //sum all weights for upper bound
      upperBound = 0;     
      
      //up to the end of the file go through the lines and get the weights for each edge
      while ( MatrixFile.hasNext() )
      {  
         int    head, tail;
         int    distance;
         String srcCity, dstCity;
         //get the line for example "a" "b" 8
         line = MatrixFile.nextLine();   
         //remove the double quotes to get the name of the source city
         head = line.indexOf('"') + 1;
         tail = line.indexOf('"', head);
         //set the source city
         srcCity = line.substring(head, tail);
         
         //remove the double quotes to get the name of the destination city 
         head = line.indexOf('"', tail+1) + 1;
         tail = line.indexOf('"', head);
         //set the destination city
         dstCity = line.substring(head, tail);
         
         //get the integer at the end of the line which is the distance between two city
         distance = Integer.parseInt( line.substring(tail+1).trim() );
         int i = Arrays.binarySearch(city, srcCity);
         int j = Arrays.binarySearch(city, dstCity);
         //set the matrix weight based on the i,j(position of the city in the matrix) and the given distance
         weightMatrix[i][j] = weightMatrix[i][j] = distance;
         //sum all distances to set the upperBound
         upperBound += distance;
      }
      //double the total 
      upperBound += upperBound;    
      //initialize bestTour
      bestTour = upperBound;    
   }
   //function that gets the Searchsrc and Searchdst as parametres and returns the weight between those two city 
    public static int getWeight(String Searchsrc, String Searchdst)
    {
       //matrix data for the naive algorithme reside in NaiveTest.txt file 
       String NaiveMatrixFile = "NaiveTest.txt";
       //define the srcIndex,desIndex variables
       int srcIndex,desIndex;
       //initialize the dist variable to zero
       int dist = 0;
       //define line variable as string 
       String line;
        try {
            //read the NaiveTest.txt file
            Scanner filecontent = new Scanner ( new java.io.File(NaiveMatrixFile) );
            //get the first line which is the number of vertix(cities)
            n = filecontent.nextInt();
            //define the weightMatrix 
            weightMatrix = new int[n][n];
            //define an array of city to keep the  name of the cities
             city = new String[n];
             //initialize weightmatrix element with -1
             for ( int i = 0; i < n; i++ )
                 Arrays.fill(weightMatrix[i], -1);
            //go to the next line 
            filecontent.nextLine();  
            //add the name of the cities in an array(city)
            for ( int i = 0; i < n; i++ )
                city[i] = filecontent.nextLine();
            //sort the city array for binarySearch
             Arrays.sort(city);     
            //discard the next line
            filecontent.nextLine();  
            
            //up to the end of the file
            while (filecontent.hasNext() )
            {  
               int    head, tail;
               //src and dst variables 
               String src, dst;
               //get the line for example : "a" "b" 1 , which a is the source, b is the destination and the 1 is the distance
               line = filecontent.nextLine(); 
               //remove the double quotes
               head = line.indexOf('"') + 1;
               tail = line.indexOf('"', head);
               //exctract the source city
               src = line.substring(head, tail);
               
               //exctract the destination city
               head = line.indexOf('"', tail+1) + 1;
               tail = line.indexOf('"', head);
               dst = line.substring(head, tail);
               
               //if the source and destination city is equal to the Searchsrc and Searchdst(function parametres) 
               if(dst.equals(Searchdst) && src.equals(Searchsrc))
               {
                  dist = Integer.parseInt( line.substring(tail+1).trim() );
                  srcIndex = Arrays.binarySearch(city, src);
                  desIndex = Arrays.binarySearch(city, dst);
                  //get the weight for the given source , destination
                  weightMatrix[srcIndex][desIndex] = weightMatrix[desIndex][srcIndex] = dist;
               }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaleManProblemOriginal.class.getName()).log(Level.SEVERE, null, ex);
        }
     // return the destination as int
     return dist;
}
//function that gets the permutation array as parametre and returns sumweight for that permutation
public static int sumweight(String[] permutation)
{
    int k=0;
    int sum=0;
    //iterate through the permutation array
      for (int count = k; count < permutation.length-1; count++) {
          //get the weight between each two city, first iteration gives permutation[0],permutation[1],
          //second iteration return the weight for permutation[1],permutation[2] and so on....
          int weight = getWeight(permutation[k],permutation[k+1]); 
           sum=sum+weight;
           //add one to k to pass to the next city in permutation
            k=k+1;
      } 
      //calculate weight between the last city and the first city
      int circle=permutation.length-1;
      int weight = getWeight(permutation[circle],permutation[0]);
      //add all the weight to get the total weight for permutation array(given as parameter)
      sum=sum+weight;
      //return the sum of weights
    return sum;
}
   //naive algorithme implemented for five cities(abcde) permuation  
   public static void naiveAlgorithme() {
       //call the permuation function(in permutation class) to generate all permuation for "abcde"
       PermutationGenerator generator = new PermutationGenerator("abcde");
       //put all the generated permuatios into an ArrayList
       ArrayList<String> permutations = generator.getPermutations();
       //define an Arraylist of string
       ArrayList<String[]> PermutationStringList = new ArrayList<String[]>();
       //iterate through all permutations
        for (String s : permutations) {
            //create an array of string
            String[] permutation = new String[5];
            //for each permutation
            for (int i = 0; i < s.length(); i++){
                //read the permutation char by char
                 char c = s.charAt(i); 
                 //convert the char to string
                 String ch=Character.toString(c);
                 //add the string(each town) to the permutation array
                 permutation[i]=ch;
            }
            //print each permutation in console
             System.out.println(s);
             //add each permutation to the PermutationStringList
             PermutationStringList.add(permutation);
        }
        //define an iterator
        Iterator<String[]> eachpermutation = PermutationStringList.iterator();
          int permutationcount=0;
          //assigne the max value of java to the bestTour
          int bestTour=Integer.MAX_VALUE;
         //while the iterator has permutation
        while (eachpermutation.hasNext()) {
            //create an array of string and fill it with each permutation
            String[] cities = eachpermutation.next();
            //print the cities in the permutation 
            for(int i=0;i<cities.length;i++)
            {
                System.out.print(cities[i]+" ");
            }
            //call the sumweight function and pass the cities array to calculate the total weight
            System.out.print("sum weight for the Tour : "  +sumweight(cities) + " ");
            //if the current sumweight is smaller than the bestTour
            if(sumweight(cities)<bestTour)
                //set the current sumweight as the bestTour
                bestTour=sumweight(cities);
            
             System.out.print("\n");
             //count the number of permutation
             permutationcount=permutationcount+1;
             }
          //print the count of permutation    
          System.out.print("number of permutation : "+permutationcount);
          //go to the next line
          System.out.print("\n");
          //print the bestTour
          System.out.print("best tour : "+bestTour);
          System.out.print("\n");
}
   
   // Used below in generating permutations.
   private static void swap ( int[] x, int p, int q )
   {  int tmp = x[p];  x[p] = x[q]; x[q] = tmp;  }

   //the function to get the best tour
   public static void tour() 
 { 
    //define an array of integer 
    int[] vect = new int[n];
    int start; 
    //define a PriorityQueue of type Tour
    Queue<Tour> QueueOfTours = new PriorityQueue<Tour>();
    // fill the first permutation vector as vect[0,1,2,3,4,...n] 
    for ( int k = 0; k < n; k++ ) 
    vect[k] = k; 
    //start from city "a"
    start = Arrays.binarySearch(city, "a");
    //if the city a 
    if ( start >= 0 ) 
    { 
        vect[start] = 0;
        vect[0] = start;
    } 
    // Consequently, we start the permutations at [1], NOT [0] and add it to the QueueOfTours
    QueueOfTours.add( new Tour(vect, 1, weightMatrix) ); 
    //while the queue is not empty and the still have tours 
    while ( ! QueueOfTours.isEmpty() ) // Branch-and-bound loop 
    {   
        //get the head from the queue
        Tour current = QueueOfTours.poll(); 
        //get the index of the tour
        int index = current.index; 
        //assign the tour array(the current tour) to the vect array
        vect = current.ToursArrayList; 
        //if it is the full permutation vector means that all cities been passed
        if ( index == n ) 
        {
            // if there is a return edge (means the weight from the last city to the first city is greate than zero,
            //and total distance of this permutation is less than the bestTour )  
            if ( weightMatrix[vect[n-1]][vect[0]] > 0 && current.dist < bestTour ) // Better than earlier? 
            {
                //Save the state in the list ,replace the best tour current tour distance
                bestTour = current.dist; 
                //add the current tour again to the list
                ToursArrayList.add(current); 
                //accept the current as the bestTour
                if ( AcceptDecision ) 
                System.out.println("Accept " + current); 
            } 
            //reject the current tour
            else if (AcceptDecision) 
            { 
                System.out.println("Reject " + current);
            } 
        } 
        //if the permutation is not complete , continue generating permutations
        else 
        { 
            int k; // Loop variable 
            int hold; // Used in regenerating the original state 
            for ( k = index; k < n; k++ ) 
            { 
                swap ( vect, index, k ); 
                if ( weightMatrix[vect[index-1]][vect[index]] < 0 ) 
                continue; 
                QueueOfTours.add ( new Tour(vect, index+1, weightMatrix) ); 
            } 
            // Restore original permutation 
            hold = vect[index]; 
            for ( k = index+1; k < n; k++ ) 
            vect[k-1] = vect[k]; 
            vect[n-1] = hold; 
        } 
    } 
 }
   public static void CreateInstanceFiles() throws IOException
   {    
       
       for(int num = 5; num < 20; num = num+5) {
           
           System.out.print("number of vertex : " + num );
           System.out.print("\n");
          //for each multiple create 10 instances 
          for(int number = 0; number < 10; number = number+1)
         { 
            //create a file name for each distance and concatinate with number_num(like ) 
            String fileName;
            fileName = "VertexSet" + number + num + ".txt";
            //create a text file with the given name
            FileWriter fw = new FileWriter(fileName);
            //to name the cities , create an array of alphabet
            char[] ch = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
            //write the num into the file
            fw.write(""+num);
            //go to the nextline in the file
            fw.write("\r\n");
            //based on the given num , name the country from "a" to ....
            for (int i = 0; i < num; i++)
             {
                 //write the name of the country into the file
                 fw.write(""+ch[i]+"");
                 //go to the next line
                 fw.write("\r\n");
             }
            //create an empty line
            fw.write("\r\n");
            //initialize the Random function
            Random rand = new Random();
            //set zero in the matrix for distance from a country to itself for example  a->a should be set to zero
            int zero=0;
            int k=0;
            //for the number of the vertex
           for (int i = 0; i < num; i++)
             {
                //from each vertex to other vertexes 
                for (int j = k; j < num; j++)
                    {
                        //for the distance from the vertex to itself set zero
                        if(i==j)
                        {
                            //write into the file for example "a" "a" 0
                            fw.write("\""+ch[i]+"\" \""+ch[j]+"\" "+zero+"");
                            //go to the next line in the file
                            fw.write("\r\n");
                        }
                        else
                        {
                           //create a random number between 1 to 100 
                           int randomNumber =rand.nextInt(9)+1; 
                           //set the random weight from source city to destination city
                           fw.write("\""+ch[i]+"\" \""+ch[j]+"\" "+randomNumber+"");
                           //go to the next line
                           fw.write("\r\n");
                           //set the same random weight from destination city to source city as it is a indirected graph
                           fw.write("\""+ch[j]+"\" \""+ch[i]+"\" "+randomNumber+"");
                           //go to the next line
                           fw.write("\r\n");
                        }

             }  
                //add one to the k to go to the next vertex(city)
                 k=k+1; 
           } 
           //flush the file
           fw.flush();
           //close the created file
           fw.close();
         }
        }
   }
   public static void FindBestTourForInstances(int multiply) throws FileNotFoundException, IOException
   {    
       
        long startTime;
        long stopTime;
        long elapsedTime;
        //number of vertex
        int num=multiply;
        //go through the 10 instances file to get the best tour and calculate the executation times
          for(int number = 0; number < 10; number = number+1)
         {
           //read the file that already created as instance   
           String fileName = "VertexSet" + number + num + ".txt";
           System.out.println(new java.io.File("").getAbsolutePath());
           String filename1 =fileName ;
           Scanner MatrixFile = new Scanner ( new java.io.File(filename1) );
           
           System.out.println("Data read from file " + filename1);
           //sent the file to  the initMatrix function to initialize the matrix 
           initMatrix(MatrixFile);
           //create a file to save the result of each instance 
            FileWriter fstream = new FileWriter("Result.txt",true);
            BufferedWriter ResultFile = new BufferedWriter(fstream);
            //calculate the execution time
            startTime = System.currentTimeMillis();
                //call the tour function
                tour();
            //stop time of execution    
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            //write the execution time in the resultfile
            ResultFile.write("execution time millisec: "+elapsedTime );
            
            ResultFile.newLine();
            //print all the discovered tours 
           if (VERBOSE)
            {  System.out.println ("Tours discovered:");
               for ( Tour opt : ToursArrayList )
                  System.out.println(opt);
            }
           //if there is no tour
            if ( ToursArrayList.size() == 0 )
            {  System.out.println("NO tours discovered. Exiting.");
               System.exit(0);
            }
            //number of generated tours
            System.out.println (Tour.nTours + " Tour objects generated.");
            Collections.sort(ToursArrayList);
            System.out.println("Best tour:  ");
            System.out.println(ToursArrayList.get(0));
            
            ResultFile.write("instance: "+(number+1) );
            ResultFile.newLine();
            ResultFile.write("nombre de sommets: "+num);
            ResultFile.newLine();
            ResultFile.write("Best tour :"+ ToursArrayList.get(0));
            ResultFile.newLine();
            ResultFile.write(fileName);
            Tour.nTours=0;
            ResultFile.newLine();
            ResultFile.newLine();
            ResultFile.flush();
            ResultFile.close();
     }
   
   }
   public static void main (String[] args) throws Exception
   {    
       //execution of the naive algorithme with creating all the permutation for abcde based on the matrix defind in NaiveTest.txt
       naiveAlgorithme();   
       
       CreateInstanceFiles();
       
       FindBestTourForInstances(5);
       
     //FindBestTourForInstances(10);
       
      // FindBestTourForInstances(15);
   }
    
}

//permutation class generator
class PermutationGenerator {
    private static int[] vect;

    private String string;
    private ArrayList<String> permutations;
    
    public PermutationGenerator(String string) {
        this.string = string;
        this.permutations = new ArrayList<String>();
    }

    public ArrayList<String> getPermutations() { 
        permutation("", this.string);
        return this.permutations;
    }

    private void permutation(String prefix, String str) {
      int n = str.length();
        if (n == 0) {
            this.permutations.add(prefix);
        } else {
          for (int i = 0; i < n; i++) {
            permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n));
          }
        }
    }   
  
}

