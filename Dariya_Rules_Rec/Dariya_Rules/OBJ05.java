/**
 * Rule OBJ05-J. Do not return references to private mutable class members
 * 
 * implement game account last login with defensive copying
 * 
 * @author Dariya
 */
package Dariya_Rules;
import java.util.Date;

public class OBJ05 {
    // private mutable field so it is not exposed directly
    private Date lastLogin;
    /**
     * Initializes last login to current time
     */
    public OBJ05(){
        lastLogin = new Date();
    }

    /**
     * Returns a defensive copy of last login to protect internal state
     * 
     * @return copy of last login date
     */
    public Date getDate(){
        return (Date)lastLogin.clone();
    }

    public static void main(String[] args){
        OBJ05 acc = new OBJ05();
        // modifying the returned Date should not affect internal state
        Date login = acc.getDate();
        login.setTime(0);

        System.out.println("Modified copy: " + login);
        System.out.println("Actual last login: " + acc.getDate());
    }
}

