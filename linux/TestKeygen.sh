#!/bin/sh
mkdir keys
java keygen -s testing -pub "keys/pub1.pem" -priv "keys/priv1.pem"

# if you open the pub1.pem key file, you will see the subject and public key data