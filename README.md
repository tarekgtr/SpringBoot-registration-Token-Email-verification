
# Hello 👋


# Spring boot registration with token and email verification

To run this app perfectly you need some configurations :




## Gmail configurations

- Go to : https://myaccount.google.com/security .

- Make sure that 2-Step verfication is ON which located in Signing in to Google section.

- Create an app passwords you will find it in the same section.

- Copy generated password and the email.

## Application.yml configurations :

- Configure your database url, username and password
Then past :
- username: the email
- password: the generated password

## EmailService :

- Go to : src/main/java/com/example/secure/registration/email/EmailService.java.
- Change the email in setFrom function with your email.






```java
helper.setFrom("xxxxxx@gmail.com");

```


## 👋 Enjoy ...


