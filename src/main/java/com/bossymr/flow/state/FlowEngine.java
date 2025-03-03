package com.bossymr.flow.state;

import com.bossymr.flow.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlowEngine {

    private final Map<Method, FlowMethod> methods = new HashMap<>();

    public FlowMethod getMethod(Method method) {
        if (methods.containsKey(method)) {
            return methods.get(method);
        }
        FlowMethod flowMethod = FlowMethod.compute(this, method);
        methods.put(method, flowMethod);
        return flowMethod;
    }

    public Set<FlowMethod> getMethods() {
        return Set.copyOf(methods.values());
    }
}
