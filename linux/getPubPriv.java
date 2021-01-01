import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class getPubPriv {
   public static PublicKey getPub(String filePath, int len) {
      try {
         byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
         byte[] pub = new byte[keyBytes.length - len];
         for (int i = 0; i < keyBytes.length - len; i++) {
            pub[i] = keyBytes[i + len];
         }
         X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
         KeyFactory kf = KeyFactory.getInstance("RSA");
         return kf.generatePublic(spec);
      } catch (IOException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      } catch (NoSuchAlgorithmException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      } catch (InvalidKeySpecException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      }
      return null;
   }

   public static PrivateKey getPriv(String filename) {
      try {
         byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

         PKCS8EncodedKeySpec spec =
             new PKCS8EncodedKeySpec(keyBytes);
         KeyFactory kf = KeyFactory.getInstance("RSA");
         return kf.generatePrivate(spec);
      } catch (IOException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      } catch (NoSuchAlgorithmException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      } catch (InvalidKeySpecException e) {
         System.out.println("[!] Error: " + e);
         System.exit(0);
      }
      return null;
   }

}
