# increase-androidmanifest-version

一个简单的clojure小程序, 替换原先的shell脚本。功能是将同目录下的
AndroidManifest.xml文件中的版本号(versionCode versionName)自增一位，且
不影响其它行的内容。并打印修改后的版本str

## Installation

    $ lein uberjar

## Usage

    $ java -jar increase-androidmanifest-version-0.1.0-standalone.jar


## Examples

before:
    android:versionCode="157"
    android:versionName="1.0.3.11">
after:
    android:versionCode="158"
    android:versionName="1.0.3.12">


