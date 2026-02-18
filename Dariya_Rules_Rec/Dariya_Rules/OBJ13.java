// OBJ13-J. Ensure that references to mutable objects are not exposed
// Implement player inventory as a public unmodifiable list 
package Dariya_Rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class OBJ13 {
    // private array of inventory items
    private static final String[] ITEMS_ARRAY = {"Sword", "Shield", "Potion"};
    // public unmodifiable list created from private array
    // clients can view but cannot modify
    public static final List<String> ITEMS =
        Collections.unmodifiableList(Arrays.asList(ITEMS_ARRAY));

    public static void main(String[] args) {
        // print current inventory
        System.out.println("Player inventory: " + ITEMS);
        // try to modify the list 
        try {
            ITEMS.add("Magic Wand"); 
        } catch (Exception e) {
            System.out.println("You cannot modify the inventory!");
        }
    }
}

