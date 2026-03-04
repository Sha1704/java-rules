package Azul.Rules;

//Rule 13 RIO05-J
//do not expose buffers or thier backing arrays methods to untrusted code
//copy code or create a read-only view

import java.nio.CharBuffer;

/**
 * FIO05-J: Do not expose buffers or their backing arrays to untrusted code.
 * Provides a read-only view of a character buffer to prevent modification
 * of the internal buffer by external code.
 */

public class FIO05{
	static final class Dup {
		private CharBuffer cb;

        /**
         * Constructs a Dup instance, initializing a CharBuffer with "Hello".
         */
		public Dup(){
			cb = CharBuffer.allocate(5);
			cb.put("Hello");
			cb.flip();
		}

        /**
         * Returns a read-only view of the internal buffer.
         * @return a read-only {@link CharBuffer} backed by the internal buffer
         */
		public CharBuffer getBufferCopy(){
			return cb.asReadOnlyBuffer(); //read-only view of buffer
		}
	}

    /**
     * The main method creates a Dup object, obtains a read-only buffer,
     * and attempts (and fails) to modify it, demonstrating the protection.
     * @param args command-line arguments (not used)
     */
	public static void main (String[] args){
		Dup dup = new Dup();
		CharBuffer buffer = dup.getBufferCopy();

		System.out.println("Buffer content: " + buffer);

		//try to modify test
		try{
			buffer.put(0, 'X');
		} catch (Exception e){
			System.out.println("Failed" + e);
		}
	}
}