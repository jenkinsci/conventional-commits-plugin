package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MavenProjectType extends ProjectType {

    public boolean check(File directory){
        return new File(directory, "pom.xml").exists();
    }

    @Override
    public Version getCurrentVersion(File directory) throws IOException, InterruptedException{

        String os = System.getProperty("os.name");
        String commandName = "mvn";

        if (os.contains("Windows")) {
            commandName += ".cmd";
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                commandName, "help:evaluate",
                "-Dexpression=project.version", "-q", "-DforceStdout"
        );

        processBuilder.directory(directory);
        Process process = processBuilder.start();

        String results = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        process.waitFor();

        return Version.valueOf(results);
    }

}
