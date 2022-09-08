import React, {useState} from "react";
import LoginComponent from "./LoginComponent";
import RegistrationComponent from "./RegistrationComponent";
import {AuthenticationService} from "../../services/AuthenticationService";
import {pageDashboard} from "../../common/pageRoutes";
import {useNavigate} from "react-router-dom";
import {UserService} from "../../services/UserService";
import axios, {AxiosError} from "axios";

const LoginAndRegistrationContainer = () => {
    const navigate = useNavigate();
    const [showLoginPage, setShowLoginPage] = useState<boolean>(true);
    const [loginError, setLoginError] = useState<string>();

    const togglePage = () => {
        setShowLoginPage(!showLoginPage);
    }

    const login = (email: string, password: string) => {
        // clear error
        setLoginError(undefined);

        AuthenticationService.login(email, password).then(() => {
            navigate(pageDashboard())
        }).catch((error) => {
            if (axios.isAxiosError(error)) {
                const axiosError = error as AxiosError;
                if (axiosError.response?.status === 401) {
                    // Probably wrong password
                    setLoginError('Wrong username/password!');
                } else {
                    setLoginError(`Error: ${axiosError.message}`);
                }
            } else {
                setLoginError(`Error: ${error.message}`);
            }
        });
    }

    const register = (firstname: string, lastname: string, email: string, password: string) => {
        UserService.register(firstname, lastname, email, password).then(() => {
            // Alternative: Show alert that the user user.username has been registered instead of logging in
            // After successful registration, login the user
            login(email, password);
        }).catch((err) => {
            // TODO: Add alerts to registration box, show an error alert
            console.log(err);
        })
    }

    return (
        <div className="container h-100 align-items-center">
            <div className="row align-self-center text-center">
                {showLoginPage ?
                    <LoginAndBox changer={togglePage} login={login} errorMessage={loginError}/> :
                    <RegistrationAndBox changer={togglePage} register={register}/>
                }
            </div>
        </div>
    );
}

interface LoginProps {
    changer: () => void;
    login: (email: string, password: string) => void;
    errorMessage: string | undefined;
}

const LoginAndBox = (props: LoginProps) => {
    return (
        <div className="container">
            <div className="d-md-none mx-4">
                <div className="pt-3 pb-4">
                    <LoginTextBox isLogin={true}/>
                </div>
                <LoginComponent changer={props.changer} login={props.login} errorMessage={props.errorMessage}/>
            </div>
            <div className="d-none d-md-block d-xl-none">
                <div className="row mx-auto my-5 py-5 ">
                    <div className="col-6">
                        <LoginTextBox isLogin={true}/>
                    </div>
                    <div className="col-6 text-center align-middle">
                        <LoginComponent changer={props.changer} login={props.login} errorMessage={props.errorMessage}/>
                    </div>
                </div>
            </div>
            <div className="d-none d-xl-block">
                <div className="row mx-auto my-5 py-5">
                    <div className="col-8">
                        <LoginTextBox isLogin={true}/>
                    </div>
                    <div className="col-4 text-center align-middle">
                        <LoginComponent changer={props.changer} login={props.login} errorMessage={props.errorMessage}/>
                    </div>
                </div>
            </div>
        </div>
    );
}

interface RegistrationProps {
    changer: () => void;
    register: (firstname: string, lastname: string, email: string, password: string) => void;
}

const RegistrationAndBox = (props: RegistrationProps) => {
    return (
        <div className="container">
            <div className="d-md-none mx-4">
                <div className="pt-3 pb-2">
                    <LoginTextBox isLogin={false}/>
                </div>
                <RegistrationComponent changer={props.changer} register={props.register}/>
            </div>
            <div className="d-none d-md-block d-xl-none">
                <div className="row mx-auto my-5 py-5 ">
                    <div className="col-6">
                        <LoginTextBox isLogin={false}/>
                    </div>
                    <div className="col-6 text-center align-middle">
                        <RegistrationComponent changer={props.changer} register={props.register}/>
                    </div>
                </div>
            </div>
            <div className="d-none d-xl-block">
                <div className="row mx-auto ">
                    <div className="col-8">
                        <LoginTextBox isLogin={false}/>
                    </div>
                    <div className="col-4 text-center align-middle">
                        <RegistrationComponent changer={props.changer} register={props.register}/>
                    </div>
                </div>
            </div>
        </div>
    );
}

interface LoginTextBoxProps {
    isLogin: boolean;
}

const LoginTextBox = (props: LoginTextBoxProps) => {
    return (
        <div className="w-100 h-100 align-middle">
            <div className="d-md-none text-center align-middle">
                <p className="display-6 text-light">{props.isLogin ? "Welcome back to" : "Welcome to"}</p>
                <h2 className="display-5 text-light align-middle">TUM Trading Technologies</h2>
                <p className="lead text-light align-middle">Learn and improve investing.</p>
            </div>
            <div className="d-none d-md-block text-center align-middle">
                <p className="display-6 text-light"
                   style={{paddingTop: "180px"}}>{props.isLogin ? "Welcome back to" : "Welcome to"}</p>
                <h2 className="display-5 text-light align-middle">TUM Trading Technologies</h2>
                <p className="lead text-light align-middle">Learn and improve investing.</p>
            </div>
        </div>
    );
}


export default LoginAndRegistrationContainer;