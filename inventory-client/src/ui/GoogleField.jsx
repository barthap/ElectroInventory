// in src/MyUrlField.js
import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import SearchIcon from '@material-ui/icons/Search';

const styles = {
    link: {
        textDecoration: 'none',
    },
    icon: {
        width: '0.5em',
        paddingLeft: 2,
    },
};

const createUrl = phrase => {
    const str = phrase.trim().replace(' ', '+');
    return `https://google.com/search?q=${str}`;
};

const GoogleField = ({ record = {}, source='name', classes }) =>
    <a href={createUrl(record[source])}
       className={classes.link}
       target="_blank"
    rel="noopener noreferrer">
        <SearchIcon className={classes.icon} />
        Google
    </a>;

export default withStyles(styles)(GoogleField);