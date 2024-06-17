package org.alfresco.encryptor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class PublicPrivateKeyShareStringEncryptorTest extends TestCase
{
    private static String PATH = null;

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

    private void assertEncryptionForMultipleAsyncKeySizes(String encryptionAlgorithm, int... keySizes)
            throws UnsupportedEncodingException
    {
        for (int keySize : keySizes)
        {
            PublicPrivateKeyShareStringEncryptor encryptor = setupAsyncKeyEncryptor(encryptionAlgorithm, keySize);
            assertEncryptionForMultipleStrings(encryptor);
        }
    }

    private void assertEncryptionForMultipleSyncKeySizes(String encryptionAlgorithm, int... keySizes)
            throws UnsupportedEncodingException
    {
        for (int keySize : keySizes)
        {
            PublicPrivateKeyShareStringEncryptor encryptor = setupSyncKeyEncryptor(encryptionAlgorithm, keySize);
            assertEncryptionForMultipleStrings(encryptor);
        }
    }

    private PublicPrivateKeyShareStringEncryptor setupAsyncKeyEncryptor()
    {
        return setupAsyncKeyEncryptor(null, 0);
    }

    private PublicPrivateKeyShareStringEncryptor setupAsyncKeyEncryptor(String encryptionAlgorithm, int keySize)
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();

        if (encryptionAlgorithm != null)
        {
            encryptor.setEncryptionAlgorithm(encryptionAlgorithm);
        }

        if (keySize > 0)
        {
            encryptor.setKeySize(keySize);
        }

        encryptor.setKeyLocation(PATH);
        encryptor.createKeyFiles();
        encryptor.initPublic();
        encryptor.initPrivate();

        return encryptor;
    }

    private PublicPrivateKeyShareStringEncryptor setupSyncKeyEncryptor(String encryptionAlgorithm, int keySize)
    {
        PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();

        if (encryptionAlgorithm != null)
        {
            encryptor.setEncryptionAlgorithm(encryptionAlgorithm);
        }

        if (keySize > 0)
        {
            encryptor.setKeySize(keySize);
        }

        encryptor.setKeyLocation(PATH);
        encryptor.createKeyFiles();
        encryptor.initSecret();

        return encryptor;
    }

    /**
     * Runs the encryption/decryption tests using strings of multiple lengths
     * 
     * @param encryptor
     * @throws UnsupportedEncodingException
     */
    private void assertEncryptionForMultipleStrings(PublicPrivateKeyShareStringEncryptor encryptor) throws UnsupportedEncodingException
    {
        assertEncryptionForString(encryptor, generateString(15));
        assertEncryptionForString(encryptor, generateString(53));
        assertEncryptionForString(encryptor, generateString(150));
        assertEncryptionForString(encryptor, generateString(250));
    }

    /**
     * Asserts if the size of the encrypted value matches the expected size and if the decrypted string matches the
     * original
     * 
     * @param encryptor
     * @param length
     * @throws UnsupportedEncodingException
     */
    private void assertEncryptionForString(PublicPrivateKeyShareStringEncryptor encryptor, String text)
            throws UnsupportedEncodingException
    {
        String encrypted = encryptor.encrypt(text);
        int encryptedLength = getEncriptedLength(encrypted);
        assertTrue(encryptedLength >= (encryptor.keySize / 8));

        PublicPrivateKeyShareStringEncryptor dencryptor = new PublicPrivateKeyShareStringEncryptor();
        dencryptor.setKeyLocation(PATH);
        String decrypted = dencryptor.decrypt(encrypted);
        assertEquals(text, decrypted);
    }

    /**
     * Get the number of bytes of the encrypted string
     * 
     * @param encryptedMessage
     * @return
     * @throws UnsupportedEncodingException
     */
    private int getEncriptedLength(String encryptedMessage) throws UnsupportedEncodingException
    {
        byte[] message = Base64.decodeBase64(encryptedMessage.getBytes(StandardCharsets.UTF_8));
        return message.length;
    }

    /**
     * Generates random characters with a given length
     * 
     * @param length
     * @return
     */
    private String generateString(int length)
    {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

}
