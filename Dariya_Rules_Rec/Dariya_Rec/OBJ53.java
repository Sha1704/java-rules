/**
 * Rule OBJ53-J. Do not use direct buffers for short-lived, infrequently used objects
 * 
 * implement short-lived buffer usage using heap memory 
 * 
 * @author Dariya
 */
package Dariya_Rec;
import java.nio.ByteBuffer;

public class OBJ53 {
    public static void main(String[] args) {
        // heap-based buffer for temporary data
        ByteBuffer tempData = ByteBuffer.allocate(1024); 
        tempData.put("Player picked up a coin".getBytes());
        tempData.flip();

        byte[] bytes = new byte[tempData.remaining()];
        tempData.get(bytes);
        // buffer is used once and then discarded
        System.out.println("Event: " + new String(bytes));

    }
}
