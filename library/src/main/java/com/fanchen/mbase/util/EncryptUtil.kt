package com.fanchen.mbase.util

import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


/**
 * 加密相关
 * Created by fanchen on 2018/9/4.
 */
object EncryptUtil {

    private val DES_Algorithm = "DES"
    ///////////////////////////////////////////////////////////////////////////
    // 哈希加密相关
    ///////////////////////////////////////////////////////////////////////////
    private val TripleDES_Algorithm = "DESede"
    private val AES_Algorithm = "AES"
    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    /**
     * DES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var DES_Transformation = "DES/ECB/NoPadding"
    /**
     * 3DES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var TripleDES_Transformation = "DESede/ECB/NoPadding"
    /**
     * AES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var AES_Transformation = "AES/ECB/NoPadding"

    /**
     * MD2加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptMD2ToString(data: String?): String {
        return encryptMD2ToString(data?.toByteArray())
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptMD2ToString(data: ByteArray?): String {
        return bytes2HexString(encryptMD2(data))
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptMD2(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD2")
    }

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptMD5ToString(data: String?): String {
        return encryptMD5ToString(data?.toByteArray())
    }

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @param salt 盐
     * @return 16进制加盐密文
     */
    fun encryptMD5ToString(data: String?, salt: String?): String {
        return bytes2HexString(encryptMD5((data + salt).toByteArray()))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptMD5ToString(data: ByteArray?): String {
        return bytes2HexString(encryptMD5(data))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @param salt 盐字节数组
     * @return 16进制加盐密文
     */
    fun encryptMD5ToString(data: ByteArray?, salt: ByteArray?): String {
        if (data?.size ?: -1 <= 0 || salt?.size ?: -1 <= 0) return ""
        val dataSalt = ByteArray((data?.size ?: 0) + (salt?.size ?: 0))
        System.arraycopy(data, 0, dataSalt, 0, (data?.size ?: 0))
        System.arraycopy(salt, 0, dataSalt, (data?.size ?: 0), (salt?.size ?: 0))
        return bytes2HexString(encryptMD5(dataSalt))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptMD5(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD5")
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的16进制密文
     */
    fun encryptMD5File2String(filePath: String): String {
        val file = if (isSpace(filePath)) null else File(filePath)
        return encryptMD5File2String(file)
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    fun encryptMD5File(filePath: String): ByteArray? {
        val file = if (isSpace(filePath)) null else File(filePath)
        return encryptMD5File(file)
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    fun encryptMD5File2String(file: File?): String {
        return bytes2HexString(encryptMD5File(file))
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    fun encryptMD5File(file: File?): ByteArray? {
        if (file == null) return null
        var fis: FileInputStream? = null
        val digestInputStream: DigestInputStream
        try {
            fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            digestInputStream = DigestInputStream(fis, md)
            val buffer = ByteArray(256 * 1024)
            while (true) {
                if (digestInputStream.read(buffer) <= 0) break
            }
            md = digestInputStream.getMessageDigest()
            return md.digest()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * SHA1加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptSHA1ToString(data: String?): String {
        return encryptSHA1ToString(data?.toByteArray())
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptSHA1ToString(data: ByteArray?): String {
        return bytes2HexString(encryptSHA1(data))
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptSHA1(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA1")
    }

    /**
     * SHA224加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptSHA224ToString(data: String?): String {
        return encryptSHA224ToString(data?.toByteArray())
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptSHA224ToString(data: ByteArray?): String {
        return bytes2HexString(encryptSHA224(data))
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptSHA224(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA224")
    }

    /**
     * SHA256加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptSHA256ToString(data: String?): String {
        return encryptSHA256ToString(data?.toByteArray())
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptSHA256ToString(data: ByteArray?): String {
        return bytes2HexString(encryptSHA256(data))
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptSHA256(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA256")
    }

    /**
     * SHA384加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptSHA384ToString(data: String?): String {
        return encryptSHA384ToString(data?.toByteArray())
    }

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptSHA384ToString(data: ByteArray?): String {
        return bytes2HexString(encryptSHA384(data))
    }

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptSHA384(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA384")
    }

    /**
     * SHA512加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    fun encryptSHA512ToString(data: String?): String {
        return encryptSHA512ToString(data?.toByteArray())
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    fun encryptSHA512ToString(data: ByteArray?): String {
        return bytes2HexString(encryptSHA512(data))
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    fun encryptSHA512(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA512")
    }

    /**
     * hash加密模板
     *
     * @param data      数据
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private fun hashTemplate(data: ByteArray?, algorithm: String = "MD5"): ByteArray? {
        try {
            if (data?.size ?: -1 <= 0) return null
            val md = MessageDigest.getInstance(algorithm)
            md.update(data).apply { return md.digest() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacMD5ToString(data: String?, key: String?): String {
        return encryptHmacMD5ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacMD5ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacMD5(data, key))
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacMD5(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacMD5")
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA1ToString(data: String?, key: String?): String {
        return encryptHmacSHA1ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA1ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacSHA1(data, key))
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacSHA1(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA1")
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA224ToString(data: String?, key: String?): String {
        return encryptHmacSHA224ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA224ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacSHA224(data, key))
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacSHA224(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA224")
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA256ToString(data: String?, key: String?): String {
        return encryptHmacSHA256ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA256ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacSHA256(data, key))
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacSHA256(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA256")
    }

    ///////////////////////////////////////////////////////////////////////////
    // DES加密相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * HmacSHA384加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA384ToString(data: String?, key: String?): String {
        return encryptHmacSHA384ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacSHA384加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA384ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacSHA384(data, key))
    }

    /**
     * HmacSHA384加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacSHA384(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA384")
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA512ToString(data: String?, key: String?): String {
        return encryptHmacSHA512ToString(data?.toByteArray(), key?.toByteArray())
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    fun encryptHmacSHA512ToString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptHmacSHA512(data, key))
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    fun encryptHmacSHA512(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA512")
    }

    /**
     * Hmac加密模板
     *
     * @param data      数据
     * @param key       秘钥
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private fun hmacTemplate(data: ByteArray?, key: ByteArray?, algorithm: String): ByteArray? {
        if (data == null || data.size == 0 || key == null || key.size == 0) return null
        try {
            val secretKey = SecretKeySpec(key, algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(secretKey).apply { return mac.doFinal(data) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return Base64密文
     */
    fun encryptDES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return base64Encode(encryptDES(data, key))
    }

    ///////////////////////////////////////////////////////////////////////////
    // 3DES加密相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * DES加密后转为16进制
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 16进制密文
     */
    fun encryptDES2HexString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptDES(data, key))
    }

    /**
     * DES加密
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 密文
     */
    fun encryptDES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, true)
    }

    /**
     * DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  8字节秘钥
     * @return 明文
     */
    fun decryptBase64DES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return decryptDES(base64Decode(data), key)
    }

    /**
     * DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  8字节秘钥
     * @return 明文
     */
    fun decryptHexStringDES(data: String, key: ByteArray): ByteArray? {
        return decryptDES(hexString2Bytes(data), key)
    }

    /**
     * DES解密
     *
     * @param data 密文
     * @param key  8字节秘钥
     * @return 明文
     */
    fun decryptDES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, false)
    }

    /**
     * 3DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return Base64密文
     */
    fun encrypt3DES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return base64Encode(encrypt3DES(data, key))
    }

    /**
     * 3DES加密后转为16进制
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return 16进制密文
     */
    fun encrypt3DES2HexString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encrypt3DES(data, key))
    }

    /**
     * 3DES加密
     *
     * @param data 明文
     * @param key  24字节密钥
     * @return 密文
     */
    fun encrypt3DES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, true)
    }

    ///////////////////////////////////////////////////////////////////////////
    // AES加密相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 3DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  24字节秘钥
     * @return 明文
     */
    fun decryptBase64_3DES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return decrypt3DES(base64Decode(data), key)
    }

    /**
     * 3DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  24字节秘钥
     * @return 明文
     */
    fun decryptHexString3DES(data: String, key: ByteArray): ByteArray? {
        return decrypt3DES(hexString2Bytes(data), key)
    }

    /**
     * 3DES解密
     *
     * @param data 密文
     * @param key  24字节密钥
     * @return 明文
     */
    fun decrypt3DES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, false)
    }

    /**
     * AES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return Base64密文
     */
    fun encryptAES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return base64Encode(encryptAES(data, key))
    }

    /**
     * AES加密后转为16进制
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 16进制密文
     */
    fun encryptAES2HexString(data: ByteArray?, key: ByteArray?): String {
        return bytes2HexString(encryptAES(data, key))
    }

    /**
     * AES加密
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 密文
     */
    fun encryptAES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, true)
    }

    /**
     * AES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    fun decryptBase64AES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return decryptAES(base64Decode(data), key)
    }

    /**
     * AES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    fun decryptHexStringAES(data: String, key: ByteArray): ByteArray? {
        return decryptAES(hexString2Bytes(data), key)
    }

    /**
     * AES解密
     *
     * @param data 密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    fun decryptAES(data: ByteArray?, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, false)
    }

    /**
     * DES加密模板
     *
     * @param data           数据
     * @param key            秘钥
     * @param algorithm      加密算法
     * @param transformation 转变
     * @param isEncrypt      `true`: 加密 `false`: 解密
     * @return 密文或者明文，适用于DES，3DES，AES
     */
    fun desTemplate(data: ByteArray?, key: ByteArray?, algorithm: String, transformation: String, isEncrypt: Boolean): ByteArray? {
        if (data == null || data.size == 0 || key == null || key.size == 0) return null
        try {
            val keySpec = SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance(transformation)
            val random = SecureRandom()
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, keySpec, random)
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    private fun bytes2HexString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[(bytes[i].toInt().ushr(4) and 0x0f).toInt()]
            ret[j++] = hexDigits[(bytes[i] and 0x0f).toInt()]
            i++
        }
        return String(ret)
    }

    private fun hexString2Bytes(hexStringx: String): ByteArray? {
        if (isSpace(hexStringx)) return null
        var hexString = hexStringx
        if (hexStringx.length % 2 != 0) {
            hexString = "0" + hexStringx
        }
        var len = hexString.length
        val hexBytes = hexString.toUpperCase().toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }

    private fun hex2Dec(hexChar: Char): Int {
        return if (hexChar >= '0' && hexChar <= '9') {
            hexChar - '0'
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            hexChar - 'A' + 10
        } else {
            throw IllegalArgumentException()
        }
    }

    fun base64Encode(input: ByteArray?): ByteArray {
        return Base64.encode(input, Base64.NO_WRAP)
    }

    fun base64Decode(input: ByteArray?): ByteArray {
        return Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * String转Base64字符串
     *
     * @param message the message
     * @return the string
     */
    fun stringToBase64(message: String): String {
        return Base64.encodeToString(message.toByteArray(), Base64.DEFAULT)
    }

    fun byte2Base64(message: ByteArray?): String {
        return Base64.encodeToString(message, Base64.DEFAULT)
    }

    /**
     * Base64字符串转bytes
     *
     * @param base64Message the message
     * @return the string
     */
    fun base64ToByte(base64Message: String): ByteArray {
        return Base64.decode(base64Message, Base64.DEFAULT)
    }

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    fun string2MD5(str: String): String {
        try {
            // 生成一个MD5加密计算摘要
            val md = MessageDigest.getInstance("MD5")
            // 计算md5函数
            md.update(str.toByteArray())
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return BigInteger(1, md.digest()).toString(16)
        } catch (e: Exception) {
            return ""
        }

    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}