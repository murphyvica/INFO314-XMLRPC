import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import org.w3c.dom.Node;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void main(String... args) throws Exception {
        System.out.println(add() == 0);
        /* System.out.println(add(1, 2, 3, 4, 5) == 15);
        System.out.println(add(2, 4) == 6);
        System.out.println(subtract(12, 6) == 6);
        System.out.println(multiply(3, 4) == 12);
        System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        System.out.println(divide(10, 5) == 2);
        System.out.println(modulo(10, 5) == 0); */
    }
    public static int add(int lhs, int rhs) throws Exception {
        Request ("add",  new Object[]{lhs, rhs});
        return -1;
    }
    public static int add(Integer... params) throws Exception {
        return -1;
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int multiply(Integer... params) throws Exception {
        return -1;
    }
    public static int divide(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        return -1;
    }

    public static int Request (String name, Object... argss) {

        String req = "<?xml version=\"1.0\"?>" + "<methodCall>" + "<methodName>" + name + "</methodName>" + "<params>";

        for (Object p : argss) {
            req += "<param>" + "<value>" + "<i4>" + p + "</i4>" + "</value>" + "</param>";
        }

        req += "</params>" + "</methodCall>";


        HttpClient client = HttpClient.newHttpClient();
        try { HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/RPC"))
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .build();
        
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
