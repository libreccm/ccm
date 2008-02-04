package com.arsdigita.templating.jsp;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

/**
 * Includes a JSP component in the current JSP.
 *
 * <p>Usage:</p>
 *
 * <pre><code>&lt;acs:include path="path/to/component.jsp" />
 **/
public class IncludeTag extends TagSupport {

  private String path = null;

  public IncludeTag() {

    super();
  }

  public void setPath(String path) {

    this.path = path;
  }

  public int doStartTag() throws JspTagException {

    try {

      pageContext.include(path);

    } catch (Exception e) {
      throw new JspTagException(e.getMessage());
    }

    return EVAL_PAGE;
  }

  public void release() {

    this.path = null;
  }
}
