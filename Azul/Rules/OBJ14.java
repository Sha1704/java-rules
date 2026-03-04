package Azul.Rules;

/**
 * Demonstrates OBJ14-J: Do not use an object that has been freed.
 * This class defines a {@code Resource} that maintains an {@code open} flag
 * to prevent usage after it has been freed.
 */

public class OBJ14{
    // A static inner class simulates a resource that must be checked before use
	static class Resource{
		private boolean open = true;

         /**
         * Uses the resource if it is still open; otherwise throws an exception.
         * @throws IllegalStateException if the resource has already been freed
         */
		public void use(){
			if(!open){
				throw new IllegalStateException("Resource freed");
			}
			System.out.println("Resource in use");
		}

        // Frees the resource and marks it as no longer usable
		public void free(){
			open = false; //free resource
			System.out.println("Freed Resource");
		}
	}

    /**
     * The main method creates a Resource, uses it, frees it, and then attempts
     * to use it again (which fails) to demonstrate the protection.
     * @param args command-line arguments
     */
	public static void main(String[] args){

		Resource res = new Resource();

		//use resource
		res.use();

		//free resource
		res.free();

		//test to use resource after freeing
		try{
			res.use();
		}catch(IllegalStateException e){
			System.out.println("error: " + e.getMessage());
		}

	}
}