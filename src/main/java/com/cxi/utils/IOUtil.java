package com.cxi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class IOUtil {
    public IOUtil() {}
	public static HashMap<String,String> BUFF_POOL = new HashMap<>();
	public static HashMap<String,Object> OBJE_POOL = new HashMap<>();
	public static HashMap<String,String> PROP_POOL = new HashMap<>();
	public static String root = "D:\\MCData\\";
	/*public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}*/
	public static File createFile(String path) {
		if (!path.endsWith(".png")) path += ".txt";
		File file = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (Exception e) {
			logException(e);
		}
		return file;
	}

	public static void deleteFiles(String path) {
        try {
            File file = new File(path);
			if (!file.exists()) return;
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null && files.length > 0)
					for (File f:files) {
						deleteFiles(f.getPath());
					}
				file.delete();
			} else {
				file.delete();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static long countFilesSize(String path) {
		File file = new File(path);
		if (!file.exists()) return -1;
		long a = 0;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f:files) {
				a += countFilesSize(f.getPath());
			}
		} else {
			return file.length();
		}
		return a;
	}
	public static long countFilesNumber(String path) {
		File file = new File(path);
		if (!file.exists()) return -1;
		long a = 0;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f:files) {
				a += countFilesNumber(f.getPath());
			}
		} else {
			return 1;
		}
		return a;
	}
	public static void copyFiles(String oldPath, String newPath) {
		try {
			File file = new File(oldPath);
			if (!file.exists()) return;
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f:files) {
					copyFiles(f.getPath(), f.getPath().replace(oldPath, newPath));
				}
			} else {
				writeText(newPath, readText(oldPath));
			}
		} catch (Exception e) {
			logException(e);
		}
	}

	public static Object readObject(String path) {
		Object object = null;
		try {
			if (OBJE_POOL.containsKey(path)) return OBJE_POOL.get(path);
			File file = createFile(path);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			OBJE_POOL.put(path, object);
			ois.close();
			fis.close();
		} catch (Exception e) {
			logException(e);
		}
		return object;
	}

	public static void writeObject(Object object, String path) {
		try {
			File file = createFile(path);
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			OBJE_POOL.put(path, object);
			oos.close();
			fos.close();
		} catch (Exception e) {
			logException(e);
		}
	}

	public static String readText(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			if (BUFF_POOL.containsKey(path)) return BUFF_POOL.get(path);
			File file = createFile(path);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			char[] buff = new char[1024];
			while (isr.read(buff) != -1) {
				sb.append(buff);
			}
			BUFF_POOL.put(path, sb.toString());
			isr.close();
			fis.close();
		} catch (Exception e) {
			logException(e);
		}
		return sb.toString();
	}

	public static void writeText(String path, String text) {
		try {
			File file = createFile(path);
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osr = new OutputStreamWriter(fos);
			osr.write(text);
			BUFF_POOL.put(path, text);
			osr.close();
			fos.close();
		} catch (Exception e) {
			logException(e);
		}
	}

	public static String getExceptionMessage(Exception ex) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		ex.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

	public static void logException(Exception e) {
		//MsgUtil.mu.sendGroupMsg("错误:" + e.toString());
		String path =  root + "data/Exception/CBot" + MassUtil.getNowTime("yyyyMMdd") + ".log";
		String text = readText(path) + new Date() + "\n" + getExceptionMessage(e) + "\n\n";
		writeText( path,text);
		e.printStackTrace();
	}
	public static void writeStringProp(String path, String key, String value) {
		try {
			File file = createFile(path);
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			p.setProperty(key, value);
			PROP_POOL.put(path + "/" + key, value);
			p.store(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), "SEC YYDS");
		} catch (Exception e) {
			logException(e);
		}
	}

	public static void writeLongProp(String path, String key, long value) {
		writeStringProp(path, key, value + "");
	}

	public static String readStringProp(String path, String key, String value) {
		try {
			if (PROP_POOL.containsKey(path + "/" + key)) return PROP_POOL.get(path + "/" + key);
			File file = createFile(path);
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			value = p.getProperty(key, value);
			PROP_POOL.put(path + "/" + key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static long readLongProp(String path, String key, long value) {
		return Long.parseLong(readStringProp(path, key, value + ""));
	}


	public static String getRandomString(int length) {
		Random random = new Random();
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
        for (int i = 0;i < length;i++) {
			sb.append(str.charAt(random.nextInt(str.length())));
		}
		return sb.toString();
    }
	/*public static Pair<ArrayList<Pair<String,String>>,String> ph(String file, String name) throws Exception {
		String text = "";
		String text1 = "";
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<Pair<String,String>> ap = new ArrayList<Pair<String,String>>();
		ArrayList<Pair<String,String>> ap1 = new ArrayList<Pair<String,String>>();
		for (;(text = br.readLine()) != null;) {
			if (text.matches(".+=.+")) {
				text1 = text.substring(0, text.indexOf("="));
				text = text.substring(text.indexOf("="));
				text = text.replaceAll("[^0-9]*", "");
				ap.add(new Pair<String,String>(text1, text));
			}
		}
		List<Long> li = new ArrayList<Long>();
		for (int i = 0;i < ap.size();i++) {
			li.add(Long.parseLong(ap.get(i).second));
		}
		Collections.sort(li);
		for (int i = 0;i < li.size();i++) {
			for (int j = 0;j < ap.size();j++) {
				if (Long.parseLong(ap.get(j).second) == li.get(i)) {
					ap1.add(0, new Pair<String,String>(ap.get(j).first, ap.get(j).second));
					ap.remove(j);
				}
			}
		}
		for (int i = 0;i < ap1.size();i++) {
			if (ap1.get(i).first.equals(name + "")) return new Pair<ArrayList<Pair<String,String>>,String>(ap1, i + "");
		}
		br.close();
		fr.close();
		return new Pair<ArrayList<Pair<String,String>>,String>(ap1, null);
	}*/
}
