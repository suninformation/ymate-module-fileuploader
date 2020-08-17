/*
 * Copyright 2007-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.fileuploader;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/3/27 06:04
 * @since 1.0
 */
public enum ResourceType {

    /**
     * 图片
     */
    IMAGE(1),

    /**
     * 视频
     */
    VIDEO(2),

    /**
     * 音频
     */
    AUDIO(3),

    /**
     * 文本
     */
    TEXT(4),

    /**
     * 应用程序
     */
    APPLICATION(5),

    /**
     * 缩略图
     */
    THUMB(6);

    private final int type;

    ResourceType(int type) {
        this.type = type;
    }

    public static ResourceType valueOf(Integer type) {
        if (type != null) {
            switch (type) {
                case 1:
                    return ResourceType.IMAGE;
                case 2:
                    return ResourceType.VIDEO;
                case 3:
                    return ResourceType.AUDIO;
                case 4:
                    return ResourceType.TEXT;
                case 6:
                    return ResourceType.THUMB;
                default:
                    return ResourceType.APPLICATION;
            }
        }
        return null;
    }

    public int type() {
        return type;
    }

    public boolean isImage() {
        return type == 1 || type == 6;
    }
}
