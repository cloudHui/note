package db.business.dao;


import db.business.entity.ServerEnterty;
import org.apache.ibatis.annotations.Param;

public interface CreateTableDao {

	int insert(@Param("entity") ServerEnterty entity, @Param("tableName") String tableName);

	int createTable(@Param("tableName") String tableName);
}
