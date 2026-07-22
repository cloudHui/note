package db.business.entity;

/**
 * IdentityCarsEntity date
 */
public class IdentityCarsEntity {

	private long userId;

	private String front;

	private String back;


	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}
}
