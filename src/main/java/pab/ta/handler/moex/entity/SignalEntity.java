package pab.ta.handler.moex.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import pab.ta.handler.base.asset.AssetType;
import pab.ta.handler.base.asset.CandleInterval;
import pab.ta.handler.base.asset.Direction;
import pab.ta.handler.base.component.rule.RuleGroup;

import java.time.LocalDateTime;

@Entity(name = "Signal")
@Table(name = "signal")
@IdClass(SignalPk.class)
@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SignalEntity {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "interval", nullable = false)
    private CandleInterval interval;

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(name = "ruleId", nullable = false)
    private String ruleId;

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private Direction direction;

    @Column(name = "rule_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleGroup ruleGroup;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}