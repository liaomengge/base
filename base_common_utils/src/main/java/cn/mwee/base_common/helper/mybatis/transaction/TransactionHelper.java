package cn.mwee.base_common.helper.mybatis.transaction;

import lombok.Data;
import lombok.Getter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.transaction.TransactionDefinition.*;

/**
 * Created by liaomengge on 2019/10/25.
 */
public class TransactionHelper {

    private TransactionDefinition transactionDefinition;
    @Getter
    private final TransactionTemplate transactionTemplate;

    public TransactionHelper(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public TransactionHelper(TransactionDefinition transactionDefinition, TransactionTemplate transactionTemplate) {
        this.transactionDefinition = transactionDefinition;
        this.transactionTemplate = transactionTemplate;
    }

    public void run(Runnable runnable) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            }
        });
    }

    public void runThrowException(Runnable runnable) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            }
        });
    }

    public <T> T supplier(Supplier<T> supplier) {
        return transactionTemplate.execute(status -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                status.setRollbackOnly();
            }
            return null;
        });
    }

    public <T> T supplierThrowException(Supplier<T> supplier) {
        return transactionTemplate.execute(status -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    @PostConstruct
    private void init() {
        Optional.ofNullable(transactionDefinition).ifPresent(val -> {
            transactionTemplate.setTimeout(val.getTimeout());
            transactionTemplate.setIsolationLevel(val.getIsolationLevel());
            transactionTemplate.setName(val.getName());
            transactionTemplate.setPropagationBehavior(val.getPropagationBehavior());
            transactionTemplate.setReadOnly(val.isReadOnly());
        });
    }

    @Data
    public static class TransactionDefinition {
        private int propagationBehavior = PROPAGATION_REQUIRED;
        private int isolationLevel = ISOLATION_DEFAULT;
        private int timeout = TIMEOUT_DEFAULT;
        private boolean readOnly = false;
        private String name;
    }
}
