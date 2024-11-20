package utils;

import java.sql.*;

public final class DbConnection {
    private static Connection connection;
    private static DbConnection dbConnection;

    private DbConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = getConnection();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:drifty.db";
        return DriverManager.getConnection(url);
    }

    public static DbConnection getInstance() throws SQLException {
        if (dbConnection != null) {
            return dbConnection;
        }
        dbConnection = new DbConnection();
        return dbConnection;
    }

    public void createTables() throws SQLException {
        String createSessionTableQuery = "CREATE TABLE IF NOT EXISTS SESSION (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, StartDate TEXT NOT NULL, EndDate TEXT);";
        String createFileTableQuery = "CREATE TABLE IF NOT EXISTS FILE (Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Url TEXT NOT NULL, SaveTargetPath TEXT NOT NULL, Size INTEGER, DownloadStartTime TEXT, DownloadEndTime TEXT, State INTEGER NOT NULL, SessionId INTEGER NOT NULL, FOREIGN KEY(SessionId) REFERENCES Session(Id));";
        PreparedStatement createSessionTableStatement = connection.prepareStatement(createSessionTableQuery);
        createSessionTableStatement.executeUpdate();
        PreparedStatement createFileTableStatement = connection.prepareStatement(createFileTableQuery);
        createFileTableStatement.executeUpdate();
    }

    public int addSessionRecord(String startDate) throws SQLException {
        String insertSessionQuery = "INSERT INTO SESSION (StartDate) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSessionQuery, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, startDate);
        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Failed to retrieve session ID.");
        }
    }

    public void updateSessionEndDate(int sessionId, String endDate) throws SQLException {
        String updateSessionQuery = "UPDATE SESSION SET EndDate = ? WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSessionQuery);
        preparedStatement.setString(1, endDate);
        preparedStatement.setInt(2, sessionId);
        preparedStatement.executeUpdate();
    }

    public ResultSet getSessionRecordById(int sessionId) throws SQLException {
        String getSessionQuery = "SELECT * FROM SESSION WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getSessionQuery);
        preparedStatement.setInt(1, sessionId);
        return preparedStatement.executeQuery();
    }

    public ResultSet getAllSessionRecords() throws SQLException {
        String getAllSessionsQuery = "SELECT * FROM SESSION";
        PreparedStatement preparedStatement = connection.prepareStatement(getAllSessionsQuery);
        return preparedStatement.executeQuery();
    }

    public void deleteSessionRecord(int sessionId) throws SQLException {
        String deleteSessionQuery = "DELETE FROM SESSION WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSessionQuery);
        preparedStatement.setInt(1, sessionId);
        preparedStatement.executeUpdate();
    }

    public int addFileRecord(String url, String saveTargetPath, int size, String startDownloadingTime, int sessionId) throws SQLException {
        String insertFileQuery = "INSERT INTO FILE (Url, SaveTargetPath, Size, DownloadStartTime, State, SessionId) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertFileQuery, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, url);
        preparedStatement.setString(2, saveTargetPath);
        preparedStatement.setInt(3, size);
        preparedStatement.setString(4, startDownloadingTime);
        preparedStatement.setInt(5, 0);
        preparedStatement.setInt(6, sessionId);
        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Failed to insert record into FILE table, no ID generated.");
        }
    }

    public void updateFileInfo(int fileId, int state, String endDownloadingTime) throws SQLException {
        String updateFileQuery = "UPDATE FILE SET State = ?, DownloadEndTime = ? WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateFileQuery);
        preparedStatement.setInt(1, state);
        preparedStatement.setString(2, endDownloadingTime);
        preparedStatement.setInt(3, fileId);
        preparedStatement.executeUpdate();
    }

    public ResultSet getFileRecordById(int fileId) throws SQLException {
        String getFileQuery = "SELECT * FROM FILE WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getFileQuery);
        preparedStatement.setInt(1, fileId);
        return preparedStatement.executeQuery();
    }

    public ResultSet getAllFileRecords() throws SQLException {
        String getAllFilesQuery = "SELECT * FROM FILE";
        PreparedStatement preparedStatement = connection.prepareStatement(getAllFilesQuery);
        return preparedStatement.executeQuery();
    }

    public void deleteFileRecord(int fileId) throws SQLException {
        String deleteFileQuery = "DELETE FROM FILE WHERE Id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteFileQuery);
        preparedStatement.setInt(1, fileId);
        preparedStatement.executeUpdate();
    }

//    public void Create() throws SQLException {
//        //connection.createStatement().executeQuery("");
//    }
//
//    public <T> List<T> Read(String sql) throws SQLException {
//        ResultSet resultSet = connection.createStatement().executeQuery(sql);
//        while (resultSet.next()) {
//
//        }
//    }
//
//    public void Update() throws SQLException {
//
//    }
//
//    public void Delete() throws SQLException {
//
//    }
}
