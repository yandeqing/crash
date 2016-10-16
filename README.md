# Crash
When your app crash log will be sent to your email via this tool

Gradle intergration

## Use
* use Gradle dependency (recommended)
```groovy
   compile 'com.ydq:crash:1.0.4'
```

Or Maven
```xml
<dependency>
  <groupId>com.ydq</groupId>
  <artifactId>crash</artifactId>
  <version>1.0.4</version>
  <type>pom</type>
</dependency>
```

```text
two sdk:

1.CrashCatchSDK
    example:
    in Application onCreate add:
        ArrayList<String> receivers = new ArrayList<>();
        receivers.add("546218945@qq.com");
        CrashCatchSDK.init(this, "1292234542@qq.com", "RPLfexaFoGXBaxiyZK9kCw==", receivers);
2.EmailerSDK
        List<String> receivers = new ArrayList<>();
        receivers.add("546218945@qq.com");
        EmailerSDK.setReceivers(receivers);
        EmailerSDK.setAccoutPwd("1292234542@qq.com", "RPLfexaFoGXBaxiyZK9kCw==");
        EmailerSDK.sendTextByEmail(MainActivity.this, "this is a test email ");
```
   

if you have problems ,contact 546218945@qq.com
