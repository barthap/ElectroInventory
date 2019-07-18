import { ImageInput } from 'react-admin';
import React from 'react';

export default (props) => {

    const input = {
        onBlur: () => {},
        value: props.record
    };

    return  <ImageInput {...props} input={input} />;
}

