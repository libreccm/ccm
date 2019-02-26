/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.theming;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlEnum
public enum ContentItemViews {

    @XmlEnumValue(value = "detail")
    DETAIL,
    @XmlEnumValue(value = "greetingItem")
    GREETING_ITEM,
    @XmlEnumValue(value = "list")
    LIST,
    @XmlEnumValue(value = "portletItemf")
    PORTLET_ITEM,
}
