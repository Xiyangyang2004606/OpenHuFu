package tk.onedb.core.sql.rule;

import java.util.List;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rex.RexNode;
import org.immutables.value.Value;

import tk.onedb.core.sql.rel.OneDBFilter;
import tk.onedb.core.sql.rel.OneDBRel;
import tk.onedb.core.sql.rel.OneDBTableScan;

public class OneDBFilterRule extends RelRule<OneDBFilterRule.OneDBFilterRuleConfig> {
  protected OneDBFilterRule(OneDBFilterRuleConfig config) {
    super(config);
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    LogicalFilter filter = call.rel(0);
    RexNode condition = filter.getCondition();

    List<RexNode> disjunctions = RelOptUtil.disjunctions(condition);
    return disjunctions.size() == 1;
  }

  @Value.Immutable
  public interface OneDBFilterRuleConfig extends RelRule.Config {
    OneDBFilterRuleConfig DEFAULT = ImmutableOneDBFilterRuleConfig.builder()
        .operandSupplier(
            b0 -> b0.operand(LogicalFilter.class).oneInput(b1 -> b1.operand(OneDBTableScan.class).noInputs()))
        .build();

    @Override
    default OneDBFilterRule toRule() {
      return new OneDBFilterRule(this);
    }
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    LogicalFilter filter = call.rel(0);
    OneDBTableScan scan = call.rel(1);
    if (filter.getTraitSet().contains(Convention.NONE)) {
      final RelNode converted = convert(filter, scan);
      if (converted != null) {
        call.transformTo(converted);
      }
    }
  }

  RelNode convert(LogicalFilter filter, OneDBTableScan scan) {
    final RelTraitSet traitSet = filter.getTraitSet().replace(OneDBRel.CONVENTION);
    return new OneDBFilter(filter.getCluster(), traitSet, convert(filter.getInput(), OneDBRel.CONVENTION),
        filter.getCondition());
  }
}
