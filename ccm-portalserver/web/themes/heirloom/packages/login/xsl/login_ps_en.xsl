<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:subsite="http://www.arsdigita.com/subsite/1.0"
                exclude-result-prefixes="subsite">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>

<xsl:template match="subsite:explainPersistentCookies">
<hr />

<p>Our server can tell your browser to remember certain things, such as your
email address and password.  This is convenient for you because, if you're
the only person who uses your computer, you won't have to keep telling us
your email address and password.</p>

<p>It would be a very bad idea to choose this option if you're using a
shared computer in a library or school.  Any subsequent user of this machine
would be able to masquerade as you on our service.</p>

<p>Note that you can erase your saved email address and password by choosing
the "log out" option from your workspace.</p>
</xsl:template>

<xsl:template match="subsite:loginPromptMsg">
<p><b>Current users:</b> Please enter your email and password below.</p>

<p><b>New users:</b> Please
begin the registration process by entering a valid email address and leaving
the password field blank.  We will direct you to another form to complete
your registration.</p>
</xsl:template>

<xsl:template match="subsite:promptToEnableCookiesMsg">
<p><i>If you keep getting thrown back here, it is probably because your
browser does not accept cookies.</i></p>
</xsl:template>

<xsl:template match="subsite:recoverPasswordMailSent">
<p>You have been sent an email that will allow you to change your
password.</p>
</xsl:template>

<xsl:template match="subsite:recoverPasswordMailFailed">
<p>Unable to send password recovery information via email.</p>
<p>Please contact the system administrator for further help in recovering your password.</p> 
</xsl:template>

<xsl:template match="subsite:explainLoginExpired">
<p></p>
<font size="-1">
<table cellpadding="5" cellspacing="1" border="1">
<tr>
<td bgcolor="#ffffcc"><b>Note:</b> For security reasons, the login page
expires after a certain interval to prevent malicious attackers from logging
in as you using cached browser passwords.</td>
</tr>
</table>
</font>
</xsl:template>

</xsl:stylesheet>
