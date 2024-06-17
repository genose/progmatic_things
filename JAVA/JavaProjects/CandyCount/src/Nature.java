import java.util.ArrayList;

public class Nature {

    public static void main(String[] args) {

        // donald receives a reference to a new instance of class Duck
        Duck donald = new Duck("Donald"); 
        // setter usage
        donald.setAge(84);
        // getters usage
        System.out.println("1. Duck name is: " + donald.getName());
        System.out.println("2. He is " + String.valueOf(donald.getAge()));
        System.out.println("3. He sings " + donald.sing());

        Eagle thorondor = new Eagle("Thorondor");
        System.out.println("4. Eagle name is: " + thorondor.getName());
        System.out.println("5. He is " + String.valueOf(thorondor.getAge()));
        System.out.println("6. He sings " + thorondor.sing());
        
        // since Donald and Thorondor are both instances of Bird, they can be
        // stored in the same ArrayList (meant to contain Bird instances)
        ArrayList<Bird> birds = new ArrayList<>();
        birds.add(donald);
        birds.add(thorondor);
        
        System.out.println("7. Birds in the list: ");
        // iterate over the list
        for (Bird bird : birds) {
            System.out.println("- " + bird.getName());
        }
    }
}
