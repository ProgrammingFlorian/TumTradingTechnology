/**
 * File to organize all page routes instead of hardcoding them every time.
 * All routes should begin with / and end without / to ensure not double or missing /.
 */

// Authentication

export const pageLoginRoute = (): string => {
    return `/login`;
}

// Pages

export const pageDashboard = (): string => {
    return `/`;
}

export const pageSearch = (query: string): string => {
    return `/search/${query}`;
}

// Shares

export const pageShareDetailRoute = (shareId: string): string => {
    return `/share/${shareId}`;
}