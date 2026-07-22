package db.business.entity;

public class TypeBean implements Comparable<TypeBean> {


	private String type;

	private int num;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public int compareTo(TypeBean o) {
		return o.getNum() - this.getNum();
	}

	@Override
	public String toString() {
		return this.getType() + " " + this.getNum();
	}
}
