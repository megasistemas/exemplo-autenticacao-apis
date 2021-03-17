# Exemplo Autenticação APIs

Foi criado 3 exemplos de como consumir a API de autenticação para acesso externo as APIs Mega. Os exemplos foram desenvolvidos em DotNet, Java e Node.

Nos exemplo mostramos como obter o token, como solicitar o refresh do token e como utilizar o token para realizar um request para as APIs de negócio.

## Sobre

- Token JWT.
- Validade do token é de 2 horas.
- Validade do refresh token é de 4 horas.
- Quando é executado o refresh token, é gerado um novo token e um novo refresh token para ser utilizado.
- O usuário pode ter apenas um refresh token valido por vez, ou seja, se exitir um refresh token valido e for gerado um novo token, o refresh token existente será descartado.

### Como executar os exemplos?

Para executar os exemplos é necessário seguir os passos abaixo.
- Configurar a tag <AUTH_HOST> no MegaConfig com o endereço que o NGINX irá configurar a autenticação.
- Reiniciar o MegaConnectionManager.
- Habilitar o acesso externo as APIs no cadastro do usuário Mega que será utilizado.
- Configurar nos exemplos as variaveis globais:
    - **user (UserName e Password)** com os dados do usuário Mega.
    - **authHost** com o endereço configurado na tag AUTH_HOST.
    - **endpointApiNegocio** com o endpoint de uma API de negócio (GET).

**Exemplo MegaConfig**
```xml
<API>
	<HOST>http://localhost:35700</HOST> <!-- Endereço utilizado pelo Mega -->
	<AUTH_HOST>http://localhost:36700</AUTH_HOST> <!-- Endereço utilizado para acesso externo -->
</API>
```

**Exemplo variaveis**
```cs
User user = new User { UserName = "usuario.teste", Password = "123456" };
string authHost = "http://localhost:36700";
string endpointApiNegocio = "/api/global/projeto";
```

### dotnet

- Projeto exemplo-autenticacao-dotnet
- Aplicação desenvolvida através do VSCode.
- Utilizado DotNet Core 3.1

**Como executar?**

Utilizar o comando dotnet run ou executar através do VSCode.

### Node

- Projeto exemplo-autenticacao-node
- Aplicação desenvolvida através do VSCode.
- Utilizado Yarn
- Utilizado TypeScript
- Utilizado Node 10.16.0

**Como executar?**

Utilizar o comando yarn start ou executar através do VSCode.

### Java

- Projeto exemplo-autenticacao-java
- Aplicação desenvolvida através do VSCode.
- Utilizado java 11
- https://code.visualstudio.com/docs/java/java-tutorial

**Como executar?**

Executar através do VSCode.
