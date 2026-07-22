package db.business.dao;


import db.business.entity.DeviceIpEntity;
import db.business.entity.IdentityCarsEntity;
import db.business.entity.KycEntity;
import db.business.entity.RecTransaction;
import db.business.entity.RecUserCash;
import db.business.entity.RecUserCashChange;
import db.business.entity.RecUserCashLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CashDao {

	int updateRecUserCash(RecUserCashChange cash);

	List<RecTransaction> queryRecTransWithEndTimeAndNum(@Param("endTimeStart") Date endTimeStart,
	                                                    @Param("endTimeEnd") Date endTimeEnd,
	                                                    @Param("begin") int begin,
	                                                    @Param("length") int length);

	List<RecUserCash> queryRecCash(@Param("begin") int begin, @Param("length") int length);


	List<KycEntity> queryKyc();

	List<KycEntity> queryCardNum();

	List<KycEntity> queryByCardNumbs(@Param("list") List<String> list);

	List<DeviceIpEntity> queryDeviceAndIp(@Param("list") List<Long> list);

	List<IdentityCarsEntity> queryIdentityCars(@Param("begin") int begin, @Param("length") int length);

	List<Long> getRegistersUserId();

	List<RecUserCashLog> getLastCashLog(@Param("userId") long userId);


}
