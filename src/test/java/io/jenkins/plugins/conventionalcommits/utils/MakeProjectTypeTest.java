package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MakeProjectTypeTest {
	@Mock
	private ProcessHelper mockProcessHelper;

	@Rule
	final public TemporaryFolder rootFolder = new TemporaryFolder();

	final private String makefileWithVersionContentType1 = "TARGET = app\n" + "VERSION = 1.0.0 \n"
			+ "INCLUDES = -I./include\n" + "CXXFLAGS = -O2 -Wall $(INCLUDES)\n" + "all: $(TARGET)";
	
	final private String makefileWithVersionContentType2 = "TARGET = app\n" + "VERSION := 1.0.0 \n"
			+ "INCLUDES = -I./include\n" + "CXXFLAGS = -O2 -Wall $(INCLUDES)\n" + "all: $(TARGET)";
	
	final private String makefileWithoutVersionContent = "TARGET = app\n" + "INCLUDES = -I./include\n"
			+ "CXXFLAGS = -O2 -Wall $(INCLUDES)\n" + "all: $(TARGET)";

	private void createMakefiles(File makeProjectDir, String content) throws Exception {
		File makefile = rootFolder.newFile(makeProjectDir.getName() + File.separator + "Makefile");
		FileWriter makefileWriter = new FileWriter(makefile);
		makefileWriter.write(content);
		makefileWriter.close();

	}

	@Test
	public void shouldCheckMakeProjectOk() throws Exception {
		// Given a directory with a build.gradle
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");
		createMakefiles(makeProjectDir, makefileWithVersionContentType1);

		// When asking if it's a make project
		MakeProjectType makeProjectType = new MakeProjectType();
		boolean isMakeProject = makeProjectType.check(makeProjectDir);

		// Then answer true
		assertThat(isMakeProject, equalTo(true));
	}

	@Test
	public void shouldCheckMakeProjectKo() throws Exception {
		// Given a directory with a build.gradle
		File makeProjectDir = rootFolder.newFolder("SampleFooProject");

		// When asking if it's a make project
		MakeProjectType makeProjectType = new MakeProjectType();
		boolean isMakeProject = makeProjectType.check(makeProjectDir);

		// Then answer true
		assertThat(isMakeProject, equalTo(false));
	}

	@Test
	public void shouldGetCurrentVersionType1() throws Exception {
		// Given a make project in 1.0.0 version
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");
		createMakefiles(makeProjectDir, makefileWithVersionContentType1);

		// When asking to get the current version
		MakeProjectType makeProjectType = new MakeProjectType();
		Version version = makeProjectType.getCurrentVersion(makeProjectDir, mockProcessHelper);
	
		// Then answer 1.0.0
		assertThat(version, equalTo(Version.valueOf("1.0.0")));
	}
	
	@Test
	public void shouldGetCurrentVersionType2() throws Exception {
		// Given a make project in 1.0.0 version
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");
		createMakefiles(makeProjectDir, makefileWithVersionContentType2);

		// When asking to get the current version
		MakeProjectType makeProjectType = new MakeProjectType();
		Version version = makeProjectType.getCurrentVersion(makeProjectDir, mockProcessHelper);
	
		// Then answer 1.0.0
		assertThat(version, equalTo(Version.valueOf("1.0.0")));
	}

	@Test
	public void shouldWriteNextVersionToFile() throws Exception {
		// Given a directory with a Makefile
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");
		createMakefiles(makeProjectDir, makefileWithVersionContentType1);

		// When : write next version to the file
		MakeProjectType makeProjectType = new MakeProjectType();
		makeProjectType.writeVersion(makeProjectDir, Version.valueOf("1.1.0"), null);

		// Then : the file is updated
		assertThat(new String(Files.readAllBytes(Paths.get(makeProjectDir.getPath() + File.separator + "Makefile"))),
				containsString("1.1.0"));
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionIfNoBuildFile() throws Exception {
		// Given : a project without a build.file
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");

		// When : ask to write next version in file
		MakeProjectType makeProjectType = new MakeProjectType();
		makeProjectType.writeVersion(makeProjectDir, Version.valueOf("1.1.0"), null);

		// Then : IOException is thrown
	}

	@Test(expected = IOException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoVersionTag() throws Exception {
		// Given a directory with a gradle.build file
		File makeProjectDir = rootFolder.newFolder("SampleMakeProject");
		createMakefiles(makeProjectDir, makefileWithoutVersionContent);

		// When : write next version to the file
		MakeProjectType makeProjectType = new MakeProjectType();
		makeProjectType.writeVersion(makeProjectDir, Version.valueOf("1.1.0"), null);

		// Then : IOException is thrown
	}
}
