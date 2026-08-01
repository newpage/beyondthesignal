package com.beyondsignal.game.combat.api;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.List;

public interface CombatEngine {
    List<CombatEvent> process(CombatCommand command);
}
