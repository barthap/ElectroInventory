import React from 'react';
import { List, Datagrid, TextField, UrlField, Edit, SimpleForm, Create,
    DisabledInput, TextInput, LongTextInput, NumberInput, NumberField,
    EditButton, DeleteButton, Show, SimpleShowLayout, ReferenceField, ReferenceInput,
    Filter, SelectInput, Toolbar, SaveButton, CloneButton, FunctionField} from 'react-admin';
import MyUrlField from "../ui/MyUrlField";

import get from 'lodash/get';
import GoogleField from "../ui/GoogleField";
import {FilteringReferenceField} from "../ui/FilteringReferenceField";
import CancelButton from "../ui/CancelButton";
import {FieldGroup} from "../ui/FieldGroup";
const BoldTextField = ({ source, record = {} }) => <span><b>{get(record, source)}</b></span>;

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

const DESC_LENGTH = 50;
const ShortDesc = record => {
    if(record.description.length < DESC_LENGTH)
        return record.description;
    else
        return record.description.substr(0,DESC_LENGTH) + "...";
};



const ItemExpand = props => (
    <Show {...props} title="">
        <SimpleShowLayout>
            <TextField source="description"/>
            <ReferenceField source="location.id"
                            reference="locations"
                            label="Location"
                            linkType="show"
                            allowEmpty>
                <TextField source="fullName"/>
            </ReferenceField>
            <MyUrlField source="website"/>
            <NumberField source="quantity"/>
            <TextField source="id"/>
        </SimpleShowLayout>
    </Show>
);

export const ItemList = props => (
    <List {...props} filters={<ItemFilter/>} sort={{field: 'name', order: 'ASC'}}>
        <Datagrid expand={<ItemExpand/>} rowClick="expand">

            <BoldTextField source="name"/>
            <FilteringReferenceField source="category.id"
                                     reference="categories"
                                     label="Category"
                                     linkType="show"
                                     allowEmpty/>

            <FunctionField label="Description" render={ShortDesc} />
            <GoogleField/>
            <FilteringReferenceField source="location.id"
                                     reference="locations"
                                     label="Location"
                                     linkType="show"
                                     allowEmpty
            />
            <FieldGroup>
                <CloneButton/>
                <EditButton/>
                <DeleteButton/>
            </FieldGroup>
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
            <ReferenceField source="location.id" reference="locations" label="Location" linkType="show" allowEmpty>
                <TextField source="fullName"/>
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
            label="Save and exit"
            redirect="list"
            submitOnEnter={false}
            variant="flat"
        />
        <CancelButton />
    </Toolbar>
);

export const ItemCreate = props => (
    <Create {...props}>
        <SimpleForm toolbar={<ItemCreateToolbar/>} redirect="list">
            <TextInput source="name" />
            <ReferenceInput source="categoryId" reference="categories" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
            <ReferenceInput source="locationId" reference="locations" allowEmpty>
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
            <ReferenceInput source="location.id" reference="locations" label="Location" allowEmpty>
                <SelectInput optionText="name" />
            </ReferenceInput>
            <NumberInput source="quantity"/>
            <TextInput source="website"/>
            <LongTextInput source="description" />
        </SimpleForm>
    </Edit>
);