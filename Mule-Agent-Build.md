# Building Mule-Agent extension

1. Edit settings.gradle
2. On the second line add the following:
include 'Mule-Agent'
4. Save settings.gradle
5. Find mule-agent-web-interface.jar according to the instructions in the README of Mule-Agent/lib
6. Run the following:
./gradlew Mule-Agent:clean Mule-Agent:install
