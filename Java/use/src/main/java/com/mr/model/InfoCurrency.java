package com.mr.model;

import java.util.Date;

public class InfoCurrency {
	private Long amount;

	private Date updatetime;

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	private Long userid;

	private Integer currencytype;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Integer getCurrencytype() {
		return currencytype;
	}

	public void setCurrencytype(Integer currencytype) {
		this.currencytype = currencytype;
	}

	public InfoCurrency(Long amount, Date updatetime, Long userid, Integer currencytype) {
		this.amount = amount;
		this.updatetime = updatetime;
		this.userid = userid;
		this.currencytype = currencytype;
	}
}