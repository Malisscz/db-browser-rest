package jiri.adam.dbviewer.db.dao;

import jiri.adam.dbviewer.db.entity.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple DbConnection wrapper service using DAO object accessing DbConnections
 */
@Service
public class DbConnectionService {

    @Autowired
    DbConnectionDao dbConnectionDao;

    public List<DbConnection> getDbConnections() {
        List<DbConnection> persons = new LinkedList<>();
        dbConnectionDao.findAll().forEach(person -> persons.add(person));
        return persons;
    }

    public DbConnection getDbConnection(Integer id) {
        return dbConnectionDao.findById(id).get();
    }

    public void saveOrUpdate(DbConnection connection) {
        dbConnectionDao.save(connection);
    }

    public void delete(Integer id) {
        dbConnectionDao.deleteById(id);
    }

}
