package excel;

import db.business.entity.RecUserCashLog;
import db.business.service.CashService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

public class WriteLastRegisterCash {


	public static void main(String[] args) {
		CashService service = new CashService();
		WriteLastRegisterCash excel = new WriteLastRegisterCash();
		HSSFWorkbook wb = new HSSFWorkbook();

		//excel标题

		String[] title = {"用户id", "充值", "赠送", "提现"};
		//sheet名
		String sheetName = "数据";

		int titleLength = title.length;
		ExcelUtil.setHSSFWorkbookTitle(sheetName, title, wb);
		HSSFSheet sheet = wb.getSheet(sheetName);

		List<RecUserCashLog> list = selectTimeLimit(service);

		excel.doAddExcelContent(sheet, list, titleLength);

		//excel文件名
		String fileName = "cash.xls";

		ExcelUtil.outFile(wb, fileName);


	}

	//数据库查数据
	private static List<RecUserCashLog> selectTimeLimit(CashService service) {
		long start = System.currentTimeMillis();
		List<Long> winList = service.getRgisterUserId();
		int size = winList.size();
		long now = System.currentTimeMillis();
		System.out.println("getRegisterUserId:" + (now - start) + "ms" + "数量:" + size);
		start = now;
		List<RecUserCashLog> cashes = new ArrayList<>();
		for (long entity : winList) {
			List<RecUserCashLog> cash = service.getLastCashLog(entity);
			if (cash != null && !cash.isEmpty()) {
				cashes.addAll(cash);
			}
		}
		size = cashes.size();
		now = System.currentTimeMillis();
		System.out.println("getLastCashLog:" + (now - start) + "ms" + "数量:" + size);
		return cashes;
	}

	/**
	 * 写excel 工具数据
	 *
	 * @param sheet 一页
	 * @param list  数据
	 */
	private void doAddExcelContent(HSSFSheet sheet, List<RecUserCashLog> list, int titleLength) {

		int size = list.size();
		//成员变量
		//Field[] fs = RecUserCash.class.getDeclaredFields();
		String[][] content = new String[size][titleLength];
		for (int begin = 0; begin < size; begin++) {
			content[begin] = new String[titleLength];
			RecUserCashLog cash = list.get(begin);
			//String[] title = {"用户id", "充值", "赠送", "提现"};
			content[begin][0] = String.valueOf(cash.getUserId());
			content[begin][1] = String.valueOf(cash.getCharge());
			content[begin][2] = String.valueOf(cash.getPresent());
			content[begin][3] = String.valueOf(cash.getWithdraw());
		}
		ExcelUtil.addContent(sheet, content);
	}
}
