package jiri.adam.dbviewer.db.dao;

import jiri.adam.dbviewer.db.entity.DbConnection;
import org.springframework.data.repository.CrudRepository;

public interface DbConnectionDao extends CrudRepository<DbConnection, Integer> {
}
