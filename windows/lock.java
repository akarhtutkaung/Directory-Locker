//package Java;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.file.Files;
import java.security.*;
import java.io.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * Steps
 * -----
 * Encrypt the directory using AES key and generate MAC tags for each file.
 * Encrypt that AES key into keyfile using recipient public key
 * Sign that keyfile with your private key
 */
public class lock {
   private static PrivateKey privKey;
   private static PublicKey pubKey;
   private static SecretKey aesKey;
   private static String directory;
   private static int subCount;


   // generate AES key
   public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
      KeyGenerator generator = KeyGenerator.getInstance("AES");
      SecureRandom random = new SecureRandom();
      generator.init(256, random.getInstanceStrong());
      return generator.generateKey();
   }

   // encrypt AES key into file using public key
   public static void encAESKey(SecretKey aes) {
      try {
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(Cipher.ENCRYPT_MODE, pubKey);
         byte[] aesEnc = cipher.doFinal(aes.getEncoded());

         //write the AES key into keyfile
         writeFile(directory + "\\keyfile", aesEnc);

      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   // write data into file
   public static void writeFile(String path, byte[] data) {
      try {
         FileOutputStream f = new FileOutputStream(path);
         f.write(data);
         f.close();
      } catch (IOException e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   public static byte[] getRandomN(int numBytes) {
      byte[] nonce = new byte[numBytes];
      new SecureRandom().nextBytes(nonce);
      return nonce;
   }

   // encrypt using AES GCM
   public static byte[] encrypt(byte[] pText, SecretKey aes, byte[] iv) {
      try {
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         GCMParameterSpec spec = new GCMParameterSpec(128, iv);
         cipher.init(Cipher.ENCRYPT_MODE, aes, spec);
         byte[] encryptedText = cipher.doFinal(pText);
         return encryptedText;
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return null;
   }

   // encrypt the data with prefix iv
   public static byte[] encryptWithPrefixIV(byte[] data, SecretKey secret, byte[] iv) {

      try {
         // encrypt the data
         byte[] encData = encrypt(data, secret, iv);
         byte[] encDataIv = ByteBuffer.allocate(iv.length + encData.length).put(iv).put(encData).array();
         return encDataIv;
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return null;
   }

   public static void encryptFile(String path) {
      try {
         // read from file
         byte[] fileMsg = Files.readAllBytes(Paths.get(path));

         // start encrypting the data
         byte[] iv = getRandomN(12);
         byte[] encryptedText = encryptWithPrefixIV(fileMsg, aesKey, iv);

         writeFile(path, encryptedText);
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   // check the files under directory and encrypt it if it not directory
   public static void fileFromDirectory(String directory){
      try {
         File directoryF = new File(directory);
         File[] fList = directoryF.listFiles();
         for (File file : fList) {
            if (file.isFile()) {
               // encrypt the data if it is file and not a directory
               encryptFile(file.getAbsolutePath());
            } else if (file.isDirectory()) {
               // if directory then go into subdirectory
               fileFromDirectory(file.getAbsolutePath());
            }
         }
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   public static void doSignature(byte[] data, String path, PrivateKey priv) {
      try {
         Signature privateSignature = Signature.getInstance("SHA256withRSA");
         privateSignature.initSign(priv);
         privateSignature.update(data);

         byte[] signature = privateSignature.sign();
         // write subject with signature into file
         writeFile(path, signature);
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   public static boolean validateSubject(String userSubject, String pubKey) {
      try {
         BufferedReader br = new BufferedReader(new FileReader(pubKey));
         String first = null, combine = "";
         first = br.readLine();
         combine = first.substring(9);
         subCount = first.length();
         if (combine.equals(userSubject)) {
            return true;
         } else {
            return false;
         }
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return false;
   }

   public static void main(String args[]) {
      if (args.length != 8) {
         System.out.println("Usage: java lock -d [directory] -p [public key] -r [private key] -s [subject] ");
         System.exit(0);
      }
      String pubKeyPath = null, privKeyPath = null, userSubject = null;

      for (int i = 0; i < 8; i += 2) {
         if (args[i].equals("-d")) {
            directory = args[i + 1];
         } else if (args[i].equals("-p")) {
            pubKeyPath = args[i + 1];
         } else if (args[i].equals("-r")) {
            privKeyPath = args[i + 1];
         } else if (args[i].equals("-s")) {
            userSubject = args[i + 1];
         } else {
            System.out.println("Usage: java lock -d [directory] -p [public key] -r [private key] -s [subject] ");
            System.exit(0);
         }
      }

      // check if the directory valid
      File directorF = new File(directory);
      if (!directorF.exists()) {
         System.out.println("[!] The directory to lock doesn't exist.");
         System.out.println("[!] Aborting...");
         System.exit(0);
      }

      try {
         //validate the subject with the subject inside public key and get the count of subject size
         if (validateSubject(userSubject, pubKeyPath) == false) {
            System.out.println("[!] Your subject is wrong.");
            System.out.println("[!] Aborting...");
            System.exit(0);
         }

         //read private key
         privKey = getPubPriv.getPriv(privKeyPath);

         //read public key, provide path and the subject count to get actual public key
         pubKey = getPubPriv.getPub(pubKeyPath, subCount + 1);

         // Create AES key
         aesKey = generateAESKey();

         //Start file checking and encrypting
         fileFromDirectory(directory);

         //Encrypt the AES key with public key and save it to file
         encAESKey(aesKey);

         //Sign the ASE keyfile using private key
         byte[] fileMsg = Files.readAllBytes(Paths.get(directory + "\\keyfile"));
         doSignature(fileMsg, directory + "\\keyfile.sig", privKey);

      } catch (Exception e) {
         System.out.println("[!] Error:" + e.getMessage());
         System.exit(0);
      }
   }
}
