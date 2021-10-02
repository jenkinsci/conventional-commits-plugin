# Contributing

Thank you for investing your time in contributing to our project! We encourage everyone to create [pull requests](https://github.com/jenkinsci/conventional-commits-plugin/pulls), [propose features and discuss issues](https://github.com/jenkinsci/conventional-commits-plugin/issues).

(*You could also check out our generic [`CONTRIBUTING.md`](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file for more guidelines on contributing to this repository*)

#### Developer Guidelines

Before you begin, check out our [Developer Guidelines](https://www.jenkins.io/doc/developer/tutorial/prepare/).

**NOTE:** *This project uses [**checkstyle**](https://checkstyle.sourceforge.io/google_style.html), to check its coding standard*

### Issues

#### Create a new Issue

If you spot an issue with this project, [search if an issue already exists](https://github.com/jenkinsci/conventional-commits-plugin/issues). If a related issue doesn't exist, you can open new issue using a relevant [issue form](https://github.com/jenkinsci/conventional-commits-plugin/issues/new/choose).

#### Solve an Issue

Go through our existing issues to find one that interests you. You could also use `labels` as filters, to find the best issue for you.

### Make Changes

#### Fork the Project

Fork this project on GitHub and check out your copy of the repository, to get started making the contribution.

```
git clone https://github.com/contributor/conventional-commits-plugin.git
cd conventional-commits-plugin
git remote add upstream conventional-commits-plugin
```

#### Create a Branch

Make sure that your fork is up-to-date and create a topic branch for your feature or bug fix.

```
git checkout master
git pull upstream master
git checkout -b feature-branch
```

#### Run Maven

Build and run tests with mvn, make sure it outputs BUILD SUCCESS.

#### Write Tests

Make sure to write a test that reproduces the problem you're trying to fix or describes a feature that you want to build.

(*We don't accept pull requests without tests.*)

#### Commit Changes

First, make sure that git knows your name and email address.

```
git config --global user.name "Your Name"
git config --global user.email "contributor@example.com"
```

Once you are done writing the code, you could now make your [commits](https://git-scm.com/docs/git-commit).

Writing good commit messages is important. A commit message should exactly describe what was changed and why.

```
git add ...
git commit
```

#### Push

Once you're done making your commits, you can now push your code.

```
git push origin feature-branch
```

#### Pull Request

Once you're done making the changes, you can now open a pull request (*PR*). Go to the forked repository in GitHub (`https://github.com/contributor/conventional-commits-plugin`) and select your feature branch. Click the '*Pull Request*' button and fill out the form.

While naming your Pull Request, make sure to start the title as follows:
* `wip: ` - For `in progress`
* `feat: ` - For `enchancement`
* `bug: `, `fix: ` - For `bug`
* `chore: ` - For `chore`
* `chore(deps)` - For `dependencies`
* `docs: ` - For `documentation`

*Pull requests are usually reviewed within a few days.* (*All checks in the pull request must be passed*)

#### Rebase

If you've been working on a change for a while, it is very likely that some other changes are already made in the repository. To solve this problem, [rebase](https://docs.github.com/en/get-started/using-git/about-git-rebase) with *upstream/master*.

```
git fetch upstream
git rebase upstream/master
git push origin my-feature-branch -f
```

### That's it!

Thank you for taking your time to contributing to this project! We really appreciate your efforts! 🚀
