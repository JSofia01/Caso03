import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ServerProtocol {
    public static void process(PrintWriter pOut, BufferedReader pIn, PrivateKey privateKey, PublicKey publicKey) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger P = new BigInteger(1024, 100, secureRandom);
        BigInteger G = BigInteger.valueOf(2);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        pOut.println(P.toString());
        pOut.println(G.toString());

        BigInteger x = new BigInteger(1024, secureRandom);
        BigInteger Gx = G.modPow(x, P);
        pOut.println(Gx.toString());

        signature.update((P.toString() + G.toString() + Gx.toString()).getBytes());
        String signedData = Base64.getEncoder().encodeToString(signature.sign());
        pOut.println(signedData);

        BigInteger Gy = new BigInteger(pIn.readLine());
        BigInteger sessionKey = Gy.modPow(x, P);

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] sessionHash = digest.digest(sessionKey.toByteArray());
        byte[] K_AB1 = new byte[16];
        byte[] K_AB2 = new byte[16];
        System.arraycopy(sessionHash, 0, K_AB1, 0, 16);
        System.arraycopy(sessionHash, 16, K_AB2, 0, 16);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Mac mac = Mac.getInstance("HmacSHA384");
        mac.init(new SecretKeySpec(K_AB2, "HmacSHA384"));

        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(K_AB1, "AES"), ivSpec);

        String encryptedConsult = pIn.readLine();
        byte[] decryptedConsult = cipher.doFinal(Base64.getDecoder().decode(encryptedConsult));
        String consult = new String(decryptedConsult);

        String[] parts = consult.split("-");
        String clientId = parts[0];
        String packageId = parts[1];

        Server.PackageData packageData = Server.getPackageStatus(clientId, packageId);
        String estadoAlfabético = PackageStatus.getStatus(packageData.status);

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(K_AB1, "AES"), ivSpec);
        byte[] encryptedResponse = cipher.doFinal(estadoAlfabético.getBytes());
        pOut.println(Base64.getEncoder().encodeToString(encryptedResponse));

        byte[] responseHmac = mac.doFinal(estadoAlfabético.getBytes());
        pOut.println(Base64.getEncoder().encodeToString(responseHmac));
    }
}
