package Maria.Rules;

/**
 * Recommendation 06. MET 51-J: Do not use overloaded methods to differentiate between runtime types.
 * @author Maria Plascencia
 */
public class MET51 {
    public static void main(String[] args) {
        //Using proper polymorphism to call the correct print method 
        //based on the runtime type of the object without relying on overloaded methods.
        Shape s = new Circle();
        s.print();
    }
}
/**
 * Helper class.
 */
class Shape {
    void print() {
        System.out.println("Printing shape");
    }
}
/**
 * Helper class with overridden method.
 */
class Circle extends Shape {
    @Override
    void print() {
        System.out.println("Printing circle");
    }
}