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
