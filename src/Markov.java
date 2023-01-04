/**
 * Name: Michael Cervantes
 * Date: 12 November 2022
 * Explanation:
 */
import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Markov {
    private static final String PUNCTUATION = "__$";
    private static final String PUNCTUATION_MARKS = ".!?$";
    private HashMap<String, ArrayList<String>> words;
    private String prevWord;

    public Markov() {
        words = new HashMap<>();
        words.put(PUNCTUATION, new ArrayList<>());
        prevWord = PUNCTUATION;
    }

    HashMap<String, ArrayList<String>> getWords() {
        return this.words;
    }

    public void addFromFile(String fileName){
/*
        Others methods called: addLine(String)
        This method takes a String called filename that represents the file that will be parsed into the words HashMap.
        This method opens the file and calls the addLine method to parse the individual lines from the filename.
        This method should catch any errors that are generated from file operations.
*/
        Scanner scan = null;
        File f = new File(fileName);

        try{
            scan = new Scanner(f);
        }catch(FileNotFoundException e){
            System.out.println("Could not find the file " + e);
        }

        while(scan != null && scan.hasNext()){

            addLine(scan.nextLine());
        }

    }

    void addLine(String line){
/*
        -Other methods called: addWord(String)
        -This is a simple method that performs two very important operations.
        -First it ensures that the line being read (the passed in String parameter) is not 0 length.
            This is important because without this functionality an empty string (blank line) will break the program.

        -Once a String is determined to have content, the String is split into individual words. These words
        are then passed to the addWord(String) method.
*/
        if(line.length() != 0 || line != null && line != ""){
            line = line.replaceAll("\\s+", " ").replaceAll("\n", "").replaceAll("\t", "");
            line = line.trim();

            for(String word : line.split(" ")){
                addWord(word);
            }
        }
    }

    void addWord(String word){
/*
        Other methods called : endsWithPunctuation(String)

        This is the method that does most of the processing for this application.The first thing that
        is done is the previous word is checked to see if it ends with punctuation. If the previous word
        ends with punctuation then the current word is added under the PUNCTUATION key in the words HashMap.

        If the previous word did NOT end with punctuation then the HashMap words is checked to see if the
        previous word has an entry as a key in the words HashMap. If the previous word is not present as a
        key in the HashMap, it will need to be added as a key.

        If the previous word is present as a key in the HashMap, the current word is added to the ArrayList
        that uses the previous word as a key.

        Note: We will use a method called endsWithPunctuation(String) to check for punctuation. The reason
        for this is to make debugging easier and because it is used in the next method.
        */

        //checks if its beginning of the sentence.
        if(endsWithPunctuation(prevWord)){
            words.get(PUNCTUATION).add(word);
        } else {
            if(words.containsKey(prevWord)){
                words.get(prevWord).add(word);
            }else{
                words.put(prevWord, new ArrayList<>());
                words.get(prevWord).add(word);
            }
        }

        prevWord = word;
    }

    public String getSentence(){
/*

        Other methods called: randomWord(String), endsWithPunctuation(String)
        This method is responsible for building a sentence from the contents of the HashMap words.
        It will make use of the method randomWord(String) described below.

        This method first pics a random word from the values stored under the key PUNCTUATION.
        This word becomes the current word.

        If the current word does not end in punctuation, it is added to the sentence being constructed
        along with a space and a new random word is selected.
        If the current word DOES end in punctuation, it is added to the String being constructed and
        no additional word is chosen.

        The String being built is then returned.
*/

        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        String currentWord ="";
        int len = words.get(PUNCTUATION).size() - 1;

        // checking if len is out of bounds.
        if(len <= 0)
        {
            prevWord = words.get(PUNCTUATION).get(0);
        }else{
            prevWord = words.get(PUNCTUATION).get(rnd.nextInt(len));
        }

        sb.append(prevWord + " ");

        while(endsWithPunctuation(prevWord) == false){
            currentWord = randomWord(prevWord);

            if(!endsWithPunctuation(currentWord)){
                sb.append(currentWord + " ");
            }
            prevWord = currentWord;
        }

        // Handle the last word the punctuation word;
        sb.append(prevWord);

        return sb.toString();
    }

    String randomWord(String wordKey){
/*
        This method takes a word as a parameter. That word is used as a key to retrieve an
        ArrayList of words from the HashMap words.

        A random word is chosen from the ArrayList and returned.
*/

        Random rnd = new Random();
        ArrayList<String> wordList = new ArrayList<>();
        wordList = words.get(wordKey);

        return wordList.get(rnd.nextInt(wordList.size()));
    }

    public static boolean endsWithPunctuation(String str){

        for(int i = 0; i < PUNCTUATION_MARKS.length() - 1; i++){
            if(PUNCTUATION_MARKS.charAt(i) == str.charAt(str.length() - 1)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return words.toString();
    }
}
