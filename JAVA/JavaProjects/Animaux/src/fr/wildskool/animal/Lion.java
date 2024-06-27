package fr.wildskool.animal;

public class Lion extends Animal implements Carnivorous {
    private Boolean isAlpha = false;

    public Lion(String nom, int nombreDePattes) {
        super(nom, nombreDePattes);
    }

    public Boolean getAlpha() {
        return isAlpha;
    }

    public void setAlpha(Boolean alpha) {
        isAlpha = alpha;
    }

    @Override
    public void hunt() {
        System.out.println(String.format("%s, hunt", this.getClass().getSimpleName() ) );
    }
}
