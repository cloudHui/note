package db.business.entity;

public class StatCost implements Comparable<StatCost> {


	private int type;

	private long cost;

	private double lastCost;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public StatCost(int type, long cost) {
		this.type = type;
		this.cost = cost;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public double getLastCost() {
		return lastCost;
	}

	public void setLastCost(double lastCost) {
		this.lastCost = lastCost;
	}

	@Override
	public int compareTo(StatCost o) {
		return this.getType() - o.getType();
	}

	@Override
	public String toString() {
		return " " + getType() + " cost:" + getLastCost() + "ms";
	}
}
