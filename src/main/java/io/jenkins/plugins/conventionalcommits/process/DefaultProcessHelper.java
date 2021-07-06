package io.jenkins.plugins.conventionalcommits.process;

import org.apache.commons.lang.NotImplementedException;

public class DefaultProcessHelper implements ProcessHelper {

    @Override
    public String runCommand(String... commandAndArgs) {
        // this should use ProcessBuilder here to run the command and return stdout
        throw new NotImplementedException();
    }
}
