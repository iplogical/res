#!/bin/bash
SCRIPT_DIR=$(dirname $0)

INSTALL_DIR="/opt/iplogical"

pushd $SCRIPT_DIR/../app
./gradlew clean --rerun-tasks
./gradlew build -x test --rerun-tasks
./gradlew installDist
popd

echo $SCRIPT_DIR
if [[ -e $INSTALL_DIR ]]
then
rm -rf /tmp/iplogical_todelete
mv $INSTALL_DIR /tmp/iplogical_todelete
fi

mkdir -p $INSTALL_DIR
cp -r $SCRIPT_DIR/../app/install/* $INSTALL_DIR/

# cleanup bat files 
rm -f $INSTALL_DIR/bin/*.bat
# setup permission and owners of binaires
chown manager:root $INSTALL_DIR/bin/manager
chown manager:root $INSTALL_DIR/bin/sampleapp
chown waiter:root $INSTALL_DIR/bin/waiter
chmod 755 $INSTALL_DIR
chmod 755 $INSTALL_DIR/bin
chmod 755 $INSTALL_DIR/lib
chmod 744 $INSTALL_DIR/bin/manager
chmod 744 $INSTALL_DIR/bin/sampleapp
chmod 744 $INSTALL_DIR/bin/waiter


