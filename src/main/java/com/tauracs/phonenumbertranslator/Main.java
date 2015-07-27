package com.tauracs.phonenumbertranslator;

import com.tauracs.phonenumbertranslator.model.Translator;

/**
 * Main class
 */
public class Main {


	/**
	 * Input phone numbers
	 */
	public static String[] INPUTS = new String[]{		
		"112",
		"5624-82",
		"4824",
		"0721/608-4067",
		"10/783--5",
		"1078-913-5",
		"381482",
		"04824"
	};

	/**
	 * Input Dictionary
	 */
	public static String[] DICT = new String[]{
		"an",
		"blau",
		"Bo\"",
		"Boot",
		"bo\"s",
		"da",
		"Fee",
		"fern",
		"Fest",
		"fort",
		"je",
		"jemand",
		"mir",
		"Mix",
		"Mixer",
		"Name",
		"neu",
		"o\"d",
		"Ort",
		"so",
		"Tor",
		"Torf",
		"Wasser"
	};

	/**
	 * Main entry point 
	 * @param args arguments
	 */
	public static void main(final String[] args) {
		
		Translator translator =new  Translator(DICT);
		
		for (int i = 0; i < INPUTS.length; i++) {
			String phoneNumber = INPUTS[i];
			
			if (translator.tryTranslate(phoneNumber) ) {
				System.out.printf(translator.toString());
			}
		}
	}

}
