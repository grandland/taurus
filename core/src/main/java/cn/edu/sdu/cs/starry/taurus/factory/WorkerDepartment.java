package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessHandlerException;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.processor.Worker;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * For each child of {@link QueryWorker}, it's corresponding
 * {@link WorkerDepartment} contains a fixed number of workers and is a
 * singleton.
 *
 * @author SDU.xccui
 */
public class WorkerDepartment<T extends Worker> {
    private static final Logger LOG = LoggerFactory
            .getLogger(WorkerDepartment.class);
    private static Map<Class<? extends Worker>, WorkerDepartment> workerDepartmentMap = new HashMap<Class<? extends Worker>, WorkerDepartment>();
    private LinkedList<T> workerList;
    private Class<T> workerClass;
    private volatile int capacity;

    private WorkerDepartment(Class<T> workerClass,
                                  int systemResource, CacheTool cacheUtillity) {
        capacity = 0;
        workerList = new LinkedList<T>();
        this.workerClass = workerClass;
        T worker = null;
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
    public synchronized T hireAWorker()
            throws BusinessHandlerException {
        if (workerList.size() <= 0) {
            throw new BusinessHandlerException();
        }
        T worker = workerList.removeFirst();
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
    public synchronized void fireAWorker(T worker)
            throws BusinessHandlerException {
        if (worker.getClass().equals(workerClass)) {
            workerList.add(worker);
            LOG.info("Fire a workerfor class '" + workerClass.getSimpleName()
                    + "', capacity: " + getCapacityLeft() + "/" + capacity);
        } else {
            throw new BusinessHandlerException();
        }
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
    public Class<T> getDepartmentClass() {
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
    @SuppressWarnings("unchecked")
	public static WorkerDepartment buildNewDepartment(Worker worker, 
                                                           int systemResource, CacheTool cacheUtility) {
        WorkerDepartment department = workerDepartmentMap.get(worker.getClass());
        if (null == department) {
            synchronized (WorkerDepartment.class) {
                if (null == (department = workerDepartmentMap.get(worker
                        .getClass().getClass()))) {
                    department = new WorkerDepartment(worker.getClass(),
                            systemResource, cacheUtility);
                    workerDepartmentMap.put(worker.getClass(), department);
                }
            }
        }
        return department;
    }
}
