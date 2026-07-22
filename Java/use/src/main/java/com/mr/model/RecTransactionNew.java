package com.mr.model;

import java.util.Date;

public class RecTransactionNew {
	private Long id;

	private String channel;

	private Long userId;

	private Integer state;

	private Integer actionType;

	private Long changeVal;

	private Long amount;

	private String transactionExtend;

	private Date insertTime;

	private Long depositRecharge;

	private Long depositFree;

	private Long winRecharge;

	private Long winFree;

	private Long withdrawCharge;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel == null ? null : channel.trim();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}

	public Long getChangeVal() {
		return changeVal;
	}

	public void setChangeVal(Long changeVal) {
		this.changeVal = changeVal;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getTransactionExtend() {
		return transactionExtend;
	}

	public void setTransactionExtend(String transactionExtend) {
		this.transactionExtend = transactionExtend == null ? null : transactionExtend.trim();
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
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

	public Long getWithdrawCharge() {
		return withdrawCharge;
	}

	public void setWithdrawCharge(Long withdrawCharge) {
		this.withdrawCharge = withdrawCharge;
	}
}