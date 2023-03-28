package com;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CBC（Cipher Block Chaining）算法是一种常用的分组密码算法，其优缺点如下：
 *
 * 优点：
 *
 * CBC算法相对于ECB（Electronic Codebook）算法更为安全，每个明文块都是先与前一个密文块进行异或操作后再加密，相邻的明文块经过加密后得到的密文块也是不同的，这样可以增加攻击者破解密码的难度。
 * CBC算法支持使用一个初始化向量（IV）来随机化加密结果，这增加了密码系统的安全性，使得攻击者很难通过猜测或者猜测密码系统的工作方式来推断出秘密信息。
 * CBC算法适用于保护机密数据的传输和存储，因为它可以支持不同长度的输入数据，并且加密输出结果没有结构性，可以更好的保证数据的安全性。
 * 缺点：
 *
 * CBC算法需要使用补位填充，因为明文块有可能不足一个分组大小，在数据最后一个分组填充0或其他内容，不能为分组大小时就要进行填充，这样会使得实现更为复杂。
 * CBC算法还存在一些安全问题，如Padding Oracle Attacks等，需要采用一些额外措施进行防护。
 * CBC算法对于错误恢复能力较弱，如果密文中的某些块发生错误，则后续块的恢复也会受到影响。
 * 综上所述，CBC算法是一种相对较为安全的密码算法，可以有效保护机密数据的传输和存储，但是它也存在一些缺陷，需要在实现过程中加强安全防护，同时在具体应用场景中进行权衡。
 */
public class CBC {
    public static void main(String[] args) {
        String inputFile = "input.txt";         // 原始文件路径
        String encryptedFile = "encrypted.txt"; // 加密后的文件路径
        String decryptedFile = "decrypted.txt"; // 解密后的文件路径
        String keyString = "0123456789abcdef";  // 秘钥
        String ivString = "fedcba9876543210";   // 初始化向量

        // 加密文件
        encryptFile(inputFile, encryptedFile, keyString, ivString);

        // 解密文件
        decryptFile(encryptedFile, decryptedFile, keyString, ivString);
    }

    public static void encryptFile(String inputFile, String outputFile, String keyString, String ivString) {
        try {
            // 读取原始文件和初始化向量
            byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
            byte[] ivBytes = ivString.getBytes("UTF-8");

            // 创建秘钥和初始化向量
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            // 加密文件并写入输出文件
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(String inputFile, String outputFile, String keyString, String ivString) {
        try {
            // 读取加密文件和初始化向量
            byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
            byte[] ivBytes = ivString.getBytes("UTF-8");

            // 创建秘钥和初始化向量
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            // 解密文件并写入输出文件
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
