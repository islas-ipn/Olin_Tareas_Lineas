package mx.com.pendulum.olintareas.interfaces;

import com.j256.ormlite.dao.Dao;

public interface GenericDaoHelper {
    Dao<? extends Object, Long> getOlinDao(
            Class<? extends Object> objectClass);
}
