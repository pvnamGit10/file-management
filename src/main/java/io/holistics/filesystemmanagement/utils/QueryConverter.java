package io.holistics.filesystemmanagement.utils;

import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import org.springframework.stereotype.Service;

@Service
public class QueryConverter {

    public CommandLineRequest convertQuery(String query) {
        query = query.replace("\"", "");
        query = query.replace("}", "");
        query = query.replace("{", "");
        String[] splitQuery = query.split(",");
        CommandLineRequest body = new CommandLineRequest();
        body.setCommandLine(splitQuery[0].split(":")[1]);
        body.setPath(splitQuery[1].split(":")[1]);
        return body;
    }
}
