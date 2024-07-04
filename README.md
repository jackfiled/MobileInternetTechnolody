# 移动互联网技术及应用课程成果仓库

北京邮电大学2021级移动互联网技术及应用课程成果仓库。

本仓库中包含该课程中两次课程作业和最后课程大作业的作业代码和报告源代码。

- 第一次作业：时钟，可以切换不同的时区。
- 第二次作业：音乐播放器，播放在线音乐并显示一个简单的播放动画。
- 大作业：在线多媒体播放系统。

## 在线多媒体播放系统

项目名称`Chiara`。				

随着互联网技术的发展，越来越多的用户选择在自己的家庭网络中搭建网络附加存储 （NAS），作为自己家庭网络上的核心存储设备，并在 NAS 上下载和存储自己所喜爱的视频 和音频资料。同时随着移动终端技术的发展，尤其是 4G 技术和 Wi-Fi 技术为终端提供了充 足的带宽之后，在移动设备上访问并收听观看家庭 NAS 中存储的各种资料变成为了一个非 常常见的需求。 本系统便是一个为了解决家庭影音需求而设计的移动端多媒体播放系统。系统应提供两 个部分的应用程序，第一部署在 NAS 上提供影音资料识别、推流的服务端系统，第二是安 装在移动终端上的客户端程序，该软件从服务端系统中获取当前 NAS 中存储各种影音资料 的信息并展示给用户选择，提供音乐的播放和视频的观看功能。

Chiara 系统使用服务端/客户端的 C/S 架构进行开发，其中服务端软件使用 ASP.NET core 技术进行开发，客户端软件使用 Jetpack Compose 框 架，按照 MVVM 设计模式进行开发，使用 HTTP 协议同后端进行通信。

服务端源代码位于`Chiara`文件夹内，客户端位于`ChiaraAndroid`文件夹内，分别使用对应的IDE打开即可编译运行对应的程序。作业报告使用`latex`撰写，相关源代码在`AndroidReport`文件夹内，使用如下的指令进行编译即可获得`AndroidReport.pdf`文件：

```shell
cd AndroidReport
latexmk main.tex
```
