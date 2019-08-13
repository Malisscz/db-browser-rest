package jiri.adam.dbviewer.db.dao;

import jiri.adam.dbviewer.db.entity.DbConnection;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD dao for DBConnection ...
 */
public interface DbConnectionDao extends CrudRepository<DbConnection, Integer> {
}
