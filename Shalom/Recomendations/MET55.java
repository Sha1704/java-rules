package Shalom.Recomendations;
/*
 * RECOMENDATION MET55-J: Do return null when returning an empty 
 * list of arrays or arraylist. Instead, return an empty 
 * array or arraylist.
 */
class Numbers {

    private final int[] numbers;

    /**
     * Default constructor initializing an empty array.
     */
    public Numbers() {
        this.numbers = new int[0];
    }

    /**
     * Constructor initializing an array of given size.
     * @param arraySize the size of the array
     */
    Numbers(int arraySize) {
        this.numbers = new int[arraySize];
    }

    /**
     * Returns the array, printing its contents if not empty.
     * @return the array of numbers
     */
    public int[] returnArray() {
        if (this.numbers == null || this.numbers.length == 0) {
            System.out.println("Returning empty array.");
            return new int[0];
        } else {
            System.out.println("Printing array values.");

            for (int i = 0; i < this.numbers.length; i++) {
                System.out.println(numbers[i]);
            }
            return numbers;
        }
    }

    /**
     * Adds a number to the array at the specified index.
     * @param number the number to add
     * @param index the index at which to add the number
     */
    public void addNum(int number, int index) {
        numbers[index] = number;
        System.out.println("Added " + number + " to array at index " + index + ".");
    }
}

/**
 * Main class to demonstrate Numbers array operations.
 */
public class MET55 {

    /**
     * Main method to demonstrate array handling.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Numbers numArray = new Numbers();

        System.out.println("Trying to return empty array.");
        numArray.returnArray();
        System.out.println("Returned empty array.");

        System.out.println();

        numArray = new Numbers(5);

        numArray.addNum(10, 0);
        numArray.addNum(20, 1);
        numArray.addNum(30, 2);
        numArray.addNum(40, 3);
        numArray.addNum(50, 4);

        System.out.println("Trying to return non-empty array.");
        numArray.returnArray();
        System.out.println("Returned non-empty array.");

        System.out.println();
    }
}
