package de.axxepta.lxdjavaclient;

import de.axxepta.lxdjavaclient.connect.HTTPClient;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

import java.util.Map;

import static spark.Spark.*;

public class App {

    private static HTTPClient httpClient;
    private static final String API_VER = "/1.0/";

    private static final String CT = "Content-Type";
    private static final String JSON = "application/json";

    private static void initClient(String... args) {
        String host = args[0];
        String keyStorePwd = args[1];
        String keyStoreFile = args.length < 3 ? "lxc.p12" : args[2];
        int port = 8443;
        httpClient = new HTTPClient(keyStoreFile, keyStorePwd, host, port);
    }

    public static void main(String... args) {
        if (args.length < 2) {
            System.out.println("Provide host IP and Keystore password as arguments, optional 3rd arg is PKCS12 keystore file (default: lxc.p12)");
            System.exit(0);
        }
        App.initClient(args);

        get("/:path", (request, response) -> App.handle(HttpMethod.get, request, response, 1) );
        get("/:path/:path2", (request, response) -> App.handle(HttpMethod.get, request, response, 2) );
        get("/:path/:path2/:path3", (request, response) -> App.handle(HttpMethod.get, request, response, 3) );
        get("/:path/:path2/:path3/:path4", (request, response) -> App.handle(HttpMethod.get, request, response, 4) );
        get("/:path/:path2/:path3/:path4/:path5", (request, response) -> App.handle(HttpMethod.get, request, response, 5) );

        delete("/:path/:path2", (request, response) -> App.handle(HttpMethod.delete, request, response, 2) );
        delete("/:path/:path2/:path3", (request, response) -> App.handle(HttpMethod.delete, request, response, 3) );
        delete("/:path/:path2/:path3/:path4", (request, response) -> App.handle(HttpMethod.delete, request, response, 4) );
        delete("/:path/:path2/:path3/:path4/:path5", (request, response) -> App.handle(HttpMethod.delete, request, response, 5) );

        post("/:path", (request, response) -> App.handle(HttpMethod.post, request, response, 1) );
        post("/:path/:path2", (request, response) -> App.handle(HttpMethod.post, request, response, 2) );
        post("/:path/:path2/:path3", (request, response) -> App.handle(HttpMethod.post, request, response, 3) );
        post("/:path/:path2/:path3/:path4", (request, response) -> App.handle(HttpMethod.post, request, response, 4) );
        post("/:path/:path2/:path3/:path4/:path5", (request, response) -> App.handle(HttpMethod.post, request, response, 5) );

        put("/:path", (request, response) -> App.handle(HttpMethod.put, request, response, 1) );
        put("/:path/:path2", (request, response) -> App.handle(HttpMethod.put, request, response, 2) );
        put("/:path/:path2/:path3", (request, response) -> App.handle(HttpMethod.put, request, response, 3) );
        put("/:path/:path2/:path3/:path4", (request, response) -> App.handle(HttpMethod.put, request, response, 4) );
        put("/:path/:path2/:path3/:path4/:path5", (request, response) -> App.handle(HttpMethod.put, request, response, 5) );

        patch("/:path", (request, response) -> App.handle(HttpMethod.patch, request, response, 1) );
        patch("/:path/:path2", (request, response) -> App.handle(HttpMethod.patch, request, response, 2) );
        patch("/:path/:path2/:path3", (request, response) -> App.handle(HttpMethod.patch, request, response, 3) );
        patch("/:path/:path2/:path3/:path4", (request, response) -> App.handle(HttpMethod.patch, request, response, 4) );
        patch("/:path/:path2/:path3/:path4/:path5", (request, response) -> App.handle(HttpMethod.patch, request, response, 5) );
    }


    private static Object handle(HttpMethod method, Request request, Response response, int level) {
        return handleRequest(method, request, response,API_VER + getPath(request.params(), level));
    }

    private static Object handleRequest(HttpMethod method, Request request, Response response, String path) {
        try {
            response.header(CT, JSON);
            if (method.equals(HttpMethod.get) || method.equals(HttpMethod.delete)) {
                return httpClient.p(method, path);
            } else {
                String content = request.body();
                return httpClient.p(method, path, content);
            }
        } catch (Throwable ex) {
            response.status(500);
            return ex.getMessage();
        }
    }

    private static String getPath(Map<String, String> p, int level) {
        switch (level) {
            case 2:
                return String.join("/", p.get(":path"), p.get(":path2"));
            case 3:
                return String.join("/", p.get(":path"), p.get(":path2"), p.get(":path3"));
            case 4:
                return String.join("/", p.get(":path"), p.get(":path2"), p.get(":path3"),
                        p.get(":path4"));
            case 5:
                return String.join("/", p.get(":path"), p.get(":path2"), p.get(":path3"),
                        p.get(":path4"), p.get(":path5"));
            default:
                return p.get(":path");
        }
    }

}
