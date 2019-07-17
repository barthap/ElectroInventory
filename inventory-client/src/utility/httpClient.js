import { fetchUtils } from 'react-admin';

export default (url, options = {}) => {
    console.log(url);
    options.user = {
        authenticated: true,
        token: 'Basic ' + Buffer.from(process.env.REACT_APP_API_USER + ':' + process.env.REACT_APP_API_PASS).toString('base64')
    };

    return fetchUtils.fetchJson(url, options);
};
