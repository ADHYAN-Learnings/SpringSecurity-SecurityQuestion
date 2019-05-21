# SpringSecurity-SecurityQuestion
A small project for adding security question during signup and forgot password using spring boot and spring security.

After running spring boot application Run this insert code in your database to enter the security questions and by the way your database 
name should be springsecurity



INSERT INTO `springsecurity`.`security_related_questions` (`id`, `text`) VALUES ('1', 'What is your middle name?');
INSERT INTO `springsecurity`.`security_related_questions` (`id`, `text`) VALUES ('2', 'What is your favourite movie');
INSERT INTO `springsecurity`.`security_related_questions` (`id`, `text`) VALUES ('3', 'Your favourite superhero?');
INSERT INTO `springsecurity`.`security_related_questions` (`id`, `text`) VALUES ('4', 'What is your nickname?');
INSERT INTO `springsecurity`.`security_related_questions` (`id`, `text`) VALUES ('5', 'What is your favourite place that you visited?');


Note: Please look into the application.properties file and change according to your requirement.
Note: You need Mysql database around 8.0
