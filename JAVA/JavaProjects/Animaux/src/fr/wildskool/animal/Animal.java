package fr.wildskool.animal;

public class Animal {
    protected int nombreDePattes = 0;
    protected String nom = "";
    protected boolean isCarnivorous = false;

    public Animal( String nom, int nombreDePattes){
        this.nom = nom;
        this.nombreDePattes = nombreDePattes;
    }
    public boolean  isDangerous()
    {
        return isCarnivorous;
    }
    public void introduce(){
        String str =  String.format("Hey, en tant que (%s)",  super.getClass().getSimpleName() );
            str += String.format(" j ai (%d) pattes et je %s carnivore", this.nombreDePattes, (isDangerous()?"suis":"ne suis pas"));
            System.out.println(str);
}

}
