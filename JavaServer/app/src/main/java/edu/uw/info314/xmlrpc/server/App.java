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
        port(80);

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

                // Parsing the method:

                String expr = "/methodCall";
                NodeList nodeList = (NodeList) xpath.compile(expr).evaluate(doc, XPathConstants.NODESET);

                Node n = nodeList.item(0);
                Element e = (Element) n;
                String methodName = (e.getElementsByTagName("methodName").item(0).getTextContent());

                // Parsing parameters:

                String expr2 = "/methodCall/params/param";
                NodeList nodeList2 = (NodeList) xpath.compile(expr2).evaluate(doc, XPathConstants.NODESET);

                int [] params = new int[nodeList2.getLength()];

                for (int i = 0; i < nodeList2.getLength(); i++) {
                Node n2 = nodeList2.item(i);
                Element e2 = (Element) n2;
                    params[i] = Integer.parseInt(e2.getElementsByTagName("i4").item(0).getTextContent());
                }


                // Doing calculations:

                int faultCode = 0;

                String faultString = "";

                int res = -1;

                Calc c = new Calc();

                if (params.length == 0) {
                    faultCode = 3;
                    faultString = "illegal arguement type" ;
                }

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
                    if (r == 0) {
                        faultCode = 1;
                        faultString = "divide by zero";
                    } else {
                        res = c.divide(l, r);
                    }
                    
                } else if (methodName.equals("modulo")) {
                    int l = (Integer) params[0];
                    int r = (Integer) params[1];
                    if (r == 0) {
                        faultCode = 1;
                        faultString = "divide by zero";
                    } else {
                    res = c.modulo(l, r);
                    }
                }

                String r = "";
                
                if (faultCode == 0) {

                    r = "<?xml version=\"1.0\"?>" + "<methodResponse>" + "<params>" + "<param>" + "<value>" + "<i4>" +
                                res + "</i4>" + "</value>" + "</param>" + "</params>" + "</methodResponse>";

                } else {
                    r = "<?xml version=\"1.0\"?>" + "<methodResponse>" +  "<fault>" + "<value>" + "<struct>" + "<member>" + "<name>" + 
                                "faultCode" + "</name>" + "<value>" + "<int>" + faultCode + "</int>" + "</value>" + "</member>" +
                                "<member>" + "<name>" + "faultString" + "</name>" + "<value>" + "<string>" + faultString + "</string>" + 
                                "</value>" + "</member>" + "</struct>" + "</value>" + "</fault>" + "</methodResponse>";
                }

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
