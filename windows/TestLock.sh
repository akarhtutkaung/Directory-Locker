#!/bin/sh
mkdir directory
cd directory
mkdir testing
cd testing
cat <<EOF > "a.txt"
This is real data for a.txt
EOF
mkdir sub
cd sub
cat <<EOF >"subA.txt"
This is real data for subA.txt
EOF
cd ../../..
mkdir keys
javac keygen.java
java keygen -s "userA's pubKey" -pub "keys\pub1.pem" -priv "keys\priv1.pem"
java keygen -s "userB's pubKey" -pub "keys\pub2.pem" -priv "keys\priv2.pem"

javac lock.java
java lock -d "directory\testing" -p "keys\pub2.pem" -r "keys\priv1.pem" -s "userB's pubKey"