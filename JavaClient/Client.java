import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import java.util.*;
import java.util.logging.*;

import org.xml.sax.*;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    public static String address;

    public static void main(String... args) throws Exception {
        address = "http://" + args[0] + ":" + args[1] + "/RPC";

        System.out.println(add(0) == 0);
        System.out.println(add(1, 2, 3, 4, 5) == 15);
        System.out.println(add(2, 4) == 6);
        System.out.println(subtract(12, 6) == 6);
        System.out.println(multiply(3, 4) == 12);
        System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        System.out.println(divide(10, 5) == 2);
        System.out.println(modulo(10, 5) == 0);
    }
    public static int add(int lhs, int rhs) throws Exception {
        int res = Request ("add",  new Integer[]{lhs, rhs});
        return res;
    }
    public static int add(Integer... params) throws Exception {
        int res = Request ("add",  params);
        return res;
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        int res = Request ("subtract",  new Integer[]{lhs, rhs});
        return res;
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        int res = Request ("multiply",  new Integer[]{lhs, rhs});
        return res;
    }
    public static int multiply(Integer... params) throws Exception {
        int res = Request ("multiply",  params);
        return res;
    }
    public static int divide(int lhs, int rhs) throws Exception {
        int res = Request ("divide",  new Integer[]{lhs, rhs});
        return res;
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        int res = Request ("modulo",  new Integer[]{lhs, rhs});
        return res;
    }

    public static int Request (String name, Integer... argss) {

        // Create and send request

        String req = "<?xml version=\"1.0\"?>" + "<methodCall>" + "<methodName>" + name + "</methodName>" + "<params>";

        for (Object p : argss) {
            req += "<param>" + "<value>" + "<i4>" + p + "</i4>" + "</value>" + "</param>";
        }

        req += "</params>" + "</methodCall>";

        HttpClient client = HttpClient.newHttpClient();
        try { HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(address))
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .build();
        
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        // Parsing Response Body

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new InputSource(new StringReader(body)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String test = "/methodResponse/fault";
        NodeList nodeList0 = (NodeList) xpath.compile(test).evaluate(doc, XPathConstants.NODESET);

        check(nodeList0, doc, xpath);

            String expr = "/methodResponse";
            NodeList nodeList = (NodeList) xpath.compile(expr).evaluate(doc, XPathConstants.NODESET);

            int param = -1;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node n = nodeList.item(i);
                Element e = (Element) n;
                param = Integer.parseInt(e.getElementsByTagName("param").item(0).getTextContent());
            }

            return param;

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException p) {
            p.printStackTrace();
        } catch (SAXException s) {
            s.printStackTrace();
        } catch (XPathException x) {
            x.printStackTrace();
        } 
        return -1;
    }


    public static void check(NodeList nodeList0, Document doc, XPath xpath) {
        try {
            if (nodeList0.getLength() != 0) {
                String test = "/methodResponse/fault/value/struct/member";
                NodeList nodeListF = (NodeList) xpath.compile(test).evaluate(doc, XPathConstants.NODESET);
                int value = -1;
            
                Node n = nodeListF.item(0);
                Element e = (Element) n;
                value = Integer.parseInt(e.getElementsByTagName("value").item(0).getTextContent());

                if (value == 3) {
                    throw new IllegalArgumentException();

                } else if (value == 1) {
                    throw new ArithmeticException();
                }
    }
        } catch (XPathExpressionException x) {
            x.printStackTrace();
}
}
}
