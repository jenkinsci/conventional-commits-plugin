package io.jenkins.plugins.conventionalcommits.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zafarkhaja.semver.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

/**
 * Class to interact with a NPM project type.
 */
public class NpmProjectType extends ProjectType {
    // Name of the package.json file
    private final static String PACKAGE_JSON_NAME = "package.json";

    /**
     * Check if the project is a NPM project (i.e a package.json is found).
     *
     * @param directory The directory of the project.
     * @return True if a package.json is found.
     */
    @Override
    public boolean check(File directory) {
        return new File(directory, PACKAGE_JSON_NAME).exists();
    }

    /**
     * Return the next version of the version attribute.
     *
     * @param directory The project's directory.
     * @return The next calculated version (based on Semver).
     * @throws IOException If an error occur reading files.
     */
    @Override
    public Version getCurrentVersion(File directory) throws IOException {
        Objects.requireNonNull(directory);

        ObjectMapper mapper = new ObjectMapper();

        // Convert package.json to a Map
        Map<?, ?> map = mapper.readValue(Paths.get(directory.getPath() + File.separator + PACKAGE_JSON_NAME).toFile(), Map.class);

        return Version.valueOf((String) map.get("version"));
    }
}
