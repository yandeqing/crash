/*
 *
 * @author yandeqing
 * @created 2016.6.3
 * @email 18612205027@163.com
 * @version $version
 *
 */

package com.ydq.crash;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    static byte[] lock;

    static {
        lock = new byte[0];
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            return fileName.substring(pos, fileName.length()).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { // 文件存在时
            InputStream inStream = null;
            FileOutputStream fs = null;
            // 读入原文件
            try {
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                byte[] buffer = new byte[1444];
                int bytesum = 0;
                int byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param content
     * @return
     * @throws IOException
     */
    public static void write(String absolutePath, String content)
            throws IOException {
        File file = new File(absolutePath);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        }
    }

    /**
     * @param content
     * @return
     * @throws IOException
     */
    public static void write(String absolutePath, String content, boolean override)
            throws IOException {
        File file = new File(absolutePath);
        if (file.exists()) {
            FileOutputStream fos;
            if (override) {
                fos = new FileOutputStream(file);
            } else {
                fos = new FileOutputStream(file, true);
            }
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        }
    }

    public static String write(Context context, String filename, String content)
            throws IOException {
        if (!filename.endsWith(".txt")) {
            filename = filename + ".txt";
        }
        FileOutputStream fos = context.openFileOutput(filename,
                Context.MODE_PRIVATE + Context.MODE_APPEND
                        + Context.MODE_WORLD_READABLE
                        + Context.MODE_WORLD_WRITEABLE);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        return context.getFilesDir().getPath() + "/" + filename;
    }

    /**
     * @param context
     * @return
     */
    public static String getSdcardLogAbsolutePath(Context context) {
        String error_report_dir = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + context.getString(R.string.app_name)
                + File.separator;
        return error_report_dir + context.getString(R.string.app_name) + ".txt";
    }

    /**
     * 生成错误报告文件，包含手机信息，系统版本，错误详情等内容
     *
     * @param throwable 存放目录
     * @return
     */
    public static String writeSdcard(Context context, Throwable throwable)throws IOException {
        String error_report_dir = "";
        String err_file_name = "";
        error_report_dir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + context.getString(R.string.app_name);
        err_file_name = context.getString(R.string.app_name) + ".txt";
        final File fileDir = new File(error_report_dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        // 记录当前时间,文件名即是
        boolean isRecordDevInfo = false;
        File errFile = new File(fileDir + File.separator + err_file_name);
        boolean b = !errFile.exists();
        if (b) {
            isRecordDevInfo = true;
        }
        final FileOutputStream output = new FileOutputStream(errFile, true);
        // 创建文件时，记录 IMEI和设备信息，其余情况，追加错误和时间即可
        // 记录imei号
        if (isRecordDevInfo) {
            try {
                final TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                final String imei = "IMEI=" + tm.getDeviceId();
                output.write(imei.getBytes());
                output.write("\n".getBytes());
            } catch (Exception e) {
                output.write("IMEI=0\n".getBytes());
            }
            // 设备详情
            writeBuildDetails(output);
            // 软件版本
            String verInfo = "AppVerName="
                    + SysInfoUtil.getVersionName(context) + "\n";
            output.write(verInfo.getBytes());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String format2 = format.format(date);
        // 记录时间
        final String timeInfo = "\n" + format2 + "\n";
        output.write(timeInfo.getBytes());
        // 错误详情
        final PrintStream printStream = new PrintStream(output);
        writeStackTrace(throwable, printStream);
        // 控制台仍打印log 供调试
        Log.e(context.getString(R.string.app_name), throwable.toString());
        throwable.printStackTrace();
        printStream.close();
        output.close();
        return error_report_dir + "/" + err_file_name;
    }

    /**
     * 写入 错误信息
     *
     * @param throwable
     * @param printStream
     */
    private static void writeStackTrace(Throwable throwable,
                                        PrintStream printStream) {
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = throwable;
        while (cause != null) {
            cause.printStackTrace(printStream);
            cause = cause.getCause();
        }
    }

    /**
     * 写入设备和系统详情
     *
     * @param os
     * @throws IOException
     */
    private static void writeBuildDetails(OutputStream os) throws IOException {
        final StringBuilder result = new StringBuilder();
        final Field[] fields_build = Build.class.getFields();
        // 设备信息
        for (final Field field : fields_build) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        // 版本信息
        final Field[] fields_version = Build.VERSION.class.getFields();
        for (final Field field : fields_version) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        os.write(result.toString().getBytes());
    }

    public static String read(String fileName) {
        String content = "";
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buf[] = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            fis.close();
            content = bos.toString();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String read(Context context, String fileName) {
        String content = "";
        if (!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buf[] = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            fis.close();
            content = bos.toString();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static boolean deleteFile(final String path) {
        boolean delete = false;
        File dir = new File(path);
        if (dir.isFile()) {
            delete = dir.delete();
        } else {
            File[] fs = dir.listFiles();
            if (fs != null) {
                final int size = fs.length;
                for (int i = 0; i < size; i++) {
                    delete = fs[i].delete();
                }
            }
            delete = dir.delete();
        }
        return delete;
    }


    public static String getFileSize(File f) {
        long size = 0;
        if (f.exists() && f.isFile()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileChannel fc = fis.getChannel();
            try {
                size = fc.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getReadableFileSize(size);

    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getStrFromFile(final String localUrl) {
        String str = "日志文件路径" + localUrl;
        File myFile = new File(localUrl);
        if (!myFile.exists()) {
            return null;
        }
        BufferedReader in = null;
        StringBuffer buffer = null;
        try {
            in = new BufferedReader(new FileReader(myFile));
            buffer = new StringBuffer("<br/>日志sdcard路径" + localUrl + "<br/>");
            while ((str = in.readLine()) != null) {
                if (str != null && str.contains("Exception")) {
                    buffer.append("<font color=red size=5 face=宋体><strong>"
                            + str + "</strong></font><br/>");
                } else {
                    buffer.append(str + "<br/>");
                }
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.getStackTrace();
            buffer.append("<font color=red size=5 face=宋体><strong>日志文件太长无法发送 OutOfMemoryError </strong></font><br/>\"");
        } catch (OutOfMemoryError e) {
            e.getStackTrace();
            buffer.append("<font color=red size=5 face=宋体><strong>日志文件太长无法发送 OutOfMemoryError </strong></font><br/>\"");
        }
        String string = buffer.toString();
        return string;
    }
}