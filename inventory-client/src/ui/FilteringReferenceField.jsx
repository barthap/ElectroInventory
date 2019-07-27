import React from 'react';
import {  ReferenceField, Link } from 'react-admin';
import set from 'lodash/set';
import { stringify } from 'query-string';

const FilterLinkField = ({ record, referenceSource, displaySource, referencePath }) => {
    if(record && record.id ) {
        const filter = set({}, referenceSource, record.id);
        return (
                <Link
                    to={{
                        pathname: `${referencePath}`,
                        search: stringify({
                            page: 1,
                            perPage: 25,
                            sort: 'name',
                            order: 'ASC',
                            filter: JSON.stringify(filter)
                        })
                    }}
                    onClick={event=>event.stopPropagation()}
                >
                    {record[displaySource]}
                </Link>
        );
    }
    return null;
};

export const FilteringReferenceField = props => {
    const {record, basePath, source, displaySource='name'} = props;
    return (
        <ReferenceField {...props}>
            <FilterLinkField referenceRecord={record}
                             referenceSource={source}
                             referencePath={basePath}
                             displaySource={displaySource}/>
        </ReferenceField>
    );
};