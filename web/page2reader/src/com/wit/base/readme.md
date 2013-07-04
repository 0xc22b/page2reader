# Webbase

Web base components

# Features

  * Home page
  * Sign up
  * Sign in/Sign out
  * Change username
  * Change password
  * Change email
  * Confirm email
  * Reset password

# Platform

  * Java appengine

# Dependencies

## External libraries

   * Closure templates: soy.jar

## Git submodules

   * JSON library: org.json

## Already included in source code

   * BCrypt.java

# Subclass com.wit.base.BaseServlet to override default values

## Required values

   * Web URL - getWebUrl()
   * Web name - getWebName()
   * From email - getFromEmail()
   * From name - getFromName()
   * Delete all user contents - deleteAllUserContents(User user)
   * Reset password email subject - getResetPasswordEmailSubject()
   * Reset password email message body - getResetPasswordEmailMsgBody()
   * Confirm email subject - getConfirmEmailSubject()
   * Confirm email message body - getConfirmEmailMsgBody()

## Paths to .soy files

   * Base page
   * Home page
   * Work page (Logged-in page)
   * About page
   * Terms page
   * Feedback page
   * Confirm email page
   * Reset password page
   * User page

## Paths to css renaming map files (Optional)

   * Home page
   * Work page (Logged-in page)
   * User page   

# web.xml

  ```xml
  <servlet>
    <servlet-name>Base</servlet-name>
    <servlet-class>[path to subclass of com.wit.base.BaseServlet]</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>Base</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>

  <servlet>
    <servlet-name>MailHandler</servlet-name>
    <servlet-class>com.wit.base.MailHandlerServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>MailHandler</servlet-name>
    <url-pattern>/_ah/mail/*</url-pattern>
  </servlet-mapping>
  <security-constraint>
    <web-resource-collection>
      <url-pattern>/_ah/mail/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>admin</role-name>
    </auth-constraint>
  </security-constraint>
  
  <servlet>
    <servlet-name>SystemServiceServlet</servlet-name>
    <servlet-class>com.google.api.server.spi.SystemServiceServlet</servlet-class>
    <init-param>
      <param-name>services</param-name>
      <param-value/>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>SystemServiceServlet</servlet-name>
    <url-pattern>/_ah/spi/*</url-pattern>
  </servlet-mapping>
  ```

# Data format

## POST request
   * Not logged in: method="methodName"&content="JSON Object"
   * Logged in: ssid="Session key string"&sid="session id"&method="methodName"&content="JSON Object"

## POST response
   * JSON array of log object

# License

The software stands under Apache 2 License and comes with NO WARRANTY
