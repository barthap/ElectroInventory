import { stringify } from 'query-string';
import {
    GET_LIST,
    GET_ONE,
    GET_MANY,
    GET_MANY_REFERENCE,
    CREATE,
    UPDATE,
    UPDATE_MANY,
    DELETE,
    DELETE_MANY,
} from 'react-admin';

import authorizedHttpClient from './utility/httpClient';


const flatten = object => {
    return Object.assign( {}, ...function _flatten( objectBit, path = '' ) {  //spread the result into our return object
        return [].concat(                                                       //concat everything into one level
            ...Object.keys( objectBit ).map(                                      //iterate over object
                key => typeof objectBit[ key ] === 'object' ?                       //check if there is a nested object
                    _flatten( objectBit[ key ], (path === '' ? `${key}` : `${ path }.${ key }`) ) :              //call itself if there is
                    ( { [ (path === '' ? `${key}` : `${ path }.${ key }`) ]: objectBit[ key ] } )                //append object with it’s path as key
            )
        )
    }( object ) );
};





/**
 * Maps react-admin queries to a simple REST API
 *
 * The REST dialect is similar to the one of FakeRest
 * @see https://github.com/marmelab/FakeRest
 * @example
 * GET_LIST     => GET http://my.api.url/posts?sort=['title','ASC']&range=[0, 24]
 * GET_ONE      => GET http://my.api.url/posts/123
 * GET_MANY     => GET http://my.api.url/posts?filter={ids:[123,456,789]}
 * UPDATE       => PUT http://my.api.url/posts/123
 * CREATE       => POST http://my.api.url/posts
 * DELETE       => DELETE http://my.api.url/posts/123
 */
export default (apiUrl, httpClient = authorizedHttpClient) => {
    /**
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The data request params, depending on the type
     * @returns {Object} { url, options } The HTTP request parameters
     */
    const API_VERSION = process.env.REACT_APP_API_VERSION;
    apiUrl = apiUrl + '/' + API_VERSION;


    const convertDataRequestToHTTP = (type, resource, params) => {
        let url = '';
        const options = {};
        switch (type) {
            case GET_LIST: {
                const { page, perPage } = params.pagination;
                const { field, order } = params.sort;

                const filterObject = flatten(params.filter);
                const query = {
                    sort: `${field},${order}`,//JSON.stringify([field, order]),
                    //range: JSON.stringify([(page - 1) * perPage, page * perPage - 1]),
                    page: page,
                    size: perPage,
                    //filter: JSON.stringify(params.filter),
                    ...filterObject
                };
                url = `${apiUrl}/${resource}?${stringify(query)}`;
                break;
            }
            case GET_ONE:
                url = `${apiUrl}/${resource}/${params.id}`;
                break;
            case GET_MANY: {
                const query = {
                    ids: params.ids.join(',')
                };
                console.log("ids", params.ids);
                url = `${apiUrl}/${resource}?${stringify(query)}`;
                break;

            }
            case GET_MANY_REFERENCE: {
                const { page, perPage } = params.pagination;
                const { field, order } = params.sort;
               /* const query = {
                    sort: JSON.stringify([field, order]),
                    range: JSON.stringify([
                        (page - 1) * perPage,
                        page * perPage - 1,
                    ]),
                    filter: JSON.stringify({
                        ...params.filter,
                        [params.target]: params.id,
                    }),
                };*/
                let flat;
                try {
                    flat = flatten(params.filter);
                } catch (e) {
                    flat = {};
                }
                const query = {
                    ...flat,
                    [params.target]: params.id,
                    //_sort: field,
                    //_order: order,
                    //_start: (page - 1) * perPage,
                    //_end: page * perPage,
                    sort: `${field},${order}`,
                    page: page,
                    size: perPage
                };

                console.log(query);

                url = `${apiUrl}/${resource}?${stringify(query)}`;
                break;
            }
            case UPDATE:
                if(resource === 'items') {
                    const cid = params.data.category.id;
                    params.data.categoryId = (cid != null && cid !== "") ? cid : 0;

                    const lid = params.data.location.id;
                    params.data.locationId = (lid != null && lid !== "") ? lid : 0;
                }

                if(resource === 'categories' || resource === 'locations')
                    params.data.parentId = params.data.parent.id;

                if(params.data.parentId === null)
                    params.data.parentId = 0;

                url = `${apiUrl}/${resource}/${params.id}`;
                options.method = 'PUT';
                options.body = JSON.stringify(params.data);
                break;
            case CREATE:
                url = `${apiUrl}/${resource}`;
                options.method = 'POST';
                options.body = JSON.stringify(params.data);
                break;
            case DELETE:
                url = `${apiUrl}/${resource}/${params.id}`;
                options.method = 'DELETE';
                break;
            default:
                throw new Error(`Unsupported fetch action type ${type}`);
        }
        return { url, options };
    };

    /**
     * @param {Object} response HTTP response from fetch()
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The data request params, depending on the type
     * @returns {Object} Data response
     */
    const convertHTTPResponse = (response, type, resource, params) => {
        const { headers, json } = response;
        let data = json;

        switch (type) {
            case GET_LIST:
            case GET_MANY_REFERENCE:
                if (!headers.has('x-total-count')) {
                    throw new Error(
                        'The X-Total-Count header is missing in the HTTP Response. The simple REST data provider expects responses for lists of resources to contain this header with the total number of results to build the pagination. If you are using CORS, did you declare Content-Range in the Access-Control-Expose-Headers header?'
                    );
                }
                if(resource === 'locations' || resource === 'categories') {
                    data = json.map(r => {
                        if (r.parent) return {...r, parent_id: r.parent.id };
                        else return r;
                    });
                }

                console.log(data);

                return {
                    data: data,
                    total: parseInt(
                        headers
                            .get('x-total-count')
                            .split('/')
                            .pop(),
                        10
                    ),
                };
            case CREATE:
                return { data: { ...params.data, id: json.id } };
            default:
                return { data: json };
        }
    };

    /**
     * @param {string} type Request type, e.g GET_LIST
     * @param {string} resource Resource name, e.g. "posts"
     * @param {Object} payload Request parameters. Depends on the request type
     * @returns {Promise} the Promise for a data response
     */
    return (type, resource, params) => {
        // simple-rest doesn't handle filters on UPDATE route, so we fallback to calling UPDATE n times instead
        if (type === UPDATE_MANY) {
            return Promise.all(
                params.ids.map(id =>
                    httpClient(`${apiUrl}/${resource}/${id}`, {
                        method: 'PUT',
                        body: JSON.stringify(params.data),
                    })
                )
            ).then(responses => ({
                data: responses.map(response => response.json),
            }));
        }
        // simple-rest doesn't handle filters on DELETE route, so we fallback to calling DELETE n times instead
        if (type === DELETE_MANY) {
            return Promise.all(
                params.ids.map(id =>
                    httpClient(`${apiUrl}/${resource}/${id}`, {
                        method: 'DELETE',
                    })
                )
            ).then(responses => ({
                data: responses.map(response => response.json),
            }));
        }

        const { url, options } = convertDataRequestToHTTP(
            type,
            resource,
            params
        );
        return httpClient(url, options).then(response =>
            convertHTTPResponse(response, type, resource, params)
        );
    };
};
