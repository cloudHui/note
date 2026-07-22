package com.mr.service;

import com.mr.mapper.InfoClubMembersMapper;
import com.mr.model.InfoClubMembers;
import dbutils.DBService;
import dbutils.DBSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class InfoClubMembersService extends DBService<InfoClubMembersMapper> {

	public InfoClubMembersService() {
		this(DBSourceFactory.INSTANCE.getSqlSessionFactory());
	}

	private InfoClubMembersService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, InfoClubMembersMapper.class);
	}


	public int insert(List<InfoClubMembers> clubMembers) {
		return execute(o -> o.insert(clubMembers));
	}
}
