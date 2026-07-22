package download.music;

import java.util.Objects;

public class HifiniMusic {
	private String author;

	private String downUrl;

	private String savePath;

	public HifiniMusic(String author, String downUrl, String savePath) {
		this.author = author;
		this.downUrl = downUrl;
		this.savePath = savePath;
	}

	public String getAuthor() {
		return author;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	@Override
	public String toString() {
		return "HifiniMusic{" +
				"author='" + author + '\'' +
				", downUrl='" + downUrl + '\'' +
				", savePath='" + savePath + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HifiniMusic music = (HifiniMusic) o;
		return Objects.equals(author, music.author);
	}

	@Override
	public int hashCode() {
		return Objects.hash(author);
	}
}
