package io.holistics.filesystemmanagement.controller.commanLineController;

import io.holistics.filesystemmanagement.facade.CommandLineFacade;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.errorMessage.ErrorMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@CrossOrigin("http://localhost:3000/")
public class CommandLineController {

    @Autowired
    private CommandLineFacade commandLineFacade;

    @PostMapping("/")
    public ResponseEntity<?> responseApi(@RequestBody CommandLineRequest body) {
        try {
            return ResponseEntity.ok().body(commandLineFacade.responseAPI(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage()));
        }
    }
}
