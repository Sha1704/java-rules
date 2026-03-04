package Azul.Rules;

import java.util.Arrays;

/**
 * Demonstrates EXP02-J: Compare arrays correctly.
 * This class shows the proper way to compare array contents 
 * using @code Arrays.equals() 
 * instead of the default @code equals() method,
 * which compares references.
 */

public class EXP02{
    /**
     * The main method creates two identical integer arrays and compares them
     * using @code Arrays.equals() to verify they are equal.
     * @param args command-line arguments (not used)
     */
	public static void main (String[] args){
		int[] arr1 = {1, 2, 3};
		int[] arr2 = {1, 2, 3};

		//doesnt use arr1.equals(arr2)
		if(Arrays.equals(arr1, arr2)){
			System.out.println("Arrays are equal.");
		}else{
			System.out.println("Array are not equal.");
		}
 
	}
}
