Pod::Spec.new do |spec|
    spec.name                     = 'common'
    spec.version                  = '29'
    spec.homepage                 = 'https://zwander.dev'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'KVD21Control'
    spec.vendored_frameworks      = 'build/cocoapods/framework/commonFrameworkOld.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.0'
    spec.osx.deployment_target = '10.13'
    spec.dependency 'Bugsnag'
                
    if !Dir.exist?('build/cocoapods/framework/commonFrameworkOld.framework') || Dir.empty?('build/cocoapods/framework/commonFrameworkOld.framework')
        raise "

        Kotlin framework 'commonFrameworkOld' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :common:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':common',
        'PRODUCT_MODULE_NAME' => 'commonFrameworkOld',
    }
                
    spec.script_phases = [
        {
            :name => 'Build common',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end