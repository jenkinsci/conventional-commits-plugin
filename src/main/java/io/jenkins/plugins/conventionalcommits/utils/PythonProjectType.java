package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import org.apache.commons.lang.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PythonProjectType extends ProjectType {

    private boolean checkSetupPy(File directory) {
        return new File(directory, "setup.py").exists();
    }

    private boolean checkSetupCfg(File directory) {
        return new File(directory, "setup.cfg").exists();
    }

    private boolean checkPyProjectTOML(File directory) {
        return new File(directory, "pyproject.toml").exists();
    }

    @Override
    public boolean check(File directory) {
        return checkSetupCfg(directory) || checkSetupPy(directory) || checkPyProjectTOML(directory);
    }

    @Override
    public Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException, InterruptedException {

        String os = System.getProperty("os.name");
        String commandName = "python";

        if (!os.contains("Windows")) {
            commandName += "3";
        }

        String results = "";

        if (checkSetupPy(directory)) {
            List<String> command = Arrays.asList(commandName, "setup.py", "--version");
            results = processHelper.runProcessBuilder(directory, command).trim();
        } else if (checkSetupCfg(directory)) {

            String filePath = directory.getAbsolutePath() + File.separator + "setup.cfg";
            File setupCfg = new File(filePath);
            Scanner scanner = new Scanner(setupCfg, StandardCharsets.UTF_8.name());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (    line.toLowerCase().startsWith("version ") ||
                        line.toLowerCase().startsWith("version")) {
                    String[] words = line.split("=");
                    results = words[1].trim();
                    break;
                }
            }

        } else if (checkPyProjectTOML(directory)) {
            throw new NotImplementedException("Support for pyproject.toml is not implemented yet.");
        }

        return Version.valueOf(results);
    }
}
