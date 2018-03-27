X-Pack Support for Fess
=========

## Overview

X-Pack support for Fess is an extension library to work with Elasticsearch and X-Pack environment.

## Setup

### Install Elasticsearch with X-Pack

See [Set up Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html) and [Installing X-Pack](https://www.elastic.co/guide/en/x-pack/current/installing-xpack.html).

For example, you can create users for X-Pack by executing `setup-passwords` command:

```
# /usr/share/elasticsearch/bin/x-pack/setup-passwords auto
Initiating the setup of passwords for reserved users elastic,kibana,logstash_system.
The passwords will be randomly generated and printed to the console.
Please confirm that you would like to continue [y/N]y


Changed password for user kibana
PASSWORD kibana = ...password...

Changed password for user logstash_system
PASSWORD logstash_system = ...password...

Changed password for user elastic
PASSWORD elastic = ...password...
```

### Create User and Role for Fess

To access Elasticsearch with X-Pack from Fess, create `fess` role:

```
curl -XPOST -u elastic 'localhost:9200/_xpack/security/role/fess' -H "Content-Type: application/json" -d '{
  "cluster": ["all"],
  "indices" : [
    {
      "names" : [ "fess*", ".fess*", ".configsync*", ".crawler*", ".suggest*" ],
      "privileges" : [ "all" ]
    }
  ]
}'
```
and then create `fess` user:
```
curl -XPOST -u elastic 'localhost:9200/_xpack/security/user/fess?pretty' -H 'Content-Type: application/json' -d'
{
  "password" : "changeme",
  "roles" : [ "fess" ],
  "full_name" : "Fess"
}'
```

### Stop Elasticsearch

Stop Elasticsearch process.

```
# systemctl stop elasticsearch.service
```

### Edit elasticsearch.yml

Insert the following setting into /etc/elasticsearch/elasticsearch.yml.

```
configsync.xpack.security.user: "fess:changeme"
```

### Install Fess

See [Installation Guide](https://fess.codelibs.org/12.1/install/index.html).
X-Pack support for Fess is avaiable on Fess 12.1.1 or above.

### Install X-Pack Support

Copy fess-xpack-12.1.X.X.jar to `lib` directory(ex. /usr/share/fess/app/WEB-INF/lib) and run jar file to download dependencies as below:

```
# cd /usr/share/fess/app/WEB-INF/lib
# java -Delasticsearch.version=6.2.2 -jar fess-xpack-12.1.X.X.jar
Downloading from http://central.maven.org/maven2/com/vividsolutions/jts/1.13/jts-1.13.jar
Saved /Users/shinsuke/workspace/fess-xpack/jts-1.13.jar
Downloading from http://central.maven.org/maven2/com/unboundid/unboundid-ldapsdk/3.2.0/unboundid-ldapsdk-3.2.0.jar
Saved /Users/shinsuke/workspace/fess-xpack/unboundid-ldapsdk-3.2.0.jar
Downloading from https://artifacts.elastic.co/maven/org/elasticsearch/plugin/x-pack-api/6.2.2/x-pack-api-6.2.2.jar
Saved /Users/shinsuke/workspace/fess-xpack/x-pack-api-6.2.2.jar
Downloading from https://artifacts.elastic.co/maven/org/elasticsearch/client/x-pack-transport/6.2.2/x-pack-transport-6.2.2.jar
Saved /Users/shinsuke/workspace/fess-xpack/x-pack-transport-6.2.2.jar
Successful!
```

### Create xpack.properties

Create xpack.properties in `conf` directory(ex. /etc/fess) and put X-Pack settings to the properties file.

```
xpack.security.user=fess:changeme
```

For other available settings, see [Java Client and Security](https://www.elastic.co/guide/en/x-pack/current/java-clients.html).

### Start Fess and Elasticsearch

```
# systemctl start elasticsearch.service
# systemctl start fess.service
```

## Support

If you need any technical help, contact [N2SM, Inc.](https://www.n2sm.net/en/support/fess_support.html)

