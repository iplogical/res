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

# How to disable sleep and hibernation
sudo systemctl mask sleep.target suspend.target hibernate.target hybrid-sleep.target

# How to install new binaries
sudo ./scripts/install.sh 
this will issue a gradlew installDist and copy the binaries to /opt/iplogical/ , either restart the app manually or logout and login for autostart

# How to add application start after login
add a file in ~/.config/autostart/.desktop 
the file entry has to be the following

[Desktop Entry]
Type=Application
Name=InspirationLogical-Waiter
Exec=/opt/iplogical/bin/waiter
X-GNOME-Autostart-enabled=true

# How to recreate schema (use UTF8 beacuse it is inherited by the table created)
CREATE DATABASE <name> CHARACTER SET = utf8
The table created by javax persistence will use the same encoding
Modify it with : ALTER DATABASE <name> CHARACTER SET = utf8
ALTER TABLE <name> CHARACTER SET = utf8


