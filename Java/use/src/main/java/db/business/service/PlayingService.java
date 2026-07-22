package db.business.service;

import db.business.dao.PlayingDao;
import db.business.entity.Playing;
import db.business.entity.RecSe;
import db.factory.DBSourceRummyFactory;
import dbutils.DBService;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.List;


public class PlayingService extends DBService<PlayingDao> {
	public PlayingService() {
		this(DBSourceRummyFactory.INSTANCE.getSqlSessionFactory());
	}

	private PlayingService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, PlayingDao.class);
	}


	public List<Playing> queryWin(int winRobot, int playCount) {
		return execute(o -> o.queryWin(playCount, winRobot));
	}

	public List<RecSe> queryInfo(int uid) {
		return execute(o -> o.queryInfo(uid));
	}

	public List<RecSe> queryInfoWithEndTime(Date dataStart, Date dateEnd) {
		return execute(o -> o.queryInfoWithEndTime(dataStart, dateEnd));
	}

	public List<RecSe> queryInfoWithEndTimeAndNum(Date dataStart, Date dateEnd, int begin, int length) {
		return execute(o -> o.queryInfoWithEndTimeAndNum(dataStart, dateEnd, begin, length));
	}
}
