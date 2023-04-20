#!/usr/bin/env bash
echo "=========================== Starting Mirroring Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "/tmp"

MIRROR_REPO="${1}" # expected format "https://github.com/Alfresco/<mirror-repo>.git"

if [ -z "${MIRROR_REPO}" ]; then
  echo "Missing MIRROR_REPO parameter..."
  exit 1
fi

# Install git-filter-repo
pip3 install --user git-filter-repo

git config --global branch.autosetuprebase always

# Clone source repo
rm -rf mirror
git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Alfresco/alfresco-enterprise-share.git mirror
cd mirror

# Checkout all support branches
git --no-pager branch -l -r | grep '/support/\|/release/' | grep -v '/5.1.' | xargs -l -r echo | cut -b 8-100 | xargs -r -I{} git checkout -b {} origin/{}
git checkout master

# Remove origin
git remote rm origin


# This exclude references of enterprise in Share. From AMPS and actual Share.
# force the evaluation
# rewrite commits and tags
git filter-repo -f --preserve-commit-hashes --commit-callback '
legacy_enterprise_commits = [b"34388b09d90792246f5381dfe98214997105f41a",b"387d6e7d2fd7fa0a6d032b0a304688a4490ffc5f",b"072c2b083d8cc59165a5fc39cf0b2ac12ab78b87",b"8666132b17243825ce5df027aa765ebae7b38293",b"596af6083f65ad6926662c5718bb3ece158cb9a2",b"1f89a84dd105eb430057d1536482efadd0eb338e",b"76f521836bb2d03035115a5a2aced7e267e1b842",b"262734b71a8ee906f933b82cc0b330f5ec22a122",b"2af8c192f2bb9f5abb21a26200671d575ecc2490",b"a0e1c98d7a0786a69fd069bc6a372ed565192521",b"4f0f248e1d130778b7134144127d0da8352ffc3d",b"252e04b1b5384e9d9ba9a7a9aaa7a7bbd9faafb5",b"5f9969195e2e3fbc1da50ac965b50c95dab4813e",b"182300f6f56ac6325d383f552c522d9ec24e34fa",b"805b0f9c23814d943e4b4f4e7683db6763395a19",b"6cc99688845fe636cf5b937ccb65eb2df7fdf77f",b"dfd9eb9975a82b9f1231a4967d924a44e89635bf",b"01772ee89fc45d04e8296753cc0bb458beb6b462",b"15871812684966c870bf3d7cc37b8fce4a8e1514",b"a41ee48a0ceb6e43746e5795bfa682fef1441ad2",b"cdcc61d4c9e8b01d35864c723a95b5297b2ddeb0",b"81ed6076df2ed51635f111f3ae10e3f9fdc564a0",b"84f816705cba25f4e863e0d81c4a853767299cb5",b"6abe17fec0f6d2d89698ea6427b8b2e06cf72234",b"f600a950120cca06f296feb384061c83992fe65e",b"ccb23105b7c98a81adb0462634a0d88121fa541a",b"5e4ffdf8e1a1546ffdd071405dc720ec1468c9d4",b"cfec188c5320fe2ea6a020a4ba294ee2224b13e4",b"14d86342cf90c9ace6b290f128e51699c0a4f619",b"bf7f8f3831a761d3cee3b78b07733443b7dcd901",b"f23c9bcc2ec5a14e157ed6cd5efe605f78f2d07d",b"a16a0a268d6134150b70729f514a8d9d5a2b478b",b"9ee04bdcf11d1ae31a4e7b8f76b1fb2e86ca7157",b"de3b36a986c4de0df65c8f0d294750f284a6eaa8",b"02a10082467dcbf73dc21e0601d2d4fbda0bb351",b"0900006e74388a074dc9a7b3344b900f639e9520",b"d7cc3907f4f57b273ea2c4eb3024b949ec9b2fbc",b"79e6ab876898289f6d20c7c74c7490337474c1d1",b"edbe63b5fea221d97e22c308cf9a5b725734cf1f",b"3d945d388dadd7ed766b92850b2d7ffc77b21681",b"1b0d2e991ca463710a81ac675d9581067b3446d1",b"ca2293d345cdd5d4b2d90dbb59661e59389a7927",b"2059c0605af5fce3b8eeaae85991d1a867b08c4d",b"3ac5ea315d22de64aaa878512be4f3c74acbab33",b"8a566dd456f58aef51908c629b7aca651963688e",b"0ba65b8aa2afa482f186112e59aaaeb84cfaca9e",b"29ce454d6d218e26dd7a12c9cb93642b2266e7aa",b"bd9981673fe5217c615a4698c7b6fa403746a0e7",b"1b920a81de7abbd908e2b4cb0d250bc94f9545ab",b"178e9e513675bffd9f27d14c5b3a84c62ae7b450",b"df3709a2562b1263fd2b74f2144cd2527d80df60",b"812f35f06d2f377de118b9dce3d5b454d81518e8",b"da18b464072908dd1ca813deb712f8c42f7eb5a1",b"6ce8a8253d10a706fd33d31660ae2d3d507cd5c9",b"00cc1996d407f604296cb384f696eb2de0aed903",b"530a3b7889ef781be8faac7a960e0969b13461d7"]
if commit.original_id in legacy_enterprise_commits:
    commit.file_changes = []
else:
    # Remove any changes in rm-enterprise.
    commit.file_changes = [change for change in commit.file_changes
        if  not b"rm-enterprise" in change.filename
        and not b"rm-automation" in change.filename
        and not b"rm-share-enterprise" in change.filename
        and not b"docker-alfresco" in change.filename
        and not b"srcclr.yml" in change.filename
        and not b"_ci" in change.filename
        and not b"alfresco-enterprise" in change.filename
        and not b"travis" in change.filename
        and not b"Licenses" in change.filename
        and not b".lic" in change.filename]
'


# Push all local branches and tags to the Mirror
git remote add origin "https://${GIT_USERNAME}:${GIT_PASSWORD}@${MIRROR_REPO:8}"
git push -f -u origin --all
git push -f -u origin --tags

popd
set +vex
echo "=========================== Finishing Mirroring Script =========================="
