package com.mr.model;

import java.util.Date;

public class InfoUser {
	private Long userid;

	private Integer state;

	private String username;

	private String password;

	private String paymentpassword;

	private String nickname;

	private String avatar;

	private Integer lang;

	private Integer sex;

	private String age;

	private String nationality;

	private String industry;

	private String job;

	private String phone;

	private String email;

	private Integer rank;

	private String title;

	private Date registertime;

	private Date lastlogintime;

	private String lastloginip;

	private String agentversion;

	private String invitationcode;

	private String hxuuid;

	private Boolean vocalverify;

	private Boolean faceverify;

	private Long creatorid;

	private String channel;

	private String thirdparty;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel == null ? null : channel.trim();
	}

	public String getThirdparty() {
		return thirdparty;
	}

	public void setThirdparty(String thirdparty) {
		this.thirdparty = thirdparty == null ? null : thirdparty.trim();
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public String getPaymentpassword() {
		return paymentpassword;
	}

	public void setPaymentpassword(String paymentpassword) {
		this.paymentpassword = paymentpassword == null ? null : paymentpassword.trim();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname == null ? null : nickname.trim();
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar == null ? null : avatar.trim();
	}

	public Integer getLang() {
		return lang;
	}

	public void setLang(Integer lang) {
		this.lang = lang;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age == null ? null : age.trim();
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality == null ? null : nationality.trim();
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry == null ? null : industry.trim();
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job == null ? null : job.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public Date getRegistertime() {
		return registertime;
	}

	public void setRegistertime(Date registertime) {
		this.registertime = registertime;
	}

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public String getLastloginip() {
		return lastloginip;
	}

	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip == null ? null : lastloginip.trim();
	}

	public String getAgentversion() {
		return agentversion;
	}

	public void setAgentversion(String agentversion) {
		this.agentversion = agentversion == null ? null : agentversion.trim();
	}

	public String getInvitationcode() {
		return invitationcode;
	}

	public void setInvitationcode(String invitationcode) {
		this.invitationcode = invitationcode == null ? null : invitationcode.trim();
	}

	public String getHxuuid() {
		return hxuuid;
	}

	public void setHxuuid(String hxuuid) {
		this.hxuuid = hxuuid == null ? null : hxuuid.trim();
	}

	public Boolean getVocalverify() {
		return vocalverify;
	}

	public void setVocalverify(Boolean vocalverify) {
		this.vocalverify = vocalverify;
	}

	public Boolean getFaceverify() {
		return faceverify;
	}

	public void setFaceverify(Boolean faceverify) {
		this.faceverify = faceverify;
	}

	public Long getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(Long creatorid) {
		this.creatorid = creatorid;
	}

	public InfoUser(Integer state, String username, String password, String paymentpassword, String nickname,
	                String avatar, Integer lang, Integer sex, String age, String nationality, String industry,
	                String job, String phone, String email, Integer rank, String title, Date registertime,
	                Date lastlogintime, String lastloginip, String agentversion, String invitationcode, String hxuuid,
	                Boolean vocalverify, Boolean faceverify, Long creatorid, String channel, String thirdparty) {
		this.state = state;
		this.username = username;
		this.password = password;
		this.paymentpassword = paymentpassword;
		this.nickname = nickname;
		this.avatar = avatar;
		this.lang = lang;
		this.sex = sex;
		this.age = age;
		this.nationality = nationality;
		this.industry = industry;
		this.job = job;
		this.phone = phone;
		this.email = email;
		this.rank = rank;
		this.title = title;
		this.registertime = registertime;
		this.lastlogintime = lastlogintime;
		this.lastloginip = lastloginip;
		this.agentversion = agentversion;
		this.invitationcode = invitationcode;
		this.hxuuid = hxuuid;
		this.vocalverify = vocalverify;
		this.faceverify = faceverify;
		this.creatorid = creatorid;
		this.channel = channel;
		this.thirdparty = thirdparty;
	}
}