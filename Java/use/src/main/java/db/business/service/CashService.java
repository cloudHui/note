package db.business.service;

import db.business.dao.CashDao;
import db.business.entity.DeviceIpEntity;
import db.business.entity.IdentityCarsEntity;
import db.business.entity.KycEntity;
import db.business.entity.RecTransaction;
import db.business.entity.RecUserCash;
import db.business.entity.RecUserCashChange;
import db.business.entity.RecUserCashLog;
import db.factory.DBSourceRummyFactory;
import dbutils.DBService;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.List;


public class CashService extends DBService<CashDao> {
	public CashService() {
		this(DBSourceRummyFactory.INSTANCE.getSqlSessionFactory());
	}

	private CashService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, CashDao.class);
	}

	public int updateRecUserCash(RecUserCashChange cash) {
		return execute(o -> o.updateRecUserCash(cash));
	}

	public List<RecTransaction> queryRecTransWithEndTimeAndNum(Date dataStart, Date dateEnd, int begin, int length) {
		return execute(o -> o.queryRecTransWithEndTimeAndNum(dataStart, dateEnd, begin, length));
	}

	public List<RecUserCash> queryRecCash(int begin, int length) {
		return execute(o -> o.queryRecCash(begin, length));
	}

	public List<KycEntity> queryKyc() {
		return execute(CashDao::queryKyc);
	}

	public List<KycEntity> queryCardNum() {
		return execute(CashDao::queryCardNum);
	}

	public List<KycEntity> queryByCardNums(List<String> list) {
		return execute(o -> o.queryByCardNumbs(list));
	}

	public List<DeviceIpEntity> queryDeviceAndIp(List<Long> list) {
		return execute(o -> o.queryDeviceAndIp(list));
	}

	public List<IdentityCarsEntity> queryIdentityCars(int begin, int length) {
		return execute(o -> o.queryIdentityCars(begin, length));
	}

	public List<Long> getRgisterUserId() {
		return execute(CashDao::getRegistersUserId);
	}

	public List<RecUserCashLog> getLastCashLog(long userId) {
		return execute(o -> o.getLastCashLog(userId));
	}
}
