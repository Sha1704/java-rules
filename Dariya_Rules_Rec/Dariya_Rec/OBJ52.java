// OBJ52-J. Write garbage-collection-friendly code
// implement short-lived objects for game messages
package Dariya_Rec;

// temporary game messages that is designed to be short-lived
class GameMessage {
    // immutable message content
    private final String message;
    // constructor initializes immutable data
    GameMessage(String message) {
        this.message = message;
    }
    // show message
    void show() {
        System.out.println(message);
    }
}
public class OBJ52 {
    public static void main(String[] args){
        // array of short lived event msg
        String[] events = {
            "Player collected a coin",
            "Player found a key"
        };
        // create and discard GameMessage objects quickly
        for (String event : events){
            GameMessage msg = new GameMessage(event);
            msg.show();
        }
        System.out.println("Round finished. All messages are temporary and discarded.");
    }
}
