/** Junshen (Kevin) Chen 
 *  cs8bwhat
 *  File Generator.java
 *  Contains program that constructs, trains, and uses a markov model 
 */

import java.lang.Object;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

/** class Generator: contains a main method that parses command line arguments,
 *  trains a Word/CharModel and generate sentences based on user specifications 
 */
public class Generator {

    static final int DEFAULT_DEGREE = 2; // default degree 2
    static final int MIN_ARG = 2; 
    static final int ARG_PAIR = 2;
    static final int DEFAULT_TOKEN_COUNT = 100;

    /** main
     *  parses command line arguments, uses a MarkovModel to generate sentences
     *  @param args     an array of String arguments
     *  @exception FileNotFoundException    thrown when input file not found
     *  @exception IOException thrown when error reading from file 
     */
    public static void main(String[] args) 
        throws FileNotFoundException, IOException{

        if (args.length < MIN_ARG)
            usage();

        String filename = args[0];

        boolean useWordModel = false;
        if (args[1].equals("w"))
        	useWordModel = true;
        else if (args[1].equals("c"))
        	useWordModel = false;
        else usage();

        boolean printFirst = false;
        if (args[2].equals("y"))
            printFirst = true;
        else if (args[2].equals("n"))
            printFirst = false;
        else usage();

        int tokenCount = DEFAULT_TOKEN_COUNT;
        int degree = DEFAULT_DEGREE;

        // parse args
        for (int i=3; i<args.length; i+=ARG_PAIR){
            if (args[i].equals("-d")) 
                degree = Integer.parseInt(args[i+1]);
            else if (args[i].equals("-n"))
                tokenCount = Integer.parseInt(args[i+1]);        
            else 
                usage();
        }

        if (degree >= tokenCount) {
            System.out.println("degree must be strictly less than count.");
            usage();
        }

        System.out.print("Constructing a Markov ");
        System.out.print(useWordModel? "word" : "character");
        System.out.println(" model of degree: " + degree);
        System.out.println();

        // construct new model object
        MarkovModel model = new MarkovModel(degree, useWordModel);

        // get all text from the file, train sentence by sentence
        if (filename == null) usage();
        System.out.println("Training from data: " + filename);


        model.trainFromText(filename);
  
        if (printFirst) {
            System.out.println("\nPrinting the model to console...\n");
            System.out.println(model);
        }

        // generate sentences
        System.out.println("\nGenerating text...\n");
		System.out.println(model.generate(tokenCount));
    }

    /** usages
     *  prints out the usage message, exit program
     */
    static void usage(){
        System.out.println("Generator");
        System.out.println("Usage: java Generator filename w|c [-d degree] [-n count]");
        System.out.println("   filename    ->  Required. Specify the training data file");
        System.out.println("     w|c       ->  Either w (word) or c (character) required. Specify which model to use");
        System.out.println("     y|n       ->  Whether to print the model before generating or not (yes / no)");
        System.out.println("  [-d degree]  ->  Specify the degree of the Markov Model. Default at 2, must < count");
        System.out.println("  [-n count]   ->  Specify how many words or chars to generate. Default at 100");
        System.exit(1);
    }

} // end of Generator.java