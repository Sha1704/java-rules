
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class DeserializationClass {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        Emp emp = null;
        try {
            FileInputStream filein = new FileInputStream("C:\\Users\\dlege\\OneDrive\\Documents\\GitHub\\java-rules\\Shalom\\serialization and deserialization example\\test.txt");
            ObjectInputStream in = new ObjectInputStream(filein);
            emp = (Emp) in.readObject();
            in.close();
            filein.close();
        } finally {
            System.out.println("Deserializing Employee...");
            System.out.println("Name of Employee: " + emp.name);
            System.out.println("Address of employee: " + emp.address);
            System.out.println("Age of employee: " + emp.age);
        }
    }
}
