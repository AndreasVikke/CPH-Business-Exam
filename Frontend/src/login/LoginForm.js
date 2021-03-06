import React, { useState, useEffect } from 'react';
import Facade from './ApiFacade';
import Login from './Login';
import LoggedIn from './LoggedIn';
import RecipeDetails from '../Pages/RecipeDetails';

export default function App({ page }) {
    const [loggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState();

    useEffect(() => {
        Facade.checkLoggedIn().then(res => setLoggedIn(true)).catch(e => setLoggedIn(false));
    }, [])

    const logout = () => { 
        Facade.logout();
        setLoggedIn(false);
    }

    const login = (user, pass) => { 
        Facade.login(user,pass)
        .then(res => {setLoggedIn(true); setError("")})
        .catch(e => setError("Wrong username or password"));
    }

    const register = (user, pass, pass2) => {
        if(pass === pass2) {
            Facade.register(user,pass)
            .then(res => {setLoggedIn(true); setError("")})
            .catch(e => setError("Something went wrong"));
        } else {
            setError("Passwords don't match")
        }
    }

    return (
        <div>
            {!loggedIn ? 
                (<div>
                <Login login={login} register={register} />
                <p>{error}</p>
                </div>) : (<div>
                    {page !== "productDetails" ? <LoggedIn /> : <RecipeDetails />}
                    <button className="btn btn-primary" onClick={logout}>Logout</button>
                </div>)}
        </div>
    )
}