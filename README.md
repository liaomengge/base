Base Framework
======================================

[![github stars](https://img.shields.io/github/stars/liaomengge/base.svg)](https://github.com/liaomengge/base/stargazers)
[![github forks](https://img.shields.io/github/forks/liaomengge/base.svg)](https://github.com/liaomengge/base/network)
[![jdk version](https://img.shields.io/badge/jdk-1.8+-blue.svg)](https://docs.oracle.com/javase/8/docs/api/)
[![maven center](https://img.shields.io/maven-central/v/com.github.liaomengge/base-framework-bom.svg?color=blue)](https://search.maven.org/search?q=g:com.github.liaomengge%20AND%20a:base-framework-bom)
[![sonatype nexus snapshots](https://img.shields.io/nexus/s/com.github.liaomengge/base-framework-bom?label=sonatype-nexus-snapshots&server=https%3A%2F%2Foss.sonatype.org%2F)](https://oss.sonatype.org/content/repositories/snapshots/com/github/liaomengge/base-framework-bom/)
[![github license](https://img.shields.io/github/license/liaomengge/base.svg)](https://github.com/liaomengge/base/blob/master/LICENSE)

[![spring boot version](https://img.shields.io/badge/spring--boot-2.3.2.RELEASE-blue.svg)](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/html/)
[![spring cloud version](https://img.shields.io/badge/spring--cloud-Hoxton.SR8-blue.svg)](https://docs.spring.io/spring-cloud/docs/Hoxton.SR8/reference/html/)
[![spring cloud alibaba version](https://img.shields.io/badge/spring--cloud--alibaba-2.2.3.RELEASE-blue.svg)](https://github.com/alibaba/spring-cloud-alibaba/blob/master/README.md)


### 使用说明

#### Maven Snapshot

```xml
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
</repositories>

```

```xml
<dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.github.liaomengge</groupId>
                <artifactId>base-framework-bom</artifactId>
                <version>0.0.7-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

#### Gradle Snapshot

```groovy
repositories {
        mavenLocal()
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/spring' }
        maven { url 'https://maven.aliyun.com/repository/spring-plugin' }
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
        mavenCentral()
    }
```

```groovy
dependencyManagement {
        imports {
            mavenBom "com.github.liaomengge:base-framework-bom:0.0.7-SNAPSHOT"
        }
}
```

