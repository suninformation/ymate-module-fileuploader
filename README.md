# YMATE-MODULE-FILEUPLOADER

[![Maven Central status](https://img.shields.io/maven-central/v/net.ymate.module/ymate-module-fileuploader.svg)](https://search.maven.org/artifact/net.ymate.module/ymate-module-fileuploader)
[![LICENSE](https://img.shields.io/github/license/suninformation/ymate-module-fileuploader.svg)](https://gitee.com/suninformation/ymate-module-fileuploader/blob/master/LICENSE)


基于 YMP 框架实现的文件上传及资源访问服务模块，特性如下：

- 支持文件指纹匹配，秒传；
- 支持图片文件多种规则等比例压缩；
- 支持视频文件截图；
- 支持上传文件 `ContentType` 白名单过滤；
- 支持主从负载模式配置；
- 支持自定义响应报文内容；
- 支持自定义扩展文件存储策略；
- 支持跨域上传文件及用户身份验证；
- 支持 MongoDB 文件存储；



## Maven包依赖

```xml
<dependency>
    <groupId>net.ymate.module</groupId>
    <artifactId>ymate-module-fileuploader</artifactId>
    <version>2.0.0</version>
</dependency>
```



## 模块配置参数说明

```properties
#-------------------------------------
# module.fileuploader 模块初始化参数
#-------------------------------------

# 节点标识符, 默认值: unknown
ymp.configs.module.fileuploader.node_id=

# 缓存名称前缀, 默认值: ""
ymp.configs.module.fileuploader.cache_name_prefix=

# 缓存数据超时时间, 可选参数, 数值必须大于等于0, 否则将采用默认
ymp.configs.module.fileuploader.cache_timeout=

# 默认控制器服务请求映射前缀(不允许'/'开始和结束), 默认值: ""
ymp.configs.module.fileuploader.service_prefix=

# 是否注册默认控制器, 默认值: true
ymp.configs.module.fileuploader.service_enabled=

# 是否开启代理模式, 默认值: false
ymp.configs.module.fileuploader.proxy_mode=

# 代理服务基准URL路径(若开启代理模式则此项必填), 必须以 http:// 或 https:// 开始并以'/'结束, 如: http://www.ymate.net/fileupload/, 默认值: 空
ymp.configs.module.fileuploader.proxy_service_base_url=

# 代理客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
ymp.configs.module.fileuploader.proxy_service_auth_key=

# 上传文件存储根路径（根据存储适配器接口实现决定其值具体含义）, 默认存储适配器取值: ${root}/upload_files
ymp.configs.module.fileuploader.file_storage_path=

# 缩略图文件存储根路径（根据存储适配器接口实现决定其值具体含义）, 默认存储适配器取值与上传文件存储根路径值相同
ymp.configs.module.fileuploader.thumb_storage_path=

# 静态资源引用基准URL路径, 必须以 http:// 或 https:// 开始并以'/'结束, 如: http://www.ymate.net/static/resources/, 默认值: 空(即不使用静态资源引用路径)
ymp.configs.module.fileuploader.resources_base_url=

# 文件存储适配器接口实现, 若未提供则使用系统默认, 此类需实现net.ymate.module.fileuploader.IFileStorageAdapter接口
ymp.configs.module.fileuploader.file_storage_adapter_class=

# 图片文件处理器接口实现, 若未提供则使用系统默认, 此类需实现net.ymate.module.fileuploader.IImageProcessor接口
ymp.configs.module.fileuploader.image_processor_class=

# 资源处理器类, 用于资源上传、匹配及验证被访问资源是否允许(非代理模式则此项必填), 此类需实现net.ymate.module.fileuploader.IResourcesProcessor接口
ymp.configs.module.fileuploader.resources_processor_class=

# 文件上传成功后是否自动执行生成图片或视频截图缩略图, 默认值: false
ymp.configs.module.fileuploader.thumb_create_on_uploaded=

# 是否允许自定义缩略图尺寸, 默认值: false
ymp.configs.module.fileuploader.allow_custom_thumb_size=

# 缩略图尺寸列表, 该尺寸列表在允许自定义缩略图尺寸时生效, 若列表不为空则自定义尺寸不能超过此范围, 如: 600_480|1024_0 (0表示等比缩放, 不支持0_0), 默认值: 空
ymp.configs.module.fileuploader.thumb_size_list=

# 缩略图清晰度, 如: 0.70f, 默认值: 0f
ymp.configs.module.fileuploader.thumb_quality=

# 允许上传的文件ContentType列表, 如: image/png|image/jpeg, 默认值: 空, 表示不限制
ymp.configs.module.fileuploader.allow_content_types=
```



## 示例代码：

**示例一：**上传文件，以 POST 方式请求 URL 地址：

```shell
http://localhost:8080/uploads/push
```

参数说明：

- file: 上传文件流数据；
- type: 指定请求结果处理器，若未提供则采用默认，可选值： `fileupload`

响应：

- 未指定 `type` 参数时：

```json
{
    "ret": 0,
    "data": {
        "createTime": 1638200758000,
        "extension": "mp4",
        "filename": "a1175d94f245b9a142955b42ac285dc2.mp4",
        "hash": "a1175d94f245b9a142955b42ac285dc2",
        "lastModifyTime": 1638200758000,
        "mimeType": "video/mp4",
        "size": 21672966,
        "sourcePath": "video/a1/17/a1175d94f245b9a142955b42ac285dc2.mp4",
        "status": 0,
        "type": "VIDEO",
        "url": "http://localhost:8080/uploads/resources/video/a1175d94f245b9a142955b42ac285dc2"
    }
}
```

- 指定 `type=fileupload` 时：

```json
{
    "files": [
        {
            "size": 21672966,
            "name": "a1175d94f245b9a142955b42ac285dc2.mp4",
            "type": "video",
            "hash": "a1175d94f245b9a142955b42ac285dc2",
            "thumbnailUrl": "http://localhost:8080/uploads/resources/video/a1175d94f245b9a142955b42ac285dc2"
        }
    ]
}
```



**示例二：**文件指纹匹配，以 POST 方式请求 URL 地址：

```shell
http://localhost:8080/uploads/match
```

参数说明：

- hash: 文件哈希值（MD5），必选参数；

响应：

若匹配成功则返回该文件的描述信息；

```json
{
    "ret": 0,
    "matched": true,
    "data": {
        "createTime": 1638200758000,
        "extension": "mp4",
        "filename": "a1175d94f245b9a142955b42ac285dc2.mp4",
        "hash": "a1175d94f245b9a142955b42ac285dc2",
        "lastModifyTime": 1638200758000,
        "mimeType": "video/mp4",
        "size": 21672966,
        "sourcePath": "video/a1/17/a1175d94f245b9a142955b42ac285dc2.mp4",
        "status": 0,
        "type": "VIDEO",
        "url": "http://localhost:8080/uploads/resources/video/a1175d94f245b9a142955b42ac285dc2"
    }
}
```



**示例三：**文件资源访问，以 GET 方式请求 URL 地址：

```shell
http://localhost:8080/uploads/resources/{type}/{hash}
```

参数说明：

- type: 文件类型，必选参数，可选值范围：`image`、 `video`、`audio`、`text`、`application`、`thumb`

- hash: 文件哈希值（MD5），必选参数；

> **注**：若需要强制浏览器下载资源，只需在请求参数中添加`?attach`即可，并支持通过?attach=<FILE_NAME>方式自定义文件名称（文件名称必须合法有效，不能包含特殊字符，否则将使用默认文件名称）。



## One More Thing

YMP 不仅提供便捷的 Web 及其它 Java 项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入官方 QQ 群：[480374360](https://qm.qq.com/cgi-bin/qm/qr?k=3KSXbRoridGeFxTVA8HZzyhwU_btZQJ2)，一起交流学习，帮助 YMP 成长！

如果喜欢 YMP，希望得到你的支持和鼓励！

![Donation Code](https://ymate.net/img/donation_code.png)

了解更多有关 YMP 框架的内容，请访问官网：[https://ymate.net](https://ymate.net)