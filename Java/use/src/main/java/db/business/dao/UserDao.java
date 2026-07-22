package db.business.dao;


import db.business.entity.ConfigRoomEntity;
import db.business.entity.GameResultEntity;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

public interface UserDao {
	List<GameResultEntity> queryGameResult(@Param("after") Date after, @Param("end") Date end);

	List<ConfigRoomEntity> loadRoom();
}
