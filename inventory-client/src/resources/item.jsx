import React from 'react';
import { List, Datagrid, TextField, UrlField, Edit, SimpleForm, Create,
    DisabledInput, TextInput, LongTextInput, NumberInput, NumberField,
    EditButton, DeleteButton, Show, SimpleShowLayout, ReferenceField, ReferenceInput,
    Filter, SelectInput, Toolbar, SaveButton, CloneButton} from 'react-admin';

const ItemTitle = ({record}) => {
    return <span>Element: {record ? `${record.name}` : ''}</span>
};

const ItemFilter = props => (
    <Filter {...props}>
        <TextInput label="Search" source="q" alwaysOn/>
        <ReferenceInput label="Category" source="category.id" reference="categories" allowEmpty>
            <SelectInput optionText="name"/>
        </ReferenceInput>
    </Filter>
);

export const ItemList = props => (
    <List {...props} filters={<ItemFilter/>} sort={{field: 'name', order: 'ASC'}}>
        <Datagrid rowClick="show">
            <TextField source="id"/>
            <TextField source="name"/>
            <ReferenceField source="category.id" reference="categories" label="Category" linkType="show" allowEmpty>
                <TextField source="name"/>
            </ReferenceField>
            <NumberField source="quantity"/>
            <UrlField source="website"/>
            <CloneButton/>
            <EditButton/>
            <DeleteButton/>
        </Datagrid>
    </List>
);

export const ItemShow = props => (
    <Show {...props} title={<ItemTitle/>}>
        <SimpleShowLayout>
            <TextField source="name"/>
            <ReferenceField source="category.id" reference="categories" label="Category" linkType="show">
                <TextField source="name"/>
            </ReferenceField>
            <NumberField source="quantity"/>
            <UrlField source="website"/>
            <TextField source="description"/>
        </SimpleShowLayout>
    </Show>
);


const ItemCreateToolbar = props => (
    <Toolbar {...props} >
        <SaveButton
            label="Save and next"
            redirect={false}
            submitOnEnter={true}
        />
        <SaveButton
            label="Save and show"
            redirect="show"
            submitOnEnter={false}
            variant="flat"
        />
    </Toolbar>
);

export const ItemCreate = props => (
    <Create {...props}>
        <SimpleForm toolbar={<ItemCreateToolbar/>} redirect="list">
            <TextInput source="name" />
            <ReferenceInput source="categoryId" reference="categories" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
            <NumberInput source="quantity"/>
            <TextInput source="website"/>
            <LongTextInput source="description" />
        </SimpleForm>
    </Create>
);

export const ItemEdit = props => (
    <Edit {...props} title={<ItemTitle/>}>
        <SimpleForm>
            <DisabledInput source="id" />
            <TextInput source="name" />
            <ReferenceInput source="category.id" reference="categories" label="Category" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
            <NumberInput source="quantity"/>
            <TextInput source="website"/>
            <LongTextInput source="description" />
        </SimpleForm>
    </Edit>
);