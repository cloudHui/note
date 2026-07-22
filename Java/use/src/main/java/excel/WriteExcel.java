package excel;

import db.business.entity.RecUserCash;
import db.business.service.CashService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class WriteExcel {

	public static void main(String[] args) {
		CashService service = new CashService();
		WriteExcel excel = new WriteExcel();
		HSSFWorkbook wb = new HSSFWorkbook();
		int start = 0, size, length = 500;

		//excel标题
		String[] title = {"用户id", "渠道", "免费", "非免费", "最后更新时间"};
		//sheet名
		String sheetName = "数据";

		int titleLength = title.length;
		ExcelUtil.setHSSFWorkbookTitle(sheetName, title, wb);
		HSSFSheet sheet = wb.getSheet(sheetName);

		do {
			List<RecUserCash> list = selectTimeLimit(service, start, length);
			size = list.size();
			excel.doAddExcelContent(sheet, list, titleLength);
			start = start + length;
		} while (size == length && start < 50000);

		//excel文件名
		String fileName = "RecUserCash.xls";

		ExcelUtil.outFile(wb, fileName);
	}

	//数据库查数据
	private static List<RecUserCash> selectTimeLimit(CashService service, int begin, int end) {
		long start = System.currentTimeMillis();
		List<RecUserCash> winList = service.queryRecCash(begin, end);
		long middle = System.currentTimeMillis();
		start = middle - start;
		int size = winList.size();
		System.out.println("selectTimeLimit:" + start + "ms" + "数量:" + size);
		return winList;
	}

	/**
	 * 写excel 工具数据
	 *
	 * @param sheet 一页
	 * @param list  数据
	 */
	private void doAddExcelContent(HSSFSheet sheet, List<RecUserCash> list, int titleLength) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

		int size = list.size();
		//成员变量
		//Field[] fs = RecUserCash.class.getDeclaredFields();
		String[][] content = new String[size][titleLength];
		for (int begin = 0; begin < size; begin++) {
			content[begin] = new String[titleLength];
			RecUserCash cash = list.get(begin);
			content[begin][0] = String.valueOf(cash.getUserId());
			content[begin][1] = cash.getChannel();
			content[begin][2] = String.valueOf(cash.getFree());
			content[begin][3] = String.valueOf(cash.getNotFree());
			content[begin][4] = String.valueOf(dateFormat.format(cash.getDate()));
		}
		ExcelUtil.addContent(sheet, content);
	}
}
