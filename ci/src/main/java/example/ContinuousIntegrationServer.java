package example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@WebServlet("/webhook")
public class ContinuousIntegrationServer extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ContinuousIntegrationServer.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // read github JSON payload
        String payload = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));

        // DEBUGG
        System.out.println("Received webhook payload: " + payload);

        // error handing (if payload is e.g. empty)
        if (payload == null || payload.trim().isEmpty()) {
            System.err.println("Error: Received empty JSON payload");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\":\"Empty payload received\"}");
            return;
        }
        try {
            // parsing webhook
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            String branch = json.get("ref").getAsString();
            String repoName = json.getAsJsonObject("repository").get("name").getAsString();
            String commitId = json.getAsJsonObject("head_commit").get("id").getAsString();
    
            // DEBUGG
            System.out.println("Webhook received: repository=" + repoName + ", branch=" + branch + ", commit=" + commitId);
    
            // response to server
            response.getWriter().println("{\"status\":\"success\", \"message\":\"Webhook received successfully\"}");
            
            // error handling
        } catch (Exception e) {
            System.err.println("Error parsing JSON payload: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\":\"Invalid JSON format\"}");
        }
    }

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
