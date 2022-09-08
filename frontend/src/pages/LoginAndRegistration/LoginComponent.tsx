import React, {useState} from "react";

interface LoginComponentInterface {
    changer: () => void;
    login: (email: string, password: string) => void;
    errorMessage: string | undefined;
}

interface LoginForm {
    email: string;
    password: string;
}

const LoginComponent = (props: LoginComponentInterface) => {
    const initialValues: LoginForm = {email: "", password: ""};
    const [formValues, setFormValues] = useState<LoginForm>(initialValues);
    const [formErrors, setFormErrors] = useState<LoginForm>(initialValues);

    const handleChange = (e: any) => {
        const {name, value} = e.target;
        setFormValues({...formValues, [name]: value});
    }

    const handleSubmit = (e: any) => {
        e.preventDefault();
        let errors = validate(formValues);
        setFormErrors(errors);
        if (errors.email === "" && errors.password === "") {
            props.login(formValues.email, formValues.password);
        }
    }

    /**
     * Validates the login data
     * @param values the login form values
     * @return LoginForm containing error messages
     */
    const validate = (values: LoginForm): LoginForm => {
        const errors = {email: "", password: ""};

        if (!values.email) {
            errors.email = "Email is required!";
        }

        if (!values.password) {
            errors.password = "Password is required!";
        } else if (values.password.length < 6) {
            errors.password = "Your password needs to be longer!"
        }

        return errors;
    }

    return (
        <div className="m-auto" style={{maxWidth: 450}}>

            <form name="loginForm" className="p-4" style={{backgroundColor: "#1a1a1a", borderRadius: 8}}
                  onSubmit={handleSubmit}>

                <h1 className="display-5 text-light mt-3 mb-4 fw-normal" style={{color: "#DDDDDD"}}>Login</h1>

                {props.errorMessage &&
                    <div className="alert alert-danger">{props.errorMessage}</div>
                }

                <div className="form-floating mb-2">

                    <input
                        type="email" name="email"
                        className="form-control text-light"
                        style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                        value={formValues.email}
                        placeholder="email"
                        onChange={handleChange}
                    />

                    <label htmlFor="floatingInput" className="text-light" style={{fontWeight: "normal"}}>Email
                        address</label>
                </div>
                {
                    formErrors.email !== "" &&
                    <p className="text-danger text-start">{formErrors.email}</p>
                }
                <div className="form-floating mt-2">

                    <input
                        type="password" name="password"
                        className="form-control text-light"
                        style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                        value={formValues.password}
                        placeholder="password"
                        onChange={handleChange}
                    />

                    <label htmlFor="floatingPassword" className="text-light"
                           style={{fontWeight: "normal"}}>Password</label>
                </div>
                {
                    formErrors.password !== "" &&
                    <p className="text-danger text-start">{formErrors.password}</p>
                }

                <button className="w-100 btn btn-lg btn-outline-light mt-4 mb-2" type="submit"
                        style={{borderRadius: 5}} unselectable="on">
                    Login
                </button>

                <div className="text-center mt-3 mb-5">
                    <p className="fw-normal text-light">
                        Not a member yet? <br/>
                        <a className="text-decoration-underline text-light" style={{cursor: "pointer"}}
                           onClick={props.changer}>Register now!</a>
                    </p>
                </div>
            </form>
        </div>

    );
}

export default LoginComponent;