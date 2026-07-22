package db.business.entity;

import java.sql.Date;

/**
 * 加金币
 */
public class Stats {

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 房间号
	 */
	private int roomId;

	/**
	 * 带进来的货币
	 */
	private int bring;

	/**
	 * 赢的货币
	 */
	private int win;

	/**
	 * 控制级别
	 */
	private int controlLevel;

	/**
	 * 控制id
	 */
	private int controlId;

	/**
	 * 倍数
	 */
	private int times;

	/**
	 * 基础分
	 */
	private int base;

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getBring() {
		return bring;
	}

	public void setBring(int bring) {
		this.bring = bring;
	}

	public int getControlLevel() {
		return controlLevel;
	}

	public void setControlLevel(int controlLevel) {
		this.controlLevel = controlLevel;
	}

	public int getControlId() {
		return controlId;
	}

	public void setControlId(int controlId) {
		this.controlId = controlId;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public Stats(GameResultEntity entity, int times, int base) {
		this.endTime = entity.getEndTime();
		this.userName = entity.getUserName();
		this.roomId = entity.getRoomId();
		this.bring = entity.getBring();
		this.win = entity.getWin();
		this.controlLevel = entity.getControlLevel();
		this.controlId = entity.getControlId();
		this.times = times;
		this.base = base;
	}

	@Override
	public String toString() {
		return "userName:" + userName + ",roomId" + roomId + ",win:" + win + ",bring:" + bring
				+ ",controlLevel:" + controlLevel + ",controlId:" + controlId + ",times:" + times + ",base:" + base;
	}
}
