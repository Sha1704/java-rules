package app;

public class VIP {


    //COUNTING SYSTEM
    //this is the basic counting system, it gives a count of the cards that have been played, it does not take into account the number of decks being used
    public static int runningCount(int count, int currentCard){
        if(currentCard >= 2 && currentCard <= 6){
            count++;
        }
        else if(currentCard >= 10 || currentCard == 1){
            count--;
        }
        return count;
    
    }

    // This is used if mulitple decks are being used, it gives a more accurate count of the cards left 
    public static double trueCount(int runningCount, int decksRemaining){
        if(decksRemaining > 0){
            return (double) runningCount / decksRemaining;
        }
        else{
            return 0;
        }
    }

    // This is used to give the player an idea of how the count is affecting their chances of winning, it gives a message based on the count
    public static void countingCards(int count){
        if(count > 0){
            System.out.println("The count is positive, you have an advantage");
        }
        else if(count < 0){
            System.out.println("The count is negative, you have a disadvantage");
        }
        else{
            System.out.println("The count is neutral, you have no advantage or disadvantage");
        }
    }
        
}
