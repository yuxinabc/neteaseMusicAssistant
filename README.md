# neteaseMusicAssistant
网易云音乐摇一摇换歌曲
### 原理  
利用AccessibilityService实现按钮点击，主要是要找到控件id，可以通过Appium Desktop与GenyMotion模拟器查看控件id，[详情](https://blog.csdn.net/wanglin_lin/article/details/78400874)
### 权限  
* 需要开启辅助功能权限  
* 需要将应用添加到锁屏不清理名单
* 需要管理员权限以实现即时熄屏，非必须