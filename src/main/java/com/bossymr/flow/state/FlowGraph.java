package com.bossymr.flow.state;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.instruction.CallInstruction;
import com.bossymr.flow.instruction.Instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlowGraph {

    private final StringBuilder buffer = new StringBuilder();

    /**
     * A list of edges that connect nodes that are in different clusters. We need to add these edges after all clusters
     * have been added, otherwise a node might be shown in the wrong cluster.
     * <p>
     * <pre>{@code
     * digraph {
     *     subgraph cluster_A {
     *         A;
     *         // if we define 'A -> B' here, B would be shown inside cluster_A
     *     }
     *     subgraph cluster_B {
     *         B;
     *     }
     *     A -> B;
     * }
     * }</pre>
     */
    private final List<String> edges = new ArrayList<>();

    /**
     * A list of discovered methods. The index of a method in this list represents it's ID.
     */
    private final List<Flow.Method> methods = new ArrayList<>();

    public FlowGraph() {
        buffer.append("digraph {").append("\n");
        buffer.append("rankdir=LR;").append("\n");
    }

    /**
     * Adds the provided methods and all dependent methods to this graph.
     *
     * @param methods the methods.
     * @return this graph.
     */
    public FlowGraph withMethods(Flow.Method... methods) {
        for (Flow.Method method : methods) {
            withMethod(method);
        }
        return this;
    }

    /**
     * Adds the provided method and all dependent methods to this graph. This includes all methods that are called by
     * this method.
     *
     * @param method the method.
     * @return this graph.
     */
    public FlowGraph withMethod(Flow.Method method) {
        if (methods.contains(method)) {
            // We have already added this method to this graph.
            return this;
        }
        methods.add(method);
        for (int currentMethod = methods.size() - 1; currentMethod < methods.size(); currentMethod++) {
            method = methods.get(currentMethod);
            buffer.append("subgraph ");
            buffer.append("cluster_").append(method.getName());
            buffer.append("{").append("\n");
            buffer.append("style=dotted;").append("\n");
            buffer.append("label=\"").append(method.getName()).append("\"").append("\n");
            List<FlowSnapshot> snapshots = new ArrayList<>(method.getExitPoints());
            for (int currentSnapshot = 0; currentSnapshot < snapshots.size(); currentSnapshot++) {
                FlowSnapshot snapshot = snapshots.get(currentSnapshot);
                withSnapshot(snapshots, method, snapshot);
                FlowSnapshot predecessor = snapshot.getPredecessor();
                if (predecessor != null && !snapshots.contains(predecessor)) {
                    snapshots.add(predecessor);
                }
                if (snapshot.getInstruction() instanceof CallInstruction instruction) {
                    Flow.Method target = instruction.getMethod();
                    if (!methods.contains(target)) {
                        methods.add(target);
                    }
                    FlowSnapshot callSnapshot = snapshot.getWeakPredecessor();
                    if (callSnapshot != null) {
                        withExternalEdge(snapshots, method, snapshot, target, callSnapshot);
                    }
                }
                if (predecessor != null) {
                    withInternalEdge(snapshots, method, predecessor, snapshot);
                }
            }
            buffer.append("}").append("\n");
        }
        return this;
    }

    private void withSnapshot(List<FlowSnapshot> snapshots, Flow.Method method, FlowSnapshot snapshot) {
        buffer.append("\"").append(getMethodIndex(method)).append("_").append(getSnapshotIndex(snapshots, snapshot)).append("\"");
        buffer.append("[shape=plain,label=<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">");
        Instruction instruction = snapshot.getInstruction();
        // The first snapshot in each method is not associated with an instruction.
        if (instruction != null) {
            buffer.append("<tr><td>");
            withText(instruction.toString());
            buffer.append("</td></tr>\n");
        }
        buffer.append("<tr><td>").append("Constraints").append("</td></tr>\n");
        for (Expression constraint : snapshot.getConstraints()) {
            buffer.append("<tr><td>");
            withText(constraint.toString());
            buffer.append("</td></tr>\n");
        }
        buffer.append("<tr><td>").append("Stack").append("</td></tr>\n");
        // Write the stack in reverse order, so that the last element (the top of the stack) is written first.
        for (Expression expression : snapshot.getStack().reversed()) {
            buffer.append("<tr><td>");
            withText(expression.toString());
            buffer.append("</td></tr>\n");
        }
        buffer.append("</table>>];\n");
    }

    private void withInternalEdge(List<FlowSnapshot> snapshots, Flow.Method method, FlowSnapshot fromSnapshot, FlowSnapshot toSnapshot) {
        int methodIndex = getMethodIndex(method);
        buffer.append("\"").append(methodIndex).append("_").append(getSnapshotIndex(snapshots, fromSnapshot)).append("\"");
        buffer.append(" -> ");
        buffer.append("\"").append(methodIndex).append("_").append(getSnapshotIndex(snapshots, toSnapshot)).append("\"");
        buffer.append(";\n");
    }

    private void withExternalEdge(List<FlowSnapshot> snapshots, Flow.Method fromMethod, FlowSnapshot fromSnapshot, Flow.Method toMethod, FlowSnapshot toSnapshot) {
        edges.add("\"" + getMethodIndex(fromMethod) + "_" + getSnapshotIndex(snapshots, fromSnapshot) + "\"" + " -> " +
                "\"" + getMethodIndex(toMethod) + "_" + getExternalSnapshotIndex(toMethod, toSnapshot) + "\"" +
                "[style=dotted]" + ";" + "\n");
    }

    /**
     * Cleans and writes the provided text to the buffer.
     *
     * @param text the text.
     */
    private void withText(String text) {
        buffer.append(text.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
    }

    /**
     * Returns the ID of the specified method.
     *
     * @param method the method.
     * @return the ID of the specified method.
     */
    private int getMethodIndex(Flow.Method method) {
        int index = methods.indexOf(method);
        if (index >= 0) {
            return index;
        }
        methods.add(method);
        return methods.size() - 1;
    }

    /**
     * Returns the ID of the specified snapshot.
     *
     * @param snapshots a list of snapshots.
     * @param snapshot the snapshot.
     * @return the ID of the specified snapshot.
     */
    private int getSnapshotIndex(List<FlowSnapshot> snapshots, FlowSnapshot snapshot) {
        int index = snapshots.indexOf(snapshot);
        if (index < 0) {
            throw new IllegalArgumentException("cannot find index of snapshot: " + snapshot);
        }
        return index;
    }

    /**
     * Returns the ID of the specified snapshot. The snapshot must be an exit point of the provided method.
     *
     * @param method the method.
     * @param exitPoint the snapshot.
     * @return the ID of the specified snapshot.
     * @throws IllegalArgumentException if the specified snapshot is not an exit point of the provided method.
     */
    private int getExternalSnapshotIndex(Flow.Method method, FlowSnapshot exitPoint) {
        List<FlowSnapshot> exitPoints = method.getExitPoints();
        // We start by visiting the exit points of a method.
        // As a result, we know the index of the snapshot without having to store a list of snapshots for every method.
        return exitPoints.indexOf(exitPoint);
    }

    /**
     * Returns this graph as a {@code .dot} file.
     *
     * @return this graph.
     */
    public String getText() {
        for (String edge : edges) {
            buffer.append(edge).append("\n");
        }
        // We try as much as possible to let this method be called more than once.
        // As a result, we clear the list of edges, so that an edge is not added more than once.
        // We also don't write the final '}' to the buffer, since that would close the graph.
        edges.clear();
        return buffer + "}";
    }
}
