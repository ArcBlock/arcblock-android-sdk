TOP_DIR=.
README=$(TOP_DIR)/README.md

VERSION=$(strip $(shell cat version))

build:
	@echo "Building the software..."
	./gradlew assembleRelease

init:
	@echo "Android Project doesn't need the init step, gradle will do it later when do build"

lint:
	@echo "Linting the software..."
	./gradlew lint

test:
	@echo "Running test suites..."
	./gradlew test

doc:
	@echo "Building the documenation..."
	javadoc @docargfile -header '<b>ArcBlock Android Sdk Documents</b><br><font size="-1">$(VERSION)</font>'


precommit: lint test

travis-init:
	@echo "Initialize software required for travis (normally ubuntu software)"
	@gem install fir-cli

travis:
	@make precommit

travis-deploy:
	if ! [ -f "./app/build/outputs/apk/release/app-release.apk" ]; then \
		echo "Preparing for deployment..."; \
		make clean; \
		make release; \
		make doc; \
	fi

clean:
	@echo "Cleaning the build..."
	-rm -rf docs
	./gradlew clean

run: clean
	@echo "Connect a Android Device to the CP, then will install a APK into it."
	./gradlew installRelease

deploy: release
	@echo "Deploy software into local machine..."

include .makefiles/release.mk
.PHONY: clean