/*
 * #%L
 * Alfresco Enterprise Repository
 * %%
 * Copyright (C) 2005 - 2025 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */
package org.alfresco.encryptor;

import java.io.File;
import java.io.ObjectInputFilter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;

abstract class AbstractPublicPrivateKeyShareStringEncryptorTest extends TestCase
{
    protected static String PATH = "/tmp";
    private static final Random RANDOM = new Random();

    protected void assertEncryptionForMultipleAsyncKeySizes(String encryptionAlgorithm, int... keySizes)
        throws UnsupportedEncodingException
    {
        for (int keySize : keySizes)
        {
            PublicPrivateKeyShareStringEncryptor encryptor = setupAsyncKeyEncryptor(encryptionAlgorithm, keySize);
            assertEncryptionForMultipleStrings(encryptor);
        }
    }

    protected void assertEncryptionForMultipleSyncKeySizes(String encryptionAlgorithm, int... keySizes)
            throws UnsupportedEncodingException
    {
        for (int keySize : keySizes)
        {
            PublicPrivateKeyShareStringEncryptor encryptor = setupSyncKeyEncryptor(encryptionAlgorithm, keySize);
            assertEncryptionForMultipleStrings(encryptor);
        }
    }

    protected PublicPrivateKeyShareStringEncryptor setupAsyncKeyEncryptor()
    {
        return setupAsyncKeyEncryptor(null, 0);
    }

    protected PublicPrivateKeyShareStringEncryptor setupAsyncKeyEncryptor(String encryptionAlgorithm, int keySize)
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

    protected PublicPrivateKeyShareStringEncryptor setupSyncKeyEncryptor(String encryptionAlgorithm, int keySize)
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
    protected void assertEncryptionForMultipleStrings(PublicPrivateKeyShareStringEncryptor encryptor)
            throws UnsupportedEncodingException
    {
        assertEncryptionForString(encryptor, generateString(15));
        assertEncryptionForString(encryptor, generateString(53));
        assertEncryptionForString(encryptor, generateString(150));
        assertEncryptionForString(encryptor, generateString(250));
    }

    /**
     * Asserts if the size of the encrypted value matches the expected size and if the decrypted string matches the original
     *
     * @param encryptor
     * @throws UnsupportedEncodingException
     */
    protected void assertEncryptionForString(PublicPrivateKeyShareStringEncryptor encryptor, String text)
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
    protected int getEncriptedLength(String encryptedMessage) throws UnsupportedEncodingException
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
    protected String generateString(int length)
    {
        byte[] array = new byte[length];
        RANDOM.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    /**
     * Helper method to create a FilterInfo instance with specified parameters for testing the filter logic
     *
     * @param clazz
     * @param arrayLength
     * @param depth
     * @param references
     * @return
     */
    protected ObjectInputFilter.FilterInfo createFilterInfo(Class<?> clazz, long arrayLength, long depth, long references)
    {
        return new ObjectInputFilter.FilterInfo() {
            @Override
            public Class<?> serialClass()
            {
                return clazz;
            }

            @Override
            public long arrayLength()
            {
                return arrayLength;
            }

            @Override
            public long depth()
            {
                return depth;
            }

            @Override
            public long references()
            {
                return references;
            }

            @Override
            public long streamBytes()
            {
                return 0L;
            }
        };
    }

}
