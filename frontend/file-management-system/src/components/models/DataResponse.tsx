import { FileReponse } from "./Files";
import { FolderResponse } from "./Folder";

export interface DataResponse {
    files: FileReponse[];
    folders: FolderResponse[];
    content: string;
    status: string;
  }