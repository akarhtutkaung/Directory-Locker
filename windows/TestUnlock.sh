#!/bin/sh

javac unlock.java
java unlock -d "directory\testing" -p "keys\pub1.pem" -r "keys\priv2.pem" -s "userA's pubKey"