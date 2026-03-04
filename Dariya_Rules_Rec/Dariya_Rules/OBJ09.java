/**
 * Rule OBJ09-J. Compare classes and not class names
 * 
 * Implement account type check using class objects
 * 
 * @author Dariya
 */
package Dariya_Rules;

public class OBJ09 {

    /**
     * check if the given account is a premium by comparing class obj
     * 
     * @param acc the account object to check
     * @return true if acc is of class premiumAcc, otherwise false
     */
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

/**
 * Premium game account
 */
class premiumAcc {
}

/**
 * Regular game account
 */
class regularAcc {
}
