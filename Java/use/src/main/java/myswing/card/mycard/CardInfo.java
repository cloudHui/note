//package myswing.card.mycard;
//
//import myswing.card.fxgame.enums.Color;
//
///**
// * 牌信息
// */
//public class CardInfo {
//	/**
//	 * 花色
//	 */
//	private Color color;
//
//	/**
//	 * 排值
//	 */
//	private int value;
//
//	public CardInfo(Color color, int value) {
//		this.color = color;
//		this.value = value;
//	}
//
//	public Color getColor() {
//		return color;
//	}
//
//	public void setColor(Color color) {
//		this.color = color;
//	}
//
//	public int getvalue() {
//		return value;
//	}
//
//	public void setPoint(int value) {
//		this.value = value;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof CardInfo) {
//			CardInfo objCard = (CardInfo) obj;
//			return objCard.color.equals(color) && objCard.value == value;
//		}
//		return false;
//	}
//
//	@Override
//	public String toString() {
//		return "CardInfo{ color=" + color + ", value=" + value + '}';
//	}
//}