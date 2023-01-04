/**
 * Name: DR. C
 * Date: 09 November 2022
 * Explanation: Test file to test Markov.java
 */
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MarkovTest {

    /**
     * Setting up values to be used in the upcoming tests.
     */

    Markov markov = null;

    static final String HELLO_THERE = "helloThere.txt";
    private static final String TEST_SENTENCE = "Hello there.";
    static final String EMPTY_HASHMAP = "{__$=[]}";
    static final String PUNCTUATION = "__$";
    static final String PUNCTUATION_MARKS = ".!?";

    HashMap<String, ArrayList<String>> hashMapTester = null;


    /**
     * This is WAY overkill, but it ensures that the file we need for testing both
     * exists AND contains the values we expect.
     * We use BeforeAll so it will run only once.
     */
    @BeforeAll
    static void makeTheFile(){
        System.out.println("Making the file...");
        File f = new File(HELLO_THERE);
        BufferedWriter writer;
        try{
            f.createNewFile();
            writer = new BufferedWriter(new FileWriter(HELLO_THERE));
            writer.write(TEST_SENTENCE);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void removeFile(){
        File f = new File(HELLO_THERE);
        f.delete();
    }

    @BeforeEach
    void setUp() {
        markov = new Markov();
    }

    @AfterEach
    void tearDown() {
        markov = null;
        hashMapTester = null;
    }

    @Test
    void constructor_test() {
        //We could make this more robust but as long as markov isn't null, given our preconditions, we should be okay.
        assertNotNull(markov);
    }

    /**
     * Since getWords is one of the chunkiest piece of code, it has a lot of tests.
     */
    @Test
    void getWords() {
        markov.addFromFile(HELLO_THERE);
        hashMapTester = markov.getWords();
        System.out.println("Testing getWords");
        System.out.print("\tSize check: Expected == ");
        System.out.println(hashMapTester.size());
        assertEquals(hashMapTester.size(), markov.getWords().size());

        System.out.print("\tcorrect structure: ");
        System.out.println(hashMapTester);
        assertTrue(hashMapTester.containsKey("Hello"));
        assertTrue(hashMapTester.containsKey(PUNCTUATION));
        assertEquals("there.", hashMapTester.get("Hello").get(0));
    }

    @Test
    void getSentence() {
        markov.addFromFile(HELLO_THERE);
        assertEquals(TEST_SENTENCE, markov.getSentence());
    }

    /**
     * This is reasonable.  The better way to do it would be to CREATE a file at the begining...
     */
    @Test
    void addFromFile() {
        hashMapTester = markov.getWords();
        System.out.println(hashMapTester);
        //this tells us the HashMap was initialized correctly.
        assertEquals(EMPTY_HASHMAP, hashMapTester.toString());
        //this is another check to ensure the HashMap was initialized correctly
        assertTrue(hashMapTester.containsKey(PUNCTUATION));
        //Final check for correct initialization.  If the hashmap has an arraylist
        //and the arraylist is of 0 length, we are good to go.
        assertTrue(hashMapTester.get(PUNCTUATION) instanceof ArrayList);
        assertEquals(0, hashMapTester.get(PUNCTUATION).size());
    }

    /**
     * Like toString this one really requires us to force a given set of values.
     * If we have a set of known knowns, we can easily test though.
     */
    @Test
    void randomWord() {
        markov.addFromFile(HELLO_THERE);
        assertEquals("Hello", markov.randomWord(PUNCTUATION));
        assertEquals("there.", markov.randomWord("Hello"));
    }


    /**
     * This is the easiest test.  The one change I would make is to make PUNCTUATION_MARKS public in Markov,
     * so we can access that instead of having to recreate it here.
     */
    @Test
    void endsWithPunctuation() {

        String testWord = "there";
        System.out.println("endsWithPunctuation test");
        System.out.println("\ttesting: " + testWord);
        assertFalse(Markov.endsWithPunctuation(testWord));

        for (char mark : PUNCTUATION_MARKS.toCharArray()) {
            System.out.println("\ttesting: " + testWord + mark);
            assertTrue(Markov.endsWithPunctuation(testWord + mark));
        }
    }

    /**
     * To string test is challenging.  The only way I can think to make it work is as below.
     * Essentially FORCE a certain sentence.
     */
    @Test
    void toStringTest() {
        assertEquals(EMPTY_HASHMAP, markov.toString());
        markov.addFromFile(HELLO_THERE);
//        for(int i = 0; i < 1000; i++) {
//        I ran this 1000 times and it always worked so I assume the HashMap will always look the same...
        System.out.println(markov);
        assertEquals("{Hello=[there.], __$=[Hello]}", markov.toString());
//        }
    }

    /**
     * This is the chunkiest piece of code to write. So we are checking for a number of things
     * Can we add a word under an existing value?
     * Can we add a word under A NEW value?
     * Does the underpinning elements work and reset back to PUNCTUATION when we hit a word with punctuation?
     *
     * If we meet all these conditions then our test is solid.
     */
    @Test
    void addWordTest(){
        String hello = "Hello";
        String there = "there.";
        String A = "A";
        markov.addWord(hello);
        //can we add a word under an existing value?
        assertEquals(hello, markov.getWords().get(PUNCTUATION).get(0));
        markov.addWord(there);
        //can we add a word under a value that does not exists?
        assertEquals(there, markov.getWords().get(hello).get(0));
        markov.addWord(A);
        //does it correctly reset to PUNCTUATION?
        assertTrue(markov.getWords().get(PUNCTUATION).contains(A));
    }

    /**
     * a bad line would be one that has extraneous white space.
     * If the following test works. then a good sentence should work too.
     */
    @Test
    void addBadLineTest(){
        String badLine = "  \t\n Hello   \n \t  there. \n\t\n ";
        System.out.println("Trying " + badLine);
        markov.addLine(badLine);
        System.out.println(markov.getSentence());
        assertEquals(TEST_SENTENCE, markov.getSentence());
    }

    /**
     * A Long line may also cause errors but probably won't if badLine passes we should be good.
     */
    @Test
    void addLongLineTest(){
        //I am making sure we get this same sentence out by capitalizing the second Of. A hack to be sure but a welcome one.
        String longLine = "Now is the winter of our discontent; made glorious summer by these sons Of York.";
        System.out.println("Trying: " + longLine);
        markov.addLine(longLine);
        System.out.println(markov.getSentence());
        System.out.println();
        assertEquals(longLine, markov.getSentence());
    }
}