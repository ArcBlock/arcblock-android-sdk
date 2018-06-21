RELEASE_VERSION=v$(VERSION)
GIT_BRANCH=$(strip $(shell git symbolic-ref --short HEAD))

release: build
	@git config --local user.name "NateRobinson"
	@git config --local user.email "840501291@qq.com"
	@git tag $(RELEASE_VERSION)
	@git push origin $(RELEASE_VERSION) | true

delete-release:
	@echo "Delete a release on $(RELEASE_VERSION)"
	@git tag -d $(RELEASE_VERSION) | true
	@git push -f -d origin $(RELEASE_VERSION) | true

bump-version:
	@echo "Bump version..."
	@.makefiles/bump_version.sh

create-pr:
	@echo "Creating pull request..."
	@make bump-version || true
	@git add .;git commit -a -m "bump version";git push origin $(GIT_BRANCH)
	@hub pull-request

browse-pr:
	@hub browse -- pulls
