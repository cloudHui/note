package com.mr.model;

import java.util.Date;

public class InfoUserCurrency {

	private Long userId;

	private Long depositRecharge;

	private Long depositFree;

	private Long winRecharge;

	private Long winFree;

	private Long bonusPending;

	private Long bonusCredited;

	private Long practiceChips;

	private Date updateTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDepositRecharge() {
		return depositRecharge;
	}

	public void setDepositRecharge(Long depositRecharge) {
		this.depositRecharge = depositRecharge;
	}

	public Long getDepositFree() {
		return depositFree;
	}

	public void setDepositFree(Long depositFree) {
		this.depositFree = depositFree;
	}

	public Long getWinRecharge() {
		return winRecharge;
	}

	public void setWinRecharge(Long winRecharge) {
		this.winRecharge = winRecharge;
	}

	public Long getWinFree() {
		return winFree;
	}

	public void setWinFree(Long winFree) {
		this.winFree = winFree;
	}

	public Long getBonusPending() {
		return bonusPending;
	}

	public void setBonusPending(Long bonusPending) {
		this.bonusPending = bonusPending;
	}

	public Long getBonusCredited() {
		return bonusCredited;
	}

	public void setBonusCredited(Long bonusCredited) {
		this.bonusCredited = bonusCredited;
	}

	public Long getPracticeChips() {
		return practiceChips;
	}

	public void setPracticeChips(Long practiceChips) {
		this.practiceChips = practiceChips;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}