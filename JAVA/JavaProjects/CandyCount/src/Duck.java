public class Duck  extends Bird{

    private String name;
    private int age;
    private boolean swimming;

    public Duck()
    {
        super();
        this.name = null;
        this.age = 0;
        this.swimming = false;
    }
    public Duck(String name) {
        super();
        this.name = name;
        this.age = 0;
        this.swimming = false;
    }

    public Duck(String name, int age) {
        this.name = name;
        this.age = age;
        this.swimming = false;
    }


@Override
    public String sing() {
        return "Quack!";
    }

    public boolean isSwimming() {
        return swimming;
    }

    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }
}
