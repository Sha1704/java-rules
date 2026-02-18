import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializationClass 
{
    public static void main(String[] args) {
        Emp emp = new Emp();
        emp.name = "john doe";
        emp.address = "123 over there st";
        emp.age = 69;

        try {
            FileOutputStream fileout = new FileOutputStream("C:\\Users\\dlege\\OneDrive\\Documents\\GitHub\\java-rules\\Shalom\\serialization and deserialization example\\test.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileout);
            out.writeObject(emp);
            out.close();
            fileout.close();
            System.out.println("Serialized data is saved in test.txt file");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
