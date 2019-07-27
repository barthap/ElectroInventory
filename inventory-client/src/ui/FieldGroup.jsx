import React from 'react';

export const FieldGroup = props => (
    <React.Fragment><div>
        {React.Children.map(props.children, child => {
                return React.cloneElement(child, props)
        })
        }
    </div>
    </React.Fragment>
);

