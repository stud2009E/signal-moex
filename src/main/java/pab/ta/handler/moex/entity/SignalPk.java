package pab.ta.handler.moex.entity;

import lombok.Data;
import pab.ta.handler.base.asset.CandleInterval;
import pab.ta.handler.base.asset.Direction;

import java.io.Serializable;

@Data
public class SignalPk implements Serializable {

    private String ticker;

    private CandleInterval interval;

    private String ruleId;

    private Direction direction;
}
