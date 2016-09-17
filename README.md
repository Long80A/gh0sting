# gh0sting
一个基于netty的socks v5 代理工具
没有加密模块,有需要可以自己写一个handler

# 协议
```
00000000  05 02 00 01
    00000000  05 00
00000000  05 01 00 01 7d 5a 5d 14  00 50
    00000000  05 00 00 01 00 00 00 00  00 00
```

#实现

socks 初始化在本地服务完成
```
   .--------.
   | client |-----------------.
   '--------'                 v
        ^                 .------.
        |                 | init |
        |                 '------'
        |                     |
        |                     |
        |                     v
   .---------.     .---------------------.
   | success |<----|  localProxyServer   |
   '---------'     '---------------------'
```

# socks 连接请求通过本地服务到远程服务器完成
```
    .--------.
    | client |--------------------.
    '--------'                    v
         ^                   .---------.
         |                   | connect |
         |                   '---------'
         |                        |
         |                        |
         |                        v
    .---------.        .---------------------.         .---------.
    | success |<-------|  localProxyServer   |-------> | connect |
    '---------'        '---------------------'         '---------'
                                     ^                      |
                                     |                      |
                                     |                      v
                                .---------.      .--------------------.
                                | success |<-----| remoteProxyServer  |
                                '---------'      '--------------------'
```

#条款
         你他妈的想干嘛就干嘛公共许可证
              第二版，2004年12月

版权所有(C) 2016 gh0sting <gh0sting@gmail.com>

任何人都有复制与发布本协议的原始或修改过的版本的权利。
若本协议被修改，须修改协议名称。

         你他妈的想干嘛就干嘛公共许可证
             复制、发布和修改条款

 0. 你只要他妈的想干嘛就干嘛好了。


           DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                   Version 2, December 2004

Copyright (C) 2004 Sam gh0sting <gh0sting@gmail.com>

Everyone is permitted to copy and distribute verbatim or modified
copies of this license document, and changing it is allowed as long
as the name is changed.

           DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 0. You just DO WHAT THE FUCK YOU WANT TO.

 https://zh.wikipedia.org/wiki/WTFPL