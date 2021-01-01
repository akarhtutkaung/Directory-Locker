Overview
========
This program implement a highly simplified encrypted file system. The basic idea is two users on a shared File System want to leave encrypted files for each other. The program uses symmetric key to encrypt all files inside the given directory.

To Compile the program 
=====================
make all

To Run the program
==================
   1) keygen
         java keygen -s [subject] -pub [public key] -priv [private key]
         #Note: 
            [public key]  -> public  key path with key name and extension
            [private key] -> private key path with key name and extension
   2) lock
         java lock -d [directory] -p [public key] -r [private key] -s [subject]
	 #Note:
	    [directory] -> directory path to lock
	    [public key] -> the unlock party public key path with the key name and extension
	    [private key] -> the lock party private key path with the key name and extension
	    [subject] -> subject inside the unlock party public key path
   3) unlock
         java unlock -d [directory] -p [public key] -r [private key] -s [subject]
	 #Note:
	    [directory] -> directory path to unlock
	    [public key] -> the lock party public key path with the key name and extension
	    [private key] -> the unlock party private key path with the key name and extension
	    [subject] -> subject inside the lock party public key path

=============
TestKeygen.sh
=============
script to generate key pair which contains "testing" as subject for public key.

===========
TestLock.sh
===========
script which will lock/encrypt the directory named "testing" under "directory" directory.
 - create 2 key pair
   - key pair 1:
     - public key: pub1.pem
     - private key: priv1.pem
     - subject: userA's pubKey
   - key pair 2:
     - public key: pub2.pem
     - private key: priv2.pem
     - subject: userA's pubKey
 - create directory name directory:
   - create subdirectory named "testing":
     - create a.txt with text "This is real data for a.txt"
     - create subdirectory named "sub":
       - create subA.txt with text "This is real data for subA.txt"
 - encrypt the files inside "testing" directory

#Note: after running this script, you can check the data inside the a.txt and subA.txt, the data will be encrypted and you will not be able to see it as normal text

=============
TestUnlock.sh
=============
#Important: Make sure to run TestLock.sh script before running this. Because this will restore the encrypted messages from TestLock.sh
#Reason: Because this script will use "testing" under "directory" directory to decrypt data to original format. So, if there's original/normal file,the directory, or keypairs doesn't exist, this will shows Error.

script which will unlock/decrypt the directory named "testing" under "directory" directory.
 - decrypt the files inside "testing" directory and remove AES keys inside it.

#Note: after running this script, you can check the data inside the a.txt and subA.txt, the data will be shows in normal/original way as which we originally created.


Programming Language
--------------------
Java

Team Member Names
------------------
Akar Htut Kaung