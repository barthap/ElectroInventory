// in src/App.js
import React from 'react';
import { fetchUtils, Admin, Resource } from 'react-admin';
import restProvider from './restProvider';
import {ItemCreate, ItemEdit, ItemList, ItemShow} from "./resources/item";
import {CategoryCreate, CategoryEdit, CategoryList, CategoryShow} from "./resources/category";
import ItemsIcon from '@material-ui/icons/Memory';
import Home from "./Home";
import { createMuiTheme } from '@material-ui/core/styles';
import lightGreen from '@material-ui/core/colors/lightGreen';
import authProvider from "./authProvider";

const API_URL = process.env.REACT_APP_API_URL;
//process.env.NODE_ENV === "production" ? 'https://inventory.hapex2.com.pl/api' : 'http://localhost:8081/api';

const httpClient = (url, options = {}) => {
    /*if (!options.headers) {
        options.headers = new Headers({ Accept: 'application/json' });
    }
    // add your own headers here
    options.headers.set('Authorization', 'Basic ZGVtbzpkZW1v');
    */
    options.user = {
        authenticated: true,
        token: 'Basic ' + Buffer.from(process.env.REACT_APP_API_USER + ':' + process.env.REACT_APP_API_PASS).toString('base64')
    }

    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restProvider(API_URL, httpClient);
//const dataProvider = jsonServerProvider('http://jsonplaceholder.typicode.com');

const theme = createMuiTheme({
    palette: {
        primary: lightGreen,
        secondary: {
            main: '#70c326',
            contrastText: '#FFFFFF'
        }
    }
});

function App() {
    return (<Admin dataProvider={dataProvider} dashboard={Home} theme={theme} authProvider={authProvider}>
        <Resource name="items" list={ItemList} edit={ItemEdit} create={ItemCreate} show={ItemShow} icon={ItemsIcon}/>
        <Resource name="categories" list={CategoryList} edit={CategoryEdit} create={CategoryCreate} show={CategoryShow}/>
        </Admin>);
}

export default App;
