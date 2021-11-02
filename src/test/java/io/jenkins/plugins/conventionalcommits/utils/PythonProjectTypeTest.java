package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PythonProjectTypeTest {
    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Mock
    private ProcessHelper mockProcessHelper;

    private void createPythonCfg(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "setup.cfg"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.cfg");
        String configContent =
                "[metadata]\n"+
                "name = myName\n" +
                "version = " + version + "\n"+
                "author = EG";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }

    private void createPythonCfgWithIdent(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "setup.cfg"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.cfg");
        String configContent =
                "[metadata]\n"+
                        "   name = myName\n" +
                        "   version = " + version + "\n"+
                        "   author = EG";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }

    private void createSetupPy(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "setup.py"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.py");
        String configContent =
                "[metadata]\n"+
                        "name = myName\n" +
                        (StringUtils.isNotBlank(version) ? "version = " + version + "\n" : "")+
                        "author = EG";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }

    private void createSetupPyWithIndent(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "setup.py"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.py");
        String configContent =
                "setup(\n" +
                        "    name='example',\n" +
                        "   version= "+ version + ",\n" +
                        "    packages=find_packages(include=['exampleproject', 'exampleproject.*'])\n" +
                        ")";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }

    private void createTomlPy(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "pyproject.toml"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "pyproject.toml");
        String configContent =
                "[project]\n"+
                        "name = \"infer_pyproject\"\n" +
                        "version = \"" + version + "\"\n"+
                        "author = [\"EG<foo@foo.com>\"]";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }

    private void createTomlPyWithIdent(File pyDir, String version) throws Exception {
        Files.deleteIfExists(Paths.get(pyDir.getPath() + File.separator + "pyproject.toml"));
        File pyCfg = rootFolder.newFile(pyDir.getName() + File.separator + "pyproject.toml");
        String configContent =
                "[project]\n"+
                        "   name = \"infer_pyproject\"\n" +
                        "   version = \"" + version + "\"\n"+
                        "   author = EG";
        FileWriter pyWriter = new FileWriter(pyCfg);
        pyWriter.write(configContent);
        pyWriter.close();
    }


    @Test
    public void shouldGetCurrentVersionForASetupPy() throws Exception {
        // Given a Python project with a setup.py
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createSetupPy(pyDir, "0.9.0");
        when(mockProcessHelper.runProcessBuilder(any(), any())).thenReturn("0.9.0");
        
        // Asking to have the current version of the project
        PythonProjectType pyProjectType = new PythonProjectType();
        Version readVersion = pyProjectType.getCurrentVersion(pyDir, mockProcessHelper);

        // The current version is returned
        assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.9.0")));
    }

    @Test
    public void shouldGetCurrentVersionForASetupCfg() throws Exception {
        // Given a Python project with a setup.cfg
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createPythonCfg(pyDir, "0.9.0");
        
        // Asking to have the current version of the project
        PythonProjectType pyProjectType = new PythonProjectType();
        Version readVersion = pyProjectType.getCurrentVersion(pyDir, mockProcessHelper);

        // The current version is returned
        assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.9.0")));
    }

    @Test
    public void shouldGetCurrentVersionForASetupToml() throws Exception {
        // Given a Python project with a pyproject.toml
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createTomlPy(pyDir, "0.9.0");
        
        // Asking to have the current version of the project
        PythonProjectType pyProjectType = new PythonProjectType();
        Version readVersion = pyProjectType.getCurrentVersion(pyDir, mockProcessHelper);

        // The current version is returned
        assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.9.0")));
    }
    
    @Test
    public void shouldGetCurrentVersionWithSeveralConfigFilesWithEmptyValues() throws Exception {
        // Given a Python project with a pyproject.toml (with version) and a setup.py (without version)
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createTomlPy(pyDir, "0.9.0");
        createSetupPy(pyDir, null);

        // Asking to have the current version of the project
        PythonProjectType pyProjectType = new PythonProjectType();
        Version readVersion = pyProjectType.getCurrentVersion(pyDir, mockProcessHelper);

        // The current version is returned
        assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.9.0")));
    }

    @Test
    public void shouldGetCurrentVersionWithSeveralConfigFilesFirstConfigFileMatche() throws Exception {
        // Given a Python project with a pyproject.toml (with version) and a setup.py (without version)
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createTomlPy(pyDir, "0.9.0");
        createSetupPy(pyDir, "0.8.0");
        when(mockProcessHelper.runProcessBuilder(any(), any())).thenReturn("0.8.0");

        // Asking to have the current version of the project
        PythonProjectType pyProjectType = new PythonProjectType();
        Version readVersion = pyProjectType.getCurrentVersion(pyDir, mockProcessHelper);

        // The current version is returned
        assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.8.0")));
    }


    @Test
    public void shouldWriteVersionBack() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createSetupPy(pyDir, "0.9.0");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("1.0.0"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "setup.py"))),
                containsString("1.0.0"));
    }

    @Test
    public void shouldWriteVersionBackWhenIdentedFile() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");

        createSetupPyWithIndent(pyDir, "1.0.1");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("1.1.0"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "setup.py"))),
                containsString("1.1.0"));
    }

    @Test
    public void shouldWriteVersionBackCfgFile() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createPythonCfg(pyDir, "0.9.0");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("1.0.0"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "setup.cfg"))),
                containsString("1.0.0"));
    }

    @Test
    public void shouldWriteVersionBackCfgFileWhenIdentedFile() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createPythonCfgWithIdent(pyDir, "2.9.0");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("2.9.1"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "setup.cfg"))),
                containsString("2.9.1"));
    }

    @Test
    public void shouldWriteVersionBackTomlFileWhenQuote() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createTomlPy(pyDir, "0.9.0");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("1.0.0"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "pyproject.toml"))),
                containsString("\"1.0.0\""));
    }

    @Test
    public void shouldWriteVersionBackTomlFileWhenQuoteAndIdentedFile() throws Exception {
        // Set python project
        File pyDir = rootFolder.newFolder("SamplePyProject");
        createTomlPyWithIdent(pyDir, "0.9.0");

        PythonProjectType pyProjectType = new PythonProjectType();

        pyProjectType.writeVersion(pyDir, Version.valueOf("1.0.0"), mockProcessHelper);

        assertThat(new String(
                        Files.readAllBytes(Paths.get(pyDir.getPath() + File.separator + "pyproject.toml"))),
                containsString("\"1.0.0\""));
    }
}
