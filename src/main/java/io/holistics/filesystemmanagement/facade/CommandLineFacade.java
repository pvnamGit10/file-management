package io.holistics.filesystemmanagement.facade;

import io.holistics.filesystemmanagement.config.facade.Facade;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.viewResponse.DataResponse;
import io.holistics.filesystemmanagement.service.commandLineService.CommandLineService;
import io.holistics.filesystemmanagement.utils.CommandLineHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Facade
public class CommandLineFacade {
    @Autowired
    private CommandLineHelper commandLineHelper;

    @Autowired
    private CommandLineService commandLineService;

    public DataResponse dispatchGetDataResponse(CommandLineRequest body) {
        String prefix = commandLineHelper.getPrefixCommand(body.getCommandLine());
        switch (prefix) {
            case "cat":
                return commandLineService.displayFile(body);
            case "mv":
                return commandLineService.moveFileOrFolder(body);
            case "cd":
                return commandLineService.changeDirect(body);
            case "find":
                return commandLineService.search(body);
            case "ls":
                return commandLineService.displayFilesAndFolder(body);
            default:
                throw new IllegalArgumentException("terminal incorrect");
        }
    }

    @Transactional
    public DataResponse createFileOrFolder(CommandLineRequest body) {
        return commandLineService.createFileOrFolder(body);
    }

    @Transactional
    public DataResponse updateFileOrFolder(CommandLineRequest body) {
        return commandLineService.updateFileOrFolder(body);
    }

    @Transactional
    public DataResponse removeFileOrFolder(CommandLineRequest body) {
        return commandLineService.removeFileOrFolder(body);
    }
}
