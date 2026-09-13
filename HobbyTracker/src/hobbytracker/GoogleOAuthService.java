package hobbytracker;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Google authorization-code flow for installed apps. Configure QUESTLOG_GOOGLE_CLIENT_ID
 * and, only for a confidential OAuth client, QUESTLOG_GOOGLE_CLIENT_SECRET in the process
 * environment. The redirect URI must be registered as http://127.0.0.1:<dynamic-port>/oauth2/callback.
 */
public final class GoogleOAuthService {
    private static final String AUTH="https://accounts.google.com/o/oauth2/v2/auth", TOKEN="https://oauth2.googleapis.com/token", INFO="https://openidconnect.googleapis.com/v1/userinfo";
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    public GoogleProfile signIn() throws Exception {
        String clientId=required("QUESTLOG_GOOGLE_CLIENT_ID"), secret=System.getenv("QUESTLOG_GOOGLE_CLIENT_SECRET");
        String state=UUID.randomUUID().toString(); CompletableFuture<Map<String,String>> callback=new CompletableFuture<>();
        HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0); String redirect="http://127.0.0.1:"+server.getAddress().getPort()+"/oauth2/callback";
        server.createContext("/oauth2/callback", exchange->{Map<String,String> q=query(exchange.getRequestURI().getRawQuery());String page=q.containsKey("error")?"<h2>Google sign-in was cancelled.</h2>":"<h2>Questlog sign-in complete. You may close this tab.</h2>";byte[] body=page.getBytes(StandardCharsets.UTF_8);exchange.sendResponseHeaders(200,body.length);exchange.getResponseBody().write(body);exchange.close();callback.complete(q);}); server.start();
        try {String url=AUTH+"?"+form(Map.of("client_id",clientId,"redirect_uri",redirect,"response_type","code","scope","openid email profile","state",state,"access_type","offline","prompt","select_account"));if(!Desktop.isDesktopSupported())throw new IOException("Desktop browser integration is unavailable.");Desktop.getDesktop().browse(URI.create(url));Map<String,String> q=callback.get(3,TimeUnit.MINUTES);if(!state.equals(q.get("state")))throw new SecurityException("OAuth state validation failed.");if(q.containsKey("error"))throw new IOException("Google sign-in was cancelled.");String token=requestToken(clientId,secret,redirect,q.get("code"));return profile(token);} finally {server.stop(0);}
    }
    private String requestToken(String id,String secret,String redirect,String code) throws Exception {Map<String,String> values=new LinkedHashMap<>();values.put("code",code);values.put("client_id",id);values.put("redirect_uri",redirect);values.put("grant_type","authorization_code");if(secret!=null&&!secret.isBlank())values.put("client_secret",secret);HttpRequest r=HttpRequest.newBuilder(URI.create(TOKEN)).header("Content-Type","application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(form(values))).build();HttpResponse<String> response=http.send(r,HttpResponse.BodyHandlers.ofString());if(response.statusCode()!=200)throw new IOException("Google token exchange failed.");String token=json(response.body(),"access_token");if(token==null)throw new IOException("Google did not return an access token.");return token;}
    private GoogleProfile profile(String token) throws Exception {HttpRequest r=HttpRequest.newBuilder(URI.create(INFO)).header("Authorization","Bearer "+token).GET().build();HttpResponse<String> response=http.send(r,HttpResponse.BodyHandlers.ofString());if(response.statusCode()!=200)throw new IOException("Google profile request failed.");String email=json(response.body(),"email"),name=json(response.body(),"name");if(email==null||email.isBlank())throw new IOException("Google account did not provide an email address.");return new GoogleProfile(name==null||name.isBlank()?email.substring(0,email.indexOf('@')):name,email);}
    private static String required(String key)throws IOException{String x=System.getenv(key);if(x==null||x.isBlank())throw new IOException("Google OAuth is not configured. Set "+key+".");return x;}
    private static String json(String text,String name)throws IOException{try(JsonParser p=new JsonFactory().createParser(text)){while(p.nextToken()!=null)if(p.currentToken()==JsonToken.FIELD_NAME&&name.equals(p.currentName())){p.nextToken();return p.getValueAsString();}}return null;}
    private static Map<String,String> query(String raw){Map<String,String>x=new HashMap<>();if(raw==null)return x;for(String pair:raw.split("&")){String[] p=pair.split("=",2);x.put(decode(p[0]),p.length==2?decode(p[1]):"");}return x;}private static String decode(String s){return URLDecoder.decode(s,StandardCharsets.UTF_8);}private static String form(Map<String,String>x){return x.entrySet().stream().map(e->URLEncoder.encode(e.getKey(),StandardCharsets.UTF_8)+"="+URLEncoder.encode(e.getValue(),StandardCharsets.UTF_8)).collect(java.util.stream.Collectors.joining("&"));}
    public record GoogleProfile(String displayName,String email) { }
}
