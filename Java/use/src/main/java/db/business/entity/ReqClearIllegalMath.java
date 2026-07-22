package db.business.entity;


/**
 * 请求删除非法比赛
 */
public class ReqClearIllegalMath {

	/**
	 * 比赛id
	 */
	private String matchGuid;

	public String getMatchGuid() {
		return matchGuid;
	}

	public void setMatchGuid(String matchGuid) {
		this.matchGuid = matchGuid;
	}
}
