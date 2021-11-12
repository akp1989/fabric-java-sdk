package com.java.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption {
	private Decoder decoder = Base64.getDecoder();

    public byte[] encryptionRSA(PublicKey publicKey, byte[] plainTextBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //byte[] plainTextBytes = plainText.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainTextBytes);
    }

    public byte[] decryptionRSA(PrivateKey privateKey, byte[] plainTextBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //byte[] plainTextBytes = plainText.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(plainTextBytes);
    }

    public PublicKey getPublicKeyFromByte(byte[] publicKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        //byte[] decodedKeyByte = decoder.decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByte);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
    }

    public PrivateKey getPrivateKeyFromByte(byte[] privateKeyByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        //byte[] decodedKeyByte = decoder.decode(publicKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
    }
}
