import React from 'react';
import httpClient from '../utility/httpClient';
import Button from '@material-ui/core/Button';

export default class ServerStatus extends React.Component {

    constructor() {
        super();

        this.state = {
            serverStatus: 'Loading...'
        };

        this.fetchStatus = this.fetchStatus.bind(this);
    }

    componentDidMount() {
        this.fetchStatus();
    }

    fetchStatus() {
        const API_URL = process.env.REACT_APP_API_URL;
        this.setState({
            serverStatus: 'Loading...'
        });

        httpClient(API_URL + '/actuator/health').then(response => {
            const { /*headers,*/ json } = response;
            this.setState({
                serverStatus: json.status
            });

        }).catch(err => {
            this.setState({
                serverStatus: 'Connection Error!'
            });
        })
    }

    render() {
        return (
            <div>
                <p>API Server status is: <b>{this.state.serverStatus}</b></p>
                <Button variant="contained" onClick={this.fetchStatus}>Refresh</Button>
            </div>
        );
    }
}