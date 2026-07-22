package com.mr.test;

import com.mr.model.InfoClubMembers;
import com.mr.model.InfoCurrency;
import com.mr.model.InfoUser;
import com.mr.service.InfoClubMembersService;
import com.mr.service.InfoCurrencyService;
import com.mr.service.InfoUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDHPK {

	public static void main(String[] args) {

		InfoClubMembersService membersService = new InfoClubMembersService();

		InfoCurrencyService currencyService = new InfoCurrencyService();


		InfoUserService userService = new InfoUserService();

		int insertNum = 500;
		List<InfoUser> userList = new ArrayList<>(insertNum);
		Date date = new Date();
		String name = "yace1%03d";
		for (int index = 0; index < insertNum; index++) {
			String nick = String.format(name, index);
			InfoUser infoUser = new InfoUser(0, nick, "9a3c03214ee6963ba0ed",
					"", nick, "1017", 0, 1, "90后",
					"中华人民共和国", "图图", "停止运营", "55663", null,
					0, "", date, null, "",
					"3.0.0.0_006f5内部版_2", "AGDH4",
					"ffc7ff80-a7d2-11eb-96df-f5764c5c0e8e",
					false, false, 0L, "vip_1",
					nick);
			userList.add(infoUser);
		}
		userService.insert(userList);

		long defaultAmount = 100000, userId, clubId = 8L;
		int type = 0, relation = -1;
		List<InfoCurrency> currencyList = new ArrayList<>(insertNum);
		List<InfoClubMembers> clubMembers = new ArrayList<>(insertNum);
		for (InfoUser infoUser : userList) {
			userId = infoUser.getUserid();
			InfoCurrency currency = new InfoCurrency(defaultAmount, date, userId, type);
			currencyList.add(currency);
			InfoClubMembers members = new InfoClubMembers(relation, type, type, date, clubId, userId);
			clubMembers.add(members);
		}
		currencyService.insert(currencyList);

		membersService.insert(clubMembers);
	}
}
