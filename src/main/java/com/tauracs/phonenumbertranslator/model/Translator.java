package com.tauracs.phonenumbertranslator.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.tauracs.phonenumbertranslator.model.nodes.DigitNode;
import com.tauracs.phonenumbertranslator.model.nodes.NodeBase;
import com.tauracs.phonenumbertranslator.model.nodes.WordNode;

/**
 * Class representing a translator that converts phone number
 *
 */
public final class Translator {

	/**
	 * Constant RADIX used for digit validation
	 */
	private static final int DECIMAL_RADIX = 10;

	/**
	 * Encoding specified in the task (only the lower cased chars)
	 */
	private static String[] ENCODING_MAP_SMALL_CASE = new String[] {
														// Item - 0
														"e",
														// Item - 1
														"jnq",
														// Item - 2
														"rwx",
														// Item - 3
														"dsy",
														// Item - 4
														"ft",
														// Item - 5
														"am",
														// Item - 6
														"civ",
														// Item - 7
														"bku",
														// Item - 8
														"lop",
														// Item - 9
														"ghz"
	                                                };

	/**
	 * Hashmap containing (initial character of word --> list of words) from the dictionary entries.
	 * Data retrieval from HashMap is very fast based on hashCode of the key.
	 */
	private HashMap<Character, List<String>> _indexedDictionary = new HashMap<Character, List<String>>();

	/**
	 * The phone number that is translated
	 */
	private String _phoneNumber = null;
	
	/**
	 * Private field which is true when the translation was successful
	 */
	private boolean _valid = false;

	/**
	 * Set storing references to each leaf entry of the
	 * translation-word-tree
	 */
	private Set<NodeBase> _translations = new LinkedHashSet<NodeBase>();

	/**
	 * Populates the translation dictionary based
	 * on the passed string array
	 * @param array_ Array containing each line of the dictionary
	 */
	private void populateDictionary(final String[] array_) {
		_indexedDictionary = new HashMap<Character, List<String>>();
		if (array_ != null) {
			for (int i = 0; i < array_.length; i++) {
				String dict_entry = array_[i];
				addEntryToIndexedDict(dict_entry);
			}
		}
	}
	
	/**
	 * Populates the translation dictionary based
	 * on the file defined by path
	 * @param path_ Path of the dictionary file
	 */
	private void populateDictionary(final String path_) throws IOException {			
			BufferedReader reader = null;
			try {
				String sCurrentLine = null;
				reader = new BufferedReader(new FileReader(path_));
				
				while((sCurrentLine = reader.readLine()) != null) {
					addEntryToIndexedDict(sCurrentLine);
				}
				
			} catch(IOException ex) {
				Logger.getGlobal().severe(String.format("Error during file reading '%s':\n\nError:\n\n '%s'",path_,ex.toString()));
				throw ex;
			} finally {
				if (reader != null) {
					try {
						reader.close();	
					}catch(IOException ex) {
							Logger.getGlobal().severe(String.format("Error during file reading '%s':\n\nError:\n\n '%s'",path_,ex.toString()));
							throw ex;
					}
				}
			}
	}
	
	/**
	 * Adds a new dictionary entry to the indexed dict.
	 * @param newDictEntry_ - the new entry to add
	 */
	private void addEntryToIndexedDict(final String newDictEntry_) {
		
		if (newDictEntry_ == null || newDictEntry_.length() == 0) {
			return;
		}
		char key = newDictEntry_.toLowerCase().charAt(0);
		List<String> entries = _indexedDictionary.get(key);
		if (entries == null) {
			entries = new LinkedList<String>();
		}
		entries.add(newDictEntry_);
		_indexedDictionary.put(key, entries);
	}

	/**
	 * Constructor 
	 * 
	 * @param dictionary_ The dictionary for the encoding
	 */
	public Translator(final String[] dictionary_) {
		populateDictionary(dictionary_);
	}
	
	/**
	 * Constructor 
	 * 
	 * @param path_ The path of the dictionary file
	 */
	public Translator(final String path_) throws IOException {
		populateDictionary(path_);
	}

	/**
	 * Returns each translation for the phone number
	 * @return returns the result
	 */
	public String[] getResults() {

		String[] result = new String[_translations.size()];
		int i = 0;
		for (NodeBase node : _translations) {
			result[i] = node.toString();
			i++;
		}
		return result;
	}

	/**
	 * Standard string conversion method - takes each translation
	 * @return string representation
	 */
	@Override
	public String toString() {

		String result = "";
		for (String translation : getResults()) {
				result = String.format("%s\n%s: %s", result, _phoneNumber, translation);
		}
		return result;
	}
	/**
	 * Tries to translate the passed phone number
	 * 
	 * @param phoneNumber_ The number to be encoded, it can contain non-digit characters
	 * @return true if the translation was successful - results will be available in the getResults() method
	 */
	public boolean tryTranslate(final String phoneNumber_) {
		_phoneNumber = phoneNumber_;
		_translations = new LinkedHashSet<NodeBase>();
		
		Logger.getGlobal().info(String.format("Processing phone number '%s'", phoneNumber_));
		/** 
		 * "A phone number is an arbitrary(!) string of dashes - , slashes / and digits. The dashes and slashes will not be encoded." 
		 */
		_valid = tryTranslate(phoneNumber_.replace("-", "").replace("/", ""), null);
		return _valid;
	}

	/**
	 * Tries to translate the passed phone number 
	 * 
	 * @param phoneNumber_ The number to be encoded, should consist of only digit characters
	 * @param node_ Last node of the translation word tree
	 * @return true if the translation was successful - results will be available in the getResults() method
	 */
	private boolean tryTranslate(final String phoneNumber_, final NodeBase node_) {		
		boolean result = false;

		if (phoneNumber_.length() == 0) {
			if (node_ == null) {
				return false;
			}
			else {
				_translations.add(node_);
				
				Logger.getGlobal().info(String.format("Translation found: '%s'", node_.toString()));
				return true;
			}
		}

		//Taking the first character of the phone number
		char char_in_number = phoneNumber_.charAt(0);

		//Converting to int
		int digit =  Character.digit(char_in_number, DECIMAL_RADIX);

		if (digit >= 0 && digit <= DECIMAL_RADIX - 1) {
			String encoderLine = ENCODING_MAP_SMALL_CASE[digit];

			//Iterating through the characters of the encoder line
			for (int i = 0; i < encoderLine.length(); i++) {
				char encodedChar = encoderLine.charAt(i);

				//Taking the words from the dictionary
				//for the current encoder char
				List<String> words = _indexedDictionary.get(encodedChar);
				if (words != null) {
					for (String word : words) {

						boolean iterationResult = tryTranslate(phoneNumber_, word, node_);

						if (iterationResult) {
							result = true;
						}
					}
				}
			}
			
			//There was no word translation found 
			if (!result) {
			    //If this is the first translation node in the translation word tree				
				//or the previous node is non-digit 
				if (node_ == null || !node_.getClass().equals(DigitNode.class)) {
					NodeBase digitNode = new DigitNode(node_, char_in_number);
					
					result = tryTranslate(phoneNumber_.substring(1), digitNode);
				}
			}
		}
		return result;
	}
	
	/**
	 * Tries to translate the passed digit to the passed word.
	 * 
	 * @param phoneNumber_ The number to be encoded, should consist of only digit characters 
	 * @param encoderWord_ The candidate word
	 * @param node_ Last node of the translation word tree
	 * @return true if the phone number can be translated to the given word
	 */
	private boolean tryTranslate(final String phoneNumber_, final String encoderWord_, final NodeBase node_) {
		boolean result = false;
		
		if (phoneNumber_.length() == 0) {
			if (node_ == null) {
				return false;
			}
			else {
				_translations.add(node_);
				return true;
			}
		}
		//Eliminate invalid characters of the encoder word
		String normalizedEncoderWord = normalize(encoderWord_);		
		if (normalizedEncoderWord.length() > phoneNumber_.length()) {
			return false;
		}

		//Matching each each digit of the phone number against the chars of the word
		for (int i = 0; i < phoneNumber_.length(); i++) {	
			char charInNumber = phoneNumber_.charAt(i);				
			char charInWord = normalizedEncoderWord.charAt(i);				
			
			if (Character.isDigit(charInNumber)) {	
				
				if (!tryTranslate(charInNumber, charInWord)) {
					return false;
				}
			}

			//When the last character of a word has been reached and is valid
			if (i + 1 == normalizedEncoderWord.length()) {
				//New word node to be added to the translation word tree
				NodeBase newNode = new WordNode(node_, encoderWord_);
				return tryTranslate(phoneNumber_.substring(i + 1), newNode);
			}
		}
		return result;
	}

	/**
	 * Tries to translate the passed digit to the candidate.
	 *
	 * @param digitChar_ Digit character that is translated
	 * @param encodedCharCandidate_ the invoker expects that digit can be converted to this character based on the mapping
	 * @return true if digitChar_ can be translated to encodedCharCandidate_
	 */
	private static boolean tryTranslate(final char digitChar_, final char encodedCharCandidate_) {
		boolean result = false;
		int digit = Character.digit(digitChar_, DECIMAL_RADIX);
		if (digit < 0 || digit > DECIMAL_RADIX - 1) {
			throw new IllegalArgumentException("digitChar_ should be a number btw 0 and 9");
		}

		String encoderLine = ENCODING_MAP_SMALL_CASE[digit];

		for (int i = 0; i < encoderLine.length(); i++) {
			char encoderChar = encoderLine.charAt(i);

			if (Character.toLowerCase(encodedCharCandidate_) == encoderChar) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Function that eliminates non letter characters from the passed word.
	 * @param word_ Input that will be normalized
	 * @return Word that contains only letter characters.
	 */
	public static String normalize(final String word_) {
		String result = "";
		for (int i = 0; i < word_.length(); i++) {
			char c = word_.charAt(i);
			if (Character.isLetter(c)) {
				result = String.format("%s%s", result, c);
			}
		}
		return result;
	}

}

