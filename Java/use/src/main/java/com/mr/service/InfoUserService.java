package com.mr.service;

import com.mr.mapper.InfoUserMapper;
import com.mr.model.InfoUser;
import dbutils.DBService;
import dbutils.DBSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class InfoUserService extends DBService<InfoUserMapper> {

	public InfoUserService() {
		this(DBSourceFactory.INSTANCE.getSqlSessionFactory());
	}

	private InfoUserService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, InfoUserMapper.class);
	}


	public int insert(List<InfoUser> infoUsers) {
		return execute(o -> o.insert(infoUsers));
	}
}
