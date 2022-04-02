// import axios from 'axios';
import React from 'react';
import { useState } from 'react';
import { deleteData, getData, postData, putData } from '../service/provider';
import { renderFiles, renderFolders } from '../utils/Utils';
import TerminalCustomize, { ColorMode, LineData, LineType } from './customizeTerminal/index';
import { FileReponse } from './models/Files';
import { FolderResponse } from './models/Folder';

interface ResponseData {
  files: FileReponse[];
  folders: FolderResponse[];
  content: string;
  status: string;
}

const welcomeGuide: LineData[] =
  [
    { type: LineType.Output, value: 'Welcome to file management system' },
    { type: LineType.Output, value: 'Type `clear` to clear console' },
    { type: LineType.Output, value: 'Type `help` to get guideline' },
  ];

const help: LineData[] =
  [
    { type: LineType.Output, value: 'Supporting the following commands ' },
    { type: LineType.Output, value: 'Note that after prefix command, PATH, FILE_PATH, FOLDER_PATH must be kept between " ` "' },
    { type: LineType.Output, value: 'Note that DATA must be kept between " `[(your data here)]` "' },
    { type: LineType.Output, value: 'Example: cd `root/demo`' },
    { type: LineType.Output, value: 'The correct direction :example "folder1/folder2" ' },
    { type: LineType.Output, value: 'cd FOLDER_PATH: change current working directory/folder to the specified FOLDER' },
    { type: LineType.Output, value: 'cr [-p] PATH [DATA]: create a new file (if DATA is specified, otherwise create a new folder) at the specified PATH' },
    { type: LineType.Output, value: 'ls [FOLDER_PATH]: list out all items directly under a folder' },
    { type: LineType.Output, value: 'find NAME [FOLDER_PATH]  search all files/folders whose name contains the substring NAME. If the optional param FOLDER_PATH is specified, find in the folder at FOLDER_PATH. ' },
    { type: LineType.Output, value: 'up PATH NAME [DATA] update the file/folder at PATH to have new NAME and, optionally, new DATA' },
    { type: LineType.Output, value: 'mv PATH FOLDER_PATH move a file/folder at PATH into the destination FOLDER_PATH' },
    { type: LineType.Output, value: 'rm PATH [PATH2 PATH3...]: remove files/folders at the specified PATH(s)' },

  ];



const CommandPromt = () => {
  const [currentPath, setCurrentPath] = useState('root');
  const [terminalLine, setTerminalLine] = useState<LineData[]>(welcomeGuide);

  const handleInput = (inputString: string) => {
    let terminalInput = inputString.trim();
    if (inputString.substring(0, inputString.lastIndexOf(':')).includes(currentPath)) {
      terminalInput = inputString.substring(inputString.indexOf(':') + 1);
    }
    if (terminalInput === '..') {
      setCurrentPath(currentPath.substring(0, currentPath.lastIndexOf('/')));
      return;
    }
    if (terminalInput === '/') {
      setCurrentPath('root');
      return;
    }

    if (terminalInput === '.') {
      return;
    }
    if (terminalInput === 'clear') {
      setTerminalLine([]);
      return;
    }

    if (terminalInput === 'help') {
      setTerminalLine(help);
      return;
    }

    const input = {
      commandLine: terminalInput,
      path: currentPath.length > 0 ? currentPath : 'root',
    };

    const prefix = terminalInput.split(" ")[0];

    switch (prefix) {
      case 'cr':
        creatFileOrFolder(input, terminalInput);
        break;
      case 'up':
        updateFileOrFolder(input, terminalInput);
        break;
      case 'mv':
        updateFileOrFolder(input, terminalInput);
        break;
      case 'rm':
        deleteFileOrFolder(input, terminalInput);
        break;
      default:
        getDataReponse(input, terminalInput);
        break;
    }
  }

  const getDataReponse = async (input: any, terminalInput: string) => {
    const res = await getData(input);
    if (res.status === "success") {
      handleGetDataReponse(res, terminalInput);
    } else {
      showError(res, terminalInput);
    }
  }

  const creatFileOrFolder = async (input: any, terminalInput: string) => {
    const res = await postData(input);
    if (res.status === "success") {
      showStatus(res, terminalInput);
    } else {
      showError(res, terminalInput);
    }
  }

  const updateFileOrFolder = async (input: any, terminalInput: string) => {
    const res = await putData(input);
    if (res.status === "success") {
      showStatus(res, terminalInput);
    } else {
      showError(res, terminalInput);
    }
  }


  const deleteFileOrFolder = async (input: any, terminalInput: string) => {
    const res = await deleteData(input);
    if (res.status === "success") {
      showStatus(res, terminalInput);
    } else {
      showError(res, terminalInput);
    }
  }

  const handleGetDataReponse = (res: ResponseData, terminalInput: string) => {
    const prefix = terminalInput.split(" ")[0];
    switch (prefix) {
      case "ls":
        handleOutputList(res, terminalInput);
        break;
      case "cat":
        showContentFile(res, terminalInput);
        break;
      case "find":
        handleFind(res, terminalInput);
        break;
      case "cd":
        changeDirect(res);
        break;
      default:
        showStatus(res, terminalInput);
        break;
    }
  }

  const changeDirect = (res: ResponseData) => {
    if (res.status === 'success') {
      setCurrentPath(res.content);
    }
  }

  const showContentFile = (res: any, terminalInput: string) => {
    let result = [...terminalLine];

    result.push(
      { type: LineType.Input, value: currentPath.concat(': ').concat(terminalInput) }
    );
    result.push(
      { type: LineType.Output, value: res.content }
    );
    setTerminalLine(result);
  }

  const handleFind = (res: ResponseData, terminalInput: string) => {
    let result = [...terminalLine];

    result.push(
      { type: LineType.Input, value: currentPath.concat(': ').concat(terminalInput) }
    );

    const files = getFiles(res.files);
    if (files.length > 0) {
      const header = 'Name  | Filepath  | Size  | Create at';
      result.push(
        { type: LineType.Output, value: 'FILES' },
        { type: LineType.Output, value: header }
      );
      files.map(it => result.push(it))
    }

    const folders = getFolders(res.folders);
    if (folders.length > 0) {
      const header = 'Name  | Filepath  | Parent path  | Size  | Create at';
      result.push(
        { type: LineType.Output, value: 'FOLDERS' },
        { type: LineType.Output, value: header }
      );
      folders.map(it => result.push(it))
    }

    setTerminalLine(result);
  }

  const handleOutputList = (res: ResponseData, terminalInput: string) => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: currentPath.concat(': ').concat(terminalInput) }
    );
    const files = getFiles(res.files);
    if (files.length > 0) {
      const header = 'Name  | Filepath  | Size  | Create at';
      result.push(
        { type: LineType.Output, value: 'FILES' },
        { type: LineType.Output, value: header }
      );
      files.map(it => result.push(it))
    }

    const folders = getFolders(res.folders);
    if (folders.length > 0) {
      const header = 'Name  | Filepath  | Parent path  | Size  | Create at';
      result.push(
        { type: LineType.Output, value: 'FOLDERS' },
        { type: LineType.Output, value: header }
      );
      folders.map(it => result.push(it))
    }

    setTerminalLine(result);

  }

  const getFiles = (files: FileReponse[]): LineData[] => {
    let result: LineData[] = [];
    if (files.length > 0) {
      files.map(it => (
        result.push(
          { type: LineType.Output, value: renderFiles(it) }
        )
      ))
    }
    return result;
  }

  const getFolders = (files: FolderResponse[]): LineData[] => {
    let result: LineData[] = [];
    if (files.length > 0) {
      files.map(it => (
        result.push(
          { type: LineType.Output, value: renderFolders(it) },
        )
      ))
    }
    return result;
  }

  const showError = (error: any, terminalInput: string) => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: terminalInput }
    )
    result.push(
      { type: LineType.Output, value: error.message },
    )
    setTerminalLine(result);
  }

  const showStatus = (res: ResponseData, terminalInput: string) => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: currentPath.concat(': ').concat(terminalInput) }
    )
    result.push(
      { type: LineType.Output, value: res.status },
    )
    setTerminalLine(result);
  }

  // Terminal has 100% width by default so it should usually be wrapped in a container div
  return (
    <div className="container">
      <TerminalCustomize name='File Management System'
        colorMode={ColorMode.Dark}
        lineData={terminalLine}
        onInput={(terminalInput) => {
          handleInput(terminalInput)
        }}
        startingInputValue={currentPath.concat(': ')}
      />
    </div>
  )
};

export default CommandPromt;
