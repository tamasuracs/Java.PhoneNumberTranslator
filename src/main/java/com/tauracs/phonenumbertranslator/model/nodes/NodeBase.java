package com.tauracs.phonenumbertranslator.model.nodes;

/**
 * Translation result tree node. 
 * 
 * Encodings of a phone number are represent as trees during the translation process.
 * This immutable class represents one node of the tree.
 *
 */
public abstract class NodeBase {

	/**
	 * The parent node of the current one.
	 * If null this element is a root
	 */
	private NodeBase _parent = null;

	/**
	 * String stored in the current node.
	 * Either a digit or a word from the directory
	 */
	protected String _data = null;

	/**
	 * Constructor of an element
	 * @param parent_ the parent node of the current entry
	 */
	protected NodeBase(final NodeBase parent_) {		
		_parent = parent_;
	}
	
	/**
	 * String conversion of the current node.	  
	 * Crawls up on each node until the root of the tree is found.
	 *  @return Returns the string
	 */
	@Override
	public final String toString() {
		String result = this._data;
		NodeBase parent = _parent;
		while (parent != null) {
			result = String.format("%s %s", parent._data, result);
			parent = parent._parent;
		}
		return result;
	}
}
