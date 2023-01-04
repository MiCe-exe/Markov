public class main {
    public static void main(String[] args) {

        Markov markov = new Markov();

        markov.addFromFile("lotr.txt");
        System.out.println(markov);

        for (int i = 0; i < 10; i ++){
            System.out.println(markov.getSentence());
        }

        System.out.println("====================================");

        markov = new Markov();

        markov.addFromFile("hamlet.txt");
        System.out.println(markov);

        for (int i = 0; i < 10; i ++){
            System.out.println(markov.getSentence());
        }


    }
}
