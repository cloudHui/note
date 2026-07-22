package db.business.entity;

public class Stat implements Comparable<Stat> {
	private Double value;

	private int times;


	public Stat(Double value, int times) {
		this.value = value;
		this.times = times;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public int compareTo(Stat o) {
		return o.getTimes() - this.getTimes();
	}
}
