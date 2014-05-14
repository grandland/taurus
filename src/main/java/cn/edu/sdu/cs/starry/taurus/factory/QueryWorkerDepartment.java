package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessHandlerException;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * For each child of {@link QueryWorker}, it's corresponding
 * {@link QueryWorkerDepartment} contains a fixed number of workers and is a
 * singleton.
 *
 * @author SDU.xccui
 */
public class QueryWorkerDepartment {
    private static final Logger LOG = LoggerFactory
            .getLogger(QueryWorkerDepartment.class);
    private static Map<Class<? extends QueryWorker>, QueryWorkerDepartment> workerDepartmentMap = new HashMap<Class<? extends QueryWorker>, QueryWorkerDepartment>();
    private LinkedList<QueryWorker> workerList;
    private Class<? extends QueryWorker> workerClass;
    private volatile int capacity;

    private QueryWorkerDepartment(Class<? extends QueryWorker> workerClass,
                                  int systemResource, CacheTool cacheUtillity) {
        capacity = 0;
        workerList = new LinkedList<QueryWorker>();
        this.workerClass = workerClass;
        QueryWorker worker = null;
        while (systemResource > 0) {
            try {
                worker = workerClass.newInstance();
                systemResource -= worker.getMinSystemResource() > 0 ? 1
                        : worker.getMinSystemResource();
                worker.setCacheUtility(cacheUtillity);
                workerList.add(worker);
                ++capacity;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (null != worker) {
            LOG.info("Build a new RPCWorkerDepartment for '"
                    + workerClass.getName() + "' by '" + worker.getAuthor()
                    + "' with " + capacity + " workers");
        } else {
            LOG.error("Error building RPCWorkerDepartment for '"
                    + (workerClass == null ? "unknown worker class"
                    : workerClass.getName()) + "'!");
        }
    }

    /**
     * Hire a worker to do business.
     *
     * @return
     * @throws BusinessHandlerException if there is no worker left
     */
    public synchronized QueryWorker hireAWorker()
            throws BusinessHandlerException {
        if (workerList.size() <= 0) {
            throw new BusinessHandlerException();
        }
        QueryWorker worker = workerList.removeFirst();
        LOG.info("Hire a worker for class '" + workerClass.getSimpleName()
                + "', capacity state: " + getCapacityLeft() + "/" + capacity);
        return worker;
    }

    /**
     * Fire a worker after doing the business.
     *
     * @param worker
     * @return
     * @throws BusinessHandlerException if the worker is not hired from this pool
     */
    public synchronized QueryWorker fireAWorker(QueryWorker worker)
            throws BusinessHandlerException {
        if (worker.getClass().equals(workerClass)) {
            workerList.add(worker);
            LOG.info("Fire a workerfor class '" + workerClass.getSimpleName()
                    + "', capacity: " + getCapacityLeft() + "/" + capacity);
        } else {
            throw new BusinessHandlerException();
        }
        return null;
    }

    /**
     * Get the capacity for this department.
     *
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Get the capacity left for this department.
     *
     * @return
     */
    public int getCapacityLeft() {
        return capacity - workerList.size();
    }

    /**
     * Get the department's worker class.
     *
     * @return
     */
    public Class<? extends QueryWorker> getDepartmentClass() {
        return workerClass;
    }

    public static void destroy() {
        // for (final SyncWorkerDepartment department : workerDepartmentMap
        // .values()) {
        // List<Thread> destoryThreadList = new LinkedList<Thread>();
        // destoryThreadList.add(new Thread() {
        // @Override
        // public void run() {
        // super.run();
        // LOG.info("'" + department.workerClass
        // + "' worker deaprtment will be destroyed");
        // while (department.getCapacityLeft() > 0) {
        // LOG.warn("For '" + department.workerClass
        // + "', waiting " + department.getCapacityLeft()
        // + " workers to be fired");
        // try {
        // Thread.sleep(5000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        // });
        // for (Thread t : destoryThreadList) {
        // t.start();
        // }
        // for (Thread t : destoryThreadList) {
        // try {
        // t.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // }
    }

    /**
     * Build a new department for the given worker class with the given capacity
     * for workers.
     *
     * @param worker
     * @param systemResource
     * @return
     */
    public static QueryWorkerDepartment buildNewDepartment(QueryWorker worker,
                                                           int systemResource, CacheTool cacheUtility) {
        QueryWorkerDepartment department = workerDepartmentMap.get(worker
                .getClass().getName().getClass());
        if (null == department) {
            synchronized (QueryWorkerDepartment.class) {
                if (null == (department = workerDepartmentMap.get(worker
                        .getClass().getClass()))) {
                    department = new QueryWorkerDepartment(worker.getClass(),
                            systemResource, cacheUtility);
                    workerDepartmentMap.put(worker.getClass(), department);
                }
            }
        }
        return department;
    }
}
