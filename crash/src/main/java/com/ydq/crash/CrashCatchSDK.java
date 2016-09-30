package com.ydq.crash;

import android.content.Context;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CrashCatchSDK {


    protected static final String SAVE_EXCEPTION_FILE_PARENT_PATH = "";
    protected static final String SAVE_EXCEPTION_FILE_NAME = "";
    private static Context mContext;
    private static List<String> mReceivers;

    private static CrashCatchSDK crashCatchSDK;
    private static UncaughtExceptionHandler defaultExceptionHandler;

    private CrashCatchSDK() {
    }


    public static CrashCatchSDK init(Context context) {
        mContext = context;
        if (crashCatchSDK == null) {
            crashCatchSDK = new CrashCatchSDK();
            mContext = context;
            defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        }
        setUnCatchableAcceptListioner();
        sendErrorLogFromSdcard();//初始化的时候发送一次
        return crashCatchSDK;
    }

    /**
     * @param context
     * @param receivers
     * @return
     */
    public static CrashCatchSDK init(Context context, List<String> receivers) {
        mContext = context;
        mReceivers = receivers;
        if (crashCatchSDK == null) {
            crashCatchSDK = new CrashCatchSDK();
            mContext = context;
            defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        }
        setUnCatchableAcceptListioner();
        sendErrorLogFromSdcard();//初始化的时候发送一次
        return crashCatchSDK;
    }

    /**
     * 初始化方法
     * <p/>
     * 上下文对象
     */
    private static void setUnCatchableAcceptListioner() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (!handleException(thread, ex) && defaultExceptionHandler != null) {
                    //崩溃退出登录信息
                    defaultExceptionHandler.uncaughtException(thread, ex);
                }
            }
        });
    }


    // 程序异常处理方法
    private static boolean handleException(final Thread thread, final Throwable ex) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                StringBuilder sb = new StringBuilder();
                Format formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date firstDate = new Date(System.currentTimeMillis()); // 第一次创建文件，也就是开始日期
                String str = formatter.format(firstDate);
                sb.append("\n");
                sb.append("thread id:" + thread.getId());
                sb.append("\n");
                sb.append(str); // 把当前的日期写入到字符串中
                Writer writer = new StringWriter();
                PrintWriter pw = new PrintWriter(writer);
                ex.printStackTrace(pw);
                String errorresult = writer.toString();
                sb.append(errorresult);
                sb.append("\n");
                String path = null;
                try {
                    String filename = "errlog";
//                    path = FileUtil.writeSdcard(mContext, ex);// 写sdcard
                    path = FileUtil.write(mContext, filename, sb.toString());// 写应用文件
                    File sdcardfile = new File(path);
                    boolean exists = sdcardfile.exists();
                    uploadLog(new File(path));
//                    } else {
//                        uploadLog(sdcardfile);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (path == null) {
                }
            }
        }.start();
        return false;
    }

    public static String getErrorLogPath() {
        String path = mContext.getFilesDir() + "/errlog.txt";
        File file = new File(path);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return FileUtil.getSdcardLogAbsolutePath(mContext);
        }
    }

    /**
     * 从本地直接发送日志文件
     */
    public static void sendErrorLogFromSdcard() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String errorLogPath = getErrorLogPath();
                File file = new File(errorLogPath);
                uploadLog(file);
            }
        }.start();
    }

    public static void uploadLog(final File file) {
        if (file.exists()) {
            try {
                EmailerSDK.setReceivers(mReceivers);
                EmailerSDK.sendClientErrorLogEmail(mContext, file.getAbsolutePath());
                boolean delete = FileUtil.deleteFile(file.getAbsolutePath());// 删除文件
                boolean deleteFile = mContext
                        .deleteFile(file.getName());
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

        }
    }
}
