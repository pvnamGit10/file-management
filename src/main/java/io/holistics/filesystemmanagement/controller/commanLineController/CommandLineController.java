package io.holistics.filesystemmanagement.controller.commanLineController;

import io.holistics.filesystemmanagement.facade.CommandLineFacade;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.utils.CommandLineHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class CommandLineController {

    @Autowired
    private CommandLineFacade commandLineFacade;

    @Autowired
    private CommandLineHelper commandLineHelper;

    @PostMapping("/")
    public ResponseEntity<?> responseApi(@RequestBody CommandLineRequest body){
        try {
            String prefix = commandLineHelper.getPrefixCommand(body.getCommandLine());
            if (prefix.equals("ls") || prefix.equals("find")) {
                return ResponseEntity.ok().body(commandLineFacade.responseListAPI(body));
            }
            return ResponseEntity.ok().body(commandLineFacade.responseAPI(body));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
