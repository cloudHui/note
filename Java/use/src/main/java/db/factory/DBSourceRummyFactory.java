package db.factory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DBSourceRummyFactory {
	public static final DBSourceRummyFactory INSTANCE = new DBSourceRummyFactory();
	private final Map<String, DBSourceRummyFactory.EnvNodes> envNodesMap = new HashMap<>();

	private DBSourceRummyFactory() {
	}

	public SqlSessionFactory getSqlSessionFactory() {
		String defaultName = "default";
		return this.getSqlSessionFactory(defaultName + "_druid.xml", "rummy");
	}

	public SqlSessionFactory getSqlSessionFactory(String configName, String env) {
		DBSourceRummyFactory.EnvNodes envNodes = this.envNodesMap.get(configName);
		if (null == envNodes) {
			this.envNodesMap.putIfAbsent(configName, new DBSourceRummyFactory.EnvNodes(configName));
			envNodes = this.envNodesMap.get(configName);
		}

		return envNodes.getSqlSessionFactory(env);
	}

	private SqlSessionFactory createSqlSessionFactory(String name, String env) {
		try {
			Reader reader = Resources.getResourceAsReader(name);
			return (new SqlSessionFactoryBuilder()).build(reader, env);
		} catch (Exception var4) {
			throw new RuntimeException("error! failed for create session factory:" + name, var4);
		}
	}

	private class EnvNodes {
		private final String configName;
		private final Object lock;
		private Map<String, SqlSessionFactory> sqlSessionFactoryMap;

		public EnvNodes(String configName) {
			this.configName = configName;
			this.lock = new Object();
			this.sqlSessionFactoryMap = new HashMap<>();
		}

		public SqlSessionFactory getSqlSessionFactory(String env) {
			SqlSessionFactory sqlSessionFactory = this.sqlSessionFactoryMap.get(env);
			if (null == sqlSessionFactory) {
				synchronized (this.lock) {
					sqlSessionFactory = this.sqlSessionFactoryMap.get(env);
					if (null == sqlSessionFactory) {
						sqlSessionFactory = DBSourceRummyFactory.this.createSqlSessionFactory(this.configName, env);
						this.sqlSessionFactoryMap.put(env, sqlSessionFactory);
					}
				}
			}

			return sqlSessionFactory;
		}
	}
}
