package io.jenkins.plugins.conventionalcommits.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface ProcessHelper {
    default String runProcessBuilder(File directory, List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        processBuilder.directory(directory);
        Process process = processBuilder.start();

        String results = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        process.waitFor();

        return results;
    };
}
