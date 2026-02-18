// implement game account last login
// OBJ05-J. Do not return references to private mutable class members
package Dariya_Rules;

import java.util.Date;

public class OBJ05 {
    // private mutable field so it is not exposed directly
    private Date lastLogin;
    public OBJ05(){
        lastLogin = new Date();
    }

    // return a defensive copy to protect internal state
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

