package com.cxi.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpUtil {
    public HttpUtil() {
    }

    public static String baseUrl = "https://cxi.cmvip.cn/";

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static byte[] sendGetByte(String url) {
        byte[] result = null;
        try {
            // 打开和URL之间的连接
            URLConnection connection = geneUrlConn(url);

            int outime = 30000;

            connection.setConnectTimeout(outime);
            connection.setReadTimeout(outime);

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; U; Intel 10.0.0; zh-cn; I8 8700K Build/OPR1.170623.032) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/61.0.3163.128 Mobile Safari/537.36 XiaoMi/MiuiBrowser/10.3.4");
            connection.setRequestProperty("Content-type", "text/html");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("contentType", "utf-8");
            // 建立实际的连接
            connection.connect();
			/*
			 // 获取所有响应头字段
			 Map<String, List<String>> map = connection.getHeaderFields();
			 // 遍历所有的响应头字段
			 for (String key : map.keySet()) {
			 System.out.println(key + "--->" + map.get(key));
			 }*/
            result = readInputStream(connection.getInputStream());

        } catch (Exception e) {
            IOUtil.logException(e);

        }

        return result;
    }

    public static String sendGet(String url) {
        try {
            byte[] data = sendGetByte(new URI(url).toASCIIString());

            if (data != null) return new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            IOUtil.logException(e);
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static byte[] sendPostByte(String url, byte[] param) {
        OutputStream out = null;
        BufferedReader in = null;

        byte[] result = null;
        try {
            // 打开和URL之间的连接
            URLConnection conn = geneUrlConn(url);

            int outime = 30000;

            conn.setConnectTimeout(outime);
            conn.setReadTimeout(outime);

            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; U; Intel 10.0.0; zh-cn; I8 8700K Build/OPR1.170623.032) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/61.0.3163.128 Mobile Safari/537.36 XiaoMi/MiuiBrowser/10.3.4");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1.获取URLConnection对象对应的输出流
            //out = conn.getOutputStream();
            //2.中文有乱码的需要将PrintWriter改为如下
            //out=new OutputStreamWriter(conn.getOutputStream(),"UTF-8")
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            result = readInputStream(conn.getInputStream());

        } catch (Exception e) {
            IOUtil.logException(e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                IOUtil.logException(ex);
            }
        }
        return result;
    }

    public static String sendPost(String url, String param) {
        byte[] data = sendPostByte(url, param.getBytes());

        if (data != null) return new String(data);

        return null;
    }

    /**
     * 忽视证书HostName
     */
    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslsession) {
            //System.out.println("WARNING: Hostname is not matched for cert.");
            return true;
        }
    };

    /**
     * Ignore Certification
     */
    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };

    /**
     * 获取UrlConn
     */
    public static URLConnection geneUrlConn(String urlstr) throws Exception {
        URL url = new URL(urlstr);
        if (urlstr.startsWith("https://")) {
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();

            // Prepare SSL Context
            TrustManager[] tm = {ignoreCertificationTrustManger};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, new java.security.SecureRandom());


            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            httpsConnection.setSSLSocketFactory(ssf);

            return httpsConnection;
        } else {
            return url.openConnection();
        }
    }

    //取出流
    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

} 
