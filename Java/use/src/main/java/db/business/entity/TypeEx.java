package db.business.entity;

import java.util.List;

public class TypeEx implements Comparable<TypeEx> {


	private List<String> type;

	private int num;

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}


	@Override
	public int compareTo(TypeEx o) {
		int lo = o.getNum();

		int valuet = this.getNum();
		return valuet - lo;
	}

//    @Override
//    public String toString() {
//        return this.getType() + ",num:" + this.getUserPoint() + ",percent:" + this.getPercent();
//    }
}
