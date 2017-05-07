### FileUploader

基于YMP框架实现的文件上传及资源访问服务模块；

#### Maven包依赖

    <dependency>
        <groupId>net.ymate.module</groupId>
        <artifactId>ymate-module-fileuploader</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

### 模块配置参数说明

    #-------------------------------------
    # module.fileuploader 模块初始化参数
    #-------------------------------------
    
    # 节点标识符, 默认值: unknown
    ymp.configs.module.fileuploader.node_id=
    
    # 缓存名称前缀, 默认值: ""
    ymp.configs.module.fileuploader.cache_name_prefix=
    
    # 缓存数据超时时间, 可选参数, 数值必须大于等于0, 否则将采用默认
    ymp.configs.module.fileuploader.cache_timeout=
    
    # 是否开启代理模式, 默认值: false
    ymp.configs.module.fileuploader.proxy_mode=
    
    # 代理服务基准URL路径(若开启代理模式则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/fileupload/, 默认值: 空
    ymp.configs.module.fileuploader.proxy_service_base_url=
    
    # 上传文件存储根路径, 默认值: ${root}/upload_files
    ymp.configs.module.fileuploader.file_storage_path=
    
    # 静态资源引用基准URL路径, 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/static/resources/, 默认值: 空(即不使用静态资源引用路径)
    ymp.configs.module.fileuploader.resources_base_url=
    
    # 是否允许自定义缩略图尺寸, 默认值: false
    ymp.configs.module.fileuploader.allow_custom_thumb_size=
    
    # 缩略图尺寸列表, 该尺寸列表在允许自定义缩略图尺寸时生效, 若列表不为空则自定义尺寸不能超过此范围, 如: 600_480、1024_0 (0表示等比缩放, 不支持0*0), 默认值: 空
    ymp.configs.module.fileuploader.thumb_size_list=
    
    # 缩略图清晰度, 如: 0.70f, 默认值: 0f
    ymp.configs.module.fileuploader.thumb_quality=
    
    # 允许上传的文件ContentType列表, 如: image/png|image/jpeg, 默认值: 空, 表示不限制
    ymp.configs.module.fileuploader.allow_content_types=

#### One More Thing

YMP不仅提供便捷的Web及其它Java项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入 官方QQ群480374360，一起交流学习，帮助YMP成长！

了解更多有关YMP框架的内容，请访问官网：http://www.ymate.net/