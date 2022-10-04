# Shared Directory Locker
@Author: Akar (Ace) Htut Kaung

## Overview
This program implement a highly simplified encrypted file system.<br>

**Use-case:** User-A want to leave encrypted file/s for User-B on a shared File System. Only the User-B would have access to decrypt the data left behind by User-A.

## Usage
### Compile
```
make all
```

### Run
1. Generate private and public key
    ```
    java keygen -s [subject] -pub [public key] -priv [private key]
    ```
    \[subject] : any keyword to re-enter when accessing the lock file<br>
    \[public key] : public  key path with key name and extension<br>
    \[private key] : private key path with key name and extension<br>

2. Lock a directory
    ```
    java lock -d [directory] -p [public key] -r [private key] -s [subject]
    ```
    \[directory] : directory path to lock<br>
    \[public key] : the unlock party public key path with the key name and extension<br>
    \[private key] : the lock party private key path with the key name and extension<br>
    \[subject] : subject inside the unlock party's public key <br>

3. Unlock a directory
    ```
    java unlock -d [directory] -p [public key] -r [private key] -s [subject]
	```
    \[directory] : directory path to unlock<br>
    \[public key] : the lock party public key path with the key name and extension<br>
    \[private key] : the unlock party private key path with the key name and extension<br>
    \[subject] : subject inside the lock party public key path<br>

## Testing

### TestKeygen.sh
This script generates key pair which contains "testing" as subject for public key.

### TestLock.sh
This script will lock/encrypt the directory named "testing" under "directory" directory.
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

**Note: after running this script, you can check the data inside the a.txt and subA.txt, the data will be encrypted and you will not be able to see it as normal text**

### TestUnlock.sh

**Important: Make sure to run TestLock.sh script before running this. Because this script will restore the encrypted messages from TestLock.sh**
**Since this script will use "testing" under "directory" directory to decrypt data to original format, if there's original/normal file,the directory, or keypairs doesn't exist, this will shows Error.**

This script will unlock/decrypt the directory named "testing" under "directory" directory.
 - decrypt the files inside "testing" directory and remove AES keys inside it.

**Note: after running this script, you can check the data inside the a.txt and subA.txt, the data will be shows in normal/original way as which we originally created.**
