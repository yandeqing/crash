# Crash
****程序崩溃的瞬间将崩溃日志第一时间发送到自己的邮箱的工具****

Gradle一键集成

## 如何引用
* 使用Gradle远程依赖，推荐
```groovy
compile 'com.ydq:crash:1.0'
```

Or Maven
```xml
<dependency>
  <groupId>com.ydq</groupId>
  <artifactId>crash</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

```text
two sdk:

1.CrashCatchSDK
example:
    Application onCreate:
        ArrayList<String> receivers = new ArrayList<>();
        receivers.add("546218945@qq.com");
        CrashCatchSDK.init(this, receivers);
2.EmailerSDK
    example:
        List<String> receivers = new ArrayList<>();
        receivers.add("546218945@qq.com");
        EmailerSDK.setReceivers(receivers);
        EmailerSDK.sendTextByEmail(MainActivity.this, "this is a test email ");
```
   

if you have problems ,contact 546218945@qq.com
