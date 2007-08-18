/*
 * $Id: PdfDocument.java 2884 2007-08-15 09:28:41Z blowagie $
 * Copyright (c) 2007 Bruno Lowagie
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lowagie.rups.nodetypes;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

/**
 * Every node in our tree corresponds with a PDF object.
 * This class is the superclass of all tree nodes used.
 */
public class PdfObjectTreeNode extends DefaultMutableTreeNode {

	/** a serial version UID. */
	private static final long serialVersionUID = -5617844659397445879L;

	/** the PDF object corresponding with this node. */
	protected PdfObject object;
	/** the key if the parent of this node is a dictionary. */
	protected PdfName key = null;
	/** if the object is indirect, the number of the PDF object. */
	protected int number = -1;
	/** indicates if the object is indirect and recursive. */
	protected boolean recursive = false;

	/**
	 * Creates a tree node for a PDF object.
	 * @param object	the PDF object represented by this tree node.
	 */
	protected PdfObjectTreeNode(PdfObject object) {
		super(getCaption(object));
		this.object = object;
	}

	/**
	 * Creates an instance of a tree node for a PDF object.
	 * @param object	the PDF object represented by this tree node.
	 * @return	a PdfObjectTreeNode
	 */
	public static PdfObjectTreeNode getInstance(PdfObject object) {
		if (object.isDictionary()) {
			if (PdfName.PAGE.equals(((PdfDictionary)object).get(PdfName.TYPE))) {
				return new PdfPageTreeNode((PdfDictionary)object);
			}
			else if (PdfName.PAGES.equals(((PdfDictionary)object).get(PdfName.TYPE))) {
				return new PdfPagesTreeNode((PdfDictionary)object);
			}
		}
		return new PdfObjectTreeNode(object);
	}
	
	/**
	 * Creates an instance of a tree node for an indirect object.
	 * @param object	the PDF object represented by this tree node.
	 * @param object	the xref number of the indirect object
	 * @return	a PdfObjectTreeNode
	 */
	public static PdfObjectTreeNode getInstance(PdfObject object, int number) {
		PdfObjectTreeNode node = getInstance(object);
		node.number = number;
		return node;
	}

	/**
	 * Creates an instance of a tree node for the object corresponding with a key in a dictionary.
	 * @param dict	the dictionary that is the parent of this tree node.
	 * @param key	the dictionary key corresponding with the PDF object in this tree node.
	 * @return	a PdfObjectTreeNode
	 */
	public static PdfObjectTreeNode getInstance(PdfDictionary dict, PdfName key) {
		PdfObjectTreeNode node = getInstance(dict.get(key));
		node.setUserObject(getDictionaryEntryCaption(dict, key));
		node.key = key;
		return node;
	}
	
	/**
	 * Getter for the PDF Object.
	 * @return	the PDF object represented by this tree node.
	 */
	public PdfObject getPdfObject() {
		return object;
	}
	
	/**
	 * Checks if this node is a dictionary item with a specific key.
	 * @param	key	the key of the node we're looking for
	 */
	public boolean isDictionaryNode(PdfName key) {
		if (key == null) return false;
		return key.equals(this.key);
	}
	
	/**
	 * Tells you if the node contains an indirect reference.
	 * @return	true if the object is an indirect reference
	 */
	public boolean isIndirectReference() {
		return object.type() == PdfObject.INDIRECT;
	}
	
	/**
	 * Tells you if the object is indirect.
	 * @return	true for indirect objects; false for direct objects.
	 */
	public boolean isIndirect() {
		return isIndirectReference() || number > -1;
	}

	/**
	 * Set this to true if the object is a reference to a node higher up in the tree.
	 * @param	recursive	true if the object is indirect and recursive
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	/**
	 * Tells you if the object is a reference to a node higher up in the tree.
	 * @return	true if the node is used recursively.
	 */
	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Getter for the object number in case the object is indirect.
	 * @return	-1 for direct objects; the object number for indirect objects
	 */
	public int getNumber() {
		if (isIndirectReference()) {
			return ((PdfIndirectReference)object).getNumber();
		}
		return number;
	}

    /**
     * Getter for the icon that is to be used for this tree node.
     * @return	the icon corresponding with the object
     */
    public Icon getIcon() {
    	switch(object.type()) {
    	case PdfObject.INDIRECT:
    		if (isRecursive())
    			return new ImageIcon(PdfObjectTreeNode.class
        				.getResource("icons/ref_recursive.png"));
    		else
    			return new ImageIcon(PdfObjectTreeNode.class
        				.getResource("icons/ref.png"));
    	case PdfObject.ARRAY:
    		return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/array.png"));
    	case PdfObject.DICTIONARY:
    		return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/dictionary.png"));
		case PdfObject.STREAM:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/stream.png"));
		case PdfObject.BOOLEAN:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/boolean.png"));
		case PdfObject.NAME:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/name.png"));
		case PdfObject.NULL:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/null.png"));
		case PdfObject.NUMBER:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/number.png"));
		case PdfObject.STRING:
    			return new ImageIcon(PdfObjectTreeNode.class
    				.getResource("icons/string.png"));
    	default:
    		return null;
    	}
	}
	
	/**
	 * Creates the caption for a PDF object.
	 * @param object	the object for which a caption has to be created.
	 * @return	a caption for a PDF object
	 */
	public static String getCaption(PdfObject object) {
		if (object == null)
			return "null";
		switch (object.type()) {
		case PdfObject.INDIRECT:
			return "Indirect reference: " + object.toString();
		case PdfObject.ARRAY:
			return "Array";
		case PdfObject.STREAM:
			return "Stream";
		}
		return object.toString();
	}

	/**
	 * Creates the caption for an object that is a dictionary entry.
	 * @param dict	a dictionary
	 * @param key	a key in the dictionary
	 * @return	a caption for the object corresponding with the key in the dictionary.
	 */
	public static String getDictionaryEntryCaption(PdfDictionary dict, PdfName key) {
		StringBuffer buf = new StringBuffer(key.toString());
		buf.append(": ");
		buf.append(dict.get(key).toString());
		return buf.toString();
	}

	/**
	 * Gets the tree path of an ancestor.
	 * This only works with recursive references
	 * @return	the treepath to an ancestor
	 */
	public PdfObjectTreeNode getAncestor() {
		if (isRecursive()) {
			PdfObjectTreeNode node = this;
			while(true) {
				node = (PdfObjectTreeNode)node.getParent();
				if (node.isIndirectReference() && node.getNumber() == getNumber()) {
					return node;
				}
			}
		}
		return null;
	}
}