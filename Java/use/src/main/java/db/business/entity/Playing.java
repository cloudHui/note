package db.business.entity;

public class Playing {
	private int userId;

	private int playCount;

	private int winCount;

	private int winRobot;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public int getWinRobot() {
		return winRobot;
	}

	public void setWinRobot(int winRobot) {
		this.winRobot = winRobot;
	}
}
