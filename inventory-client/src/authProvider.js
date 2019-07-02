// in src/authProvider.js
import { AUTH_LOGIN, AUTH_LOGOUT, AUTH_ERROR, AUTH_CHECK } from 'react-admin';

export default (type, params) => {
    // called when the user attempts to log in
    if (type === AUTH_LOGIN) {
        const { username, password } = params;

        if(process.env.NODE_ENV !== "production") {
            if(username === process.env.REACT_APP_DEMO_LOGIN && password === process.env.REACT_APP_DEMO_PASSWD) {
                localStorage.setItem('username', username);
                return Promise.resolve();
            } else {
                return Promise.reject();
            }
        }
        else {
            const req = new Request('/auth', {
                method: 'POST',
                body: JSON.stringify(params),
                headers: new Headers({ 'Content-Type': 'application/json' })
            });

            return fetch(req)
                .then(response => {
                    if (response.status < 200 || response.status >= 300) {
                        throw new Error(response.statusText);
                    }
                    return response.text();
                })
                .then(( text ) => {
                    if(text === 'OK') {
                        localStorage.setItem('username', username);
                        return Promise.resolve();
                    }

                });
        }
    }
    // called when the user clicks on the logout button
    if (type === AUTH_LOGOUT) {
        localStorage.removeItem('username');
        return Promise.resolve();
    }
    // called when the API returns an error
    if (type === AUTH_ERROR) {
        const { status } = params;
        if (status === 401 || status === 403) {
            localStorage.removeItem('username');
            return Promise.reject();
        }
        return Promise.resolve();
    }
    // called when the user navigates to a new location
    if (type === AUTH_CHECK) {
        return localStorage.getItem('username')
            ? Promise.resolve()
            : Promise.reject();
    }
    return Promise.reject('Unknown method');
};