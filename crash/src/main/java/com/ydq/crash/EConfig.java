/*
 *
 * @author yandeqing
 * @created 2016.6.3
 * @email 18612205027@163.com
 * @version $version
 *
 */

package com.ydq.crash;

import android.os.Build;

import java.lang.reflect.Field;

/**
 * 邮件发送工具类
 * 20160318 yandeqing 修订 修复发送日志版本号为空的情况
 *
 * @author yandeqing
 */
public class EConfig {
    public static String from;
    public static String account;
    public static String pwd;
    public static String host;
    public static String post;
    public static String path;

    static {
        //设置默认值
//        from = "1292234542@qq.com";
//        account = "1292234542@qq.com";
//        pwd = DESUtil.decryptDES("RPLfexaFoGXBaxiyZK9kCw==");
        host = "smtp.qq.com";
        post = "25";
    }


    public static String getDeviceInfo() {
        final StringBuilder result = new StringBuilder();
        final Field[] fields_build = Build.class.getFields();
        // 设备信息
        for (final Field field : fields_build) {
            String name = field.getName();
            result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
            result.append(addDesc(name));
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A 未知");
            }
            result.append("</FONT></DIV>");
        }
        // 版本信息
        final Field[] fields_version = Build.VERSION.class.getFields();
        for (final Field field : fields_version) {
            String name = field.getName();
            result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
            result.append(addDesc(name));
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A 未知");
            }
            result.append("</FONT></DIV>");
        }
        return result.toString();
    }

    private static String addDesc(String name) {
        if (name.equalsIgnoreCase("IMEI")) { // 设备串号
            return "【设备串号】IMEI：";
        } else if (name.equalsIgnoreCase("BOARD")) { // 主板
            return "【主板】BOARD：";
        } else if (name.equalsIgnoreCase("BOOTLOADER")) { // 引导
            return "【引导】BOOTLOADER：";
        } else if (name.equalsIgnoreCase("BRAND")) { // Android系统定制商
            return "【Android系统定制商】BRAND：";
        } else if (name.equalsIgnoreCase("CPU_ABI")) { // CPU指令集
            return "【CPU指令集】CPU_ABI：";
        } else if (name.equalsIgnoreCase("CPU_ABI2")) { // CPU指令集
            return "【CPU指令集】CPU_ABI2：";
        } else if (name.equalsIgnoreCase("DEVICE")) { // 设备参数
            return "【设备参数】DEVICE：";
        } else if (name.equalsIgnoreCase("DISPLAY")) { // 显示屏参数
            return "【显示屏参数】DISPLAY：";
        } else if (name.equalsIgnoreCase("FINGERPRINT")) { // 硬件名称
            return "【硬件名称】FINGERPRINT：";
        } else if (name.equalsIgnoreCase("HARDWARE")) { // 硬件
            return "【硬件】HARDWARE：";
        } else if (name.equalsIgnoreCase("HOST")) { // 主机
            return "【主机】HOST：";
        } else if (name.equalsIgnoreCase("ID")) { // 修订版本列表
            return "【修订版本列表 】ID：";
        } else if (name.equalsIgnoreCase("MANUFACTURER")) { // 硬件制造商
            return "【硬件制造商】MANUFACTURER：";
        } else if (name.equalsIgnoreCase("MODEL")) { // 机型
            return "【机型】MODEL：";
        } else if (name.equalsIgnoreCase("PRODUCT")) { // 手机制造商
            return "【手机制造商】PRODUCT：";
        } else if (name.equalsIgnoreCase("RADIO")) { // 无线电通讯
            return "【无线电通讯】RADIO：";
        } else if (name.equalsIgnoreCase("SERIAL")) { // 序列
            return "【序列】SERIAL：";
        } else if (name.equalsIgnoreCase("TAGS")) { // 描述build的标签
            return "【描述build的标签 】TAGS：";
        } else if (name.equalsIgnoreCase("TIME")) { // 时间
            return "【时间】TIME：";
        } else if (name.equalsIgnoreCase("TYPE")) { // builder类型
            return "【builder类型 】TYPE：";
        } else if (name.equalsIgnoreCase("UNKNOWN")) { // 未知
            return "【未知】UNKNOWN：";
        } else if (name.equalsIgnoreCase("USER")) { // 用户
            return "【用户】USER：";
        } else if (name.equalsIgnoreCase("BASEBAND")) { // 基带
            return "【基带】BASEBAND：";
        } else if (name.equalsIgnoreCase("CODENAME")) { // 当前开发代号
            return "【当前开发代号】CODENAME：";
        } else if (name.equalsIgnoreCase("HW_VERSION")) { // 硬件版本
            return "【硬件版本】HW_VERSION：";
        } else if (name.equalsIgnoreCase("INCREMENTAL")) { // 源码控制版本号
            return "【源码控制版本号】INCREMENTAL：";
        } else if (name.equalsIgnoreCase("RELEASE")) { // 系统版本号
            return "【系统版本号 】RELEASE：";
        } else if (name.equalsIgnoreCase("SDK")) { // SDK版本号
            return "【SDK版本号】SDK：";
        } else if (name.equalsIgnoreCase("SDK_INT")) { // SDK_INT版本号
            return "【SDK_INT版本号】SDK_INT：";
        } else if (name.equalsIgnoreCase("SW_VERSION")) { // 软件版本
            return "【软件版本】SW_VERSION：";
        } else if (name.equalsIgnoreCase("AppVerName")) { // 客户端版本
            return "【客户端版本】AppVerName：";
        } else { // 未知
            return "【未知】" + name + "：";
        }
    }
}
