import axios from 'axios';
import React from 'react';
import { useState, useEffect } from 'react';
import TerminalCustomize, { ColorMode, LineType } from './customizeTerminal/index';

const CommandPromt = (props = {}) => {
  const [currentPath, setCurrentPath] = useState('');
  const [files, setFiles] = useState([]);
  const [folders, setFolders] = useState([]);
  const [terminalLine, setTerminalLine] = useState([]);
  const [currentTerminal, setCurrentTerminal] = useState('');
  const [data, setData] = useState({});
  const [error, setError] = useState('');

  const handleInput = (terminalInput) => {
    if (terminalInput === 'clear') {
      setTerminalLine([]);
      setCurrentPath('');
      return;
    }
    setCurrentTerminal(terminalInput);
  }

  const getCommandLine = async (terminalInput) => {


  }

  useEffect(() => {
    const input = {
      commandLine: currentTerminal,
      path: currentPath.length > 0 ? currentPath : 'root',
    };
    const fetchData = async () => {
      await axios.post('http://localhost:8080/api/v1/', input)
        .then(res => setData(res))
        .then(() => console.log('new data: ', data))
        .catch(error => {
          setError(error);
          showError();
        })
    }
    fetchData();
    
  }, [currentTerminal]);
  // const renderFiles = () => {
  //   let result = [...terminalLine];
  //   files.length > 0 && files.map(it => (
  //     result.push(
  //       { type: LineType.Output, value: it.filePath },
  //       { type: LineType.Input, value: currentTerminal }
  //     )
  //   ));
  //   setTerminalLine(result);
  // };

  // const renderFolders = () => {
  //   let result = [...terminalLine];
  //   folders.length > 0 && folders.map(it => (
  //     result.push(
  //       { type: LineType.Output, value: it.folderPath },
  //       { type: LineType.Input, value: currentTerminal }
  //     )
  //   ));
  //   setTerminalLine(result);
  // };

  const handleCurrentTeminalInput = (terminalInput) => {
    console.log('current terminal: ', currentTerminal);
    const prefix = terminalInput.split(" ")[0];
    switch (prefix) {
      case "ls":
        console.log('ls');
        break;
      case "cat":
        console.log('cat');
        showContentFile(terminalInput);
        break;
      case "find":
        console.log('find');
        break;
      case "cd":
        changeDir(terminalInput);
        break;
      default:
        console.log('default');
        showStatus();
        break;
    }
  }

  const changeDir = (terminalInput) => {
    if (data.status === 200) {
      setCurrentPath(data.data);
    }
  }

  const showContentFile = (terminalInput) => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: currentPath }
    );
    console.log('content: ', data.data);
    result.push(
      { type: LineType.Output, value: data.data }
    );
    setTerminalLine(result);
  }

  const showError = () => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: currentTerminal }
    )
    result.push(
      { type: LineType.Output, value: error },
    )
    setTerminalLine(result);
  }

  const showStatus = () => {
    let result = [...terminalLine];
    result.push(
      { type: LineType.Input, value: currentTerminal }
    )
    result.push(
      { type: LineType.Output, value: data.data },
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
        startingInputValue={currentPath}
      />
    </div>
  )
};

export default CommandPromt;
