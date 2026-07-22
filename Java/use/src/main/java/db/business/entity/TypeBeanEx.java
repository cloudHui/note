package db.business.entity;

public class TypeBeanEx implements Comparable<TypeBeanEx> {


	private String type;

	private int num;

	private String percent;


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

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}


	@Override
	public String toString() {
		return this.getType() + ",num:" + this.getNum() + ",percent:" + this.getPercent();
	}

	@Override
	public int compareTo(TypeBeanEx typeBeanO) {
		String so = typeBeanO.getType();
//        int lo = so.length();
//        int valueo = Integer.valueOf(so.substring(5, lo));
		int valueo = Integer.parseInt(so);
		String st = this.getType();
//        int lt = st.length();
//        int valuet = Integer.valueOf(st.substring(5, lt));
		int valuet = Integer.parseInt(st);
		return valuet - valueo;
	}
}
