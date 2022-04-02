package io.holistics.filesystemmanagement.controller.commanLineController;

import io.holistics.filesystemmanagement.facade.CommandLineFacade;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.errorMessage.ErrorMessageResponse;
import io.holistics.filesystemmanagement.utils.QueryConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@CrossOrigin("https://io-file-management-system.herokuapp.com/")
public class CommandLineController {

    @Autowired
    private CommandLineFacade commandLineFacade;

    @Autowired
    private QueryConverter queryConverter;

    @PostMapping("/terminal")
    public ResponseEntity<?> createFileOrFolder(@RequestBody CommandLineRequest body) {
        try {
            return ResponseEntity.ok().body(commandLineFacade.createFileOrFolder(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage(), "failed"));
        }
    }

    @GetMapping("/terminal")
    public ResponseEntity<?> getDataResponse(@RequestParam("query") String query) {
        try {
            CommandLineRequest body = queryConverter.convertQuery(query);
            return ResponseEntity.ok().body(commandLineFacade.dispatchGetDataResponse(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage(), "failed"));
        }
    }

    @PutMapping("/terminal")
    public ResponseEntity<?> updateFileOrFolder(@RequestBody CommandLineRequest body) {
        try {
            return ResponseEntity.ok().body(commandLineFacade.updateFileOrFolder(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage(), "failed"));
        }
    }

    @DeleteMapping("/terminal")
    public ResponseEntity<?> removeFileOrFolder(@RequestParam String query) {
        try {
            CommandLineRequest body = queryConverter.convertQuery(query);
            return ResponseEntity.ok().body(commandLineFacade.removeFileOrFolder(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage(), "failed"));
        }
    }
}
