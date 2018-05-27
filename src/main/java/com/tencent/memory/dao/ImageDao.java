package com.tencent.memory.dao;

import com.tencent.memory.model.Image;
import com.tencent.memory.model.MyException;
import com.tencent.memory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;


@Repository
public class ImageDao extends JdbcDaoSupport {

    @Autowired
    public ImageDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public int addImagesToGallery(long galleryId, long uid, String description, Image[] images) {
        Connection connection = getConnection();

        String sql = "insert into image_group(galleryId,description) values(?,?)";
        String sql2 = "insert into image(galleryId,groupId,description,url,creater) values(?,?,?,?,?)";

        PreparedStatement pst = null;
        ResultSet rs = null;
        int groupId = -1;

        try {
            pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setLong(1, galleryId);
            pst.setString(2, description);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();

            groupId = 0;
            if (rs.next()) {
                groupId = rs.getInt(1);
                System.out.println("new groupId:" + groupId);
            }


            pst = connection.prepareStatement(sql2);
            for (Image image : images) {
                pst.setLong(1, galleryId);
                pst.setLong(2, groupId);
                pst.setString(3, image.description);
                pst.setString(4, image.url);
                pst.setLong(5, uid);
                pst.addBatch();
            }

            int[] ss = pst.executeBatch();
            int count = 0;
            for (int i : ss) {
                count += i;
            }
            return count;


        } catch (SQLException e) {
            throw new MyException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
