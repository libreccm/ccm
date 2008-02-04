/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpServletDummyResponse implements HttpServletResponse {

    public static final String versionId = "$Id: HttpServletDummyResponse.java 747 2005-09-02 11:02:24Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private PrintStream m_out;
    private boolean     m_committed;
    private TestServletContainer m_container;
   private Cookie m_cookie;

    public HttpServletDummyResponse() {
        m_out = System.out;
        m_committed = false;
    }

    public HttpServletDummyResponse(PrintStream out) {
        m_out=out;
        m_committed = false;
    }

    void setContainer(TestServletContainer container) {
        m_container = container;
    }


    public void addCookie(javax.servlet.http.Cookie cookie) {
      m_cookie = cookie;
    }

    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String encodeRedirectURL(String url) {
        return url;
    }

    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    public String encodeURL(String url){
        return url;
    }

    public String encodeUrl(String url){
        return encodeURL(url);
    }

    public void sendError(int sc) throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void sendError(int sc, String msg) throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void sendRedirect(String location) throws java.io.IOException{
        m_container.sendRedirect(location);
    }

    public void setDateHeader(String name, long date){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setHeader(String name, String value){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setIntHeader(String name, int value){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setStatus(int sc){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setStatus(int sc, String sm){
        throw new UnsupportedOperationException("Method not implemented");
    }


    /* Methods from SevletResponse */
    public void flushBuffer() throws java.io.IOException{
        m_committed = true;
        m_out.flush();
    }

    public void resetBuffer() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int getBufferSize(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String getCharacterEncoding(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public java.util.Locale getLocale(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public javax.servlet.ServletOutputStream getOutputStream() throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public PrintWriter getWriter() throws java.io.IOException {
        return new PrintWriter(m_out);
    }

    public boolean isCommitted() {
        return m_committed;
    }

    public void reset() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setBufferSize(int size) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setContentLength(int len) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setContentType(String type) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setLocale(java.util.Locale loc) {
        throw new UnsupportedOperationException("Method not implemented");
    }

}
