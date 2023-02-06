package kos.core.events;

import com.sun.net.httpserver.Authenticator.Failure;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ThrowableNotThrown")
class DefaultAsyncEventReplierTest {

    @Mock Message<Object> message;
    @Mock AsyncResult<Void> result;
    DefaultAsyncEventReplier<Object> replier;

    @BeforeEach
    void setup(){
        replier = new DefaultAsyncEventReplier<>(message);
    }

    @Nested class WhenResultHoldsFailure {

        Throwable FAILURE = new RuntimeException("Failure");

        @BeforeEach
        void assume() {
            doReturn(false).when(result).succeeded();
            doReturn(FAILURE).when(result).cause();
        }

        @DisplayName("Should send a failure reply to sender")
        @Test void handle()
        {
            replier.handle(result);
            verify(message).fail(
                    eq(1),
                    eq("java.lang.RuntimeException: Failure\n" +
                            "\tat kos.core.events.DefaultAsyncEventReplierTest$WhenResultHoldsFailure.<init>(DefaultAsyncEventReplierTest.java:36)\n" +
                            "\tat java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)\n" +
                            "\tat java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:77)\n" +
                            "\tat java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)\n" +
                            "\tat java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)\n" +
                            "\tat java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:480)\n" +
                            "\tat org.junit.platform.commons.util.ReflectionUtils.newInstance(ReflectionUtils.java:513)\n" +
                            "\tat org.junit.jupiter.engine.execution.ConstructorInvocation.proceed(ConstructorInvocation.java:56)\n" +
                            "\tat org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)\n" +
                            "\tat org.junit.jupiter.api.extension.InvocationInterceptor.interceptTestClassConstructor(InvocationInterceptor.java:72)\n" +
                            "\tat org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)\n" +
                            "\tat org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)\n" +
                            "\tat org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)\n" +
                            "\tat org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)\n" +
                            "\tat org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)\n" +
                            "\tat org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)\n" +
                            "\tat org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:77)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeTestClassConstructor(ClassBasedTestDescriptor.java:342)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.instantiateTestClass(ClassBasedTestDescriptor.java:289)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.NestedClassTestDescriptor.instantiateTestClass(NestedClassTestDescriptor.java:87)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.instantiateAndPostProcessTestInstance(ClassBasedTestDescriptor.java:267)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$testInstancesProvider$2(ClassBasedTestDescriptor.java:259)\n" +
                            "\tat java.base/java.util.Optional.orElseGet(Optional.java:364)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$testInstancesProvider$3(ClassBasedTestDescriptor.java:258)\n" +
                            "\tat org.junit.jupiter.engine.execution.TestInstancesProvider.getTestInstances(TestInstancesProvider.java:31)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$prepare$0(TestMethodTestDescriptor.java:101)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.prepare(TestMethodTestDescriptor.java:100)\n" +
                            "\tat org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.prepare(TestMethodTestDescriptor.java:65)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$prepare$1(NodeTestTask.java:111)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.prepare(NodeTestTask.java:111)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:79)\n" +
                            "\tat java.base/java.util.ArrayList.forEach(ArrayList.java:1511)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)\n" +
                            "\tat java.base/java.util.ArrayList.forEach(ArrayList.java:1511)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)\n" +
                            "\tat java.base/java.util.ArrayList.forEach(ArrayList.java:1511)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)\n" +
                            "\tat org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)\n" +
                            "\tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:108)\n" +
                            "\tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)\n" +
                            "\tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)\n" +
                            "\tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)\n" +
                            "\tat org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)\n" +
                            "\tat org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:96)\n" +
                            "\tat org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:75)\n" +
                            "\tat com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:57)\n" +
                            "\tat com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)\n" +
                            "\tat com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)\n" +
                            "\tat com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)\n" +
                            "\tat com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:235)\n" +
                            "\tat com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:54)\n")
            );
        }
    }

    @Nested class WhenResultHoldsSuccess {

        @BeforeEach
        void assume() {
            doReturn(true).when(result).succeeded();
            doReturn("local").when(message).address();
        }

        @DisplayName("Should send a failure reply to sender")
        @Test void handle()
        {
            replier.handle(result);
            verify(message).reply(eq("local"));
        }
    }
}