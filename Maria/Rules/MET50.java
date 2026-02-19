package Maria.Rules;

/**
 * Recommendation 06. MET 50-J: Avoid ambiguous or confusing uses of overload 
 * @author Maria Plascencia
 */
class MET50 {
    /**
     * Calculates the distance traveled given a start and end position.
     * @param start
     * @param end
     * @return the distance traveled
     */
    int getDistanceTraveled(int start, int end) {
        return end - start;
    }
    /**
     * Calculates the remaining distance to a destination given the current position.
     * @param current
     * @param destination
     * @return the remaining distance to the destination
     */
    int getRemainingDistance(int current, int destination) {
        return destination - current;
    }
    public static void main(String[] args) {
        int current = 100;
        int destination = 150;
        //Correctly uses two different methods to calculate distance traveled and remaining distance without ambiguity.
        System.out.println("Distance traveled: " + new MET50().getDistanceTraveled(0, current));
        System.out.println("Remaining distance: " + new MET50().getRemainingDistance(current, destination));
    }
}