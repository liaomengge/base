package cn.mwee.base_common.mq.activemq.pool;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

/**
 * Created by liaomengge on 2019/10/28.
 */
public class MonitorPooledConnectionFactory extends PooledConnectionFactory {

    public MonitorPooledConnectionFactory() {
    }

    public MonitorPooledConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        super(activeMQConnectionFactory);
    }

    public MonitorPooledConnectionFactory(String brokerURL) {
        super(brokerURL);
    }

    public PoolMonitor createPoolMonitor() {
        return new InternalPoolMonitor();
    }

    public interface PoolMonitor {

        /**
         * @see org.apache.commons.pool2.impl.GenericKeyedObjectPool#getMaxTotal()
         */
        int getMaxTotal();

        /**
         * @see org.apache.commons.pool2.impl.GenericKeyedObjectPool#getNumActive()
         */
        int getNumActive();

        /**
         * @see org.apache.commons.pool2.impl.GenericKeyedObjectPool#getNumIdle()
         */
        int getNumIdle();

        /**
         * @see org.apache.commons.pool2.impl.GenericKeyedObjectPool#getNumWaiters()
         */
        int getNumWaiters();

        /**
         * @see org.apache.commons.pool2.impl.GenericKeyedObjectPool#getMaxWaitMillis()
         */
        long getMaxWaitMillis();
    }

    protected class InternalPoolMonitor implements PoolMonitor {

        @Override
        public int getMaxTotal() {
            return getMaxConnections();
        }

        @Override
        public int getNumActive() {
            return getConnectionsPool().getNumActive();
        }

        @Override
        public int getNumIdle() {
            return getNumConnections();
        }

        @Override
        public int getNumWaiters() {
            return getConnectionsPool().getNumWaiters();
        }

        @Override
        public long getMaxWaitMillis() {
            return getConnectionsPool().getMaxWaitMillis();
        }
    }
}
