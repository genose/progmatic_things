
public class Main {
    public static void main(String[] args) {

        float money = 12.4f;
        float price = 1.2f;
        int candies = 0;

        if( ( money > 0) && (price > 0)){

            while ((money - price) >= 0) {
                candies = candies + 1;
                money = money - price;
            }
        }
        System.out.println( candies );

        System.out.println("**********");
        Duck donald = new Duck("Donald", 84);

        System.out.println( ( donald.getClass().getSimpleName()  ) + ": "+donald.getName());
        System.out.println("**********");

        Nature.main(args);
        System.out.println("**********");
        System.out.println(Decipherer.DeciphererMessage(Decipherer.message1));
        System.out.println(Decipherer.DeciphererMessage(Decipherer.message2));
        System.out.println(Decipherer.DeciphererMessage(Decipherer.message3));

    }
}