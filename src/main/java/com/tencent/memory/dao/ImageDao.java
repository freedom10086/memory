package com.tencent.memory.dao;

import com.tencent.memory.model.MyException;
import com.tencent.memory.model.UploadRequest;
import com.tencent.memory.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;


@Repository
public class ImageDao extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(ImageDao.class);
    private final ImageGroupMapper imageGroupMapper;

    @Autowired
    public ImageDao(DataSource dataSource, ImageGroupMapper imageGroupMapper) {
        setDataSource(dataSource);
        this.imageGroupMapper = imageGroupMapper;
    }

    // 新发布一条动态
    // 可以上传多张图片
    // 1. create_group
    // 2. insert image
    // 3. update_cover 更新封面
    public int addImagesToGallery(long galleryId, long uid, String description, UploadRequest[] images) {
        logger.info("add images galleryId:{}, uid:{},images:{}", galleryId, uid, images.length);
        Connection connection = getConnection();

        String sql = "insert into image_group(galleryId,creater,description) values(?,?,?)";
        String sql2 = "insert into image(galleryId,groupId,description,url,creater) values(?,?,?,?,?)";
        String sql3 = "update gallery set cover = ? where id = ?";

        PreparedStatement pst = null;
        ResultSet rs = null;
        long groupId = -1;

        try {
            pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setLong(1, galleryId);
            pst.setLong(2, uid);
            pst.setString(3, description);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();

            if (rs.next()) {
                groupId = rs.getLong(1);
                logger.info("first step create group {}", groupId);
            } else {
                logger.error("group id not return");
                throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, "创建图片组失败");
            }


            pst = connection.prepareStatement(sql2);
            for (UploadRequest image : images) {
                pst.setLong(1, galleryId);
                pst.setLong(2, groupId);
                pst.setString(3, TextUtils.isEmpty(image.description) ? "" : image.description);
                pst.setString(4, image.url);
                pst.setLong(5, uid);
                pst.addBatch();
            }

            int[] ss = pst.executeBatch();
            int count = 0;
            for (int i : ss) {
                count += i;
            }

            logger.info("second step insert image {}", count);

            pst = connection.prepareStatement(sql3);
            pst.setString(1, images[0].url);
            pst.setLong(2, galleryId);
            int u = pst.executeUpdate();

            logger.info("last step update cover {}", u);
            return count;


        } catch (SQLException e) {
            if (groupId > 0) {
                logger.error("insert images failed roll back delete image group {}", groupId);
                imageGroupMapper.delete(groupId);
            }

            throw new MyException(e);

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("error close connection {}", e.getMessage());
            }
        }
    }
}
