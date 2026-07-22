package db.business.entity;

/**
 * 加金币
 */
public class ConfigRoomEntity {

	/**
	 * 房间类型
	 */
	private int roomId;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 基础分数
	 */
	private int baseScore;

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBaseScore() {
		return baseScore;
	}

	public void setBaseScore(int baseScore) {
		this.baseScore = baseScore;
	}
}
