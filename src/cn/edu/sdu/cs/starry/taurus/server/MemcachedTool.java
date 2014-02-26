package cn.edu.sdu.cs.starry.taurus.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.code.yanf4j.core.impl.StandardSocketOption;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.ByteUtils;

/**
 * @author SDU.xccui
 */
public class MemcachedTool implements CacheTool {
    private static final int CONVERT_KEY_BYTE_LENGTH = 230;
    private static final int MAX_KEY_LENGTH = 20000;
    private MemcachedClientBuilder builder;
    private MemcachedClient client;

    public MemcachedTool(List<InetSocketAddress> addressList)
            throws IOException {
        builder = new XMemcachedClientBuilder(addressList);
        builder.setConnectionPoolSize(10);
        builder.setSocketOption(StandardSocketOption.SO_RCVBUF, 64 * 1024);
        builder.setSocketOption(StandardSocketOption.SO_SNDBUF, 32 * 1024);
        builder.setSocketOption(StandardSocketOption.TCP_NODELAY, false);
        builder.getConfiguration().setSessionIdleTimeout(10000);
        builder.getConfiguration().setStatisticsServer(true);
        builder.setCommandFactory(new BinaryCommandFactory());
        client = builder.build();
        client.setEnableHeartBeat(true);
        client.setMergeFactor(50);
    }

    @Override
    public void set(String key, byte[] bytes) {
        set(key, bytes, 0);
    }

    @Override
    public void set(String key, byte[] bytes, int time) {
        if (key.length() > MAX_KEY_LENGTH) {
            return;
        }
        byte[] keyBytes = key.getBytes(ByteUtils.DEFAULT_CHARSET);
        if (keyBytes.length > CONVERT_KEY_BYTE_LENGTH) {
            byte[] oldBytes = bytes;
            key = toMD5String(keyBytes);
            bytes = toNewValueBytes(keyBytes, oldBytes);
        }
        if (null != client && null != key) {
            try {
                client.set(key, time, bytes);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MemcachedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] get(String key) {
        if (null == client || null == key || key.length() > MAX_KEY_LENGTH) {
            return null;
        }
        byte[] keyBytes = key.getBytes(ByteUtils.DEFAULT_CHARSET);
        if (keyBytes.length > CONVERT_KEY_BYTE_LENGTH) {
            key = toMD5String(keyBytes);
        }
        try {
            byte[] bytes = client.get(key);
            if (keyBytes.length > CONVERT_KEY_BYTE_LENGTH && null != bytes) {
                return fromNewValueBytes(keyBytes, bytes);
            }
            return bytes;
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(String key) {
        if (null != client) {
            try {
                client.delete(key);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MemcachedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        if (null != client) {
            try {
                client.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String toMD5String(byte[] keyBytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(keyBytes);
            byte[] b = md5.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static byte[] toNewValueBytes(byte[] keyBytes, byte[] bytes) {
        byte[] newBytes = new byte[4 + keyBytes.length + bytes.length];
        newBytes[3] = (byte) (keyBytes.length >> 24);
        newBytes[2] = (byte) (keyBytes.length >> 16);
        newBytes[1] = (byte) (keyBytes.length >> 8);
        newBytes[0] = (byte) (keyBytes.length >> 0);
        System.arraycopy(keyBytes, 0, newBytes, 4, keyBytes.length);
        System.arraycopy(bytes, 0, newBytes, 4 + keyBytes.length, bytes.length);
        return newBytes;
    }

    public static byte[] fromNewValueBytes(byte[] keyBytes, byte[] bytes) {
        int keyLength = ((((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16)
                | ((bytes[1] & 0xff) << 8) | ((bytes[0] & 0xff) << 0)));
        byte[] valueKeyBytes = Arrays.copyOfRange(bytes, 4, 4 + keyLength);
        if (Arrays.equals(keyBytes, valueKeyBytes)) {
            return Arrays.copyOfRange(bytes, 4 + keyLength, bytes.length);
        }
        return null;
    }
}
