package com.hh.rdbms;

/*
 * C3p0 JDBC
 */

import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

public class C3p0Connector {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(C3p0Connector.class.getSimpleName());
    public String DATABASE_CONFIG_PATH;
    public ComboPooledDataSource cpds;

    public String driverClass = "";
    public String jdbcURL = "";
    public String user = "";
    public String password = "";
    public String minPoolSize = "";
    public String acquireIncrement = "";
    public String maxPoolSize = "";
    public String maxStatements = "";

    public C3p0Connector(String configPath) {
        DATABASE_CONFIG_PATH = configPath;
    }

    public void start() {
        try {
            log.info("DATABASE_CONFIG_PATH: " + DATABASE_CONFIG_PATH);
            Properties prop = new Properties();
            File file = new File(DATABASE_CONFIG_PATH);
            log.info("FILE PATH: " + file.getAbsolutePath());
            if (file.exists()) {
                log.info("file existed!");
                try (FileInputStream inputStream = new FileInputStream(DATABASE_CONFIG_PATH)) {
                    prop.load(inputStream);
                } catch (Exception ex) {
                    log.error("Error when load database.conf", ex);
                }
            }

            String encrypt = prop.getProperty("encrypt-database");
            File configFile = new File(DATABASE_CONFIG_PATH);
            if (configFile.exists()) {
                // Giai ma va doc thong tin tu file config
                String decryptString = "";
                if (encrypt != null && encrypt.equals("true")) {
                    EncryptDecryptUtils edUtils = new EncryptDecryptUtils();
                    decryptString = edUtils.decryptFile(DATABASE_CONFIG_PATH);
                } else {
                    FileUtils fu = new FileUtils();
                    decryptString = fu.readFileToString(DATABASE_CONFIG_PATH, FileUtils.UTF_8);
                }

                String[] properties = decryptString.split(System.getProperty("line.separator").toString());
                for (String property : properties) {
                    if (property != null && !property.trim().isEmpty() &&
                            property.trim().charAt(0) != '#' && property.contains("=")) {
                        String[] arrInformation = property.split("=", 2);
                        if (arrInformation.length == 2) {
                            if (arrInformation[0].equals("driverClass")) driverClass = arrInformation[1];
                            if (arrInformation[0].equals("jdbcURL")) jdbcURL = arrInformation[1];
                            if (arrInformation[0].equals("user")) user = arrInformation[1];
                            if (arrInformation[0].equals("password")) password = arrInformation[1];
                            if (arrInformation[0].equals("minPoolSize")) minPoolSize = arrInformation[1];
                            if (arrInformation[0].equals("acquireIncrement")) acquireIncrement = arrInformation[1];
                            if (arrInformation[0].equals("maxPoolSize")) maxPoolSize = arrInformation[1];
                            if (arrInformation[0].equals("maxStatements")) maxStatements = arrInformation[1];
                        }
                    }
                }
            }
            log.info("JDBC URL: " + jdbcURL);
            if (cpds != null) {
                cpds.close();
                cpds = null;
            }

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(driverClass); //loads the jdbc driver
            cpds.setJdbcUrl(jdbcURL);
            cpds.setUser(user);
            cpds.setPassword(password);

            // the settings below are optional -- c3p0 can work with defaults
            cpds.setMinPoolSize(Integer.parseInt(minPoolSize));
            cpds.setAcquireIncrement(Integer.parseInt(acquireIncrement));
            cpds.setMaxPoolSize(Integer.parseInt(maxPoolSize));
            cpds.setMaxStatements(Integer.parseInt(maxStatements));
            cpds.setIdleConnectionTestPeriod(10);
            cpds.setTestConnectionOnCheckin(true);
            cpds.setTestConnectionOnCheckout(true);
        } catch (Exception ex) {
            log.error("C3p0 error: ", ex);
        }
    }

    public void closeC3p0Pool() {
        cpds.close();
        cpds = null;
    }

    /**
     * Hàm mở kết nối tới cơ sở dữ liệu
     *
     * @return kết nối tới cơ sở dữ liệu
     * @since 22/07/2014 HienDM
     */
    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }

    /**
     * Hàm tạo tìm kiếm dữ liệu
     *
     * @param query      Câu lệnh truy vấn dữ liệu
     * @param connection kết nối tới database
     * @param fetchSize  Số lượng bản ghi trong cache
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<Map> queryData(String query, Connection connection, int fetchSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            if (fetchSize > 0) preparedStatement.setFetchSize(fetchSize);
            rs = preparedStatement.executeQuery();
            List lstResult = new ArrayList();
            if (rs != null) {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                while (rs.next()) {
                    Map row = new HashMap();
                    for (int i = 1; i <= columnCount; ++i) {
                        Object obj = rs.getObject(i);
                        row.put(rsMetaData.getColumnLabel(i).toLowerCase(), obj);
                    }
                    lstResult.add(row);
                }
            }
            return lstResult;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public List<Map> queryData(String query, Connection connection) throws SQLException {
        return queryData(query, connection, 0);
    }

    /**
     * Hàm tạo tìm kiếm dữ liệu
     *
     * @param query     Câu lệnh truy vấn dữ liệu
     * @param fetchSize Số lượng bản ghi trong cache
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */

    public List<Map> queryData(String query, int fetchSize) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return queryData(query, connection, fetchSize);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    public List<Map> queryData(String query) throws SQLException {
        return queryData(query, 0);
    }

    public PreparedStatement setPreparedStatement(PreparedStatement preparedStatement, List lstParameter) throws SQLException {
        if (jdbcURL.toLowerCase().contains("oracle")) {
            return setPreparedOracleStatement(preparedStatement, lstParameter);
        } else {
            return setPreparedMysqlStatement(preparedStatement, lstParameter);
        }
    }

    public PreparedStatement setPreparedOracleStatement(PreparedStatement preparedStatement, List lstParameter) throws SQLException {
        if (lstParameter != null) {
            for (int i = 0; i < lstParameter.size(); i++) {
                if (lstParameter.get(i) == null) {
                    preparedStatement.setNull(i + 1, java.sql.Types.NULL);
                } else if (lstParameter.get(i) instanceof Integer) {
                    preparedStatement.setInt(i + 1, (Integer) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Long) {
                    preparedStatement.setLong(i + 1, (Long) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Boolean) {
                    preparedStatement.setBoolean(i + 1, (Boolean) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Float) {
                    preparedStatement.setFloat(i + 1, (Float) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Double) {
                    preparedStatement.setDouble(i + 1, (Double) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Short) {
                    preparedStatement.setShort(i + 1, (Short) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof String) {
                    preparedStatement.setString(i + 1, (String) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof java.sql.Date) {
                    preparedStatement.setDate(i + 1, (java.sql.Date) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof java.util.Date) {
                    preparedStatement.setTimestamp(i + 1, new Timestamp(((java.util.Date) lstParameter.get(i)).getTime()));
                }
            }
        }
        return preparedStatement;
    }

    public PreparedStatement setPreparedMysqlStatement(PreparedStatement preparedStatement, List lstParameter) throws SQLException {
        if (lstParameter != null) {
            for (int i = 0; i < lstParameter.size(); i++) {
                if (lstParameter.get(i) == null) {
                    preparedStatement.setNull(i + 1, java.sql.Types.NULL);
                } else if (lstParameter.get(i) instanceof Integer) {
                    preparedStatement.setInt(i + 1, (Integer) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Long) {
                    preparedStatement.setLong(i + 1, (Long) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Boolean) {
                    preparedStatement.setBoolean(i + 1, (Boolean) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Float) {
                    preparedStatement.setFloat(i + 1, (Float) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Double) {
                    preparedStatement.setDouble(i + 1, (Double) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof Short) {
                    preparedStatement.setShort(i + 1, (Short) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof String) {
                    preparedStatement.setNString(i + 1, (String) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof java.sql.Date) {
                    preparedStatement.setDate(i + 1, (java.sql.Date) lstParameter.get(i));
                } else if (lstParameter.get(i) instanceof java.util.Date) {
                    preparedStatement.setTimestamp(i + 1, new Timestamp(((java.util.Date) lstParameter.get(i)).getTime()));
                }
            }
        }
        return preparedStatement;
    }

    public List<Map> queryData(String query, List lstParameter) throws SQLException {
        return queryData(query, lstParameter, 0);
    }

    /**
     * Hàm truy vấn dữ liệu theo tham số
     *
     * @param query        Câu lệnh truy vấn dữ liệu trong database
     * @param lstParameter Tham số truyền vào câu lệnh
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */

    public List<Map> queryData(String query, List lstParameter, int fetchSize) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return queryData(query, lstParameter, connection, fetchSize);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    public List<Map> queryData(String query, List lstParameter, Connection connection) throws SQLException {
        return queryData(query, lstParameter, connection, 0);
    }

    /**
     * Hàm truy vấn dữ liệu theo tham số
     *
     * @param query        Câu lệnh truy vấn dữ liệu trong database
     * @param lstParameter Tham số truyền vào câu lệnh
     * @param connection   Kết nối tới cơ sở dữ liệu
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<Map> queryData(String query, List lstParameter, Connection connection, int fetchSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            if (fetchSize > 0) preparedStatement.setFetchSize(fetchSize);
            preparedStatement = setPreparedStatement(preparedStatement, lstParameter);
            rs = preparedStatement.executeQuery();
            List lstResult = new ArrayList();
            if (rs != null) {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                while (rs.next()) {
                    Map row = new HashMap();
                    for (int i = 1; i <= columnCount; ++i) {
                        Object obj = rs.getObject(i);
                        row.put(rsMetaData.getColumnLabel(i).toLowerCase(), obj);
                    }
                    lstResult.add(row);
                }
            }
            return lstResult;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm tạo tìm kiếm dữ liệu
     *
     * @param query Câu lệnh truy vấn dữ liệu
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<List> queryDataToList(String query) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return queryDataToList(query, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm tạo tìm kiếm dữ liệu
     *
     * @param query      Câu lệnh truy vấn dữ liệu
     * @param connection Kết nối tới database
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<List> queryDataToList(String query, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            List lstResult = new ArrayList();
            if (rs != null) {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                while (rs.next()) {
                    List row = new ArrayList();
                    for (int i = 1; i <= columnCount; ++i) {
                        Object obj = rs.getObject(i);
                        row.add(obj);
                    }
                    lstResult.add(row);
                }
            }
            return lstResult;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm truy vấn dữ liệu theo tham số
     *
     * @param query        Câu lệnh truy vấn dữ liệu trong database
     * @param lstParameter Tham số truyền vào câu lệnh
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<List> queryDataToList(String query, List lstParameter) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return queryDataToList(query, lstParameter, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm truy vấn dữ liệu theo tham số
     *
     * @param query        Câu lệnh truy vấn dữ liệu trong database
     * @param lstParameter Tham số truyền vào câu lệnh
     * @param connection   Kết nối tới cơ sở dữ liệu
     * @return Các bản ghi tìm kiếm được
     * @since 13/03/2014 HienDM
     */
    public List<List> queryDataToList(String query, List lstParameter, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement = setPreparedStatement(preparedStatement, lstParameter);
            rs = preparedStatement.executeQuery();
            List lstResult = new ArrayList();
            if (rs != null) {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                while (rs.next()) {
                    List row = new ArrayList();
                    for (int i = 1; i <= columnCount; ++i) {
                        Object obj = rs.getObject(i);
                        row.add(obj);
                    }
                    lstResult.add(row);
                }
            }
            return lstResult;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query Câu lệnh cập nhật dữ liệu trong cassandra
     * @since 13/03/2014 HienDM
     */

    public void executeQuery(String query) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            executeQuery(query, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query      Câu lệnh cập nhật dữ liệu trong cassandra
     * @param connection kết nối tới cơ sở dữ liệu
     * @since 13/03/2014 HienDM
     */
    public void executeQuery(String query, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query        Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstParameter Tham số truyền vào câu lệnh
     * @since 13/03/2014 HienDM
     */

    public void executeQuery(String query, List lstParameter) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            executeQuery(query, lstParameter, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query        Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstParameter Tham số truyền vào câu lệnh
     * @param connection   kết nối tới cơ sở dữ liệu
     * @since 13/03/2014 HienDM
     */
    public void executeQuery(String query, List lstParameter, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement = setPreparedStatement(preparedStatement, lstParameter);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query Câu lệnh cập nhật dữ liệu trong cassandra
     * @since 13/03/2014 HienDM
     */
    public Integer insertData(String query) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return insertData(query, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query      Câu lệnh them moi du lieu
     * @param connection kết nối tới cơ sở dữ liệu
     * @since 13/03/2014 HienDM
     */
    public Integer insertData(String query, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return null;
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query        Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstParameter Tham số truyền vào câu lệnh
     * @since 13/03/2014 HienDM
     */
    public Integer insertData(String query, List lstParameter) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return insertData(query, lstParameter, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query        Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstParameter Tham số truyền vào câu lệnh
     * @param connection   kết nối tới cơ sở dữ liệu
     * @since 13/03/2014 HienDM
     */
    public Integer insertData(String query, List lstParameter, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement = setPreparedStatement(preparedStatement, lstParameter);
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return null;
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query    Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstBatch Tham số truyền vào câu lệnh
     * @since 13/03/2014 HienDM
     */
    public void executeQueryBatch(String query, List<List> lstBatch) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            executeQueryBatch(query, lstBatch, connection);
        } finally {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Hàm cập nhật cơ sở dữ liệu
     *
     * @param query      Câu lệnh cập nhật dữ liệu trong cassandra
     * @param lstBatch   Tham số truyền vào câu lệnh
     * @param connection kết nối tới cơ sở dữ liệu
     * @since 13/03/2014 HienDM
     */
    public void executeQueryBatch(String query, List<List> lstBatch, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query);
            for (int k = 0; k < lstBatch.size(); k++) {
                List<List> lstParameter = lstBatch.get(k);
                preparedStatement = setPreparedStatement(preparedStatement, lstParameter);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Hàm lấy dữ liệu sequence
     *
     * @param sequence Sequence
     * @return dữ liệu Sequence
     * @since 03/01/2015 HienDM
     */
    public long getSequenceValue(String sequence) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            long myId = -1l;
            String sqlIdentifier = "select " + sequence + ".NEXTVAL from dual";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlIdentifier);
            rs = preparedStatement.executeQuery();
            if (rs != null) if (rs.next()) myId = rs.getLong(1);
            return myId;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

}
