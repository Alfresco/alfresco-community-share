/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
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

public class ShareStringEncryption
{

    static final String CMD_INITKEY = "initkey";
    static final String CMD_ENCRYPT = "encrypt";
    static final String CMD_VALIDATE = "validate";

    static final String USAGE = "USAGE : " + ShareStringEncryption.class.getName() + " " + CMD_INITKEY + " | " + CMD_ENCRYPT + " | " + CMD_VALIDATE
            + " <shared dir> ";
    static final String USAGE_1 = " initkey : initialise the public and private keystores";
    static final String USAGE_2 = " encrypt : encrypt a value ";
    static final String USAGE_3 = " validate : compare an encrypted value with a value to see if they match";

    private static final int USAGE_EXIT_CODE = 255;
    private static final int ERROR_EXIT_CODE = 1;
    private static final int SUCCESS_EXIT_CODE = 0;

    static final String USAGE_INITKEY = "USAGE : " + ShareStringEncryption.class.getName() + " " + CMD_INITKEY + " <shared dir> ";
    static final String USAGE_ENCRYPT = "USAGE : " + ShareStringEncryption.class.getName() + " " + CMD_ENCRYPT + " <shared dir> [value to encrypt]";
    static final String USAGE_DECRYPT = "USAGE : " + ShareStringEncryption.class.getName() + " " + CMD_VALIDATE
            + " <shared dir> encrypted_value [value]";

    private static final String PROP_KEY_SIZE = "props.encryption.keySize";
    private static final String PROP_ENCRYPTION_ALGORITHM = "props.encryption.encryptionAlgorithm";
    private static final String PROP_LEGACY_MODE = "props.encryption.legacyMode";
    
    /**
     * Main command line program
     * 
     * @return 0 success
     * @return 1 error
     * @return 255 usage failure
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Alfresco Share  Encrypted String Tool");
            System.err.println(USAGE);
            System.err.println("");
            System.err.println(USAGE_1);
            System.err.println(USAGE_2);
            System.err.println(USAGE_3);

            System.exit(USAGE_EXIT_CODE);
        }
        PublicPrivateKeyShareStringEncryptor enc = new PublicPrivateKeyShareStringEncryptor();
        String shareExtensionsDirectory = null;
        
        String propKeySize = System.getProperty(PROP_KEY_SIZE);
        String propEncryptionAlgorithm = System.getProperty(PROP_ENCRYPTION_ALGORITHM);
        String propLegacyMode = System.getProperty(PROP_LEGACY_MODE);

        if (propKeySize != null && !propKeySize.isBlank())
        {
            enc.setKeySize(Integer.parseInt(propKeySize));
        }

        if (propEncryptionAlgorithm != null && !propEncryptionAlgorithm.isBlank())
        {
            enc.setEncryptionAlgorithm(propEncryptionAlgorithm);
        }

        if (propLegacyMode != null && !propLegacyMode.isBlank() && propLegacyMode.equalsIgnoreCase("true"))
        {
            enc.setLegacyMode(true);
        }

        enc.setKeyAlgorithm();

        if (args.length > 1)
        {
            shareExtensionsDirectory = args[1];
            if (!args[0].equalsIgnoreCase(CMD_INITKEY))
            {
                enc.setKeyLocation(shareExtensionsDirectory);
            }
        }
        if (args[0].equalsIgnoreCase(CMD_INITKEY))
        {
            if (args.length < 2)
            {
                System.err.println(USAGE_INITKEY);
                System.exit(USAGE_EXIT_CODE);
            }
            try
            {
                enc.createKeyFiles(shareExtensionsDirectory);

                System.out.println("The key files have been generated, please set permissions on the private key to keep it protected.");
                System.exit(SUCCESS_EXIT_CODE);

            }
            catch (Throwable t)
            {
                System.err.println("unable to initialise keys");
                t.printStackTrace(System.err);
                System.exit(ERROR_EXIT_CODE);
            }
        }
        else if (args[0].equalsIgnoreCase(CMD_ENCRYPT))
        {
            if (args.length < 2)
            {
                System.err.println(USAGE_ENCRYPT);
                System.exit(USAGE_EXIT_CODE);
            }
            String password = null;
            if (args.length > 2)
            {
                password = args[2];
            }
            else
            {
                password = enc.getPasswordFromConsole();
            }

            try
            {
                if (enc.legacyMode)
                {
                    System.out.println("Legacy mode enabled, using default encryption");
                    enc.setEncryptionAlgorithm(PublicPrivateKeyShareStringEncryptor.DEFAULT_ENCRYPTION_ALGORITHM);
                    enc.setKeySize(PublicPrivateKeyShareStringEncryptor.DEFAULT_KEY_SIZE);
                    enc.setKeyAlgorithm();
                }

                System.out.println("Key size: " + enc.keySize);
                System.out.println("Key algorithm: " + enc.keyAlgorithm);
                System.out.println("Encription Algorithm: " + enc.encryptionAlgorithm);

                if (enc.symmetricKey)
                {
                    enc.initSecret();
                }
                else
                {
                    enc.initPublic();
                }
                
                System.out.println(enc.encrypt(password));
                System.exit(SUCCESS_EXIT_CODE);
            }
            catch (Throwable t)
            {
                System.err.println("Error : Unable to encrypt : " + t.getMessage());
                t.printStackTrace(System.err);
                System.exit(ERROR_EXIT_CODE);
            }
        }
        else if (args[0].equalsIgnoreCase(CMD_VALIDATE))
        {
            if (args.length < 3)
            {
                System.err.println(USAGE_DECRYPT);
                System.exit(USAGE_EXIT_CODE);
            }
            String password = null;
            if (args.length > 3)
            {
                password = args[3];
            }
            else
            {
                password = enc.getPasswordFromConsole();
            }
            String encryptedValue = args[2];

            try
            {
                String decryptedValue = enc.decrypt(encryptedValue);

                if (decryptedValue.equals(password))
                {
                    System.out.println("Key size: " + enc.keySize);
                    System.out.println("Key algorithm: " + enc.keyAlgorithm);
                    System.out.println("Encription Algorithm: " + enc.encryptionAlgorithm);
                    System.out.println("The value and encrypted value MATCH");
                    System.exit(SUCCESS_EXIT_CODE);
                }
                else
                {
                    System.err.println("The value and encrypted value DO NOT MATCH");
                    System.exit(ERROR_EXIT_CODE);
                }
            }
            catch (Throwable t)
            {
                System.err.println("Error : Unable to validate :" + t.getMessage());
                t.printStackTrace(System.err);
                System.exit(ERROR_EXIT_CODE);
            }
        }
        else
        {
            System.err.println(USAGE);
            System.err.println("");
            System.err.println(USAGE_1);
            System.err.println(USAGE_2);
            System.err.println(USAGE_3);

            System.err.println("");
            System.err.println("BAD COMMAND: " + args[0]);
            System.exit(USAGE_EXIT_CODE);
        }

        // Don't expect to get here
        System.exit(ERROR_EXIT_CODE);
    }
}
