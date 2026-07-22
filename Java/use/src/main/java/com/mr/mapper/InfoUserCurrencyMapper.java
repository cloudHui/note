package com.mr.mapper;

import com.mr.model.InfoUserCurrency;

public interface InfoUserCurrencyMapper {
	int deleteByPrimaryKey(Long userId);

	int insert(InfoUserCurrency record);

	int insertSelective(InfoUserCurrency record);

	InfoUserCurrency selectByPrimaryKey(Long userId);

	int updateByPrimaryKeySelective(InfoUserCurrency record);

	int updateByPrimaryKey(InfoUserCurrency record);
}