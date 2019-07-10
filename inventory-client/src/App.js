// in src/App.js
import React from 'react';
import { Admin, Resource, mergeTranslations } from 'react-admin';
import restProvider from './restProvider';
import {ItemCreate, ItemEdit, ItemList} from "./resources/item";
import {CategoryCreate, CategoryTree} from "./resources/category";
import ItemsIcon from '@material-ui/icons/Memory';
import LocationsIcon from '@material-ui/icons/PinDrop'
import Home from "./Home";
import { createMuiTheme } from '@material-ui/core/styles';
import lightGreen from '@material-ui/core/colors/lightGreen';
import authProvider from "./authProvider";
import {LocationCreate, LocationTree} from "./resources/location";

//tree imports
import { reducer as tree } from 'ra-tree-ui-materialui';
import englishMessages from 'ra-language-english';
import treeEnglishMessages from 'ra-tree-language-english';

// Tree View support
const messages = {
    'en': mergeTranslations(englishMessages, treeEnglishMessages),
};
const i18nProvider = locale => messages[locale];


//API provider
const API_URL = process.env.REACT_APP_API_URL;
const dataProvider = restProvider(API_URL);

// UI Material theme
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
    return (<Admin dataProvider={dataProvider}
                   dashboard={Home}
                   theme={theme}
                   authProvider={authProvider}
                   i18nProvider={i18nProvider}
                   locale="en"
                   customReducers={{tree}}>

        <Resource name="items" list={ItemList} edit={ItemEdit} create={ItemCreate} icon={ItemsIcon}/>
        <Resource name="categories" list={CategoryTree} create={CategoryCreate}/>
        <Resource name="locations" list={LocationTree} create={LocationCreate} icon={LocationsIcon}/>
    </Admin>);
}

export default App;
