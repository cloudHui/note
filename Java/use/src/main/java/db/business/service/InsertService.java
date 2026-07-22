package db.business.service;

import db.business.dao.TimeDao;
import db.business.entity.TimeEntity;
import dbutils.DBService;
import dbutils.DBSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;


public class InsertService extends DBService<TimeDao> {
	public InsertService() {
		this(DBSourceFactory.INSTANCE.getSqlSessionFactory());
	}

	private InsertService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, TimeDao.class);
	}


	public int insertTimes(TimeEntity timeEntity) {
		return execute(o -> o.insertTimes(timeEntity));
	}
}
