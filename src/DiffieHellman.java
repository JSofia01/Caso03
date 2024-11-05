import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {
    private BigInteger p;
    private BigInteger g;
    private BigInteger secret;

    public DiffieHellman(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
        this.secret = new BigInteger(1024, new SecureRandom());
    }

    public BigInteger generatePublicKey() {
        return g.modPow(secret, p);
    }

    public BigInteger generateSharedSecret(BigInteger publicKey) {
        return publicKey.modPow(secret, p);
    }
}
