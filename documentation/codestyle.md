# Codestyle

### Java

The [Google Java Code Style](https://google.github.io/styleguide/javaguide.html) is used.

### Git

The current production state is kept on the main branch.

Every feature is assigned a unique ID and worked on in a separate branch.
After reviewing the finished feature, it is merged into the main branch.

Naming style:

Branch: `&lt;ID>-<name-separated-by-dashes>` (52-add-login-page)

Commits: `#&lt;Branch-ID> commit message` (#52 added backend rest endpoint to get user details)