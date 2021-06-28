package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class GradleProjectType extends ProjectType{

    public boolean check(File directory){
        return new File(directory, "build.gradle").exists();
    }

    @Override
    public Version getCurrentVersion(File directory) throws IOException, InterruptedException{

        String os = System.getProperty("os.name");
        String commandName = "gradle";

        if (os.contains("Windows")) {
            commandName += ".bat";
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                commandName, "properties", "-q"
        );

        processBuilder.directory(directory);
        Process process = processBuilder.start();

        String results = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        process.waitFor();

        String version = "undefined";

        String[] resultLines = results.split("[\\r\\n]+");
        for (String line: resultLines){
            if (line.startsWith("version:")) {
                String[] words = line.split(" ");
                version = words[1];
                break;
            }
        }
        return Version.valueOf(version);
    }

}
