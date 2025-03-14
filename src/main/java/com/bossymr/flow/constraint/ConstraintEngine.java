package com.bossymr.flow.constraint;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.type.*;
import io.github.cvc5.*;

import java.util.*;

public class ConstraintEngine {

    private final TermManager manager = new TermManager();
    private final Solver solver = new Solver(manager);

    private final Map<Variable, Term> variables = new HashMap<>();
    private final Map<ValueType, Sort> types = new HashMap<>();

    public ConstraintEngine(FlowSnapshot snapshot) throws CVC5ApiException {
        solver.setLogic("ALL");
        for (FlowSnapshot state : getSnapshotBranch(snapshot)) {
            for (Expression constraint : state.getConstraints()) {
                Term expression = getTerm(constraint);
                solver.assertFormula(expression);
            }
        }
    }

    /**
     * Checks whether the provided snapshot is reachable. A snapshot is reachable if all constraints are satisfiable,
     * that is, whether there exists some value for all variables where all constraints are met. Likewise, a snapshot is
     * unreachable if all constraints can't be met at the same time.
     *
     * @param snapshot the snapshot.
     * @return if the snapshot is reachable.
     */
    public static Reachable isReachable(FlowSnapshot snapshot) {
        try {
            ConstraintEngine engine = new ConstraintEngine(snapshot);
            return engine.isReachable();
        } catch (CVC5ApiException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Checks the possible values of the provided predicate.
     *
     * @param snapshot  the snapshot.
     * @param predicate the expression.
     * @return the possible values of the provided predicate.
     * @throws IllegalArgumentException if the provided expression is not a predicate.
     */
    public static Constraint getConstraint(FlowSnapshot snapshot, Expression predicate) {
        try {
            ConstraintEngine engine = new ConstraintEngine(snapshot);
            return engine.getConstraint(predicate);
        } catch (CVC5ApiException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Reachable isReachable() {
        Result result = solver.checkSat();
        if (result.isSat()) {
            return Reachable.REACHABLE;
        }
        if (result.isUnsat()) {
            return Reachable.NOT_REACHABLE;
        }
        return Reachable.UNKNOWN;
    }

    public Constraint getConstraint(Expression predicate) throws CVC5ApiException {
        Term expression = getTerm(predicate);
        solver.push();
        solver.assertFormula(manager.mkTerm(manager.mkOp(Kind.EQUAL), expression, manager.mkBoolean(true)));
        Result maybeTrue = solver.checkSat();
        if (maybeTrue.isUnknown()) {
            return Constraint.UNKNOWN;
        }
        solver.pop();
        solver.assertFormula(manager.mkTerm(manager.mkOp(Kind.EQUAL), expression, manager.mkBoolean(false)));
        Result maybeFalse = solver.checkSat();
        if (maybeFalse.isUnknown()) {
            return Constraint.UNKNOWN;
        }
        if (maybeTrue.isSat() && maybeFalse.isSat()) {
            return Constraint.ANY_VALUE;
        }
        if (maybeTrue.isSat()) {
            return Constraint.ALWAYS_TRUE;
        }
        if (maybeFalse.isSat()) {
            return Constraint.ALWAYS_FALSE;
        }
        return Constraint.NO_VALUE;
    }

    /**
     * Get a list of snapshots leading from the start of the method until this snapshot.
     *
     * @param snapshot the last snapshot.
     * @return a list of snapshots.
     */
    private List<FlowSnapshot> getSnapshotBranch(FlowSnapshot snapshot) {
        List<FlowSnapshot> snapshots = new ArrayList<>();
        while (snapshot != null) {
            snapshots.add(snapshot);
            snapshot = snapshot.getPredecessor();
        }
        return snapshots.reversed();
    }

    private Term getTerm(Expression expression) throws CVC5ApiException {
        return switch (expression) {
            case UnaryExpression unary -> {
                Term component = getTerm(unary.getExpression());
                Op operator = switch (unary.getOperator()) {
                    case NOT -> manager.mkOp(Kind.NOT);
                    case NEGATE -> manager.mkOp(Kind.NEG);
                    case INTEGER_TO_REAL -> manager.mkOp(Kind.TO_REAL);
                    case REAL_TO_INTEGER -> manager.mkOp(Kind.TO_INTEGER);
                };
                yield manager.mkTerm(operator, component);
            }
            case BinaryExpression binary -> {
                Term left = getTerm(binary.getLeft());
                Term right = getTerm(binary.getRight());
                Op operator = switch (binary.getOperator()) {
                    case EQUAL_TO -> manager.mkOp(Kind.EQUAL);
                    case GREATER_THAN -> manager.mkOp(Kind.GT);
                    case LESS_THAN -> manager.mkOp(Kind.LT);
                    case ADD -> manager.mkOp(Kind.ADD);
                    case SUBTRACT -> manager.mkOp(Kind.SUB);
                    case MULTIPLY -> manager.mkOp(Kind.MULT);
                    case DIVIDE -> manager.mkOp(Kind.DIVISION);
                    case MODULO -> manager.mkOp(Kind.INTS_MODULUS);
                    case AND -> manager.mkOp(Kind.AND);
                    case XOR -> manager.mkOp(Kind.XOR);
                    case OR -> manager.mkOp(Kind.OR);
                };
                yield manager.mkTerm(operator, left, right);
            }
            case LiteralExpression literal -> switch (literal.getValue()) {
                case Boolean value -> manager.mkBoolean(value);
                case String value -> manager.mkString(value);
                case Integer value -> manager.mkInteger(value);
                case Long value -> manager.mkInteger(value);
                case RealType.Fraction(long numerator, long denominator) -> manager.mkReal(numerator, denominator);
                default -> throw new IllegalStateException();
            };
            case Variable variable -> {
                if (variables.containsKey(variable)) {
                    yield variables.get(variable);
                }
                Term term = manager.mkConst(getSort(variable.getType()));
                variables.put(variable, term);
                yield term;
            }
            default -> throw new IllegalStateException();
        };
    }

    private Sort getSort(ValueType type) throws CVC5ApiException {
        if (types.containsKey(type)) {
            return types.get(type);
        }
        Sort sort = switch (type) {
            case BooleanType ignored -> manager.getBooleanSort();
            case IntegerType ignored -> manager.getIntegerSort();
            case RealType ignored -> manager.getRealSort();
            case StringType ignored -> manager.getStringSort();
            case ArrayType arrayType ->
                    manager.mkArraySort(manager.getIntegerSort(), getSort(arrayType.getElementType()));
            case StructureType structureType -> {
                DatatypeDecl dataType = manager.mkDatatypeDecl(structureType.getName());
                DatatypeConstructorDecl constructor = manager.mkDatatypeConstructorDecl(structureType.getName());
                for (StructureType.Field field : structureType.getFields()) {
                    constructor.addSelector(field.getName(), getSort(field.getType()));
                }
                yield manager.mkDatatypeSort(dataType);
            }
            default -> throw new IllegalStateException("unexpected value: " + type);
        };
        types.put(type, sort);
        return sort;
    }
}
