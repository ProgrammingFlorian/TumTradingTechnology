/**
 * File to organize all page routes instead of hardcoding them every time.
 * All routes should begin with / and end without / to ensure not double or missing /.
 */

// Authorization
const authRoute = `/auth`

export const apiLoginRoute = (): string => {
    return `${authRoute}/login`;
}

export const apiRegisterRoute = (): string => {
    return `${authRoute}/register`;
}