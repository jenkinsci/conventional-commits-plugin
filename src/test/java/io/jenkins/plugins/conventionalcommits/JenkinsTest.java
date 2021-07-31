package io.jenkins.plugins.conventionalcommits;

import com.github.zafarkhaja.semver.Version;
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class JenkinsTest {

  @Rule public JenkinsRule rule = new JenkinsRule();

  @Test
  public void testPipelineWithNoTags() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-notags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n" + "  unzip '" + zipFile.getPath() + "'\n" + "  nextVersion()\n" + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("No tags found"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.0"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void testPipelineWithTags() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-tags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n" + "  unzip '" + zipFile.getPath() + "'\n" + "  nextVersion()\n" + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("Current Tag is: 0.1.0"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.1"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void testDeclarativePipelineWithTags() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-tags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "pipeline { \n"
                + "  agent any\n"
                + "  stages {\n"
                + "    stage('Stage') {\n"
                + "      steps {\n"
                + "        unzip '"
                + zipFile.getPath()
                + "'\n"
                + "        nextVersion()\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("Current Tag is: 0.1.0"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.1"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void testDeclarativePipelineWithTagsInEnvironmentBlock() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-tags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "pipeline { \n"
                + "  agent any\n"
                + "  stages {\n"
                + "    stage('Extract') {\n"
                + "      steps {\n"
                + "        unzip '"
                + zipFile.getPath()
                + "'\n"
                + "      }\n"
                + "    }\n"
                + "    stage('Version') {\n"
                + "      environment {\n"
                + "        NEXT_VERSION = nextVersion()"
                + "      }\n"
                + "      steps {\n"
                + "        echo \"next version = ${NEXT_VERSION}\"\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("Current Tag is: 0.1.0"));
    assertThat(JenkinsRule.getLog(b), containsString("next version = 0.1.1"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void shouldAddBuildMetadataInformation() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-notags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n"
                + "  unzip '"
                + zipFile.getPath()
                + "'\n"
                + "  nextVersion(buildMetadata: '001')\n"
                + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("No tags found"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.0+001"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void shouldAddPreReleaseInformation() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-notags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n" + "  unzip '" + zipFile.getPath() + "'\n" + "  nextVersion(preRelease: 'alpha')\n" + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("No tags found"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.0-alpha"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void shouldKeepPreReleaseInformation() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-prerelease-tags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n" + "  unzip '" + zipFile.getPath() + "'\n" + "\nnextVersion(preservePreRelease: true)\n" + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("Current Tag is: 0.2.0-alpha"));
    assertThat(JenkinsRule.getLog(b), containsString("0.2.1-alpha"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void shouldNotKeepPreReleaseInformation() throws Exception {
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-notags.zip");
    assertThat(zipFile, is(notNullValue()));

    p.setDefinition(
        new CpsFlowDefinition(
            "node {\n" + "  unzip '" + zipFile.getPath() + "'\n" + "  nextVersion(preRelease: 'alpha')" +
                "\nnextVersion()\n" + "}\n",
            true));

    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("nextVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("No tags found"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.0-alpha"));
    assertThat(JenkinsRule.getLog(b), containsString("0.1.0"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }


}
