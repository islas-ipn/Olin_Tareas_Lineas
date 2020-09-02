package mx.com.pendulum.olintareas.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.dto.UserPermissions;
import mx.com.pendulum.olintareas.dto.UserSession;

public class UserDataDaoImpl extends BaseDaoImpl<UserData, Long> {
    private static final String TAG = UserDataDaoImpl.class.getSimpleName();

    Dao<UserSession, Long> sessionDao;
    Dao<UserPermissions, Long> permissionDao;

    public UserDataDaoImpl(ConnectionSource connectionSource,
                           DatabaseTableConfig<UserData> tableConfig)
            throws SQLException {
        super(connectionSource, tableConfig);
    }

    public UserDataDaoImpl(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, UserData.class);

        sessionDao = DaoManager.createDao(connectionSource, UserSession.class);
        permissionDao = DaoManager.createDao(connectionSource,
                UserPermissions.class);
    }

    @Override
    public int create(UserData user) throws SQLException {
        if (user.getPermisos() != null)
            permissionDao.create(user.getPermisos());

        if (user.getSession() != null)
            sessionDao.create(user.getSession());

        return super.create(user);
    }

    @Override
    public int update(UserData user) throws SQLException {
        if (user.getPermisos() != null)
            permissionDao.update(user.getPermisos());

        if (user.getSession() != null)
            sessionDao.update(user.getSession());

        return super.update(user);
    }

    @Override
    public int delete(UserData user) throws SQLException {
        if (user.getPermisos() != null)
            permissionDao.delete(user.getPermisos());

        if (user.getSession() != null)
            sessionDao.delete(user.getSession());

        return super.delete(user);
    }

    protected UserSession getCurrentUserSession() {
        UserSession session = null;
        try {
            QueryBuilder<UserSession, Long> queryBuilder = sessionDao
                    .queryBuilder();
            queryBuilder.where().eq(UserSession.COL_LOGGED_IN, true);
            PreparedQuery<UserSession> preparedQuery = queryBuilder.prepare();
            List<UserSession> sessions = sessionDao.query(preparedQuery);
            if (sessions.size() > 0) {
                session = sessions.get(sessions.size() - 1);
            } else {
                sessions = sessionDao.queryForAll();
                if (sessions.size() > 0)
                    session = sessions.get(sessions.size() - 1);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return session;
    }

    public UserData getCurrentUser() {
        UserData userData = null;
        UserSession session = getCurrentUserSession();
        try {
            if (session != null) {
                QueryBuilder<UserData, Long> userQueryBuilder = queryBuilder();
                userQueryBuilder.where().eq(UserData.COL_ID_SESSION,
                        session.get_id());
                PreparedQuery<UserData> userPreparedQuery = userQueryBuilder
                        .prepare();
                List<UserData> users = query(userPreparedQuery);
                if (users.size() > 0)
                    userData = users.get(users.size() - 1);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return userData;
    }
}
