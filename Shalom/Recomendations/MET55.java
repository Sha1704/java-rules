package Shalom.Recomendations;

class Numbers {

    private final int[] numbers;

    public Numbers() {
        this.numbers = new int[0];
    }

    Numbers(int arraySize) {
        this.numbers = new int[arraySize];
    }

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

    public void addNum(int number, int index) {
        numbers[index] = number;
        System.out.println("Added " + number + " to array at index " + index + ".");
    }
}

public class MET55 {

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
