/*
 * Copyright 2007-2021 the original author or authors.
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
package net.ymate.module.fileuploader.mongodb;

import net.ymate.module.fileuploader.AbstractResourcesProcessor;
import net.ymate.module.fileuploader.ResourceType;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.platform.commons.lang.PairObject;
import net.ymate.platform.persistence.mongodb.IMongo;
import net.ymate.platform.persistence.mongodb.MongoDB;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/11/14 8:14 下午
 * @since 2.0.0
 */
public class MongoGridResourcesProcessor extends AbstractResourcesProcessor {

    private IMongo mongo;

    private PairObject<String, String> dataSourceCfg;

    @Override
    protected void doInitialize() throws Exception {
        this.mongo = getOwner().getOwner().getModuleManager().getModule(MongoDB.class);
        this.dataSourceCfg = MongoGridFileStorageAdapter.parseMongoDataSourceCfg(getOwner(), this.mongo);
    }

    @Override
    protected UploadFileMeta doMatchHash(String hash, ResourceType resourceType) throws Exception {
        if (StringUtils.isBlank(hash)) {
            return null;
        }
        return mongo.openGridFsSession(dataSourceCfg.getKey(), dataSourceCfg.getValue(), session -> MongoGridFileStorageAdapter.buildUploadFileMeta(hash, session.match(hash)));
    }
}
