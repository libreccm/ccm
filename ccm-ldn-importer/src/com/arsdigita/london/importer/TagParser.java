package com.arsdigita.london.importer;

import org.xml.sax.Attributes;


/**
 *  A parser which handles the XML element denoted by
 * {@link #getTagName()} method.
 * <p>
 *  When top-level import parser, {@link ParserDispatcher},
 * is parsing the XML source, upon encountering
 * new XML element, it makes lookup in a map containing registered
 * TagParser instances.  If there is a TagParser registered
 * to handle the met element, importer passes control to TagParser.
 * Otherwise an exception is thrown.
 *
 *  @see com.arsdigita.london.importer
 *  @see ParserDispatcher
 *  @see org.xml.sax.helpers.DefaultHandler
 */
public interface TagParser {

    /**
     *  Denotes an XML element name this parser can handle.
     */
    String getTagName();

    /**
     *  Provides support for XML namespaces.
     */
    String getTagURI();

    /**
     *  Executed when XML element is started.
     */
    void startElement(String name,
                      String uri,
                      Attributes atts);

    /**
     *  Executed (multiple times possibly) when processing the body of XML element.
     */
    void characters(char[] ch, int start, int length);

    /**
     *  Executed when XML element is ended.
     */
    void endElement(String name,
                    String uri);

    /**
     *  Executed when parser encounters XML element denoting start of a block.
     * A block is XML piece between opening and closing tag identified by
     * {@link #getTagName() getTagName} method.  Executed only once per
     * parser instance, unlike {@link #startElement(String,String,Attributes) startElement}
     * which can be executed multiple times.
     */
    void startBlock();

    /**
     *  Executed when this parser instance is about to finish its job
     * and hands off control, either to parent parser instance or
     * top-level handler.  Unlike {@link #endElement(String,String) endElement},
     * this method is being called only once per parser instance.
     */
    void endBlock();

    /**
     *  Executed just prior to handing off control to another parser instance
     * registered for handling the encountered subblock.  Gives parent
     * parser access to child instance before the child has done any work.
     *
     *  @param parser parser instance registered for handling the subblock encountered.
     */
    void startSubBlock(TagParser parser);

    /**
     *  Executed when a subblock, handled by another parser instance,
     * has ended and control has returned to this parser.
     * <p>
     *  This gives developer elegant access to parser instance which is processing
     * the subblock.  One of the common usage patterns is setting the folder attribute
     * of an imported content item.  It is performed within FolderParser
     * since there we have access to ItemParser instance, and not vice versa.
     *
     * @param parser parser instance that has been processing
     *               the subblock
     *
     * @see com.arsdigita.london.importer.cms.FolderParser
     * @see com.arsdigita.london.importer.cms.ItemParser
     */
    void endSubBlock(TagParser parser);

}



