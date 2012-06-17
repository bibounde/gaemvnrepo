package com.bibounde.gaemvnrepo.server.service.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GravatarUtil {

    /**
     * Retrieves gravatar URL
     * @param email
     * @return url
     */
    public static String getGravatarUrl(String email) {
        return getGravatarUrl(email, -1);
    }
    
    /**
     * Retrieves gravatar URL
     * @param email
     * @param size
     * @return url
     */
    public static String getGravatarUrl(String email, int size) {
        String hash = md5Hex(email);
        StringBuilder ret = new StringBuilder("http://www.gravatar.com/avatar/");
        ret.append(hash).append("?d=mm");
        if (size > 0) {
            ret.append("&s=").append(size);
        }
        return ret.toString();
    }
    
    private static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i]
            & 0xFF) | 0x100).substring(1,3));        
        }
        return sb.toString();
    }
    private static String md5Hex (String message) {
        try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return hex (md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}
