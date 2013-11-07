/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

/**
 *
 * Content Items implementing this interface are be language invariant, if
 * isLanguageInvariant returns true
 * 
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public interface LanguageInvariantContentItem {
 
    public abstract boolean isLanguageInvariant();
}
