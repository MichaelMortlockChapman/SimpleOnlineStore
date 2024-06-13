# SimpleOnlineStore v1.0.0

This project was built to develop and showcase my skills with the Spring framework. The web service created utilises Spring Boot & a PostgreSQL database to comprise the RESTful API backend of a fictional online store. The service provides logic for authentication, authorization, customer details, product information, and ordering. Additionally, the project includes a comprehensive suite of unit tests to ensure reliability and performance.

The project PostgreSQL database includes 5 tables (scheme outlined in ```Database\schema.pgsql```). The tables consist of orders, products, users, customers, and sessions. These tables/ the database are integrated via JPA/Hibernate to effectively store and query necessary information. To facilitate session management the service utilises cookies. During development, experimentation was done with JWT tokens however they were found to be ill fitting for the situation. In a similar vein, refresh tokens were investigated however I found them to be out-of-scope for this project.

Finally, authentication and authorization are implemented through 3 sperate security filter chains with 3 custom request filters. The first security chain safeguards ‘open’ routes (routes anyone can access). These routes include `/v1/auth/signup/**`, `/v1/auth/signout`, and `/v1/product/get/**`. The second security chain exclusively safeguards the sign in route `/v1/auth/signin`, utilising the custom filters `LoginAuthenticationFilter` and `ExceptionHandlerFilter` for initial authentication and error handling respectively. The last security chain safeguards any other route. This chain uses the custom filters `CookieAuthenticationFilter` and `ExceptionHandlerFilter` to authenticate and authorize the current user session. Additionally, the service utilizes the default web security protocol protections provide by Spring Security, such as the `CorsFilter`.

The web service has been deployed through render.com at the url https://simpleonlinestore.onrender.com. However, currently no frontend exists for this fictional online store however curl commands can get used to communicate and experiment with the service.

#### Example command:
```
curl -XPOST -H "Content-type: application/json" -d '{ "login": "hello@world.com", "password": "password", "name": "Jim", "address": "1 One Street", "city": "Big Smoke", "postalCode": "1999", "country": "USA" }' 'https://simpleonlinestore.onrender.com/v1/auth/signup/customer'
```
If the server is unresponsive, it is likely the database is down. The service uses the free render tier, so it only has a month of uptime. Please send me a message if this happens and I will bring it back online.

## Code Dev Requirements
- Java Version 22
- Maven  (3.9.6*)

## Lifecycle Scripts
Script names self explanatory, both need to run at `Backend\simpleonlinestore`
- `Backend\simpleonlinestore\build.bash`
  - Jar is placed in `Backend\simpleonlinestore\target\simpleonlinestore-[version].jar`
- `Backend\simpleonlinestore\test.bash`

## Implementation Aspects 
<table>
  <tr>
    <td width="50%">Strengths</td>
    <td width="50%">Weaknesses</td>
  </tr>
  <tr>
    <td valign=top >
      <ul>
        <li>Cookies/Tokens have an inbuilt age similar to JWT</li>
        <li>The service uses environmental variables for important secrets. So, a bad actor would need to gain access to the server environment to compromise security (this repo has ".env" public for testing/showcase purposes, however production env vars would never be pushed to main/a repo)</li>
        <li>Passwords are salted and hashed using Spring’s inbuilt cryptographic password encoder before being stored</li>
        <li>The security filter chains/ custom request filters allow easy scalability and performance for site security.</li>
        <li>The service has a suite of unit tests to ensure reliability and performance. Additionally, the API implementation has full test coverage.</li>
        <li>The API implementation has full Javadoc documentation and extended error messaging </li>
      </ul>
    </td>
    <td valign=top >
      <ul>
        <li>The token generator is a random UUID which is a strong token generation method</li>
        <li>Some routes are too simplistic. For example, the order creation routes assume all orders are a single product but in a real-world scenario this is highly unlikely </li>
        <li>At this moment, admins can only be created in the database no admin creation route is present</li>
        <li>There are only two user types, customers and admins, this could be extended. For example, delivery drivers could be a user type which has access to orders selected by an admin</li>
        <li>The system has no email authentication so any email can be used to sign up. However, this was not implemented as I believe it was out-of-scope for the project</li>
      </ul>
    </td>
  </tr>
</table>

#### Some Assumptions:
-	Orders should never be deleted for logging/audit requirements
-	Emails/logins are unique (standard)
-	A user would rather sign in and use a cookie for their session than use their credentials for every API call (standard)


## Routes
```
  /v1
  |-- /auth
  |   |-- /sigin
  |   |-- /signup/customer
  |   |-- /user/update
  |   |-- /delete
  |   `-- /signout
  |       `-- /all
  |-- /product
  |   |-- /get
  |   |   |-- /all
  |   |   `-- /{productId}
  |   |-- /update
  |   |-- /add
  |   `-- /delete
  |-- /customer
  |   |-- /details
  |   `-- /update
  |-- /order
  |   |-- /create
  |   |   `-- /simple
  |   |-- /admin
  |   |   |-- /create
  |   |   `-- /update
  |   |       `-- /status
  |   |-- /update
  |   `-- /get
  |       |-- /active
  |       |-- /all
  |       `-- /{orderId}
  `-- /hello
      |-- /user
      `-- /admin
```

## Route Info
#### Term definitions
 - Login – synonymous with email
 - ROLE_USER – customer user
 - ROLE_ADMIN – admin user
<br><br>

Following table converted from [Route Info.docx](<Route Info.pdf>)
<table cellspacing=0 cellpadding=0>
 <tr>
  <td width=212 style='width:158.65pt;border:solid windowtext 1.0pt;padding:
  0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Route</b></p>
  </td>
  <td width=160 style='width:120.05pt;border:solid windowtext 1.0pt;border-left:
  none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Authorization</b></p>
  </td>
  <td width=177 style='width:132.4pt;border:solid windowtext 1.0pt;border-left:
  none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Request</b></p>
  </td>
  <td width=169 style='width:126.95pt;border:solid windowtext 1.0pt;border-left:
  none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Response</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border:solid windowtext 1.0pt;
  border-left:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Description</b></p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/signup/customer</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>N/A</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; login:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; password:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country:
  String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  201</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;User
  signed up&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Login already in use)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Login
  already in use&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Invalid login/email)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Invalid
  email&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (password length &lt; 8)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Password
  less than 8 characters&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Creates a new
  customer and sends back a new session cookie back.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/signin</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>N/A</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; login:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; password:
  String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Missing login details)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Missing
  login details&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Malformed auth header)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Bad
  Auth Header&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Malformed auth header)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Bad
  Auth Header&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Email/password wrong)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Invalid
  login&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Signs user in
  sending a new session cookie back.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/user/update</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; login:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; password:
  String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Invalidating
  Session Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Login already in use)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Login already
  in use&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (Invalid login/email)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Invalid
  email&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (password length &lt; 8)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Password
  less than 8 characters&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows a user
  to update their login credentials. If the user updates their password all
  previous sessions are invalidated, and user must sign in again.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/delete</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allow user to
  delete their account.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/signout</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows user
  to end current session cookie.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/auth/signout/all</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows user
  to end all sessions.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/customer/details</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country:
  String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows
  customer to view their account details.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/customer/update</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country:
  String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows
  customer to update their account details</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/product/get/all</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>N/A</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>[</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; {</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; productId:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; description:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; units: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; price:
  Long</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; }</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>]</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Packages all
  product info as a JSON list.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/product/get/{productId}</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Anyone</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Path
  Variable:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>productid:
  Integer</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; description:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; units:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; price: Long</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot; Unknown
  ProductId&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Packages
  selected product info as a JSON object.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/product/add</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; description:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; units:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; price: Long</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productid:
  Integer</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows admins
  to create new products.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/product/update</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; id:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; name:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; description:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; units:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; price: Long</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows admins
  to update a product&emsp;s info.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/product/delete</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Integer
  (productId)</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot; Unknown
  ProductId&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows admins
  to delete products.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/create/simple</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productid:
  Integer</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot; Unknown
  ProductId&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Creates a new
  order with the customer&emsp;s location details as the order&emsp;s destination.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/create</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productid:
  Integer</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot; Unknown
  ProductId&quot;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Creates a new
  order.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/admin/create</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; login:
  String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productid:
  Integer</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot; Unknown
  ProductId&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (associated login is not a customer/ login does not exist)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;Bad request&emsp;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows admins
  to create an order for selected customer.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/admin/update/status</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;
  orderStatus: OrderStatuses.statuses (String)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Updates the
  status of an order.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/update</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Bad
  OrderId&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (order has already begun or been cancelled)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Order
  underway or cancelled&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows a
  customer to change their order, but only if the order has not already begun
  or been cancelled. </p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/admin/update</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;
  customerLogin: String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Done&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (product with Id unknown)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Bad
  OrderId&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  400 (associated login is not a customer/ login does not exist)</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;Bad customer
  login&emsp;</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Allows admins
  to update order&emsp;s details but not its status as this is done by /v1/order/admin/update/status.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/get/active</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>[</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; {</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp;
  customerLogin: String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; productId:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; quantity:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; }</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>]</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Packages the
  customer&emsp;s active orders as a JSON list.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/get/all</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>[</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; {</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp;
  customerLogin: String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; productId:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; quantity:
  Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; city:
  String, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;&emsp;&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; }</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>]</p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Packages the
  all customer&emsp;s orders as a JSON list.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/order/get/{orderId}</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Path
  Variable:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>productid:
  String</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>{</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; orderId:
  UUID,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp;
  customerLogin: String,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; productId: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; quantity: Integer,</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; address: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; city: String,
  </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; postalCode:
  Integer, </p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&emsp; country: String</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>}</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Packages the
  customer&emsp;s select order as a JSON object.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/hello</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER, ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Hello,
  World!&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Testing
  route.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/hello/user</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_USER</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Hello,
  admin!&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Testing
  route.</p>
  </td>
 </tr>
 <tr>
  <td width=212 valign=top style='width:158.65pt;border:solid windowtext 1.0pt;
  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>/v1/hello/admin</p>
  </td>
  <td width=160 valign=top style='width:120.05pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>ROLE_ADMIN</p>
  </td>
  <td width=177 valign=top style='width:132.4pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Header:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Session
  Cookie</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&nbsp;</p>
  </td>
  <td width=169 valign=top style='width:126.95pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>Status</b>:
  200</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Body:</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>&quot;Hello,
  admin!&quot;</p>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'><b>&nbsp;</b></p>
  </td>
  <td width=212 valign=top style='width:159.35pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  padding:0cm 5.4pt 0cm 5.4pt'>
  <p class=MsoNormal style='margin-bottom:0cm;line-height:normal'>Testing
  route.</p>
  </td>
 </tr>
</table>