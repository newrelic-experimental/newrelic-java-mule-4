# Building Mule-Agent extension

1. Edit settings.gradle and uncomment the line:
//include 'Mule-Batch-ee'
2. Save settings.gradle
3. Find mule-modules-batch-ee.jar according to the instructions in the README of Mule-Batch-ee/lib
4. Run the following:
./gradlew Mule-Batch-ee:clean Mule-Batch-ee:install
