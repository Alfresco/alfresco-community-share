/*
 * Copyright 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.encryption.StringEncryptor;

public class PublicPrivateKeyShareStringEncryptor implements StringEncryptor
{

    protected static final int DEFAULT_KEY_SIZE = 512;
    protected static final String DEFAULT_KEY_ALGORITHM = "RSA";
    protected static final String DEFAULT_ENCRYPTION_ALGORITHM = "RSA/ECB/PKCS1PADDING";
    protected static final int IV_SIZE_64 = 8;
    protected static final int IV_SIZE_128 = 16;

    private static final String CONFIG_SEPARATOR = ";";

    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected SecretKey secretKey;
    public static final String KEYNAME = "alfrescoSpringKey";
    static final String PUBKEYNAME = KEYNAME + ".pub";
    static final String PRIKEYNAME = KEYNAME + ".pri";
    static final String SECKEYNAME = KEYNAME + ".key";

    static final String KEY_PACKAGE = "/alfresco/web-extension";
    static final String PRIKEYPATH = KEY_PACKAGE + "/" + PRIKEYNAME;
    static final String PUBKEYPATH = KEY_PACKAGE + "/" + PUBKEYNAME;
    static final String SECKEYPATH = KEY_PACKAGE + "/" + SECKEYNAME;

    public int keySize = DEFAULT_KEY_SIZE;
    protected String keyAlgorithm = DEFAULT_KEY_ALGORITHM;
    protected String encryptionAlgorithm = DEFAULT_ENCRYPTION_ALGORITHM;
    protected boolean legacyMode;
    protected boolean symmetricKey;
    private String keyLocation = KEY_PACKAGE;
    private int ivSize;

    public void setKeyAlgorithm(String keyAlgorithm)
    {
        this.keyAlgorithm = keyAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm)
    {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public void setIsSymmetricKey(boolean symmetricKey)
    {
        this.symmetricKey = symmetricKey;
    }

    public void setKeyLocation(String keyLocation)
    {
        this.keyLocation = keyLocation;
    }

    public void setKeySize(int keySize)
    {
        this.keySize = keySize;
    }

    public void setIvSize(int ivSize)
    {
        this.ivSize = ivSize;
    }

    public void setLegacyMode(boolean legacyMode)
    {
        this.legacyMode = legacyMode;
    }

    /**
     * Notify an information message
     * 
     * @param msg
     *            the message
     */
    void info(String msg)
    {
        System.out.println(msg);
    }

    /**
     * privateKey initialization using resource from classpath
     */
    public void init()
    {
        URL privateKeyURL = this.getClass().getResource(PRIKEYPATH);
        URL secretKeyURL = this.getClass().getResource(SECKEYPATH);

        if (privateKeyURL == null && secretKeyURL == null)
        {
            return;
        }

        // Set path to keys so we can load the key on decryption
        setKeyLocation(this.getClass().getResource(KEY_PACKAGE).getPath());

        // If we are used a secret key, it will only be loaded on decryption (when we know the algorithm)
        if (privateKeyURL == null)
        {
            return;
        }

        try
        {
            File privateKeyFile = new File(privateKeyURL.toURI());

            initPrivateKeyFile(privateKeyFile);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Could not instantiate Private Key", e);
        }

    }

    @Override
    public String encrypt(String message)
    {
        byte[] cipherText = new byte[0];
        String retval = message;

        try
        {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            // bytes to encrypt
            byte[] messageBytes = message.getBytes("UTF-8");

            // We only need to split RSA into chunks
            int chunkSize = !symmetricKey ? getChunkSize() : messageBytes.length;

            if (messageBytes.length > chunkSize)
            {
                // yes we need multiple chunks
                byte[] cipherChunk = new byte[0]; // chunk of encrypted stuff
                byte[] buffer = new byte[chunkSize]; // working buffer

                for (int i = 0; i < messageBytes.length; i++)
                {
                    // if we filled our buffer array we have our block ready for encryption
                    if ((i > 0) && (i % chunkSize == 0))
                    {
                        // execute the encryption operation
                        cipherChunk = cipher.doFinal(buffer);
                        cipherText = append(cipherText, cipherChunk);

                        // here we calculate the length of the next buffer required
                        int newlength = chunkSize;

                        // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                        if (i + chunkSize > messageBytes.length)
                        {
                            newlength = messageBytes.length - i;
                        }
                        // clean the buffer array
                        buffer = new byte[newlength];
                    }
                    // copy byte into our buffer.
                    buffer[i % chunkSize] = messageBytes[i];
                } // for each byte in message

                // Any remaining bytest in buffer
                cipherChunk = cipher.doFinal(buffer);
                cipherText = append(cipherText, cipherChunk);
            }
            else
            {
                // we don't need multiple chunks
                cipherText = cipher.doFinal(messageBytes);
            }

            // In legacy mode, send the cipher without any prepended config
            if (legacyMode)
            {
                info("Legacy Mode Enabled, cipher text will not contain config preprended ");
                return new String(Base64.encodeBase64(cipherText));
            }

            retval = new String(Base64.encodeBase64(prependCipherConfig(cipherText, cipher.getIV())));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (BadPaddingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        return retval;
    }

    @Override
    public String decrypt(String encryptedMessage)
    {
        String retval = encryptedMessage;

        byte[] plainText = new byte[0];

        try
        {
            byte[] messageBytes;
            IvParameterSpec ivParams = null;

            byte[] completeMessage = Base64.decodeBase64(encryptedMessage.getBytes("UTF-8"));

            // Extract cipher and set encryption algorithm and key size if its in the message
            byte[] encryptedIvAndTextBytes = extractMessage(completeMessage);

            byte[] iv = new byte[ivSize];
            messageBytes = new byte[encryptedIvAndTextBytes.length - iv.length];
            if (iv.length > 0)
            {
                // Separate IV from message
                System.arraycopy(encryptedIvAndTextBytes, 0, iv, 0, iv.length);
                ivParams = new IvParameterSpec(iv);
                System.arraycopy(encryptedIvAndTextBytes, iv.length, messageBytes, 0, messageBytes.length);
            }
            else
            {
                // Message does not contain IV
                messageBytes = encryptedIvAndTextBytes;
            }

            int chunkSize = (keySize / 8);

            Cipher cipher;
            try
            {
                cipher = Cipher.getInstance(encryptionAlgorithm);
                cipher.init(Cipher.DECRYPT_MODE, symmetricKey ? secretKey : privateKey, ivParams);
            }
            catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e)
            {
                throw new RuntimeException("Could not decrypt value", e);
            }

            byte[] decrypted;
            // We only need to split RSA into chunks
            if (!symmetricKey)
            {
                // encrypted chunk
                byte[] cipherChunk;
                // Working buffer
                byte[] buffer = new byte[chunkSize];

                for (int i = 0; i < messageBytes.length; i++)
                {
                    // if we filled our buffer array we have our block ready for encryption
                    if ((i > 0) && (i % chunkSize == 0))
                    {
                        // execute the operation
                        cipherChunk = cipher.doFinal(buffer);
                        // add the result to our total result.
                        plainText = append(plainText, cipherChunk);

                        // here we calculate the length of the next buffer required
                        int newlength = chunkSize;

                        // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                        if (i + chunkSize > messageBytes.length)
                        {
                            newlength = messageBytes.length - i;
                        }
                        // clean the buffer array
                        buffer = new byte[newlength];
                    }
                    // copy byte into our buffer.
                    buffer[i % chunkSize] = messageBytes[i];
                }

                // Any remaining buffer
                decrypted = cipher.doFinal(buffer);
            }
            else
            {
                decrypted = cipher.doFinal(messageBytes);
            }

            plainText = append(plainText, decrypted);

            retval = new String(plainText, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        catch (BadPaddingException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        return retval;
    }

    public void initPublic()
    {
        initPublic(keyLocation);
    }

    public void initPublic(String alfrescoSharedDir)
    {
        File webExtensionDir = getWebExtensionDir(alfrescoSharedDir);

        if (!webExtensionDir.exists())
        {
            webExtensionDir = new File(alfrescoSharedDir);
        }

        File publicKeyFile = new File(webExtensionDir, PUBKEYNAME);

        if (publicKeyFile.canRead())
        {
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(publicKeyFile));)
            {
                publicKey = (PublicKey) is.readObject();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not instantiate Public Key", e);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not open Public Key", e);
            }
        }
        else
        {
            throw new RuntimeException("Public Key File Not Found :" + publicKeyFile.getPath());
        }
    }

    public void initPrivate()
    {
        initPrivate(keyLocation);
    }

    public void initPrivate(String alfrescoSharedDir)
    {
        File webExtension = getWebExtensionDir(alfrescoSharedDir);
        
        if (!webExtension.exists())
        {
            webExtension = new File(alfrescoSharedDir);
        }

        File privateKeyFile = new File(webExtension, PRIKEYNAME);
        if (privateKeyFile.canRead())
        {
            ObjectInputStream is = null;
            try
            {
                is = new ObjectInputStream(new FileInputStream(privateKeyFile));
                privateKey = (PrivateKey) is.readObject();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not instantiate Private Key", e);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not find Private Key", e);
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException error)
                    {
                        // nothing
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("BBB Private Key File Not Found :" + privateKeyFile.getPath());
        }
    }

    public void initSecret()
    {
        if (secretKey != null)
        {
            // Secret key already loaded
            return;
        }
        File enterpriseDir = getWebExtensionDir(keyLocation);

        if (!enterpriseDir.exists())
        {
            enterpriseDir = new File(keyLocation);
        }

        File secretKeyFile = new File(enterpriseDir, SECKEYNAME);
        if (secretKeyFile.canRead())
        {
            try (InputStream is = Files.newInputStream(Paths.get(secretKeyFile.getPath())))
            {
                byte[] keyBytes = is.readAllBytes();
                secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not find Secret Key", e);
            }

        }
        else
        {
            throw new RuntimeException("Secret Key File Not Found :" + secretKeyFile.getPath());
        }
    }

    /**
     * createKeyFiles
     *
     * @param alfrescoSharedDir
     *            String
     */
    public void createKeyFiles()
    {
        createKeyFiles(keyLocation);
    }

    public void createKeyFiles(String alfrescoSharedDir)
    {
        File sharedDir = new File(alfrescoSharedDir);

        File webExtensionDir = getWebExtensionDir(alfrescoSharedDir);

        if (!sharedDir.exists())
        {
            throw new RuntimeException("alfresco shared dir does not exist : " + sharedDir);
        }

        if (!webExtensionDir.exists())
        {
            webExtensionDir = new File(alfrescoSharedDir);
            info("Keys will be created in folder " + alfrescoSharedDir);
        }

        File publicKeyFile = new File(webExtensionDir, PUBKEYNAME);
        File privateKeyFile = new File(webExtensionDir, PRIKEYNAME);

        this.setKeyLocation(alfrescoSharedDir);

        try
        {
            setKeyAlgorithm();

            if (symmetricKey)
            {
                File secretKeyFile = new File(webExtensionDir, SECKEYNAME);

                KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
                keyGenerator.init(keySize);
                SecretKey key = keyGenerator.generateKey();

                try (OutputStream fos = Files.newOutputStream(Paths.get(secretKeyFile.getPath())))
                {
                    fos.write(key.getEncoded());
                    info("secret key created file: " + secretKeyFile.getPath());
                }
                catch (IOException e)
                {
                    throw new RuntimeException("unable to create secret key file", e);
                }

                return;

            }

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
            keyGen.initialize(keySize);
            KeyPair key = keyGen.generateKeyPair();

            try (ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));)
            {
                publicKeyOS.writeObject(key.getPublic());

                // TODO config log
                // info("public key created file: "+ publicKeyFile.getPath());
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to create public key file", e);
            }

            try (ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));)
            {
                privateKeyOS.writeObject(key.getPrivate());

                // TODO config log
                // info("private key created file:" + privateKeyFile.getPath());

            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to create private key file", e);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Unable to generate public/private key", e);
        }
    }

    public String getPasswordFromConsole()
    {
        String enteredPassword = null;
        String verifyPassword = null;
        boolean firstOne = true;
        do
        {
            if (!firstOne)
            {
                System.console().writer().println("Please enter the same value twice to verify your encrypted value");
                System.console().writer().flush();
            }
            firstOne = false;
            System.console().writer().print("Please Enter Value: ");
            System.console().writer().flush();
            enteredPassword = new String(System.console().readPassword());
            System.console().writer().print("Please Repeat Value: ");
            System.console().writer().flush();
            verifyPassword = new String(System.console().readPassword());
        } while (enteredPassword == null || enteredPassword.length() < 1 || !enteredPassword.equals(verifyPassword));
        return enteredPassword;
    }

    /**
     * privateKey initialization using a defined uri
     *
     * @param shareDir
     */
    public void initConfig(String shareDir)
    {
        this.setKeyLocation(shareDir);
        File privateKeyFile = new File(getWebExtensionDir(shareDir), PRIKEYNAME);
        initPrivateKeyFile(privateKeyFile);
    }

    /**
     * Sets the key algorithm, if key is symmetric and IV size based on the encryptionAlgorithm
     */
    protected void setKeyAlgorithm()
    {
        String[] encryptionAlgorithmArray = encryptionAlgorithm.split("/", 3);

        if (encryptionAlgorithmArray.length != 3)
        {
            // Not a valid algorithm, set default
            setEncryptionAlgorithm(DEFAULT_ENCRYPTION_ALGORITHM);
            setKeySize(DEFAULT_KEY_SIZE);
            setKeyAlgorithm();
            return;
        }

        this.setKeyAlgorithm(encryptionAlgorithmArray[0].toUpperCase(Locale.ROOT).trim());
        String mode = encryptionAlgorithmArray[1];

        switch (keyAlgorithm)
        {
        case "AES":
        {
            setIsSymmetricKey(true);
            setIvSize(mode.equals("ECB") ? 0 : IV_SIZE_128);
            break;
        }
        case "DESEDE":
        {
            setIsSymmetricKey(true);
            setIvSize(mode.equals("ECB") ? 0 : IV_SIZE_64);
            break;
        }
        case "RSA":
        {
            setIsSymmetricKey(false);
            setIvSize(0);
            break;
        }
        default:
            throw new RuntimeException("Key algorithm not supported: " + keyAlgorithm);
        }
    }

    private File getWebExtensionDir(String alfrescoSharedDir)
    {
        File sharedDir = new File(alfrescoSharedDir);
        File alfrescoDir = new File(sharedDir, "alfresco");
        File webExtensionDir = new File(alfrescoDir, "web-extension");
        return webExtensionDir;
    }

    /**
     * append two byte arrays together
     *
     * @param prefix
     * @param suffix
     * @return a byte array containing the contents of prefix + suffix
     */
    private static byte[] append(byte[] prefix, byte[] suffix)
    {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i = 0; i < prefix.length; i++)
        {
            toReturn[i] = prefix[i];
        }
        for (int i = 0; i < suffix.length; i++)
        {
            toReturn[i + prefix.length] = suffix[i];
        }
        return toReturn;
    }

    private void initPrivateKeyFile(File privateKeyFile)
    {
        if (privateKeyFile.canRead())
        {
            ObjectInputStream is = null;
            try
            {
                is = new ObjectInputStream(new FileInputStream(privateKeyFile));
                privateKey = (PrivateKey) is.readObject();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not instantiate Private Key", e);

            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not instantiate Private Key", e);
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException error)
                    {
                        throw new RuntimeException("Problem while closing stream.", error);
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("Private Key File: " + privateKeyFile.getAbsolutePath() + " Cannot be read");
        }

    }

    /**
     * Sets IV and cipher
     * 
     * @param mode
     * @return
     */
    private Cipher getCipher(int mode)
    {
        try
        {
            IvParameterSpec ivParams = null;
            if (ivSize > 0)
            {
                byte[] iv = new byte[ivSize];
                SecureRandom random = new SecureRandom(iv);
                random.nextBytes(iv);
                ivParams = new IvParameterSpec(iv);
            }
            Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(mode, symmetricKey ? secretKey : publicKey, ivParams);
            return cipher;

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new RuntimeException("Could not create cipher", e);
        }
    }

    /**
     * Creates a pair separated by the PAIR_SEPARATOR character. First element contains the encryption algorithm. Second
     * element contains cipher
     * 
     * @param cipherText
     * @return
     */
    private byte[] prependCipherConfig(byte[] cipherText, byte[] iv)
    {

        byte[] prepend = getCipherConfig();

        if (iv != null)
        {
            byte[] pair = new byte[prepend.length + iv.length + cipherText.length];
            ByteBuffer buffer = ByteBuffer.wrap(pair);
            buffer.put(prepend);
            buffer.put(iv);
            buffer.put(cipherText);
            return buffer.array();
        }

        byte[] pair = new byte[prepend.length + cipherText.length];
        ByteBuffer buffer = ByteBuffer.wrap(pair);
        buffer.put(prepend);
        buffer.put(cipherText);
        return buffer.array();
    }

    private byte[] getCipherConfig()
    {
        return encryptionAlgorithm.concat(CONFIG_SEPARATOR).concat(String.valueOf(keySize)).concat(CONFIG_SEPARATOR).getBytes();
    }

    /**
     * Extracts the Cipher from the full message that also contains the encryption algorithm.
     * 
     * @param encryptedMessageByteArray
     * @return
     */
    private int extractCipherConfig(byte[] encryptedMessageByteArray)
    {
        // Expected components: {encryptionAlgorithm};{keySize};{IV if CBC + encryptedText}
        int messageComponents = 3;
        String encryptedMessageString = new String(encryptedMessageByteArray);
        String[] config = encryptedMessageString.split(";", messageComponents);

        if (isValidConfig(config, messageComponents))
        {
            String encAlg = config[0];
            int keyS = Integer.valueOf(config[1]);
            setEncryptionAlgorithm(encAlg);
            setKeySize(keyS);
            setKeyAlgorithm();
            loadPrivateKeys();
            return getCipherConfig().length;
        }

        // No valid config detected, use default
        this.loadPrivateKeys();
        return 0;
    }

    /**
     * Validate the extracted configuration: - The full message needs to be able to be split in 3; - The first element
     * needs to be a valid encryption algorithm - The second element is the key size and needs to be a number greater
     * than 128
     * 
     * @param config
     * @param messageComponents
     * @return
     */
    private boolean isValidConfig(String[] config, int messageComponents)
    {
        if (config.length != messageComponents)
        {
            return false;
        }

        try
        {
            // First element is the encryption algorithm, attempt to instance it
            Cipher.getInstance(config[0]);
            // Second element is the key size. it needs to be a number greater that 128
            if (Integer.valueOf(config[1]) < 128)
            {
                return false;
            }
        }
        catch (NumberFormatException | NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            // Not a number or a valid algorithm
            return false;
        }

        return true;
    }

    /**
     * Extracts and sets the configuration from the message and returns only the iv+cipher
     * 
     * @param encryptedMessageByteArray
     * @return
     */
    private byte[] extractMessage(byte[] encryptedMessageByteArray)
    {
        int cipherConfigPosition = extractCipherConfig(encryptedMessageByteArray);
        return Arrays.copyOfRange(encryptedMessageByteArray, cipherConfigPosition, encryptedMessageByteArray.length);
    }

    /**
     * Loads keys (secret if symmetric key algorithm or public/private keys if asymmetric
     */
    private void loadPrivateKeys()
    {
        if (symmetricKey)
        {
            initSecret();
            return;
        }

        initPrivate();

    }

    /**
     * Only applicable to RSA. Depending on the algorithm and keySize the padding is different.
     * 
     * @return
     */
    private int getChunkSize()
    {
        String paddingScheme = encryptionAlgorithm.split("/", 3)[2].toUpperCase(Locale.ROOT);
        int padding = 0;

        if (paddingScheme.endsWith("PKCS1PADDING"))
        {
            padding = 11;
        }
        else if (paddingScheme.endsWith("MD5ANDMGF1PADDING"))
        {
            padding = 34;
        }
        else if (paddingScheme.endsWith("1ANDMGF1PADDING"))
        {
            padding = 42;
        }
        else if (paddingScheme.endsWith("224ANDMGF1PADDING"))
        {
            padding = 58;
        }
        else if (paddingScheme.endsWith("256ANDMGF1PADDING"))
        {
            padding = 66;
        }
        else if (paddingScheme.endsWith("384ANDMGF1PADDING"))
        {
            padding = 98;
        }
        else if (paddingScheme.endsWith("512ANDMGF1PADDING"))
        {
            padding = 130;
        }

        return (keySize / 8) - padding;
    }

}
