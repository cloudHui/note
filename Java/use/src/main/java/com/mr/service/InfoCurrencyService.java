package com.mr.service;

import com.mr.mapper.InfoCurrencyMapper;
import com.mr.model.InfoCurrency;
import dbutils.DBService;
import dbutils.DBSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class InfoCurrencyService extends DBService<InfoCurrencyMapper> {

	public InfoCurrencyService() {
		this(DBSourceFactory.INSTANCE.getSqlSessionFactory());
	}

	private InfoCurrencyService(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory, InfoCurrencyMapper.class);
	}


	public int insert(List<InfoCurrency> currencies) {
		return execute(o -> o.insert(currencies));
	}
}
