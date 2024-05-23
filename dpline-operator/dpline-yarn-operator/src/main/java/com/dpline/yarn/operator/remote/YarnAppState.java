package com.dpline.yarn.operator.remote;

public enum YarnAppState {

    NEW,
    //RMApp的初始状态，当客户端通过RPC调用RM的submitApplication方法后，RM会初始化RMAppImpl，此时状态机的状态被设置为NEW。

    NEW_SAVING,
    // 表示RM在处理客户端提交作业的请求期间状态为NEW_SAVING。RM的submitApplication方法中，在返回给客户端前，RM会创建START事件，当NEW状态遇到START事件后，RMAppImpl的状态转换为NEW_SAVING。

    SUBMITTED,
    //表示App已经提交成功，RM已经存下该App状态。两种情况下会转换为SUBMITTED状态：
    //1.当RMAppImpl的状态由NEW转换为NEW_SAVING期间，会触发RMAppNewlySavingTransition的transition方法，在此方法中会调用RMStateStore存储RMAppImpl，事实上是在RMStateStore.ForwardingEventHandler中调用handleStoreEvent方法存储RMAppImpl，完成存储后会调用notifyDoneStoringApplication方法，在此方法中创建RMAppNewSavedEvent事件并交给rmDispatcher。RMAppImpl遇到RMAppNewSavedEvent（对应APP_NEW_SAVED）事件后，状态转换为SUBMITTED。
    // 2.在NEW状态下，如果是Recover模式，且该App存储在RMStateStore中，则转换为SUBMITTED。

    ACCEPTED,
    //  表示该App已经提交给调度器。
    //  在NEW_SAVING转换为SUBMITTED状态的时候，RMAppImpl会触发StartAppAttemptTransition，这时会创建一个新的RMAppAttempt，然后新建RMAppAttemptEventType.START事件给处理器，经过RMAppAttempt处理机。当该RMAppAttempt交给调度器（的某个组）后，状态改为ACCEPTED。

    RUNNING,
    // AM已经启动并注册到RM上。两种情况下会转换为RUNNING状态：
    // AM启动后会向RM注册，这时候会触发RMAppImpl状态转换为RUNNING状态。
    // RMAppImpl也有可能在Recovery模式下转换为RUNNING。

//    FINAL_SAVING,
    // FINAL_SAVING状态表示正在保存RMAppImpl到存储器，目的是保证RMAppImpl的状态已经存储下来，当RMStateStore在完成App状态更新到存储器后会根据App的状态转换为最终状态，包括FAILED，FINISHED，FINISHING，KILLED。
    // 这个状态其实阻断了原来清晰的状态转换流程，如RUNNING在遇到RMAppEventType.ATTEMPT_FAILED时，转换为FINAL_SAVING状态，但是设置了targetedFinalState为RMAppState.FAILED，最终经过FINAL_SAVING后转换为RMAppState.FAILED状态。

//    FINISHING,
    // FINISHING状态表示RM上相应的App状态已经完成存储工作，在等待RMAppEventType.ATTEMPT_FINISHED事件。因为只有RMAppAttempt结束后RMApp才能结束。
    // 在RMAppState.RUNNING状态遇到RMAppEventType.ATTEMPT_UNREGISTERED事件时，RMAppImpl转换为FINAL_SAVING，并存储targetedFinalState为RMAppState.FINISHING，遇到RMAppEventType.APP_UPDATE_SAVED事件后RMAppImpl转换为FINISHING状态。

    FINISHED,
    // RMAppImpl的结束状态（另外两个结束状态是KILLED和FAILED），正常情况下处于RUNNING的RMAppImpl成功结束后状态就是FINISHED，另外RM收到AM的REJECTED请求后最终状态也是FINISHED，即FINISHED状态是AM主动通知RM自己结束后的状态。

    FAILED,
    // 处于FINAL_SAVING的RMAppImpl遇到RMAppEventType. FAILED事件后RMAppImpl转换为FAILED状态。

//    KILLING,
    // RMAppImpl遇到客户端执行KILL操作后会转换为FINAL_SAVING状态，另外会设置RMAppImpl的targetedFinalState为RMAppEventType.KILL。

    KILLED,
    OTHER;
    public static YarnAppState of(String name) {
        for (YarnAppState yarnAppState : YarnAppState.values()) {
            if (yarnAppState.name().equals(name)) {
                return yarnAppState;
            }
        }
        return null;
    }



}
