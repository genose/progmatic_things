public abstract class Bird {
  
    // attributes
    private String name;  
    private int age;  
  
    // constructors
    public Bird(String name) {  
        this.name = name;  
        this.age = 0;  
    }
    public Bird(){
        this.name = null;
        this.age = 0;
    }
  
    // getters and setters
    public String getName() {  
        return this.name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public int getAge() {  
        return this.age;  
    }  
  
    public void setAge(int age) {
        this.age = age;
    }

    // methods
    public abstract String sing();
}
