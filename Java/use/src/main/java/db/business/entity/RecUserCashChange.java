package db.business.entity;


import java.sql.Timestamp;

/**
 * 玩家cash变化
 */
public class RecUserCashChange {

	private long userId;

	private String channel;

	private long free;

	private long notFree;

	private int cause;


	private String des;

	private Timestamp date;

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

	public int getCause() {
		return cause;
	}

	public void setCause(int cause) {
		this.cause = cause;
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

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "RecUserCashChange{" +
				"userId=" + userId +
				", channel='" + channel + '\'' +
				", cause=" + cause +
				", free=" + free +
				", notFree=" + notFree +
				", des='" + des + '\'' +
				", date=" + date +
				'}';
	}
}
