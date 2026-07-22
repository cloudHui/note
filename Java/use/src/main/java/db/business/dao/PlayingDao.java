package db.business.dao;


import db.business.entity.Playing;
import db.business.entity.RecSe;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PlayingDao {
	List<Playing> queryWin(@Param("playCount") int playCount, @Param("winRobot") int winRobot);

	List<RecSe> queryInfo(@Param("uid") int uid);

	List<RecSe> queryInfoWithEndTime(@Param("endTimeStart") Date endTimeStart, @Param("endTimeEnd") Date endTimeEnd);

	List<RecSe> queryInfoWithEndTimeAndNum(@Param("endTimeStart") Date endTimeStart,
	                                       @Param("endTimeEnd") Date endTimeEnd,
	                                       @Param("begin") int begin,
	                                       @Param("length") int length);


}
