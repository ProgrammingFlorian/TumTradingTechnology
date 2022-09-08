import {UserDTO} from "../models/UserDTO";
import {Requests} from "../common/requests";
import {apiRegisterRoute} from "../common/apiRoutes";
import {RegistrationDTO} from "../models/RegistrationDTO";

const register = (firstname: string, lastname: string, email: string, password: string): Promise<UserDTO> => {
    const data: RegistrationDTO = {
        username: email,
        password
    };

    return Requests.unfilteredPostRequest<UserDTO>(apiRegisterRoute(), data);
};

const getCash = (): Promise<number> => {
    return Requests.getRequest<number>("users/cash");
}

export const UserService = {
    register,
    getCash
};