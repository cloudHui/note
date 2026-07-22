package db.business.entity;

import java.util.Date;

/**
 * 玩家cash
 */
public class RecUserCash {

	private long userId;

	private String channel;

	private long free;

	private long notFree;

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

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public long getNotFree() {
		return notFree;
	}

	public void setNotFree(long notFree) {
		this.notFree = notFree;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "RecUserCash{" +
				"userId=" + userId +
				", channel='" + channel + '\'' +
				", free=" + free +
				", notFree=" + notFree +
				", date=" + date +
				'}';
	}
}
