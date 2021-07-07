package io.jenkins.plugins.conventionalcommits.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ProcessHelper {
    String runProcessBuilder(File directory, List<String> command) throws IOException, InterruptedException;
}
