
DROP TABLE IF EXISTS `monkpentdb`.`user_join_log` ;
DROP TABLE IF EXISTS `monkpentdb`.`ticket_stock` ;
DROP TABLE IF EXISTS `monkpentdb`.`purchase_ticket_mapping` ;
DROP TABLE IF EXISTS `monkpentdb`.`purchase` ;
DROP TABLE IF EXISTS `monkpentdb`.`photo` ;
DROP TABLE IF EXISTS `monkpentdb`.`ticket` ;
DROP TABLE IF EXISTS `monkpentdb`.`dibs` ;
DROP TABLE IF EXISTS `monkpentdb`.`user` ;
DROP TABLE IF EXISTS `monkpentdb`.`room` ;
DROP TABLE IF EXISTS `monkpentdb`.`amenity_category` ;
DROP TABLE IF EXISTS `monkpentdb`.`category` ;
DROP TABLE IF EXISTS `monkpentdb`.`amenity` ;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`amenity` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `deadline_date` DATETIME NOT NULL,
  `detail` VARCHAR(50) NOT NULL,
  `max_person_num` INT(11) NOT NULL,
  `min_person_num` INT(11) NOT NULL,
  `recommended` INT(11) NOT NULL,
  `start_date` DATETIME NOT NULL,
  `status` INT(11) NOT NULL DEFAULT 0,
  `thumbnail_name` VARCHAR(255) NOT NULL,
  `title` VARCHAR(30) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_lw6jrn2i2gddofvl81h84e1gr` (`title` ASC) VISIBLE,
  INDEX `ix_amenity_status_deadline_date` (`status` ASC, `deadline_date` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(10) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_46ccwnsi9409t36lurvtyljak` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`amenity_category` (
  `amenity_id` BIGINT(20) NOT NULL,
  `category_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`amenity_id`, `category_id`),
  INDEX `FKserl2gbxnq559it1ssa8892tu` (`category_id` ASC) VISIBLE,
  CONSTRAINT `FK3m37mqtbypu6gs7y69lij502x`
    FOREIGN KEY (`amenity_id`)
    REFERENCES `monkpentdb`.`amenity` (`id`),
  CONSTRAINT `FKserl2gbxnq559it1ssa8892tu`
    FOREIGN KEY (`category_id`)
    REFERENCES `monkpentdb`.`category` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`room` (
  `id` VARCHAR(8) NOT NULL,
  `user_role` INT(11) NOT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_q0m921ecs8v2s58xh95nppp5c` (`user_id` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_role` INT(11) NOT NULL,
  `birth` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `gender` INT(11) NOT NULL,
  `receive_info` INT(11) NOT NULL,
  `last_modified_at` DATETIME NULL DEFAULT NULL,
  `life_style` INT(11) NULL DEFAULT NULL,
  `login_type` INT(11) NOT NULL,
  `name` VARCHAR(15) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `phone_num` VARCHAR(20) NOT NULL,
  `room_id` VARCHAR(8) NULL DEFAULT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ob8kqyqqgmefl0aco34akdtpe` (`email` ASC) VISIBLE,
  UNIQUE INDEX `UK_gh4o0b6dwn4yexk0f96wjga26` (`phone_num` ASC) VISIBLE,
  UNIQUE INDEX `UK_dv85qk2eq1vks4gdr353b3igm` (`room_id` ASC) VISIBLE,
  INDEX `ix_user_email` (`email` ASC) VISIBLE,
  INDEX `ix_user_phone_num` (`phone_num` ASC) VISIBLE,
  CONSTRAINT `FKka86kdnug8uwmlylletgja0x0`
    FOREIGN KEY (`room_id`)
    REFERENCES `monkpentdb`.`room` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`dibs` (
  `amenity_id` BIGINT(20) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`amenity_id`, `user_id`),
  INDEX `FKqoso757ps276iuaim56x9iqsx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKq0w27pjmpddvek9ejgwtprjys`
    FOREIGN KEY (`amenity_id`)
    REFERENCES `monkpentdb`.`amenity` (`id`),
  CONSTRAINT `FKqoso757ps276iuaim56x9iqsx`
    FOREIGN KEY (`user_id`)
    REFERENCES `monkpentdb`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`ticket` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `capacity` INT(11) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `detail` VARCHAR(50) NULL DEFAULT NULL,
  `event_date_time` DATETIME NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  `price` INT(11) NOT NULL,
  `amenity_id` BIGINT(20) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  INDEX `FKeuqy2hpu9h8vdw3xbbrivl2c5` (`amenity_id` ASC) VISIBLE,
  CONSTRAINT `FKeuqy2hpu9h8vdw3xbbrivl2c5`
    FOREIGN KEY (`amenity_id`)
    REFERENCES `monkpentdb`.`amenity` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`photo` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `type` INT(11) NOT NULL,
  `amenity_id` BIGINT(20) NULL DEFAULT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  INDEX `FK764btxfnrsyqj7ycr6mbgnomy` (`amenity_id` ASC) VISIBLE,
  CONSTRAINT `FK764btxfnrsyqj7ycr6mbgnomy`
    FOREIGN KEY (`amenity_id`)
    REFERENCES `monkpentdb`.`amenity` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`purchase` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `amount` INT(11) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `order_id` VARCHAR(255) NOT NULL,
  `order_name` VARCHAR(255) NOT NULL,
  `order_status` VARCHAR(255) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `amenity_id` BIGINT(20) NOT NULL,
  `cancel_reason` VARCHAR(255) NULL DEFAULT NULL,
  `payments_key` VARCHAR(255) NULL DEFAULT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  INDEX `FK40q1hrtmdxj8leh75a6crq56c` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK40q1hrtmdxj8leh75a6crq56c`
    FOREIGN KEY (`user_id`)
    REFERENCES `monkpentdb`.`user` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`purchase_ticket_mapping` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `quantity` INT(11) NOT NULL,
  `purchase_id` BIGINT(20) NOT NULL,
  `ticket_id` BIGINT(20) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  INDEX `FKejhthot8fw1u7npxnejsjyob` (`purchase_id` ASC) VISIBLE,
  INDEX `FKffrqcrk71sv7ikp8stgc45y4l` (`ticket_id` ASC) VISIBLE,
  CONSTRAINT `FKejhthot8fw1u7npxnejsjyob`
    FOREIGN KEY (`purchase_id`)
    REFERENCES `monkpentdb`.`purchase` (`id`),
  CONSTRAINT `FKffrqcrk71sv7ikp8stgc45y4l`
    FOREIGN KEY (`ticket_id`)
    REFERENCES `monkpentdb`.`ticket` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`ticket_stock` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `purchased_quantity` INT(11) NOT NULL,
  `ticket_id` BIGINT(20) NOT NULL,
  `total_quantity` INT(11) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_i931a9j94dajo62h6msmy45ub` (`ticket_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `monkpentdb`.`user_join_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NOT NULL,
  `sign_out_reason` VARCHAR(255) NULL DEFAULT NULL,
  `type` INT(11) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4;
