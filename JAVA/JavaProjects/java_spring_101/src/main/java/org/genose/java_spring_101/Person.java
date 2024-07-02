package org.genose.java_spring_101;

public class Person {
    private String name;
    private int age;

    // Constructor, getters, and setters
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and setters omitted for brevity

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}