package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class PhpProjectTypeTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock
  private ProcessHelper mockProcessHelper;

  private void createComposerJson(File phpDir) throws Exception {
    Files.deleteIfExists(Paths.get(phpDir.getPath() + File.separator + "composer.json"));
    File composerJson = rootFolder.newFile(phpDir.getName() + File.separator + "composer.json");
    String configContent = "{\"name\":\"someentity/example\",\"minimum-stability\":\"dev\",\"authors\":[{\"name\":\"SomeProgrammer\",\"email\":\"some.programmer@company.com\"}],\"version\":\"0.10.0\",\"require\":{\"composer/installers\":\"^1.0.20\",\"drupal/file_browser\":\"dev-1.x\",\"drupal/entity_embed\":\"dev-1.x\",\"drupal/entity_browser\":\"dev-1.x\",\"drupal/dropzonejs\":\"dev-1.x\",\"enyo/dropzone\":\"4.2.0\",\"desandro/masonry\":\"3.3.1\",\"desandro/imagesloaded\":\"3.1.8\"}}";
    FileWriter phpWriter = new FileWriter(composerJson);
    phpWriter.write(configContent);
    phpWriter.close();
  }

  @Test
  public void shouldGetCurrentVersionForAComposerJson() throws Exception {
    // Given a PHP project with a composer.json
    File phpDir = rootFolder.newFolder("SamplePhpProject");
    createComposerJson(phpDir);
    Mockito.lenient().when(mockProcessHelper.runProcessBuilder(any(), any())).thenReturn(
        "0.10.0"
    );

    // Asking to have the current version of the project
    PhpProjectType phpProjectType = new PhpProjectType();
    Version readVersion = phpProjectType.getCurrentVersion(phpDir, mockProcessHelper);

    // The current version is returned
    assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.10.0")));
  }
}
