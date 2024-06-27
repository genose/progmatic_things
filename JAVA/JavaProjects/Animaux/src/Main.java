import fr.wildskool.animal.Animal;
import fr.wildskool.animal.Bird;
import fr.wildskool.animal.Parrot;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        Animal unOiseau = new Parrot("Oiseau", 2);
        System.out.println(" ********** ");
        unOiseau.introduce();
        ((Animal)unOiseau).introduce();




    }
}