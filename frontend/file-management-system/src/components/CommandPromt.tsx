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

const CommandPromt = () => {
  const [currentPath, setCurrentPath] = useState('root');
  const [terminalLine, setTerminalLine] = useState<LineData[]>([]);

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
