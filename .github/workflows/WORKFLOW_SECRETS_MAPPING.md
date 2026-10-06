# GitHub Actions secrets mapping (analysis baseline)

This document captures the **current, verified mapping** of GitHub Actions secrets to workflows/jobs/steps/scripts.
It is intended as the baseline before refactoring secrets from workflow-global `env:` to job/step scope.

> Notes
>
> - Maven/Nexus, Quay and DockerHub credentials are consumed via `.ci.settings.xml` and `scripts/ci/settings.xml`.
> - `scripts/ci/init.sh` performs Docker logins to DockerHub and Quay; therefore any job that runs `init.sh` requires Docker/Quay creds.
> - The current GitHub Actions pipelines upload/download test results using **GitHub Actions artifacts** (`actions/upload-artifact`, `actions/download-artifact`), not S3.

## Secret inventory (by purpose)

### Maven / Nexus (artifact repository)
- `NEXUS_USERNAME` / `NEXUS_PASSWORD` → exported as `MAVEN_USERNAME` / `MAVEN_PASSWORD`

### Container registries
- `QUAY_USERNAME` / `QUAY_PASSWORD`
- DockerHub credentials are stored once and referenced under two secret names depending on workflow:
  - `DOCKER_USERNAME` / `DOCKER_PASSWORD` (used by `build.yml`, `nightly-ci-build.yml`)
  - `DOCKERHUB_USERNAME` / `DOCKERHUB_PASSWORD` (used by `release.yml`)

  The build and release workflows work with the same DockerHub account, so treat these as the same effective credential set.

### Git bot (GitHub authentication)
- `BOT_GITHUB_TOKEN`
- `BOT_GITHUB_USERNAME`
- `BOT_GITHUB_EMAIL`

### Security scanning
- `SRCCLR_API_TOKEN` (SourceClear)
- `VERACODE_API_ID` / `VERACODE_API_KEY` (Veracode)

### Test credentials / external services
- `ALF_PWD` (used by Share web execution scripts)

### Artifact storage (Allure/test artifacts)
- `ARTIFACTS_BUCKET`
- `ARTIFACTS_KEY`
- `ARTIFACTS_SECRET`

> Status: legacy Travis/S3 Allure flow only.
>
> `Install_Allure.md` documents a Travis CI pipeline that syncs surefire reports to S3 and runs `scripts/ci/upload_test_artifacts.sh`.
> The current GitHub Actions workflows do **not** invoke `upload_test_artifacts.sh` and instead use GitHub Actions artifacts.
> These secrets should be considered **unused by current GitHub Actions workflows**.

### AWS (WORM support)
- `AGS_AWS_ACCESS_KEY_ID`
- `AGS_AWS_SECRET_ACCESS_KEY`

## Workflow → job/step mapping

### `.github/workflows/build.yml` (alfresco_enterprise_share)

#### Secrets required by jobs

| Secret (env or step input) | Source secret | Where used (job/step/script) |
|---|---|---|
| `MAVEN_USERNAME`, `MAVEN_PASSWORD` | `NEXUS_USERNAME`, `NEXUS_PASSWORD` | Jobs that build/test with Maven and read `.ci.settings.xml`: `build`, `veracode_sast_share`, `build_docker_image`, `unit_tests`, `ags_unit_tests`, `community_shared_UI_configuration`, `enterprise_shared_UI_configuration` (and any job invoking Maven via `build.sh`) |
| `QUAY_USERNAME`, `QUAY_PASSWORD` | `QUAY_USERNAME`, `QUAY_PASSWORD` | Any job running `scripts/ci/init.sh` (Docker login): `build`, `veracode_sast_share`, `build_docker_image`, `unit_tests`, `ags_unit_tests`, `test`, `community_shared_UI_configuration`, `enterprise_shared_UI_configuration` |
| `DOCKERHUB_USERNAME`, `DOCKERHUB_PASSWORD` | `DOCKER_USERNAME`, `DOCKER_PASSWORD` | Any job running `scripts/ci/init.sh` (Docker login): `build`, `veracode_sast_share`, `build_docker_image`, `unit_tests`, `ags_unit_tests`, `test`, `community_shared_UI_configuration`, `enterprise_shared_UI_configuration` |
| `SRCCLR_API_TOKEN` | `SRCCLR_API_TOKEN` | Job `build` (matrix stage `source_clear`) via `scripts/ci/source_clear.sh` |
| AWS creds (step inputs) | `AGS_AWS_ACCESS_KEY_ID`, `AGS_AWS_SECRET_ACCESS_KEY` | Job `enterprise_shared_UI_configuration` step `aws-actions/configure-aws-credentials@v4` |
| Baseline download token (step input) | `BOT_GITHUB_TOKEN` | Job `veracode_sast_share` step `github-download-file` (`token:` input) |
| Veracode credentials (step inputs) | `VERACODE_API_ID`, `VERACODE_API_KEY` | Job `veracode_sast_share` step `veracode/Veracode-pipeline-scan-action` (`vid`/`vkey`) |

#### Secrets currently defined but **not evidenced as used** by `build.yml` directly
These secrets are present in workflow-global `env:` today, but `build.yml` does not directly call the scripts they are known to be used in:

- `ALF_PWD` (known usage: `scripts/ci/execute_share_web.sh`; `build.yml` invokes `execute_share_web_docker.sh` instead)
- `ARTIFACTS_BUCKET`, `ARTIFACTS_KEY`, `ARTIFACTS_SECRET`
  - Context: `Install_Allure.md` describes a legacy Travis CI flow that syncs surefire reports to S3 and generates Allure reports from S3.
  - Current GitHub Actions workflows (`build.yml`/`nightly-ci-build.yml`) generate/merge Allure results using GitHub artifacts and do **not** invoke `scripts/ci/upload_test_artifacts.sh`.
- Git bot env (`GIT_USERNAME`, `GIT_PASSWORD`, `GIT_EMAIL`) are only required if a build job runs scripts that use:
  - `scripts/ci/build_functions.sh` (git clone/ls-remote)
  - `scripts/ci/maven_release.sh` (needs email too)
  - `scripts/ci/mirror-community-repo.sh`

If any `build.yml` job invokes those scripts indirectly, those jobs must be updated accordingly in the mapping.

### `.github/workflows/nightly-ci-build.yml` (alfresco_enterprise_share_regression_ci)

This file is effectively a copy of `build.yml`. Apply the same mapping as above.

Additional notes:
- `MASTER_URL` and `SSL_CERT` are currently present in workflow-global `env:` but are **confirmed unused** (not referenced by workflows/scripts) and should be removed/scoped out during refactor.

### `.github/workflows/release.yml`

#### Secrets required by jobs

| Secret (env or step input) | Source secret | Where used (job/step/script) |
|---|---|---|
| `MAVEN_USERNAME`, `MAVEN_PASSWORD` | `NEXUS_USERNAME`, `NEXUS_PASSWORD` | Job `release` (Maven install + `scripts/ci/maven_release.sh`) |
| `QUAY_USERNAME`, `QUAY_PASSWORD` | `QUAY_USERNAME`, `QUAY_PASSWORD` | Jobs `release`, `update_downstream`, `mirroring` because they run `scripts/ci/init.sh` (Docker login) |
| `DOCKERHUB_USERNAME`, `DOCKERHUB_PASSWORD` | `DOCKERHUB_USERNAME`, `DOCKERHUB_PASSWORD` | Jobs `release`, `update_downstream`, `mirroring` because they run `scripts/ci/init.sh` (Docker login) |
| `GIT_USERNAME`, `GIT_PASSWORD` | `BOT_GITHUB_USERNAME`, `BOT_GITHUB_TOKEN` | Job `release` via `scripts/ci/maven_release.sh`; job `mirroring` via `scripts/ci/mirror-community-repo.sh` |
| `GIT_EMAIL` | `BOT_GITHUB_EMAIL` | Job `release` via `scripts/ci/maven_release.sh` |

#### Secrets currently defined but **not evidenced as used** by `release.yml` directly
- `ALF_PWD` (known usage: `scripts/ci/execute_share_web.sh`)
- `ARTIFACTS_BUCKET`, `ARTIFACTS_KEY`, `ARTIFACTS_SECRET` (legacy Travis/S3 Allure flow; unused by current GitHub Actions workflows)
- `MASTER_URL`, `SSL_CERT` (confirmed unused)

## Optimization note (non-blocking)

`release.yml` jobs `update_downstream` and `mirroring` currently run `scripts/ci/init.sh`, which performs DockerHub/Quay logins.
If those jobs do not need Docker at all, consider skipping `init.sh` for them (or adding a flag to `init.sh`) so DockerHub/Quay creds can be avoided in those jobs entirely.

## Least-privilege refactor guidance (next step)

When moving secrets out of workflow-global `env:`:

- Scope DockerHub/Quay creds to any job that runs `scripts/ci/init.sh`.
- Scope `SRCCLR_API_TOKEN` only to the `build` job (matrix stage `source_clear`).
- Keep Veracode and baseline download token step-scoped.
- Scope AWS creds only to `enterprise_shared_UI_configuration`.
- Scope Git bot creds only to jobs that run `maven_release.sh`, `mirror-community-repo.sh`, or scripts that call into `build_functions.sh`.
