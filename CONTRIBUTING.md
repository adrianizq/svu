# Contributing to SVU

---

**Disclaimer**: This document is not intended to follow a complex structure as if it were an open source project, it is just a reference to technologies such as jhipster along with the expected workflow in git/github.

#### **Technologies and Tools**

The basis of the project was generated using the **JHipster** development tool. This tool implemented a client-server model and, applying **Domain Driven Design** (DDD) principles, automatically generated the basic **CRUD** operations for each **entity** in the data model. The project uses **MongoDB** as the document database. During generation, **JHipster** incorporated an important set of dependencies that facilitate the application of good programming practices and provide a solid and well-organized folder structure for the project. The dependencies are listed below:

- **Jhipster**: internally implements Java and JavaScript modules to facilitate development.
- **Java - Spring boot**: Basic **API REST** architecture with spring-boot using **DTOs** and **Mappers** for data handling.
- **Vue3**: Using **Vue.js 3.5** to create the basic **CRUD** user interface for database entities.
- **Mongodb**: Document database to manage application transactions.
- **Swagger-UI**: **REST API** documentation tool.
- **Ehcache**: Most robust Java-Based cache.
- **JWT-auth**: Standard that defines a secure way of transmitting information.
- **vitest**: Engine to create unit and integration tests that will be applied to the user interface.
- **Junit**: Test framework for creating unit or integration tests for each Java module.

#### **Create a GPG and SSH keys**

Security and identity verification are crucial when interacting with GitHub. To **authenticate your connection** and perform secure operations with repositories (like `push` or `pull`), it's necessary to configure an **SSH key**. On the other hand, to **verify the authorship of commits** and ensure they genuinely come from a specific contributor, a **GPG key** is used to digitally sign them.

The following official guides will help you configure both keys to work with your projects on GitHub:

**Configure GPG (Commit Signing):**

- [Generating a new GPG key](https://docs.github.com/en/authentication/managing-commit-signature-verification/generating-a-new-gpg-key?platform=linux)
- [Adding a GPG key to your GitHub account](https://docs.github.com/en/authentication/managing-commit-signature-verification/adding-a-gpg-key-to-your-github-account)

**Configure SSH (Connection Authentication):**

- [Generating a new SSH key and adding it to the ssh-agent](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
- [Adding a new SSH key to your GitHub account](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)

#### **Trunk based development(TBD)**

Trunk-based development is a version control management practice where developers merge small, frequent updates to a core “trunk” or main branch. It’s a common practice among DevOps teams and part of the DevOps lifecycle since it streamlines merging and integration phases.

-[image]

#### **Name branch convention**

When using the trunk-based development approach, it is convenient to create a feature branch / hot-fix based on main with a name following the convention:

`<nro-issue>-title-issue-summary`

for example I have **issue 97** with the title “**add holidays to the global calendar**” then my branch would be with the name:

`97-add-holydays-to-global-calendar

#### **Commit convention**

1. The message must begin with a verb
2. The message should not be longer than 50 characters
3. The message should clearly state what the commit does.
4. Each commit should represent a single, logical change(**Atomicity**).
5. Apply the same format to all commits.

**Examples**:

- Add initial data for authorities
- Add initial data for offices
- Implement the showPqrsByOfficeId service
- Fix user login validation error
- Limit access to office endpoints

#### **Using git rebase for a Clean History**

To maintain a linear and tidy commit history, it's recommended to favor `git rebase` over `git merge` when integrating changes.

**Caution:** `git rebase` rewrites the commit history. It is crucial to understand its implications and follow best practices to avoid issues, especially on shared branches (the golden rule is: **do not rebase public/shared branches**).

**To use rebase safely:** Consult the detailed guidelines in this Atlassian tutorial: [Merging vs. rebasing](https://www.google.com/url?sa=E&q=https%3A%2F%2Fwww.atlassian.com%2Fgit%2Ftutorials%2Fmerging-vs-rebasing)

**For a Git refresher:** If you are unfamiliar with how rebase or Git in general works, we recommend reviewing this basic guide before proceeding: [Git Crash Course for Beginners](https://www.google.com/url?sa=E&q=https%3A%2F%2Fgist.github.com%2Fbrandon1024%2F14b5f9fcfd982658d01811ee3045ff1e)

#### **Recommended language for documentation/code**

Currently **English** and **Spanish** are used because the name of the entities is in **Spanish**, it is possible that in the future it will be completely **changed to English**, the ideal is that from now on the code or documentation that is written is in **English** to **unify everything** in a single language in addition to allowing to practice the **language considered universal**.

#### **Programming Style Guide**

We do not have a programming style guide beyond the examples generated by **jhipster**, however in the future there should be a **document** that allows you to follow good programming practices, for now it will not be a requirement in the **pull request** but it should be taken into account:

##### Java

- camelCase for variables, attributes and methods
- PascalCase for classes, enum and interfaces

**JavaScript**

- camelCase for variables, constants, functions and methods
- PascalCase for classes

**Jhipster** configures by default a container that runs a **sonar code quality server** that allows to improve the code quality from day zero, currently this tool has not been tested so in the future this section should be updated.

**Note**: While it is true that the code generated by Large Language Models (LLM) is usually of high quality and can significantly increase **developer productivity**, it is not advisable to copy and paste it directly without reviewing it. It is **essential to evaluate** aspects such as compliance with naming conventions, the need for possible refactoring and the removal of redundant comments to ensure code readability and maintainability.

#### **Expected Work-flow**

**The intended workflow would be as follows**

1. Pull the “main” branch, also known as source of truth.
2. Create my feature/hot-fix branch based on main following the branch naming convention.
3. Make the appropriate changes and **follow the commit convention**.
4. Rebase my branch with main to avoid conflicts of changes
5. If there are conflicts, resolve them and check that it is not a double assignment error in the issues.
6. Upload my local branch to the **github** repository.
7. Add title and description to my PR, usually related to the solution to the issue.
8. Request a maintainer's review.

**Using the git commands it could look like this**

1. `git fetch`
2. `git pull origin main`
3. `git checkout -b 97-add-holydays-to-global-calendar`
4. Work on the feature/hot-fix
5. `git rebase main` (there may be conflicts here)
6. Fix conflicts if any
7. `git push origin 97-add-holydays-to-global-calendar`
8. Fill in the fields of the pull request form
9. Send the pull request to the maintainer/collaborator for review

**Note:** If you have made changes on the wrong branch and want to move them to another, you can use `git stash`. Follow these steps:

1. **Temporarily save the changes:** Run `git stash`. This command saves your local modifications (both from the staging area and the working directory) onto a temporary stack (the stash stack) and resets your working directory to match the last commit (HEAD).

2. **Switch to the correct branch:** Navigate to the branch where you intended to make the changes using `git checkout <destination_branch_name>`.

3. **Apply the saved changes:** Run `git stash pop`. This command applies the most recently stashed changes from the stash stack to your current branch and removes them from the stack.

   This way, you can effectively move changes between branches. **Important:** When applying the **stash** (`git stash pop` or `git stash apply`), conflicts might occur if the destination branch has been modified in incompatible ways. You will need to resolve these conflicts manually. For more information on `git stash` and conflict resolution, refer to the official **Git** documentation.

#### **Guidelines for new contributors (without direct repository access)**

New contributors who do not have direct write access to the repository are expected to follow standard open-source contribution practices. This involves applying all the guidelines detailed in this document, with the additional requirement of using the forking workflow:

1. **Create a Fork:** Create a personal copy of the main repository in your account (e.g., **GitHub**, GitLab).
2. **Clone Your Fork:** Clone your personal copy (fork) to your local machine.
3. **Keep Synced:** Configure the original repository as a remote (typically named "upstream") and periodically update your fork to incorporate the latest changes.
4. **Work on a Branch:** Create a new branch in your local fork for your specific changes.
5. **Make Changes:** Develop your contribution and save the changes using commits.
6. **Submit a Pull Request (PR):** Once your work is complete, push the changes to your remote fork and open a Pull Request from your branch to the corresponding branch in the original repository.

For a detailed visual guide on this workflow, you can consult this resource from [freeCodeCamp.org](https://youtu.be/mklEhT_RLos?si=06m46VT19sGRj6jC)

-[image_of_happy_code]

#### **references**

1. https://www.atlassian.com/continuous-delivery/continuous-integration/trunk-based-development
2. https://www.travis-ci.com/blog/explaining-trunk-based-development/
3. https://www.jhipster.tech/getting-started
4. https://git-scm.com/docs/git-stash
5. https://www.atlassian.com/es/git/tutorials/merging-vs-rebasing
6. https://www.youtube.com/watch?v=mklEhT_RLos
7. https://www.youtube.com/watch?v=yzeVMecydCE
8. https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account
9. https://docs.github.com/en/authentication/managing-commit-signature-verification/adding-a-gpg-key-to-your-github-account
10. https://docs.github.com/en/authentication/managing-commit-signature-verification/generating-a-new-gpg-key
11. https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent
