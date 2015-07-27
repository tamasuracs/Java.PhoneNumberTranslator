package com.tauracs.phonenumbertranslator.model.nodes;

/**
 * Digit Translation tree node.
 * Created when no word found in the dictionary for the given phone number digit.
 */
public class DigitNode extends NodeBase {

	/** Constructor
	 * @param parent_ The parent (if null this is the root of the translation tree)
	 * @param digit_ The digit character to be represented in the tree
	 */
	public DigitNode(final NodeBase parent_, final Character digit_) {
		super(parent_);
		if (digit_ == null || !Character.isDigit(digit_)) {
			throw new IllegalArgumentException("Argument digit_ should be a digit character");
		}
		super._data = digit_.toString();
	}

}
