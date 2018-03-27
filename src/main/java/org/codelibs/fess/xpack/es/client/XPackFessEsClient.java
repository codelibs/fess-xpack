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
package org.codelibs.fess.xpack.es.client;

import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.xpack.util.XPackUtil;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPackFessEsClient extends FessEsClient {

    private static final Logger logger = LoggerFactory.getLogger(XPackFessEsClient.class);

    @Override
    protected Client createTransportClient(final FessConfig fessConfig) {
        final Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("cluster.name", fessConfig.getElasticsearchClusterName());
        settingsBuilder.put("client.transport.sniff", fessConfig.isElasticsearchTransportSniff());
        settingsBuilder.put("client.transport.ping_timeout", fessConfig.getElasticsearchTransportPingTimeout());
        settingsBuilder.put("client.transport.nodes_sampler_interval", fessConfig.getElasticsearchTransportNodesSamplerInterval());
        XPackUtil.getProperties().entrySet().stream().filter(e -> e.getKey().toString().startsWith("xpack."))
                .forEach(e -> settingsBuilder.put(e.getKey().toString(), e.getValue().toString()));
        final Settings settings = settingsBuilder.build();
        if (logger.isDebugEnabled()) {
            logger.debug("Transport settings: " + settings);
        }
        final TransportClient transportClient = new PreBuiltXPackTransportClient(settings);
        for (final TransportAddress address : transportAddressList) {
            transportClient.addTransportAddress(address);
        }
        return transportClient;
    }
}
