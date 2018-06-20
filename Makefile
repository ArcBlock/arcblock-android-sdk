TOP_DIR=.
README=$(TOP_DIR)/README.md

VERSION=$(strip $(shell cat version))

build:
	@echo "Building the software..."
	./gradlew assembleRelease

init:
	@echo "Initializing the repo..."

travis-init:
	@echo "Initialize software required for travis (normally ubuntu software)"
	@gem install fir-cli

install:
	@echo "Install software required for this repo..."

dep:
	@echo "Install dependencies required for this repo..."

pre-build: install dep
	@echo "Running scripts before the build..."

post-build:
	@echo "Running scripts after the build is done..."

all: pre-build build post-build

test:
	@echo "Running test suites..."

lint:
	@echo "Linting the software..."

doc:
	@echo "Building the documenation..."
	./gradlew javadoc

travis:
	@set -o pipefail
	@make precommit

travis-deploy:
	@echo "Preparing for deployment...";
	@make release;
	@make doc;

clean:
	@echo "Cleaning the build..."
	./gradlew clean

run:
	@echo "Running the software..."

deploy:
	@echo "Deploy software into local machine..."

include .makefiles/release.mk
