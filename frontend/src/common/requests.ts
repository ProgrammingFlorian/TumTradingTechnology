import axios, {AxiosError, AxiosResponse} from "axios";
import {AuthenticationService} from "../services/AuthenticationService";

// File to handle all api calls.
// Wrapper for axios for easier api calls and token injector for authentication.
// Works closely with AuthenticationService to get the token and logout the user if unauthorized calls happen.

// Axios instances

/**
 * Axios to be used for authorized requests.
 * Interceptor will be attached or error thrown when not logged in.
 */
const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-type': 'application/json',
    }
});

/**
 * Unfiltered axios. No interceptor will be attached.
 * Used for login and public endpoints.
 */
const httpUnfiltered = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-type': 'application/json',
    }
});

// Helper functions

/**
 * Helper function to easily parse response.
 */
const parseResponse = <T>(response: AxiosResponse<T>): Promise<T> => {
    let data = response.data;
    if (data) {
        return Promise.resolve(data);
    } else {
        return Promise.reject('Could not parse response');
    }
}

/**
 * Checks if server responded with 401 unauthorized to log out the user.
 * Ensure faulty or expired sessions lead to redirect to login instead of broken requests.
 * If permissions are introduced (and are not using 403) or 401 can happen for other reasons this has to be reworked.
 */
const catchUnauthorized = <T>(error: any): Promise<T> => {
    if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response?.status === 401) {
            AuthenticationService.logout();
        }
    }
    // continue to pass error down
    return Promise<T>.reject(error);
}

// Setup for normal requests

let currentInterceptor: number | null = null;

const getAuthorizationHeader = (): string => {
    return `Bearer ${AuthenticationService.getToken()}`;

}

const setupInterceptor = () => {
    currentInterceptor = http.interceptors.request.use((config) => {
        // @ts-ignore
        config.headers.authorization = getAuthorizationHeader();
        return config;
    });
}

/**
 * Checks if a token will be injected into the request.
 * If not and the users is logged in, interceptor will be setup.
 */
const ensureToken = (): boolean => {
    if (currentInterceptor === null) {
        if (AuthenticationService.isLoggedIn()) {
            setupInterceptor();
        } else {
            return false;
        }
    }
    return true;

}

/**
 * To be called when logging out.
 * Interceptor will be ejected.
 */
const logout = () => {
    if (currentInterceptor !== null) {
        http.interceptors.request.eject(currentInterceptor);
        currentInterceptor = null;
    }
}

// Unfiltered requests

const unfilteredGetRequest = <T>(url: string): Promise<T> => {
    return httpUnfiltered.get<T>(url).then(parseResponse);
}

const unfilteredPostRequest = <T>(url: string, data: any): Promise<T> => {
    return httpUnfiltered.post<T>(url, data).then(parseResponse);
}

// Normal requests

const getRequest = <T>(url: string): Promise<T> => {
    if (!ensureToken()) {
        return Promise.reject('Not logged in');
    }

    return http.get<T>(url).then(parseResponse).catch(catchUnauthorized<T>);
}

const postRequest = <T>(url: string, data: any): Promise<T> => {
    if (!ensureToken()) {
        return Promise.reject('Not logged in');
    }

    return http.post<T>(url, data).then(parseResponse).catch(catchUnauthorized<T>);
}

// Export
export const Requests = {
    unfilteredGetRequest,
    unfilteredPostRequest,

    getRequest,
    postRequest,
    logout
};