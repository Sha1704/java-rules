// OBJ09-J. Compare classes and not class names
// Implement account type check using class objects
package Dariya_Rules;

public class OBJ09 {

    // method check if the account is a premium by comparing class obj
    public static boolean isPremiumAcc(Object acc) {
        return acc.getClass() == premiumAcc.class;
    }

    public static void main(String[] args) {
        // create a premium and regular acc
        Object premium = new premiumAcc();
        Object regular = new regularAcc();

        // check and print if the acc are premium
        System.out.println("Premium account? " + isPremiumAcc(premium)); 
        System.out.println("Premium account? " + isPremiumAcc(regular)); 
    }
}

// Premium game account
class premiumAcc {
}

// Regular game account
class regularAcc {
}
