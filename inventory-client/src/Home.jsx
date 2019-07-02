// in src/Dashboard.js
import React from 'react';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';

export default () => (
    <Card>
        <CardHeader title="Welcome to the Inventory" />
        <CardContent>
            <p>You are running a {process.env.NODE_ENV} build of client app!</p>
            <p>Your <b>API URL</b> is {process.env.REACT_APP_API_URL}.</p>

        </CardContent>
    </Card>
);