/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.xpack.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Install {

    private static final String ES_VERSION = "6.2.2";

    public static void main(final String[] args) {
        final File currentDir;
        if (args.length == 1) {
            currentDir = new File(args[0]);
        } else {
            currentDir = new File(System.getProperty("user.dir"));
        }

        if (!currentDir.exists()) {
            System.err.println(currentDir.getAbsolutePath() + " does not exist.");
            System.exit(1);
            return;
        }

        final String esVersion = System.getProperty("elasticsearch.version", ES_VERSION);
        try {
            download("http://central.maven.org/maven2/com/vividsolutions/jts/1.13/jts-1.13.jar", new File(currentDir, "jts-1.13.jar"));
            download("http://central.maven.org/maven2/com/unboundid/unboundid-ldapsdk/3.2.0/unboundid-ldapsdk-3.2.0.jar",
                    new File(currentDir, "unboundid-ldapsdk-3.2.0.jar"));
            download("https://artifacts.elastic.co/maven/org/elasticsearch/plugin/x-pack-api/" + esVersion + "/x-pack-api-" + esVersion
                    + ".jar", new File(currentDir, "x-pack-api-" + esVersion + ".jar"));
            download("https://artifacts.elastic.co/maven/org/elasticsearch/client/x-pack-transport/" + esVersion + "/x-pack-transport-"
                    + esVersion + ".jar", new File(currentDir, "x-pack-transport-" + esVersion + ".jar"));
        } catch (final IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
            return;
        }

        System.out.println("Successful!");
    }

    private static void download(final String url, final File file) throws IOException {
        System.out.println("Downloading from " + url);
        try (ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream()); FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        System.out.println("Saved " + file.getAbsolutePath());
    }

}
