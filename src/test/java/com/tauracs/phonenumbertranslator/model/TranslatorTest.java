package com.tauracs.phonenumbertranslator.model;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * Basic unit test for check some simple translation scenarios
 */
public class TranslatorTest {
	
	/**
	 * Constructor of the log file
	 */
	public TranslatorTest() {
		Logger.getGlobal().setLevel(Level.INFO);
	}

	/**
	 * Checks use case when 4824 is encoded with the dictionary "Tor" 
	 */
	@Test
	public final void Check_output_if_INPUT_is_4824_and_DICTIONARY_contains_Tor() {
		Translator t = new Translator(new String[]{"Tor"});
		t.tryTranslate("4824");
		assertNotNull("message", t);
		assertEquals("Tor 4", t.getResults()[0]);
	}

	/**
	 * Checks use case when 381482 is encoded with the dictionary "so","Tor" 
	 */
	@Test
	public final void Check_output_if_INPUT_is_381482_and_DICTIONARY_contains_so_Tor() {
		Translator t = new Translator(new String[]{"Tor", "so"});
		t.tryTranslate("381482");
		assertNotNull("message", t);
		assertEquals("so 1 Tor", t.getResults()[0]);
	}

	/**
	 * Checks use case when 562482 is encoded with the dictionary "mir","Tor","Mix"
	 */
	@Test
	public final void Check_output_if_INPUT_is_562482_and_DICTIONARY_contains_mir_Mix_Tor() {
		Translator t = new Translator(new String[]{"Tor", "mir", "Mix"});
		t.tryTranslate("5624-82");
		assertNotNull("message", t);
		assertEquals("mir Tor", t.getResults()[0]);
		assertEquals("Mix Tor", t.getResults()[1]);
	}
	
	/**
	 * Checks use case when dictionary and phone numbers are taken from file
	 */
	@Test
	public final void Check_output_when_small_INPUT_and_DICTIONARY_files_are_loaded() {
		
		String phoneFilePath_ = "src/test/java/com/tauracs/phonenumbertranslator/model/SmallPhoneNumberFile.txt";
		String dictionaryPath_ = "src/test/java/com/tauracs/phonenumbertranslator/model/SmallDictionary.txt";
		loadFilesAndExecuteTranslation(phoneFilePath_, dictionaryPath_);
	}
	
	/**
	 * Returns a set of string rows that are expected for the small dictionary and input  
	 * 
	 * @return the result set defined in the requirement document
	 */
	public final Set<String> getExpectedLines(){
		Set<String> result = new HashSet<String>();
		
		result.add("5624-82: mir Tor");
		result.add("5624-82: Mix Tor");
		result.add("4824: Torf");
		result.add("4824: fort");
		result.add("4824: Tor 4");
		result.add("10/783--5: neu o\"d 5");
		result.add("10/783--5: je bo\"s 5");
		result.add("10/783--5: je Bo\" da");
		result.add("381482: so 1 Tor");
		result.add("04824: 0 Torf");
		result.add("04824: 0 fort");
		result.add("04824: 0 Tor 4");
		return result;
	}

	/**
	 * Loads the given phone numbers and dictionary files and executes translation
	 * @param phoneFilePath_ File containing the phone numbers
	 * @param dictionaryPath_ File containing the dictionary entries
	 */
	private void loadFilesAndExecuteTranslation(String phoneFilePath_,
			String dictionaryPath_) {
		BufferedReader reader = null;
		Set<String> expectedLines = getExpectedLines();
		
		try {
			Translator t = new Translator(dictionaryPath_);			
			String sCurrentLine = null;
			reader = new BufferedReader(new FileReader(phoneFilePath_));
			
			while((sCurrentLine = reader.readLine()) != null) {
				if(t.tryTranslate(sCurrentLine)){					
					String[] rows = t.toString().split("\n");
					
					for (int i = 0; i < rows.length; i++) {
						expectedLines.remove(rows[i]);	
					}
				}
			}
			
			assertEquals("Not all the expected lines were generated", 0, expectedLines.size());
			
		} catch(IOException ex) {
			Logger.getGlobal().severe(String.format("Error during file reading '%s':\n\nError:\n\n '%s'",phoneFilePath_,ex.toString()));
			fail();
		} finally {
			if (reader != null) {
				try {
					reader.close();	
				}catch(IOException e) {
						Logger.getGlobal().severe(String.format("Error during file reading '%s':\n\nError:\n\n '%s'",phoneFilePath_,e.toString()));	
				}
			}
		}
	}
}
