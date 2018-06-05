CREATE TABLE `user` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
`openId` varchar(64) NOT NULL,
`name` varchar(64) NOT NULL,
`avatar` varchar(255) NOT NULL DEFAULT '',
`gender` varchar(2) NOT NULL DEFAULT '',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) ,
UNIQUE INDEX `k_unique_openid` (`openId` ASC)
);
CREATE TABLE `gallery` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL DEFAULT '',
`description` varchar(255) NOT NULL DEFAULT '',
`cover` varchar(255) NOT NULL DEFAULT '' COMMENT '封面',
`type` int NOT NULL DEFAULT 0 COMMENT '类型',
`creater` bigint UNSIGNED NOT NULL,
`images` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '图片数',
`users` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户数',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) ,
FULLTEXT INDEX `i_index_name` (`name` ASC)
);
CREATE TABLE `user_gallery` (
`uid` bigint UNSIGNED NOT NULL,
`galleryId` bigint UNSIGNED NOT NULL,
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`exited` timestamp NOT NULL DEFAULT '2038-01-19 03:14:07' COMMENT '用户退出时间',
PRIMARY KEY (`uid`, `galleryId`) 
);
CREATE TRIGGER `t_user_gallery_add` After INSERT ON `user_gallery` FOR EACH ROW update gallery set users = users + 1 where id = new.galleryId;;
CREATE TRIGGER `t_user_gallery_del` After DELETE ON `user_gallery` FOR EACH ROW update gallery set users = users - 1 where id = old.galleryId;;

CREATE TABLE `image` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
`galleryId` bigint UNSIGNED NOT NULL,
`groupId` bigint UNSIGNED NOT NULL COMMENT '所属组id',
`url` varchar(255) NOT NULL,
`creater` bigint UNSIGNED NOT NULL,
`description` varchar(255) NOT NULL DEFAULT '',
`likes` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
`comments` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) 
);
CREATE TRIGGER `t_add_image` After INSERT ON `image` FOR EACH ROW UPDATE gallery SET images = images + 1 , updated = now()  WHERE id = new.galleryId;;
CREATE TRIGGER `t_del_image` Before DELETE ON `image` FOR EACH ROW UPDATE gallery SET images = images - 1  WHERE id = old.galleryId;;

CREATE TABLE `comment` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
`imageId` bigint UNSIGNED NOT NULL,
`content` varchar(2000) NOT NULL DEFAULT '',
`creater` bigint UNSIGNED NOT NULL,
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) 
);
CREATE TRIGGER `t_add_comment` After INSERT ON `comment` FOR EACH ROW update image set comments = comments + 1 where id = new.imageId;;
CREATE TRIGGER `t_del_comment` After DELETE ON `comment` FOR EACH ROW update image set comments = comments - 1 where id = old.imageId;;

CREATE TABLE `image_group` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '组id',
`galleryId` bigint UNSIGNED NOT NULL,
`creater` bigint UNSIGNED NOT NULL,
`description` varchar(255) NOT NULL DEFAULT '',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`deleted` timestamp NOT NULL DEFAULT '2038-01-19 03:14:07',
PRIMARY KEY (`id`) 
);
CREATE TABLE `message` (
`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
`uid` bigint UNSIGNED NOT NULL COMMENT '对象用户id',
`creater` bigint UNSIGNED NOT NULL COMMENT '创建人id',
`imageId` bigint UNSIGNED NOT NULL COMMENT '图片id',
`type` int NOT NULL DEFAULT 0 COMMENT '类型 0-点赞 1-评论',
`content` varchar(255) NOT NULL DEFAULT '' COMMENT '评论内容',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) 
);

ALTER TABLE `gallery` ADD CONSTRAINT `fk_gallery_user_1` FOREIGN KEY (`creater`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `user_gallery` ADD CONSTRAINT `fk_user_gallery_user_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `user_gallery` ADD CONSTRAINT `fk_user_gallery_gallery_1` FOREIGN KEY (`galleryId`) REFERENCES `gallery` (`id`) ON DELETE CASCADE;
ALTER TABLE `image` ADD CONSTRAINT `fk_image_user_1` FOREIGN KEY (`creater`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `fk_comment_image_1` FOREIGN KEY (`imageId`) REFERENCES `image` (`id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `fk_comment_user_1` FOREIGN KEY (`creater`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `image_group` ADD CONSTRAINT `fk_image_group_gallery_1` FOREIGN KEY (`galleryId`) REFERENCES `gallery` (`id`) ON DELETE CASCADE;
ALTER TABLE `image` ADD CONSTRAINT `fk_image_gallery_1` FOREIGN KEY (`galleryId`) REFERENCES `gallery` (`id`) ON DELETE CASCADE;
ALTER TABLE `image` ADD CONSTRAINT `fk_image_image_group_1` FOREIGN KEY (`groupId`) REFERENCES `image_group` (`id`) ON DELETE CASCADE;
ALTER TABLE `image_group` ADD CONSTRAINT `fk_image_group_user_1` FOREIGN KEY (`creater`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `message` ADD CONSTRAINT `fk_message_user_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `message` ADD CONSTRAINT `fk_message_user_2` FOREIGN KEY (`creater`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `message` ADD CONSTRAINT `fk_message_image_1` FOREIGN KEY (`imageId`) REFERENCES `image` (`id`) ON DELETE CASCADE;

