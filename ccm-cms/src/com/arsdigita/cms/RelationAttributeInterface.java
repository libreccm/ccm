/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import java.util.StringTokenizer;

/**
 *
 * @author quasi
 */
public interface RelationAttributeInterface {

    public abstract boolean hasRelationAttributes();

    public abstract boolean hasRelationAttributeProperty(String propertyName);

    public abstract StringTokenizer getRelationAttributes();

    public abstract String getRelationAttributeName(String propertyName);

    public abstract String getRelationAttributeKeyName(String propertyName);

    public abstract String getRelationAttributeKey(String propertyName);
}
