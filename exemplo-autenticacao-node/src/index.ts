import axios from 'axios';

const user: User = { userName: "", password: "" };
const authHost = "";
const endpointApiNegocio = "";

start();

async function start() {
    if (user.userName && user.password && authHost && endpointApiNegocio) {
        let token: Token;
        token = await ObterToken();

        if (token.refreshToken)
            token = await RefreshToken(token.refreshToken);

        if (token.accessToken)
            await GetApiNegocio(token.accessToken);
    } else {
        console.log('\nNecessário preencher user, authHost e endpointApiNegocio.\n');
    }
}

async function ObterToken() {
    console.log("\nObtendo Token...\n");

    let token = <Token>{};
    try {
        const response = await axios.post<Token>(`${authHost}/api/Auth/SignIn`, user);
        token = response.data

        console.log("Token obtido com sucesso.\n");

        console.log(`Token: ${token.accessToken}`);
        console.log(`Validade Token: ${token.expirationToken}`);
        console.log(`Refresh Token: ${token.refreshToken}`);
        console.log(`Validade Refresh Token: ${token.expirationRefreshToken}`);
    } catch (e) {
        console.log(`Mensagem: ${e.response.data}\n`);
    }

    return token;
}

async function RefreshToken(refreshToken: string) {
    console.log("\nRefresh Token...\n");

    let token = <Token>{};
    try {
        const param = `refreshToken=${refreshToken}`;
        const response = await axios.post<Token>(`${authHost}/api/Auth/RefreshToken?${param}`);
        token = response.data;

        console.log("Refresh Token realizado com sucesso.\n");

        console.log(`Novo Token: ${token.accessToken}`);
        console.log(`Validade Token: ${token.expirationToken}`);
        console.log(`Novo Refresh Token: ${token.refreshToken}`);
        console.log(`Validade Refresh Token: ${token.expirationRefreshToken}`);
    } catch (e) {
        console.log(`Mensagem: ${e.response.data}`);
    }

    return token;
}

async function GetApiNegocio(token: string) {
    console.log("\nGet Api Negócio...\n");

    try {
        const response = await axios.get(`${authHost}${endpointApiNegocio}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

        console.log("Chamada da api realizada com sucesso.\n");
    } catch (ex) {
        if (ex.response.status == 401) {
            console.log("Token inválido");
        } else {
            console.log("Falha ao realizar chamada da api.\n");
        }
    }
}

export interface Token {
    expirationToken: Date
    accessToken: string
    expirationRefreshToken: Date
    refreshToken: string
}

export interface User {
    userName: string;
    password: string;
}