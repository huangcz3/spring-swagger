## 注意事项
1. *spring-boot-starter-rocketmq*  找不到    
是因为*spring-boot-starter-rocketmq*还没有在中央仓库中，需要从源码编译安装
在<https://github.com/apache/rocketmq-externals>下载源代码后进行spring-boot-starter-rocketmq的目录，执行以下命令即可：
`mvn source:jar install`  


