package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;


public class ContinuousIntegrationServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        // make the ServerConnector component listen on ALL network interfaces
        ServerConnector connector = new ServerConnector(server);
        // port
        connector.setPort(8080);
        server.addConnector(connector);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        // DEBUGG
        System.out.println("starting CI server on ALL interfaces (0.0.0.0:8080)");

        server.start();
        server.join();
    }
}
