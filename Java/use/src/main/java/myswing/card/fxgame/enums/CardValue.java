package myswing.card.fxgame.enums;

public enum CardValue {
	THREE(3, "3"),
	FOUR(4, "4"),
	FIVE(5, "5"),
	SIX(6, "6"),
	SEVEN(7, "7"),
	EIGHT(8, "8"),
	NINE(9, "9"),
	TEN(10, "10"),
	ELEVEN(11, "J"),
	TWELVE(12, "Q"),
	THIRTEEN(13, "K"),
	FOURTEEN(14, "A"),
	FIFTEEN(15, "2");

	;

	private final int id;

	private final String desc;


	CardValue(int id, String des) {
		this.id = id;
		this.desc = des;
	}

	public int getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "Value{" + "desc='" + desc + '}';
	}
}
