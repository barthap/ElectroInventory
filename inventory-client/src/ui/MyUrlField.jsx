// in src/MyUrlField.js
import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import LaunchIcon from '@material-ui/icons/Launch';

const styles = {
    link: {
        textDecoration: 'none',
    },
    icon: {
        width: '0.5em',
        paddingLeft: 2,
    },
};

const simplifyUrl = url => {
    if(url == null)
        return null;
    const startIndex = url.indexOf('://') + 3;
    const subs = url.substr(startIndex);
    const endIndex = subs.indexOf('/');
    if(endIndex > 0)
        return subs.substr(0, endIndex);
    else return subs;
};

const MyUrlField = ({ record = {}, source, classes }) =>
    <a href={record[source]} className={classes.link}>
        {simplifyUrl(record[source])}
        {record[source] != null ? <LaunchIcon className={classes.icon} /> : ''}
    </a>;

export default withStyles(styles)(MyUrlField);