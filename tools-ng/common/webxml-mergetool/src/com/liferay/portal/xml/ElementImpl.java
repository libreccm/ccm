/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.xml;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.CDATA;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Entity;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.Text;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class ElementImpl extends BranchImpl implements Element {

	public ElementImpl(org.dom4j.Element element) {
		super(element);

		_element = element;
	}

	public void add(Attribute attribute) {
		AttributeImpl attributeImpl = (AttributeImpl)attribute;

		_element.add(attributeImpl.getWrappedAttribute());
	}

	public void add(CDATA cdata) {
		CDATAImpl cdataImpl = (CDATAImpl)cdata;

		_element.add(cdataImpl.getWrappedCDATA());
	}

	public void add(Entity entity) {
		EntityImpl entityImpl = (EntityImpl)entity;

		_element.add(entityImpl.getWrappedEntity());
	}

	public void add(Namespace namespace) {
		NamespaceImpl namespaceImpl = (NamespaceImpl)namespace;

		_element.add(namespaceImpl.getWrappedNamespace());
	}

	public void add(Text text) {
		TextImpl textImpl = (TextImpl)text;

		_element.add(textImpl.getWrappedText());
	}

	public Element addAttribute(QName qName, String value) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new ElementImpl(
			_element.addAttribute(qNameImpl.getWrappedQName(), value));
	}

	public Element addAttribute(String name, String value) {
		return new ElementImpl(_element.addAttribute(name, value));
	}

	public Element addCDATA(String cdata) {
		cdata = StringUtil.replace(cdata, "]]>", "]]]]><![CDATA[>");

		return new ElementImpl(_element.addCDATA(cdata));
	}

	public Element addComment(String comment) {
		return new ElementImpl(_element.addComment(comment));
	}

	public Element addEntity(String name, String text) {
		return new ElementImpl(_element.addEntity(name, text));
	}

	public Element addNamespace(String prefix, String uri) {
		return new ElementImpl(_element.addNamespace(prefix, uri));
	}

	public Element addProcessingInstruction(
		String target, Map<String, String> data) {

		return new ElementImpl(_element.addProcessingInstruction(target, data));
	}

	public Element addProcessingInstruction(String target, String data) {
		return new ElementImpl(_element.addProcessingInstruction(target, data));
	}

	public Element addText(String text) {
		return new ElementImpl(_element.addText(text));
	}

	public List<Namespace> additionalNamespaces() {
		return SAXReaderImpl.toNewNamespaces(_element.additionalNamespaces());
	}

	public void appendAttributes(Element element) {
		ElementImpl elementImpl = (ElementImpl)element;

		_element.appendAttributes(elementImpl.getWrappedElement());
	}

	public Attribute attribute(int index) {
		org.dom4j.Attribute attribute = _element.attribute(index);

		if (attribute == null) {
			return null;
		}
		else {
			return new AttributeImpl(attribute);
		}
	}

	public Attribute attribute(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		org.dom4j.Attribute attribute = _element.attribute(
			qNameImpl.getWrappedQName());

		if (attribute == null) {
			return null;
		}
		else {
			return new AttributeImpl(attribute);
		}
	}

	public Attribute attribute(String name) {
		org.dom4j.Attribute attribute = _element.attribute(name);

		if (attribute == null) {
			return null;
		}
		else {
			return new AttributeImpl(attribute);
		}
	}

	public int attributeCount() {
		return _element.attributeCount();
	}

	public Iterator<Attribute> attributeIterator() {
		return attributes().iterator();
	}

	public String attributeValue(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.attributeValue(qNameImpl.getWrappedQName());
	}

	public String attributeValue(QName qName, String defaultValue) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.attributeValue(
			qNameImpl.getWrappedQName(), defaultValue);
	}

	public String attributeValue(String name) {
		return _element.attributeValue(name);
	}

	public String attributeValue(String name, String defaultValue) {
		return _element.attributeValue(name, defaultValue);
	}

	public List<Attribute> attributes() {
		return SAXReaderImpl.toNewAttributes(_element.attributes());
	}

	public Element createCopy() {
		return new ElementImpl(_element.createCopy());
	}

	public Element createCopy(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new ElementImpl(
			_element.createCopy(qNameImpl.getWrappedQName()));
	}

	public Element createCopy(String name) {
		return new ElementImpl(_element.createCopy(name));
	}

	public List<Namespace> declaredNamespaces() {
		return SAXReaderImpl.toNewNamespaces(_element.declaredNamespaces());
	}

	public Element element(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		org.dom4j.Element element = _element.element(
			qNameImpl.getWrappedQName());

		if (element == null) {
			return null;
		}
		else {
			return new ElementImpl(element);
		}
	}

	public Element element(String name) {
		org.dom4j.Element element = _element.element(name);

		if (element == null) {
			return null;
		}
		else {
			return new ElementImpl(element);
		}
	}

	public Iterator<Element> elementIterator() {
		return elements().iterator();
	}

	public Iterator<Element> elementIterator(QName qName) {
		return elements(qName).iterator();
	}

	public Iterator<Element> elementIterator(String name) {
		return elements(name).iterator();
	}

	public String elementText(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.elementText(qNameImpl.getWrappedQName());
	}

	public String elementText(String name) {
		return _element.elementText(name);
	}

	public String elementTextTrim(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.elementTextTrim(qNameImpl.getWrappedQName());
	}

	public String elementTextTrim(String name) {
		return _element.elementTextTrim(name);
	}

	public List<Element> elements() {
		return SAXReaderImpl.toNewElements(_element.elements());
	}

	public List<Element> elements(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return SAXReaderImpl.toNewElements(
			_element.elements(qNameImpl.getWrappedQName()));
	}

	public List<Element> elements(String name) {
		return SAXReaderImpl.toNewElements(_element.elements(name));
	}

	public boolean equals(Object obj) {
		org.dom4j.Element element = ((ElementImpl)obj).getWrappedElement();

		return _element.equals(element);
	}

	public Object getData() {
		return _element.getData();
	}

	public Namespace getNamespace() {
		org.dom4j.Namespace namespace = _element.getNamespace();

		if (namespace == null) {
			return null;
		}
		else {
			return new NamespaceImpl(namespace);
		}
	}

	public Namespace getNamespaceForPrefix(String prefix) {
		org.dom4j.Namespace namespace = _element.getNamespaceForPrefix(prefix);

		if (namespace == null) {
			return null;
		}
		else {
			return new NamespaceImpl(namespace);
		}
	}

	public Namespace getNamespaceForURI(String uri) {
		org.dom4j.Namespace namespace = _element.getNamespaceForURI(uri);

		if (namespace == null) {
			return null;
		}
		else {
			return new NamespaceImpl(namespace);
		}
	}

	public String getNamespacePrefix() {
		return _element.getNamespacePrefix();
	}

	public String getNamespaceURI() {
		return _element.getNamespaceURI();
	}

	public List<Namespace> getNamespacesForURI(String uri) {
		return SAXReaderImpl.toNewNamespaces(_element.getNamespacesForURI(uri));
	}

	public QName getQName() {
		org.dom4j.QName qName = _element.getQName();

		if (qName == null) {
			return null;
		}
		else {
			return new QNameImpl(qName);
		}
	}

	public QName getQName(String qualifiedName) {
		org.dom4j.QName qName = _element.getQName(qualifiedName);

		if (qName == null) {
			return null;
		}
		else {
			return new QNameImpl(qName);
		}
	}

	public String getQualifiedName() {
		return _element.getQualifiedName();
	}

	public String getTextTrim() {
		return _element.getTextTrim();
	}

	public org.dom4j.Element getWrappedElement() {
		return _element;
	}

	public Node getXPathResult(int index) {
		org.dom4j.Node node = _element.getXPathResult(index);

		if (node == null) {
			return null;
		}
		else {
			return new NodeImpl(node);
		}
	}

	public int hashCode() {
		return _element.hashCode();
	}

	public boolean hasMixedContent() {
		return _element.hasMixedContent();
	}

	public boolean isRootElement() {
		return _element.isRootElement();
	}

	public boolean isTextOnly() {
		return _element.isTextOnly();
	}

	public boolean remove(Attribute attribute) {
		AttributeImpl attributeImpl = (AttributeImpl)attribute;

		return _element.remove(attributeImpl.getWrappedAttribute());
	}

	public boolean remove(CDATA cdata) {
		CDATAImpl cdataImpl = (CDATAImpl)cdata;

		return _element.remove(cdataImpl.getWrappedCDATA());
	}

	public boolean remove(Entity entity) {
		EntityImpl entityImpl = (EntityImpl)entity;

		return _element.remove(entityImpl.getWrappedEntity());
	}

	public boolean remove(Namespace namespace) {
		NamespaceImpl namespaceImpl = (NamespaceImpl)namespace;

		return _element.remove(namespaceImpl.getWrappedNamespace());
	}

	public boolean remove(Text text) {
		TextImpl textImpl = (TextImpl)text;

		return _element.remove(textImpl.getWrappedText());
	}

	public void setAttributes(List<Attribute> attributes) {
		_element.setAttributes(SAXReaderImpl.toOldAttributes(attributes));
	}

	public void setData(Object data) {
		_element.setData(data);
	}

	public void setQName(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		_element.setQName(qNameImpl.getWrappedQName());
	}

	public String toString() {
		return _element.toString();
	}

	private org.dom4j.Element _element;

}