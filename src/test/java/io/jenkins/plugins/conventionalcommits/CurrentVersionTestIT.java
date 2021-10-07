package io.jenkins.plugins.conventionalcommits;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Result;
import java.net.URL;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration tests for the currentVersion step
 */
public class CurrentVersionTestIT {
  @Rule
  public JenkinsRule rule = new JenkinsRule();

  @Test
  public void shouldGetCurrentVersionForProjectWithConfigurationFile() throws Exception {
    // Given : a maven project, i.e with a configuration file
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-maven-project.zip");
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
                + "        println \"Current version is ${currentVersion()}\"\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n",
            true));

    // When : call currentVersion step
    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

    System.out.println(JenkinsRule.getLog(b));

    // Then : get the version of the pom.xml
    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("currentVersion"));
    assertThat(JenkinsRule.getLog(b), containsString("Current version is 1.0.0-SNAPSHOT"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }

  @Test
  public void shouldNotGetCurrentVersionIfNoConfigurationFile() throws Exception {
    // Given : a project without configuration file
    WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
    URL zipFile = getClass().getResource("simple-project-with-notags.zip");
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
                + "        println \"Current version is ${currentVersion()}\"\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n",
            true));

    // When : call currentVersion step
    WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());
    System.out.println(JenkinsRule.getLog(b));

    // Then : current version is 0.0.0
    assertThat(JenkinsRule.getLog(b), containsString("Started"));
    assertThat(JenkinsRule.getLog(b), containsString("No tags found"));
    assertThat(JenkinsRule.getLog(b), containsString("Current Tag is:"));
    assertThat(JenkinsRule.getLog(b), containsString("Current version is 0.0.0"));
    assertThat(JenkinsRule.getLog(b), containsString("Finished: SUCCESS"));
  }
}
