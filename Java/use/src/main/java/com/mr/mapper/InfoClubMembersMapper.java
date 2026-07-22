package com.mr.mapper;

import com.mr.model.InfoClubMembers;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InfoClubMembersMapper {

	int insert(@Param("list") List<InfoClubMembers> infoUsers);
}