/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.manager.node;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractClusterManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClusterManager.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    private static final long RETRY_INTERVAL_MS = 5000;
    private static final Object BASE_NODE_CONTROLLER_INFO_ROUTE = "/api/v2/node/info";
    private static final int CONNECT_TIMEOUT = 1000;

    protected static boolean syncStateUpdateWithRemoteNodeController(NodeInfoDescription desc, boolean activate) {
        boolean synced = false;
        String url;
        if (activate) {
            url = generateEndpoint(desc, "/activate");
        } else {
            url = generateEndpoint(desc, "/deactivate");
        }
        LOG.info("Trying to sync state update with node controller=" + url);

        boolean connected = false;
        while (!connected) {
            connected = post(url);
            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 10000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synced = true;
        return synced;
    }

    protected static boolean syncWithRemoteNodeController(NodeInfoDescription desc) {
        boolean synced = false;
        try {
            String body = JacksonSerializer.getObjectMapper().writeValueAsString(desc);
            String url = generateEndpoint(desc);
            LOG.info("Trying to sync description updates with node controller=" + url);

            boolean connected = false;
            while (!connected) {
                connected = put(url, body);
                if (!connected) {
                    LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 10000));
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synced = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return synced;
    }

    protected static String generateEndpoint(NodeInfoDescription desc) {
        return generateEndpoint(desc, "");
    }

    protected static String generateEndpoint(NodeInfoDescription desc, String subroute) {
        return PROTOCOL + desc.getHostname() + COLON + desc.getPort() + BASE_NODE_CONTROLLER_INFO_ROUTE + subroute;
    }

    protected static boolean put(String url, String body) {
        try {
            Request.Put(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static boolean post(String url) {
        try {
            Request.Post(url)
                    .bodyString("{}", ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
