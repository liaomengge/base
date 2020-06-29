package cn.mwee.base_common.helper.lock.distributed;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface AcquiredLockWorker<T> {

    T lockSuccess();

    T lockFail();

    //default void runSuccess() {
    //}
    //
    //default void runFail() {
    //}
    //
    //default <T> T supplySuccess() {
    //    return null;
    //}
    //
    //default <T> T supplyFail() {
    //    return null;
    //}
}
