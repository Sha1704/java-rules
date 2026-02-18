package Shalom.Recomendations;

class Person implements Cloneable {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected Person clone() throws CloneNotSupportedException {
        return (Person) super.clone();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

public class MET53 {

    public static void main(String[] args) throws CloneNotSupportedException {
        Person p1 = new Person("Alice", 25);
        Person p2 = p1.clone();

        System.out.println(p1.getName()); // Alice
        System.out.println(p2.getName()); // Alice

        p2.setName("Bob");

        System.out.println(p1.getName()); // Alice
        System.out.println(p2.getName()); // Bob
    }
}
