package com.mr.model;

import java.util.Date;

public class InfoClubMembers {
	private Integer relationshiptype;

	private Integer role;

	private Integer privilege;

	private Date jointime;

	public Integer getRelationshiptype() {
		return relationshiptype;
	}

	public void setRelationshiptype(Integer relationshiptype) {
		this.relationshiptype = relationshiptype;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Integer privilege) {
		this.privilege = privilege;
	}

	public Date getJointime() {
		return jointime;
	}

	public void setJointime(Date jointime) {
		this.jointime = jointime;
	}

	private Long clubid;

	private Long userid;

	public Long getClubid() {
		return clubid;
	}

	public void setClubid(Long clubid) {
		this.clubid = clubid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public InfoClubMembers(Integer relationshiptype, Integer role, Integer privilege, Date jointime, Long clubid, Long userid) {
		this.relationshiptype = relationshiptype;
		this.role = role;
		this.privilege = privilege;
		this.jointime = jointime;
		this.clubid = clubid;
		this.userid = userid;
	}
}