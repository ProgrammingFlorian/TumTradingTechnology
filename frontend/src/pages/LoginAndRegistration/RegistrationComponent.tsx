import React, {useState} from "react";

interface RegistrationComponentInterface {
    changer: () => void;
    register: (firstname: string, lastname: string, email: string, password: string) => void;
}

interface RegistrationFormValues {
    firstname: string;
    lastname: string;
    email: string;
    password1: string;
    password2: string;
    agb: boolean;
}

interface RegistrationFormErrors {
    firstname: string;
    lastname: string;
    email: string;
    password1: string;
    password2: string;
    agb: string;
}

const RegistrationComponent = (props: RegistrationComponentInterface) => {

    const initialFormValues: RegistrationFormValues =
        {firstname: "", lastname: "", email: "", password1: "", password2: "", agb: false};
    const initialErrorValues: RegistrationFormErrors =
        {firstname: "", lastname: "", email: "", password1: "", password2: "", agb: ""};

    const [formValues, setFormValues] = useState<RegistrationFormValues>(initialFormValues);
    const [formErrors, setFormErrors] = useState<RegistrationFormErrors>(initialErrorValues);

    const handleChange = (e: any) => {
        const {name, value} = e.target;
        if (name === "agb") {
            setFormValues({...formValues, agb: !formValues.agb});
        } else {
            setFormValues({...formValues, [name]: value});
        }
    }

    const handleSubmit = (e: any) => {
        e.preventDefault();
        let errors = validate(formValues);
        setFormErrors(errors);
        if (errors.firstname === "" && errors.lastname === "" && errors.email === "" &&
            errors.password1 === "" && errors.password2 === "" && errors.agb === "") {
            props.register(formValues.firstname, formValues.lastname, formValues.email, formValues.password1);
        }
    }

    const validate = (values: RegistrationFormValues): RegistrationFormErrors => {
        const errors = initialErrorValues;

        if (!values.firstname) {
            errors.firstname = "First name is required!";
        }

        if (!values.lastname) {
            errors.lastname = "Last name is required!";
        }

        if (!values.email) {
            errors.email = "Email is required!";
        }

        if (!values.password1) {
            errors.password1 = "Password is required!";
        } else if (values.password1.length < 6) {
            errors.password1 = "Your password needs to be longer!";
        }

        if (!values.password2) {
            errors.password2 = "Repeat your password!";
        } else if (values.password2 !== values.password1) {
            errors.password2 = "Your passwords don't match!";
        }

        if (!values.agb) {
            errors.agb = "You have to agree with our polices";
        }

        return errors;
    }

    return (
        <div className="m-auto" style={{maxWidth: 450}}>
            <form name="registrationForm" className="p-4 mt-4" style={{backgroundColor: "#1a1a1a", borderRadius: 8}}
                  onSubmit={handleSubmit}>

                <h1 className="display-5 text-light mt-3 mb-4 fw-normal" style={{color: "#DDDDDD"}}>Registration</h1>

                <div className="form-floating mb-2">
                    <input type="text"
                           name="firstname"
                           className="form-control text-light"
                           style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                           value={formValues.firstname}
                           placeholder="firstname"
                           onChange={handleChange}
                    />
                    <label htmlFor="floatingInput" className="text-light" style={{fontWeight: "normal"}}>
                        First name
                    </label>
                </div>
                {
                    formErrors.firstname !== "" &&
                    <p className="text-danger text-start">{formErrors.firstname}</p>
                }
                <div className="form-floating mb-2">
                    <input type="text"
                           name="lastname"
                           className="form-control text-light"
                           style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                           value={formValues.lastname}
                           placeholder="lastname"
                           onChange={handleChange}
                    />
                    <label htmlFor="floatingInput" className="text-light" style={{fontWeight: "normal"}}>Last
                        name</label>
                </div>
                {
                    formErrors.lastname !== "" &&
                    <p className="text-danger text-start">{formErrors.lastname}</p>
                }
                <div className="form-floating mb-2">
                    <input type="email"
                           name="email"
                           className="form-control text-light"
                           style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                           value={formValues.email}
                           placeholder="email"
                           onChange={handleChange}
                    />
                    <label htmlFor="floatingInput" className="text-light" style={{fontWeight: "normal"}}>
                        Email address
                    </label>
                </div>
                {
                    formErrors.email !== "" &&
                    <p className="text-danger text-start">{formErrors.email}</p>
                }
                <div className="form-floating mt-4">
                    <input type="password"
                           name="password1"
                           className="form-control text-light" id="floatingPassword"
                           style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                           value={formValues.password1}
                           placeholder="password"
                           onChange={handleChange}
                    />
                    <label htmlFor="floatingInput" className="text-light"
                           style={{fontWeight: "normal"}}>Password</label>
                </div>
                {
                    formErrors.password1 !== "" &&
                    <p className="text-danger text-start">{formErrors.password1}</p>
                }
                <div className="form-floating mt-2">
                    <input type="password"
                           name="password2"
                           className="form-control text-light"
                           style={{backgroundColor: "#2d2d2d", borderRadius: 5}}
                           value={formValues.password2}
                           placeholder="password repetition"
                           onChange={handleChange}
                    />
                    <label htmlFor="floatingInput" className="text-light" style={{fontWeight: "normal"}}>Repeat your
                        password</label>
                </div>
                {
                    formErrors.password2 !== "" &&
                    <p className="text-danger text-start">{formErrors.password2}</p>
                }
                <div className="checkbox mt-3">
                    <label className="text-light" style={{fontWeight: "normal", userSelect: "none"}}>
                        <input type="checkbox"
                               name="agb"
                               checked={formValues.agb}
                               onChange={handleChange}
                        />
                        <span style={{marginLeft: "5px"}}>Accept AGBs</span>
                    </label>
                </div>
                {
                    formErrors.agb !== "" &&
                    <p className="text-danger text-start">{formErrors.agb}</p>
                }
                <button className="w-100 btn btn-lg btn-outline-light mt-4 mb-2" type="submit"
                        style={{borderRadius: 5}} unselectable="on">
                    Register
                </button>
                <div className="text-center mt-3 mb-5">
                    <p className="fw-normal text-light">
                        Already a member?<br/>
                        <a className="text-decoration-underline text-light" style={{cursor: "pointer"}}
                           onClick={props.changer}>Log in now!</a>
                    </p>
                </div>
            </form>
        </div>
    );
}

export default RegistrationComponent;