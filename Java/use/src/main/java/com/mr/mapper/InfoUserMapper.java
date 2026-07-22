package com.mr.mapper;

import com.mr.model.InfoUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InfoUserMapper {

	int insert(@Param("list") List<InfoUser> infoUsers);
}