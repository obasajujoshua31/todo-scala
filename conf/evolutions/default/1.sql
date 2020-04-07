# Todos schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `todo_scalar`.`todos` (
   `id` INT(11) NOT NULL AUTO_INCREMENT,
   `title` VARCHAR(45) NULL DEFAULT NULL,
   `is_completed` TINYINT(4) NULL  DEFAULT NULL,
   PRIMARY KEY (`id`))
   AUTO_INCREMENT = 2
   DEFAULT CHARACTER SET = utf8
   # --- !Downs
   drop table `todos`