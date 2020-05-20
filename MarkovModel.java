/**
 * Author: Vicki Chen
 * CS8B Login: cs8bwamh
 * Date: 3/12/19
 * File: MarkovModel.java
 * Source of Help: PA7 write up, Piazza, CSE8B tutor
 *
 * This file contains the class MarkovModel.
 * It takes in texts and predicts the following texts after
 * */

import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;

/*
 * This class contains methods that train the model to know what are the
 * possible prediction after words, characters, or phrases, and generate
 * a texts based on the predictions it stores.
 * */
public class MarkovModel {

    protected HashMap<String, WordCountList> predictionMap;

    protected int degree;
    protected Random random;
    protected boolean isWordModel; 
    protected final static char DELIMITER = '\u0000';	
    /* comment ALL methods in this file */
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STR = "";
    private static final String SPACE_STR = " ";
    private static final String COLON_STR = ":";

    /**
     * Initializes a Markov object with its degree, type of model, random
     * generator, and prediction hashmap
     * @param degree Degree of model
     * @param isWordModel Type of model
     * @return            Markov object created
     * */
    public MarkovModel (int degree, boolean isWordModel) {
        this.degree = degree;
        this.isWordModel = isWordModel;
        this.random = new Random();
        this.predictionMap = new HashMap<String, WordCountList>();
    }

    /**
     * Takes in a text file and populate predictionMap with the predictions
     * matching with the prefix
     * @param filename Name of file
     * @return void
     * */
    public void trainFromText(String filename) {

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // `content` contains everything from the file, in one single string

        // TODO Wrap the training text and train the word or char model.
        
        if (isWordModel == true)
        {
            content = content.toLowerCase().replaceAll(NEW_LINE, EMPTY_STR);
            Scanner wordContent = new Scanner(content); 
            
            //based on the degree add that many words at the end of the text
            //to wrap text
            for (int i = 0; i < degree; i++)
            {
                if (wordContent.hasNext())
                {
                    content = content + SPACE_STR + wordContent.next();
                }
            }

            //call helper method to store predictions of text
            trainWordModel(content);
        }

        if (isWordModel == false)
        {
            content = content.toLowerCase();
            ArrayList<String> charList = new ArrayList<String>();

            //based on the degree add that many charactrs at the end of the
            //text to wrap text and add to a list
            for (int i = 0; i < degree; i++)
            {
                charList.add(Character.toString(content.charAt(i)));
            }

            //iterate through list to concat the wrapped-text to one string
            for (int j = 0; j < charList.size(); j++)
            {
                content = content + charList.get(j);
            }

            //call helper method to store predictions of text            
            trainCharacterModel(content);
        }
                        
    }

    /**
     * Helper method to train word model to add prefix and store all 
     * predictions after prefixes or increment prediction occurences
     * @param content 
     * @return void
     * */
    private void trainWordModel(String content)
    {
        content = content.toLowerCase();
        Scanner scanContent = new Scanner(content);
        
        ArrayList<String> wordContent = new ArrayList<String>();

        //put each word into an arraylist (content has spaces so scanner
        //can seperate it)
        while (scanContent.hasNext())
        {
            wordContent.add(scanContent.next());
        }

        //iterate through text to put key and value pairs into hashmap
        for (int i = 0; i < wordContent.size()-degree; i++)
        {
            String prefix = new String();

            //iterate to get prefix based on degree
            for (int j = 0; j < degree; j++)
            {
                prefix = prefix + wordContent.get(j+i) + DELIMITER;
            }

            //if prediction not in hashmap, add it and its value
            if (!(predictionMap.containsKey(prefix)))
            {
                WordCountList wordList = new WordCountList();
                predictionMap.put(prefix, wordList);
                wordList.add(wordContent.get(i+degree));
            }
            //increment value occurences of corresponding key in hashmap
            else
            {
                predictionMap.get(prefix).add(wordContent.get(i+degree));
            }
        }                  
     }

     /**
     * Helper method to train character model to add prefix and store all 
     * predictions after prefixes or increment prediction occurences
     * @param content 
     * @return void
     * */    
    private void trainCharacterModel(String content)
    {
        content = content.toLowerCase();
        ArrayList<String> charContent = new ArrayList<String>();

        //put each character of text into an arraylist
        for (int i = 0; i < content.length(); i++)
        {
            charContent.add(Character.toString(content.charAt(i)));
        }

        //iterate through text to put key and value pairs into hashmap       
        for (int j = 0; j < charContent.size()-degree; j++)
        {
            String prefix = new String();

            //iterate to get prefix based on degree           
            for (int k = 0; k < degree; k++)
            {
                prefix = prefix + charContent.get(k+j) + DELIMITER;
            }
            //if prediction not in hashmap, add it and its value            
            if (!(predictionMap.containsKey(prefix)))
            {
                WordCountList charList = new WordCountList();
                predictionMap.put(prefix, charList);
                charList.add(charContent.get(j+degree));
            }
            //increment value occurences of corresponding key in hashmap            
            else
            {
                predictionMap.get(prefix).add(charContent.get(j+degree));
            }
        }
    }

    /**
     * Put predictions of a prefix into a list
     * @param prefix The prefix key
     * @return       ArrayList of predictions
     * */
    public ArrayList<String> getFlattenedList(String prefix){
        // TODO Create a "flattened list" of predictions
        
        ArrayList<String> flattenedList = new ArrayList<String>();

        if (!(predictionMap.containsKey(prefix)))
        {
            return flattenedList;
        }
        else
        {
            ArrayList<WordCount> list = predictionMap.get(prefix).getList();
            
            //iterate through the key's value objects and add the value
            //objects based on the number of counts
            for (int i = 0; i < list.size(); i++)
            {
                for (int j = 0; j < list.get(i).getCount(); j++)
                {
                    flattenedList.add(list.get(i).getWord());
                }
            }   
            return flattenedList;
        }      
    }

    /**
     * Generate a possible prediction of a prefix
     * @param prefix 
     * @return prediction
     * */
    public String generateNext(String prefix) {
        // TODO
        
        ArrayList<String> flatList = getFlattenedList(prefix);

        //get random object as the prediction for the prefix from
        //flattenedList
        int randIndex = random.nextInt(flatList.size());
        String randPredict = flatList.get(randIndex);
        return randPredict;
    }

    /**
     * Generate text with number of word/character based on the count number
     * and extract predictions and append to create text.
     * @param count
     * @return Generated text
     * */
    public String generate(int count) {
        // TODO
        ArrayList<String> keys = new ArrayList<String>(predictionMap.keySet());
        
        int randIndex = random.nextInt(keys.size());
        String randKey = keys.get(randIndex);

        ArrayList<String> textWords = new ArrayList<String>();

        //first prefix and first prediction
        randKey = randKey + generateNext(randKey);

        int start = 0;
        //split words/characters from delimiters and add cut words/characters
        //or phrases into a list
        for (int i = 0; i < randKey.length(); i++)
        {
            if (randKey.charAt(i) == DELIMITER)
            {
               String randKeyCut = randKey.substring(start, i);
               start = i+1;
               textWords.add(randKeyCut);
            }
        }
        //add the last part into list
        textWords.add(randKey.substring(start, randKey.length()));

        //iterate to shift through to create new prefixes and generate
        //a random predicition from each of those prefixes
        for (int j = 0; j < count-degree-1; j++)
        {
            String newPrefix = new String();
            for (int k = textWords.size()-degree; k < textWords.size(); k++)
            {
                newPrefix = newPrefix + textWords.get(k) + DELIMITER;
            }
            textWords.add(generateNext(newPrefix));
        }

        String text = new String();

        //iterate to concat all prefix and predictions of arraylist
        //to one string
        for (int k = 0; k < textWords.size(); k++)
        {
            text = text + textWords.get(k);

            //add spaces between words
            if (isWordModel == true)
            {
                text = text + SPACE_STR;
            }
        }

        //get rid of last space in wordmodel
        if (isWordModel == true)
        {
            text = text.substring(0, text.length()-1);
        }

        return text;
    }

    /**
     * String represetation the statistics of words/word occurences of a
     * prefix
     * @param none
     * @return String representation of the stats of a prefix
     * */
    @Override 
    public String toString(){
        // TODO

        String newKey = new String();

        //iterate through all keys to format the occurrences
        for (String key: predictionMap.keySet())            
        {
            String prefixKey = key.replaceAll(Character.toString(DELIMITER), 
                    SPACE_STR);
            newKey = newKey + prefixKey + COLON_STR + SPACE_STR +  
                predictionMap.get(key) + NEW_LINE;
        }
        
        return newKey;
    }

    /**
    public static void main(String[] args)
    {
        MarkovModel m = new MarkovModel (2, true);
        m.trainFromText("paul.txt");
        System.out.println(m.toString());
        System.out.println(m.getFlattenedList("h" + DELIMITER + "e"
        + DELIMITER));
        System.out.println(m.generateNext("hello"+DELIMITER));
        System.out.println(m.generate(5));
    }
    **/

}
