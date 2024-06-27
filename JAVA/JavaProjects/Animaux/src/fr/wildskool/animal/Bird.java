package fr.wildskool.animal;


public abstract class Bird extends Animal {


    public Bird(String nom, int nombreDePattes) {
        super(nom, nombreDePattes);
    }

    public abstract void fly();

}
