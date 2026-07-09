package org.alfresco.encryptor;

import java.io.File;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class PublicPrivateKeyShareStringEncryptorTest extends AbstractPublicPrivateKeyShareStringEncryptorTest
{

    @Override
    @Before
    public void setUp() throws Exception
    {
        ClassLoader cl = PublicPrivateKeyShareStringEncryptorTest.class.getClassLoader();
        URL uri = cl.getResource("keystore");
        PATH = uri.getPath();
    }

    @Override
    public void tearDown() throws Exception
    {
        // Remove all keys
        File dir = new File(PATH);
        for (File file : dir.listFiles())
        {
            if (file.getName().startsWith(PublicPrivateKeyShareStringEncryptor.KEYNAME))
            {
                file.delete();
            }

        }
    }

    @Test
    public void testOne() throws Exception
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();

        encryptor.createKeyFiles(PATH);

        encryptor.init();
        encryptor.initPublic(PATH);
        encryptor.initPrivate(PATH);

        String message = "qwerty123";
        String encrypted = encryptor.encrypt(message);
        String decrypted = encryptor.decrypt(encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void testVeryLongStrings() throws Exception
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();

        encryptor.createKeyFiles(PATH);

        encryptor.init();
        encryptor.initPublic(PATH);
        encryptor.initPrivate(PATH);

        String message = "a horribly long string, considerably longer than can be encrypted by RSA in one chunk";

        assertTrue("test not valid, test message too small", message.length() > 60);
        String encrypted = encryptor.encrypt(message);
        String decrypted = encryptor.decrypt(encrypted);

        assertEquals(message, decrypted);
    }

    /**
     * Messages are chunked to 53 byte boundaries
     *
     * @throws Exception
     */
    @Test
    public void testMultiplesOf53() throws Exception
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();

        encryptor.createKeyFiles(PATH);

        encryptor.init();
        encryptor.initPublic(PATH);
        encryptor.initPrivate(PATH);

        String message = "12345678901234567890123456789012345678901234567890123";

        assertEquals("test not valid, test message not 53 bytes", message.length(), 53);
        String encrypted = encryptor.encrypt(message);
        String decrypted = encryptor.decrypt(encrypted);

        assertEquals(message, decrypted);

        String message2 = message + message;

        assertEquals("test not valid, test message not 106 bytes", message2.length(), 106);
        encrypted = encryptor.encrypt(message2);
        decrypted = encryptor.decrypt(encrypted);

        assertEquals(message2, decrypted);
    }


    /**
     * Tests decrypting strings without a prepended config in the encrypted value
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testLegacyMode() throws UnsupportedEncodingException
    {
        PublicPrivateKeyShareStringEncryptor encryptor = setupAsyncKeyEncryptor();
        encryptor.setLegacyMode(true);
        assertEncryptionForMultipleStrings(encryptor);
    }

    /**
     * Tests decrypting strings without a prepended config in the encrypted value but with a string that mimics a config
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testLegacyModeWithSeparator() throws UnsupportedEncodingException
    {
        PublicPrivateKeyShareStringEncryptor encryptor = setupAsyncKeyEncryptor();
        encryptor.setLegacyMode(true);
        assertEncryptionForString(encryptor, "this;is;notAPasswordWithConfig");
        assertEncryptionForString(encryptor, "this;1024;notAPasswordWithConfig");
    }

    /**
     * Tests decrypting strings without with the default settings
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testDefaultMode() throws UnsupportedEncodingException
    {
        PublicPrivateKeyShareStringEncryptor encryptor = setupAsyncKeyEncryptor();
        assertEncryptionForMultipleStrings(encryptor);
    }

    @Test
    public void testRSA() throws UnsupportedEncodingException
    {
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/PKCS1Padding", 512, 1024, 2048);
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/OAEPWithMD5AndMGF1Padding", 512, 1024, 2048);
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/OAEPWithSHA-224AndMGF1Padding", 1024, 2048);
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", 1024, 2048);
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/OAEPWithSHA-384AndMGF1Padding", 1024, 2048);
        assertEncryptionForMultipleAsyncKeySizes("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", 2048);
    }

    @Test
    public void testAES() throws UnsupportedEncodingException
    {
        assertEncryptionForMultipleSyncKeySizes("AES/CBC/PKCS5Padding", 128, 192, 256);
        assertEncryptionForMultipleSyncKeySizes("AES/ECB/PKCS5Padding", 128, 192, 256);
    }

    @Test
    public void testDESede() throws UnsupportedEncodingException
    {
        assertEncryptionForMultipleSyncKeySizes("DESede/CBC/PKCS5Padding", 168);
        assertEncryptionForMultipleSyncKeySizes("DESede/ECB/PKCS5Padding ", 168);
    }

    /**
     * Tests that the checkInput method of the filter is automatically invoked by the JVM during deserialization when reading the private key file, and that it is invoked with the expected FilterInfo parameters
     *
     * @throws Exception
     */
    @Test
    public void testCheckInputIsAutomaticallyInvokedOnReadObject() throws Exception
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();
        encryptor.createKeyFiles(PATH);

        // Write a key file to read back
        encryptor.init();
        encryptor.initPublic(PATH);
        encryptor.initPrivate(PATH);

        // Get the private key file
        File privateKeyFile = new File(PATH, PublicPrivateKeyShareStringEncryptor.PRIKEYNAME);

        // Tracking flag
        boolean[] checkInputInvoked = {false};

        try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(privateKeyFile.toPath())))
        {
            // Wrap with a spy filter that delegates and records invocation
            is.setObjectInputFilter(filterInfo -> {
                checkInputInvoked[0] = true; // Mark as invoked
                return ObjectInputFilter.Status.UNDECIDED; // Defer to default behavior
            });

            // readObject() triggers the JVM to call checkInput automatically
            Object key = is.readObject();

            assertTrue("checkInput should have been automatically invoked during readObject()", checkInputInvoked[0]);
            assertNotNull("Key should have been deserialized successfully", key);
        }
    }

    /**
     * Tests that the filter rejects deserialization attempts that exceed the maximum allowed depth
     *
     * @throws Exception
     */
    @Test
    public void testFilterRejectsExcessiveDepth() throws Exception
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();
        encryptor.createKeyFiles(PATH);
        encryptor.init();
        encryptor.initPublic(PATH);
        encryptor.initPrivate(PATH);

        File privateKeyFile = new File(PATH, PublicPrivateKeyShareStringEncryptor.PRIKEYNAME);

        // Use an atomic reference to capture the filter result at depth > MAX_DEPTH
        AtomicReference<ObjectInputFilter.Status> capturedStatus = new AtomicReference<>(null);

        try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(privateKeyFile.toPath())))
        {
            is.setObjectInputFilter(filterInfo -> {
                if (filterInfo.depth() > 5 && filterInfo.serialClass() != null)
                {
                    capturedStatus.set(ObjectInputFilter.Status.REJECTED);
                    return ObjectInputFilter.Status.REJECTED;
                }
                return ObjectInputFilter.Status.UNDECIDED;
            });

            try
            {
                is.readObject();
            }
            catch (InvalidClassException | StreamCorruptedException e)
            {
                assertFalse(ObjectInputFilter.Status.UNDECIDED.equals(capturedStatus.get()));
            }
        }

        // Exceeds MAX_DEPTH of 5
        ObjectInputFilter.FilterInfo deepFilterInfo = createFilterInfo(String.class, -1L, 6L, 1L);

        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.Status status = filter.checkInput(deepFilterInfo);
        assertEquals("Depth exceeding MAX_DEPTH(5) must be REJECTED",
            ObjectInputFilter.Status.REJECTED, status);
    }

    /**
     * Tests that the filter rejects array lengths exceeding MAX_ARRAY_LENGTH (8192)
     *
     * @throws Exception
     */
    @Test
    public void testFilterRejectsExcessiveArrayLength() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        // Exceeds MAX_ARRAY_LENGTH of 8192
        ObjectInputFilter.FilterInfo exceedArrayfilterInfo = createFilterInfo(byte[].class, 9000L, 1L, 1L);

        ObjectInputFilter.Status status = filter.checkInput(exceedArrayfilterInfo);
        assertEquals("Array length exceeding MAX_ARRAY_LENGTH(8192) must be REJECTED",
            ObjectInputFilter.Status.REJECTED, status);
    }

    /**
     * Tests that the filter rejects references exceeding MAX_REFERENCES (64)
     *
     * @throws Exception
     */
    @Test
    public void testFilterRejectsExcessiveReferences() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        // Exceeds MAX_REFERENCES of 64
        ObjectInputFilter.FilterInfo exceedRefFilterInfo = createFilterInfo(String.class, 1L, 1L, 65L);

        ObjectInputFilter.Status status = filter.checkInput(exceedRefFilterInfo);
        assertEquals("References exceeding MAX_REFERENCES(64) must be REJECTED",
            ObjectInputFilter.Status.REJECTED, status);
    }

    /**
     * Tests that the filter rejects unknown/disallowed classes
     *
     * @throws Exception
     */
    @Test
    public void testFilterRejectsUnknownClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        // Not an allowed class
        ObjectInputFilter.FilterInfo unknownClassFilterInfo = createFilterInfo(ArrayList.class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(unknownClassFilterInfo);
        assertEquals("Unknown/disallowed class must be REJECTED",
            ObjectInputFilter.Status.REJECTED, status);
    }

    /**
     * Tests that the filter allows expected classes related to cryptographic keys
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsKeyRepClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo keyRepFilterInfo = createFilterInfo(java.security.KeyRep.class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(keyRepFilterInfo);
        assertEquals("java.security.KeyRep must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows sun.security.provider.DSAPublicKeyImpl class, which is used for DSA public keys
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsDSAPublicKeyImplClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();

        /* sun.security.provider is an internal JDK package. Starting from Java 9+, the module system restricts access to internal sun.* packages — they are not exported to unnamed modules. Class.forName(...) bypasses the compile-time module access check while still loading the internal class at runtime. */

        Class<?> dsaPublicKeyImplClass = Class.forName("sun.security.provider.DSAPublicKeyImpl");
        ObjectInputFilter.FilterInfo dsaPublicKeyFilterInfo = createFilterInfo(dsaPublicKeyImplClass, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(dsaPublicKeyFilterInfo);
        assertEquals("sun.security.provider.DSAPublicKeyImpl must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that {@code java.security.KeyRep$Type} is allowed by the filter.
     *
     * <p>
     * {@code KeyRep$Type} is an enum — it represents the type of key stored in a {@link java.security.KeyRep} (one of SECRET, PUBLIC, or PRIVATE). It is not listed in the explicit class-name allow-list ({@code ALLOWED_CLASS_NAMES}); instead it is admitted through the <em>enum admission path</em> in {@code CryptographicKeyFilter.checkInput}.
     *
     * @throws Exception
     *             if reflection or filter invocation fails unexpectedly
     */
    @Test
    public void testFilterAllowsKeyRepTypeClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        Class<?> keyRepTypeClass = Class.forName("java.security.KeyRep$Type");
        ObjectInputFilter.FilterInfo byteArrayFilterInfo = createFilterInfo(keyRepTypeClass, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(byteArrayFilterInfo);
        assertEquals("java.security.KeyRep$Type must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows byte[] class, which is commonly used in key representations
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsByteArrayClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo byteArrayFilterInfo = createFilterInfo(byte[].class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(byteArrayFilterInfo);
        assertEquals("byte[] ('[B') must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows sun.security.rsa.RSAPrivateCrtKeyImpl class, which is used for RSA private keys
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsRSAPrivateCrtKeyClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        Class<?> rsaPrivateCrtKeyImplClass = Class.forName("sun.security.rsa.RSAPrivateCrtKeyImpl");
        ObjectInputFilter.FilterInfo rsaPrivateCrtKeyFilterInfo = createFilterInfo(rsaPrivateCrtKeyImplClass, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(rsaPrivateCrtKeyFilterInfo);
        assertEquals("sun.security.rsa.RSAPrivateCrtKeyImpl must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows sun.security.rsa.RSAPublicKeyImpl class, which is used for RSA public keys
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsRSAPublicKeyImplClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        Class<?> rsaPublicKeyImplClass = Class.forName("sun.security.rsa.RSAPublicKeyImpl");
        ObjectInputFilter.FilterInfo rsaPrivateCrtKeyFilterInfo = createFilterInfo(rsaPublicKeyImplClass, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(rsaPrivateCrtKeyFilterInfo);
        assertEquals("sun.security.rsa.RSAPublicKeyImpl must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows java.math.BigInteger class, which is used in key representations
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsBigIntegerClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo rsaPrivateCrtKeyFilterInfo = createFilterInfo(java.math.BigInteger.class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(rsaPrivateCrtKeyFilterInfo);
        assertEquals("java.math.BigInteger must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

    /**
     * Tests that the filter allows sun.security.provider.DSAPrivateKey class, which is used for DSA private keys
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsDSAPrivateKeyClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        Class<?> dsaPrivateCrtKeyImplClass = Class.forName("sun.security.provider.DSAPrivateKey");
        ObjectInputFilter.FilterInfo dsaPrivateCrtKeyFilterInfo = createFilterInfo(dsaPrivateCrtKeyImplClass, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(dsaPrivateCrtKeyFilterInfo);
        assertEquals("sun.security.provider.DSAPrivateKey must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
        assertEquals("sun.security package must be ALLOWED",
            ObjectInputFilter.Status.ALLOWED, filter.checkInput(dsaPrivateCrtKeyFilterInfo));
    }

    /**
     * Tests that {@code javax.crypto.SecretKey} — and by extension any class from the {@code javax.crypto} package — is allowed by the filter.
     *
     * <p>
     * {@code javax.crypto.SecretKey} implements {@link java.security.Key}, and {@code javax.crypto} is a standard JDK/JCE package listed in {@code ALLOWED_PACKAGE_PREFIXES}. So it is admitted through the <em>key-interface and the trusted-package path</em>, not through the explicit class-name allow-list. The symmetric key types (AES, DESede, etc.) live in {@code javax.crypto} and must be deserializable for the encryptor to support symmetric algorithms alongside RSA.
     */
    @Test
    public void testFilterAllowsClassesInJavaxCrypto() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo javaxCryptoClassKeyFilterInfo = createFilterInfo(javax.crypto.SecretKey.class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(javaxCryptoClassKeyFilterInfo);
        assertEquals("sun.security.provider.DSAPrivateKey must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
        assertEquals("sun.security package must be ALLOWED",
            ObjectInputFilter.Status.ALLOWED, filter.checkInput(javaxCryptoClassKeyFilterInfo));
    }

    /**
     * Tests that the filter allows java.security.PublicKey interface, which is commonly used as a type for public keys and expected to be deserialized
     *
     * @throws Exception
     */
    @Test
    public void testFilterPublicKeyAllows() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo publicKeyFilterInfo = createFilterInfo(java.security.PublicKey.class, 1L, 1L, 1L);
        assertEquals("java.security.PublicKey must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, filter.checkInput(publicKeyFilterInfo));
        // PublicKey interface itself — isAssignableFrom(PublicKey) is true
        assertEquals("java.security.PublicKey must be ALLOWED via isAssignableFrom",
            ObjectInputFilter.Status.ALLOWED, filter.checkInput(publicKeyFilterInfo));
    }

    /**
     * Tests that the filter allows String class, which is commonly used in key representations and expected to be deserialized
     *
     * @throws Exception
     */
    @Test
    public void testFilterAllowsStringClass() throws Exception
    {
        ObjectInputFilter filter = new PublicPrivateKeyShareStringEncryptor.CryptographicKeyFilter();
        ObjectInputFilter.FilterInfo stringFilterInfo = createFilterInfo(String.class, 1L, 1L, 1L);
        ObjectInputFilter.Status status = filter.checkInput(stringFilterInfo);
        assertEquals("String.class must be ALLOWED by CryptographicKeyFilter",
            ObjectInputFilter.Status.ALLOWED, status);
    }

}
