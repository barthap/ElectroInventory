import React from 'react';
import { List, Datagrid, TextField, Edit, SimpleForm, Create,
    DisabledInput, TextInput, SaveButton,
    EditButton, DeleteButton, Show, SimpleShowLayout, ReferenceField,
    ReferenceManyField, SelectInput, ReferenceInput} from 'react-admin';
import {IgnoreFormProps, NodeActions, NodeForm, Tree} from "ra-tree-ui-materialui";

const CategoryTreesActions = props => (
    <NodeActions {...props}>
        <SaveButton variant="flat"/>
        <IgnoreFormProps>
            <DeleteButton />
        </IgnoreFormProps>
    </NodeActions>
);
export const CategoryTree = props => (
    <List {...props} perPage={1000}>
        <Tree enableDragAndDrop allowDropOnRoot>
            <NodeForm actions={<CategoryTreesActions/>}>
                <TextInput source="name"/>
            </NodeForm>
        </Tree>
    </List>
);


export const CategoryCreate = props => (
    <Create {...props}>
        <SimpleForm>
            <TextInput source="name" />
            <ReferenceInput source="parentId" reference="categories" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
        </SimpleForm>
    </Create>
);


//Unused legacy components for List view

const CategoryTitle = ({record}) => {
    return <span>Category: {record ? `${record.name}` : ''}</span>
};

export const CategoryList = props => (
    <List {...props}>
        <Datagrid rowClick="show">
            <TextField source="id"/>
            <TextField source="name"/>
            <ReferenceField source="parent.id" reference="categories" label="Parent" linkType="show" allowEmpty>
                <TextField source="name"/>
            </ReferenceField>
            <EditButton/>
            <DeleteButton/>
        </Datagrid>
    </List>
);

export const CategoryShow = props => (
    <Show {...props} title={<CategoryTitle/>}>
        <SimpleShowLayout>
            <TextField source="name"/>
            <ReferenceField source="parent.id" reference="categories" label="Parent" linkType="show" allowEmpty>
                <TextField source="name"/>
            </ReferenceField>
            <ReferenceManyField label="Subcategories" reference="categories" target="parent.id">
                <Datagrid rowClick="show">
                    <TextField source="id"/>
                    <TextField source="name"/>
                </Datagrid>
            </ReferenceManyField>
        </SimpleShowLayout>
    </Show>
);

export const CategoryEdit = props => (
    <Edit {...props} title={<CategoryTitle/>}>
        <SimpleForm>
            <DisabledInput source="id" />
            <TextInput source="name" />
            <ReferenceInput source="parent.id" reference="categories" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
        </SimpleForm>
    </Edit>
);

