package io.jenkins.plugins;

import com.github.zafarkhaja.semver.Version;
import java.util.Arrays;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class ConventionalCommitsTest {

    public void willIgnoreMergeCommits() {

    }

    @Test
    public void willBumpPatchVersion() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "fix: bug fix"
        ));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("0.0.2"));
    }

    @Test
    public void willBumpPatchVersion_MultipleCommits() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "fix: bug fix",
                "fix: bug fix",
                "fix: another fix"
        ));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("0.0.2"));
    }

    @Test
    public void willBumpMinorVersion() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "feat: add new feature"
        ));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("0.1.0"));
    }

    @Test
    public void willBumpMinorVersion_MultipleCommits() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "feat: add new feature",
                "fix: bug fix",
                "fix: another fix"));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("0.1.0"));
    }

    @Test
    public void willBumpMajorVersion() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "BREAKING CHANGE: new major version"
        ));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("1.0.0"));
    }

    @Test
    public void willBumpMajorVersion_MultipleCommits() {
        ConventionalCommits cc = new ConventionalCommits();

        Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList(
                "BREAKING CHANGE: new major version",
                "BREAKING CHANGE: another major version",
                "feat: new feature",
                "chore: bug fix"));
        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("1.0.0"));
    }
}
