package com.common.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final String TAG = "FileUtil";

    /**
     * 文件是否存在
     */
    public static boolean fileIsExist(String filePath) {
        if (filePath == null || filePath.length() < 1) {
            Logger.e(TAG, "param invalid, filePath: " + filePath);
            return false;
        }

        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 读文件
     */
    public static InputStream readFile(String filePath) {
        if (null == filePath) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return null;
        }

        InputStream is = null;

        try {
            if (fileIsExist(filePath)) {
                File f = new File(filePath);
                is = new FileInputStream(f);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.e(TAG, "Exception, ex: " + ex.toString());
            return null;
        } finally {
            close(is);
        }
        return is;
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }

        File file = new File(filePath);

        if (file.exists()) {
            return true;
        }

        file.mkdirs();
        return true;
    }

    /**
     * 循环删除文件
     */
    public static boolean deleteDirectory(String filePath) {
        if (null == filePath || filePath.trim().length() == 0) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        }
        File file = new File(filePath);
        return deleteDirectory(file);
    }

    /**
     * 循环删除文件
     */
    public static boolean deleteDirectory(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    // Logger.d("delete filePath: " + list[i].getAbsolutePath());
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }

        file.delete();
        return true;
    }

    /**
     * 写文件
     */
    public static boolean writeFile(String filePath, InputStream inputStream) {
        if (null == filePath || filePath.length() < 1) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                deleteDirectory(filePath);
            }

            String pth = filePath.substring(0, filePath.lastIndexOf("/"));
            boolean ret = createDirectory(pth);
            if (!ret) {
                Logger.e(TAG, "createDirectory fail path = " + pth);
                return false;
            }

            boolean ret1 = file.createNewFile();
            if (!ret1) {
                Logger.e(TAG, "createNewFile fail filePath = " + filePath);
                return false;
            }

            fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[BUFFER_SIZE];
            int c = inputStream.read(buf);
            while (-1 != c) {
                fileOutputStream.write(buf, 0, c);
                c = inputStream.read(buf);
            }
            fileOutputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 写文件
     */
    public static boolean writeFile(String filePath, String fileContent) {
        return writeFile(filePath, fileContent, false);
    }

    /**
     * 写文件
     */
    public static boolean writeFile(String filePath, String fileContent,
                                    boolean append) {
        if (null == filePath || fileContent == null || filePath.length() < 1
                || fileContent.length() < 1) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath
                    + ", fileContent: " + fileContent);
            return false;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(file,
                    append));
            output.write(fileContent);
            output.flush();
            output.close();
        } catch (IOException ioe) {
            Logger.e(TAG, "writeFile ioe: " + ioe.toString());
            return false;
        }

        return true;
    }

    public static boolean writeFile(String filePath, byte[] content,
                                    boolean append) {
        if (null == filePath || null == content) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath
                    + ", content: " + content);
            return false;
        }

        FileOutputStream fos = null;
        try {
            File pf = null;
            int index = filePath.lastIndexOf("/");
            if (index > -1) {
                String pth = filePath.substring(0, index);
                if (!append) {
                    pf = new File(pth);
                    if (pf.exists() && !pf.isDirectory()) {
                        pf.delete();
                    }
                    pf = new File(filePath);
                    if (pf.exists()) {
                        if (pf.isDirectory())
                            FileUtil.deleteDirectory(filePath);
                        else
                            pf.delete();
                    }
                }
                pf = new File(pth + File.separator);
                if (!pf.exists()) {
                    if (!pf.mkdirs()) {
                        Logger.e(TAG, "Can't make dirs, path=" + pth);
                        return false;
                    }
                }
            }
            pf = new File(filePath);

            fos = new FileOutputStream(filePath, append);
            fos.write(content);
            fos.flush();
            fos.close();
            fos = null;
            pf.setLastModified(System.currentTimeMillis());

            return true;

        } catch (Exception ex) {
            Logger.e(TAG, "Exception, ex: " + ex.toString());
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception ex) {
                }
                ;
            }
        }
        return false;
    }

    /**
     * 获得文件大小
     */
    public static long getFileSize(String filePath) {
        if (null == filePath) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return 0;
        }

        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return 0;
        }

        return file.length();
    }

    /**
     * 文件修改时间
     */
    public static long getFileModifyTime(String filePath) {
        if (null == filePath) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return 0;
        }

        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return 0;
        }

        return file.lastModified();
    }

    /**
     * 设置文件修改时间
     */
    public static boolean setFileModifyTime(String filePath, long modifyTime) {
        if (null == filePath) {
            Logger.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        }

        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return false;
        }

        return file.setLastModified(modifyTime);
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(ContentResolver cr, String fromPath,
                                   String destUri) {
        if (null == cr || null == fromPath || fromPath.length() < 1
                || null == destUri || destUri.length() < 1) {
            Logger.e(TAG, "copyFile Invalid param. cr=" + cr + ", fromPath="
                    + fromPath + ", destUri=" + destUri);
            return false;
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(fromPath);
            // check output uri
            String path = null;
            Uri uri = null;

            String lwUri = destUri.toLowerCase();
            if (lwUri.startsWith("content://")) {
                uri = Uri.parse(destUri);
            } else if (lwUri.startsWith("file://")) {
                uri = Uri.parse(destUri);
                path = uri.getPath();
            } else {
                path = destUri;
            }

            // open output
            if (null != path) {
                File fl = new File(path);
                String pth = path.substring(0, path.lastIndexOf("/"));
                File pf = new File(pth);

                if (pf.exists() && !pf.isDirectory()) {
                    pf.delete();
                }

                pf = new File(pth + File.separator);

                if (!pf.exists()) {
                    if (!pf.mkdirs()) {
                        Logger.e(TAG, "Can't make dirs, path=" + pth);
                    }
                }

                pf = new File(path);
                if (pf.exists()) {
                    if (pf.isDirectory())
                        deleteDirectory(path);
                    else
                        pf.delete();
                }

                os = new FileOutputStream(path);
                fl.setLastModified(System.currentTimeMillis());
            } else {
                os = new ParcelFileDescriptor.AutoCloseOutputStream(
                        cr.openFileDescriptor(uri, "w"));
            }

            // copy file
            byte[] dat = new byte[BUFFER_SIZE];
            int i = is.read(dat);
            while (-1 != i) {
                os.write(dat, 0, i);
                i = is.read(dat);
            }

            is.close();
            is = null;

            os.flush();
            os.close();
            os = null;

            return true;

        } catch (Exception ex) {
            Logger.e(TAG, "Exception, ex: " + ex.toString());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
                ;
            }
            if (null != os) {
                try {
                    os.close();
                } catch (Exception ex) {
                }
                ;
            }
        }
        return false;
    }

    public static byte[] readAll(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        byte[] buf = new byte[BUFFER_SIZE];
        int c = is.read(buf);
        while (-1 != c) {
            baos.write(buf, 0, c);
            c = is.read(buf);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    /**
     * 读文件
     */
    public static byte[] readFile(Context ctx, Uri uri) {
        if (null == ctx || null == uri) {
            Logger.e(TAG, "Invalid param. ctx: " + ctx + ", uri: " + uri);
            return null;
        }

        InputStream is = null;
        String scheme = uri.getScheme().toLowerCase();
        if (scheme.equals("file")) {
            is = readFile(uri.getPath());
        }

        try {
            is = ctx.getContentResolver().openInputStream(uri);
            if (null == is) {
                return null;
            }

            byte[] bret = readAll(is);
            is.close();
            is = null;

            return bret;
        } catch (FileNotFoundException fne) {
            Logger.e(TAG, "FilNotFoundException, ex: " + fne.toString());
        } catch (Exception ex) {
            Logger.e(TAG, "Exception, ex: " + ex.toString());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
                ;
            }
        }
        return null;
    }

    /**
     * 读文件
     */
    public static String readAssertFile(Context context, String fileName) {
        if (null == context || null == fileName) {
            return null;
        }

        InputStream is = null;

        try {
            is = context.getAssets().open(fileName);
            return IOUtils.toString(is, "UTF-8");

        } catch (FileNotFoundException fne) {
            Logger.e(TAG, "FilNotFoundException, ex: " + fne.toString());
        } catch (Exception ex) {
            Logger.e(TAG, "Exception, ex: " + ex.toString());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    /**
     * 复制Assert文件
     */
    public static boolean copyAssertFile(Context context, String fileName, String savePath) {
        InputStream is = null;
        OutputStream out = null;
        try {
            is = context.getAssets().open(fileName);
            out = new FileOutputStream(savePath);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.flush();
            return true;
        } catch (Exception e) {
            Logger.e("copyAssertFile, error=" + e);
        } finally {
            close(is);
            close(out);
        }

        return false;
    }

    /**
     * 复制Assert数据库文件
     */
    public static boolean copyAssertDbFile(Context context, String fileName, String dbName) {
        InputStream is = null;
        OutputStream out = null;
        try {
            is = context.getAssets().open(dbName);
            String outFileName = context.getDatabasePath(dbName).getAbsolutePath();
            // Open the empty db as the output stream
            out = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.flush();
            return true;
        } catch (Exception e) {
            Logger.e("copyAssertDbFile, error=" + e);
        } finally {
            close(is);
            close(out);
        }

        return false;
    }


    /*************
     * ZIP file operation
     ***************/
    public static boolean readZipFile(String zipFileName, StringBuffer crc) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(
                    zipFileName));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                long size = entry.getSize();
                crc.append(entry.getCrc() + ", size: " + size);
            }
            zis.close();
        } catch (Exception ex) {
            Logger.e(TAG, "Exception: " + ex.toString());
            return false;
        }
        return true;
    }

    public static byte[] readGZipFile(String zipFileName) {
        if (fileIsExist(zipFileName)) {
            Logger.i(TAG, "zipFileName: " + zipFileName);
            try {
                FileInputStream fin = new FileInputStream(zipFileName);
                int size;
                byte[] buffer = new byte[BUFFER_SIZE];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((size = fin.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, size);
                }
                byte[] data = baos.toByteArray();
                fin.close();
                baos.close();
                return data;
            } catch (Exception ex) {
                Logger.i(TAG, "read zipRecorder file error");
            }
        }
        return null;
    }

    public static boolean zipFile(String baseDirName, String fileName,
                                  String targerFileName) throws IOException {
        if (baseDirName == null || "".equals(baseDirName)) {
            return false;
        }
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return false;
        }

        String baseDirPath = baseDir.getAbsolutePath();
        File targerFile = new File(targerFileName);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                targerFile));
        File file = new File(baseDir, fileName);

        boolean zipResult = false;
        if (file.isFile()) {
            zipResult = fileToZip(baseDirPath, file, out);
        } else {
            zipResult = dirToZip(baseDirPath, file, out);
        }
        out.close();
        return zipResult;
    }

    public static boolean unZipFile(String fileName, String unZipDir)
            throws Exception {
        File f = new File(unZipDir);

        if (!f.exists()) {
            f.mkdirs();
        }

        BufferedInputStream is = null;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile(fileName);
        Enumeration<?> enumeration = zipfile.entries();
        byte data[] = new byte[BUFFER_SIZE];
        Logger.i(TAG, "unZipDir: " + unZipDir);

        while (enumeration.hasMoreElements()) {
            entry = (ZipEntry) enumeration.nextElement();

            if (entry.isDirectory()) {
                File f1 = new File(unZipDir + "/" + entry.getName());
                Logger.i(TAG, "entry.isDirectory XXX " + f1.getPath());
                if (!f1.exists()) {
                    f1.mkdirs();
                }
            } else {
                is = new BufferedInputStream(zipfile.getInputStream(entry));
                int count;
                String name = unZipDir + "/" + entry.getName();
                RandomAccessFile m_randFile = null;
                File file = new File(name);
                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();
                m_randFile = new RandomAccessFile(file, "rw");
                int begin = 0;

                while ((count = is.read(data, 0, BUFFER_SIZE)) != -1) {
                    try {
                        m_randFile.seek(begin);
                    } catch (Exception ex) {
                        Logger.e(TAG, "exception, ex: " + ex.toString());
                    }

                    m_randFile.write(data, 0, count);
                    begin = begin + count;
                }

                file.delete();
                m_randFile.close();
                is.close();
            }
        }

        return true;
    }

    private static boolean fileToZip(String baseDirPath, File file,
                                     ZipOutputStream out) throws IOException {
        FileInputStream in = null;
        ZipEntry entry = null;

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes_read;
        try {
            in = new FileInputStream(file);
            entry = new ZipEntry(getEntryName(baseDirPath, file));
            out.putNextEntry(entry);

            while ((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }
            out.closeEntry();
            in.close();
        } catch (IOException e) {
            Logger.e(TAG, "Exception, ex: " + e.toString());
            return false;
        } finally {
            if (out != null) {
                out.closeEntry();
            }

            if (in != null) {
                in.close();
            }
        }
        return true;
    }

    private static boolean dirToZip(String baseDirPath, File dir,
                                    ZipOutputStream out) throws IOException {
        if (!dir.isDirectory()) {
            return false;
        }

        File[] files = dir.listFiles();
        if (files.length == 0) {
            ZipEntry entry = new ZipEntry(getEntryName(baseDirPath, dir));

            try {
                out.putNextEntry(entry);
                out.closeEntry();
            } catch (IOException e) {
                Logger.e(TAG, "Exception, ex: " + e.toString());
            }
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileToZip(baseDirPath, files[i], out);
            } else {
                dirToZip(baseDirPath, files[i], out);
            }
        }
        return true;
    }

    private static String getEntryName(String baseDirPath, File file) {
        if (!baseDirPath.endsWith(File.separator)) {
            baseDirPath = baseDirPath + File.separator;
        }

        String filePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            filePath = filePath + "/";
        }

        int index = filePath.indexOf(baseDirPath);
        return filePath.substring(index + baseDirPath.length());
    }

    /**
     * 修改文件读写权限
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            int state = proc.waitFor();
            if (state == 0) {
                Logger.i(TAG, "change file pemission success");
            } else {
                Logger.e(TAG, "change file pemission failure");
            }
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * 查看目录下文件是否存在
     *
     * @return true 文件存在，或字段有空值；false 文件不存在
     */
    public static boolean isExist(String dirPath, String fileName) {
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
            return true;
        }

        File dir = new File(dirPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return false;
            } else {
                for (File file : files) {
                    Logger.e("bill", fileName + "," + file.getName());
                    if (fileName.equals(file.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 改名，若目标文件存在，则删除临时文件
     *
     * @param from
     * @param to
     */
    public static void rename(String from, String to) {
        File f = new File(to);
        if (f.exists()) {
            new File(from).delete();
        } else {
            new File(from).renameTo(f);
        }
    }

    /**
     * 列出目录下的所有文件
     */
    public static File[] listFiles(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return null;
        }

        File dir = new File(dirPath);
        if (dir.exists()) {
            return dir.listFiles();
        }
        return null;
    }

    /**
     * 获取可以使用的缓存目录
     *
     * @param context
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取程序外部的缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 计算文件夹大小，返回单位Byte
     */
    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return file.length();
            }
        } else {
            Logger.d(TAG, file.getAbsolutePath() + ", 文件或者文件夹不存在，请检查路径是否正确！");
            return 0;
        }
    }

    public static void close(Closeable x) {
        if (x != null) {
            try {
                x.close();
            } catch (Exception e) {
                // skip
            }
        }
    }
}