/**
 * Demonstrates object cloning and the use of the Cloneable interface.
 */
package Shalom.Recomendations;

class Person implements Cloneable {

    /** Name of the person. */
    private String name;
    /** Age of the person. */
    private int age;

    /**
     * Gets the name of the person.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the age of the person.
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Constructs a new Person with the given name and age.
     * @param name the name
     * @param age the age
     */
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * Creates and returns a copy of this object.
     * @return a clone of this instance
     * @throws CloneNotSupportedException if the object's class does not support the Cloneable interface
     */
    @Override
    protected Person clone() throws CloneNotSupportedException {
        return (Person) super.clone();
    }

    /**
     * Sets the name of the person.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the age of the person.
     * @param age the new age
     */
    public void setAge(int age) {
        this.age = age;
    }
}

/**
 * Main class to demonstrate cloning of Person objects.
 */
public class MET53 {

    /**
     * Main method to demonstrate object cloning.
     * @param args command-line arguments
     * @throws CloneNotSupportedException if cloning is not supported
     */
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
