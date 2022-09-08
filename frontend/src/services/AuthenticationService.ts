import {Requests} from "../common/requests";
import {TokenDTO} from "../models/TokenDTO";
import {apiLoginRoute} from "../common/apiRoutes";

export const TOKEN_SESSION_NAME = 'token';

/**
 * Logs in the user.
 *
 * @param username username
 * @param password password
 * @return whether or not the login was successful.
 */
const login = (username: string, password: string): Promise<void> => {
    return Requests.unfilteredPostRequest<TokenDTO>(apiLoginRoute(), {
        username,
        password
    }).then((response) => {
        localStorage.setItem(TOKEN_SESSION_NAME, response.token);
        return Promise.resolve();
    });
}

const logout = (): void => {
    localStorage.removeItem(TOKEN_SESSION_NAME);
    Requests.logout();
}

const isLoggedIn = (): boolean => {
    return localStorage.getItem(TOKEN_SESSION_NAME) !== null;
}

const getToken = (): string => {
    let token = localStorage.getItem(TOKEN_SESSION_NAME);
    if (token) {
        return token;
    } else {
        throw new Error('Token not found');
    }
}

export const AuthenticationService = {
    login,
    logout,
    isLoggedIn,
    getToken
}