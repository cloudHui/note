package db.business.entity;

import java.sql.Date;

/**
 * 加金币
 */
public class GameResultEntity {

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

	@Override
	public String toString() {
		return "userName:" + userName + ",roomId" + roomId + ",win:" + win + ",bring:" + bring
				+ ",controlLevel:" + controlLevel + ",controlId:" + controlId;
	}
}
