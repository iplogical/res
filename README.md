# How to set up and run the project?
1. Install Java 8 JDK
2. Clone master branch
3. Import into IDE as Gradle project
4. Install Lombok plugin for IDE
5. Run app/build.sh
6. Install and start MySQL server (set "root" user's password to "test")
7. Create MySQL schemas "ReceiptSchemaTest" and "ReceiptViewTest"
8. Run main() in Main.java from IDE or "gradlew run" from terminal

# What does build.sh do?
Script app/build.sh runs "gradlew xjc" to automatically generate .java files from XML schema file app/src/main/resources/schema/receipt.xsd. The output files are located in app/src/main/java/com/inspirationlogical/receipt/jaxb/.

# How to build the project without running the unit tests?
gradlew -x test build

# How to run a single test class?
gradlew -Dtest.single=<TestClassName> test

# How to set up Ubuntu in a VM on Windows with full screen support?
https://www.lifewire.com/run-ubuntu-within-windows-virtualbox-2202098

# How to set up Java 8 JDK on Ubuntu?
sudo apt-get install openjdk-8-jdk
sudo apt-get install openjfx

# How to calibrate touch display on Ubuntu
sudo ./scripts/touch_calibrate.sh 
