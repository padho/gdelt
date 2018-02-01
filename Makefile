.PHONY: fetch-bash-utils
fetch-bash-utils:
	@test ! -d ~/shell-semver && git clone git@github.com:TeslaGov/shell-semver.git ~/shell-semver || git -C ~/shell-semver pull

.PHONY: patch-version
patch-version:
	@$(MAKE) fetch-bash-utils
	@~/shell-semver/bump-version-push-tag.sh patch

.PHONY: bintray-publish
bintray-publish:
	./gradlew clean build :gdelt-java-sdk:bintrayUpload

.PHONY: tag-and-publish
tag-and-publish: patch-version bintray-publish
