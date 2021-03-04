import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final User user = new User("", "");
    private static final String authHost = "";
    private static final String endpointApiNegocio = "";

    public static void main(String[] args) throws Exception {

        if (!user.UserName.isEmpty() && !user.Password.isEmpty() && !authHost.isEmpty()
                && !endpointApiNegocio.isEmpty()) {
            Token token = ObterToken();

            if (token != null)
                token = RefreshToken(token.refreshToken);

            if (token != null)
                GetApiNegocio(token.accessToken);
        } else {
            System.out.println("\nNecessário preencher os campos user, authHost e endpointApiNegocio.\n");
        }
    }

    private static Token ObterToken() {
        System.out.println("\nObtendo Token...\n");

        try {
            String userJson = mapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/api/Auth/SignIn", authHost)))
                    .header("Content-Type", "application/json").POST(BodyPublishers.ofString(userJson)).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            Token token = null;
            if (response.statusCode() == 200) {
                token = mapper.readValue(response.body(), Token.class);

                System.out.println("Token obtido com sucesso.\n");

                System.out.println(String.format("Token: %s", token.accessToken));
                System.out.println(String.format("Validade: %s", token.expirationToken));
                System.out.println(String.format("Refresh Token: %s", token.refreshToken));
                System.out.println(String.format("Validade Refresh Token: %s \n", token.expirationRefreshToken));
            } else {
                System.out.println(String.format("Mensagem: %s \n", response.body()));
            }

            return token;

        } catch (Exception ex) {
            System.out.println("\nFalha ao realizar chamada da api.\n");
            return new Token();
        }
    }

    private static Token RefreshToken(String refreshToken) {
        System.out.println("Refresh Token...\n");

        try {
            String param = String.format("refreshToken=%s", refreshToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/api/Auth/RefreshToken?%s", authHost, param)))
                    .header("Content-Type", "application/json").POST(BodyPublishers.noBody()).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            Token token = null;
            if (response.statusCode() == 200) {
                token = mapper.readValue(response.body(), Token.class);

                System.out.println("Refresh Token realizado com sucesso.\n");

                System.out.println(String.format("Novo Token: %s", token.accessToken));
                System.out.println(String.format("Validade: %s", token.expirationToken));
                System.out.println(String.format("Novo Refresh Token: %s", token.refreshToken));
                System.out.println(String.format("Validade Refresh Token: %s \n", token.expirationRefreshToken));
            } else {
                System.out.println(String.format("Mensagem: %s \n", response.body()));
            }

            return token;

        } catch (Exception ex) {
            System.out.println("\nFalha ao realizar chamada da api.\n");
            return null;
        }
    }

    private static void GetApiNegocio(String token) {
        System.out.println("Get Api Negócio...\n");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s%s", authHost, endpointApiNegocio)))
                    .header("Authorization", String.format("Bearer %s", token)).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Chamada da api realizada com sucesso.\n");
            } else if (response.statusCode() == 401) {
                System.out.println("Token inválido");
            } else {
                System.out.println("Falha ao realizar chamada da api.\n");
            }
        } catch (Exception ex) {
            System.out.println("Falha ao realizar chamada da api.\n");
        }
    }
}

class User {
    public String UserName;
    public String Password;

    User(String userName, String password) {
        this.UserName = userName;
        this.Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

    public void setName(String userName) {
        this.UserName = userName;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}

class Token {
    public Date expirationToken;
    public String accessToken;
    public Date expirationRefreshToken;
    public String refreshToken;

    public Date getExpirationToken() {
        return expirationToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Date getExpirationRefreshToken() {
        return expirationRefreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setExpirationToken(Date expirationToken) {
        this.expirationToken = expirationToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpirationRefreshToken(Date expirationRefreshToken) {
        this.expirationRefreshToken = expirationRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
