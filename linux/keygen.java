import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class keygen {
   private static String userSubject;

   public static KeyPair generateKeyPair() throws Exception { // to generate key pair using RSA
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      SecureRandom random = new SecureRandom();
      generator.initialize(2048, random);
      KeyPair pair = generator.generateKeyPair();
      return pair;
   }

   public static void doPublic(String publicPath, PublicKey pub) {
      byte[] pubKey = pub.getEncoded();
      try {
         FileOutputStream pubKeyF = new FileOutputStream(publicPath);
         try {
            String sub = "subject: " + userSubject+"\n";
            byte[] s = sub.getBytes();
            pubKeyF.write(s);
            pubKeyF.write(pubKey);
            pubKeyF.close();
         } catch (IOException e) {
            System.out.println("[!] Error:" + e);
            System.exit(0);
         }
      } catch (FileNotFoundException e) {
         System.out.println("[!] Directory for public key does not exist.");
         System.exit(0);
      }
   }

   public static void doPrivate(String privatePath, PrivateKey priv) {
      byte[] privKey = priv.getEncoded();
      try {
         FileOutputStream privKeyF = new FileOutputStream(privatePath);
         try {
            privKeyF.write(privKey);
            privKeyF.close();
         } catch (IOException e) {
            System.out.println("[!] Error:" + e);
            System.exit(0);
         }
      } catch (FileNotFoundException e) {
         System.out.println("[!] Directory for private key does not exist.");
         System.exit(0);
      }
   }

   public static void main(String[] args) throws Exception {

      String publicPath = null, privatePath = null;

      if (args.length < 6) {
         System.out.println("Usage: java keygen -s [subject] -pub [public key] -priv [private key]");
         System.exit(0);
      }
      for (int i = 0; i < args.length; i += 2) {
         if (args[i].equals("-s")) {
            userSubject = args[i + 1];
         } else if (args[i].equals("-pub")) {
            publicPath = args[i + 1];
         } else if (args[i].equals("-priv")) {
            privatePath = args[i + 1];
         } else {
            System.out.println("Usage: java keygen -s [subject] -pub [public key] -priv [private key]");
            System.exit(0);
         }
      }

      KeyPair pair = generateKeyPair();
      PrivateKey priv = pair.getPrivate();
      PublicKey pub = pair.getPublic();

      // write public key into file
      System.out.println("[+] Creating public key file...");
      doPublic(publicPath, pub);
      System.out.println("[+] Public key file created!");

      // write private key into file
      System.out.println("[+] Creating private key file...");
      doPrivate(privatePath, priv);
      System.out.println("[+] Private key file created!");
   }
}
