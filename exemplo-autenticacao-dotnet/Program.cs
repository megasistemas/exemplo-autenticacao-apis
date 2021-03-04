using Newtonsoft.Json;
using System;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace ExemploAutenticacao
{
    class Program
    {
        private static readonly HttpClient client = new HttpClient();
        private static readonly User user = new User { UserName = "", Password = "" };
        private static readonly string authHost = "";
        private static readonly string endpointApiNegocio = "";

        static async Task Main(string[] args)
        {
            if (!string.IsNullOrEmpty(user.UserName) &&
                !string.IsNullOrEmpty(user.Password) &&
                !string.IsNullOrEmpty(authHost) &&
                !string.IsNullOrEmpty(endpointApiNegocio))
            {
                Token token = await ObterToken();

                if (token is { })
                    token = await RefreshToken(token.RefreshToken);

                if (token is { })
                    await GetApiNegocio(token.AccessToken);
            }
            else
            {
                Console.WriteLine("\nNecessário preencher user, authHost e endpointApiNegocio.\n");
            }
        }

        private static async Task<Token> ObterToken()
        {
            Console.WriteLine("\nObtendo Token...\n");

            var userJson = JsonConvert.SerializeObject(user);
            var body = new StringContent(userJson, Encoding.UTF8, "application/json");

            var response = await client.PostAsync(new Uri($"{authHost}/api/Auth/SignIn"), body).ConfigureAwait(false);
            var content = await response.Content.ReadAsStringAsync();

            Token token = null;
            if (response.IsSuccessStatusCode)
            {
                token = JsonConvert.DeserializeObject<Token>(content);

                Console.WriteLine("Token obtido com sucesso.\n");

                Console.WriteLine($"Token: {token.AccessToken}");
                Console.WriteLine($"Validade Token: {token.ExpirationToken}");
                Console.WriteLine($"Refresh Token: {token.RefreshToken}");
                Console.WriteLine($"Validade Refresh Token: {token.ExpirationRefreshToken}");
            }
            else
            {
                Console.WriteLine($"Mensagem: {content}");
            }

            return token;
        }

        private static async Task<Token> RefreshToken(string refreshToken)
        {
            Console.WriteLine("\nRefresh Token...\n");

            string param = $"refreshToken={refreshToken}";

            var response = await client.PostAsync(new Uri($"{authHost}/api/Auth/RefreshToken?{param}"), null).ConfigureAwait(false);
            var content = await response.Content.ReadAsStringAsync();

            Token token = null;
            if (response.IsSuccessStatusCode)
            {
                token = JsonConvert.DeserializeObject<Token>(content);

                Console.WriteLine("Refresh Token realizado com sucesso.\n");

                Console.WriteLine($"Novo Token: {token.AccessToken}");
                Console.WriteLine($"Validade Token: {token.ExpirationToken}");
                Console.WriteLine($"Novo Refresh Token: {token.RefreshToken}");
                Console.WriteLine($"Validade Refresh Token: {token.ExpirationRefreshToken}");
            }
            else
            {
                Console.WriteLine($"Mensagem: {content}");
            }

            return token;
        }

        private static async Task GetApiNegocio(string token)
        {
            Console.WriteLine("\nGet Api Negócio...\n");

            client.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", token);

            var response = await client.GetAsync(new Uri($"{authHost}{endpointApiNegocio}")).ConfigureAwait(false);

            if (response.IsSuccessStatusCode)
            {
                Console.WriteLine("Chamada da api realizada com sucesso.\n");
            }
            else if (response.StatusCode == HttpStatusCode.Unauthorized)
            {
                Console.WriteLine("Token inválido.");
            }
            else
            {
                Console.WriteLine("Falha ao realizar chamada da api.");
            }
        }
    }

    class Token
    {
        public DateTime ExpirationToken { get; set; }
        public string AccessToken { get; set; }
        public DateTime ExpirationRefreshToken { get; set; }
        public string RefreshToken { get; set; }
    }

    class User
    {
        public string UserName { get; set; }
        public string Password { get; set; }
    }
}
