package com.quantum.ampmjobs.utility;

import java.text.ParseException;
import java.time.LocalDate;

class GFG {
	public static void main(final String[] args) throws ParseException {
		LocalDate date1 = LocalDate.now();
		LocalDate date2 = date1.plusDays(1);

		System.out.println("d1 : " + date1);
		System.out.println("d2 : " + date2);

		System.out.println(date1.isBefore(date2));
		System.out.println(date1.isEqual(date2));
		System.out.println(date1.isAfter(date2));
	}
}