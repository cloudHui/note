# Web 启动读取配置流程

```mermaid
%%{init: {"themeVariables": {"fontSize": "10px"}}}%%
flowchart TD
    A[Web 容器启动] --> B[读取 web.xml]
    B --> C[WebStartupListener.contextInitialized]
    C --> D[读取 WEB-INF/conf/env.properties]
    D --> E[创建 ServerConfig]
    E --> F[设置系统属性 env.config.path]
    F --> G[ContextLoaderListener 启动 Root Spring 容器]

    G --> H[读取全局 contextConfigLocation]
    H --> I[加载 application.xml]
    I --> J[创建 propertyConfigurer]
    J --> K[读取 env.properties]
    J --> L[读取 env.config.path/config.properties]
    K --> M[合并 Properties]
    L --> M

    M --> N[解析 XML 中的占位符]
    N --> O["例如 ${redis.host}、${jdbc.url.passport}"]
    O --> P[创建业务 Bean]
    P --> Q[bean.xml]
    P --> R[db.xml]
    P --> S[net.xml]
    P --> T[action_c2s.xml]

    G --> U[DispatcherServlet 启动]
    U --> V[读取自己的 contextConfigLocation]
    V --> W[加载 servlet.xml]
    W --> X[创建 Spring MVC Bean]
```

## 配置关系

```text
web.xml
├── 全局 contextConfigLocation
│   └── application.xml
│       ├── propertyConfigurer
│       ├── bean.xml
│       ├── db.xml
│       ├── net.xml
│       └── action_c2s.xml
│
└── DispatcherServlet 的 contextConfigLocation
    └── servlet.xml
```

## 关键代码位置

```text
web.xml：loginsrv/WebContent/WEB-INF/web.xml
启动初始化：loginsrv/src/com/gow/loginserver/web/WebStartupListener.java
Spring 入口：loginsrv/src/application.xml
配置处理器：common/src/com/gow/common/util/CustomizedPropertyConfigurer.java
配置使用：loginsrv/src/bean.xml、db.xml、net.xml
```

## 一句话总结

Web 容器先读取环境配置并确定配置目录，再由 Spring 的 propertyConfigurer 读取 properties 文件，把配置值替换到各个 XML Bean 配置中。
