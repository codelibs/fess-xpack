/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.xpack.helper;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.codelibs.curl.Curl.Method;
import org.codelibs.curl.CurlRequest;
import org.codelibs.fess.helper.CurlHelper;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.fess.xpack.util.XPackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPackCurlHelper extends CurlHelper {

    private static final Logger logger = LoggerFactory.getLogger(XPackCurlHelper.class);

    private String basicAuth = null;

    @PostConstruct
    public void init() {
        logger.info("Initializing XPackCurlHelper");
        final Object value = XPackUtil.getProperties().get("xpack.security.user");
        if (value != null) {
            basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(value.toString().getBytes(StandardCharsets.UTF_8));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("basicAuth: " + basicAuth);
        }
    }

    @Override
    public CurlRequest request(final Method method, final String path) {
        final CurlRequest curlRequest = new CurlRequest(method, ResourceUtil.getElasticsearchHttpUrl() + path);
        if (logger.isDebugEnabled()) {
            logger.debug(method + " " + ResourceUtil.getElasticsearchHttpUrl() + path + " " + basicAuth);
        }
        if (basicAuth != null) {
            curlRequest.header("Authorization", basicAuth);
        }
        return curlRequest;
    }
}
