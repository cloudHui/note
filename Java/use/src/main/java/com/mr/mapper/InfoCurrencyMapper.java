package com.mr.mapper;

import com.mr.model.InfoCurrency;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InfoCurrencyMapper {

	int insert(@Param("list") List<InfoCurrency> infoUsers);
}