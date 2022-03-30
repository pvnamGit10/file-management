package io.holistics.filesystemmanagement.facade;

import io.holistics.filesystemmanagement.config.facade.Facade;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.viewResponse.ViewResponse;
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

    @Transactional
    public String responseAPI(CommandLineRequest body) {
        String prefix = commandLineHelper.getPrefixCommand(body.getCommandLine());
        switch (prefix) {
            case "cr":
                commandLineService.createFileOrFolder(body);
                break;
            case "rm":
                commandLineService.removeFile(body);
                break;
            case "cat":
                return commandLineService.displayFile(body);
            case "up":
                commandLineService.updateFileOrFolder(body);
                break;
            case "mv":
                commandLineService.moveFileOrFolder(body);
                break;
            default:
                break;
        }
        return "success";
    }

    public ViewResponse responseListAPI(CommandLineRequest body) {
        String prefix = commandLineHelper.getPrefixCommand(body.getCommandLine());
        switch (prefix) {
            case "find":
                return commandLineService.search(body);
            case "ls":
                return commandLineService.displayFilesAndFolder(body);
            default:
                return null;
        }
    }
}
