
package co.escuelaing.edu.parcialarep1corte;




import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {

    private static final HttpServer _instance = new HttpServer();
    private static final Integer PORT = 35000;
    private final HashMap<String, Method> services = new HashMap<String, Method>();

    public static HttpServer getInstance() {
        return _instance;
    }

    private HttpServer() {
    }

    public void startServer(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir por el puerto: " + PORT);
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            manageConnection(clientSocket);
        }
        serverSocket.close();
    }

   

    public void manageConnection(Socket clientSocket) throws IOException, URISyntaxException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine, outputLine;
        ArrayList<String> request = new ArrayList<String>();
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            request.add(inputLine);
            if (!in.ready()) {
                break;
            }
        }

        String uriStr = request.get(0).split(" ")[1];
        URI resourceURI = new URI(uriStr);
        outputLine = getResource(resourceURI);
        out.println(outputLine);

        out.close();
        in.close();
        clientSocket.close();
    }

    public String getResource(URI resourceURI) throws IOException {
        System.out.println("Received URI path: " + resourceURI.getPath());
        System.out.println("Received URI query: " + resourceURI.getQuery());
        System.out.println("Received URI: " + resourceURI);
        //return computeDefaultResponse();
        return getRequestDisc(resourceURI);
    }

    //Hacer que el servidor ya reciba js, css, imagenes y no solo html.
    public String getRequestDisc(URI resourceURI) throws IOException {
        Path file = Paths.get("target/classes/public" + resourceURI.getPath());
        String output = null;

        try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {

            String str, type = null;
            type = "text/css";
            output = "HTTP/1.1 200 OK\r\n" + "Content-Type: " + type + "\r\n"; // Define tipo de archivo por ahora solo css

            while ((str = in.readLine()) != null) {
                System.out.println(str);
                output += str + "\n";
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        System.out.println(output);
        return output;
    }

    

    public String getServiceResponse(URI serviceURI) {
        String response = "";
        Method m = services.get(serviceURI.getPath().substring(3));
        try {
            response = m.invoke(null).toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n" + response;
        return response;
    }

    public String computeDefaultResponse() {
        return "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/html\r\n"
                            + "\r\n"
                            + "<!DOCTYPE html>"
                            + "<html>"
                            + "<head>"
                            + "<meta charset=\"UTF-8\">"
                            + "<title>Title of the document</title>\n" + "</head>"
                            + "<body>"
                            + "My Web Site"
                            + "</body>"
                            + "</html>";
    }
        
    

    public static void main(String[] args) throws IOException, URISyntaxException {
        HttpServer.getInstance().startServer(args);
    }
}

