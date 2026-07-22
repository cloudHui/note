package com.mr.mapper;

import com.mr.model.RecTransactionNew;

public interface RecTransactionNewMapper {
	int deleteByPrimaryKey(Long id);

	int insert(RecTransactionNew record);

	int insertSelective(RecTransactionNew record);

	RecTransactionNew selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(RecTransactionNew record);

	int updateByPrimaryKey(RecTransactionNew record);
}