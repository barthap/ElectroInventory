import React from "react";
import {getPhoto} from "../utility/photoFetcher";
import { withStyles, createStyles } from '@material-ui/core/styles';
import get from 'lodash/get';

const STATE_LOADING = 'loading';
const STATE_NONE = 'none';
const STATE_FETCHED = 'fetched';
const STATE_ERROR = 'err';


const styles = createStyles({
    list: {
        display: 'flex',
        listStyleType: 'none',
    },
    image: {
        margin: '0.5rem',
        maxHeight: '10rem',
    },
});

class PhotoPreview extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            image: null,
            status: STATE_LOADING
        };
        this.fetchPhoto = this.fetchPhoto.bind(this);
    }

    componentDidMount() {
        this.fetchPhoto();
    }

    componentWillReceiveProps(nextProps, nextContext) {
        const { record, source } = nextProps;
        const sourceValue = get(record, source);
        if (sourceValue) {
            this.setState({
                status: STATE_FETCHED,
                image: sourceValue
            });
        }
    }

    render() {

        switch (this.state.status) {
            case STATE_NONE:
                return <b>Item has no photo</b>;
            case STATE_LOADING:
                return <b>Loading...</b>;
            case STATE_FETCHED:
                return (
                    <div>
                        <img className={this.props.classes.image}
                            alt={this.props.record.name} src={this.state.image}/>
                </div>);
            case STATE_ERROR:
            default:
                return <b>Error while fetching image!</b>;
        }
    }


    fetchPhoto() {
        const { record, source } = this.props;

        const sourceValue = get(record, source);
        if (sourceValue) {
            console.log("sourceValue", sourceValue);
            this.setState({
                status: STATE_FETCHED,
                image: sourceValue
            });
            return;
        }

        getPhoto(record.id).then(imageData => {
            if(imageData === null) {
                this.setState({
                    status: STATE_NONE
                });
            } else {
                this.setState({
                    status: STATE_FETCHED,
                    image: imageData
                });
            }
        }).catch(err => {
            console.error(err);
            this.setState({
                status: STATE_ERROR
            });
        });
    }
}

export default withStyles(styles)(PhotoPreview);