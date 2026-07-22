package picture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.spi.FileTypeDetector;
import java.text.DecimalFormat;
import java.util.Objects;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

public class GetPictureLocation {

	public static void main(String[] args) throws JpegProcessingException, IOException {

		File jpegFile = new File("D:/dingding/1.jpg");
		Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
		GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
		if (Objects.nonNull(gpsDirectory)) {
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			if (geoLocation != null) {
				System.out.println(geoLocation.getLongitude());
				System.out.println(geoLocation.getLatitude());
			} else {
				System.out.println("location null");
			}
		}
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
//				System.out.println("名称：" + tag.getTagName() + "========内容：" + tag.getDescription());

				//获取纬度
				if ("GPS Latitude".equals(tag.getTagName())) {
					System.out.println("Latitude==" + tag.getDescription());
				}
				//获取经度
				if ("GPS Longitude".equals(tag.getTagName())) {
					System.out.println("Longitude==" + tag.getDescription());
				}
				//获取拍摄时间
				if ("File Modified Date".equals(tag.getTagName())) {
					System.out.println("File Modified Date" + tag.getDescription());
				}
				//文件拍摄时间
				if ("Date/Time".equals(tag.getTagName())) {
					System.out.println("Date/Time " + tag.getDescription());
				}
				//拍摄设备名
				if ("Model".equals(tag.getTagName())) {
					System.out.println("Model" + tag.getDescription());
				}
				//获取文件名
				if ("File Name".equals(tag.getTagName())) {
					System.out.println("File Name" + tag.getDescription());
				}
				//文件大小
				if ("File Size".equals(tag.getTagName())) {
					System.out.println("File Size" + tag.getDescription());
				}
			}
		}
	}


	/**
	 * 经纬度格式  转换为  度分秒格式转换
	 *
	 * @param point 坐标点
	 * @return
	 */
	public static String pointToLatlong(String point) {
		double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
		double fen = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim());
		double miao = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim());
		double duStr = du + fen / 60 + miao / 60 / 60;
		return Double.toString(duStr);
	}

	/**
	 * 把byte转化为KB、MB、GB
	 *
	 * @param size
	 * @return
	 */
	public static String getNetFileSizeDescription(long size) {
		StringBuilder bytes = new StringBuilder();
		DecimalFormat format = new DecimalFormat("###.00");
		if (size >= 1024L * 1024L * 1024L * 1024L) {
			double i = (size / (1024.0 * 1024.0 * 1024.0 * 1024.0));
			bytes.append(format.format(i)).append("TB");
		} else if (size >= 1024L * 1024L * 1024L) {
			double i = (size / (1024.0 * 1024.0 * 1024.0));
			bytes.append(format.format(i)).append("GB");
		} else if (size >= 1024L * 1024L) {
			double i = (size / (1024.0 * 1024.0));
			bytes.append(format.format(i)).append("MB");
		} else if (size >= 1024L) {
			double i = (size / (1024.0));
			bytes.append(format.format(i)).append("KB");
		} else {
			bytes.append("0B");
		}
		return bytes.toString();
	}
}
