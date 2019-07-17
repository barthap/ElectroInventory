import React from 'react';
import { List, SimpleForm, Create,
    TextInput, DeleteButton,
    SelectInput, ReferenceInput, SaveButton} from 'react-admin';
import {IgnoreFormProps, NodeActions, NodeForm, Tree} from "ra-tree-ui-materialui";


const LocationTreesActions = props => (
    <NodeActions {...props}>
        <SaveButton variant="flat"/>
        <IgnoreFormProps>
            <DeleteButton />
        </IgnoreFormProps>
    </NodeActions>
);
export const LocationTree = props => (
    <List {...props} perPage={1000}>
        <Tree enableDragAndDrop allowDropOnRoot>
            <NodeForm actions={<LocationTreesActions/>}>
                <TextInput source="name"/>
            </NodeForm>
        </Tree>
    </List>
);
export const LocationCreate = props => (
    <Create {...props}>
        <SimpleForm>
            <TextInput source="name" />
            <ReferenceInput source="parentId" reference="locations" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
        </SimpleForm>
    </Create>
);