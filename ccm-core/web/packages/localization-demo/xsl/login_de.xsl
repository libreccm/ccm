<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:subsite="http://www.arsdigita.com/subsite/1.0"
                exclude-result-prefixes="subsite">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>

<!-- this rule matches one user -->
<xsl:template match="subsite:userInfo">
<p><font color="red">subsite:userInfo -&gt; START GERMAN TRANSLATION</font></p>

<hr />

<h3>What we tell other users about you</h3>

In general we identify content that you've posted by your full name.
In an attempt to protect you from unsolicited bulk email (spam), we
keep your email address hidden except from other registered users.
Total privacy is technically feasible but an important element of an
online community is that people can learn from each other.  So we try
to make it possible for users with common interests to contact each
other.

<h4>Basic Information</h4>

<ul>
  <li> Name: <xsl:value-of select="@name"/> </li>
  <li> User ID:  <xsl:value-of select="@id"/> </li>
  <li> email address:  <xsl:value-of select="@email"/> </li>
  <li> personal URL:  <xsl:value-of select="@URI"/> </li>
  <li> screen name:  <xsl:value-of select="@screenName"/> </li>
</ul>

<p><font color="red">subsite:userInfo -&gt; END GERMAN TRANSLATION</font></p>
</xsl:template>

<!-- this rule displays information about peristent cookies -->
<xsl:template match="subsite:explainPersistentCookies">
<p><font color="red">subsite:explainPersistentCookies -&gt; START GERMAN TRANSLATION</font></p>
<hr />

Our server can tell your browser to remember certain things, such as
your email address and password.  This is convenient for you because,
if you're the only person who uses your computer, you won't have to
keep telling us your email address and password.

<p>

It would be a very bad idea to choose this option if you're using a
shared computer in a library or school.  Any subsequent user of this
machine would be able to masquerade as you on our service.

</p>
<p>

Note that you can erase your saved email address and password by
choosing the "log out" option from your workspace.

</p>

<p><font color="red">subsite:explainPersistentCookies -&gt; END GERMAN TRANSLATION</font></p>
</xsl:template>

<!-- this rule displays a message saying the user typed in the wrong
     password when logging in -->
<xsl:template match="subsite:badPassword">
<p><font color="red">subsite:badPassword -&gt; START GERMAN TRANSLATION</font></p>

in <xsl:value-of select="@systemHome"/>

<hr />

The password you typed doesn't match what we have in the database.  If
you think you made a typo, please back up using your browser and try
again. 

<xsl:if test="@offerToEmailPassword='true'">
  <p>If you've forgotten your password, you can
  <a href="email-password?user_id={@userId}">ask this server to reset
  your password and email a new randomly generated password to you</a>
  </p>
</xsl:if>
        
<p><font color="red">subsite:badPassword -&gt; END GERMAN TRANSLATION</font></p>
</xsl:template>

<xsl:template match="subsite:loginPromptMsg">
<p><font color="red">subsite:loginPromptMsg -&gt; START GERMAN TRANSLATION</font></p>

<p><b>Current users:</b> Please enter your email and  
password below.</p> 
<p><b>New users:</b>  Welcome to ACS Developer Central.  
Please begin the registration process by entering a  
valid email address and a password for signing into  
the system.  We will direct you to another  
form to complete your registration.</p>

<p><font color="red">subsite:loginPromptMsg -&gt; END GERMAN TRANSLATION</font></p>
</xsl:template>

<xsl:template match="subsite:promptToEnableCookiesMsg">
<p><font color="red">subsite:promptToEnableCookiesMsg -&gt; START GERMAN TRANSLATION</font></p>
<p>If you keep getting thrown back here, it is probably because  
browser does not accept cookies. We're sorry for the  
inconvenience but it really is impossible to program a system  
like this without keeping track of who is posting what.</p> 
In Netscape 4.0, you can enable cookies from  
Edit -&gt; Preferences -&gt; Advanced.  
In Microsoft Internet Explorer 4.0, you can enable cookies from  
View -&gt; Internet Options -&gt; Advanced -&gt; Security.
<p><font color="red">subsite:promptToEnableCookiesMsg -&gt; END GERMAN TRANSLATION</font></p>
</xsl:template>
</xsl:stylesheet>
