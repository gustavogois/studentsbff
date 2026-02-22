Apply the git conventions for this project when committing or creating pull requests.

## Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short summary>

[optional body]

[optional footer]
```

- **Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `build`
- **Summary:** imperative mood, no trailing period, max 72 chars (e.g., `add Subject JPA entity`)
- **Body:** explain *why*, not *what*; blank line after summary; wrap at 72 chars
- **Footer:** `BREAKING CHANGE: <description>` or `Closes #<issue>`

## Branch Naming
`<type>/<short-description>` — e.g., `feat/subject-crud`, `fix/jwt-expiry`

## Pull Requests
- Title follows the same Conventional Commits format as commit messages
- Body must include:
  - **Summary:** what changed and why
  - **Changes:** bullet list of relevant files/components modified
  - **Test plan:** steps to verify the change works correctly
- Keep PRs small and focused on a single concern
- Link related issues in the PR description
