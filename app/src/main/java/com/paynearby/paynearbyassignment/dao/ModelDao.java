package com.paynearby.paynearbyassignment.dao;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.paynearby.paynearbyassignment.db.Model;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public class ModelDao {

    private OrmLiteSqliteOpenHelper dbHelper;
    private static final String PERSIST_ERROR = "Sorry, could not persist list";
    private static final String DELETE_ERROR = "Sorry, could not delete list";

    public ModelDao(OrmLiteSqliteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * @param modelClass the class of the model which extends {@linkplain Model}
     * @return the dao object for operations
     * @throws SQLException if a problem occurs
     */
    public Dao getModelDao(Class modelClass) throws SQLException {
        return dbHelper.getDao(modelClass);
    }

    /**
     * this function will store model
     *
     * @param model which extends {@linkplain Model}
     * @throws SQLException if something wrong occurs
     */
    public void persist(Model model) throws SQLException {
        Dao dao = dbHelper.getDao(model.getClass());
        dao.create(model);
    }

    /**
     * this function will store a list of models in bulk
     *
     * @param modelList which extends {@linkplain Model}
     * @throws Exception if something wrong occurs
     */
    public void bulkPersist(final List modelList) throws Exception {
        if (modelList != null && !modelList.isEmpty()) {
            final Dao dao = dbHelper.getDao(modelList.get(0).getClass());
            dao.callBatchTasks(new Callable() {
                                   @Override
                                   public Object call() throws Exception {
                                       for (Object model : modelList) {
                                           dao.create(model);
                                       }
                                       return null;
                                   }
                               }
            );
        } else {
            throw new Exception(PERSIST_ERROR);
        }
    }

    /**
     * this method returns model if found
     *
     * @param id         of model
     * @param modelClass model child class
     * @return {@linkplain Model}
     * @throws SQLException if something goes wrong
     */
    public Model getModelById(int id, Class modelClass) throws SQLException {
        Dao dao = dbHelper.getDao(modelClass);
        return (Model) dao.queryForId(id);
    }

    /**
     * this function will remove model
     *
     * @param model       which extends {@linkplain Model}
     * @param persistence set to true if u want to delete the model from database; to false if you want to only set the active parameter to false
     * @throws SQLException if something wrong occurs
     */
    public void remove(Model model, boolean persistence) throws SQLException {

        Dao dao = dbHelper.getDao(model.getClass());
        if (persistence) {
            dao.delete(model);
        } else {
            model.setActive(false);
            dao.update(model);
        }
    }

    /**
     * this function will remove a list of models in bulk
     *
     * @param modelList   which extends {@linkplain Model}
     * @param persistence set to true if u want to delete the models from database; to false if you want to only set the active parameter to false
     * @throws SQLException if something wrong occurs
     */
    public void bulkDelete(final List modelList, boolean persistence) throws Exception {
        if (modelList != null && !modelList.isEmpty()) {
            final Dao dao = dbHelper.getDao(modelList.get(0).getClass());
            if (persistence) {
                dao.delete(modelList);
            } else {
                dao.callBatchTasks(new Callable() {
                                       @Override
                                       public Object call() throws Exception {
                                           for (Object model : modelList) {
                                               ((Model) model).setActive(false);
                                               dao.update(model);
                                           }
                                           return null;
                                       }
                                   }
                );
            }
        } else {
            throw new Exception(DELETE_ERROR);
        }
    }

    /**
     * get all model list
     *
     * @param modelClass model class
     * @return {@link List} of models if found else null
     * @throws SQLException throws if some error occurs
     */
    public List getAllModelList(Class modelClass) throws SQLException {
        Dao dao = getDbHelper().getDao(modelClass);
        QueryBuilder queryBuilder = dao.queryBuilder();
        return dao.query(queryBuilder.prepare());
    }

    /**
     * get count of all model list
     *
     * @param modelClass model class
     * @return {@linkplain Integer} of count of models if found else null
     * @throws SQLException throws if some error occurs
     */
    public int getCountOfModels(Class modelClass) throws SQLException {

        Dao dao = getDbHelper().getDao(modelClass);
        QueryBuilder queryBuilder = dao.queryBuilder().setCountOf(true);
        return (int) dao.countOf(queryBuilder.prepare());
    }

    /**
     * Clear the list
     *
     * @param modelClass model class
     * @throws SQLException throws if some error occurs
     */
    public void clear(Class modelClass) throws SQLException {
        TableUtils.clearTable(dbHelper.getConnectionSource(), modelClass);
    }

    public OrmLiteSqliteOpenHelper getDbHelper() {
        return dbHelper;
    }
}
