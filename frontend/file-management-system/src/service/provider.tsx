import axios from "axios";
import { handleError, handleResponse } from "./response";

const BASE_URL = ' https://be-file-management.herokuapp.com/api/v1/terminal';

export const getData = (query: any) => {
    return axios.get(BASE_URL, {
        params: { query }
    })
        .then(response => handleResponse(response))
        .catch(error => handleError(error.response))
}

export const postData = (input: any) => {
    return axios.post(BASE_URL, input)
        .then(response => handleResponse(response))
        .catch(error => handleError(error.response))
}

export const putData = (input: any) => {
    return axios.put(BASE_URL, input)
        .then(response => handleResponse(response))
        .catch(error => handleError(error.response))
}

export const deleteData = (query: any) => {
    return axios.delete(BASE_URL, {
        params: { query }
    })
        .then(response => handleResponse(response))
        .catch(error => handleError(error.response))
}