# Building Mule-Agent extension

1. Edit settings.gradle and uncomment the line:
\#Mule-Agent
2. Save settings.gradle
3. Find mule-agent-web-interface.jar according to the instructions in the README of Mule-Agent/lib
4. Run the following:
./gradlew Mule-Agent:clean Mule-Agent:install
