package db.business.entity;

public class RecUserCashLog {

	private long userId;

	private long charge;

	private long present;

	private long withdraw;


	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCharge() {
		return charge;
	}

	public void setCharge(long charge) {
		this.charge = charge;
	}

	public long getPresent() {
		return present;
	}

	public void setPresent(long present) {
		this.present = present;
	}

	public long getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(long withdraw) {
		this.withdraw = withdraw;
	}
}
