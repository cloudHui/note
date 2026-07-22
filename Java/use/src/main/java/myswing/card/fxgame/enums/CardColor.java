package myswing.card.fxgame.enums;

public enum CardColor {
	NULL(0, "无"),
	DIAMOND(1, "♦"),
	CLUB(2, "♣"),
	HEART(3, "♥"),
	SPADE(4, "♠"),
	JOKER(5, "🃏");

	private final int id;

	private final String desc;


	CardColor(int id, String des) {
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
		return "Color{" + "desc='" + desc + '}';
	}
}
