import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class unlock {
   private static int subCount;
   private static PublicKey pubKey;
   private static PrivateKey privKey;
   private static SecretKey aesKey;
   private static final Charset UTF_8 = StandardCharsets.UTF_8;

   public static byte[] getRandomN(int numBytes) {
      byte[] nonce = new byte[numBytes];
      new SecureRandom().nextBytes(nonce);
      return nonce;
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

   public static boolean checkSignature(String directory, PublicKey pubKey) {
      try {
         byte[] fileMsg = Files.readAllBytes(Paths.get(directory + "\\keyfile"));
         Signature sig = Signature.getInstance("SHA256withRSA");
         sig.initVerify(pubKey);
         sig.update(fileMsg);
         byte[] fileMsgSig = Files.readAllBytes(Paths.get(directory + "\\keyfile.sig"));
         return sig.verify(fileMsgSig);
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return false;
   }

   public static void decAESkey(String path) {
      try {
         byte[] aesEnc = Files.readAllBytes(Paths.get(path));
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(Cipher.DECRYPT_MODE, privKey);
         byte[] aesDec = cipher.doFinal(aesEnc);
         aesKey = new SecretKeySpec(aesDec, 0, aesDec.length, "AES");
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   public static byte[] decrypt(byte[] cText, SecretKey secret, byte[] iv) {
      try {
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         GCMParameterSpec spec = new GCMParameterSpec(128, iv);
         cipher.init(Cipher.DECRYPT_MODE, secret, spec);
         byte[] plainText = cipher.doFinal(cText);
         return plainText;
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return null;
   }

   public static byte[] decryptWithPrefixIV(byte[] encData, SecretKey secret){

      try {
         ByteBuffer data = ByteBuffer.wrap(encData);

         byte[] iv = new byte[12];
         data.get(iv);

         byte[] cipherText = new byte[data.remaining()];
         data.get(cipherText);

         byte[] plainText = decrypt(cipherText, secret, iv);
         return plainText;
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
      return null;

   }

   public static void decryptFile(String path) {

      try {
         // read from file
         byte[] fileMsg = Files.readAllBytes(Paths.get(path));

         // start decrypting the data
         byte[] decryptedText = decryptWithPrefixIV(fileMsg, aesKey);

         FileOutputStream f = new FileOutputStream(path);
         f.write(decryptedText);
         f.close();
      } catch (Exception e) {
         System.out.println("[!] Error: " + e.getMessage());
         System.exit(0);
      }
   }

   // check the files under directory and encrypt it if it not directory
   public static void fileFromDirectory(String directory)  {
      try {
         File directoryF = new File(directory);
         File[] fList = directoryF.listFiles();
         for (File file : fList) {
            if (file.isFile()) {
               // encrypt the data if it is file and not a directory
               decryptFile(file.getAbsolutePath());
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

   public static void deleteKeyFile(String directory) {
      File keyfile = new File(directory + "\\keyfile");
      keyfile.delete();
      File keyfileSig = new File(directory + "\\keyfile.sig");
      keyfileSig.delete();
   }

   public static void main(String args[]) {
      if (args.length != 8) {
         System.out.println("Usage: java unlock -d [directory] -p [public key] -r [private key] -s [subject] ");
         System.exit(0);
      }
      String pubKeyPath = null, privKeyPath = null, userSubject = null, directory = null;

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
            System.out.println("Usage: java unlock -d [directory] -p [public key] -r [private key] -s [subject] ");
            System.exit(0);
         }
      }

      try {
         // check if the directory valid
         File directorF = new File(directory);
         if (!directorF.exists()) {
            System.out.println("[!] The directory to unlock doesn't exist.");
            System.out.println("[!] Aborting...");
            System.exit(0);
         }

         //validate the subject with the subject inside public key and get the count of subject size
         if (validateSubject(userSubject, pubKeyPath)) {
            System.out.println("[+] The subject matches.");
         } else {
            System.out.println("[!] Your subject is wrong.");
            System.out.println("[!] Aborting...");
            System.exit(0);
         }

         //read private key
         privKey = getPubPriv.getPriv(privKeyPath);
         //read public key, provide path and the subject count to get actual public key
         pubKey = getPubPriv.getPub(pubKeyPath, subCount + 1);

         //check the signature using public key
         if (checkSignature(directory, pubKey)) {
            System.out.println("[+] keyfile signature match with the public key.");
         } else {
            System.out.println("[!] keyfile signature doesn't match with the public key.");
            System.out.println("[!] Aborting...");
            System.exit(0);
         }

         //decrypt aes key
         decAESkey(directory + "\\keyfile");

         //delete keyfile and keyfile.sig
         deleteKeyFile(directory);

         //decrypt encrypted files
         fileFromDirectory(directory);
      } catch (Exception e) {
         System.out.println("[!] Error: " + e);
      }
   }
}
