#!/bin/sh

brew install cocoapods

curl -s "https://get.sdkman.io" | bash
. "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 18.0.2-open

pod install

export JAVA_HOME=/Users/local/.sdkman/candidates/java/current
