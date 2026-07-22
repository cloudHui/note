package db.business.entity;


import java.util.Date;

/**
 * 数据库记录数据
 */
public class RecTransaction {

	private long userId;

	private String channel;

	private int actionType;

	private String des;

	private long val;

	private Date date;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public long getVal() {
		return val;
	}

	public void setVal(long val) {
		this.val = val;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
