import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import compose from 'recompose/compose';
import Button from '@material-ui/core/Button';
import { withStyles, createStyles } from '@material-ui/core/styles';
import CancelIcon from '@material-ui/icons/Cancel';
import classnames from 'classnames';
import { translate } from 'react-admin';

const styles = ({ spacing }) =>
    createStyles({
        button: {
            position: 'relative',
        },
        leftIcon: {
            marginRight: spacing.unit,
        },
        icon: {
            fontSize: 18,
        },
    });

const sanitizeRestProps = ({
                               basePath,
                               className,
                               classes,
                               saving,
                               label,
                               invalid,
                               variant,
                               translate,
                               handleSubmit,
                               handleSubmitWithRedirect,
                               submitOnEnter,
                               record,
                               redirect,
                               resource,
                               locale,
                               showNotification,
                               undoable,
                               ...rest
                           }) => rest;

export class CancelButton extends Component {
    static propTypes = {
        className: PropTypes.string,
        classes: PropTypes.object,
        doRedirect: PropTypes.func,
        invalid: PropTypes.bool,
        label: PropTypes.string,
        pristine: PropTypes.bool,
        redirect: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.bool,
            PropTypes.func,
        ]),
        showNotification: PropTypes.func,
        submitOnEnter: PropTypes.bool,
        translate: PropTypes.func,
        variant: PropTypes.oneOf(['raised', 'flat', 'fab']),
        icon: PropTypes.element,
    };

    static defaultProps = {
        icon: <CancelIcon />,
    };

    handleClick = e => {
        const {
            doRedirect,
            resource,
            record,
            basePath,
            redirect,
            onClick,
        } = this.props;

            // always submit form explicitly regardless of button type
        if (e) {
            e.preventDefault();
        }
        doRedirect(resource, record, basePath, redirect);


        if (typeof onClick === 'function') {
            onClick();
        }
    };

    render() {
        const {
            className,
            classes = {},
            invalid,
            label = 'ra.action.cancel',
            pristine,
            redirect,
            variant = 'flat',
            icon,
            translate,
            onClick,
            ...rest
        } = this.props;

        return (
            <Button
                className={classnames(classes.button, className)}
                variant={variant}
                type="button"
                onClick={this.handleClick}
                color="default"
                {...sanitizeRestProps(rest)}
            >
                {
                    React.cloneElement(icon, {
                        className: classnames(classes.leftIcon, classes.icon),
                    })
                }
                {label && translate(label, { _: label })}
            </Button>
        );
    }
}

const enhance = compose(
    translate,
    connect(
        (state) => {},
        { doRedirect: (
                resource,
                data,
                basePath,
                redirectTo = 'list'
            ) => ({
                type: 'REDIRECT',
                payload: { data },
                meta: {
                    basePath,
                    redirectTo
                }
            })}
    ),
    withStyles(styles)
);

export default enhance(CancelButton);
