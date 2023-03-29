package at.kaindorf.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class DB_ChachedConnection {
    private Connection connection;
    private LinkedList<Statement> statementQueue = new LinkedList<>();

    public DB_ChachedConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() throws SQLException {
        if (connection == null) {
            throw new RuntimeException("No Connection!");
        }
        if (statementQueue.isEmpty()) {
            return connection.createStatement();
        }
        return statementQueue.poll();
    }

    public void releaseStatement(Statement statement) {
        if (connection == null) {
            throw new RuntimeException("No Connection!");
        }
        statementQueue.offer(statement);
    }
}
