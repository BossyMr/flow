package com.bossymr.flow.constraint;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.instruction.BinaryOperator;
import com.bossymr.flow.instruction.UnaryOperator;
import com.bossymr.flow.state.FlowSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstraintEngineTest {

    @DisplayName("Assert 0 != 1")
    @Test
    void zeroEqualToOneNotReachable() {
        Flow flow = new Flow();
        FlowSnapshot snapshot = FlowSnapshot.emptyState(flow);
        snapshot.require(new BinaryExpression(BinaryOperator.EQUAL_TO, LiteralExpression.integerLiteral(0), LiteralExpression.integerLiteral(1)));
        Reachable reachable = ConstraintEngine.isReachable(snapshot);
        assertEquals(Reachable.NOT_REACHABLE, reachable);
    }

    @DisplayName("Assert 0 == 0")
    @Test
    void zeroEqualToZeroReachable() {
        Flow flow = new Flow();
        FlowSnapshot snapshot = FlowSnapshot.emptyState(flow);
        snapshot.require(new BinaryExpression(BinaryOperator.EQUAL_TO, LiteralExpression.integerLiteral(0), LiteralExpression.integerLiteral(0)));
        Reachable reachable = ConstraintEngine.isReachable(snapshot);
        assertEquals(Reachable.REACHABLE, reachable);
    }

    @DisplayName("Assert 0 == {real to int} 0")
    @Test
    void zeroIntEqualToZeroRealReachable() {
        Flow flow = new Flow();
        FlowSnapshot snapshot = FlowSnapshot.emptyState(flow);
        snapshot.require(new BinaryExpression(BinaryOperator.EQUAL_TO, LiteralExpression.integerLiteral(0), new UnaryExpression(UnaryOperator.REAL_TO_INTEGER, LiteralExpression.numericLiteral(0))));
        Reachable reachable = ConstraintEngine.isReachable(snapshot);
        assertEquals(Reachable.REACHABLE, reachable);
    }
}