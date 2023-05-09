package edu.uw.info314.xmlrpc.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import static spark.Spark.*;

class Call {
    public String name;
    public List<Object> args = new ArrayList<Object>();
}

public class App {
    public static final Logger LOG = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {
        port(8080);

        // This is the mapping for POST requests to "/RPC";
        // this is where you will want to handle incoming XML-RPC requests
        post("/RPC", (request, response) -> {  
            String body = request.body();
            
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc = builder.parse(new InputSource(new StringReader(body)));

                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();

                String expr = "/methodCall";
                NodeList nodeList = (NodeList) xpath.compile(expr).evaluate(doc, XPathConstants.NODESET);

                String methodName = "";
                int [] params = new int[nodeList.getLength()];

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node n = nodeList.item(i);
                    Element e = (Element) n;
                    methodName = (e.getElementsByTagName("methodName").item(0).getTextContent());
                    params[i] = Integer.parseInt(e.getElementsByTagName("param").item(0).getTextContent());
                }

                int res = -1;

                Calc c = new Calc();

                if (methodName.equals("add")) {
                    res = c.add(params);

                } else if (methodName.equals("subtract")) {
                    int l = (Integer) params[0];
                    int r = (Integer) params[1];
                    res = c.subtract(l, r);
                    
                } else if (methodName.equals("multiply")) {
                    res =  c.multiply(params);
                    
                } else if (methodName.equals("divide")) {
                    int l = (Integer) params[0];
                    int r = (Integer) params[1];
                    res = c.divide(l, r);
                    
                } else if (methodName.equals("modulo")) {
                    int l = (Integer) params[0];
                    int r = (Integer) params[1];
                    res = c.modulo(l, r);
                }
                
                String r = "<?xml version=\"1.0\"?>" + "<response>" + res +  "</response>";
                response.type("text/xml");
                return r;
                

            } catch (Exception e) {
                response.status(401);
                return ""; 

            }
        });

        // Each of the verbs has a similar format: get() for GET,
        // put() for PUT, delete() for DELETE. There's also an exception()
        // for dealing with exceptions thrown from handlers.
        // All of this is documented on the SparkJava website (https://sparkjava.com/).
    }
}
