package Shalom.Example;

/**
 * Serializable employee class with name, address, and age fields.
 */

import java.io.Serializable;

public class Emp implements Serializable {

    /** Name of the employee. */
    public String name;
    /** Address of the employee. */
    public String address;
    /** Age of the employee. */
    public int age;
}
