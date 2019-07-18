

const API_URL = `${process.env.REACT_APP_API_URL}/${process.env.REACT_APP_API_VERSION}`;



export function getPhoto(id) {
    const url = `${API_URL}/items/${id}/photo`;
    //
    return fetch(url, {
        method: 'GET',
        cache: 'default',
        headers: {
            "Authorization": 'Basic ' + Buffer.from(process.env.REACT_APP_API_USER + ':' + process.env.REACT_APP_API_PASS).toString('base64')
        }
    }).then(response => {
        //if there is no photo
        if(response.status === 204) {
            return Promise.resolve(null);
        }

        if(response.status === 200) {
            return response.blob()
                .then(buf => Promise.resolve(URL.createObjectURL(buf)))
                .catch(err => Promise.reject(err));
        }
        return Promise.reject("Invalid status code " + response.status);

    }).catch(err => Promise.reject(err));
}

export function uploadPhoto(id, data) {
    const url = `${API_URL}/items/${id}/photo`;
    const form = new FormData();
    form.set("file", data);

    return fetch(url, {
        method: 'POST',
        body: form,
        headers: {
            "Authorization": 'Basic ' + Buffer.from(process.env.REACT_APP_API_USER + ':' + process.env.REACT_APP_API_PASS).toString('base64')
        }
    }).then(response => Promise.resolve(response))
        .catch(err => Promise.reject(err));

}