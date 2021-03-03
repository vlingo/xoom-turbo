# vlingo-xoom

[![Javadocs](http://javadoc.io/badge/io.vlingo/vlingo-xoom.svg?color=brightgreen)](http://javadoc.io/doc/io.vlingo/vlingo-xoom) [![Build](https://github.com/vlingo/vlingo-xoom/workflows/Build/badge.svg)](https://github.com/vlingo/vlingo-xoom/actions?query=workflow%3ABuild) [ ![Download](https://api.bintray.com/packages/vlingo/vlingo-platform-java/vlingo-xoom/images/download.svg) ](https://bintray.com/vlingo/vlingo-platform-java/vlingo-xoom/_latestVersion) [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/xoom) [![Join the chat at https://gitter.im/vlingo-platform-java/xoom](https://badges.gitter.im/vlingo-platform-java/xoom.svg)](https://gitter.im/vlingo-platform-java/xoom?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The VLINGO/XOOM provides a JVM microframework for the VLINGO/PLATFORM that accelerates building high-performance reactive microservices.

Docs: https://docs.vlingo.io/vlingo-xoom

### Bintray

```xml
  <repositories>
    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
  </repositories>
  <dependencies>
    <dependency>
      <groupId>io.vlingo</groupId>
      <artifactId>vlingo-xoom</artifactId>
      <version>1.3.0</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
```

```gradle
dependencies {
    compile 'io.vlingo:vlingo-xoom:1.3.0'
}

repositories {
    jcenter()
}
```

## Building snapshots

To build examples from this repository you'll need access to VLINGO snapshot builds on
[GitHub Packages](https://github.com/vlingo/vlingo-platform/packages).

GitHub [requires authentication with a Personal Access Token](https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-with-a-personal-access-token)
to use their Maven repository.
In order to build VLINGO examples locally, you will need to configure the following in your `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>GITHUB-USERNAME</username>
      <password>GITHUB-PERSONAL-ACCESS-TOKEN</password>
    </server>
  </servers>
</settings>
```

Replace `GITHUB-USERNAME` with your GitHub username, and `GITHUB-PERSONAL-ACCESS-TOKEN` with your Personal Access Token.
Personal Access Tokens can be created in Settings > Developer Settings > [Personal Access Tokens](https://github.com/settings/tokens) on GitHub.
Remember to create the token with `read:packages` scope.

## Development

:warning: On maven test, Windows will sometimes open a firewall dialog, you should just close it (will reappear due to mysqld-instance getting copied each time).

License (See LICENSE file for full license)
-------------------------------------------
Copyright © 2012-2020 VLINGO LABS. All rights reserved.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.
