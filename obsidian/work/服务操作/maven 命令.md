# Maven 常用命令速查

> 复制下方任意命令，在项目根目录（含 `pom.xml`）下执行。

## 基础生命周期

```bash
mvn clean
mvn compile
mvn test-compile
mvn test
mvn package
mvn install
mvn deploy
```

## 组合命令（最常用）

```bash
mvn clean compile
mvn clean test
mvn clean package
mvn clean install
mvn clean deploy
```

## 跳过测试

```bash
mvn clean package -DskipTests
mvn clean package -Dmaven.test.skip=true
```

## 指定 Profile

```bash
mvn clean package -Pprod
mvn clean install -Pdev -DskipTests
```

## 依赖分析

```bash
mvn dependency:tree
mvn dependency:list
mvn dependency:analyze
```

## 查看配置信息

```bash
mvn help:effective-pom
mvn help:effective-settings
mvn help:active-profiles
```

## 源码与文档

```bash
mvn source:jar
mvn javadoc:jar
```

## 多模块项目

```bash
mvn clean install -pl module-name
mvn clean install -am
mvn clean install -amd
mvn clean install -pl module-name -am
```

## 版本管理

```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

## 创建项目

```bash
mvn archetype:generate
```