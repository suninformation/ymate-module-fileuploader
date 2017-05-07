/*
 * Copyright 2007-2017 the original author or authors.
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

import net.ymate.module.fileuploader.annotation.UploadResultProcessor;
import net.ymate.module.fileuploader.handle.UploadResultProcessorHandler;
import net.ymate.module.fileuploader.impl.DefaultModuleCfg;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/27 上午6:06
 * @version 1.0
 */
@Module
public class FileUploader implements IModule, IFileUploader {

    private static final Log _LOG = LogFactory.getLog(FileUploader.class);

    public static final Version VERSION = new Version(1, 0, 0, FileUploader.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile IFileUploader __instance;

    private YMP __owner;

    private IFileUploaderModuleCfg __moduleCfg;

    private boolean __inited;

    private Map<String, IUploadResultProcessor> __resultProcessors;

    private ICache __matchHashCache;

    public static IFileUploader get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(FileUploader.class);
                }
            }
        }
        return __instance;
    }

    public String getName() {
        return MODULE_NAME;
    }

    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-fileuploader-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            __owner.getEvents().registerEvent(FileUploadEvent.class);
            __owner.registerHandler(UploadResultProcessor.class, new UploadResultProcessorHandler(this));
            //
            __matchHashCache = Caches.get().getCacheProvider().getCache(__moduleCfg.getCacheNamePrefix().concat("match_file_hash"));
            __resultProcessors = new HashMap<String, IUploadResultProcessor>();
            //
            __moduleCfg.getFileStorageAdapter().init(this);
            //
            __inited = true;
        }
    }

    public boolean isInited() {
        return __inited;
    }

    public void registerUploadResultProcessor(String name, Class<? extends IUploadResultProcessor> targetClass) throws Exception {
        if (StringUtils.isNotBlank(name) && targetClass != null) {
            __resultProcessors.put(name, targetClass.newInstance());
        }
    }

    public IUploadResultProcessor getUploadResultProcessor(String name) {
        return __resultProcessors.get(name);
    }

    public ICache getMatchHashCache() {
        return __matchHashCache;
    }

    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    public IFileUploaderModuleCfg getModuleCfg() {
        return __moduleCfg;
    }

    public YMP getOwner() {
        return __owner;
    }
}
