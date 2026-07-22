package excel;

import com.mysql.cj.util.StringUtils;
import db.business.entity.IdentityCarsEntity;
import db.business.service.CashService;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WritePictureExcel {


	public static void main(String[] args) {
		CashService service = new CashService();
		WritePictureExcel excel = new WritePictureExcel();
		HSSFWorkbook wb = new HSSFWorkbook();

		//excel标题

		String[] title = {"用户id", "身份证正面", "身份证正面图", "身份证反面", "身份证反面图"};

		//sheet名
		String sheetName = "数据";

		int titleLength = title.length;
		ExcelUtil.setHSSFWorkbookTitle(sheetName, title, wb);
		HSSFSheet sheet = wb.getSheet(sheetName);

		List<IdentityCarsEntity> list = selectTimeLimit(service);

		excel.doAddExcelContent(sheet, list, titleLength, wb);

		//excel文件名
		String fileName = "identityCard.xls";

		ExcelUtil.outFile(wb, fileName);


	}

	//数据库查数据
	private static List<IdentityCarsEntity> selectTimeLimit(CashService service) {
		long startTime = System.currentTimeMillis();

		List<IdentityCarsEntity> userCashList = new ArrayList<>(10000);
		int start = 0, length = 10;//500
		int size;
//        do {
		List<IdentityCarsEntity> cashList = service.queryIdentityCars(start, length);
		size = cashList.size();
		if (size > 0) {
			userCashList.addAll(cashList);
		}
		start = start + length;
		long now = System.currentTimeMillis();
		System.out.println("查询queryIdentityCars:" + size + " 耗时:" + (now - startTime) + "ms");
		startTime = now;
//        } while (size == length);
		System.out.println("queryIdentityCars:" + (now - start) + "ms" + "数量:" + size);
		return userCashList;
	}

	/**
	 * 写excel 工具数据
	 *
	 * @param sheet 一页
	 * @param list  数据
	 */
	private void doAddExcelContent(HSSFSheet sheet, List<IdentityCarsEntity> list, int titleLength, HSSFWorkbook wb) {

		int size = list.size();
		String[][] content = new String[size][titleLength];
		for (int begin = 0; begin < size; begin += 4) {
			content[begin] = new String[titleLength];
			IdentityCarsEntity cash = list.get(begin);
			//String[] title = {"用户id", "身份证正面", "身份证正面图", "身份证反面","身份证反面图"};
			content[begin][0] = String.valueOf(cash.getUserId());
			content[begin][1] = cash.getFront();
			String imgUrl = cash.getFront();
			addPicture(imgUrl, sheet, begin, wb, 2);

			content[begin][3] = cash.getBack();
			imgUrl = cash.getBack();
			addPicture(imgUrl, sheet, begin, wb, 4);
		}
		ExcelUtil.addContent(sheet, content);
	}

	private void addPicture(String imgUrl, HSSFSheet sheet, int index, HSSFWorkbook wb, int width) {
		if (!StringUtils.isNullOrEmpty(imgUrl)) {
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			BufferedImage bufferImg = null;
			//网络图片用下面的方法处理不适合本地图片
			URL url = null;
			try {
				url = new URL(imgUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			if (url != null) {
				try {
					bufferImg = ImageIO.read(url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufferImg != null) {
				try {
					String type = imgUrl.substring(imgUrl.lastIndexOf(".") + 1);
					int typeInt = HSSFWorkbook.PICTURE_TYPE_JPEG;
					if ("png".equals(type)) {
						typeInt = HSSFWorkbook.PICTURE_TYPE_PNG;
					}
					ImageIO.write(bufferImg, type, byteArrayOut);
					// width 第几列
					HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,
							(short) (width), (1 + index), (short) (width + 1), (2 + index));
					patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), typeInt));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
