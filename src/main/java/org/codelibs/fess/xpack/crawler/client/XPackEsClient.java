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
package org.codelibs.fess.xpack.crawler.client;

import java.net.InetAddress;
import java.util.Arrays;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.xpack.util.XPackUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPackEsClient extends EsClient {

    private static final Logger logger = LoggerFactory.getLogger(XPackEsClient.class);

    @Override
    protected TransportClient createTransportClient() {
        final Builder settingsBuilder = Settings.builder().put("cluster.name", StringUtil.isBlank(clusterName) ? "elasticsearch" : clusterName);
        XPackUtil.getProperties().entrySet().stream().filter(e -> e.getKey().toString().startsWith("xpack."))
                .forEach(e -> settingsBuilder.put(e.getKey().toString(), e.getValue().toString()));
        final Settings settings = settingsBuilder.build();
        if (logger.isDebugEnabled()) {
            logger.debug("Transport settings: " + settings);
        }
        final TransportClient transportClient = new PreBuiltXPackTransportClient(settings);
        Arrays.stream(addresses).forEach(address -> {
            final String[] values = address.split(":");
            String hostname;
            int port = 9300;
            if (values.length == 1) {
                hostname = values[0];
            } else if (values.length == 2) {
                hostname = values[0];
                port = Integer.parseInt(values[1]);
            } else {
                throw new CrawlerSystemException("Invalid address: " + address);
            }
            try {
                transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(hostname), port));
            } catch (final Exception e) {
                throw new CrawlerSystemException("Unknown host: " + address);
            }
            logger.info("Connected to " + hostname + ":" + port);
        });
        return transportClient;
    }
}
