package io.jenkins.plugins.conventionalcommits;

import com.github.zafarkhaja.semver.Version;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConventionalCommitsTest {

  @Test
  public void willBumpPatchVersion() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList("fix: bug fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("0.0.2"));
  }

  @Test
  public void willBumpPatchVersion_MultipleCommits() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList("fix: bug fix", "fix: bug fix", "fix: another fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("0.0.2"));
  }

  @Test
  public void willBumpMinorVersion() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out = cc.nextVersion(Version.valueOf("0.0.1"), Arrays.asList("feat: add new feature"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("0.1.0"));
  }

  @Test
  public void willBumpMinorVersion_MultipleCommits() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList("feat: add new feature", "fix: bug fix", "fix: another fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("0.1.0"));
  }

  @Test
  public void willBumpMajorVersion() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"), Arrays.asList("BREAKING CHANGE: new major version"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_MultipleCommits() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList(
                "BREAKING CHANGE: new major version",
                "BREAKING CHANGE: another major version",
                "feat: new feature",
                "chore: bug fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_ExclamationCommit() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"), Collections.singletonList("feat!: new major version"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_FooterMultipleLineCommit() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Collections.singletonList(
                "feat: new major version \nBREAKING CHANGE: new breaking change"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_ExclamationMultipleLineCommitNotFooter() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Collections.singletonList(
                "chore: new major version \nBREAKING CHANGE: new breaking change \nextra footer"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_MultipleCommitsMultipleLineExclamation() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList("feat!: add new feature", "fix: bug fix", "fix: another fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willBumpMajorVersion_MultipleCommitsMultipleLineFooter() {
    ConventionalCommits cc = new ConventionalCommits();

    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList(
                "feat: add new feature",
                "fix: bug fix \nBREAKING CHANGE: breaking change",
                "fix: another fix"));
    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("1.0.0"));
  }

  @Test
  public void willNotBumpMajorVersion_BreakingChangeCaseSensitivity() {
    ConventionalCommits cc = new ConventionalCommits();
    Version out =
        cc.nextVersion(
            Version.valueOf("0.0.1"),
            Arrays.asList(
                "feat: add new feature",
                "fix: bug fix \nBreaking Change: breaking change",
                "fix: bug fix \nBREAKING change: breaking change"));

    assertThat(out, is(notNullValue()));
    assertThat(out.toString(), is("0.1.0"));
  }
}
