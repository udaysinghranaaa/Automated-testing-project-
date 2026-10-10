# GitHub Publishing Checklist

Nothing in this checklist was executed during Phase 10. Run it only after explicit authorization and after replacing placeholders.

## Pre-publish

- Confirm no credentials, tokens, cookies, browser profiles, generated reports or backups are tracked.
- Review `git diff` and every file under `.github/workflows`.
- Confirm README counts match `docs/TEST_CASES.md`.
- Run `mvn clean test` and one headless smoke.
- Decide repository owner, visibility, license and branch protection.

## Exact future commands

```powershell
Set-Location C:\devlabs
git init
git branch -M main
git status --short
git add .
git diff --cached --check
git diff --cached --stat
git commit -m "Build ShopFlow Selenium QA automation framework"

# Create the empty repository in GitHub UI first, then use its real URL:
git remote add origin https://github.com/<OWNER>/shopflow-qa-automation.git
git remote -v
git push -u origin main
```

Optional GitHub CLI alternative after authenticated approval:

```powershell
gh auth status
gh repo create <OWNER>/shopflow-qa-automation --source . --private --remote origin
git push -u origin main
```

Do not paste tokens into the remote URL, README, properties or workflow. Use GitHub authentication and repository/environment secrets.

## First remote CI verification

1. Open Actions and manually run **ShopFlow Selenium CI**.
2. Keep full regression enabled; optionally enable advanced suite.
3. Confirm Java, Maven and Chrome version output.
4. Confirm smoke passes and artifact upload runs under `if: always()`.
5. Download artifacts; open TestNG/Extent outputs and check paths.
6. Verify push and pull-request triggers on a small documentation branch.
7. Configure branch protection only after required check names are stable.

## Jenkins onboarding

- Install Pipeline, Declarative Pipeline, Git, JUnit and artifact support required by the controller.
- Configure tool names `jdk17` and `maven3`, or update the Jenkinsfile to actual approved names.
- Provide a Linux agent labeled `linux && chrome` with Chrome installed.
- Create a Pipeline-from-SCM job and run smoke first.
- Confirm XML glob and archive paths on the actual controller.
- Keep credentials in Jenkins Credentials, never in the Jenkinsfile.

## Release-quality check

```powershell
git status --short
git log --oneline -5
git ls-files | Select-String -Pattern 'target/|artifacts/|\.env$|\.pem$|\.key$|browser-profiles'
```

Expected final command output for the sensitive/generated pattern should be empty. If it is not, unstage and fix `.gitignore` before pushing.

