package com.lynn.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";


    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>(2);
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) throws Exception{
        Map<String, String> keyMap = RSAUtils.createKeys(1024);
        String  publicKey = keyMap.get("publicKey");
        String  privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJxmWQdpI3R/DcJYaDNy4944o900od1zadHootdrOGHMWF7vw2oyuGzI1N/frmxoVLaUAMrcLMBLMfhPRtP4acnvuOgM4/7RKq5scrAAi/znSVPRDFL5165QeDb64diF2EjDk0KZnRKQ1qXDyKA/XJL7ZMQhhqfeVqQ8G9khKpGzAgMBAAECgYEAj+5AkGlZj6Q9bVUez/ozahaF9tSxAbNs9xg4hDbQNHByAyxzkhALWVGZVk3rnyiEjWG3OPlW1cBdxD5w2DIMZ6oeyNPA4nehYrf42duk6AI//vd3GsdJa6Dtf2has1R+0uFrq9MRhfRunAf0w6Z9zNbiPNSd9VzKjjSvcX7OTsECQQD20kekMToC6LZaZPr1p05TLUTzXHvTcCllSeXWLsjVyn0AAME17FJRcL9VXQuSUK7PQ5Lf5+OpjrCRYsIvuZg9AkEAojdC6k3SqGnbtftLfGHMDn1fe0nTJmL05emwXgJvwToUBdytvgbTtqs0MsnuaOxMIMrBtpbhS6JiB5Idb7GArwJAfKTkmP5jFWT/8dZdBgFfhJGv6FYkEjrqLMSM1QT7VzvStFWtPNYDHC2b8jfyyAkGvpSZb4ljZxUwBbuh5QgM4QJBAJDrV7+lOP62W9APqdd8M2X6gbPON3JC09EW3jaObLKupTa7eQicZsX5249IMdLQ0A43tanez3XXo0ZqNhwT8wcCQQDUubpNLwgAwN2X7kW1btQtvZW47o9CbCv+zFKJYms5WLrVpotjkrCgPeuloDAjxeHNARX8ZTVDxls6KrjLH3lT";
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" +
                "有关的命令，但看到大批盛装的官员来临，也就\n" +
                "以为确系举行大典，因而未加询问。进大明门即\n" +
                "为皇城。文武百官看到端门午门之前气氛平静，\n" +
                "城楼上下也无朝会的迹象，既无几案，站队点名\n" +
                "的御史和御前侍卫“大汉将军”也不见踪影，不免\n" +
                "心中揣测，互相询问：所谓午朝是否讹传？";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        String encodedData = RSAUtils.publicEncrypt("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiQp9gF9DKE1Qz1Y0ttDhL9m9TlCsaxkMzLs0dgZNLMgzC91JA5rWYudCtqSK8SulFzMQ75JxPup6uPFx/W6G0WcHE4oBE4jlJJlWoZrAvdvLn3eWSqBDnHSaKsjJrtVx3V12+ntUcCASTVbdYiHiQcVv1hZUPJ5kbwjFXNqGp/wIDAQAB", RSAUtils.getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcZlkHaSN0fw3CWGgzcuPeOKPdNKHdc2nR6KLXazhhzFhe78NqMrhsyNTf365saFS2lADK3CzASzH4T0bT+GnJ77joDOP+0SqubHKwAIv850lT0QxS+deuUHg2+uHYhdhIw5NCmZ0SkNalw8igP1yS+2TEIYan3lakPBvZISqRswIDAQAB"));
        System.out.println("密文：\r\n" + encodedData);
        System.out.println("ZB7y2ntCL++cTXoI3kOgrDZ8BLz/5LeraPMMQI+XdU7PjB6Sw/4cRcbcD8eOJvR0sRPu8CZ+n4Bzr9FQq0P3fEPB6JvXWRetcO6sROOKxRgucktIM2rjdcdC+iiMQVXlT+2+TxzgL2IxVcDC2GYguPf8fKHusn/pdO2N2FOu91pce5ev57QIhzQivsuNr3lxtWo/LPcK5YT1tM2VoqYWUxybXjtsDxVPOKNu9KKX5+1QB7kyj2euxZaxtPp5VxvqR0ql03oyO32VKLUfHpOkjjfnKeq3jOOwGkrg1+BcNAPFT4xETAZRCd80BE+P/LWg0krMaBQL9d/5g0hrZ/FvcA==".length());
        String decodedData = RSAUtils.privateDecrypt(encodedData, RSAUtils.getPrivateKey("MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJxmWQdpI3R/DcJYaDNy4944o900od1zadHootdrOGHMWF7vw2oyuGzI1N/frmxoVLaUAMrcLMBLMfhPRtP4acnvuOgM4/7RKq5scrAAi/znSVPRDFL5165QeDb64diF2EjDk0KZnRKQ1qXDyKA/XJL7ZMQhhqfeVqQ8G9khKpGzAgMBAAECgYEAj+5AkGlZj6Q9bVUez/ozahaF9tSxAbNs9xg4hDbQNHByAyxzkhALWVGZVk3rnyiEjWG3OPlW1cBdxD5w2DIMZ6oeyNPA4nehYrf42duk6AI//vd3GsdJa6Dtf2has1R+0uFrq9MRhfRunAf0w6Z9zNbiPNSd9VzKjjSvcX7OTsECQQD20kekMToC6LZaZPr1p05TLUTzXHvTcCllSeXWLsjVyn0AAME17FJRcL9VXQuSUK7PQ5Lf5+OpjrCRYsIvuZg9AkEAojdC6k3SqGnbtftLfGHMDn1fe0nTJmL05emwXgJvwToUBdytvgbTtqs0MsnuaOxMIMrBtpbhS6JiB5Idb7GArwJAfKTkmP5jFWT/8dZdBgFfhJGv6FYkEjrqLMSM1QT7VzvStFWtPNYDHC2b8jfyyAkGvpSZb4ljZxUwBbuh5QgM4QJBAJDrV7+lOP62W9APqdd8M2X6gbPON3JC09EW3jaObLKupTa7eQicZsX5249IMdLQ0A43tanez3XXo0ZqNhwT8wcCQQDUubpNLwgAwN2X7kW1btQtvZW47o9CbCv+zFKJYms5WLrVpotjkrCgPeuloDAjxeHNARX8ZTVDxls6KrjLH3lT"));
        System.out.println("解密后文字: \r\n" + decodedData);

    }
}
