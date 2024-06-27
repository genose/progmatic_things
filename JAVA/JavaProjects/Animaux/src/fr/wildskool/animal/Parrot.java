package fr.wildskool.animal;

public class Parrot  extends Bird {
    protected boolean canTalk = false;

    public boolean isCanTalk() {
        return canTalk;
    }

    public void setCanTalk(boolean canTalk) {
        this.canTalk = canTalk;
    }

    public Parrot(String nom, int nombreDePattes) {
        super(nom, nombreDePattes);
        this.canTalk = true;
    }

    @Override
    public void fly() {
        System.out.println(" Fly ");
    }

    public Parrot(String nom, int nombreDePattes, boolean canTalk) {
        super(nom, nombreDePattes);
        this.canTalk = canTalk;
    }
}
