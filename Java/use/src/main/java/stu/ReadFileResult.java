package stu;

/**
 * 按定长读取文件
 */
public class ReadFileResult {

	/**
	 * 结果
	 */
	private String result;

	/**
	 * 读到位置
	 */
	private long pos;

	/**
	 * 字节数
	 */
	private int byteSize = 1024;

	public ReadFileResult() {
	}

	public ReadFileResult(String result, long pos, int byteSize) {
		this.result = result;
		this.pos = pos;
		this.byteSize = byteSize;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public int getByteSize() {
		return byteSize;
	}

	public void setByteSize(int byteSize) {
		this.byteSize = byteSize;
	}

	@Override
	public String toString() {
		return "ReadFileResult{" +
				"result='" + result + '\'' +
				", pos=" + pos +
				", byteSize=" + byteSize +
				'}';
	}
}
