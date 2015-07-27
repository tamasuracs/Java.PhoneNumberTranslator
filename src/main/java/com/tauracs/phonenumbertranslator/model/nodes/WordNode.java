package com.tauracs.phonenumbertranslator.model.nodes;

/**
 * Word Translation tree node.
 * Created when a word was found in the dictionary for the given phone number digit.
 *
 */
public class WordNode extends NodeBase {

	/**
	 * Constructor
	 * @param parent_ The parent (if null this is the root of the translation tree) 
	 * @param word_ The word  to represented in the tree
	 */
	public WordNode(final NodeBase parent_, final String word_) {
		super(parent_);
		
		if (word_ == null) {
			throw new IllegalArgumentException("Argument should be not null");
		}
		
		_data = word_;
	}
}
