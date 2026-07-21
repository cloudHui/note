---
title: LoginSrv 启动流程
service: 登陆服（login）
---

# LoginSrv 启动流程

## 1. Spring 配置结构

### `web.xml`

```text
全局 contextConfigLocation
└── application.xml
    ├── propertyConfigurer
    ├── bean.xml
    ├── db.xml
    ├── net.xml
    └── action_c2s.xml

DispatcherServlet 的 contextConfigLocation
└── servlet.xml
```

## 2. 配置加载过程

1. Web 容器启动，读取 `web.xml`。
2. `WebStartupListener.contextInitialized` 读取 `/WEB-INF/conf/env.properties`。
3. 创建 `ServerConfig`，并设置系统属性 `env.config.path`。
4. `ContextLoaderListener` 启动 Root Spring 容器。
5. Root 容器读取 `contextConfigLocation`，加载 `application.xml`。
6. 创建 `propertyConfigurer`，根据 `locations` 读取并合并：
   - `/WEB-INF/conf/env.properties`
   - `${env.config.path}/config.properties`
7. Spring 替换 XML 中的 `${xxx}` 占位符。
8. 创建 `application.xml` 引入的业务 Bean。
9. `DispatcherServlet` 启动，读取自己的 `contextConfigLocation`，加载 `servlet.xml`。
10. 创建 Spring MVC 相关 Bean。

## 3. 启动流程图

```mermaid
%%{init: {"themeVariables": {"fontSize": "10px"}}}%%
flowchart TD
    webContainer[Web 容器启动] --> readWebXml[读取 web.xml]
    readWebXml --> initializeListener[WebStartupListener.contextInitialized]
    initializeListener --> readEnv[读取 WEB-INF/conf/env.properties]
    readEnv --> createServerConfig[创建 ServerConfig]
    createServerConfig --> setConfigPath[设置系统属性<br/>env.config.path]
    setConfigPath --> startRootContext[ContextLoaderListener 启动 Root Spring 容器]

    startRootContext --> readRootConfig[读取 contextConfigLocation]
    readRootConfig --> loadApplication[加载 application.xml]
    loadApplication --> createConfigurer[创建 propertyConfigurer]
    createConfigurer --> loadDefaultProperties[读取 env.properties]
    createConfigurer --> loadExternalProperties[读取 env.config.path/config.properties]
    loadDefaultProperties --> mergeProperties[合并 Properties]
    loadExternalProperties --> mergeProperties
    mergeProperties --> resolvePlaceholders[解析 XML 中的占位符]
    resolvePlaceholders --> placeholderExamples["例如 ${redis.host}<br/>${jdbc.url.passport}"]
    placeholderExamples --> createBusinessBeans[创建 application.xml 引入的 Bean]
    createBusinessBeans --> beanConfig[bean.xml]
    createBusinessBeans --> databaseConfig[db.xml]
    createBusinessBeans --> networkConfig[net.xml]
    createBusinessBeans --> actionConfig[action_c2s.xml]

    startRootContext --> startDispatcher[DispatcherServlet 启动]
    startDispatcher --> readServletConfig[读取自己的 contextConfigLocation]
    readServletConfig --> loadServlet[加载 servlet.xml]
    loadServlet --> createMvcBeans[创建 Spring MVC 相关 Bean]
```

## 4. 相关文件

| 用途 | 文件 |
| --- | --- |
| Web 配置入口 | `loginsrv/WebContent/WEB-INF/web.xml` |
| Web 启动初始化 | `loginsrv/src/com/gow/loginserver/web/WebStartupListener.java` |
| Spring 配置入口 | `loginsrv/src/application.xml` |
| 配置占位符处理器 | `common/src/com/gow/common/util/CustomizedPropertyConfigurer.java` |
| 业务 Bean 配置 | `loginsrv/src/bean.xml` |
| 数据库配置 | `loginsrv/src/db.xml` |
| 网络配置 | `loginsrv/src/net.xml` |





