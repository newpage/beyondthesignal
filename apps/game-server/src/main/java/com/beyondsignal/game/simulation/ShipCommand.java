package com.beyondsignal.game.simulation;

public sealed interface ShipCommand
    permits SetHeadingCommand, SetThrottleCommand, AllocatePowerCommand,
    SetShieldsCommand, SelectTargetCommand, ClearTargetCommand, FireWeaponCommand {
}
