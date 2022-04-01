import { DataResponse } from "../components/models/DataResponse";

export const handleResponse = (response: any) => {
  if (response.data) {
    const dataResponse: DataResponse = response.data;
    return dataResponse;
  }
  return response;
}

export const handleError = (error: any) => {
  if (error.data) {
    return error.data;
  }
  return error;
}