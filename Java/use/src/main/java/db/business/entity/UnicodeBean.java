package db.business.entity;

public class UnicodeBean implements Comparable<UnicodeBean> {


	private String type;

	private int userPoint;

	private int newPointNum;

	private int newNum;

	private int userNum;

	private String percent;

	private String newPercent;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getUserPoint() {
		return userPoint;
	}

	public void setUserPoint(int userPoint) {
		this.userPoint = userPoint;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public int getNewNum() {
		return newNum;
	}

	public void setNewNum(int newNum) {
		this.newNum = newNum;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public int getNewPointNum() {
		return newPointNum;
	}

	public void setNewPointNum(int newPointNum) {
		this.newPointNum = newPointNum;
	}

	public String getNewPercent() {
		return newPercent;
	}

	public void setNewPercent(String newPercent) {
		this.newPercent = newPercent;
	}

	@Override
	public int compareTo(UnicodeBean o) {
		String so = o.getType();
//        int lo = so.length();
//        int valueo = Integer.valueOf(so.substring(5, lo));
		int valueo = Integer.parseInt(so);
		String st = this.getType();
//        int lt = st.length();
//        int valuet = Integer.valueOf(st.substring(5, lt));
		int valuet = Integer.parseInt(st);
		return valuet - valueo;
	}

	@Override
	public String toString() {
		return "点位:" + this.getType() + ",用户数量:" + getUserNum() + ",用户点击次数:" + this.getUserPoint()
				+ ",用户点击率:" + this.getPercent()
				+ ", 新用户数量:" + this.getNewNum() + ",新用户点击次数:" + this.getNewPointNum()
				+ ",新用户点击率:" + this.getNewPercent();
	}
}
