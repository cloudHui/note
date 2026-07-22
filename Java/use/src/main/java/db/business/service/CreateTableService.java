package db.business.service;

import db.business.dao.CreateTableDao;
import db.business.entity.ServerEnterty;
import dbutils.DBService;
import dbutils.DBSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;


public class CreateTableService extends DBService<CreateTableDao> {
	public CreateTableService() {
		this(DBSourceFactory.INSTANCE.getSqlSessionFactory());
	}

	private CreateTableService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, CreateTableDao.class);
	}


	public int insert(ServerEnterty serverEnterty, String tableName) {
		return execute(o -> o.insert(serverEnterty, tableName));
	}

	public int createTable(String tableName) {
		return execute(o -> o.createTable(tableName));
	}
}
