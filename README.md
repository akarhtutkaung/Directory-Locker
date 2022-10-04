# Shared Directory Locker
@Author: Akar (Ace) Htut Kaung

## Overview
This program implement a highly simplified encrypted file system in order to use for sharing the encrypt data to another person.<br>

**Use-case:** User-A want to leave encrypted file/s for User-B on a shared File System. Only the User-B would have access to decrypt the data left behind by User-A.

## Usage
### Compile
```
make all
```

### Run
1. Generate private and public key
    ```
    java keygen -s [subject] -d [directory] -f [filename]
    ```
    \[subject] : any keyword to re-enter when accessing the lock file<br>
    \[directory] : directory path for public and private key<br>
    \[filename] : filename for public and private key<br>

2. Lock a directory
    ```
    java lock -d [directory] -p [public key] -r [private key] -s [subject]
    ```
    \[directory] : directory path to lock<br>
    \[public key] : the unlock party public key path with the key name<br>
    \[private key] : the lock party private key path with the key name<br>
    \[subject] : subject inside the unlock party's public key <br>

3. Unlock a directory
    ```
    java unlock -d [directory] -p [public key] -r [private key] -s [subject]
	```
    \[directory] : directory path to unlock<br>
    \[public key] : the lock party public key path with the key name<br>
    \[private key] : the unlock party private key path with the key name<br>
    \[subject] : subject inside the lock party public key path<br>
