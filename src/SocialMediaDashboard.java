package com.dashboard;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class SocialMediaDashboard {

    private static final int PORT = 8000;
    private static final String USERNAME = "admin";
    private static String password = "password";

    static SocialMediaApiClient socialMediaApiClient;

    public static void main(String[] args) throws IOException {
        socialMediaApiClient = new SocialMediaApiClient();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new StaticFileHandler("web"));
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/logout", new LogoutHandler());
        server.createContext("/api/change-password", new ChangePasswordHandler());

        server.createContext("/api/social/twitter", new TwitterHandler());
        server.createContext("/api/social/facebook", new FacebookHandler());
        server.createContext("/api/social/instagram", new InstagramHandler());

        server.setExecutor(null);
        System.out.println("Server started at http://localhost:" + PORT);
        server.start();
    }

    static class TwitterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String id = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals("id")) {
                        id = pair[1];
                        break;
                    }
                }
            }
            String response = socialMediaApiClient.fetchTwitterData(id);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class FacebookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String id = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals("id")) {
                        id = pair[1];
                        break;
                    }
                }
            }
            String response = socialMediaApiClient.fetchFacebookData(id);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class InstagramHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String id = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals("id")) {
                        id = pair[1];
                        break;
                    }
                }
            }
            String response = socialMediaApiClient.fetchInstagramData(id);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;

        StaticFileHandler(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/login.html";
            }
            java.nio.file.Path filePath = Paths.get(baseDir + path).normalize().toAbsolutePath();

            if (!filePath.startsWith(Paths.get(baseDir).toAbsolutePath())) {
                // Prevent path traversal attack
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            byte[] bytes = Files.readAllBytes(filePath);
            String contentType = getContentType(filePath.toString());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            if (path.endsWith(".json")) return "application/json";
            return "application/octet-stream";
        }
    }

    static class LoginHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes());
            Map<String, String> params = parseJson(body);

            String username = params.get("username");
            String password = params.get("password");

            String response;
            if (USERNAME.equals(username) && SocialMediaDashboard.password.equals(password)) {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                response = "{\"message\":\"Login successful\"}";
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                response = "{\"message\":\"Invalid username or password\"}";
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> parseJson(String json) {
            Map<String, String> map = new HashMap<>();
            json = json.trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replaceAll("\"", "");
                        String value = kv[1].trim().replaceAll("\"", "");
                        map.put(key, value);
                    }
                }
            }
            return map;
        }
    }

    static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                exchange.getResponseBody().close();
                return;
            }
            if (!method.equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }
            // No session management, so just respond OK
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(200, -1);
            exchange.getResponseBody().close();
        }
    }

    static class ChangePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                exchange.getResponseBody().close();
                return;
            }
            if (!method.equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes());
            Map<String, String> params = parseJson(body);

            String currentPassword = params.get("currentPassword");
            String newPassword = params.get("newPassword");
            String confirmPassword = params.get("confirmPassword");

            String response;
            if (!SocialMediaDashboard.password.equals(currentPassword)) {
                response = "{\"message\":\"Current password is incorrect.\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(400, response.getBytes().length);
            } else if (newPassword == null || !newPassword.equals(confirmPassword)) {
                response = "{\"message\":\"New password and confirmation do not match.\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(400, response.getBytes().length);
            } else {
                SocialMediaDashboard.password = newPassword;
                response = "{\"message\":\"Password changed successfully.\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> parseJson(String json) {
            Map<String, String> map = new HashMap<>();
            json = json.trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replaceAll("\"", "");
                        String value = kv[1].trim().replaceAll("\"", "");
                        map.put(key, value);
                    }
                }
            }
            return map;
        }
    }
}

