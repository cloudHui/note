package excel;

import db.business.entity.DeviceIpEntity;
import db.business.entity.KycEntity;
import db.business.service.CashService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteKeyExcel {


	public static void main(String[] args) {
		CashService service = new CashService();
		WriteKeyExcel excel = new WriteKeyExcel();
		HSSFWorkbook wb = new HSSFWorkbook();

		//excel标题

		String[] title = {"用户id", "身份证号", "用户名", "生日", "地区", "设备号", "ip"};
		//sheet名
		String sheetName = "数据";

		int titleLength = title.length;
		ExcelUtil.setHSSFWorkbookTitle(sheetName, title, wb);
		HSSFSheet sheet = wb.getSheet(sheetName);

		List<KycEntity> list = selectTimeLimit(service);

		excel.doAddExcelContent(sheet, list, titleLength);

		//excel文件名
		String fileName = "Kyc.xls";

		ExcelUtil.outFile(wb, fileName);


	}

	//数据库查数据
	private static List<KycEntity> selectTimeLimit(CashService service) {
		long start = System.currentTimeMillis();
		List<KycEntity> winList = service.queryCardNum();
		int size = winList.size();
		long now = System.currentTimeMillis();
		System.out.println("queryCardNum:" + (now - start) + "ms" + "数量:" + size);
		start = now;
		List<String> cardNums = new ArrayList<>();
		for (KycEntity entity : winList) {
			String cardNum = entity.getCardNum();
			if (cardNum != null && cardNum.length() > 0) {
				cardNums.add(entity.getCardNum());
			}
		}
		winList.clear();
		winList = service.queryByCardNums(cardNums);
		size = winList.size();
		now = System.currentTimeMillis();
		System.out.println("queryByIds:" + (now - start) + "ms" + "数量:" + size);
		start = now;

		Map<Long, KycEntity> kycEntityMap = new HashMap<>();
		List<Long> ids = new ArrayList<>();
		for (KycEntity entity : winList) {
			long id = entity.getUserId();
			ids.add(id);
			kycEntityMap.put(id, entity);
		}
		List<DeviceIpEntity> deviceIpEntities = service.queryDeviceAndIp(ids);
		for (DeviceIpEntity entity : deviceIpEntities) {
			KycEntity entity1 = kycEntityMap.get(entity.getId());
			entity1.setDevice(entity.getDevice_info());
			entity1.setIp(entity.getLast_login_ip());
		}
		size = deviceIpEntities.size();
		now = System.currentTimeMillis();
		System.out.println("getDevice_info:" + (now - start) + "ms" + "数量:" + size);

		return winList;
	}

	/**
	 * 写excel 工具数据
	 *
	 * @param sheet 一页
	 * @param list  数据
	 */
	private void doAddExcelContent(HSSFSheet sheet, List<KycEntity> list, int titleLength) {

		int size = list.size();
		//成员变量
		//Field[] fs = RecUserCash.class.getDeclaredFields();
		String[][] content = new String[size][titleLength];
		for (int begin = 0; begin < size; begin++) {
			content[begin] = new String[titleLength];
			KycEntity cash = list.get(begin);
			//String[] title = {"用户id", "身份证号", "用户名", "生日","地区","设备号","ip"};
			content[begin][0] = String.valueOf(cash.getUserId());
			content[begin][1] = cash.getCardNum();
			content[begin][2] = cash.getCardName();
			content[begin][3] = cash.getBirthday();
			content[begin][4] = cash.getPlacestate();
			content[begin][5] = cash.getDevice();
			content[begin][6] = cash.getIp();
		}
		ExcelUtil.addContent(sheet, content);
	}
}
